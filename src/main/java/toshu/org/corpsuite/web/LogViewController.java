package toshu.org.corpsuite.web;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/logs")
@PreAuthorize("hasAnyRole('IT','ADMIN')")
public class LogViewController {

    @GetMapping
    public ModelAndView getLogPage() {
        return new ModelAndView("log");
    }
}
