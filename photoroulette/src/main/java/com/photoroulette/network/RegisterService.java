package com.photoroulette.network;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import com.photoroulette.R;

import java.io.IOException;
import java.net.ServerSocket;

public class RegisterService extends ActionBarActivity {

    private NsdManager.RegistrationListener mRegistrationListener;
    private NsdManager mNsdManager;
    private String mServiceName;
    private Context mContext;
    private int mLocalPort;
    private ServerSocket mServerSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_service);
        initializeRegistrationListener();
        initializeServerSocket();
        registerService(mLocalPort,this);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.register, menu);
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
    public void registerService(int port, Context context) {
        // Create the NsdServiceInfo object, and populate it.
        NsdServiceInfo serviceInfo  = new NsdServiceInfo();

        // The name is subject to change based on conflicts
        // with other services advertised on the same network.
        serviceInfo.setServiceName("PhotoRoulette");
        serviceInfo.setServiceType("_http._tcp.");
        serviceInfo.setPort(port);
        mNsdManager = (NsdManager) context.getSystemService(Context.NSD_SERVICE);

        mNsdManager.registerService(
                serviceInfo, NsdManager.PROTOCOL_DNS_SD, mRegistrationListener);

    }

    @TargetApi(18)
    public void initializeRegistrationListener() {
        mRegistrationListener = new NsdManager.RegistrationListener() {

            @Override
            public void onServiceRegistered(NsdServiceInfo NsdServiceInfo) {
                // Save the service name.  Android may have changed it in order to
                // resolve a conflict, so update the name you initially requested
                // with the name Android actually used.
                mServiceName = NsdServiceInfo.getServiceName();

            }

            @Override
            public void onRegistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
                // Registration failed!  Put debugging code here to determine why.
            }

            @Override
            public void onServiceUnregistered(NsdServiceInfo arg0) {
                // Service has been unregistered.  This only happens when you call
                // NsdManager.unregisterService() and pass in this listener.
            }

            @Override
            public void onUnregistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
                // Unregistration failed.  Put debugging code here to determine why.
            }
        };
    }

    public void initializeServerSocket() {
        // Initialize a server socket on the next available port.
        try{
            mServerSocket = new ServerSocket(0);
        } catch (IOException e){}

        // Store the chosen port.
        mLocalPort =  mServerSocket.getLocalPort();

    }

}
