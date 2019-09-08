package com.example.services_demo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import io.flutter.app.FlutterActivity;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.PluginRegistry;
import io.flutter.plugins.GeneratedPluginRegistrant;
import io.flutter.plugins.androidalarmmanager.AlarmService;

public class MainActivity extends FlutterActivity implements MethodChannel.MethodCallHandler, PluginRegistry.PluginRegistrantCallback {

    static final String TAG = "Main Activity.....";
    static final String CHANNEL = "com.example.services_demo/service";
    MyReceiver myReceiver;
    MethodChannel.Result keepResult = null;

    boolean serviceConnected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AlarmService.setPluginRegistrant(this);
        GeneratedPluginRegistrant.registerWith(this);

        new MethodChannel(getFlutterView(), CHANNEL).setMethodCallHandler(this::onMethodCall);
    }

    @Override
    protected void onStart() {
        myReceiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SimpleService.MY_ACTION);
        registerReceiver(myReceiver, intentFilter);

        super.onStart();
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        if (myReceiver != null) {
            unregisterReceiver(myReceiver);
        }
        serviceConnected = false;
        super.onStop();
    }

    @Override
    public void onMethodCall(MethodCall call, MethodChannel.Result result) {

        try {
            if (call.method.equals("connect")) {
                connectToService();
                keepResult = result;

            } else if (serviceConnected) {

                //
                if (call.method.equals("start")) {
                    String _data = SimpleService.helloFromService();
                    result.success(_data);
                } else if (call.method.equals("latestValue")) {
                    int _data = new SimpleService().getValue();
                    result.success(_data);

//                    Log.i(TAG, "Value received : "+String.valueOf(_data));
                }

            } else {
                result.error(null, "App not connected to service", null);
            }
        } catch (Exception e) {
            result.error(null, e.getMessage(), null);
        }

    }

    private void connectToService() {
        //

        if (!serviceConnected) {

            Intent service = new Intent(this, SimpleService.class);
            startService(service);
            Log.i(TAG, "Service connected");

            serviceConnected = true;

        } else {
            Log.i(TAG, "Service already connected");

            if (keepResult != null) {
                keepResult.success(null);
                keepResult = null;
            }
        }

        //
    }

    @Override
    public void registerWith(PluginRegistry registry) {
        GeneratedPluginRegistrant.registerWith(registry);
    }

    private class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context arg0, Intent arg1) {
            // TODO Auto-generated method stub

            int datapassed = arg1.getIntExtra("DATAPASSED", 0);

            Toast.makeText(MainActivity.this,
                    "Value from service !!\n"
                            + "Data passed: " + String.valueOf(datapassed),
                    Toast.LENGTH_LONG).show();

        }

    }
}
