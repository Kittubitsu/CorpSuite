package toshu.org.corpsuite.web;

import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import toshu.org.corpsuite.computer.model.Computer;
import toshu.org.corpsuite.computer.service.ComputerService;
import toshu.org.corpsuite.security.AuthenticationMetadata;
import toshu.org.corpsuite.user.model.User;
import toshu.org.corpsuite.user.service.UserService;
import toshu.org.corpsuite.web.dto.ComputerAdd;
import toshu.org.corpsuite.web.mapper.dtoMapper;

import java.util.List;

@Controller
@RequestMapping("/computers")
public class ComputerController {

    private final UserService userService;
    private final ComputerService computerService;

    public ComputerController(UserService userService, ComputerService computerService) {
        this.userService = userService;
        this.computerService = computerService;
    }

    @GetMapping
    public ModelAndView getComputerPage(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        ModelAndView mav = new ModelAndView("computer");
        User user = userService.findById(authenticationMetadata.getUserId());
        List<Computer> computers = computerService.getAllActiveComputers();
        mav.addObject("user", user);
        mav.addObject("computers", computers);

        return mav;
    }

    @GetMapping("/add")
    public ModelAndView getComputerAddPage() {

        List<User> usersWithoutComputer = userService.getUsersWithoutComputer();
        ModelAndView mav = new ModelAndView("computer-edit");
        mav.addObject("method", "POST");
        mav.addObject("endpoint", "add");
        mav.addObject("computerRequest", ComputerAdd.builder().build());
        mav.addObject("freeUsers", usersWithoutComputer);

        return mav;
    }

    @PostMapping("/add")
    public ModelAndView handleComputerAdd(@Valid @ModelAttribute("computerRequest") ComputerAdd computerRequest, BindingResult result, @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        if (result.hasErrors()) {
            List<User> usersWithoutComputer = userService.getUsersWithoutComputer();
            ModelAndView mav = new ModelAndView("computer-edit");
            mav.addObject("method", "POST");
            mav.addObject("endpoint", "add");
            mav.addObject("computerRequest", computerRequest);
            mav.addObject("freeUsers", usersWithoutComputer);
            return mav;
        }


        computerService.addComputer(computerRequest);

        return new ModelAndView("redirect:/computers");
    }

    @GetMapping("/edit/{id}")
    public ModelAndView getComputerEditPage(@PathVariable long id) {

        List<User> usersWithoutComputer = userService.getUsersWithoutComputer();
        Computer computer = computerService.findById(id);

        if (computer.getOwner() != null) {
            usersWithoutComputer.add(computer.getOwner());
        }

        ModelAndView mav = new ModelAndView("computer-edit");
        mav.addObject("method", "PUT");
        mav.addObject("endpoint", "edit/" + id);
        mav.addObject("computerRequest", dtoMapper.toComputerDto(computer));
        mav.addObject("freeUsers", usersWithoutComputer);

        return mav;
    }

    @PutMapping("/edit/{id}")
    public ModelAndView handleEditPage(@PathVariable long id, @Valid @ModelAttribute("computerRequest") ComputerAdd computerRequest, BindingResult result) {

        if (result.hasErrors()) {
            Computer computer = computerService.findById(id);
            List<User> usersWithoutComputer = userService.getUsersWithoutComputer();

            if (computer.getOwner() != null) {
                usersWithoutComputer.add(computer.getOwner());
            }

            ModelAndView mav = new ModelAndView("computer-edit");
            mav.addObject("method", "PUT");
            mav.addObject("endpoint", "edit/" + id);
            mav.addObject("computerRequest", computerRequest);
            mav.addObject("freeUsers", usersWithoutComputer);

            return mav;
        }

        computerService.editComputer(id, computerRequest);

        return new ModelAndView("redirect:/computers");
    }

}
