package com.example.wifidirect;

import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.util.Log;
import android.widget.Toast;

import com.example.wifidirect.activities.MainActivity;
import com.example.wifidirect.controller.MainActivityController;
import com.example.wifidirect.ui.MyAdapter;

public class BroadcastReceiver extends android.content.BroadcastReceiver {
    MainActivityController mMainActivityController;
    String TAG = "Wifidirect: BroadcastReceiver: ";
    MyAdapter mAdapter;
    WifiP2pManager manager;
    Channel channel;
    PeerListListener peerlistListener;

    MainActivity activity;

    public BroadcastReceiver(WifiP2pManager manager, Channel channel, Context context, MyAdapter mAdapter, WifiP2pManager.PeerListListener peerlistListener) {
        mMainActivityController = MainActivityController.getSC();
        this.mAdapter = mAdapter;
        this.manager = manager;
        this.channel = channel;
        this.peerlistListener = peerlistListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            Log.d(TAG, "WIFI_P2P_STATE_CHANGED_ACTION");

            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {

            }
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            Log.d(TAG, "WIFI_P2P_PEERS_CHANGED_ACTION");

            if (manager != null) {
                manager.requestPeers(channel, peerlistListener);
            }
            Toast.makeText(context, "Wifi peer list has changed", Toast.LENGTH_SHORT).show();

        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            Log.d(TAG, "WIFI_P2P_CONNECTION_CHANGED_ACTION");

            Toast.makeText(context, "Connection state has changed", Toast.LENGTH_SHORT).show();
            if(manager==null) {
                return;
            }
                NetworkInfo networkInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

                if(networkInfo.isConnected()){
                    manager.requestConnectionInfo(channel, mMainActivityController.connectionInfoListener);
                    Toast.makeText(context, "device connected", Toast.LENGTH_LONG).show();

                }else{
                            Toast.makeText(context, "device disconnected", Toast.LENGTH_LONG).show();
                }

        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            Log.d(TAG, "WIFI_P2P_THIS_DEVICE_CHANGED_ACTION");

            Toast.makeText(context, "This device changed action", Toast.LENGTH_SHORT).show();
        }
    }
}
