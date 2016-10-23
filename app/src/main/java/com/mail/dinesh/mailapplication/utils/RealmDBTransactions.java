package com.mail.dinesh.mailapplication.utils;

import com.mail.dinesh.mailapplication.bo.DirtyMail;

import io.realm.Realm;

/**
 * Created by dinesh on 22.10.16.
 */

public class RealmDBTransactions {

    //Primary key implementation
    private int getNextKey(Realm realm)
    {
        return realm.where(DirtyMail.class).max("id").intValue() + 1;
    }
}
