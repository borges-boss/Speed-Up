package com.ikkeware.rambooster;


import android.content.Context;
import android.media.AudioManager;
import android.os.Build;
import android.telecom.TelecomManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;


public class PhoneCallBlocker extends PhoneStateListener {

    private Context context;
    public PhoneCallBlocker(Context context){
        this.context=context;
    }


    @Override
    public void onCallStateChanged(int state, String phoneNumber){

        AudioManager phoneRingSound= (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        TelephonyManager tel=(TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        TelecomManager tl=(TelecomManager) context.getSystemService(Context.TELECOM_SERVICE);


        assert tel != null;
        System.err.println("CALL STATE LISTENER "+tel.getCallState());



        switch (state) {
            case TelephonyManager.CALL_STATE_IDLE:
                Log.v("PhoneStateListener", "IDLE");
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                Log.v("PhoneStateListener", "OFFHOOK");
                break;
            case TelephonyManager.CALL_STATE_RINGING:
                Log.v("State", "Ringing");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    assert tl != null;
                    // tl.silenceRinger();
                    assert phoneRingSound != null;
                    phoneRingSound.setStreamVolume(AudioManager.STREAM_RING,AudioManager.ADJUST_MUTE,AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
                    tl.endCall();
                    Toast.makeText(context, "CALL BLOCKED!", Toast.LENGTH_SHORT).show();
                }
                break;
            default: {
                Log.v("Status", "something");
            }

        }


    }

}
