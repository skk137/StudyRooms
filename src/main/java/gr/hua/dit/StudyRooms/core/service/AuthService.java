package gr.hua.dit.StudyRooms.core.service;

import gr.hua.dit.StudyRooms.core.service.model.LogInRequest;
import gr.hua.dit.StudyRooms.core.service.model.LoginResult;

public interface AuthService {
    LoginResult login(LogInRequest request);
}
