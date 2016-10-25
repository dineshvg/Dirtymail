package com.mail.dinesh.mailapplication.googleUtils;

import android.util.Log;

import com.google.api.client.repackaged.org.apache.commons.codec.binary.Base64;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePart;
import com.google.api.services.gmail.model.MessagePartHeader;
import com.mail.dinesh.mailapplication.bo.DirtyMail;
import com.mail.dinesh.mailapplication.bo.DirtyMailContent;
import com.mail.dinesh.mailapplication.conf.Constants;
import com.mail.dinesh.mailapplication.utils.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dinesh on 24.10.16.
 */

public class DirtyMailHelper {

    private static final String TAG = DirtyMailHelper.class.getSimpleName();

    public static DirtyMail getHeaderParts(DirtyMail mail, List<MessagePartHeader> headers)
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

    public static DirtyMailContent getContent(Message msg) {

        DirtyMailContent content = new DirtyMailContent();
        MessagePart payLoad = msg.getPayload();
        String mimeType = payLoad.getMimeType();
        content = Util.placeMimeType(content,mimeType);
        String mailBody = "";

        List<MessagePart> parts = payLoad.getParts();
        String mailBodyParts = "";
        String mailBodyMimeTypes = "";

        if(mimeType.contains(Constants.PLAIN)) {
            //Log.d(TAG,msg.toString());
            if(msg.getPayload()!=null) {
                if(msg.getPayload().getBody()!=null) {
                    if(msg.getPayload().getBody().getData()!=null) {
                        String data = Util.base64UrlDecode(msg.getPayload().getBody().getData());
                        content.setParts(data);
                        content.setNumOfParts(1);
                        content.setPartTypes(mimeType);
                    }
                }
            }
        } else if(mimeType.contains(Constants.ALT)
                || mimeType.contains(Constants.HTML)) {
            if(parts!=null) {
                for (MessagePart part : parts) {
                    //commitMsgPartToContent(part);
                    if(part!=null) {
                        if(part.getBody()!=null) {
                            if(part.getBody().getData()!=null) {
                                //Log.d(TAG,"decoding part");
                                mailBody = new String(Base64.decodeBase64(part.getBody()
                                        .getData().getBytes()));
                                mailBodyParts = mailBodyParts+mailBody+Constants.BOUNDARY;
                                mailBodyMimeTypes = mailBodyMimeTypes+part.getMimeType()+Constants.BOUNDARY;
                                //Log.d(TAG,mailBody);
                            }
                        }
                    }
                }
                content.setParts(mailBodyParts);
                content.setPartTypes(mailBodyMimeTypes);
                if(parts!=null)
                    content.setNumOfParts(parts.size());
            }
        } //TODO : Parse Mix type mails and show them
        /*else if (mimeType.contains(Constants.MIX)) {
            parse(parts,content, mailBodyParts, mailBodyMimeTypes);
        }*/

        return content;
    }
}

    /*private static void commitMsgPartToContent(MessagePart part) {

        if(part!=null) {
            if(part.getBody()!=null) {
                if(part.getBody().getData()!=null) {
                    Log.d(TAG,"decoding part");
                    String mailBody = new String(Base64.decodeBase64(part.getBody()
                            .getData().getBytes()));
                    //mailBodyParts.add(mailBody);
                    //mailBodyMimeTypes.add(part.getMimeType());
                    Log.d(TAG,mailBody);
                }
            }
        }
    }

    private static void parse(List<MessagePart> parts, DirtyMailContent content,
                              List<String> mailBodyParts, List<String> mailBodyMimeTypes) {
        for (MessagePart parentPart : parts) {
            if(parentPart.getMimeType().equals(Constants.MIX)) {
                Log.d(TAG,"more layers");
            } else {
                String mimeType = parentPart.getMimeType();
                if(mimeType.contains(Constants.ALT) || mimeType.contains(Constants.PLAIN)
                        || mimeType.contains(Constants.HTML)) {
                    List<MessagePart> childParts = parentPart.getParts();

                    if(childParts!=null) {
                        for (MessagePart part : childParts) {
                            if(part!=null) {
                                if(part.getBody()!=null) {
                                    if(part.getBody().getData()!=null) {
                                        Log.d(TAG,"decoding part");
                                        String mailBody = new String(Base64.decodeBase64(part.getBody()
                                                .getData().getBytes()));
                                        mailBodyParts.add(mailBody);
                                        mailBodyMimeTypes.add(part.getMimeType());
                                        Log.d(TAG,mailBody);
                                    }
                                }
                            }
                        }
                        //content.setParts(mailBodyParts);
                        //content.setPartTypes(mailBodyMimeTypes);
                        content.setNumOfParts(parts.size());
                    }

                }
            }
        }
    }*/



    /*protected void parse(final Multipart parent, final MimePart part)
            throws MessagingException, IOException
    {
        if (isMimeType(part, "text/plain") && plainContent == null
                && !MimePart.ATTACHMENT.equalsIgnoreCase(part.getDisposition()))
        {
            plainContent = (String) part.getContent();
        }
        else
        {
            if (isMimeType(part, "text/html") && htmlContent == null
                    && !MimePart.ATTACHMENT.equalsIgnoreCase(part.getDisposition()))
            {
                htmlContent = (String) part.getContent();
            }
            else
            {
                if (isMimeType(part, "multipart*//*"))
                {
                    this.isMultiPart = true;
                    final Multipart mp = (Multipart) part.getContent();
                    final int count = mp.getCount();

                    // iterate over all MimeBodyPart

                    for (int i = 0; i < count; i++)
                    {
                        parse(mp, (MimeBodyPart) mp.getBodyPart(i));
                    }
                }
                else
                {
                    this.attachmentList.add(createDataSource(parent, part));
                }
            }
        }
    }*/



/*if(mimeType.contains("html")) {
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
        }*/
