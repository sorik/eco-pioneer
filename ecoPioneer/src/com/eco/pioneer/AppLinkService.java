/**Ford Motor Company
 * September 2012
 * Elizabeth Halash
 */

package com.eco.pioneer;

import java.util.Vector;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.ford.syncV4.exception.SyncException;
import com.ford.syncV4.exception.SyncExceptionCause;
import com.ford.syncV4.proxy.SyncProxyALM;
import com.ford.syncV4.proxy.TTSChunkFactory;
import com.ford.syncV4.proxy.interfaces.IProxyListenerALM;
import com.ford.syncV4.proxy.rpc.AddCommand;
import com.ford.syncV4.proxy.rpc.AddCommandResponse;
import com.ford.syncV4.proxy.rpc.AddSubMenuResponse;
import com.ford.syncV4.proxy.rpc.AlertResponse;
import com.ford.syncV4.proxy.rpc.CreateInteractionChoiceSetResponse;
import com.ford.syncV4.proxy.rpc.DeleteCommandResponse;
import com.ford.syncV4.proxy.rpc.DeleteInteractionChoiceSetResponse;
import com.ford.syncV4.proxy.rpc.DeleteSubMenuResponse;
import com.ford.syncV4.proxy.rpc.EncodedSyncPDataResponse;
import com.ford.syncV4.proxy.rpc.GenericResponse;
import com.ford.syncV4.proxy.rpc.OnButtonEvent;
import com.ford.syncV4.proxy.rpc.OnButtonPress;
import com.ford.syncV4.proxy.rpc.OnCommand;
import com.ford.syncV4.proxy.rpc.OnDriverDistraction;
import com.ford.syncV4.proxy.rpc.OnEncodedSyncPData;
import com.ford.syncV4.proxy.rpc.OnHMIStatus;
import com.ford.syncV4.proxy.rpc.OnPermissionsChange;
import com.ford.syncV4.proxy.rpc.OnTBTClientState;
import com.ford.syncV4.proxy.rpc.PerformInteractionResponse;
import com.ford.syncV4.proxy.rpc.ResetGlobalPropertiesResponse;
import com.ford.syncV4.proxy.rpc.SetGlobalPropertiesResponse;
import com.ford.syncV4.proxy.rpc.SetMediaClockTimerResponse;
import com.ford.syncV4.proxy.rpc.ShowResponse;
import com.ford.syncV4.proxy.rpc.Speak;
import com.ford.syncV4.proxy.rpc.SpeakResponse;
import com.ford.syncV4.proxy.rpc.SubscribeButtonResponse;
import com.ford.syncV4.proxy.rpc.TTSChunk;
import com.ford.syncV4.proxy.rpc.UnsubscribeButtonResponse;
import com.ford.syncV4.proxy.rpc.enums.ButtonName;
import com.ford.syncV4.proxy.rpc.enums.DriverDistractionState;
import com.ford.syncV4.proxy.rpc.enums.SpeechCapabilities;
import com.ford.syncV4.proxy.rpc.enums.TextAlignment;
import com.ford.syncV4.util.DebugTool;

public class AppLinkService extends Service implements IProxyListenerALM{

	String TAG = "pioneer";
	//variable used to increment correlation ID for every request sent to SYNC
	public static int autoIncCorrId = 0;
	//variable to contain the current state of the service
	private static AppLinkService instance = null;
	//variable to contain the current state of the main UI ACtivity
	private MainActivity currentUIActivity;
	//variable to access the BluetoothAdapter
	private BluetoothAdapter mBtAdapter;
	//variable to create and call functions of the SyncProxy
	private SyncProxyALM proxy = null;
	//variable that keeps track of whether SYNC is sending driver distractions
	//(older versions of SYNC will not send this notification)
	private boolean driverdistrationNotif = false;
	//variable to contain the current state of the lockscreen
	private boolean lockscreenUP = false;
	
	public static AppLinkService getInstance() {
		return instance;
	}
	
	public MainActivity getCurrentActivity() {
		return currentUIActivity;
	}
	
	public SyncProxyALM getProxy() {
		return proxy;
	}

	public void setCurrentActivity(MainActivity currentActivity) {
		this.currentUIActivity = currentActivity;
	}
	
	public void onCreate() {
		super.onCreate();
		instance = this;
	}
	
	public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
        	mBtAdapter = BluetoothAdapter.getDefaultAdapter();
    		if (mBtAdapter != null){
    			if (mBtAdapter.isEnabled()){
    				startProxy();
    			}
    		}
		}
        if (MainActivity.getInstance() != null) {
        	setCurrentActivity(MainActivity.getInstance());
        }
			
        return START_STICKY;
	}
	
	public void startProxy() {
		if (proxy == null) {
			try {
				proxy = new SyncProxyALM(this, "Eco Pioneer", true);
			} catch (SyncException e) {
				e.printStackTrace();
				//error creating proxy, returned proxy = null
				if (proxy == null){
					stopSelf();
				}
			}
		}
	}
	
	public void onDestroy() {
		disposeSyncProxy();
		clearlockscreen();
		instance = null;
		super.onDestroy();
	}
	
	public void disposeSyncProxy() {
		if (proxy != null) {
			try {
				proxy.dispose();
			} catch (SyncException e) {
				e.printStackTrace();
			}
			proxy = null;
			clearlockscreen();
		}
	}
	
	public void onProxyClosed(String info, Exception e) {
		clearlockscreen();
		
		if((((SyncException) e).getSyncExceptionCause() != SyncExceptionCause.SYNC_PROXY_CYCLED))
		{
			if (((SyncException) e).getSyncExceptionCause() != SyncExceptionCause.BLUETOOTH_DISABLED) 
			{
				Log.v(TAG, "reset proxy in onproxy closed");
				reset();
			}
		}
	}

   public void reset(){
	   if (proxy != null) {
		   try {
			   proxy.resetProxy();
		   } catch (SyncException e1) {
			   e1.printStackTrace();
			   //something goes wrong, & the proxy returns as null, stop the service.
			   //do not want a running service with a null proxy
			   if (proxy == null){
				   stopSelf();
			   }
		   }
	   }else {
		   startProxy();
	   }
   }
   
   static final int CMD_START = 200;
   static final int CMD_FINISH = 201;
   static final int CMD_SUMMARY = 202;
   
   public void onOnHMIStatus(OnHMIStatus notification) {

		  switch(notification.getSystemContext()) {
				 case SYSCTXT_MAIN:
					   break;
				 case SYSCTXT_VRSESSION:
					   break;
				 case SYSCTXT_MENU:
					   break;
				 default:
					   return;
		  }
		  
		  switch(notification.getAudioStreamingState()) {
				 case AUDIBLE:
					//play audio if applicable
					   break;
				 case NOT_AUDIBLE:
					//pause/stop/mute audio if applicable
					   break;
				 default:
					   return;
		  }
		  
		  switch(notification.getHmiLevel()) {
				 case HMI_FULL:
					 if (driverdistrationNotif == false) {showLockScreen();}
					 if(notification.getFirstRun()) {
						   //setup app on SYNC
						   //send welcome message if applicable
						 	try {
								proxy.show("this is the first", "show command", TextAlignment.CENTERED, autoIncCorrId++);
							} catch (SyncException e) {
								DebugTool.logError("Failed to send Show", e);
							}
						 	
						 	//addCommand
						 	addVoiceCommand("Start", CMD_START);
						 	addVoiceCommand("Finish", CMD_FINISH);
						 	addVoiceCommand("Summary", CMD_SUMMARY);
							
						    //subscribe to buttons
						 	subButtons();
						 	if (MainActivity.getInstance() != null) {
					        	setCurrentActivity(MainActivity.getInstance());
					        }
						}
					 else{
						 try {
								proxy.show("SyncProxy is", "Alive", TextAlignment.CENTERED, autoIncCorrId++);
							} catch (SyncException e) {
								DebugTool.logError("Failed to send Show", e);
							}
					 }
					   break;
				 case HMI_LIMITED:
					 if (driverdistrationNotif == false) {showLockScreen();}
					   break;
				 case HMI_BACKGROUND:
					 if (driverdistrationNotif == false) {showLockScreen();}
					   break;
				 case HMI_NONE:
					   Log.i("hello", "HMI_NONE");
					   driverdistrationNotif = false;
					   clearlockscreen();
					   break;
				 default:
					   return;
		  }
   }
   
   private void addVoiceCommand(String command, int cmdId)
   {
	 	// Add Command
	 	AddCommand msg = new AddCommand();
		msg.setCorrelationID(autoIncCorrId++);
		
		String vrSynonym = command;
		if (vrSynonym.length() > 0) {
			Vector<String> vrCommands = new Vector<String>();
			vrCommands.add(vrSynonym);
			msg.setVrCommands(vrCommands);
		}
		
		msg.setCmdID(cmdId);
		
		try {
			AppLinkService.getInstance().getProxy().sendRPCRequest(msg);
		} catch (SyncException e) {
		}
		// End
	   
   }
public void showLockScreen() {
	//only throw up lockscreen if main activity is currently on top
	//else, wait until onResume() to throw lockscreen so it doesn't 
	//pop-up while a user is using another app on the phone
	if(currentUIActivity != null) {
		if(currentUIActivity.isActivityonTop() == true){
			if(LockScreenActivity.getInstance() == null) {
				Intent i = new Intent(this, LockScreenActivity.class);
				i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				i.addFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION);
				startActivity(i);
			}
		}
	}
	lockscreenUP = true;		
}

private void clearlockscreen() {
	if(LockScreenActivity.getInstance() != null) {  
		LockScreenActivity.getInstance().exit();
	}
	lockscreenUP = false;
}

public boolean getLockScreenStatus() {return lockscreenUP;}

public void subButtons() {
	try {
        proxy.subscribeButton(ButtonName.OK, autoIncCorrId++);
        proxy.subscribeButton(ButtonName.SEEKLEFT, autoIncCorrId++);
		proxy.subscribeButton(ButtonName.SEEKRIGHT, autoIncCorrId++);
		proxy.subscribeButton(ButtonName.TUNEUP, autoIncCorrId++);
		proxy.subscribeButton(ButtonName.TUNEDOWN, autoIncCorrId++);
		proxy.subscribeButton(ButtonName.PRESET_1, autoIncCorrId++);
		proxy.subscribeButton(ButtonName.PRESET_2, autoIncCorrId++);
		proxy.subscribeButton(ButtonName.PRESET_3, autoIncCorrId++);
		proxy.subscribeButton(ButtonName.PRESET_4, autoIncCorrId++);
		proxy.subscribeButton(ButtonName.PRESET_5, autoIncCorrId++);
		proxy.subscribeButton(ButtonName.PRESET_6, autoIncCorrId++);
		proxy.subscribeButton(ButtonName.PRESET_7, autoIncCorrId++);
		proxy.subscribeButton(ButtonName.PRESET_8, autoIncCorrId++);
		proxy.subscribeButton(ButtonName.PRESET_9, autoIncCorrId++);
		proxy.subscribeButton(ButtonName.PRESET_0, autoIncCorrId++);
	} catch (SyncException e) {}
}

public void onOnDriverDistraction(OnDriverDistraction notification) {
	driverdistrationNotif = true;
	//Log.i(TAG, "dd: " + notification.getStringState());
	if (notification.getState() == DriverDistractionState.DD_OFF)
	{
		Log.i(TAG,"clear lock, DD_OFF");
		clearlockscreen();
	} else {
		Log.i(TAG,"show lockscreen, DD_ON");
		showLockScreen();
	}
}

public void onError(String info, Exception e) {
	// TODO Auto-generated method stub
}

public void onGenericResponse(GenericResponse response) {
	// TODO Auto-generated method stub
}

public static void speakVoice(String text)
{
	if(AppLinkService.getInstance() == null)
		return;
	
	Speak msg = new Speak();
	msg.setCorrelationID(autoIncCorrId++);
	Log.v("EcoMaster", text);
	Vector<TTSChunk> chunks = new Vector<TTSChunk>();

	if (text.length() > 0) {
		chunks.add(TTSChunkFactory.createChunk(SpeechCapabilities.TEXT, text));
	}
	msg.setTtsChunks(chunks);
	try {
		Log.v("EcoMaster", msg.toString());
		AppLinkService.getInstance().getProxy().sendRPCRequest(msg);
	} catch (SyncException e) {
		Log.v("EcoMaster", e.toString());
	}
}

public void onOnCommand(OnCommand notification) {
	switch(notification.getCmdID())
	{
		case CMD_START: //XML Test
			speakVoice(MainActivity.ecoService.Start());
			break;
		case CMD_FINISH: //XML Test
			speakVoice(MainActivity.ecoService.Stop());
        	Intent intent = new Intent(currentUIActivity, MapActivity.class); 
        	intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        	getApplication().startActivity(intent);
			break;
		default:
			break;
	}
}

public void onAddCommandResponse(AddCommandResponse response) {
	// TODO Auto-generated method stub
}

public void onAddSubMenuResponse(AddSubMenuResponse response) {
	// TODO Auto-generated method stub
}

public void onCreateInteractionChoiceSetResponse(
		CreateInteractionChoiceSetResponse response) {
	// TODO Auto-generated method stub
}

public void onAlertResponse(AlertResponse response) {
	// TODO Auto-generated method stub
}

public void onDeleteCommandResponse(DeleteCommandResponse response) {
	// TODO Auto-generated method stub
}

public void onDeleteInteractionChoiceSetResponse(
		DeleteInteractionChoiceSetResponse response) {
	// TODO Auto-generated method stub
}

public void onDeleteSubMenuResponse(DeleteSubMenuResponse response) {
	// TODO Auto-generated method stub
}

public void onEncodedSyncPDataResponse(EncodedSyncPDataResponse response) {
	// TODO Auto-generated method stub
}

public void onPerformInteractionResponse(PerformInteractionResponse response) {
	// TODO Auto-generated method stub
}

public void onResetGlobalPropertiesResponse(
		ResetGlobalPropertiesResponse response) {
	// TODO Auto-generated method stub
}

public void onSetGlobalPropertiesResponse(SetGlobalPropertiesResponse response) {
}

public void onSetMediaClockTimerResponse(SetMediaClockTimerResponse response) {
	// TODO Auto-generated method stub
}

public void onShowResponse(ShowResponse response) {
	// TODO Auto-generated method stub
}

public void onSpeakResponse(SpeakResponse response) {
	// TODO Auto-generated method stub
}

public void onOnButtonEvent(OnButtonEvent notification) {
	// TODO Auto-generated method stub
}

public void onOnButtonPress(OnButtonPress notification) {
	// TODO Auto-generated method stub
}

public void onSubscribeButtonResponse(SubscribeButtonResponse response) {
	// TODO Auto-generated method stub
}

public void onUnsubscribeButtonResponse(UnsubscribeButtonResponse response) {
	// TODO Auto-generated method stub	
}

public void onOnPermissionsChange(OnPermissionsChange notification) {
	// TODO Auto-generated method stub	
}

public void onOnEncodedSyncPData(OnEncodedSyncPData notification) {
	// TODO Auto-generated method stub
}

public void onOnTBTClientState(OnTBTClientState notification) {
	// TODO Auto-generated method stub
}

@Override
public IBinder onBind(Intent intent) {
	// TODO Auto-generated method stub
	return null;
}
}
