package com.mail.dinesh.mailapplication.bo;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

import io.realm.RealmObject;

/**
 * Created by dinesh on 24.10.16.
 */

public class DirtyMailContent extends RealmObject /*implements Parcelable*/{

    public DirtyMailContent() {}

    //private int contentId;
    //private int id; //ID to link to Dirty mail
    private boolean isMixed;
    private boolean isAlt;
    private boolean isPlain;
    private boolean isHtml;
    private Integer numOfParts;
    private String parts;
    private boolean hasAttachment;
    private String partTypes;

    /*protected DirtyMailContent(Parcel in) {
        contentId = in.readInt();
        id = in.readInt();
        isMixed = in.readByte() != 0;
        isAlt = in.readByte() != 0;
        isPlain = in.readByte() != 0;
        isHtml = in.readByte() != 0;
        parts = in.readString();
        hasAttachment = in.readByte() != 0;
        partTypes = in.readString();
    }*/

    /*public static final Creator<DirtyMailContent> CREATOR = new Creator<DirtyMailContent>() {
        @Override
        public DirtyMailContent createFromParcel(Parcel in) {
            return new DirtyMailContent(in);
        }

        @Override
        public DirtyMailContent[] newArray(int size) {
            return new DirtyMailContent[size];
        }
    };*/

    public boolean isMixed() {
        return isMixed;
    }

    public void setMixed(boolean mixed) {
        isMixed = mixed;
    }

    public boolean isAlt() {
        return isAlt;
    }

    public void setAlt(boolean alt) {
        isAlt = alt;
    }

    public boolean isPlain() {
        return isPlain;
    }

    public void setPlain(boolean plain) {
        isPlain = plain;
    }

    public boolean isHtml() {
        return isHtml;
    }

    public void setHtml(boolean html) {
        isHtml = html;
    }

    public Integer getNumOfParts() {
        return numOfParts;
    }

    public void setNumOfParts(Integer numOfParts) {
        this.numOfParts = numOfParts;
    }

    public String getParts() {
        return parts;
    }

    public void setParts(String parts) {
        this.parts = parts;
    }

    public boolean isHasAttachment() {
        return hasAttachment;
    }

    public void setHasAttachment(boolean hasAttachment) {
        this.hasAttachment = hasAttachment;
    }

    public String getPartTypes() {
        return partTypes;
    }

    public void setPartTypes(String partTypes) {
        this.partTypes = partTypes;
    }

    /*@Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(contentId);
        parcel.writeInt(id);
        parcel.writeByte((byte) (isMixed ? 1 : 0));
        parcel.writeByte((byte) (isAlt ? 1 : 0));
        parcel.writeByte((byte) (isPlain ? 1 : 0));
        parcel.writeByte((byte) (isHtml ? 1 : 0));
        parcel.writeString(parts);
        parcel.writeByte((byte) (hasAttachment ? 1 : 0));
        parcel.writeString(partTypes);
    }*/

    /*public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getContentId() {
        return contentId;
    }

    public void setContentId(int contentId) {
        this.contentId = contentId;
    }*/
}
