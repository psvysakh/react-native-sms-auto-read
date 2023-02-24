package com.smsautoread;

import android.app.Activity;
import android.content.IntentFilter;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.module.annotations.ReactModule;

import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.google.android.gms.auth.api.phone.SmsRetriever;

import java.util.HashMap;
import java.util.Map;

@ReactModule(name = SmsAutoReadModule.NAME)
public class SmsAutoReadModule extends ReactContextBaseJavaModule {
  public static final String NAME = "SmsAutoRead";
  private static final String AKV_SMS_RETRIEVED = "AKV_SMS_RETRIEVED";
  private static final String AKV_SMS_RETRIEVE_ERROR = "AKV_SMS_RETRIEVE_ERROR";

  public ReactApplicationContext reactContext;
  private SmsBroadcastReceiver broadcastReceiver;
  private SmsListener listener;

  public SmsAutoReadModule(ReactApplicationContext reactContext) {

    super(reactContext);
    this.reactContext = reactContext;
    listener = new SmsListener(this);
  }

  private void subscribe() throws SmsAutoReadException {
    Activity activity = getCurrentActivity();
    if (activity == null) {
      throw new SmsAutoReadException(
        Errors.NULL_ACTIVITY,
        "Could not subscribe, activity is null"
      );
    }

    SmsRetriever.getClient(getCurrentActivity()).startSmsUserConsent(null);

    broadcastReceiver = new SmsBroadcastReceiver(getCurrentActivity(), this);
    getCurrentActivity().registerReceiver(
      broadcastReceiver,
      new IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION),
      SmsRetriever.SEND_PERMISSION,
      null
    );

    reactContext.addActivityEventListener(listener);
  }

  private void unsubscribe() throws SmsAutoReadException {
    Activity activity = getCurrentActivity();

    if (activity == null) {
      throw new SmsAutoReadException(
        Errors.NULL_ACTIVITY,
        "Could not unsubscribe, activity is null"
      );
    }

    if (broadcastReceiver == null) {
      throw new SmsAutoReadException(
        Errors.NULL_BROADCAST_RECEIVER,
        "Could not unsubscribe, broadcastReceiver is null"
      );
    }

    activity.unregisterReceiver(broadcastReceiver);
    broadcastReceiver = null;

    reactContext.removeActivityEventListener(listener);
  }

  private void resubscribe() {
    try {
      unsubscribe();
    } catch (SmsAutoReadException e) {
      sendErrorEventToJs(e);
      return;
    }

    try {
      subscribe();
    } catch (SmsAutoReadException e) {
      sendErrorEventToJs(e);
    }
  }

  public void handleSms(String sms) {
    sendSmsEventToJs(sms);
    resubscribe();
  }

  public void handleError(SmsAutoReadException e) {
    sendErrorEventToJs(e);
    resubscribe();
  }

  private void sendSmsEventToJs(String sms) {
    WritableMap params = Arguments.createMap();
    params.putString("sms", sms);
    reactContext
      .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
      .emit(AKV_SMS_RETRIEVED, params);
  }

  private void sendErrorEventToJs(SmsAutoReadException e) {
    WritableMap params = Arguments.createMap();
    params.putString(e.code, e.getMessage());
    reactContext
      .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
      .emit(AKV_SMS_RETRIEVE_ERROR, params);
  }

  @ReactMethod
  public void startNativeSmsListener(Promise promise) {
    try {
      subscribe();
      promise.resolve(null);
    } catch (SmsAutoReadException e) {
      promise.reject(e.code, e.getMessage());
    }
  }

  @ReactMethod
  public void stopNativeSmsListener(Promise promise) {
    try {
      unsubscribe();
      promise.resolve(null);
    } catch (SmsAutoReadException e) {
      promise.reject(e.code, e.getMessage());
    }
  }

  @Override
  public Map<String, Object> getConstants() {
    final Map<String, Object> constants = new HashMap<>();

    for (Errors error : Errors.values()) {
      constants.put(error.toString(), error.toString());
    }

    return constants;
  }

  @Override
  @NonNull
  public String getName() {
    return NAME;
  }

}
