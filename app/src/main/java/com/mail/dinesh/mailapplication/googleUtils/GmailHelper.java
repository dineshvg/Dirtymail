package com.mail.dinesh.mailapplication.googleUtils;

import com.google.api.client.util.Base64;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.ListThreadsResponse;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.Thread;
import com.mail.dinesh.mailapplication.SendMailActivity;
import com.mail.dinesh.mailapplication.bo.DirtyMail;
import com.mail.dinesh.mailapplication.conf.Constants;
import com.mail.dinesh.mailapplication.dbUtils.RealmDBTransactions;
import com.mail.dinesh.mailapplication.utils.Manager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import io.realm.Realm;

/**
 * Created by dinesh on 21.10.16.
 */

public class GmailHelper {

    private static final String TAG = GmailHelper.class.getSimpleName();
    /**
     * List all Threads of the user's mailbox matching the query.
     *
     * @param service Authorized Gmail API instance.
     * @param userId User's email address. The special value "me"
     * can be used to indicate the authenticated user.
     * @throws IOException
     */
    public List<List> listThreadsMatchingQuery (Gmail service, String userId, Realm myRealm) throws IOException {

        List<List> threadsOfMails = new ArrayList<>();
        ListThreadsResponse response = service.users().threads().list(userId)
                .setMaxResults(Long.parseLong(Constants.MAX_NUMBER)).setQ(Constants.INBOX_QUERY).execute();
        List<Thread> threads = new ArrayList<Thread>();
        while(response.getThreads() != null) {
            threads.addAll(response.getThreads());
            //Log.d(TAG,"size of thread "+threads.size());
            if(threads.size()>=Integer.parseInt(Constants.MAX_NUMBER)){
                break;
            }
        }

        for(Thread thread : threads) {
            List<DirtyMail> mails = getThread(service,Constants.USER,thread.getId(),myRealm);
            threadsOfMails.add(mails);
        }
        return threadsOfMails;
    }

    /**
     * Get a Thread with given id.
     *
     * @param service Authorized Gmail API instance.
     * @param userId User's email address. The special value "me"
     * can be used to indicate the authenticated user.
     * @param threadId ID of Thread to retrieve.
     * @param myRealm
     * @throws IOException
     */
    private static List<DirtyMail> getThread(Gmail service, String userId, String threadId, Realm myRealm) throws IOException {
        Thread thread = service.users().threads().get(userId, threadId).execute();
        List<Message> messages = thread.getMessages();
        return getMessages(service,messages,myRealm);
    }

    private static List<DirtyMail> getMessages(Gmail mService, List<Message> messages, Realm myRealm) throws IOException{

        List<DirtyMail> mails = new ArrayList<DirtyMail>();
        for (Message message : messages) {
            DirtyMail mail = new DirtyMail();
            Message fullMessage = getMessage(mService,Constants.USER,message.getId(),
                    Constants.EMAIL_FULL_FORMAT);
            try {
                mail = Manager.convertGmailToDirtyMail(fullMessage);
                mail = RealmDBTransactions.commitMailToDB(mail, myRealm);
                mails.add(mail);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return mails;
    }

    /**
     * Get Message with given ID.
     *
     * @param service Authorized Gmail API instance.
     * @param userId User's email address. The special value "me"
     * can be used to indicate the authenticated user.
     * @param messageId ID of Message to retrieve.
     * @return Message Retrieved Message.
     * @throws java.io.IOException
     */
    public static Message getMessage(Gmail service, String userId, String messageId, String format)
            throws IOException {
        Message message = null;
        if(format != null && !format.isEmpty()) {
            message = service.users().messages().get(userId, messageId)
                    .setFormat(Constants.EMAIL_FULL_FORMAT).execute();
        } else {
            message = service.users().messages().get(userId, messageId).execute();
        }
        return message;
    }


    public Message sendMail(Gmail mService, String user, Realm myRealm, DirtyMail email, String bodyText, SendMailActivity activity) {
        Message msg = null;
        try {

            MimeMessage mimeMessage = SendEmail.createEmail(email.getToAddress(),
                    email.getFromAddress(),email.getSubject(),bodyText);

            msg =  SendEmail.sendMessage(mService,Constants.USER,mimeMessage,activity);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return  msg;
    }
}

    /**
     * Create a MimeMessage using the parameters provided.
     *
     * @param to email address of the receiver
     * @param from email address of the sender, the mailbox account
     * @param subject subject of the email
     * @param bodyText body text of the email
     * @return the MimeMessage to be used to send email
     * @throws MessagingException
     */
    /*public static MimeMessage createEmail(String to,
                                          String from,
                                          String subject,
                                          String bodyText)
            throws MessagingException {

        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);
        MimeMessage email = new MimeMessage(session);

        try {
            email.setFrom(new InternetAddress(from));
            email.addRecipient(javax.mail.Message.RecipientType.TO,
                    new InternetAddress(to));
            email.setSubject(subject);
            email.setText(bodyText);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return email;
    }*/

    /**
     * Create a message from an email.
     *
     * @param emailContent Email to be set to raw of message
     * @return a message containing a base64url encoded email
     * @throws IOException
     * @throws MessagingException
     */
    /*public static Message createMessageWithEmail(MimeMessage emailContent)
            *//*throws MessagingException, IOException*//* {
        Message message = new Message();
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            emailContent.writeTo(buffer);
            byte[] bytes = buffer.toByteArray();
            String encodedEmail = Base64.encodeBase64URLSafeString(bytes);
            message.setRaw(encodedEmail);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return message;
    }*/



    /**
     * Send an email from the user's mailbox to its recipient.
     *
     * @param service Authorized Gmail API instance.
     * @param userId User's email address. The special value "me"
     * can be used to indicate the authenticated user.
     * @param email DirtyMail created.
     * @param bodyText Text entered in the sent screen.
     * @return The sent message
     * @throws MessagingException
     * @throws IOException
     */





/*MimeMessage msg = Manager.getMimeMsg(mService,Constants.USER,fullMessage.getId());
                MimeMessageParser parser = new MimeMessageParser(msg);
                parser.parse();*/

/*mailHeader = fullMessage.getPayload().getHeaders();
            for (MessagePartHeader header:mailHeader) {
                String name = header.getName().trim();
                if(name.equals(Constants.MAIL_SUBJECT)) {
                    String subject = header.getValue().trim();
                    if (!subjects.contains(subject)){
                        subjects.add(subject);
                    }
                }
            }*/

/**
 * Fetch a list of read mails attached to the specified account.
 * @return List of messages
 * @throws IOException
 *//*
    public List<String> getUnreadMails(Gmail mService) throws IOException {
        // Get the labels in the user's account.
        List<String> threadConversations = new ArrayList<String>();
        List<String> subjects = new ArrayList<String>();
        ListMessagesResponse response = mService.users().messages().list(Constants.USER)
                .setQ(Constants.INBOX_UNREAD_QUERY).execute();
        List<Message> messages = new ArrayList<Message>();
        while (response.getMessages() != null) {
            messages.addAll(response.getMessages());
            Log.d(TAG,"Number of mails picker by  : "+messages.size());
            if(messages.size()>=50) {
                break;
            }
            *//*if (response.getNextPageToken() != null) {
                try {
                    String pageToken = response.getNextPageToken();
                    response = mService.users().messages().list(Constants.USER)
                            .setQ(Constants.INBOX_UNREAD_QUERY)
                            .setPageToken(pageToken).execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else {
                break;
            }*//*
        }

        for (Message message : messages) {

            List<MessagePartHeader> mailHeader;

            Message fullMessage = getMessage(mService,Constants.USER,message.getId(),
                    Constants.EMAIL_FULL_FORMAT);
            //String threadId = message.getThreadId();
            mailHeader = fullMessage.getPayload().getHeaders();
            for (MessagePartHeader header:mailHeader) {
                String name = header.getName().trim();
                if(name.equals(Constants.MAIL_SUBJECT)) {
                    String subject = header.getValue().trim();
                    if (!subjects.contains(subject)){
                        subjects.add(subject);
                    }
                }
            }

        }
        Log.d(TAG,"Number of unread mails : "+subjects.size());
        return subjects;
    }

    *//**
 * Fetch a list of read mails attached to the specified account.
 * @return List of messages
 * @throws IOException
 *//*
    public List<String> getReadMails(Gmail mService) throws IOException {
        // Get the labels in the user's account.
        List<String> labels = new ArrayList<String>();
        List<Message> messages = new ArrayList<Message>();
        ListMessagesResponse response = mService.users().messages().list(Constants.USER)
                .setQ(Constants.INBOX_READ_QUERY).execute();
        while (response.getMessages() != null) {
            messages.addAll(response.getMessages());
            if (response.getNextPageToken() != null) {
                String pageToken = response.getNextPageToken();
                response = mService.users().messages().list(Constants.USER)
                        .setQ(Constants.INBOX_READ_QUERY)
                        .setPageToken(pageToken).execute();
            } else {
                break;
            }
        }

        for (Message message : messages) {
            labels.add(message.toPrettyString());
        }


        return labels;
    }
    private static MimeMessage getRawMessage(Gmail service, String userId, String messageId)
            throws IOException, MessagingException {

        MimeMessage email = null;
        if(service!=null && !userId.equals("") && !messageId.equals("")) {
            Message message = service.users().messages().get(userId, messageId)
                    .setFormat(Constants.EMAIL_RAW_FORMAT).execute();
            Base64 base64Url = new Base64(true);
            byte[] emailBytes = base64Url.decodeBase64(message.getRaw());
            Properties props = new Properties();
            Session session = Session.getDefaultInstance(props, null);
            email = new MimeMessage(session, new ByteArrayInputStream(emailBytes));
        }
        return email;
    }

    */

//MimeMessage rawMessage = getRawMessage(mService,Constants.USER,message.getId());
            /*MimeMessageParser parser = new MimeMessageParser(rawMessage);
            try {
                //parser.parse();
                String htmlContent = parser.getHtmlContent();
                String plainContent = parser.getPlainContent();
            } catch (Exception e) {
                e.printStackTrace();
            }*/
//Util.getContentFromMime(rawMessage);
//Log.d(TAG,"Content : "+rawMessage.getContent().toString());
