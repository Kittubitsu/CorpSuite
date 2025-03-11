package toshu.org.corpsuite.web;

import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
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
import toshu.org.corpsuite.web.dto.AddComputerRequest;
import toshu.org.corpsuite.web.mapper.DtoMapper;

import java.util.List;

@Controller
@RequestMapping("/computers")
@PreAuthorize("hasAnyRole('IT','ADMIN')")
public class ComputerController {

    private final UserService userService;
    private final ComputerService computerService;

    public ComputerController(UserService userService, ComputerService computerService) {
        this.userService = userService;
        this.computerService = computerService;
    }

    @GetMapping
    public ModelAndView getComputerPage(@RequestParam(name = "show", defaultValue = "false") Boolean show) {

        ModelAndView mav = new ModelAndView("computer");

        List<Computer> computers = computerService.getAllComputers(show);

        mav.addObject("computers", computers);
        mav.addObject("bool", show);

        return mav;
    }

    @GetMapping("/add")
    public ModelAndView getComputerAddPage() {

        List<User> usersWithoutComputer = userService.getUsersWithoutComputer();
        ModelAndView mav = new ModelAndView("computer-edit");
        mav.addObject("method", "POST");
        mav.addObject("endpoint", "add");
        mav.addObject("computerRequest", AddComputerRequest.builder().build());
        mav.addObject("freeUsers", usersWithoutComputer);

        return mav;
    }

    @PostMapping("/add")
    public ModelAndView handleComputerAdd(@Valid @ModelAttribute("computerRequest") AddComputerRequest computerRequest, BindingResult result, @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        User user = userService.getById(authenticationMetadata.getUserId());

        if (result.hasErrors()) {
            List<User> usersWithoutComputer = userService.getUsersWithoutComputer();
            ModelAndView mav = new ModelAndView("computer-edit");
            mav.addObject("method", "POST");
            mav.addObject("endpoint", "add");
            mav.addObject("computerRequest", computerRequest);
            mav.addObject("freeUsers", usersWithoutComputer);
            return mav;
        }

        computerService.addComputer(computerRequest,user);

        return new ModelAndView("redirect:/computers?show=false");
    }

    @GetMapping("/edit/{id}")
    public ModelAndView getComputerEditPage(@PathVariable long id) {

        List<User> usersWithoutComputer = userService.getUsersWithoutComputer();
        Computer computer = computerService.getById(id);

        if (computer.getOwner() != null) {
            usersWithoutComputer.add(computer.getOwner());
        }

        ModelAndView mav = new ModelAndView("computer-edit");
        mav.addObject("method", "PUT");
        mav.addObject("endpoint", "edit/" + id);
        mav.addObject("computerRequest", DtoMapper.toComputerDto(computer));
        mav.addObject("freeUsers", usersWithoutComputer);

        return mav;
    }

    @PutMapping("/edit/{id}")
    public ModelAndView handleEditPage(@PathVariable long id, @Valid @ModelAttribute("computerRequest") AddComputerRequest computerRequest, BindingResult result, @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        User user = userService.getById(authenticationMetadata.getUserId());

        if (result.hasErrors()) {
            Computer computer = computerService.getById(id);
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

        computerService.editComputer(id, computerRequest, user);

        return new ModelAndView("redirect:/computers?show=false");
    }

}
