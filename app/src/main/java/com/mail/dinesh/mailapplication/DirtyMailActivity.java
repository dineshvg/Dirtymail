package com.mail.dinesh.mailapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.mail.dinesh.mailapplication.bo.DirtyMail;
import com.mail.dinesh.mailapplication.conf.Constants;
import com.mail.dinesh.mailapplication.dbUtils.RealmDBTransactions;
import com.mail.dinesh.mailapplication.utils.Util;

import java.util.List;

import io.realm.Realm;

public class DirtyMailActivity extends AppCompatActivity {

    private static final String TAG = DirtyMailActivity.class.getSimpleName();

    TextView subject;
    TextView date;
    TextView from_address;
    TextView to_address;
    ImageView reply_to_button;
    TextView snippet;
    WebView content;

    DirtyMail mail;
    String msg_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle b = getIntent().getExtras();
        msg_id = b.getString(Constants.MAIL_ID);
        if(msg_id!=null) {
            Log.i(TAG,"id :"+msg_id);
           // Log.d(TAG,"ID of the mail being shown here is : "+msg_id);
            setContentView(R.layout.activity_dirty_mail);
            init();
            initListeners();
            fillData();
        }
        
    }


    private void init() {
        subject = (TextView)findViewById(R.id.subject);
        date = (TextView) findViewById(R.id.date);
        from_address = (TextView) findViewById(R.id.from_address);
        reply_to_button = (ImageView) findViewById(R.id.reply_to_button);
        to_address = (TextView) findViewById(R.id.to_address);
        snippet = (TextView) findViewById(R.id.snippet);
        content = (WebView) findViewById(R.id.content);
    }

    private void initListeners() {

    }

    private void fillData() {
        try {
            mail = RealmDBTransactions.getMail(Realm.getDefaultInstance(),msg_id);
            if(mail!=null) {
                subject.setText(mail.getSubject());
                date.setText(Util.getDate(mail.getDate()));
                from_address.setText(mail.getFromAddress());
                to_address.setText(mail.getToAddress());
                if(mail.getContent()!=null) {
                    snippet.setVisibility(View.INVISIBLE);
                    content.setVisibility(View.VISIBLE);
                    List<String> data = Util.parseContentToHtml(mail.getContent());
                    if(mail.getContent().isAlt() || mail.getContent().isHtml()) {
                        if(data.get(1) !=null)
                            content.loadData(data.get(1), "text/html; charset=utf-8", "UTF-8");
                        else
                            content.loadData(data.get(1), "text/plain; charset=utf-8", "UTF-8");
                    } else if(mail.getContent().isPlain()) {
                        if(mail.getMessageSnippet()!=null) {
                            snippet.setVisibility(View.VISIBLE);
                            content.setVisibility(View.INVISIBLE);
                            snippet.setText(mail.getContent().getParts());
                            //content.loadData(mail.getContent().getParts(),"text/plain; charset=utf-8", "UTF-8");
                        }
                        /*if(data.get(1) !=null)
                            content.loadData(data.get(1), "text/html; charset=utf-8", "UTF-8");
                        else
                            content.loadData(data.get(1), "text/plain; charset=utf-8", "UTF-8");*/
                    }
                } else {
                    if(mail.getMessageSnippet()!=null) {
                        snippet.setVisibility(View.VISIBLE);
                        content.setVisibility(View.INVISIBLE);
                        snippet.setText(mail.getMessageSnippet());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
