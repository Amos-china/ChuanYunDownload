package com.chuanyun.downloader.utils;

import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.content.Context;


public class NetworkMonitor {
    private ConnectivityManager connectivityManager;
    private ConnectivityManager.NetworkCallback networkCallback;

    public NetworkMonitor(Context context) {
        connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    public void startNetworkCallback() {
        NetworkRequest networkRequest = new NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build();

        networkCallback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(Network network) {
                super.onAvailable(network);
                // 网络可用
                System.out.println("网络已连接");
            }

            @Override
            public void onLost(Network network) {
                super.onLost(network);
                // 网络丢失
                System.out.println("网络已断开");
            }
        };

        connectivityManager.registerNetworkCallback(networkRequest, networkCallback);
    }

    public void stopNetworkCallback() {
        if (networkCallback != null) {
            connectivityManager.unregisterNetworkCallback(networkCallback);
        }
    }
}
