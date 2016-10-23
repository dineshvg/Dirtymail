package com.mail.dinesh.mailapplication.utils;

import android.util.SparseArray;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Created by dinesh on 21.10.16.
 */

public class Constants {

    public static final String APP_NAME = "Gmail API Android Quickstart";
    //The user's email address.
    public static final String USER = "me";
    //Query max result number to be pulled
    public static final String MAX_NUMBER = "1";
    // Query to get unread and read mails separately
    public static final String INBOX_QUERY = "in:inbox";
    public static final String INBOX_UNREAD_QUERY = "is:unread in:inbox";
    public static final String INBOX_READ_QUERY = "is:read in:inbox";

    /**
     * Key value pairs for Gmail
     */
    public static final String MSG_ID = "id";
    public static final String THREAD_ID = "threadId";
    public static final String LABELS = "labelIds";
    public static final String PAYLOAD = "payload";
    public static final String SNIPPET = "snippet";
    public static final String HEADERS = "headers";
    // Data pulled from inside the header
    public static final String DATE = "Date";
    public static final String SUBJECT = "Subject";
    public static final String REPLY_TO = "Reply-To";
    public static final String DELIVERED_TO = "Delivered-To";
    public static final String FROM = "From";

    /**
     * Mail labels
     */
    public static final String UNREAD = "UNREAD";
    public static final String READ = "READ";
    public static final String SENT = "SENT";
    public static final String IMPORTANT = "IMPORTANT";
    public static final String UNCATEGORIZED = "UNCATEGORIZED";

    public static final int UNREAD_NUM = 1;
    public static final int READ_NUM = 2;
    public static final int SENT_NUM = 3;
    public static final int UNCATERGORIZED_NUM = 0;

    //SparseArray is slower than hashmap, but more memory efficient.
    //For small datasets it makes sense.
    public static SparseArray<String> labelChecker() {
        final SparseArray<String> labelMap = new SparseArray<String>();
        labelMap.put(UNREAD_NUM, UNREAD);
        labelMap.put(READ_NUM, READ);
        labelMap.put(SENT_NUM, SENT);
        labelMap.put(UNCATERGORIZED_NUM, UNCATEGORIZED);
        return labelMap;
    }

    /**
     * Query parameters for mail
     * Source : https://developers.google.com/gmail/api/v1/reference/users/messages/get
     */

    //Returns the full email message data with body content in the raw field
    // as a base64url encoded string; the payload field is not used.
    public static final String EMAIL_RAW_FORMAT = "raw";

    //Returns only email message ID, labels, and email headers.
    public static final String EMAIL_METADATA_FORMAT = "metadata";

    //Returns the full email message data with body content parsed in the payload field;
    // the raw field is not used. (default)
    public static final String EMAIL_FULL_FORMAT = "full";

    //Returns only email message ID and labels; does not return the email headers, body, or payload.
    public static final String EMAIL_MIN_FORMAT = "minimal";

    //When given and format is METADATA, only include headers specified
    public static final String EMAIl_METADATA_HEADER = "metadataHeaders";

    /**
     * Mail header parts
     */
    public static final String MAIL_SUBJECT = "Subject";
    public static final int MISSING = -1;


}
