package com.androidstudioprojects.etsysharingfavorites;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.androidstudioprojects.etsysharingfavorites.google.GoogleServicesHelper;
import com.androidstudioprojects.etsysharingfavorites.model.ActiveListings;

public class MainActivity extends AppCompatActivity {
	
	private static final String STATE_ACTIVE_LISTINGS = "StateActiveListings";
	
	private RecyclerView recyclerView;
	private View progressBar;
	private TextView errorView;
	
	private GoogleServicesHelper googleServicesHelper;
	private ListingAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		recyclerView = findViewById(R.id.recyclerView);
		progressBar = findViewById(R.id.progressBar);
		errorView = findViewById(R.id.error_view);
		
		// Setup RecycleView
		recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1,
						StaggeredGridLayoutManager.VERTICAL));
		
		adapter = new ListingAdapter(this);
		recyclerView.setAdapter(adapter);
		
		// googleServiceHelper constructor is created after the Adapter is created
		googleServicesHelper = new GoogleServicesHelper(this, adapter);
		
		// Always showLoading() by default
		showLoading();
		
		if (savedInstanceState != null) {
			if (savedInstanceState.containsKey(STATE_ACTIVE_LISTINGS)) {
				adapter.success((ActiveListings) savedInstanceState.getParcelable(STATE_ACTIVE_LISTINGS), null);
			}
		}
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		googleServicesHelper.connect();
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		googleServicesHelper.disconnect();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
	                                @Nullable Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		googleServicesHelper.handleActivityResult(requestCode, resultCode, data);
		
		if (requestCode == ListingAdapter.REQUEST_CODE_PLUS_ONE) {
			adapter.notifyDataSetChanged();
		}
	}
	
	@Override
	protected void onSaveInstanceState(@NonNull Bundle outState) {
		super.onSaveInstanceState(outState);
		ActiveListings activeListings = adapter.getActiveListings();
		if (activeListings != null) {
			outState.putParcelable(STATE_ACTIVE_LISTINGS, activeListings);
		}
	}
	
	public void showLoading() {
		progressBar.setVisibility(View.VISIBLE);
		recyclerView.setVisibility(View.GONE);
		errorView.setVisibility(View.GONE);
	}
	
	public void showList() {
		progressBar.setVisibility(View.GONE);
		recyclerView.setVisibility(View.VISIBLE);
		errorView.setVisibility(View.GONE);
	}
	
	public void showError() {
		progressBar.setVisibility(View.GONE);
		recyclerView.setVisibility(View.GONE);
		errorView.setVisibility(View.VISIBLE);
	}
}
