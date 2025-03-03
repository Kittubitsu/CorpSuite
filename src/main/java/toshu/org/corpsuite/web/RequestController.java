package toshu.org.corpsuite.web;

import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import toshu.org.corpsuite.request.model.Request;
import toshu.org.corpsuite.request.service.RequestService;
import toshu.org.corpsuite.security.AuthenticationMetadata;
import toshu.org.corpsuite.user.model.User;
import toshu.org.corpsuite.user.service.UserService;
import toshu.org.corpsuite.web.dto.RequestAdd;


import java.util.List;

@Controller
@RequestMapping("/requests")
public class RequestController {


    private final UserService userService;
    private final RequestService requestService;

    public RequestController(UserService userService, RequestService requestService) {
        this.userService = userService;
        this.requestService = requestService;
    }

    @GetMapping
    public ModelAndView getRequestPage(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        User user = userService.findById(authenticationMetadata.getUserId());

        List<Request> requestList = requestService.getAllByUser(user);

        ModelAndView mav = new ModelAndView("request");
        mav.addObject("user", user);
        mav.addObject("requestList", requestList);

        return mav;
    }

    @GetMapping("/add")
    public ModelAndView getRequestAddPage(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        User user = userService.findById(authenticationMetadata.getUserId());

        ModelAndView mav = new ModelAndView("request-add");
        mav.addObject("user", user);
        mav.addObject("absenceRequest", RequestAdd.builder()
                .requester(user.getCorporateEmail())
                .responsible(user.getManager() == null ? "None" : user.getManager().getCorporateEmail())
                .build());
        mav.addObject("endpoint", "add");
        mav.addObject("method", "POST");

        return mav;
    }

    @PostMapping("/add")
    public ModelAndView handleRequestAddPage(@Valid @ModelAttribute("absenceRequest") RequestAdd absenceRequest, BindingResult result, @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        User user = userService.findById(authenticationMetadata.getUserId());

        if (result.hasErrors()) {
            ModelAndView mav = new ModelAndView("request-add");
            mav.addObject("user", user);
            mav.addObject("absenceRequest", absenceRequest);
            mav.addObject("endpoint", "add");
            mav.addObject("method", "POST");

            return mav;
        }

        return new ModelAndView("redirect:/requests");
    }

    //TODO: finish thymeleaf logic and adding/editing to database
}
