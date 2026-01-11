package gr.hua.dit.StudyRooms.core.port;

public interface EmailNotificationPort {

    void sendSms(String phoneNumber, String message);

}