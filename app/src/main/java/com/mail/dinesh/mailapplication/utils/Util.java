package com.mail.dinesh.mailapplication.utils;

import android.util.Log;

import com.google.api.client.repackaged.org.apache.commons.codec.binary.Base64;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.internet.MimeMessage;

//Source : https://github.com/skadyrov/GmailAPI-android-example/blob/master/app/src/main/java/
// android/api/gmail/at/ac/ait/androidgmailapiexample/Util.java
/**
 * Created by dinesh on 21.10.16.
 */

public class Util {
    
    private static final String TAG = Util.class.getSimpleName();

    public static String base64UrlDecode(String input) {
        if(input != null && !input.isEmpty()) {
            String result = null;
            Base64 decoder = new Base64(true);
            byte[] decodedBytes = decoder.decode(input);
            result = new String(decodedBytes);
            return result;
        }
        return null;
    }

    public static int checkForRequiredLabels(List<String> labelIds) {

        if(labelIds.contains(Constants.UNREAD)){
            return Constants.UNREAD_NUM;
        } else if (labelIds.contains(Constants.READ)) {
            return Constants.READ_NUM;
        } else if (labelIds.contains(Constants.SENT)){
            return Constants.SENT_NUM;
        } else {
            return Constants.UNCATERGORIZED_NUM;
        }
    }

    public static boolean isImportantMail(List<String> labelIds) {
        if(labelIds.contains(Constants.IMPORTANT)) {
            return  true;
        } else {
            return false;
        }
    }

    public static void getContentFromMime(MimeMessage m) throws IOException {
        Object c = null;
        String contentType = "";
        String myMail = "";
        Part messagePart = m;
        try {
            Object content = m.getContent();
            if (content instanceof Multipart) {
                messagePart = ((Multipart) content).getBodyPart(0);
                Log.d(TAG,"[ Multipart Message ]");
            }
            contentType = messagePart.getContentType();
            Log.d(TAG,contentType);
            if(contentType.startsWith("TEXT/PLAIN") || contentType.startsWith("TEXT/HTML")) {
                InputStream is = m.getInputStream();
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(is));
                String thisLine = reader.readLine();
                while (thisLine != null) {
                    Log.d(TAG,thisLine);
                    myMail = myMail + thisLine;
                    thisLine = reader.readLine();
                }
            }
            /*if (c instanceof String) {
                content = (String)c;
                Log.d(TAG,content);
            } else if (c instanceof Multipart) {
                Multipart multipart = (Multipart) c;
                BodyPart part = multipart.getBodyPart(0);
                content = part.toString();
                Log.d(TAG,content);
            }*/
        } /*catch (IOException e) {
            e.printStackTrace();
        }*/ catch (MessagingException e) {
            e.printStackTrace();
        }
        //return contentType;
    }
}
