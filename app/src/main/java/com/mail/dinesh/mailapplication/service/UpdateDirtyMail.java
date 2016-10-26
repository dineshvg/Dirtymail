package com.mail.dinesh.mailapplication.service;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.mail.dinesh.mailapplication.activity.MailsActivity;
import com.mail.dinesh.mailapplication.activity.MainActivity;
import com.mail.dinesh.mailapplication.conf.Configuration;
import com.mail.dinesh.mailapplication.conf.Constants;
import com.mail.dinesh.mailapplication.googleUtils.GmailHelper;
import com.mail.dinesh.mailapplication.utils.Util;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by dinesh on 26.10.16.
 */

public class UpdateDirtyMail /*implements EasyPermissions.PermissionCallbacks*/ {

    static final String TAG = "UpdateDirtyMail";
    static Context activity;
    static GmailHelper apiQuery;
    GoogleAccountCredential mCredential;

    /*public void getResultsFromApi() {
        if (!isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        } else if (mCredential.getSelectedAccountName() == null) {
            chooseAccount();
        } else if (!isDeviceOnline()) {
            Log.e(TAG, "offline");
        } else {
            UpdateDirtyMail.gMailToDirtyMail(mCredential, activity);
        }
    }*/

    public static void gMailToDirtyMail(GoogleAccountCredential mCredential, Context mactivity) {
        Log.e(TAG,"service runing");
        activity = mactivity;
        new UpdateDirtyMail.MakeRequestTask(mCredential).execute();
    }

    public static class MakeRequestTask extends AsyncTask<Void, Void, List<String>> {

        com.google.api.services.gmail.Gmail mService = null;
        Exception mLastError = null;

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
         *
         * @param params no parameters needed for this task.
         */
        @Override
        protected List<String> doInBackground(Void... params) {
            try {
                Realm myRealm = Realm.getDefaultInstance();
                List<String> dataObtained = new ArrayList<>();
                apiQuery = new GmailHelper();
                apiQuery.listThreadsMatchingQuery(mService, Constants.USER, myRealm);
                return dataObtained;
            } catch (Exception e) {
                mLastError = e;
                e.printStackTrace();
                cancel(true);
                return null;
            }
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onPostExecute(List<String> output) {
            /*if (Util.isNetworkAvailable(activity.getApplicationContext(), (Activity) activity)) {
            }*/
        }

        @Override
        protected void onCancelled() {
            Log.d(TAG,"cancelled");
        }

        //if (mLastError != null) {
                /*if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());
                }*/ /*else if (mLastError instanceof UserRecoverableAuthIOException) {
                    activity.startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            Constants.REQUEST_AUTHORIZATION);
                } else {
                    Toast.makeText(activity.getApplicationContext(),"The following error occurred:\n"
                            + mLastError.getMessage(),Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(activity.getApplicationContext(),"Request cancelled.",Toast.LENGTH_SHORT).show();
            }*/
        //}

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
        //@AfterPermissionGranted(Constants.REQUEST_PERMISSION_GET_ACCOUNTS)
    /*private void chooseAccount() {
        if (EasyPermissions.hasPermissions(
                activity, Manifest.permission.GET_ACCOUNTS)) {
            String accountName = activity.getPreferences(Context.MODE_PRIVATE)
                    .getString(Constants.PREF_ACCOUNT_NAME, null);
            if (accountName != null) {
                mCredential.setSelectedAccountName(accountName);
                getResultsFromApi();
            } else {
                // Start a dialog from which the user can choose an account
                activity.startActivityForResult(
                        mCredential.newChooseAccountIntent(),
                        Constants.REQUEST_ACCOUNT_PICKER);
            }
        } else {
            // Request the GET_ACCOUNTS permission via a user dialog
            EasyPermissions.requestPermissions(
                    this,
                    "This app needs to access your Google account (via Contacts).",
                    Constants.REQUEST_PERMISSION_GET_ACCOUNTS,
                    Manifest.permission.GET_ACCOUNTS);
        }
    }*/

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
        // @Override
    /*protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case Constants.REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != Constants.RESULT_OK) {
                    Toast.makeText(activity.getApplicationContext(),
                            "This app requires Google Play Services. Please install \" +\n" +
                                    "                                    \"Google Play Services on your device and relaunch this app.",
                            Toast.LENGTH_LONG).show();
                } else {
                    getResultsFromApi();
                }
                break;
            case Constants.REQUEST_ACCOUNT_PICKER:
                if (resultCode == Constants.RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        SharedPrefs settings =
                                activity.getPreferences(Context.MODE_PRIVATE);
                        SharedPrefs.Editor editor = settings.edit();
                        editor.putString(Constants.PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                        mCredential.setSelectedAccountName(accountName);
                        activity.getResultsFromApi();
                    }
                }
                break;
            case Constants.REQUEST_AUTHORIZATION:
                if (resultCode == Constants.RESULT_OK) {
                    getResultsFromApi();
                }
                break;
        }
    }*/

        /**
         * Respond to requests for permissions at runtime for API 23 and above.
         * @param requestCode The request code passed in
         *     requestPermissions(android.app.Activity, String, int, String[])
         * @param permissions The requested permissions. Never null.
         * @param grantResults The grant results for the corresponding permissions
         *     which is either PERMISSION_GRANTED or PERMISSION_DENIED. Never null.
         */
    /*@Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        EasyPermissions.onRequestPermissionsResult(
                requestCode, permissions, grantResults, this);
    }*/

        /**
         * Callback for when a permission is granted using the EasyPermissions
         * library.
         * @param requestCode The request code associated with the requested
         *         permission
         * @param list The requested permission list. Never null.
         */
    /*@Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        // Do nothing.
    }*/

        /**
         * Callback for when a permission is denied using the EasyPermissions
         * library.
         * @param requestCode The request code associated with the requested
         *         permission
         * @param list The requested permission list. Never null.
         */
    /*@Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        // Do nothing.
    }*/

        /**
         * Checks whether the device currently has a network connection.
         *
         * @return true if the device has a network connection, false otherwise.
         */
        private boolean isDeviceOnline() {
            ConnectivityManager connMgr =
                    (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            return (networkInfo != null && networkInfo.isConnected());
        }

        /**
         * Check that Google Play services APK is installed and up to date.
         *
         * @return true if Google Play Services is available and up to
         * date on this device; false otherwise.
         */
        private boolean isGooglePlayServicesAvailable() {
            GoogleApiAvailability apiAvailability =
                    GoogleApiAvailability.getInstance();
            final int connectionStatusCode =
                    apiAvailability.isGooglePlayServicesAvailable(activity);
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
                    apiAvailability.isGooglePlayServicesAvailable(activity);
            /*if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
                //showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
            }*/
        }
    }
}


    /**
     * Display an error dialog showing that Google Play Services is missing
     * or out of date.
     * @param connectionStatusCode code describing the presence (or lack of)
     *     Google Play Services on this device.
     */
    /*static void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        *//*Dialog dialog = apiAvailability.getErrorDialog(
                activity,
                connectionStatusCode,
                Constants.REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();*//*
    }
}*/
