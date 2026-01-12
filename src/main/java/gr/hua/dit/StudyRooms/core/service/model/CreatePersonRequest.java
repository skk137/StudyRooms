package gr.hua.dit.StudyRooms.core.service.model;


import gr.hua.dit.StudyRooms.core.model.PersonType;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.UniqueElements;

public record CreatePersonRequest(
        @NotNull PersonType PersonType,
        @NotNull @NotBlank @Size(max = 20) String huaId,
        @NotNull @NotBlank @Size(max = 100) String FirstName,
        @NotNull @NotBlank @Size(max = 100) String LastName,
        @NotNull @NotBlank @Size(max = 155) @Email String Email,
        @NotNull @NotBlank @Size(max = 25)String Phone,
        @NotNull @NotBlank @Size(max = 255 )String passwordHash)
{





}
