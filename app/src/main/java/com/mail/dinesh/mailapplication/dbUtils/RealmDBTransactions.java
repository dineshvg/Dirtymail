package com.mail.dinesh.mailapplication.dbUtils;

import android.widget.Toast;

import com.mail.dinesh.mailapplication.activity.MailsActivity;
import com.mail.dinesh.mailapplication.bo.DirtyMail;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by dinesh on 22.10.16.
 */

public class RealmDBTransactions {

    private static final String TAG = "Realm";

    public static DirtyMail  commitMailToDB(DirtyMail mail, Realm realm) {

        DirtyMail mailObj = new DirtyMail();
        try {
            realm.beginTransaction();
            // Create an object
            mailObj = realm.copyToRealmOrUpdate(mail);
            realm.commitTransaction();
            //Log.d(TAG, mailObj.getId()+"");
            //Log.d(TAG,"transaction done");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mailObj;
    }

    public static void deleteData(Realm realm, String msgId) {
        RealmResults<DirtyMail> results = realm.where(DirtyMail.class).findAll();
        DirtyMail mail = results .where().equalTo("messageId",msgId).findFirst();
        if(mail!=null) {
            if (!realm.isInTransaction())
            {
                realm.beginTransaction();
            }
            mail.removeFromRealm();
            realm.commitTransaction();
        }
    }

    public static DirtyMail getMail(Realm realm, String msgId) {
        RealmResults<DirtyMail> results = realm.where(DirtyMail.class).findAll();
        DirtyMail mail = results .where().equalTo("messageId",msgId).findFirst();
        /*if(mail!=null) {
            Log.d(TAG,mail.getDate());
        }*/
        return mail;
    }



    //Primary key implementation
    private static int getNextKey(Realm realm)
    {
        try {
            int maxVal = realm.where(DirtyMail.class).max("id").intValue() + 1;
            return maxVal;
        } catch (Exception e) {
            return 0;
        }

    }

    public static List<DirtyMail> showAllMails() {
        Realm realm = Realm.getDefaultInstance();
        final List<DirtyMail> dirtyMails = new ArrayList<>();
        try {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmResults<DirtyMail> results = realm.where(DirtyMail.class).findAll();
                    for(DirtyMail c:results) {
                        dirtyMails.add(c);
                        //Log.d(TAG, c.getFromAddress());
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dirtyMails;
    }

    //Test method
    public static void pullAllData(Realm realm,MailsActivity activity) {
        RealmResults<DirtyMail> results = realm.where(DirtyMail.class).findAll();
        Toast.makeText(activity.getApplicationContext(), results.size()+"",Toast.LENGTH_SHORT).show();

    }
}
