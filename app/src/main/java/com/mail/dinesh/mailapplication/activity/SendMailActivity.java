package com.mail.dinesh.mailapplication.activity;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.Message;
import com.mail.dinesh.mailapplication.R;
import com.mail.dinesh.mailapplication.bo.DirtyMail;
import com.mail.dinesh.mailapplication.conf.Constants;
import com.mail.dinesh.mailapplication.googleUtils.GmailHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.realm.Realm;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class SendMailActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks{

    SendMailActivity activity;
    GoogleAccountCredential mCredential;
    GmailHelper apiQuery;

    String fromAddress;
    String toAddress;

    TextView from_address;
    EditText to_address;

    LinearLayout send_button;

    EditText subject;
    EditText content;

    ProgressBar progressbar;

    DirtyMail email = new DirtyMail();
    String bodyText = null;

    static final int REQUEST_ACCOUNT_PICKER = 1000;
    public  static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;
    private static final String PREF_ACCOUNT_NAME = "accountName";

    private static final String[] SCOPES =
            { GmailScopes.MAIL_GOOGLE_COM,
                    GmailScopes.GMAIL_MODIFY,
                    GmailScopes.GMAIL_COMPOSE,
                    GmailScopes.GMAIL_SEND};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle b = getIntent().getExtras();
        fromAddress = b.getString(Constants.FROM_ID);
        toAddress = b.getString(Constants.REPLY_ID);

        setContentView(R.layout.activity_send_mail);


        
        init();
        initListeners();
        fillContent();
    }


    private void init() {

        from_address = (TextView) findViewById(R.id.from_address);
        to_address = (EditText) findViewById(R.id.to_address);
        send_button = (LinearLayout) findViewById(R.id.send_button);
        subject = (EditText) findViewById(R.id.subject);
        content = (EditText) findViewById(R.id.content);
        progressbar = (ProgressBar) findViewById(R.id.progressBar);

        mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());
        activity = SendMailActivity.this;
    }

    private void initListeners() {

        send_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(from_address.getText()!=null && to_address.getText()!=null) {
                    if(subject!=null) {
                        if(content!=null) {
                            //TRUE
                            email.setFromAddress(from_address.getText().toString());
                            email.setToAddress(to_address.getText().toString());
                            email.setSubject(subject.getText().toString());
                            bodyText = content.getText().toString();
                            sendMailUsingGoogle();
                        } else {
                            Toast.makeText(getApplicationContext(),"Please enter mail content",Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(),"Please enter subject",Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(),"Please enter address",Toast.LENGTH_SHORT).show();
                }
                //sendMailUsingGoogle();
            }
        });
    }

    private void fillContent() {

        if(from_address!=null)
            from_address.setText(fromAddress);

        if(toAddress!=null)
            to_address.setText(toAddress);
    }

    private void sendMailUsingGoogle() {

        if (! isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        } else if (mCredential.getSelectedAccountName() == null) {
            chooseAccount();
        } else if (! isDeviceOnline()) {
            Toast.makeText(getApplicationContext(),"No network connection available.",Toast.LENGTH_SHORT).show();
        } else {
            new SendMailActivity.MakeRequestTask(mCredential,activity).execute();
        }

    }

    /**
     * Check that Google Play services APK is installed and up to date.
     * @return true if Google Play Services is available and up to
     *     date on this device; false otherwise.
     */
    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    /**
     * Attempt to resolve a missing, out-of-date, invalid or disabled Google
     * Play Services installation via a user dialog, if possible.
     */
    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }

    /**
     * Display an error dialog showing that Google Play Services is missing
     * or out of date.
     * @param connectionStatusCode code describing the presence (or lack of)
     *     Google Play Services on this device.
     */
    void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                SendMailActivity.this,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

    /**
     * Checks whether the device currently has a network connection.
     * @return true if the device has a network connection, false otherwise.
     */
    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    /**
     * Attempts to set the account used with the API credentials. If an account
     * name was previously saved it will use that one; otherwise an account
     * picker dialog will be shown to the user. Note that the setting the
     * account to use with the credentials object requires the app to have the
     * GET_ACCOUNTS permission, which is requested here if it is not already
     * present. The AfterPermissionGranted annotation indicates that this
     * function will be rerun automatically whenever the GET_ACCOUNTS permission
     * is granted.
     */
    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private void chooseAccount() {
        if (EasyPermissions.hasPermissions(
                this, Manifest.permission.GET_ACCOUNTS)) {
            String accountName = getPreferences(Context.MODE_PRIVATE)
                    .getString(PREF_ACCOUNT_NAME, null);
            if (accountName != null) {
                try {
                    mCredential.setSelectedAccountName(accountName);
                    sendMailUsingGoogle();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                // Start a dialog from which the user can choose an account
                startActivityForResult(
                        mCredential.newChooseAccountIntent(),
                        REQUEST_ACCOUNT_PICKER);
            }
        } else {
            // Request the GET_ACCOUNTS permission via a user dialog
            EasyPermissions.requestPermissions(
                    this,
                    "This app needs to access your Google account (via Contacts).",
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    Manifest.permission.GET_ACCOUNTS);
        }
    }

    /**
     * Called when an activity launched here (specifically, AccountPicker
     * and authorization) exits, giving you the requestCode you started it with,
     * the resultCode it returned, and any additional data from it.
     * @param requestCode code indicating which activity result is incoming.
     * @param resultCode code indicating the result of the incoming
     *     activity result.
     * @param data Intent (containing result data) returned by incoming
     *     activity result.
     */
    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                    Toast.makeText(getApplicationContext(),
                            "This app requires Google Play Services. Please install \" +\n" +
                                    "                                    \"Google Play Services on your device and relaunch this app.",
                            Toast.LENGTH_LONG).show();
                } else {
                    sendMailUsingGoogle();
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        SharedPreferences settings =
                                getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                        mCredential.setSelectedAccountName(accountName);
                        sendMailUsingGoogle();
                    }
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    sendMailUsingGoogle();
                }
                break;
        }
    }

    /**
     * Respond to requests for permissions at runtime for API 23 and above.
     * @param requestCode The request code passed in
     *     requestPermissions(android.app.Activity, String, int, String[])
     * @param permissions The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *     which is either PERMISSION_GRANTED or PERMISSION_DENIED. Never null.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(
                requestCode, permissions, grantResults, this);
    }

    /**
     * Callback for when a permission is granted using the EasyPermissions
     * library.
     * @param requestCode The request code associated with the requested
     *         permission
     * @param list The requested permission list. Never null.
     */
    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        // Do nothing.
    }

    /**
     * Callback for when a permission is denied using the EasyPermissions
     * library.
     * @param requestCode The request code associated with the requested
     *         permission
     * @param list The requested permission list. Never null.
     */
    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        // Do nothing.
    }


    private class MakeRequestTask extends AsyncTask<Void, Void, List<String>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressbar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(List<String> strings) {
            super.onPostExecute(strings);
            progressbar.setVisibility(View.INVISIBLE);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            progressbar.setVisibility(View.INVISIBLE);
        }

        private com.google.api.services.gmail.Gmail mService = null;
        private Exception mLastError = null;

        public MakeRequestTask(GoogleAccountCredential credential,SendMailActivity activity) {

            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.gmail.Gmail.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName(Constants.APP_NAME)
                    .build();
        }
        @Override
        protected List<String> doInBackground(Void... voids) {
            try {
                List<String> addresses = new ArrayList<>();
                Realm myRealm = Realm.getDefaultInstance();
                apiQuery = new GmailHelper();
                Message message = new Message();
                message = apiQuery.sendMail(mService,Constants.USER,myRealm, email,bodyText,activity);
                // listThreadsMatchingQuery(mService, Constants.USER, myRealm);
                return  addresses;
            } catch (Exception e) {
                mLastError = e;
                e.printStackTrace();
                cancel(true);
                return null;
            }
        }
    }


}
