package com.androidstudioprojects.etsysharingfavorites;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.androidstudioprojects.etsysharingfavorites.api.etsy;
import com.androidstudioprojects.etsysharingfavorites.google.GoogleServicesHelper;
import com.androidstudioprojects.etsysharingfavorites.model.ActiveListings;
import com.androidstudioprojects.etsysharingfavorites.model.Listing;
import com.google.android.gms.plus.PlusOneButton;
import com.squareup.picasso.Picasso;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ListingAdapter extends RecyclerView.Adapter<ListingAdapter.ListingHolder>
				implements Callback<ActiveListings>,
				GoogleServicesHelper.GoogleServicesListener {
	
	public static final int REQUEST_CODE_PLUS_ONE = 10;
	public static final int REQUEST_CODE_SHARE = 11;
	
	private MainActivity activity;
	private LayoutInflater inflater;
	private ActiveListings activeListings;
	
	private boolean isGooglePlayServicesAvailable;
	
	// Create constructor for the ListingAdapter
	public ListingAdapter(MainActivity activity) {
		this.activity = activity;
		inflater = LayoutInflater.from(activity);
		this.isGooglePlayServicesAvailable = false;
	}
	
	@NonNull
	@Override
	// Create new instance of ViewHolder
	public ListingHolder onCreateViewHolder(@NonNull ViewGroup viewGroup,
	                                        int i) {
		return new ListingHolder(inflater.inflate(R.layout.layout_listing,
						viewGroup, false));
	}
	
	// Bind the data from the listings to the views themselves
	@Override
	public void onBindViewHolder(@NonNull ListingHolder listingHolder, int i) {
		final Listing listing = activeListings.results[i];
		listingHolder.titleView.setText(listing.title);
		listingHolder.priceView.setText(listing.price);
		listingHolder.shopNameView.setText(listing.Shop.shop_name);
		
		listingHolder.itemView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent openListing = new Intent(Intent.ACTION_VIEW);
				openListing.setData(Uri.parse(listing.url));
				activity.startActivity(openListing);
			}
		});
		
		// If googlePlayServices Available, display + button for sharing
		if (isGooglePlayServicesAvailable) {
			listingHolder.plusOneButton.setVisibility(View.VISIBLE);
			listingHolder.plusOneButton.initialize(listing.url, REQUEST_CODE_PLUS_ONE);
			listingHolder.plusOneButton.setAnnotation(PlusOneButton.ANNOTATION_NONE);
			
			// set up SHARE button
			listingHolder.shareButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					Intent intent = new Intent(Intent.ACTION_SEND);
					intent.putExtra(Intent.EXTRA_TEXT,
									"Checkout this item on Etsy " + listing.title + " " + listing.url);
					intent.setType("text/plain");
					
					activity.startActivityForResult(Intent.createChooser(intent, "Share"), REQUEST_CODE_SHARE);
				}
			});
		} else {
			listingHolder.plusOneButton.setVisibility(View.GONE);
			
			listingHolder.shareButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					Intent intent = new Intent(Intent.ACTION_SEND);
					intent.putExtra(Intent.EXTRA_TEXT,
									"Checkout this item on Etsy " + listing.title + " " + listing.url);
					intent.setType("text/plain");
					
					activity.startActivityForResult(Intent.createChooser(intent, "Share"), REQUEST_CODE_SHARE);
				}
			});
		}
		
		Picasso.with(listingHolder.imageView.getContext())
						.load(listing.Images[0].url_570xN)
						.into(listingHolder.imageView);
	}
	
	// return number of items display on this list
	@Override
	public int getItemCount() {
		if (activeListings == null)
			return 0;
		
		if (activeListings.results == null)
			return 0;
		
		return activeListings.results.length;
	}
	
	@Override
	public void success(ActiveListings activeListings, Response response) {
		this.activeListings = activeListings;
		notifyDataSetChanged();
		this.activity.showList();
	}
	
	@Override
	public void failure(RetrofitError error) {
		this.activity.showError();
	}
	
	public ActiveListings getActiveListings(){
		return activeListings;
	}
	
	@Override
	public void onConnected() {
		
		// If items count == 0, call etsy api for item listings
		if (getItemCount() == 0) {
			etsy.getActiveListings(this);
		}
		
		isGooglePlayServicesAvailable = true;
		notifyDataSetChanged();
	}
	
	@Override
	public void onDisconnected() {
		
		if (getItemCount() == 0) {
			etsy.getActiveListings(this);
		}
		
		isGooglePlayServicesAvailable = false;
		notifyDataSetChanged();
	}
	
	public class ListingHolder extends RecyclerView.ViewHolder {
		
		public ImageView imageView;
		public TextView titleView;
		public TextView shopNameView;
		public TextView priceView;
		
		public PlusOneButton plusOneButton;
		public ImageButton shareButton;
		
		public ListingHolder(@NonNull View itemView) {
			super(itemView);
			imageView = itemView.findViewById(R.id.listing_image);
			titleView = itemView.findViewById(R.id.listing_title);
			shopNameView = itemView.findViewById(R.id.listing_shop_name);
			priceView = itemView.findViewById(R.id.listing_price);
			
			plusOneButton =
							(PlusOneButton) itemView.findViewById(R.id.listing_plus_one_btn);
			shareButton = (ImageButton) itemView.findViewById(R.id.listing_share_btn);
		}
	}
	
}
