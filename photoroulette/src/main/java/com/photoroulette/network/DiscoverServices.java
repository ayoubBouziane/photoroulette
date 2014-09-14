package com.photoroulette.network;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import com.photoroulette.R;

public class DiscoverServices extends ActionBarActivity {

    private NsdManager.DiscoveryListener mDiscoveryListener;
    private NsdManager mNsdManager;
    private NsdManager.ResolveListener mResolveListener;
    private String mServiceName = "PhotoRoulette";

    @Override
    @TargetApi(18)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discover_services);

        initializeDiscoveryListener();
        mNsdManager = (NsdManager) this.getSystemService(Context.NSD_SERVICE);
        mNsdManager.discoverServices(
                "_http._tcp.", NsdManager.PROTOCOL_DNS_SD, mDiscoveryListener);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.discover_services, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @TargetApi(18)
    public void initializeDiscoveryListener() {

        // Instantiate a new DiscoveryListener
        mDiscoveryListener = new NsdManager.DiscoveryListener() {

            //  Called as soon as service discovery begins.
            @Override
            public void onDiscoveryStarted(String regType) {
                Log.d("TAG", "Service discovery started");
            }

            @Override
            public void onServiceFound(NsdServiceInfo service) {
                // A service was found!  Do something with it.
                Log.d("TAG", "Service discovery success" + service);
                if (!service.getServiceType().equals("_http._tcp.")) {
                    // Service type is the string containing the protocol and
                    // transport layer for this service.
                    Log.d("TAG", "Unknown Service Type: " + service.getServiceType());
                } else if (service.getServiceName().equals(mServiceName)) {
                    // The name of the service tells the user what they'd be
                    // connecting to. It could be "Bob's Chat App".
                    Log.d("TAG", "Same machine: " + mServiceName);
                } else if (service.getServiceName().contains("NsdChat")){
                    mNsdManager.resolveService(service, mResolveListener);
                }
            }

            @Override
            public void onServiceLost(NsdServiceInfo service) {
                // When the network service is no longer available.
                // Internal bookkeeping code goes here.
                Log.e("TAG", "service lost" + service);
            }

            @Override
            public void onDiscoveryStopped(String serviceType) {
                Log.i("TAG", "Discovery stopped: " + serviceType);
            }

            @Override
            public void onStartDiscoveryFailed(String serviceType, int errorCode) {
                Log.e("TAG", "Discovery failed: Error code:" + errorCode);
                mNsdManager.stopServiceDiscovery(this);
            }

            @Override
            public void onStopDiscoveryFailed(String serviceType, int errorCode) {
                Log.e("TAG", "Discovery failed: Error code:" + errorCode);
                mNsdManager.stopServiceDiscovery(this);
            }
        };
    }
}
