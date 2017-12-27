package com.dieam.reactnativepushnotification.modules;

import android.os.Bundle;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class RNPushNotificationAttributes {
    private HashMap<String, Object> attributes;

    public RNPushNotificationAttributes() {
        attributes = new HashMap<>();
    }

    public Bundle toBundle() {
        Bundle bundle = new Bundle();
        for(String key : attributes.keySet()) {
            Object value = attributes.get(key);
            if(value instanceof String) {
                bundle.putString(key, (String)value);
            } else if(value instanceof Boolean) {
                bundle.putBoolean(key, (boolean)value);
            } else if(value instanceof Double) {
                bundle.putDouble(key, (double)value);
            }
        }
        return bundle;
    }

    public void fromBundle(Bundle bundle) {
        Set<String> keys = bundle.keySet();
        for(String key : keys) {
            Object value = bundle.get(key);
            attributes.put(key, value);
        }
    }

    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            for(String key : attributes.keySet()) {
                Object value = attributes.get(key);
                jsonObject.put(key, value);
            }
        } catch (JSONException e) {
            Log.e(RNPushNotification.LOG_TAG, "Exception while converting RNPushNotificationAttributes to " +
                    "JSON. Returning an empty object", e);
            return new JSONObject();
        }
        return jsonObject;
    }

    public void fromJson(JSONObject jsonObject) {
        try {
            Iterator<String> keysIterator = jsonObject.keys();
            while (keysIterator.hasNext()) {
                String key = keysIterator.next();
                Object value = jsonObject.get(key);
                attributes.put(key, value);
            }
        } catch (JSONException e) {
            Log.e(RNPushNotification.LOG_TAG, "Exception while initializing RNPushNotificationAttributes from " +
                    "JSON. Some fields may not be set", e);
        }
    }

    @Override
    // For debugging
    public String toString() {
        return "RNPushNotificationAttributes: " + attributes.toString();
    }

    public String getId() {
        return (String)attributes.get("id");
    }

    public double getFireDate() {
        return (double)attributes.get("fireDate");
    }

    public HashMap<String, Object> getAttributes() {
        return attributes;
    }

}
