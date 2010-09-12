package com.radio.freshlife;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.Window;

public class Splash extends Activity {
    /** Called when the activity is first created. */
    protected boolean _active = true;
    protected int _splashTime = 1000; // time to display the splash screen in ms
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.splash);
        final Context c = this;
        // thread for displaying the SplashScreen
        Thread splashThread = new Thread() {
            @Override
            public void run() {
                try {
                        sleep(_splashTime);
                } catch(InterruptedException e) {
                    // do nothing
                } finally {
                    finish();
                    Intent i = new Intent(c,RadioMain.class);
                    startActivity(i);
                    stop();
                }
            }
        };
        splashThread.start();
    }
    
    	
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            _active = false;
        }
        return true;
    }
}