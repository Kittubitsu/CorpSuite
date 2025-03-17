package toshu.org.corpsuite.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import toshu.org.corpsuite.card.model.Card;
import toshu.org.corpsuite.card.service.CardService;
import toshu.org.corpsuite.history.History;
import toshu.org.corpsuite.security.AuthenticationMetadata;
import toshu.org.corpsuite.user.model.User;
import toshu.org.corpsuite.user.model.UserDepartment;
import toshu.org.corpsuite.user.service.UserService;
import toshu.org.corpsuite.web.dto.AddUserRequest;
import toshu.org.corpsuite.web.dto.EditUserRequest;
import toshu.org.corpsuite.web.mapper.DtoMapper;

import java.util.List;
import java.util.UUID;

@Slf4j
@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final CardService cardService;
    private final History history;

    public UserController(UserService userService, CardService cardService, History history) {
        this.userService = userService;
        this.cardService = cardService;
        this.history = history;
    }

    @PreAuthorize("hasAnyRole('HR','ADMIN')")
    @GetMapping
    public ModelAndView getUsersPage(@RequestParam(name = "show", required = false) Boolean show, @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {
        ModelAndView mav = new ModelAndView("user");

        history.setShow(show);
        List<User> users = userService.getAllUsers(show);
        User user = userService.getById(authenticationMetadata.getUserId());
        List<Card> allCards = cardService.getAllFreeCards(user);

        mav.addObject("userRequest", AddUserRequest.builder().build());
        mav.addObject("cards", allCards);
        mav.addObject("method", "POST");
        mav.addObject("endpoint", "add");
        mav.addObject("users", users);

        return mav;
    }

//    @GetMapping("/edit/{id}")
//    public ModelAndView getAccountEditPage(@PathVariable UUID id) {
//        ModelAndView mav = new ModelAndView("user-edit");
//
//        User user = userService.getById(id);
//        List<Card> allCards = cardService.getAllFreeCards(user);
//
//
//        mav.addObject("userRequest", DtoMapper.toEditUserDto(user));
//        mav.addObject("method", "PUT");
//        mav.addObject("cards", allCards);
//        mav.addObject("endpoint", "edit/" + id);
//
//        return mav;
//    }

    @PutMapping("/edit/{id}")
    public ModelAndView handleAccountEditPage(@Valid @ModelAttribute("userRequest") EditUserRequest userRequest, BindingResult result, @PathVariable UUID id, @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        User user = userService.getById(authenticationMetadata.getUserId());

        if (result.hasErrors()) {
            ModelAndView mav = new ModelAndView();

            List<Card> allCards = cardService.getAllFreeCards(user);
            List<User> users = userService.getAllUsers(history.isShow());

            mav.setViewName("user");
            mav.addObject("userRequest", userRequest);
            mav.addObject("method", "PUT");
            mav.addObject("cards", allCards);
            mav.addObject("endpoint", "edit/" + id);
            mav.addObject("users", users);

            return mav;
        }

        userService.editUser(id, userRequest, user);


        if (user.getDepartment().equals(UserDepartment.HR) || user.getDepartment().equals(UserDepartment.ADMIN)) {
            return new ModelAndView("redirect:/users?show=" + history.isShow());
        }

        return new ModelAndView("redirect:/home");
    }

//    @PreAuthorize("hasAnyRole('HR','ADMIN')")
//    @GetMapping("/add")
//    public ModelAndView getAddAccountPage(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {
//        ModelAndView mav = new ModelAndView("user-edit");
//
//        User user = userService.getById(authenticationMetadata.getUserId());
//        List<Card> allCards = cardService.getAllFreeCards(user);
//
//        mav.addObject("userRequest", AddUserRequest.builder().build());
//        mav.addObject("cards", allCards);
//        mav.addObject("method", "POST");
//        mav.addObject("endpoint", "add");
//
//        return mav;
//    }

    @PreAuthorize("hasAnyRole('HR','ADMIN')")
    @PostMapping("/add")
    public ModelAndView handleAddPage(@Valid @ModelAttribute("userRequest") AddUserRequest userRequest, BindingResult result, @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        User user = userService.getById(authenticationMetadata.getUserId());

        if (result.hasErrors()) {
            ModelAndView mav = new ModelAndView("user");

            List<Card> allCards = cardService.getAllCards(true);
            List<User> users = userService.getAllUsers(history.isShow());

            mav.addObject("users", users);
            mav.addObject("userRequest", userRequest);
            mav.addObject("cards", allCards);
            mav.addObject("method", "POST");
            mav.addObject("endpoint", "add");

            return mav;
        }

        userService.addUser(userRequest, user);

        return new ModelAndView("redirect:/users?show=" + history.isShow());
    }


}
