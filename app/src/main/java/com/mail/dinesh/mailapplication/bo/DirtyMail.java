package com.mail.dinesh.mailapplication.bo;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by dinesh on 22.10.16.
 *
 * Business Object class for storing and retrieving mails from Realm database
 */

public class DirtyMail extends RealmObject{

    public DirtyMail() {}

    @PrimaryKey
    private int id;
    private String messageId;
    private String threadId;
    private String readLabel;
    private String fromAddress;
    private String toAddress;
    private String replyToAddress;
    private String messageSnippet;
    private String messageFull;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getThreadId() {
        return threadId;
    }

    public void setThreadId(String threadId) {
        this.threadId = threadId;
    }

    public String getReadLabel() {
        return readLabel;
    }

    public void setReadLabel(String readLabel) {
        this.readLabel = readLabel;
    }

    public String getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    public String getToAddress() {
        return toAddress;
    }

    public void setToAddress(String toAddress) {
        this.toAddress = toAddress;
    }

    public String getReplyToAddress() {
        return replyToAddress;
    }

    public void setReplyToAddress(String replyToAddress) {
        this.replyToAddress = replyToAddress;
    }

    public String getMessageSnippet() {
        return messageSnippet;
    }

    public void setMessageSnippet(String messageSnippet) {
        this.messageSnippet = messageSnippet;
    }

    public String getMessageFull() {
        return messageFull;
    }

    public void setMessageFull(String messageFull) {
        this.messageFull = messageFull;
    }
}
