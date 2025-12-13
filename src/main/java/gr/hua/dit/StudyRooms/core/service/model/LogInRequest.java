package gr.hua.dit.StudyRooms.core.service.model;

public record LogInRequest(String huaId, String password) {

    public boolean success() {
        return true;
    }

}