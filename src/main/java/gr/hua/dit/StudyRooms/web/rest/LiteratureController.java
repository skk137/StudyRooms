package gr.hua.dit.StudyRooms.web.rest;

import gr.hua.dit.StudyRooms.core.model.Person;
import gr.hua.dit.StudyRooms.core.model.PersonType;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

@GetMapping("/literature/dashboard")
public String dashboardLiterature(@SessionAttribute("loggedInUser") Person user, Model model) {
    if (user.getPersonType() != PersonType.LITERATURE) {
        return "redirect:/login";
    }

    model.addAttribute("rooms", roomService.getAllRooms());
    return "literature-dashboard";
}