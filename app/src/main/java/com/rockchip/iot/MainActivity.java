package com.rockchip.iot;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "IotNfcControl";
    private EditText mEditSSID;
    private EditText mEditPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mEditSSID = findViewById(R.id.tvSSID);
        mEditPassword = findViewById(R.id.tvPwd);

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkRequest.Builder request = new NetworkRequest.Builder()
                    .addTransportType(NetworkCapabilities.TRANSPORT_WIFI);
            cm.registerNetworkCallback(request.build(), mWifiCallback);
        }

        setHint(getIntent());
    }

    @Override
    protected void onStop() {
        super.onStop();
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            cm.unregisterNetworkCallback(mWifiCallback);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d(TAG, "--------------new intent-------------");
        setHint(intent);
    }

    private void setHint(Intent intent) {
        Uri uri = intent.getData();
        if (uri != null) {
            String ssid = uri.getQueryParameter("i");
            String key = uri.getQueryParameter("k");
            mEditSSID.setText(ssid);
            mEditPassword.setText(key);
            setWifi(ssid, key);
        }
    }

    private void setWifi(String ssid, String pwd) {
        Toast.makeText(this, "Trying connect wifi ...", Toast.LENGTH_LONG).show();
        WifiConfiguration wifiConfig = new WifiConfiguration();
        wifiConfig.SSID = String.format("\"%s\"", ssid);
        wifiConfig.preSharedKey = String.format("\"%s\"", pwd);

        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        int netId = wifiManager.addNetwork(wifiConfig);
        wifiManager.disconnect();
        wifiManager.enableNetwork(netId, true);
        wifiManager.reconnect();

    }

    private ConnectivityManager.NetworkCallback mWifiCallback = new ConnectivityManager.NetworkCallback() {
        @Override
        public void onAvailable(Network network) {
            super.onAvailable(network);
            Toast.makeText(getApplicationContext(), "Wifi available .", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCapabilitiesChanged(Network network, NetworkCapabilities networkCapabilities) {
            super.onCapabilitiesChanged(network, networkCapabilities);
            Toast.makeText(getApplicationContext(), "Wifi changed .", Toast.LENGTH_SHORT).show();
        }
    };

}
