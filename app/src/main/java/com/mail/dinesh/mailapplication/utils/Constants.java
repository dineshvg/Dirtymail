package com.mail.dinesh.mailapplication.utils;

/**
 * Created by dinesh on 21.10.16.
 */

public class Constants {

    public static final String APP_NAME = "Gmail API Android Quickstart";
    //The user's email address.
    public static final String USER = "me";
    //Query max result number to be pulled
    public static final String MAX_NUMBER = "50";
    // Query to get unread and read mails separately
    public static final String INBOX_UNREAD_QUERY = "in:inbox";
    public static final String INBOX_READ_QUERY = "is:read in:inbox";

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
