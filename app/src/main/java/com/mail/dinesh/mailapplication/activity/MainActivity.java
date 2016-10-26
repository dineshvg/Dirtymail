package com.mail.dinesh.mailapplication.activity;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.badoo.mobile.util.WeakHandler;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.gmail.GmailScopes;
import com.mail.dinesh.mailapplication.R;
import com.mail.dinesh.mailapplication.conf.Configuration;
import com.mail.dinesh.mailapplication.conf.Constants;
import com.mail.dinesh.mailapplication.googleUtils.GmailHelper;
import com.mail.dinesh.mailapplication.service.UpdateDirtyMail;
import com.mail.dinesh.mailapplication.service.UpdateMailService;
import com.mail.dinesh.mailapplication.utils.SharedPrefs;
import com.mail.dinesh.mailapplication.utils.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import io.realm.Realm;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks{

    public  static  final  String TAG = MainActivity.class.getSimpleName();
    MainActivity activity;
    GoogleAccountCredential mCredential;
    GmailHelper apiQuery;
    private LinearLayout login_button;
    ProgressBar progressBarLoadActivity;
    private WeakHandler mHandler;

    List<List> threads;
    boolean serviceRuning;

    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;

    Intent intent = null;

    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES =
            { GmailScopes.MAIL_GOOGLE_COM,
            GmailScopes.GMAIL_MODIFY,
            GmailScopes.GMAIL_READONLY };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        initListeners();
        setFilters();
        mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());
    }



    private void init() {

        activity = MainActivity.this;
        mHandler = new WeakHandler();
        login_button = (LinearLayout) findViewById(R.id.login_button);
        progressBarLoadActivity = (ProgressBar) findViewById(R.id.progressBarLoadActivity);
        intent = new Intent(this, UpdateMailService.class);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //myRealm.close();
    }

    private void initListeners() {
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("MainActivity","buttonpressed");
                getResultsFromApi();
            }
        });
    }

    private void setFilters() {
        IntentFilter filter = new IntentFilter();
/*        filter.addAction(Configuration.BROADCAST_SERVICE_ON);
        filter.addAction(Configuration.BROADCAST_SERVICE_OFF);
        registerReceiver(threadInfoReceiver, filter);*/
    }

    /**
     * Attempt to call the API, after verifying that all the preconditions are
     * satisfied. The preconditions are: Google Play Services installed, an
     * account was selected and the device currently has online access. If any
     * of the preconditions are not satisfied, the app will prompt the user as
     * appropriate.
     */
    public void getResultsFromApi() {
        if (! isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        } else if (mCredential.getSelectedAccountName() == null) {
            chooseAccount();
        } else if (! isDeviceOnline()) {
            Toast.makeText(getApplicationContext(),"No network connection available.",Toast.LENGTH_SHORT).show();
        } else {
            new MakeRequestTask(mCredential).execute();
        }
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
                mCredential.setSelectedAccountName(accountName);
                getResultsFromApi();
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
                    getResultsFromApi();
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        SharedPreferences settings =
                                activity.getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                        mCredential.setSelectedAccountName(accountName);
                        getResultsFromApi();
                    }
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    getResultsFromApi();
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
                MainActivity.this,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

    /**
     * An asynchronous task that handles the Gmail API call.
     * Placing the API calls in their own task ensures the UI stays responsive.
     */
    private class MakeRequestTask extends AsyncTask<Void, Void, List<String>> {
        private com.google.api.services.gmail.Gmail mService = null;
        private Exception mLastError = null;

        public MakeRequestTask(GoogleAccountCredential credential) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.gmail.Gmail.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName(Constants.APP_NAME)
                    .build();
        }

        /**
         * Background task to call Gmail API.
         * @param params no parameters needed for this task.
         */
        @Override
        protected List<String> doInBackground(Void... params) {
            try {
                Realm myRealm = Realm.getDefaultInstance();
                List<String> dataObtained = new ArrayList<>();
                apiQuery = new GmailHelper();
                if(!SharedPrefs.getPrefBooolean(Configuration.SERVICE_ON,false,getApplicationContext())) {
                    Log.e(TAG,"servcie started");
                    startService(intent);
                } else {
                    Log.e(TAG,"service running");
                }
                threads = apiQuery.listThreadsMatchingQuery(mService, Constants.USER, myRealm);
                return  dataObtained;
            } catch (Exception e) {
                mLastError = e;
                e.printStackTrace();
                cancel(true);
                return null;
            }
        }

        @Override
        protected void onPreExecute() {
            progressBarLoadActivity.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(List<String> output) {
            progressBarLoadActivity.setVisibility(View.INVISIBLE);
            if(threads!=null) {
                if(Util.isNetworkAvailable(getApplicationContext(),activity)) {
                    Intent intent = new Intent(MainActivity.this, MailsActivity.class);
                    //intent.putExtra(Configuration.INTENT_MAIN_TO_MAILS, (Parcelable) threads);
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), Configuration.INTERNET_ERROR_CODE,
                            Toast.LENGTH_SHORT).show();
                }
                if(threads.size()>0) {
                    Toast.makeText(getApplicationContext(),threads.size() +"threads",Toast.LENGTH_SHORT).show();
                    Log.d("Activity","in loop");
                }
            }
        }

        @Override
        protected void onCancelled() {
            progressBarLoadActivity.setVisibility(View.INVISIBLE);
            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());
                } else if (mLastError instanceof UserRecoverableAuthIOException) {
                    startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            MainActivity.REQUEST_AUTHORIZATION);
                } else {
                    Toast.makeText(getApplicationContext(),"The following error occurred:\n"
                            + mLastError.getMessage(),Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getApplicationContext(),"Request cancelled.",Toast.LENGTH_SHORT).show();
            }
        }
    }


   /* private final BroadcastReceiver threadInfoReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(Configuration.BROADCAST_SERVICE_OFF)) {
                Bundle bundle = intent.getExtras();
                if(null!=bundle) {
                    if(bundle.get(Configuration.BROADCAST_SERVICE_OFF)!=null) {
                        if(!bundle.getString(Configuration.BROADCAST_SERVICE_OFF).equals("")) {
                            serviceRuning = false;
                        }
                    } else if(bundle.get(Configuration.BROADCAST_SERVICE_ON)!=null) {
                        if(!bundle.getString(Configuration.BROADCAST_SERVICE_ON).equals("")) {
                            serviceRuning = true;
                        }
                    }
                }
            }
        }
    };*/
}

/*for(List<Thread> thread : threads) {
                        if(thread!=null) {
                            if(thread.size()>0) {
                                for(List<DirtyMail> mails : thread) {

                                }
                            }
                        }
                    }*/

    /*if (output == null || output.size() == 0) {
                mOutputText.setText("No results returned.");
            } else {
                output.add(0, "Data retrieved using the Gmail API:");
                mOutputText.setText(TextUtils.join("\n", output));
            }*/

/**
 * Fetch a list of Gmail labels attached to the specified account.
 * @return List of messages
 * @throws IOException
 */
        /*private List<String> getMsgListFromApi() throws IOException {
            // Get the labels in the user's account.
            String user = "me";
            String query = "is:unread";
            List<String> labels = new ArrayList<String>();
            ListMessagesResponse response = mService.users().messages().list(user).setQ(query).execute();
            List<Message> messages = new ArrayList<Message>();
            while (response.getMessages() != null) {
                messages.addAll(response.getMessages());
                if (response.getNextPageToken() != null) {
                    String pageToken = response.getNextPageToken();
                    response = mService.users().messages().list(user).setQ(query)
                            .setPageToken(pageToken).execute();
                } else {
                    break;
                }
            }

            for (Message message : messages) {
                labels.add(message.toPrettyString());
                //System.out.println(message.toPrettyString());
            }


            return labels;
        }*/

/**
 * Fetch a list of Gmail labels attached to the specified account.
 * @return List of Strings labels.
 * @throws IOException
 */
        /*private List<String> getDataFromApi() throws IOException {
            // Get the labels in the user's account.
            String user = "me";
            List<String> labels = new ArrayList<String>();
            ListLabelsResponse listResponse =
                    mService.users().labels().list(user).execute();
            for (Label label : listResponse.getLabels()) {
                labels.add(label.getName());
            }
            return labels;
        }*/

/*LinearLayout activityLayout = new LinearLayout(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        activityLayout.setLayoutParams(lp);
        activityLayout.setOrientation(LinearLayout.VERTICAL);
        activityLayout.setPadding(16, 16, 16, 16);

        ViewGroup.LayoutParams tlp = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        mCallApiButton = new Button(this);
        mCallApiButton.setText(BUTTON_TEXT);
        mCallApiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallApiButton.setEnabled(false);
                mOutputText.setText("");
                getResultsFromApi();
                mCallApiButton.setEnabled(true);
            }
        });
        activityLayout.addView(mCallApiButton);

        mOutputText = new TextView(this);
        mOutputText.setLayoutParams(tlp);
        mOutputText.setPadding(16, 16, 16, 16);
        mOutputText.setVerticalScrollBarEnabled(true);
        mOutputText.setMovementMethod(new ScrollingMovementMethod());
        mOutputText.setText(
                "Click the \'" + BUTTON_TEXT +"\' button to test the API.");
        activityLayout.addView(mOutputText);

        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Calling Gmail API ...");*/

//setContentView(activityLayout);

// Initialize credentials and service object.

//private TextView mOutputText;
//private Button mCallApiButton;
//ProgressDialog mProgress;
//private static final String BUTTON_TEXT = "Call Gmail API";