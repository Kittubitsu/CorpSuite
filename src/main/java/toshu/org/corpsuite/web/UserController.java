package toshu.org.corpsuite.web;

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
import toshu.org.corpsuite.log.client.dto.LogRequest;
import toshu.org.corpsuite.log.service.LogService;
import toshu.org.corpsuite.security.AuthenticationMetadata;
import toshu.org.corpsuite.user.model.User;
import toshu.org.corpsuite.user.model.UserDepartment;
import toshu.org.corpsuite.user.service.UserService;
import toshu.org.corpsuite.web.dto.AddUser;
import toshu.org.corpsuite.web.dto.EditUser;

import java.util.List;
import java.util.UUID;

@Slf4j
@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final CardService cardService;
    private final LogService logService;
    private Boolean showQuery;

    public UserController(UserService userService, CardService cardService, LogService logService) {
        this.userService = userService;
        this.cardService = cardService;
        this.logService = logService;
    }

    @PreAuthorize("hasAnyRole('HR','ADMIN')")
    @GetMapping
    public ModelAndView getUsersPage(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata, @RequestParam(name = "show", defaultValue = "false") Boolean show) {

        ModelAndView mav = new ModelAndView("user");

        User user = userService.findById(authenticationMetadata.getUserId());
        List<User> users = userService.getAllUsers();

        showQuery = show;

        mav.addObject("users", users);

        if (!show) {
            List<User> filteredUsers = users.stream().filter(User::isActive).toList();
            mav.addObject("users", filteredUsers);
        }

        mav.addObject("bool", show);
        mav.addObject("user", user);

        return mav;
    }

    @GetMapping("/edit/{id}")
    public ModelAndView getAccountEditPage(@PathVariable UUID id, @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        ModelAndView mav = new ModelAndView();
        User userToEdit = userService.findById(id);
        User user = userService.findById(authenticationMetadata.getUserId());
        List<Card> allCards = cardService.getAllFreeCards();

        if (userToEdit.getCard() != null) {
            allCards.add(userToEdit.getCard());
        }

        mav.setViewName("user-edit");
        mav.addObject("userRequest", EditUser.builder()
                .firstName(userToEdit.getFirstName())
                .lastName(userToEdit.getLastName())
                .corporateEmail(userToEdit.getCorporateEmail())
                .card(userToEdit.getCard())
                .country(userToEdit.getCountry())
                .department(userToEdit.getDepartment())
                .isActive(userToEdit.isActive())
                .password(userToEdit.getPassword())
                .position(userToEdit.getPosition())
                .profilePicture(userToEdit.getProfilePicture())
                .build());
        mav.addObject("method", "PUT");
        mav.addObject("user", user);
        mav.addObject("cards", allCards);
        mav.addObject("endpoint", "edit/" + id);
        return mav;
    }

    @PutMapping("/edit/{id}")
    public ModelAndView handleAccountEditPage(@Valid @ModelAttribute("userRequest") EditUser userRequest, BindingResult result, @PathVariable UUID id, @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        User user = userService.findById(authenticationMetadata.getUserId());

        if (result.hasErrors()) {
            List<Card> allCards = cardService.getAllFreeCards();

            ModelAndView mav = new ModelAndView();
            mav.setViewName("user-edit");
            mav.addObject("userRequest", userRequest);
            mav.addObject("method", "PUT");
            mav.addObject("user", user);
            mav.addObject("cards", allCards);
            mav.addObject("endpoint", "edit/" + id);

            return mav;
        }

        userService.editUser(id, userRequest);

        logService.saveLog(LogRequest.builder()
                .email(user.getCorporateEmail())
                .action("EDIT")
                .module("User")
                .comment("User edited with id [%s]".formatted(user.getId()))
                .build());


        if (user.getDepartment().equals(UserDepartment.HR) || user.getDepartment().equals(UserDepartment.ADMIN)) {
            return new ModelAndView("redirect:/users?show=" + showQuery);
        }

        return new ModelAndView("redirect:/home");
    }

    @PreAuthorize("hasAnyRole('HR','ADMIN')")
    @GetMapping("/add")
    public ModelAndView getAddAccountPage(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        User user = userService.findById(authenticationMetadata.getUserId());
        List<Card> allCards = cardService.getAllFreeCards();

        ModelAndView mav = new ModelAndView("user-edit");
        mav.addObject("userRequest", AddUser.builder().build());
        mav.addObject("cards", allCards);
        mav.addObject("method", "POST");
        mav.addObject("endpoint", "add");
        mav.addObject("user", user);

        return mav;
    }

    @PreAuthorize("hasAnyRole('HR','ADMIN')")
    @PostMapping("/add")
    public ModelAndView handleAddPage(@Valid @ModelAttribute("userRequest") AddUser userRequest, BindingResult result, @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        User user = userService.findById(authenticationMetadata.getUserId());
        if (result.hasErrors()) {
            List<Card> allCards = cardService.getAllCards();

            ModelAndView mav = new ModelAndView("user-edit");
            mav.addObject("userRequest", userRequest);
            mav.addObject("cards", allCards);
            mav.addObject("method", "POST");
            mav.addObject("endpoint", "add");
            mav.addObject("user", user);

            return mav;
        }
        User createdUser = userService.addUser(userRequest);

        logService.saveLog(LogRequest.builder()
                .email(user.getCorporateEmail())
                .action("CREATE")
                .module("User")
                .comment("User created with id [%s]".formatted(createdUser.getId()))
                .build());

        return new ModelAndView("redirect:/users?show=" + showQuery);
    }


}
