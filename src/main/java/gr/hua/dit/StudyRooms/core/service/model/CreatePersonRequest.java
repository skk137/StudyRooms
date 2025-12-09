package gr.hua.dit.StudyRooms.core.service.model;


import gr.hua.dit.StudyRooms.core.model.PersonType;

public record CreatePersonRequest(
        PersonType PersonType,
        String huaId,
        String FirstName,
        String LastName,
        String Email,
        String Phone,
        String passwordHash)
{





}
