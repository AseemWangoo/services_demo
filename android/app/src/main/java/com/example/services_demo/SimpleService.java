package com.example.services_demo;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class SimpleService extends Service {

    final static String MY_ACTION = "MY_ACTION";

    int _currentValue = 0;

    public SimpleService() {
    }

    public static String helloFromService() {
        return "Hello from Service";
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.

        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub

        MyThread myThread = new MyThread();
        myThread.start();

        return super.onStartCommand(intent, flags, startId);
    }

    public int getValue() {
        return _currentValue;
    }

    public class MyThread extends Thread {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            for (int i = 0; i < 1; i++) {
                try {
                    Thread.sleep(500);
                    Intent intent = new Intent();
                    intent.setAction(MY_ACTION);

                    intent.putExtra("DATAPASSED", i);
                    _currentValue = i;

                    sendBroadcast(intent);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            stopSelf();
        }

    }
}
