package com.mail.dinesh.mailapplication.utils;

import android.util.Log;
import android.util.SparseArray;

import com.google.api.client.util.Base64;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePart;
import com.google.api.services.gmail.model.MessagePartHeader;
import com.mail.dinesh.mailapplication.bo.DirtyMail;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.List;

/**
 * Created by dinesh on 23.10.16.
 */

public class Manager {

    public static final String TAG = Manager.class.getSimpleName();

    public static void convertGmailToDirtyMail(Message fullMessage) throws IOException {

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
            MessagePart payLoad = fullMessage.getPayload();
            //Get content
            getContent(payLoad);
            //Get headers
            if(payLoad.getHeaders()!=null) {
                List<MessagePartHeader> headers = payLoad.getHeaders();
                mail = getHeaderParts(mail,headers);
                Log.d(TAG,"dirty mail created");
            }
        }
    }

    private static void getContent(MessagePart payLoad) {
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
        /*if(mimeType.contains("mixed")) {
            for (MessagePart part : parts) {
                String partMimeType = part.getMimeType();
                Log.d(TAG,"part mime "+part.getMimeType());
                String partMailBody = "";

                if(partMimeType.contains("plain")) {
                    partMailBody = new String(Base64.decodeBase64(part.getBody()
                            .getData().getBytes()));
                    Log.d(TAG,partMailBody);
                }
                if(partMimeType.contains("html")) {
                    partMailBody = new String(Base64.decodeBase64(part.getBody()
                            .getData().getBytes()));
                    Log.d(TAG,partMailBody);
                }
                if(partMimeType.contains("alternative")) {
                    partMailBody = new String(Base64.decodeBase64(part.getBody()
                            .getData().getBytes()));
                    Log.d(TAG,partMailBody);
                }
                *//*mailBody = new String(Base64.decodeBase64(part.getBody()
                        .getData().getBytes()));
                Log.d(TAG,mailBody);*//*
            }
        }*/

        if(mimeType.contains("plain")) {
            if(parts!=null) {
                for (MessagePart part : parts) {
                    Log.d(TAG,"mimeType of part"+part.getMimeType());
                    mailBody = new String(Base64.decodeBase64(part.getBody()
                            .getData().getBytes()));
                    Log.d(TAG,mailBody);
                }
            }
        }

        if(mimeType.contains("html")) {
            if(parts!=null) {
                for (MessagePart part : parts) {
                    Log.d(TAG,"mimeType of part"+part.getMimeType());
                    mailBody = new String(Base64.decodeBase64(part.getBody()
                            .getData().getBytes()));
                    Log.d(TAG,mailBody);
                }
            }
        }

        if (mimeType.contains("alternative")) {
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
    }
}
