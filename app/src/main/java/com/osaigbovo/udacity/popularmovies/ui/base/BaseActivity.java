package com.osaigbovo.udacity.popularmovies.ui.base;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.osaigbovo.udacity.popularmovies.R;
import com.osaigbovo.udacity.popularmovies.data.receiver.CheckConnectionBroadcastReceiver;

public class BaseActivity extends AppCompatActivity
        implements CheckConnectionBroadcastReceiver.CheckConnectionListener {

    private Snackbar mSnackBar;

    private CheckConnectionBroadcastReceiver checkConnectionBroadcastReceiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //checkConnectionBroadcastReceiver = new CheckConnectionBroadcastReceiver();
    }

    @Override
    protected void onResume() {
        super.onResume();

        /*if(true){
            return;
        }else{

        }*/
        /*CheckConnectionBroadcastReceiver.checkConnectionListener = this;
        this.registerReceiver(checkConnectionBroadcastReceiver,
                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));*/
    }

    @Override
    protected void onPause() {
        super.onPause();

        // BUG java.lang.IllegalArgumentException: Receiver not registered:
        // LocalBroadcastManager.getInstance().un
        /*if (checkConnectionBroadcastReceiver != null) {
            this.unregisterReceiver(checkConnectionBroadcastReceiver);
        }*/
    }

    public void showMessage(Boolean isConnected) {

        if (!isConnected) {
            String messageToUser = "No Internet Connection";

            //Assume "rootLayout" as the root layout of every activity.
            mSnackBar = Snackbar
                    .make(findViewById(R.id.coordinator), messageToUser, Snackbar.LENGTH_LONG)
                    .setAction("Dismiss", view -> mSnackBar.dismiss());

            mSnackBar.setDuration(Snackbar.LENGTH_INDEFINITE);
            // Changing message text color
            mSnackBar.setActionTextColor(Color.RED);
            mSnackBar.show();
        } else {
            String messageToUser = "You are online now.";
            mSnackBar = Snackbar
                    .make(findViewById(R.id.coordinator), messageToUser, Snackbar.LENGTH_SHORT);
            mSnackBar.setDuration(Snackbar.LENGTH_SHORT);
            mSnackBar.setActionTextColor(Color.WHITE);
            mSnackBar.show();
            //mSnackBar.dismiss();
        }
    }

    @Override
    public void onNetworkConnectionChanged(Boolean isConnected) {
        showMessage(isConnected);
    }
}
