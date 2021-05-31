package models.mail;

import entities.StatusEntity;
import entities.StudentEntity;

import javax.mail.Message;
import javax.mail.MessagingException;
import java.io.File;
import java.util.ArrayList;

public interface MailDao {

    int notSeenMessagesCount();

    int inboxMessagesCount();

    void sendMessage(String toEmail, String subject, String body) throws MessagingException;

    ArrayList<Message> getNotSeenInboxMessages();

    ArrayList<Message> getInboxMessages();

    ArrayList<Message> getDialogMessages(String emailTo, String theme);

    void deleteDialogMessages(String emailTo, String theme);

    void loadNotSeenInboxMessage(File rootFolder);

    void changeStatus(StudentEntity student, StatusEntity nextStatus);

    void messagesSetSeen();

}
