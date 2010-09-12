package com.radio.freshlife;

import java.io.IOException;


import android.R.string;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.net.Uri;
import android.os.DeadObjectException;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

public class FLRService extends Service {

    private MediaPlayer mp = new MediaPlayer();
    private boolean _isPlaying = false;
    private NotificationManager mNotificationManager; 
    private int NOTIFICATION_ID = 21;
    private Notification notification;
    private PendingIntent contentIntent;
    private Context context;

    public static OnBufferingUpdateListener _radioBufferingListener;

//    public OnBufferingUpdateListener getBufferingListener()
//    {
//    	return _radioBufferingListener;
//    }
    public static void setBufferingListener(OnBufferingUpdateListener radioBufferingListener)
    {
    	if(_radioBufferingListener != radioBufferingListener)
    	{
    		_radioBufferingListener = radioBufferingListener;
    	}
    }
    
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		mp = new MediaPlayer();

		// Get the notification manager service. 
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE); 
        long when = System.currentTimeMillis();
        
        context = getApplicationContext();
        Intent notificationIntent = new Intent(context, RadioMain.class);
        contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

		notification = new Notification(R.drawable.status_icon, "Welcome to Fresh Life Radio", when);

		notification.icon |= R.drawable.status_icon;
		notification.defaults |= Notification.FLAG_ONGOING_EVENT;
		notification.defaults |= Notification.DEFAULT_VIBRATE;
		notification.flags |= Notification.FLAG_ONGOING_EVENT;
		notification.setLatestEventInfo(context, context.getString(R.string.app_name), context.getString(R.string.notification_paused), contentIntent);
        
		mNotificationManager.notify(NOTIFICATION_ID, notification);
        

        /* Clear the notification. */  
		return mBinder;
		
	}

	private final FLRInterface.Stub mBinder = new FLRInterface.Stub() {
		
		public void stop() throws DeadObjectException {
			mp.stop();
			notification.setLatestEventInfo(context, context.getString(R.string.app_name), context.getString(R.string.notification_paused), contentIntent);
			mNotificationManager.notify(NOTIFICATION_ID, notification);

			_isPlaying = false;
		}
		
		public void play() throws RemoteException {
	    	try { 
	    		mp.reset();
	    		mp.setOnBufferingUpdateListener(_radioBufferingListener);
				mp.setDataSource(getString(R.string.stream_url));
				mp.setAudioStreamType(AudioManager.STREAM_MUSIC);

				notification.setLatestEventInfo(context, context.getString(R.string.app_name), context.getString(R.string.notification_buffering), contentIntent);
				mNotificationManager.notify(NOTIFICATION_ID, notification);
				mp.prepare();
				mp.start();
				notification.setLatestEventInfo(context, context.getString(R.string.app_name), context.getString(R.string.notification_playing), contentIntent);
				mNotificationManager.notify(NOTIFICATION_ID, notification);
				

				_isPlaying = true;

				
	    	} catch (IOException e) {
		    	//Log.e(getClass().getName(), "Error starting to stream audio.", e);  
		    	_isPlaying = false;
	    	}
			
		}
		
		public boolean isPlaying() throws RemoteException {

			return _isPlaying;
		}
	};
	
	@Override
    public void onDestroy() {
        // Cancel the persistent notification.
		mNotificationManager.cancel(NOTIFICATION_ID);
	}
      
}
