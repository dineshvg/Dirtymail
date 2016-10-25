package com.mail.dinesh.mailapplication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.mail.dinesh.mailapplication.bo.DirtyMail;
import com.mail.dinesh.mailapplication.conf.Constants;
import com.mail.dinesh.mailapplication.dbUtils.RealmDBTransactions;
import com.mail.dinesh.mailapplication.utils.Util;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

/**
 * Created by dinesh on 25.10.16.
 */

public class MailAdapter extends RecyclerView.Adapter {

    private List<DirtyMail> mDataSet = new ArrayList<>();
    private LayoutInflater mInflater;
    private final ViewBinderHelper binderHelper = new ViewBinderHelper();
    private MailsActivity activity;

    /*public MailAdapter(Context context, List<String> dataSet) {
        mDataSet = dataSet;
        mInflater = LayoutInflater.from(context);

        // uncomment if you want to open only one row at a time
        // binderHelper.setOpenOnlyOne(true);
    }*/

    public MailAdapter(Context context, List<DirtyMail> dataSet, MailsActivity activity) {
        mDataSet = dataSet;
        mInflater = LayoutInflater.from(context);
        this.activity = activity;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.row_mail, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder h, int position) {
        final ViewHolder holder = (ViewHolder) h;

        if (mDataSet != null && 0 <= position && position < mDataSet.size()) {
            final DirtyMail data = mDataSet.get(position);
            String msgId = data.getMessageId();
            // Use ViewBindHelper to restore and save the open/close state of the SwipeRevealView
            // put an unique string id as value, can be any string which uniquely define the data
            binderHelper.bind(holder.swipeLayout, msgId);

            // Bind your data here
            holder.bind(data);
        }
    }

    @Override
    public int getItemCount() {
        if (mDataSet == null)
            return 0;
        return mDataSet.size();
    }

    /**
     * Only if you need to restore open/close state when the orientation is changed.
     * Call this method in {@link android.app.Activity#onSaveInstanceState(Bundle)}
     */
    public void saveStates(Bundle outState) {
        binderHelper.saveStates(outState);
    }

    /**
     * Only if you need to restore open/close state when the orientation is changed.
     * Call this method in {@link android.app.Activity#onRestoreInstanceState(Bundle)}
     */
    public void restoreStates(Bundle inState) {
        binderHelper.restoreStates(inState);
    }

    private class ViewHolder extends RecyclerView.ViewHolder {
        private SwipeRevealLayout swipeLayout;
        private TextView more;
        private TextView delete;
        private TextView from;
        private TextView date;
        private TextView subject;
        private TextView snippet;

        public ViewHolder(View itemView) {
            super(itemView);
            swipeLayout = (SwipeRevealLayout) itemView.findViewById(R.id.swipe_layout);
            more = (TextView) itemView.findViewById(R.id.more);
            delete = (TextView) itemView.findViewById(R.id.delete);
            from = (TextView) itemView.findViewById(R.id.from);
            date = (TextView) itemView.findViewById(R.id.date);
            subject = (TextView) itemView.findViewById(R.id.subject);
            snippet = (TextView) itemView.findViewById(R.id.snippet);
        }

        public void bind(final DirtyMail data) {

            more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent mailIntent = new Intent(activity, DirtyMailActivity.class);
                    mailIntent.putExtra(Constants.MAIL_ID,data.getMessageId());
                    activity.startActivity(mailIntent);
                }
            });

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDataSet.remove(getAdapterPosition());
                    DirtyMail mailToDelete = mDataSet.get(getAdapterPosition());
                    Log.d("Mail to delete", mailToDelete.getMessageId());
                    RealmDBTransactions.deleteData(Realm.getDefaultInstance(),mailToDelete.getMessageId());
                    notifyItemRemoved(getAdapterPosition());
                }
            });

            if(data.getFromAddress()!=null) {
                String[] fromAddressArray = data.getFromAddress().split("<");
                if (fromAddressArray[0]!=null)
                    from.setText(fromAddressArray[0]);
            }


            if(data.getDate()!=null) {
                date.setText(Util.getDate(data.getDate()));
            }


            if(data.getSubject()!=null)
                subject.setText(data.getSubject());

            if(data.getMessageSnippet()!=null)
                snippet.setText(data.getMessageSnippet());
        }
    }
}
