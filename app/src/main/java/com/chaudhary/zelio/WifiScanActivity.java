package com.chaudhary.zelio;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Path;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.List;

public class WifiScanActivity extends Activity {

    LinearLayout search;
    RelativeLayout oops;

    WifiManager mainWifiObj;
    WifiScanReceiver wifiReciever;
    String wifis[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_scan);
        oops=(RelativeLayout)findViewById(R.id.rl_opps);
        search=(LinearLayout)findViewById(R.id.rl_search);

        mainWifiObj = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiReciever = new WifiScanReceiver();
        mainWifiObj.startScan();

        searchMethod();
    }

    protected void onPause() {
        unregisterReceiver(wifiReciever);
        super.onPause();
    }

    protected void onResume() {
        registerReceiver(wifiReciever, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        super.onResume();
    }

    public void searchAgain(View view) {
        oops.setVisibility(View.GONE);
        searchMethod();
    }


    class WifiScanReceiver extends BroadcastReceiver {
        @SuppressLint("UseValueOf")
        public void onReceive(Context c, Intent intent) {
            List<ScanResult> wifiScanList = mainWifiObj.getScanResults();
            wifis = new String[wifiScanList.size()];
            for(int i = 0; i < wifiScanList.size(); i++){
                wifis[i] = ((wifiScanList.get(i)).toString());
            }
            String filtered[] = new String[wifiScanList.size()];
            int counter = 0;
            for (String eachWifi : wifis) {
                String[] temp = eachWifi.split(",");
                System.out.println("----"+temp);
                filtered[counter] = temp[0].substring(5).trim();
                counter++;
            }
        }
    }


    public void searchMethod()
    {
        search.setVisibility(View.VISIBLE);
        oops.setVisibility(View.GONE);

        View view = findViewById(R.id.imageview);
        Path path = new Path();
        path.addCircle(0, 0, 80, Path.Direction.CW);
        ViewPathAnimator.animate(view, path, 1000/ 60, 5);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                oops.setVisibility(View.VISIBLE);
                search.setVisibility(View.GONE);
            }
        },5000);
    }

    public void allreadyHaveAccount(View view) {
        startActivity(new Intent(this,AllreadyHaveAccount.class));
    }
}
