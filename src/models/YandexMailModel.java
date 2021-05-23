package models;

import entities.StageEntity;
import entities.StatusEntity;
import entities.StudentEntity;
import entities.UserEntity;
import models.messageReader.EmailMessageReader;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.search.FlagTerm;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Properties;

public class YandexMailModel extends BaseModel implements MailModel {

    private final String yandexEmail;
    private final String password;
    private final String personal;
    private final String IMAP_host = "imap.yandex.ru";
    private static YandexMailModel instance = null;

    public static synchronized YandexMailModel getInstance() throws MessagingException {
        if (instance == null) {
            instance = new YandexMailModel();
        }
        return instance;
    }

    private YandexMailModel() throws MessagingException {
        UserEntity user = dataBaseModel.getUser();
        if (user != null) {
            this.yandexEmail = user.getUsername();
            this.password  = user.getPassword();
            this.personal  = user.getPersonal();
        } else {
            this.yandexEmail = "example@yandex.ru";
            this.password   = "password";
            this.personal   = "personal";
        }
        getInbox();
    }

    private Folder getInbox() throws MessagingException {
        Store store = createSession().getStore();
        store.connect(IMAP_host, yandexEmail, password);
        Folder inbox = store.getFolder("INBOX");
        inbox.open(Folder.READ_WRITE);
        return inbox;
    }

    private Session createSession(){
        Properties props = getInboxProps();
        Authenticator authenticator = getAuthenticator();
        Session session = Session.getDefaultInstance(props, authenticator);
        session.setDebug(false);
        return session;
    }

    private Properties getSendProps() {
        Properties props = System.getProperties();
        String host = "smtp.yandex.ru";
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.user", yandexEmail);
        props.put("mail.smtp.password", password);
        props.put("mail.smtp.port", "465");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.ssl.enable", "true");
        props.put("mail.smtp.quitwait", "false");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.debug", "false");
        return props;
    }

    private Properties getInboxProps() {
        Properties props = new Properties();
        props.put("mail.imap.port", "993");
        props.put("mail.store.protocol", "imaps");
        props.put("mail.imap.ssl.enable", "true");
        props.put("mail.debug", "false");
        return props;
    }

    private Authenticator getAuthenticator() {
        return new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(yandexEmail, password);
            }
        };
    }

    private void saveFile(StudentEntity student, Message message) throws Exception {
        File folder = new File(student.getFolderPath() + File.separator + student.getStage() + File.separator + student.getStatus());
        folder.mkdir();
        folder.mkdirs();
        MimeMultipart mp;
        try {
            mp = (MimeMultipart) message.getContent();
            for (int i = 0; i < mp.getCount(); i++) {
                models.messageReader.File file = EmailMessageReader.getFileFromMimeBodyPart((MimeBodyPart) mp.getBodyPart(i));
                if (file != null) {
                    if (file.getFileName().contains(".pdf")
                            || file.getFileName().contains(".docx")
                            || file.getFileName().contains(".doc")
                            || file.getFileName().contains(".txt")
                            || file.getFileName().contains(".c")) {
                        file.saveFile(folder.getPath());
                        student.setFileCount(student.getFileCount() + 1);
                        dataBaseModel.updateStudent(student);
                        File thisFile = new File(folder + File.separator + file.getFileName());
                        thisFile.renameTo(new File(folder.getPath() + File.separator + student.getFileCount() + file.getFileName()));
                    }
                    else
                        throw new Exception();
                }

            }
        }
        catch (ClassCastException | MessagingException | IOException e) { e.printStackTrace(); }
    }

    @Override
    public int notSeenMessagesCount() {
        return getNotSeenInboxMessages().size();
    }

    @Override
    public int inboxMessagesCount() {
        return getInboxMessages().size();
    }

    @Override
    public void sendMessage(String toEmail, String subject, String body) throws MessagingException {
        Properties props = getSendProps();
        Session session = Session.getInstance(props);
        session.setDebug(false);
        MimeMessage message = new MimeMessage(session);
        try {
            InternetAddress address = new InternetAddress(yandexEmail);
            address.setPersonal(personal);
            message.setFrom(address);
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));
            message.addRecipient(Message.RecipientType.TO, address); //ЭТО ВАЖНО, ТАК КАК ЯНДЕКС ОТПРАВЛЯЕТ СООБЩЕНИЯ ПО SMTP, А ДАННЫЙ ПРОТОКОЛ НЕ ОСТАВЛЯЕТ ПОСЛЕ СЕБЯ КОПИИ СООБЩЕНИЙ. НЕ ДОБАВЛЯЕТ ИХ В ОТПРАВЛЕННЫЕ
            message.setSubject(subject);

            Multipart multipart = new MimeMultipart();
            BodyPart textBody = new MimeBodyPart();
            textBody.setContent(body, "text/plain; charset=utf-8");
            multipart.addBodyPart(textBody);
            message.setContent(multipart);
            Transport transport = session.getTransport("smtp");
            transport.connect(props.getProperty("mail.smtp.host"), yandexEmail, password);
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ArrayList<Message> getNotSeenInboxMessages() {
        ArrayList<Message> messages = new ArrayList<>();
        try {
            Folder inbox = getInbox();
            messages.addAll(Arrays.asList(inbox.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false))));
        } catch (MessagingException ignored) { }
        return messages;
    }

    @Override
    public ArrayList<Message> getInboxMessages() {
        ArrayList<Message> messages = new ArrayList<>();
        try {
            Folder inbox = getInbox();
            messages = new ArrayList<>(Arrays.asList(inbox.getMessages()));
        } catch (MessagingException ignored) { }
        return messages;
    }

    @Override
    public ArrayList<Message> getInboxDialogMessages(String emailTo, String theme) {
        ArrayList<Message> allMessages = new ArrayList<>();
        ArrayList<Message> dialogMessages = new ArrayList<>();
        try {
            Folder inbox = getInbox();
            allMessages = new ArrayList<>(Arrays.asList(inbox.getMessages()));
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        for (Message message : allMessages) {
            String messageTheme = EmailMessageReader.getSubject(message);
            Address[] addresses = null;
            try {
                addresses = message.getRecipients(Message.RecipientType.TO);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
            if (addresses != null) {
                boolean isEmailTo = false;
                boolean isEmailFrom = false;
                for (Address address : addresses) {
                    if (address.toString().equals(emailTo))
                        isEmailTo = true;
                    if (address.toString().equals(yandexEmail))
                        isEmailFrom = true;
                }
                if (isEmailTo && isEmailFrom) {
                    if (theme != null) {
                        if (messageTheme.equals(theme.toLowerCase().trim())) {
                            dialogMessages.add(message);
                        }
                    } else {
                        dialogMessages.add(message);
                    }
                }
            }
        }
        return dialogMessages;
    }

    @Override
    public void deleteInboxDialogMessages(String emailTo, String theme) {
        ArrayList<Message> dialogMessages = getInboxDialogMessages(emailTo, theme);
        dialogMessages.forEach(message -> {
            try {
                Message[] inboxMessages = getInbox().getMessages();
                for (Message inboxMessage : inboxMessages) {
                    if (inboxMessage == message) {
                        inboxMessage.setFlag(Flags.Flag.DELETED, true);
                        inboxMessage.setFlag(Flags.Flag.SEEN, true);
                        getInbox().expunge();
                    }
                }
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void loadNotSeenInboxMessage(File rootFolder) {
        try {
            Folder inbox = getInbox();
            ArrayList<Message> messages = new ArrayList<>(Arrays.asList(inbox.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false))));
            for (Message message : messages) {
                String messageStage    = EmailMessageReader.getSubject(message).toLowerCase().trim();
                String messageFrom     = EmailMessageReader.getFromEmailAddress(message);
                String messagePersonal = EmailMessageReader.getFromPersonal(message);
                Date messageDate       = EmailMessageReader.getDate(message);
                String PostScriptum = "\n\n" + AutoMessageModel.getInstance().getAutoMessage(AutoMessageModel.TEXT_ANSWERED_ON_DATE).getText().replaceAll("#ДАТА_СООБЩЕНИЯ#", messageDate.toString());
                StageEntity firstStage = StageModel.getInstance().getFirstStage();
                StatusEntity firstStatus = StatusModel.getInstance().getFirstStatus();
                StatusEntity lastStatus = StatusModel.getInstance().getLastStatus();
                // Если корректный этап в письме
                if (StageModel.getInstance().isStageCorrect(messageStage)) {
                    StudentEntity student = StudentModel.getInstance().getStudent(messageFrom);
                    // Если написал существующий студент
                    if (student != null) {
                        StageEntity currentStage = StageModel.getInstance().getStage(student.getStage().getId());
                        StageEntity nextStage = StageModel.getInstance().getNextStage(currentStage);
                        // Если студент верно указал этап
                        if (messageStage.equals(currentStage.getName())) {
                            //Если этап не завершен
                            if (student.getStatus().getId() != lastStatus.getId()) {
                                try {
                                    //Сохранить файлы
                                    saveFile(student, message);
                                } catch (Exception e) {
                                    //Ошибка, формат файла
                                    sendMessage(
                                            student.getEmailAddress(),
                                            AutoMessageModel.getInstance().getAutoMessage(AutoMessageModel.THEME).getText().replaceAll("#ТЕКУЩИЙ_ЭТАП#", currentStage.getName()),
                                            AutoMessageModel.getInstance().getAutoMessage(AutoMessageModel.TEXT_WRONG_FORMAT).getText() + PostScriptum
                                    );
                                }
                            }
                            //Если этап завершен
                            else {
                                //Если существует следующий этап
                                if (nextStage == null) {
                                    //Ошибка, курсовой проект завершен
                                    sendMessage(
                                            student.getEmailAddress(),
                                            AutoMessageModel.getInstance().getAutoMessage(AutoMessageModel.THEME).getText().replaceAll("#ТЕКУЩИЙ_ЭТАП#", currentStage.getName()),
                                            AutoMessageModel.getInstance().getAutoMessage(AutoMessageModel.TEXT_COURSE_PROJECT_COMPLETED).getText() + PostScriptum
                                    );
                                }
                                //Если следующего этапа не существует
                                else {
                                    //Ошибка, этап уже завершен
                                    sendMessage(
                                            student.getEmailAddress(),
                                            AutoMessageModel.getInstance().getAutoMessage(AutoMessageModel.THEME).getText().replaceAll("#ТЕКУЩИЙ_ЭТАП#", currentStage.getName()),
                                            AutoMessageModel.getInstance().getAutoMessage(AutoMessageModel.TEXT_STAGE_IS_ALREADY_COMPLETED).getText().replaceAll("#УКАЗАННЫЙ_ЭТАП#", currentStage.getName()).replaceAll("#СЛЕДУЮЩИЙ_ЭТАП#", nextStage.getName()) + PostScriptum
                                    );
                                }
                            }
                        }
                        // Если студент неверно указал этап
                        else {
                            // Если текущий этап не завершен
                            if (student.getStatus().getId() != lastStatus.getId()) {
                                //Ошибка, не завершен текущий этап
                                sendMessage(
                                        student.getEmailAddress(),
                                        AutoMessageModel.getInstance().getAutoMessage(AutoMessageModel.THEME).getText().replaceAll("#ТЕКУЩИЙ_ЭТАП#", currentStage.getName()),
                                        AutoMessageModel.getInstance().getAutoMessage(AutoMessageModel.TEXT_YOU_HAVE_NOT_DONE_STAGE).getText().replaceAll("#ТЕКУЩИЙ_ЭТАП#", currentStage.getName()) + PostScriptum
                                );
                            }
                            // Если текущий этап завершен
                            else {
                                //Если существует следующий этап
                                if (nextStage != null) {
                                    // Если указан следующий этап
                                    if (messageStage.equals(nextStage.getName())) {
                                        //Изменить этап на новый и скачать файлы в новую папку, изменить в бд
                                        student.setStage(nextStage);
                                        student.setStatus(firstStatus);
                                        StudentModel.getInstance().updateStudent(student);
                                        try {
                                            saveFile(student, message);
                                        } catch (Exception e) {
                                            //Ошибка, формат файла
                                            sendMessage(
                                                    student.getEmailAddress(),
                                                    AutoMessageModel.getInstance().getAutoMessage(AutoMessageModel.THEME).getText().replaceAll("#ТЕКУЩИЙ_ЭТАП#", currentStage.getName()),
                                                    AutoMessageModel.getInstance().getAutoMessage(AutoMessageModel.TEXT_WRONG_FORMAT).getText() + PostScriptum
                                            );
                                        }
                                    }
                                    // Если указан не следующий этап
                                    else {
                                        //Ошибка, пропуск этапа
                                        sendMessage(
                                                student.getEmailAddress(),
                                                AutoMessageModel.getInstance().getAutoMessage(AutoMessageModel.THEME).getText().replaceAll("#ТЕКУЩИЙ_ЭТАП#", currentStage.getName()),
                                                AutoMessageModel.getInstance().getAutoMessage(AutoMessageModel.TEXT_NOT_NEXT_STAGE).getText().replaceAll("#СЛЕДУЮЩИЙ_ЭТАП#", nextStage.getName()) + PostScriptum
                                        );
                                    }
                                }
                                //Если следующего этапа не существует
                                else {
                                    //Ошибка, курсовой проект завершен
                                    sendMessage(
                                            student.getEmailAddress(),
                                            AutoMessageModel.getInstance().getAutoMessage(AutoMessageModel.THEME).getText().replaceAll("#ТЕКУЩИЙ_ЭТАП#", currentStage.getName()),
                                            AutoMessageModel.getInstance().getAutoMessage(AutoMessageModel.TEXT_COURSE_PROJECT_COMPLETED).getText() + PostScriptum
                                    );
                                }
                            }
                        }
                    }
                    // Если студент не добавлен
                    else {
                        //Если автор не преподаватель
                        if (!messageFrom.equals(yandexEmail)) {
                            //Если тема сообщения - первый этап
                            if (messageStage.equals(firstStage.getName())) {
                                //Создать папку, скачать, добавить в бд.
                                File folder = new File(rootFolder.getPath() + File.separator + EmailMessageReader.getFromEmailAddress(message));
                                String folderPath = folder.getPath();
                                StageEntity stage = StageModel.getInstance().getFirstStage();
                                StatusEntity status = StatusModel.getInstance().getFirstStatus();
                                int fileCount = 0;
                                StudentModel.getInstance().addStudent(new StudentEntity(0, messagePersonal, messageFrom, folderPath, stage, status, fileCount));
                                student = StudentModel.getInstance().getStudent(messageFrom);
                                try {
                                    saveFile(student, message);
                                } catch (Exception e) {
                                    //Ошибка, формат файла
                                    sendMessage(
                                            student.getEmailAddress(),
                                            AutoMessageModel.getInstance().getAutoMessage(AutoMessageModel.THEME).getText().replaceAll("#ТЕКУЩИЙ_ЭТАП#", messageStage),
                                            AutoMessageModel.getInstance().getAutoMessage(AutoMessageModel.TEXT_WRONG_FORMAT).getText() + PostScriptum
                                    );
                                }
                            }
                            //Если тема сообщения - не первый этап
                            else {
                                //Ошибка, не завершен первый этап
                                sendMessage(
                                        messageFrom,
                                        AutoMessageModel.getInstance().getAutoMessage(AutoMessageModel.THEME).getText().replaceAll("#ТЕКУЩИЙ_ЭТАП#", messageStage),
                                        AutoMessageModel.getInstance().getAutoMessage(AutoMessageModel.TEXT_YOU_HAVE_NOT_DONE_STAGE).getText().replaceAll("#ТЕКУЩИЙ_ЭТАП#", firstStage.getName()) + PostScriptum
                                );
                            }
                        }
                    }
                }
            }
            messagesSetSeen();
        } catch (MessagingException ignored) {
        }
    }

    @Override
    public void changeStatus(StudentEntity student, StatusEntity nextStatus) {
        StageEntity currentStage   = student.getStage();
        StatusEntity currentStatus = student.getStatus();
        try {
            sendMessage(
                    student.getEmailAddress(),
                    AutoMessageModel.getInstance().getAutoMessage(AutoMessageModel.THEME).getText().replaceAll("#ТЕКУЩИЙ_ЭТАП#", student.getStage().getName()),
                    AutoMessageModel.getInstance().getAutoMessage(AutoMessageModel.TEXT_STATUS_CHANGED).getText().replaceAll("#ТЕКУЩИЙ_ЭТАП#", currentStage.getName()).replaceAll("#ТЕКУЩИЙ_СТАТУС#", currentStatus.getName()).replaceAll("#СЛЕДУЮЩИЙ_СТАТУС#", nextStatus.getName())
            );
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void messagesSetSeen() {
        try {
            Folder inbox = getInbox();
            ArrayList<Message> messages = new ArrayList<>(Arrays.asList(inbox.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false))));
            for (Message message : messages) message.setFlag(Flags.Flag.SEEN, true);
        } catch (MessagingException ignored) { }
    }
}

