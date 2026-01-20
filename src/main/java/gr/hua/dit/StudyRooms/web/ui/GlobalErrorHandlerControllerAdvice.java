package gr.hua.dit.StudyRooms.web.ui;

// Global error handling και χρήση custom error templates

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

// ControllerAdvice για χειρισμό σφαλμάτων σε όλη την εφαρμογή
@ControllerAdvice
public class GlobalErrorHandlerControllerAdvice {

    // Logger για καταγραφή σφαλμάτων
    private static final Logger LOGGER =
            LoggerFactory.getLogger(GlobalErrorHandlerControllerAdvice.class);

    // Χειρισμός όλων των εξαιρέσεων απο την Exception
    @ExceptionHandler(Exception.class)
    public String handleAnyError(
            final Exception exception,
            final HttpServletRequest httpServletRequest,
            final HttpServletResponse httpServletResponse,
            final Model model
    ){

        // Έλεγχος για σφάλμα 404 (resource not found)
        if (exception instanceof NoResourceFoundException){
            httpServletResponse.setStatus(404);
            return "error/404";
        }

        // Καταγραφή του σφάλματος στα logs
        LOGGER.warn(
                "Handling exception {} {}",
                exception.getClass(),
                exception.getMessage()
        );

        // Πέρασμα πληροφοριών στο error view
        model.addAttribute("message", exception.getMessage());
        model.addAttribute("path", httpServletRequest.getRequestURI());

        // Επιστροφή γενικού error template
        return "error/error";
    }
}