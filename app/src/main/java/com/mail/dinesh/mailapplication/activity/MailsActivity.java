package com.mail.dinesh.mailapplication.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mail.dinesh.mailapplication.R;
import com.mail.dinesh.mailapplication.adapter.MailAdapter;
import com.mail.dinesh.mailapplication.bo.DirtyMail;
import com.mail.dinesh.mailapplication.conf.Configuration;
import com.mail.dinesh.mailapplication.conf.Constants;
import com.mail.dinesh.mailapplication.dbUtils.RealmDBTransactions;
import com.mail.dinesh.mailapplication.service.UpdateMailService;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

public class MailsActivity extends AppCompatActivity {

    private static final String TAG = MailsActivity.class.getSimpleName();

    LinearLayout select_button_colour;
    List<DirtyMail> dirtyMails;
    MailsActivity activity;
    TextView service_button;
    private RecyclerView recyclerView;
    private MailAdapter adapter;
    String toAddress = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Log.d(TAG,"obtained mails");
        setContentView(R.layout.activity_mails);
        init();
        initListeners();
        setFilters();
        setupList();
    }

    private void setFilters() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Configuration.BROADCAST_ROW_SUB);
        registerReceiver(threadInfoReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(threadInfoReceiver);
    }

    private void initListeners() {

        //final Intent intent = new Intent(this, UpdateMailService.class);

        select_button_colour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mailIntent = new Intent(MailsActivity.this, SendMailActivity.class);
                mailIntent.putExtra(Constants.FROM_ID,toAddress);
                startActivity(mailIntent);
                /*String service_status = service_button.getText().toString();
                if(service_status.equals(Configuration.SERVICE_OFF)) {
                    startService(intent);
                    service_button.setText(Configuration.SERVICE_ON);
                } else {
                    service_button.setText(Configuration.SERVICE_OFF);
                    stopService(intent);
                }*/

            }
        });
    }

    private void init() {
        activity = MailsActivity.this;
        select_button_colour = (LinearLayout) findViewById(R.id.select_button_colour);
        dirtyMails =  RealmDBTransactions.showAllMails();
        if(dirtyMails!=null)  {
            toAddress = dirtyMails.get(0).getToAddress();
        }
        service_button = (TextView) findViewById(R.id.service_button);
        //DirtyMail mail = dirtyMails.get(0);
        //makeMsgIdList(dirtyMails);
        //Log.d(TAG,mail.getMessageSnippet());
        //Log.d(TAG,"mails retrieved");
    }

    private List<String> makeMsgIdList(List<DirtyMail> dirtyMails) {
        List<String> msgIds = new ArrayList<>();
        for(DirtyMail mail : dirtyMails) {
            msgIds.add(mail.getMessageId());
        }
        return msgIds;
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

    private void refreshList() {
        dirtyMails =  RealmDBTransactions.showAllMails();
        recyclerView.setAdapter(new MailAdapter(this, dirtyMails,activity));
    }

    private final BroadcastReceiver threadInfoReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(Configuration.BROADCAST_ROW_SUB)) {
                Bundle bundle = intent.getExtras();
                if(null!=bundle) {
                    if(bundle.get(Configuration.REFRESH_LIST)!=null) {
                        if(!bundle.getString(Configuration.REFRESH_LIST).equals("")) {
                            refreshList();
                        }
                    }
                }
            }
        }
    };


}
