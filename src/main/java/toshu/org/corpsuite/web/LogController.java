package toshu.org.corpsuite.web;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import toshu.org.corpsuite.log.client.dto.Log;
import toshu.org.corpsuite.log.service.LogService;
import toshu.org.corpsuite.security.AuthenticationMetadata;
import toshu.org.corpsuite.user.model.User;
import toshu.org.corpsuite.user.service.UserService;

import java.util.List;

@Controller
@RequestMapping("/logs")
@PreAuthorize("hasAnyRole('IT','ADMIN')")
public class LogController {

    private final LogService logService;
    private final UserService userService;

    public LogController(LogService logService, UserService userService) {
        this.logService = logService;
        this.userService = userService;
    }

    @GetMapping
    public ModelAndView getLogPage(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {
        ModelAndView mav = new ModelAndView("log");

        User user = userService.findById(authenticationMetadata.getUserId());
        List<Log> logs = logService.getAllLogs();

        mav.addObject("user", user);
        mav.addObject("logs", logs);

        return mav;
    }
}
