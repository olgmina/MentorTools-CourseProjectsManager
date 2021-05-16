package models;

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
    void deleteInboxDialogMessages(String emailTo);
    void loadNotSeenInboxMessage(File rootFolder);
    void messagesSetSeen();
}
