package voja.android.vault;

import android.app.Application;

public class VaultApplication extends Application{

	private Boolean auth = false;
	
	@Override
	public void onCreate() {
		super.onCreate();
	}
	
	public void setAuth(boolean state) {
		auth = state;
	}
	
	public boolean isAuthenticated() {
		return auth;
	}
}
