package toshu.org.corpsuite.web;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import toshu.org.corpsuite.exception.*;

@Slf4j
@ControllerAdvice
public class ExceptionAdvice {

    @ExceptionHandler(AuthorizationDeniedException.class)
    public String handleAuthorization() {
        return "redirect:/home";
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(Exception.class)
    public ModelAndView genericError(Exception exception) {
        ModelAndView mav = new ModelAndView("generic-error");

        mav.addObject("errorMessage", exception.getMessage());

        return mav;
    }

    @ExceptionHandler(CardAlreadyExistsException.class)
    public String handleCardExists(RedirectAttributes redirectAttributes, CardAlreadyExistsException exception) {

        redirectAttributes.addFlashAttribute("alreadyExistsException", exception.getMessage());

        return "redirect:/cards/add";
    }

    @ExceptionHandler(ComputerAlreadyExistsException.class)
    public String handleComputerExists(RedirectAttributes redirectAttributes, ComputerAlreadyExistsException exception) {

        redirectAttributes.addFlashAttribute("alreadyExistsException", exception.getMessage());

        return "redirect:/computers/add";
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public String handleUserExists(RedirectAttributes redirectAttributes, UserAlreadyExistsException exception) {

        redirectAttributes.addFlashAttribute("alreadyExistsException", exception.getMessage());

        return "redirect:/users/add";
    }

    @ExceptionHandler(SamePasswordException.class)
    public String handleSamePassword(HttpServletRequest request, SamePasswordException exception, RedirectAttributes redirectAttributes) {

        redirectAttributes.addFlashAttribute("samePasswordException", exception.getMessage());

        return "redirect:" + request.getRequestURI();
    }

    @ExceptionHandler(LogServiceConnectionException.class)
    public String handleLogService(LogServiceConnectionException logServiceConnectionException) {

        log.error(logServiceConnectionException.getMessage());

        return "redirect:/home";
    }


}
