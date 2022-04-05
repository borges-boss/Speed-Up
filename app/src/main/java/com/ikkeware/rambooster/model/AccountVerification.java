package com.ikkeware.rambooster.model;

import android.content.Context;
import android.content.SharedPreferences;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesResponseListener;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

import androidx.annotation.NonNull;

public class AccountVerification {

    private BillingClient billingClient;
    private final String SKU="premium_upgrade";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private final String SP_FIELD=".token.user.temp";
    private final String IS_PREMIUM="isPremium";
    private Context context;


    public AccountVerification(Context context){
        this.context=context;
    }


    public boolean verifyUserAccountType(){

        final boolean resp=false;

        sharedPreferences=context.getSharedPreferences(context.getPackageName()+SP_FIELD,MODE_PRIVATE);
        if(sharedPreferences.contains(IS_PREMIUM)) {
            return sharedPreferences.getBoolean(IS_PREMIUM, false);
        }
        else{
            billingClient=BillingClient.newBuilder(context).setListener(null).enablePendingPurchases().build();

            billingClient.startConnection(new BillingClientStateListener() {
                @Override
                public void onBillingSetupFinished(BillingResult billingResult) {
                    List<Purchase> purchases=new ArrayList<>();
               billingClient.queryPurchasesAsync(BillingClient.SkuType.INAPP, new PurchasesResponseListener() {
                   @Override
                   public void onQueryPurchasesResponse(@NonNull BillingResult billingResult, @NonNull List<Purchase> list) {
                       if(list.size() > 0) {
                           for (Purchase purchase : list) {
                               if (purchase.getSkus().get(0).equals(SKU)) {
                                   if(purchase.getPurchaseState()== Purchase.PurchaseState.PURCHASED){
                                       if(purchase.isAcknowledged()){
                                           editor=sharedPreferences.edit();
                                           editor.putBoolean(IS_PREMIUM,true);
                                           editor.commit();
                                       }
                                   }
                               } } }
                   }
               });



                }

                @Override
                public void onBillingServiceDisconnected() {

                }
            });


            return false;
        }



    }





}
