package com.androidstudioprojects.etsysharingfavorites.google;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;

public class GoogleServicesHelper implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
	
	public interface GoogleServicesListener {
		public void onConnected();
		
		public void onDisconnected();
	}
	
	private static final int REQUEST_CODE_RESOLUTION = -100;
	private static final int REQUEST_CODE_AVAILABILITY = -101;
	
	private Activity activity;
	private GoogleServicesListener listener;
	private GoogleApiClient apiClient;
	
	public GoogleServicesHelper(Activity activity,
	                            GoogleServicesListener listener) {
		this.activity = activity;
		this.listener = listener;
		
		this.apiClient =
						new GoogleApiClient.Builder(activity).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(Plus.API, Plus.PlusOptions.builder().setServerClientId(("GOOGLE_PROVIDED_SERVER_CLIENT_ID")).build()).build();
	}
	
	public void connect() {
		if (isGooglePlayServicesAvailable()) {
			apiClient.connect();
		} else {
			listener.onDisconnected();
		}
	}
	
	public void disconnect() {
		if (isGooglePlayServicesAvailable()) {
			apiClient.disconnect();
		} else {
			listener.onDisconnected();
		}
	}
	
	private boolean isGooglePlayServicesAvailable() {
		int availability =
						GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);
		switch (availability) {
			case ConnectionResult.SUCCESS:
				return true;
			case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED:
			case ConnectionResult.SERVICE_DISABLED:
			case ConnectionResult.SERVICE_INVALID:
				GooglePlayServicesUtil.getErrorDialog(availability, activity,
								REQUEST_CODE_AVAILABILITY).show();
				return false;
			default:
				return false;
		}
	}
	
	@Override
	public void onConnected(@Nullable Bundle bundle) {
		listener.onConnected();
	}
	
	@Override
	public void onConnectionSuspended(int i) {
		listener.onDisconnected();
	}
	
	@Override
	public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
		if (connectionResult.hasResolution()) {
			try {
				connectionResult.startResolutionForResult(activity,
								REQUEST_CODE_RESOLUTION);
			} catch (IntentSender.SendIntentException e) {
				e.printStackTrace();
				connect();
			}
		} else {
			listener.onDisconnected();
		}
	}
	
	public void handleActivityResult(int requestCode, int resultCode,
	                                 Intent data) {
		if (requestCode == REQUEST_CODE_RESOLUTION || requestCode == REQUEST_CODE_AVAILABILITY) {
			if (resultCode == Activity.RESULT_OK) {
				connect();
			} else {
				listener.onDisconnected();
			}
		}
	}
}
