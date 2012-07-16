package voja.android.vault;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

public class VaultActivity extends Activity {
	
	public static final int ITEM_WIDTH = 100;
	public static final int ITEM_HEIGHT = 100;
	public static final String ENC_KEY = "adluahdasbndlasdjbasldhv";
	
	SharedPreferences prefs;
	Bundle bundle;
	
	List<VaultItem> items;
	public static Sqlite database;
	
	int requestCode;
	
	int rotation;
		
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        bundle = savedInstanceState;
        
        database = new Sqlite(getApplicationContext());
        try {
        	database.db = database.getWritableDatabase();
        }catch(Exception e) {
        	android.util.Log.v("voja", "class:" + e.getClass().getCanonicalName());
        }
        
        init(savedInstanceState);
        
    }
	
	public void init(Bundle savedInstanceState) {
		int forceAuth = 0;
        try {
	        Bundle xtras = getIntent().getExtras();
	        if(xtras != null) {
		        if(xtras.containsKey("forceAuth")) {
		        	forceAuth = xtras.getInt("forceAuth");
		        }
	        }
        }catch(Exception e) {
        	// do nothing
        	e.printStackTrace();
        }
        
        if(getPref("password") == "") {
        	setContentView(R.layout.set_password);
        }else if(isAuthenticated() || forceAuth>0) {
        	setContentView(R.layout.main);
        	// set Application.auth=true
        	getVaultApplication().setAuth(true);
        	draw();
        }else{
        	setContentView(R.layout.login);
        }
	}
	
	public void draw() {
		try {
    		items = database.getAll();
    		drawItems();
    	}catch(Exception e) {
    		e.printStackTrace();
    		alert("Greška!\n" + e.toString());
    	}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		if(isAuthenticated()) {
			draw();
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		database.close();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    //Handle the back button
	    if(keyCode == KeyEvent.KEYCODE_BACK) {
	        //Ask the user if they want to quit
	        new AlertDialog.Builder(this)
	        .setIcon(android.R.drawable.ic_dialog_alert)
	        .setTitle("Izlaz")
	        .setMessage("Da li želite da izađete iz aplikacije?")
	        .setPositiveButton("Da", new DialogInterface.OnClickListener() {

	            @Override
	            public void onClick(DialogInterface dialog, int which) {
	                //Stop the activity
	            	VaultActivity.this.onDestroy();
	                VaultActivity.this.finish();
	            }

	        })
	        .setNegativeButton("Ne", null)
	        .show();

	        return true;
	    }else {
	        return super.onKeyDown(keyCode, event);
	    }
	}
	
	public void drawItems() {
		GridView main = (GridView)findViewById(R.id.lytMain);
		main.setAdapter(new VaultItemAdapter(this, items));

	    main.setOnItemClickListener(new AdapterView.OnItemClickListener() {
	        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
	            VaultItem item = new VaultItem("", "");
				try {
					item = database.getOne(items.get(position).key);
				} catch (Exception e) {
					e.printStackTrace();
				}
	            
				Intent itemActivity = new Intent(getBaseContext(),VaultItemActivity.class);
				itemActivity.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
	            itemActivity.putExtra("itemKey", item.key);
	            itemActivity.putExtra("itemValue", item.value);
	            startActivity(itemActivity);
	        }
	    });
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		
		MenuItem mnuSettings = menu.add("Podešavanja").setIcon(android.R.drawable.ic_menu_preferences);
		mnuSettings.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				Intent settingsActivity = new Intent(getBaseContext(),Preferences.class);
		        startActivity(settingsActivity);
				return false;
			}
		});
		
		SubMenu mnuCreate = menu.addSubMenu("Dodaj");
		mnuCreate.setIcon(android.R.drawable.ic_menu_add);
		mnuCreate.setHeaderIcon(android.R.drawable.ic_menu_add);
		
		MenuItem subCreateNew = mnuCreate.add("Nov tekst");
		subCreateNew.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				Intent itemActivity = new Intent(getBaseContext(),VaultItemActivity.class);
				itemActivity.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		        itemActivity.putExtra("itemKey", "__NEW__");
		        itemActivity.putExtra("itemValue", "");
		        startActivity(itemActivity);
				return false;
			}
		});
		MenuItem subAddExt = mnuCreate.add("Postojeći fajl");
		subAddExt.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				Intent ChooseActivity = new Intent(getBaseContext(),FileChoose.class);
				ChooseActivity.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		        //startActivity(ChooseActivity);
		        startActivityForResult(ChooseActivity, requestCode);
				return false;
			}
		});
		
		MenuItem mnuExit = menu.add("Izlaz").setIcon(android.R.drawable.ic_lock_power_off);
		mnuExit.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				onDestroy();
				finish();
				return false;
			}
		});
		
		return true;
	}
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intnt) {
        super.onActivityResult(requestCode, resultCode, intnt);
        if(resultCode==1){
        	Bundle xtras = intnt.getExtras();
        	String name = xtras.getString("name");
        	String data = xtras.getString("data");
        	String path = xtras.getString("path");
        	
        	//TODO: what to do with File selected?
        	Toast.makeText(this, "File: " + path, Toast.LENGTH_LONG).show();
        }
    }
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		
		if(isAuthenticated()) {
			menu.getItem(0).setVisible(true);
			menu.getItem(1).setVisible(true);
		}else{
			menu.getItem(0).setVisible(false);
			menu.getItem(1).setVisible(false);
		}
		
		return true;
	}
    
    
	public String getPref(String key) {
		return prefs.getString(key, "");
	}
	public void setPref(String key, String value) {
		SharedPreferences.Editor edit = prefs.edit();
		edit.putString(key, value);
		edit.commit();
	}
	
	public void savePassword(View v) {
		String pass1 = ((EditText)findViewById(R.id.txtPassword)).getText().toString();
		String pass2 = ((EditText)findViewById(R.id.txtPassword2)).getText().toString();
		
		if(pass1.length()==0 || pass2.length()==0) {
			alert("Morate uneti lozinku u oba polja.");
		}else if(!pass1.equals(pass2)) {
			alert("Lozinke se ne podudaraju.");
		}else{
			setPref("password", pass1);
			InputMethodManager imm = (InputMethodManager)getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
    		imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
			
			init(bundle);
		}
	}
	
	public VaultApplication getVaultApplication() {
		return (VaultApplication)getApplication();
	}
    
    public boolean isAuthenticated() {
    	return getVaultApplication().isAuthenticated();
    }
    public void authenticate(View v) {
    	String prefPass = getPref("password");
    	
    	String loginPass = ((EditText)findViewById(R.id.txtLoginPassword)).getText().toString();
    	
    	if(prefPass.equals(loginPass)) {
    		getVaultApplication().setAuth(true);
    		InputMethodManager imm = (InputMethodManager)getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
    		imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    	}else{
    		alert("Prijava neuspela!\nPokušajte ponovo.");
    	}
    	init(bundle);
    }
    
    public void alert(String message) {
    	Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }
    
    public void loginKeyboardClick(View v) {
    	Button b = (Button)v;
    	EditText pass = (EditText)findViewById(R.id.txtLoginPassword);
    	String txt = b.getText().toString().toLowerCase();
    	String passTxt = pass.getText().toString();
    	
    	int num;
    	try {
    		num = Integer.parseInt(txt);
    	}catch(Exception e) {
    		num = 0;
    	}
    	
    	
    	switch(num) {
    		case 0:
    			if(txt.equals("c")) {
    				pass.setText("");
    			}else if(txt.equals("ok")) {
    				authenticate(v);
    			}else{
    				pass.setText(passTxt + "0");
    			}
    			break;
    		default:
    			pass.setText(passTxt + num);
    			break;
    	}
    	
    }

}