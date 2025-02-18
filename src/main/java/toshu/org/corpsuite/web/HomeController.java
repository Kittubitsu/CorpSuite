package toshu.org.corpsuite.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller()
@RequestMapping("/home")
public class HomeController {

    @GetMapping
    public ModelAndView getHomePage(){
        ModelAndView mav = new ModelAndView("home");

        return mav;
    }
}
