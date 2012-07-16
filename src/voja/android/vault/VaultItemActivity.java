package voja.android.vault;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class VaultItemActivity extends Activity {
	
	String key;
	String value;
	
	Sqlite database;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item);
        
        database = VaultActivity.database;
        
        Bundle xtras = getIntent().getExtras();
        key = xtras.getString("itemKey");
        value = xtras.getString("itemValue");
        
        TextView l_title = (TextView)findViewById(R.id.lblTitle);
        TextView t_title = (EditText)findViewById(R.id.txtTitle);
        Button b_del = (Button)findViewById(R.id.btnDelete);
        
        if(key.equals("__NEW__")) {
        	l_title.setVisibility(View.GONE);
        	b_del.setVisibility(View.GONE);
        	t_title.setVisibility(View.VISIBLE);
        	t_title.requestFocus();
        	t_title.setText("");
        }else{
        	l_title.setVisibility(View.VISIBLE);
        	b_del.setVisibility(View.VISIBLE);
        	t_title.setVisibility(View.GONE);
        	l_title.setText(key);
        }
        
        EditText content = (EditText)findViewById(R.id.txtContent);
        content.setText(value);
	}
	
	public void save(View v){
		if(!key.equals("")) {
			String value = ((EditText)findViewById(R.id.txtContent)).getText().toString();
			
			try {
				value = SimpleCrypto.encrypt(value);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			if(key.equals("__NEW__")) {
				String k = ((EditText)findViewById(R.id.txtTitle)).getText().toString();
				
				try {
					if(!k.equals("")) {
						database.db.execSQL("INSERT INTO data(value,key) VALUES('"+value+"','"+k+"')");
					}else{
						Toast.makeText(getApplicationContext(), "Unesite naziv zabeleške.", Toast.LENGTH_LONG).show();
						return;
					}
				}catch(Exception e) {
					Toast.makeText(getApplicationContext(), "Greška. Duplirano ime zabeleške.", Toast.LENGTH_LONG).show();
					return;
				}
			}else{
				database.db.execSQL("UPDATE data SET value='"+value+"' WHERE key='"+key+"'");
			}
		}
		Toast.makeText(getApplicationContext(), "Sačuvano", Toast.LENGTH_LONG).show();
		goMain();
	}

	public void delete(View v) {
		
		new AlertDialog.Builder(this)
	        .setIcon(android.R.drawable.ic_dialog_alert)
	        .setTitle("Izlaz")
	        .setMessage("Da li želite da obrišete stavku '"+key+"'?")
	        .setPositiveButton("Da", new DialogInterface.OnClickListener() {
	
	            @Override
	            public void onClick(DialogInterface dialog, int which) {
	            	if(!key.equals("")) {
	        			database.db.execSQL("DELETE FROM data WHERE key='"+key+"'");
	        		}
	        		Toast.makeText(getApplicationContext(), "Obrisano", Toast.LENGTH_LONG).show();
	        		goMain();
	            }
	
	        })
	        .setNegativeButton("Ne", null)
	        .show();
	}
	
	public void goMain() {
		Intent i = new Intent(getBaseContext(),VaultActivity.class);
		i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		i.putExtra("forceAuth", 1);
		startActivity(i);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    //Handle the back button
	    if(keyCode == KeyEvent.KEYCODE_BACK) {
	        finish();
	        return true;
	    }else {
	        return super.onKeyDown(keyCode, event);
	    }
	}
}
