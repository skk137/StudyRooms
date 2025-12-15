package gr.hua.dit.StudyRooms.core.service.model;

import gr.hua.dit.StudyRooms.core.model.Person;



public record LoginResult(
        boolean success,
        String reason,
        Person person
){


    public static LoginResult success(Person person) {
        return new LoginResult(true, null, person);
    }



    public static LoginResult failed(String reason) {
        return new LoginResult(false, reason, null);
    }


}
