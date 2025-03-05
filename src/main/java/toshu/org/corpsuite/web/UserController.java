package toshu.org.corpsuite.web;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import toshu.org.corpsuite.card.model.Card;
import toshu.org.corpsuite.card.service.CardService;
import toshu.org.corpsuite.security.AuthenticationMetadata;
import toshu.org.corpsuite.user.model.User;
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

    public UserController(UserService userService, CardService cardService) {
        this.userService = userService;
        this.cardService = cardService;
    }

    @GetMapping
    public ModelAndView getUsersPage(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {
        ModelAndView mav = new ModelAndView("user");
        User user = userService.findById(authenticationMetadata.getUserId());
        List<User> users = userService.getAllActiveUsers();
        mav.addObject("user", user);
        mav.addObject("users", users);

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

        if (result.hasErrors()) {
            User user = userService.findById(authenticationMetadata.getUserId());
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

        return new ModelAndView("redirect:/home");
    }

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

    @PostMapping("/add")
    public ModelAndView handleAddPage(@Valid @ModelAttribute("userRequest") AddUser userRequest, BindingResult result, @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        if (result.hasErrors()) {
            List<Card> allCards = cardService.getAllCards();
            User user = userService.findById(authenticationMetadata.getUserId());

            ModelAndView mav = new ModelAndView("user-edit");
            mav.addObject("userRequest", userRequest);
            mav.addObject("cards", allCards);
            mav.addObject("method", "POST");
            mav.addObject("endpoint", "add");
            mav.addObject("user", user);

            return mav;
        }
        userService.addUser(userRequest);

        return new ModelAndView("redirect:/users");
    }


}
