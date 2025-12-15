package gr.hua.dit.StudyRooms.web.rest;

import gr.hua.dit.StudyRooms.core.model.Person;
import gr.hua.dit.StudyRooms.core.model.PersonType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

@Controller
public class StudentController {


    @GetMapping("/student/dashboard")
    public String dashboardStudent(@SessionAttribute("loggedInUser") Person user, Model model) {
        if (user.getPersonType() != PersonType.STUDENT) {
            return "redirect:/login";
        }

        //πχ λίστα διαθέσιμων δωματίων
        //model.addAttribute("rooms", roomService.getAllRooms());
        //model.addAttribute("bookings", bookingService.getBookingsForStudent(user.getId()));
        return "student-dashboard";
    }




}