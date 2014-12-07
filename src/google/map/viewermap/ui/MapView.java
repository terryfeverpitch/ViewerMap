package google.map.viewermap.ui;

import android.graphics.Bitmap;
import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MapView extends WebView {
	public final static int HOME_CODE = 0;
	public final static int SCHOOL_CODE = 1;
	
	private int currentPlaceCode = HOME_CODE;
	
	private String MAP_URL = "file:///android_asset/map.html";
	private boolean loaded = false;
	private WebViewClient client = new WebViewClient() {
		@Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {          
//			MapActivity.progressdialog.setTitle("Loading");
//			MapActivity.progressdialog.setMessage(url + "is loading");
//			MapActivity.progressdialog.show();    
        }
		
		@Override
	    public boolean shouldOverrideUrlLoading(WebView view, String url) {
	        return shouldOverrideUrlLoading(view, url);
	    }
		
		@Override  
		public void onPageFinished(WebView view, String url) {
			MapView.this.loaded = true;
			initMap();
//			MapActivity.progressdialog.dismiss();
		}
	};
	
	public MapView(final Context context, final AttributeSet attrs) {
		super(context, attrs);
		getSettings().setJavaScriptEnabled(true);
		setWebViewClient(client);
		loadUrl(MAP_URL);
	}
	
	public void initMap() {
		// 25.074378, 121.661085 = home
		// 25.066884, 121.522144 = school
		loadUrl("javascript:initialize(25.074378, 121.661085)");
	}
	
	public void goSearch(String place) {
//		MapActivity.progressdialog.setTitle("Loading");
//		MapActivity.progressdialog.setMessage("searching for food...");
//		MapActivity.progressdialog.show();
//		loadUrl("javascript:searchFood()");
//		MapActivity.progressdialog.dismiss();
		loadUrl("javascript:goSearch('" + place + "')");
	}
	
	public void gotoPlace(int where) {
		currentPlaceCode = where;
		if(where == HOME_CODE)
			loadUrl("javascript:initialize(25.074378, 121.661085)");
		else if(where == SCHOOL_CODE)
			loadUrl("javascript:initialize(25.066884, 121.522144)");
	}
	
	public int getCurrentPlace() {
		return currentPlaceCode;
	}

	public boolean isReady() {
		return this.loaded;
	}
}
