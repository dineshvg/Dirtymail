package com.mail.dinesh.mailapplication.utils;

import android.util.Log;

import com.google.api.client.repackaged.org.apache.commons.codec.binary.Base64;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.ListThreadsResponse;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.Thread;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

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
    public List<String> listThreadsMatchingQuery (Gmail service, String userId) throws IOException, MessagingException {
        List<String> threadsOfMails = new ArrayList<>();
        ListThreadsResponse response = service.users().threads().list(userId)
                .setMaxResults(Long.parseLong(Constants.MAX_NUMBER)).setQ(Constants.INBOX_QUERY).execute();
        List<Thread> threads = new ArrayList<Thread>();
        while(response.getThreads() != null) {
            threads.addAll(response.getThreads());
            Log.d(TAG,"size of thread "+threads.size());
            if(threads.size()>=Integer.parseInt(Constants.MAX_NUMBER)){
                break;
            }
        }

        for(Thread thread : threads) {
            getThread(service,Constants.USER,thread.getId());
            //threadsOfMails.add(subjects.get(0));
            //Log.d(TAG,subjects.get(0));
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
     * @throws IOException
     */
    private static List<String> getThread(Gmail service, String userId, String threadId) throws IOException, MessagingException {
        Thread thread = service.users().threads().get(userId, threadId).execute();
        List<Message> messages = thread.getMessages();
        return getMessages(service,messages);
    }

    private static List<String> getMessages(Gmail mService,List<Message> messages) throws IOException, MessagingException {

        List<String> subjects = new ArrayList<String>();
        for (Message message : messages) {
            Message fullMessage = getMessage(mService,Constants.USER,message.getId(),
                    Constants.EMAIL_FULL_FORMAT);
            MimeMessage rawMessage = getRawMessage(mService,Constants.USER,message.getId());
            /*MimeMessageParser parser = new MimeMessageParser(rawMessage);
            try {
                parser.parse();
                String htmlContent = parser.getHtmlContent();
                String plainContent = parser.getPlainContent();
            } catch (Exception e) {
                e.printStackTrace();
            }*/
            //String content = Util.getContentFromMime(rawMessage);
            //Log.d(TAG,"Content : "+rawMessage.getContent().toString());
            Log.d(TAG,"mail");
            // TODO Add mail into realmDB and show it.
            try {
                Manager.convertGmailToDirtyMail(fullMessage);
                Log.d(TAG,"conversion done");
            } catch (Exception e) {
                e.printStackTrace();
            }


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
        }
        return subjects;
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
    }*/
}
