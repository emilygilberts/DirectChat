package com.example.wifidirect;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wifidirect.activities.MainActivity;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;

public class MainActivityController {

    private static MainActivityController mMainActivityController;

    private Context context;
    private MainActivity mainActivity;

    public ArrayList<WifiP2pDevice> peers;

    public String TAG = "MainActivityController: ";

    private MainActivityController(){

        peers = new ArrayList<>();
    }

    public static MainActivityController getSC(){
        if(MainActivityController.mMainActivityController == null){
            MainActivityController.mMainActivityController = new MainActivityController();
        }
        return MainActivityController.mMainActivityController;
    }

    public void setMainActivity(MainActivity mainActivity){
        this.mainActivity = mainActivity;
        context = mainActivity.getApplicationContext();
    }

    public void turnOnWifi(){
        WifiManager mWifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (!mWifiManager.isWifiEnabled()) mWifiManager.setWifiEnabled(true);
        else {
            Toast.makeText(context, "please turn on wifi", Toast.LENGTH_LONG).show();
        }
    }

    public void startSearch(WifiP2pManager.Channel channel, WifiP2pManager manager){
        manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                // Code for when the discovery initiation is successful goes here.

                Log.d(TAG, "startSearch - onSuccess");
            }

            @Override
            public void onFailure(int reason) {
                // Code for when the discovery initiation fails goes here.

                Log.d(TAG, "startSearch - onFailure");

            }
        });
    }

    public WifiP2pManager.PeerListListener peerListListener = new WifiP2pManager.PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList peerList) {

            Collection<WifiP2pDevice> refreshedPeers = peerList.getDeviceList();
            if (!refreshedPeers.equals(MainActivityController.this.peers)) {
                MainActivityController.this.peers.clear();
                MainActivityController.this.peers.addAll(refreshedPeers);

                // If an AdapterView is backed by this data, notify it
                // of the change. For instance, if you have a ListView of
                // available peers, trigger an update.
                //((WiFiPeerListAdapter) getListAdapter()).notifyDataSetChanged();

                // Perform any other updates needed based on the new list of
                // peers connected to the Wi-Fi P2P network.
                for(WifiP2pDevice peer : peers){
                    Log.d(TAG, peer.deviceName);
                }
            }
            if (MainActivityController.this.peers.size() == 0) {
                //Log.d(WiFiDirectActivity.TAG, "No devices found");
                Toast.makeText(context, "no devices found", Toast.LENGTH_LONG).show();
            }
        }
    };

    public void connectToPeer(int itemPosition, WifiP2pManager manager, WifiP2pManager.Channel channel){
        WifiP2pDevice peer = peers.get(itemPosition);
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = peer.deviceAddress;
        config.wps.setup = WpsInfo.PBC;

        manager.connect(channel, config, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                // WiFiDirectBroadcastReceiver notifies us. Ignore for now.
                Log.d(TAG, "connected succesfully");
                Toast.makeText(context, "connected succesfully",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int reason) {
                Toast.makeText(context, "Connect failed. Retry.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    WifiP2pManager.ConnectionInfoListener connectionInfoListener = new WifiP2pManager.ConnectionInfoListener(){
            @Override
            public void onConnectionInfoAvailable(WifiP2pInfo p2PInfo){
                final InetAddress griupOwnerAddress = p2PInfo.groupOwnerAddress;

                if(p2PInfo.groupFormed && p2PInfo.isGroupOwner){
                    mainActivity.p2pInfoText.setText("Host");
                }else if(p2PInfo.groupFormed){
                    mainActivity.p2pInfoText.setText("Client");
                }

            }
    };

    public String[] getPeerList(){

        String[] peerNames = new String[peers.size()];
        for (int i = 0; i < peers.size(); i++) {
            peerNames[i] = peers.get(i).deviceName;
        }
        return peerNames;
    }

}
