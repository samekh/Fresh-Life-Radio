package com.radio.freshlife;


import javax.xml.parsers.SAXParserFactory;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.*;


public class RadioMain extends Activity implements
	OnBufferingUpdateListener{

	private static final int MENU_QUIT = 0;
	private ImageButton playButton;
	private boolean isServiceStarted = false;
	private boolean isServiceBound = false;
	private RelativeLayout controlsLayout;
	private ImageView windowBG;
	private FLRInterface flrInterface;
	public void onBufferingUpdate(MediaPlayer mp, int percent) {
		
	}
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.radio_main);
        Object serviceObject = getLastNonConfigurationInstance();
        if(serviceObject != null)
        {
        	flrInterface = (FLRInterface)serviceObject;
        }

    	startSVC();
    	bindSVC();

    }
    
    
    @Override
    public Object onRetainNonConfigurationInstance() {
        final FLRInterface flrService = flrInterface;
        return flrService;
    }
    /* Creates the menu items */
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, MENU_QUIT, 0, this.getString(R.string.menu_quit))
        .setIcon(R.drawable.ic_menu_close_clear_cancel);
        return true;
    }/* Handles item selections */
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case MENU_QUIT:
        	quitApplication();
            return true;
        }
        return false;
    }
    private void initControls() throws RemoteException {
		playButton = (ImageButton) findViewById(R.id.button_play);
		controlsLayout = (RelativeLayout) findViewById(R.id.controlContainer);
		windowBG = (ImageView) findViewById(R.id.window_background);

		playButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				playStream();
        }});
		

		if(flrInterface.isPlaying()){
			playButton.setImageResource(R.drawable.button_pause);
		}
		else
		{
			playButton.setImageResource(R.drawable.button_play);
		}
		
    }
    private ServiceConnection mConnection = new ServiceConnection()
    {
            public void onServiceConnected(ComponentName className, IBinder service) {
            	flrInterface = FLRInterface.Stub.asInterface((IBinder)service);

                try {
        			initControls();
        		} catch (RemoteException e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        		}
            	
            }
     
            public void onServiceDisconnected(ComponentName className) {
            	flrInterface = null;
        		unbindSVC();
            	
            }
    };
    
    private void getAlbumInfo(){
    	SAXParserFactory spf = SAXParserFactory.newInstance();
    	
    	s
    }
    
    private void playStream() {
    	try {

    		if(flrInterface.isPlaying()){
    			flrInterface.stop();
    			playButton.setImageResource(R.drawable.button_play);
    		}
    		else
    		{
    			playButton.setImageResource(R.drawable.button_play_over);
    			flrInterface.play(); 
    			playButton.setImageResource(R.drawable.button_pause);
    		}
    		
		} catch (Exception e) {
			//Log.e(getClass().getName(), "Error starting to stream audio.", e);      
			playButton.setImageResource(R.drawable.button_play);      		
		}
    	   	
    }
    private void quitApplication() {
    	try {
    		if(flrInterface.isPlaying()){
    			flrInterface.stop();
    		}
    		unbindSVC();
    		stopSVC();
    		
		} catch (Exception e) {
           		
		}
		finally
		{
		    super.onDestroy();
			this.finish();
		}
    	   	
    }
    @Override
    protected void onPause() {
        super.onPause();
        unbindSVC();
    }
    @Override 
    protected void onDestroy() {
      super.onDestroy();
	  unbindSVC();
	  mConnection = null;

    }
    

    private void startSVC()
    {
    	if(!isServiceStarted)
    	{
	    	Intent svc = new Intent(RadioMain.this, FLRService.class);
	        this.getApplicationContext().startService(svc);
	        isServiceStarted = true;
    	}
    }
    private void stopSVC()
    {
    	if(isServiceStarted) 
    	{
	    	Intent svc = new Intent(RadioMain.this, FLRService.class);
	        this.getApplicationContext().stopService(svc);
	        isServiceStarted = false;
    	}
    }
    
    private void bindSVC()
    {
    	if(!isServiceBound)
    	{
	    	Intent svc = new Intent(RadioMain.this, FLRService.class);
	        this.getApplicationContext().bindService(svc,
	        		mConnection, Context.BIND_AUTO_CREATE);
	        isServiceBound = true;
    	}
    }
    
    private void unbindSVC()
    {
    	if(isServiceBound)
    	{
	        this.getApplicationContext().unbindService(mConnection);
	        isServiceBound = false;
    	}
    }
    
}
