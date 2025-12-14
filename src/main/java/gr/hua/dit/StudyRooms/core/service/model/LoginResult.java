package gr.hua.dit.StudyRooms.core.service.model;

import gr.hua.dit.StudyRooms.core.model.Person;



public record LoginResult(boolean success, String reason, Person user) {}
