package com.ikkeware.rambooster;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;


public class PhoneCallBlockerServiceReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {

        TelephonyManager tel=(TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        assert tel != null;
        tel.listen(new PhoneCallBlocker(context), PhoneStateListener.LISTEN_CALL_STATE);



    }
}
