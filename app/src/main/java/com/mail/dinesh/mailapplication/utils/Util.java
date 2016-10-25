package com.mail.dinesh.mailapplication.utils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.StrictMode;
import android.util.Log;

import com.google.api.client.repackaged.org.apache.commons.codec.binary.Base64;
import com.mail.dinesh.mailapplication.bo.DirtyMailContent;
import com.mail.dinesh.mailapplication.conf.Constants;

import java.util.ArrayList;
import java.util.List;

//Source : https://github.com/skadyrov/GmailAPI-android-example/blob/master/app/src/main/java/
// android/api/gmail/at/ac/ait/androidgmailapiexample/Util.java
/**
 * Created by dinesh on 21.10.16.
 */

public class Util {
    
    private static final String TAG = Util.class.getSimpleName();

    public static boolean isNetworkAvailable(Context context, Activity activity) {
        ConnectivityManager connectivityManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    public static void removeStricMode() {

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
    }

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

    /*public static void getContentFromMime(MimeMessage m) throws IOException {
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
            *//*if (c instanceof String) {
                content = (String)c;
                Log.d(TAG,content);
            } else if (c instanceof Multipart) {
                Multipart multipart = (Multipart) c;
                BodyPart part = multipart.getBodyPart(0);
                content = part.toString();
                Log.d(TAG,content);
            }*//*
        } *//*catch (IOException e) {
            e.printStackTrace();
        }*//* catch (MessagingException e) {
            e.printStackTrace();
        }
        //return contentType;
    }*/

    public static DirtyMailContent placeMimeType(DirtyMailContent content, String mimeType) {

        if(mimeType.contains(Constants.PLAIN)) {
            content.setPlain(true);
        } else if (mimeType.contains(Constants.HTML)) {
            content.setHtml(true);
        } else if(mimeType.contains(Constants.ALT)) {
            content.setAlt(true);
        } else if (mimeType.contains(Constants.MIX)) {
            content.setMixed(true);
        } else {
            Log.d(TAG,"Dirty mail cannot process these mails in this version");
        }
        return content;
    }

    public static String getDate(String date) {
        String givenDate = "";
        if(date!=null) {
            String[] dateArray = date.split(" ");
            if(dateArray[0]!=null && dateArray[1]!=null && dateArray[2]!=null && dateArray[3]!=null) {
                givenDate = dateArray[0]+" "+
                        dateArray[1]+" "+dateArray[2]+" "+dateArray[3];
            }
        }
        return givenDate;
    }

    public static List<String> parseContentToHtml(DirtyMailContent content) {
        List<String> htmlData = new ArrayList<>();
        Log.d(TAG,"Data parsing");
        String[] partTypes = null;
        String[] parts = null;

        if(content.getPartTypes()!=null && content.getParts()!=null) {
            if(content.isAlt() || content.isHtml()) {
                partTypes = content.getPartTypes().split(Constants.BOUNDARY);
                parts = content.getParts().split(Constants.BOUNDARY);
            } /*else if (content.isPlain()) {
                Log.d(TAG,content.getPartTypes());
                Log.d(TAG,content.getParts());
            }*/

        }


        if(content.isAlt()) {
            if(partTypes!= null && parts!=null) {
                if(parts[0]!=null)
                    htmlData.add(parts[0]);
                if(parts[1]!=null)
                    htmlData.add(parts[1]);
            }
        } /*else if(content.isPlain()) {
            if(partTypes!= null && parts!=null) {
                if(parts[0]!=null)
                    htmlData.add(parts[0]);
                if(parts[1]!=null)
                    htmlData.add(parts[1]);
            }

        }*/ else if (content.isHtml()) {
            if(partTypes!= null && parts!=null) {
                if(parts[0]!=null)
                    htmlData.add(parts[0]);
                if(parts[1]!=null)
                    htmlData.add(parts[1]);
            }
        }
        return htmlData;
    }
}
