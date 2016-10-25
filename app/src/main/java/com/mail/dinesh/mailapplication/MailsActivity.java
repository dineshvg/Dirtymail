package com.mail.dinesh.mailapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.mail.dinesh.mailapplication.bo.DirtyMail;
import com.mail.dinesh.mailapplication.bo.DirtyMailContent;
import com.mail.dinesh.mailapplication.conf.Configuration;
import com.mail.dinesh.mailapplication.dbUtils.RealmDBTransactions;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

public class MailsActivity extends AppCompatActivity {

    private static final String TAG = MailsActivity.class.getSimpleName();

    LinearLayout select_button_colour;
    List<DirtyMail> dirtyMails;
    MailsActivity activity;
    private RecyclerView recyclerView;
    private MailAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Log.d(TAG,"obtained mails");
        setContentView(R.layout.activity_mails);
        init();
        initListeners();
        setupList();
    }

    private void initListeners() {

        select_button_colour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RealmDBTransactions.pullAllData(Realm.getDefaultInstance(),activity);
            }
        });
    }

    private void init() {
        activity = MailsActivity.this;
        select_button_colour = (LinearLayout) findViewById(R.id.select_button_colour);
        dirtyMails =  RealmDBTransactions.showAllMails();
        DirtyMail mail = dirtyMails.get(0);
        //Log.d(TAG,mail.getMessageSnippet());
        //Log.d(TAG,"mails retrieved");
    }

    private List<String> createList(int n) {
        List<String> list = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            list.add("View " + i);
        }

        return list;
    }

    private void setupList() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new MailAdapter(this, dirtyMails,activity);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Only if you need to restore open/close state when
        // the orientation is changed
        if (adapter != null) {
            adapter.saveStates(outState);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // Only if you need to restore open/close state when
        // the orientation is changed
        if (adapter != null) {
            adapter.restoreStates(savedInstanceState);
        }
    }
}
