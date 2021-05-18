package models;

import entities.StatusEntity;
import entities.StudentEntity;

import javax.mail.Message;
import javax.mail.MessagingException;
import java.io.File;
import java.util.ArrayList;

public interface MailModel {
    int notSeenMessagesCount();

    int inboxMessagesCount();

    void sendMessage(String toEmail, String subject, String body) throws MessagingException;

    ArrayList<Message> getNotSeenInboxMessages();

    ArrayList<Message> getInboxMessages();

    ArrayList<Message> getInboxDialogMessages(String emailTo, String theme);

    void deleteInboxDialogMessages(String emailTo, String theme);

    void loadNotSeenInboxMessage(File rootFolder);

    void changeStatus(StudentEntity student, StatusEntity nextStatus);

    void messagesSetSeen();
}
