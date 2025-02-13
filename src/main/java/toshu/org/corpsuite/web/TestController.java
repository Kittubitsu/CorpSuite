package toshu.org.corpsuite.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class TestController {

    @GetMapping()
    public String getTestPage() {
        return "index";
    }
}
