package gr.hua.dit.StudyRooms.core.service.model;









//PersonView DTO for informations that will be exposed.

import gr.hua.dit.StudyRooms.core.model.PersonType;

public record PersonView(
        long id,
        String huaId,
        String FirstName,
        String LastName,
        String Email,
        String Phone,
        PersonType PersonType
){




}
