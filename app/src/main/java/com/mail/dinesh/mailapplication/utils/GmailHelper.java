package com.mail.dinesh.mailapplication.utils;

import android.util.Log;

import com.google.api.client.util.Base64;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.ListThreadsResponse;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePart;
import com.google.api.services.gmail.model.MessagePartHeader;
import com.google.api.services.gmail.model.Thread;


import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
     * @param query String used to filter the Threads listed.
     * @throws IOException
     */
    public List<String> listThreadsMatchingQuery (Gmail service, String userId,
                                                 String query) throws IOException {
        List<String> threadsOfMails = new ArrayList<>();
        ListThreadsResponse response = service.users().threads().list(userId)
                .setMaxResults(Long.parseLong(Constants.MAX_NUMBER)).setQ(query).execute();
        List<Thread> threads = new ArrayList<Thread>();
        while(response.getThreads() != null) {
            threads.addAll(response.getThreads());
            Log.d(TAG,"size of thread "+threads.size());
            if(threads.size()>=50){
                break;
            }
        }

        for(Thread thread : threads) {

            threadsOfMails.add(thread.toPrettyString());
            System.out.println(thread.toPrettyString());
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
    private static void getThread(Gmail service, String userId, String threadId) throws IOException {
        Thread thread = service.users().threads().get(userId, threadId).execute();
        List<Message> messages = thread.getMessages();
        getMessage(service,messages);

        /*System.out.println("Thread id: " + thread.getId());
        System.out.println("No. of messages in this thread: " + thread.getMessages().size());
        System.out.println(thread.toPrettyString());*/
    }

    private static List<String> getMessage(Gmail mService,List<Message> messages) throws IOException {

        List<String> subjects = new ArrayList<String>();
        for (Message message : messages) {
            List<MessagePartHeader> mailHeader;
            Message fullMessage = getMessage(mService,Constants.USER,message.getId(),
                    Constants.EMAIL_FULL_FORMAT);
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
        return subjects;
    }



    /**
     * Fetch a list of read mails attached to the specified account.
     * @return List of messages
     * @throws IOException
     */
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
            /*if (response.getNextPageToken() != null) {
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
            }*/
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

    /**
     * Fetch a list of read mails attached to the specified account.
     * @return List of messages
     * @throws IOException
     */
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
    public static Message getMessage(Gmail service, String userId, String messageId, String format) throws IOException {
        Message message = null;
        if(format != null && !format.isEmpty()) {
            message = service.users().messages().get(userId, messageId).setFormat(format).execute();
        } else {
            message = service.users().messages().get(userId, messageId).execute();
        }

        /*System.out.println("Message snippet: " + message.getSnippet());
        System.out.println("-----------------------------------------------****");
        System.out.println("Message raw: " + Util.base64UrlDecode(message.getRaw()));
        System.out.println("Message raw Base64 android: " + Base64.encodeBase64URLSafeString(message.decodeRaw()));
        System.out.println("-----------------------------------------------****");*/

        return message;
    }
}
