package com.mail.dinesh.mailapplication.utils;

import com.google.api.client.repackaged.org.apache.commons.codec.binary.Base64;

//Source : https://github.com/skadyrov/GmailAPI-android-example/blob/master/app/src/main/java/
// android/api/gmail/at/ac/ait/androidgmailapiexample/Util.java
/**
 * Created by dinesh on 21.10.16.
 */

public class Util {

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
}
