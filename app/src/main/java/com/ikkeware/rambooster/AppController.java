package com.ikkeware.rambooster;


import android.app.Activity;
import android.content.Context;

import android.content.SharedPreferences;


import android.widget.Toast;


import androidx.annotation.Nullable;
import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


import CustomComponents.FancyGifDialog;

import static android.content.Context.MODE_PRIVATE;

public class AppController{
    Context context;
    private Activity activity;
    private BillingClient billingClient;
    private final String SKU="premium_upgrade";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private final String SP_FIELD=".token.user.temp";
    private final String IS_PREMIUM="isPremium";
    private final String SHARED_PREF_TOKEN="token",SHARED_PREF_TOKEN_STATUS="token-status",SHARED_PREF_ORDER_ID="orderid";
    private final int NOT_SAVED=9;


    AppController(Context context,Activity activity){
        this.context=context;
        this.activity=activity;
        // purchaseToken=getUserPurchaseToken();
    }


    private void  openBillingConnection(final BillingClientStateListener billingClientStateListener){
        billingClient=BillingClient.newBuilder(context).setListener(new PurchasesUpdatedListener() {
            @Override
            public void onPurchasesUpdated(BillingResult billingResult, @Nullable List<Purchase> list) {

            }
        }).enablePendingPurchases().build();

        billingClient.startConnection(billingClientStateListener);

    }

    private BillingClient openBillingConnection(PurchasesUpdatedListener purchasesUpdatedListener, final BillingClientStateListener billingClientStateListener){
        billingClient=BillingClient.newBuilder(context).setListener(purchasesUpdatedListener).enablePendingPurchases().build();

        billingClient.startConnection(billingClientStateListener);
        return billingClient;
    }

    public boolean isUserPremium(){

        sharedPreferences=context.getSharedPreferences(context.getPackageName()+SP_FIELD,MODE_PRIVATE);
        if (!sharedPreferences.contains(IS_PREMIUM)) {
            openBillingConnection(new BillingClientStateListener() {
                @Override
                public void onBillingSetupFinished(BillingResult billingResult) {
                    List<Purchase> purchases = new ArrayList<>();
                    Purchase.PurchasesResult result = billingClient.queryPurchases(BillingClient.SkuType.INAPP);

                    if (result.getPurchasesList() != null) {
                        purchases = new ArrayList<>(result.getPurchasesList());
                    }
                    if (purchases.size() > 0) {
                        for (Purchase purchase : purchases) {
                            if (purchase.getSkus().get(0).equals(SKU)) {
                                if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
                                    if (purchase.isAcknowledged()) {
                                        editor = sharedPreferences.edit();
                                        editor.putBoolean(IS_PREMIUM, true);
                                        editor.commit();
                                    }
                                }

                            }
                        }
                    }
                }

                @Override
                public void onBillingServiceDisconnected() {

                }
            });

        }

        return sharedPreferences.getBoolean(IS_PREMIUM,false);
    }

    public void verifyPremium(){
        sharedPreferences=context.getSharedPreferences(context.getPackageName()+SP_FIELD,MODE_PRIVATE);
        openBillingConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                List<Purchase> purchases = new ArrayList<>();
                Purchase.PurchasesResult result = billingClient.queryPurchases(BillingClient.SkuType.INAPP);

                if (result.getPurchasesList() != null) {
                    purchases = new ArrayList<>(result.getPurchasesList());
                }
                if (purchases.size() > 0) {
                    for (Purchase purchase : purchases) {
                        if (purchase.getSkus().get(0).equals(SKU)) {
                            if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
                                if (purchase.isAcknowledged()) {
                                    editor = sharedPreferences.edit();
                                    editor.putBoolean(IS_PREMIUM, true);
                                    editor.apply();
                                }
                            }

                        }
                    }
                }
            }

            @Override
            public void onBillingServiceDisconnected() {

            }
        });


    }

    public void verifyPurchases(){
        //Verifica compras que ainda não foram confirmadas e as confirma
        openBillingConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                billingClient.queryPurchasesAsync(BillingClient.SkuType.INAPP, (billingResult1, list) -> {
                    if(list.size() > 0) {
                        for (Purchase purchase : list) {
                            if (purchase.getSkus().get(0).equals(SKU)) {
                                //Confirma a compra caso ela ainda não tenha sido confirmada
                                handlePurchases(purchase);
                       } } }
           });
            }

            @Override
            public void onBillingServiceDisconnected() {

            }
        });


    }


    private void saveUserAccountStatusLocal(boolean isPremium){
        sharedPreferences=context.getSharedPreferences(context.getPackageName()+SP_FIELD,MODE_PRIVATE);
        editor=sharedPreferences.edit();
        editor.putBoolean(IS_PREMIUM,isPremium);
        editor.apply();
    }



    public String queryPremiumPrice(){
        final String[] price = new String[1];
        openBillingConnection( new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    final SkuDetailsParams.Builder skuDetailsParams = SkuDetailsParams.newBuilder();
                    skuDetailsParams.setSkusList(Collections.singletonList(SKU)).setType(BillingClient.SkuType.INAPP);

                    billingClient.querySkuDetailsAsync(skuDetailsParams.build(), new SkuDetailsResponseListener() {
                        @Override
                        public void onSkuDetailsResponse(BillingResult billingResult, List<SkuDetails> list) {
                            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {

                                for (SkuDetails skuDetails : list) {
                                    if (skuDetails.getSku().equals(SKU)) {
                                        price[0] = skuDetails.getPrice();

                                        break;
                                    }
                                }


                            }
                        }
                    });


                }
            }

            @Override
            public void onBillingServiceDisconnected() {

            }
        });


        return price[0];
    }

    private boolean handlePurchases(Purchase purchase){

        if(purchase.getPurchaseState()== Purchase.PurchaseState.PURCHASED){
            //purchaseToken=getUserPurchaseToken();

            //confirms the purchase automatically if it's not confirmed
            if(!purchase.isAcknowledged()){
                AcknowledgePurchaseParams acknowledgePurchaseParams =
                        AcknowledgePurchaseParams.newBuilder()
                                .setPurchaseToken(purchase.getPurchaseToken())
                                .build();
                billingClient.acknowledgePurchase(acknowledgePurchaseParams, new AcknowledgePurchaseResponseListener() {
                    @Override
                    public void onAcknowledgePurchaseResponse(BillingResult billingResult) {

                    }
                });
            }
            return true;
        }
        return  false;
    }

    public void showPurchaseDialog(){

        openBillingConnection(new PurchasesUpdatedListener() {
            @Override
            public void onPurchasesUpdated(BillingResult billingResult, @Nullable List<Purchase> list) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK
                        && list != null) {

                    sharedPreferences=context.getSharedPreferences(context.getPackageName()+".token.user.temp",MODE_PRIVATE);
                    editor=sharedPreferences.edit();
                    for (Purchase purchase : list) {
                        handlePurchases(purchase);
                        //Armazena o token de compra do usuario localmente se o status da compra for igual a PURCHASED
                        //Os dados salvos localmente deverão ser enviados ao servidor
                        //APOS o envio com SUCESSO ao servidor os dados locais poderão ser apagados.
                        //O unico dado que deve persistir é o ISPURCHASED
                        //O ISPURCHASED é criado no metodo verifyPremium()
                        if(purchase.getPurchaseState()==Purchase.PurchaseState.PURCHASED){

                            editor.putString(SHARED_PREF_TOKEN,purchase.getPurchaseToken());//Token de compra do usuario
                            editor.putString(SHARED_PREF_ORDER_ID,purchase.getOrderId());//ID da compra
                            editor.putInt(SHARED_PREF_TOKEN_STATUS,NOT_SAVED);//Status do registro
                            editor.apply();
                            //saveTokenToServerExecute();
                            saveUserAccountStatusLocal(true);
                            //Dialog
                            new FancyGifDialog.Builder(activity)
                                    .setTitle("You are Premium!")
                                    .setTitleSize(22f)
                                    .setPositiveBtnText("Close")
                                    .setMessage("Thank you so much for supporting us!" +
                                            " Enjoy your premium super powers ;)")
                                    .setPositiveBtnBackground("#A227FF")
                                    .setTitleColor(context.getColor(R.color.darkActionBarColor))
                                    .setNegativeBtnBackground("#A227FF")
                                    .setGifResource(R.drawable.premium_background_header)   //Pass your Gif here
                                    .isCancellable(true).build();
                        }
                        else{
                            saveUserAccountStatusLocal(false);
                        }

                    }
                }

            }

        }, new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {//Show purchase dialog here

                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    //querying the product details
                    final SkuDetailsParams.Builder skuDetailsParams = SkuDetailsParams.newBuilder();
                    skuDetailsParams.setSkusList(Collections.singletonList(SKU)).setType(BillingClient.SkuType.INAPP);

                    billingClient.querySkuDetailsAsync(skuDetailsParams.build(), new SkuDetailsResponseListener() {
                        @Override
                        public void onSkuDetailsResponse(BillingResult billingResult, List<SkuDetails> list) {
                            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                                //Launching dialog here
                                for (SkuDetails skuDetails : list) {
                                    if (skuDetails.getSku().equals(SKU)) {
                                        BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                                                .setSkuDetails(skuDetails)
                                                .build();
                                        billingClient.launchBillingFlow(activity, flowParams);//launch purchase dialog here
                                        break;
                                    }
                                }
                            }
                            else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.ERROR ||
                                    billingResult.getResponseCode() == BillingClient.BillingResponseCode.SERVICE_DISCONNECTED ) {
                                Toast.makeText(context, "An error has occurred, please check you internet connection. "
                                        , Toast.LENGTH_LONG).show();
                            }


                        }
                    });
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                billingClient.startConnection(this);
            }
        });
    }


}
