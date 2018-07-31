package com.osaigbovo.udacity.popularmovies.ui.base;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;

import com.osaigbovo.udacity.popularmovies.R;
import com.osaigbovo.udacity.popularmovies.data.receiver.CheckConnectionBroadcastReceiver;

public class BaseActivity extends AppCompatActivity
        implements CheckConnectionBroadcastReceiver.CheckConnectionListener {

    private Snackbar mSnackBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.registerReceiver(new CheckConnectionBroadcastReceiver(),
                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    protected void onResume() {
        super.onResume();
        CheckConnectionBroadcastReceiver.checkConnectionListener = this;
    }

    private void showMessage(Boolean isConnected) {

        if (!isConnected) {
            String messageToUser = "No Internet Connection";

            //Assume "rootLayout" as the root layout of every activity.
            mSnackBar = Snackbar
                    .make(findViewById(R.id.frameLayout), messageToUser, Snackbar.LENGTH_LONG)
                    .setAction("Dismiss", view -> mSnackBar.dismiss());

            mSnackBar.setDuration(Snackbar.LENGTH_INDEFINITE);
            // Changing message text color
            //snackbar.setActionTextColor(Color.WHITE);
            mSnackBar.show();
        } else {
            String messageToUser = "You are online now.";
            mSnackBar = Snackbar
                    .make(findViewById(R.id.frameLayout), messageToUser, Snackbar.LENGTH_SHORT);
            mSnackBar.setDuration(Snackbar.LENGTH_SHORT);
            mSnackBar.show();
            //mSnackBar.dismiss();
        }
    }

    @Override
    public void onNetworkConnectionChanged(Boolean isConnected) {
        showMessage(isConnected);
    }
}
