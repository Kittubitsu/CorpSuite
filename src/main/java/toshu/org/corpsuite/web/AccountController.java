package toshu.org.corpsuite.web;

import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import toshu.org.corpsuite.card.model.Card;
import toshu.org.corpsuite.card.service.CardService;
import toshu.org.corpsuite.security.AuthenticationMetadata;
import toshu.org.corpsuite.user.model.User;
import toshu.org.corpsuite.user.service.UserService;
import toshu.org.corpsuite.web.dto.EditUser;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/account")
public class AccountController {

    private final UserService userService;
    private final CardService cardService;

    public AccountController(UserService userService, CardService cardService) {
        this.userService = userService;
        this.cardService = cardService;
    }

    @GetMapping
    public ModelAndView getAccountPage(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        ModelAndView mav = new ModelAndView();
        User user = userService.findById(authenticationMetadata.getUserId());
        if (!user.getPosition().name().contains("ADMIN")) {
            mav.setViewName("account-edit");
            mav.addObject("accountRequest", EditUser.builder()
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .corporateEmail(user.getCorporateEmail())
                    .cardId(user.getCard())
                    .country(user.getCountry())
                    .department(user.getDepartment())
                    .isActive(user.isActive())
                    .password(user.getPassword())
                    .position(user.getPosition())
                    .profilePicture(user.getProfilePicture())
                    .build());
            return mav;
        }


        mav.setViewName("account");
        List<User> allUsers = userService.getAllUsers();

        mav.addObject("users", allUsers);
        mav.addObject("user", user);


        return mav;
    }

    @GetMapping("/add")
    public ModelAndView getAddAccountPage(){
        List<Card> allCards = cardService.getAllFreeCards();
        ModelAndView mav = new ModelAndView("account-edit");
        mav.addObject("addUserRequest", EditUser.builder().build());
        mav.addObject("cards", allCards);
        mav.addObject("method", "POST");
        return mav;
    }

    @PostMapping("/add")
    public ModelAndView handleAddPage(@Valid EditUser addUserRequest, BindingResult result){
        //TODO
        ModelAndView mav = new ModelAndView();
        userService.addUser(addUserRequest);

        return new ModelAndView("redirect:/account");
    }

}
