/*
 * Copyright 2018.  Osaigbovo Odiase
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.osaigbovo.udacity.popularmovies.ui.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;

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

        // TODO 1 - BUG java.lang.IllegalArgumentException: Receiver not registered:
        // LocalBroadcastManager.getInstance().un
        /*if (checkConnectionBroadcastReceiver != null) {
            this.unregisterReceiver(checkConnectionBroadcastReceiver);
        }*/
    }

    private void showMessage(Boolean isConnected) {

        if (!isConnected) {
            String messageToUser = "No Internet Connection";

            //Assume "rootLayout" as the root layout of every activity.
            mSnackBar = Snackbar
                    .make(findViewById(R.id.coordinator), messageToUser, Snackbar.LENGTH_LONG)
                    .setAction("Dismiss", view -> mSnackBar.dismiss());

            mSnackBar.setDuration(Snackbar.LENGTH_INDEFINITE);
            // Changing message text color
            //snackbar.setActionTextColor(Color.WHITE);
            mSnackBar.show();
        } else {
            String messageToUser = "You are online now.";
            mSnackBar = Snackbar
                    .make(findViewById(R.id.coordinator), messageToUser, Snackbar.LENGTH_SHORT);
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