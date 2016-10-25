package com.mail.dinesh.mailapplication.utils;

import android.util.Log;
import android.util.SparseArray;

import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePartHeader;
import com.mail.dinesh.mailapplication.bo.DirtyMail;
import com.mail.dinesh.mailapplication.bo.DirtyMailContent;
import com.mail.dinesh.mailapplication.conf.Constants;
import com.mail.dinesh.mailapplication.dbUtils.RealmDBTransactions;
import com.mail.dinesh.mailapplication.googleUtils.DirtyMailHelper;

import java.io.IOException;
import java.util.List;

/**
 * Created by dinesh on 23.10.16.
 */

public class Manager {

    public static final String TAG = Manager.class.getSimpleName();

    public static DirtyMail convertGmailToDirtyMail(Message fullMessage) throws IOException {

        DirtyMail mail = new DirtyMail();
        Log.d(TAG,"conversion ...");
        //Set mail ID
        if(fullMessage.getId()!=null || !fullMessage.getId().equals("")) {
            Log.d(TAG,"msg id : "+fullMessage.getId());
            mail.setMessageId(fullMessage.getId());
        }
        //Set thread ID
        if(fullMessage.getThreadId()!=null || !fullMessage.getThreadId().equals("")) {
            Log.d(TAG,"thread id : "+fullMessage.getThreadId());
            mail.setThreadId(fullMessage.getThreadId());
        }
        //Set read, unread, sent label and also set important or not important label
        if(fullMessage.getLabelIds()!=null) {
            int labelNum = Util.checkForRequiredLabels(fullMessage.getLabelIds());
            SparseArray<String> labelMap = Constants.labelChecker();
            String label = labelMap.get(labelNum);
            if(label!=null || !label.equals("")) {
                Log.d(TAG, "mail label :"+label);
                mail.setReadLabel(label);
            }
            mail.setImportant(Util.isImportantMail(fullMessage.getLabelIds()));
        }
        //Set mail Snippet
        if(fullMessage.getSnippet()!=null || !fullMessage.getSnippet().equals("")) {
            Log.d(TAG,"Snippet :"+fullMessage.getSnippet());
            mail.setMessageSnippet(fullMessage.getSnippet());
        }

        //Get payload -> header and content from mail
        if(fullMessage.getPayload()!=null) {
            //Get content
            DirtyMailContent content = DirtyMailHelper.getContent(fullMessage);
            mail.setContent(content);
            //Get headers
            if(fullMessage.getPayload().getHeaders()!=null) {
                List<MessagePartHeader> headers = fullMessage.getPayload().getHeaders();
                mail = DirtyMailHelper.getHeaderParts(mail,headers);
                Log.d(TAG,"dirty mail created");
            }
        }
        return mail;
    }

    /*public static MimeMessage getMimeMsg(Gmail service, String userId, String messageId)
            throws IOException, MessagingException {

        MimeMessage email = null;
        if(service!=null && !userId.equals("") && !messageId.equals("")) {
            Message message = service.users().messages().get(userId, messageId)
                    .setFormat(Constants.EMAIL_RAW_FORMAT).execute();
            com.google.api.client.repackaged.org.apache.commons.codec.binary.Base64 base64Url = new com.google.api.client.repackaged.org.apache.commons.codec.binary.Base64(true);
            byte[] emailBytes = base64Url.decodeBase64(message.getRaw());
            Properties props = new Properties();
            Session session = Session.getDefaultInstance(props, null);
            email = new MimeMessage(session, new ByteArrayInputStream(emailBytes));
        }
        return email;
    }*/
}

    /*private static void getContent(MessagePart payLoad) {
        String mimeType = payLoad.getMimeType();
        Log.d("New Mail","------------------------------------------------------");
        Log.d(TAG,"Mime type of payload: "+mimeType);
        String mailBody = "";
        List<MessagePart> parts = payLoad.getParts();

        if(mimeType.contains("mixed")) {
            if(parts!=null) {
                Log.d(TAG,parts.size()+" parts in the mixed mime");
                for (MessagePart part : parts) {
                    Log.d(TAG,"part mimeType : "+part.getMimeType());
                    if(part!=null) {
                        if(part.getBody()!=null) {
                            if(part.getBody().getData()!=null) {
                                Log.d("Data", part.getBody().getData());
                                Log.d(TAG,"part established");
                            }

                        }
                    }
                }
            }
        }


        if(mimeType.contains("alternative") || mimeType.contains("plain") || mimeType.contains("html")) {
            if(parts!=null) {
                for (MessagePart part : parts) {
                    Log.d(TAG,"mimeType of part"+part.getMimeType());
                    mailBody = new String(Base64.decodeBase64(part.getBody()
                            .getData().getBytes()));
                    Log.d(TAG,mailBody);
                }
            }
        }

    }

    // Pull all the information that is required from the header which is required to be stored
    // inside the Realm Database.
    private static DirtyMail getHeaderParts(DirtyMail mail, List<MessagePartHeader> headers)
    {
        try {
            for (MessagePartHeader header : headers) {
                String name = header.getName().trim();

                //Pull delivered-to address
                if(name.equals(Constants.DELIVERED_TO.trim())) {
                    String value = header.getValue().trim();
                    mail.setToAddress(value);
                }

                //Pull reply-to address
                if(name.equals(Constants.REPLY_TO.trim())) {
                    String value = header.getValue().trim();
                    mail.setReplyToAddress(value);
                }

                //Pull delivered-from address
                if(name.equals(Constants.FROM.trim())) {
                    String value = header.getValue().trim();
                    mail.setFromAddress(value);
                }

                //Pull date
                if(name.equals(Constants.DATE.trim())) {
                    String value = header.getValue().trim();
                    mail.setDate(value);
                }

                //Pull Subject
                if(name.equals(Constants.SUBJECT.trim())) {
                    String value = header.getValue().trim();
                    mail.setSubject(value);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return mail;
    }*/
