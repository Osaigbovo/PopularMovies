package com.osaigbovo.udacity.popularmovies.data.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

public class CheckConnectionBroadcastReceiver extends BroadcastReceiver {

    @Nullable
    public static CheckConnectionListener checkConnectionListener;

    @Override
    public void onReceive(@NotNull Context context, @NotNull Intent intent) {

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
