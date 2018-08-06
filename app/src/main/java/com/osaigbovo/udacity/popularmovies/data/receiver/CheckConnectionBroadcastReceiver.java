package com.osaigbovo.udacity.popularmovies.data.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;


public class CheckConnectionBroadcastReceiver extends BroadcastReceiver {

    @Nullable
    public static CheckConnectionListener checkConnectionListener;

    @Override
    public void onReceive(@NonNull Context context, @NonNull Intent intent) {

        if (checkConnectionListener != null) {
            checkConnectionListener.onNetworkConnectionChanged(isConnectedOrConnecting(context));
        }
    }

    private boolean isConnectedOrConnecting(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    public interface CheckConnectionListener {
        public void onNetworkConnectionChanged(Boolean isConnected);
    }
}
