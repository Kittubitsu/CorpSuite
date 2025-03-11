package toshu.org.corpsuite.web;

import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import toshu.org.corpsuite.request.model.Request;
import toshu.org.corpsuite.request.service.RequestService;
import toshu.org.corpsuite.security.AuthenticationMetadata;
import toshu.org.corpsuite.user.model.User;
import toshu.org.corpsuite.user.service.UserService;
import toshu.org.corpsuite.web.dto.AddAbsenceRequest;
import toshu.org.corpsuite.web.mapper.DtoMapper;


import java.util.List;
import java.util.UUID;

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
    public ModelAndView getRequestPage(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata, @RequestParam(name = "show", defaultValue = "false") Boolean show) {
        ModelAndView mav = new ModelAndView("request");

        User user = userService.getById(authenticationMetadata.getUserId());

        List<Request> requestList = requestService.getAllByUser(user, show);

        mav.addObject("requestList", requestList);
        mav.addObject("bool", show);

        return mav;
    }

    @GetMapping("/add")
    public ModelAndView getRequestAddPage(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        User user = userService.getById(authenticationMetadata.getUserId());

        ModelAndView mav = new ModelAndView("request-add");
        mav.addObject("absenceRequest", AddAbsenceRequest.builder()
                .requesterEmail(user.getCorporateEmail())
                .responsibleEmail(user.getManager() == null ? "None" : user.getManager().getCorporateEmail())
                .totalDays(1)
                .build());
        mav.addObject("endpoint", "add");
        mav.addObject("method", "POST");

        return mav;
    }

    @PostMapping("/add")
    public ModelAndView handleRequestAddPage(@Valid @ModelAttribute("absenceRequest") AddAbsenceRequest absenceRequest, BindingResult result, @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        User user = userService.getById(authenticationMetadata.getUserId());

        User manager = user.getManager();

        if (manager == null) {
            manager = user;
        }

        if (result.hasErrors()) {
            ModelAndView mav = new ModelAndView("request-add");
            absenceRequest.setTotalDays(1);
            mav.addObject("absenceRequest", absenceRequest);
            mav.addObject("endpoint", "add");
            mav.addObject("method", "POST");

            return mav;
        }

        requestService.addRequest(absenceRequest, user, manager);

        return new ModelAndView("redirect:/requests?show=false");
    }

    @GetMapping("/edit/{id}")
    public ModelAndView getRequestEditPage(@PathVariable UUID id) {

        Request request = requestService.getById(id);

        ModelAndView mav = new ModelAndView("request-add");
        mav.addObject("absenceRequest", DtoMapper.toRequestDto(request));
        mav.addObject("endpoint", "edit/" + id);
        mav.addObject("method", "PUT");

        return mav;
    }

    @PutMapping("/edit/{id}")
    public ModelAndView handleRequestEditPage(@Valid @ModelAttribute("absenceRequest") AddAbsenceRequest absenceRequest, BindingResult result, @PathVariable UUID id, @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        User user = userService.getById(authenticationMetadata.getUserId());

        if (result.hasErrors()) {
            ModelAndView mav = new ModelAndView("request-add");
            absenceRequest.setTotalDays(1);
            mav.addObject("absenceRequest", absenceRequest);
            mav.addObject("endpoint", "edit/" + id);
            mav.addObject("method", "PUT");

            return mav;
        }

        requestService.editRequest(absenceRequest, id, user);

        return new ModelAndView("redirect:/requests?show=false");
    }
}
