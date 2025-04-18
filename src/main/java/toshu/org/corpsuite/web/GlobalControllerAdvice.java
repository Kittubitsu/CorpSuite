package toshu.org.corpsuite.web;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import toshu.org.corpsuite.security.AuthenticationMetadata;
import toshu.org.corpsuite.user.model.User;
import toshu.org.corpsuite.user.service.UserService;

@ControllerAdvice
public class GlobalControllerAdvice {


    private final UserService userService;

    public GlobalControllerAdvice(UserService userService) {
        this.userService = userService;
    }

    @ModelAttribute("user")
    public User addUserToModel(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        if (authenticationMetadata == null) {
            return null;
        }

        return userService.getById(authenticationMetadata.getUserId());
    }

    @ModelAttribute("bool")
    public Boolean addBoolToModel(@RequestParam(required = false) Boolean show) {

        if (show == null) {
            return false;
        }

        return show;
    }
}
