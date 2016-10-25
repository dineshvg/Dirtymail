package com.mail.dinesh.mailapplication.bo;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by dinesh on 22.10.16.
 *
 * Business Object class for storing and retrieving mails from Realm database
 */

public class DirtyMail extends RealmObject /*implements Parcelable*/ {

    public DirtyMail() {}


    //private int id;
    @PrimaryKey
    private String messageId;
    private String threadId;
    private String readLabel;
    private String date;
    private String subject;
    private boolean important;
    private String fromAddress;
    private String toAddress;
    private String replyToAddress;
    private String messageSnippet;
    private String messageFull;
    //Content based parsing
    private DirtyMailContent content;

    /*public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }*/

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

    public boolean getImportant() {
        return important;
    }

    public void setImportant(boolean important) {
        this.important = important;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public DirtyMailContent getContent() {
        return content;
    }

    public void setContent(DirtyMailContent content) {
        this.content = content;
    }

    /*@Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.messageId);
        dest.writeString(this.threadId);
        dest.writeString(this.readLabel);
        dest.writeString(this.date);
        dest.writeString(this.subject);
        dest.writeByte((byte) (important ? 1 : 0));
        dest.writeString(this.fromAddress);
        dest.writeString(this.toAddress);
        dest.writeString(this.replyToAddress);
        dest.writeString(this.messageSnippet);
        //Fix from
        // http://stackoverflow.com/questions/21551167/parcelable-list-that-contains-a-custom-object
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            dest.writeTypedObject(this.content,flags);
        }
    }


    private DirtyMail(Parcel in) {
        this.id = in.readInt();
        this.messageId = in.readString();
        this.threadId = in.readString();
        this.readLabel = in.readString();
        this.date = in.readString();
        this.subject = in.readString();
        this.important = in.readByte() != 0;
        this.fromAddress = in.readString();
        this.toAddress = in.readString();
        this.replyToAddress = in.readString();
        this.messageSnippet = in.readString();
        this.content = in.readParcelable(DirtyMailContent.class.getClassLoader());
    }

    public static final Parcelable.Creator<DirtyMail> CREATOR = new Parcelable.Creator<DirtyMail>() {
        public DirtyMail createFromParcel(Parcel source) {
            return new DirtyMail(source);
        }

        public DirtyMail[] newArray(int size) {
            return new DirtyMail[size];
        }
    };*/
}
