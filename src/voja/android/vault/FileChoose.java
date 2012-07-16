package voja.android.vault;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.R.anim;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class FileChoose extends ListActivity {
	
	private File currentDir;
	
	private static final String DIR_BASE = "/sdcard"; 
	
	private class Option implements Comparable<Option> {
		
		public String name, path, data;
		public boolean isDir=false;
		
		public Option(String n, String d, String p, boolean dir) {
			name = n;
			data = d;
			path = p;
			isDir = dir;
		}
		
		@Override
		public int compareTo(Option another) {
			if(this.name != null)
				return this.name.toLowerCase().compareTo(another.name.toLowerCase());
			else
				throw new IllegalArgumentException();
		}
		
	};
	
	public class FileArrayAdapter extends ArrayAdapter<Option> {

		private Context c;
		private int id;
		private List<Option> items;
		
		public FileArrayAdapter(Context context, int textViewResourceId, List<Option> objects) {
			super(context, textViewResourceId, objects);
			
			c = context;
			id = textViewResourceId;
			items = objects;
		}
		
		public Option getItem(int i) {
			return items.get(i);
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			if (v == null) {
				LayoutInflater vi = (LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(id, null);
			}
			final Option o = items.get(position);
			if (o != null) {
				ImageView icon = (ImageView) v.findViewById(R.id.icon);
				TextView tName = (TextView) v.findViewById(R.id.txtName);
				TextView tSize = (TextView) v.findViewById(R.id.txtSize);
				
				if(icon!=null){
					if(o.isDir) {
						icon.setImageResource(R.drawable.folder);
					}else{
						icon.setImageResource(R.drawable.file);
					}
				}
				
				if(tName!=null){
					tName.setText(o.name);
				}
				if(tSize!=null){
					tSize.setText(o.data);
				}
			}
			return v;
		}
		
	}
	
	public FileArrayAdapter adapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		currentDir = new File(DIR_BASE);
		fill(currentDir);
	}
	
	private void fill(File dir) {
		File[] files = dir.listFiles();
		this.setTitle(dir.getName());
		
		List<Option> dirs = new ArrayList<Option>();
		List<Option> fils = new ArrayList<Option>();
		
		try {
			for(File ff : files) {
				if(ff.isDirectory()) {
					dirs.add(new Option(ff.getName(), "<DIR>", ff.getAbsolutePath(), true));
				}else{
					fils.add(new Option(ff.getName(), toBytes(ff.length()), ff.getAbsolutePath(), false));
				}
			}
		}catch(Exception e) {}
		
		Collections.sort(dirs);
		Collections.sort(fils);
		
		dirs.addAll(fils);
		if(!dir.getAbsolutePath().equalsIgnoreCase(DIR_BASE)) {
			dirs.add(0, new Option("..", "", dir.getParent(), true));
		}
		
		adapter = new FileArrayAdapter(FileChoose.this, R.layout.file_view, dirs);
		this.setListAdapter(adapter);
	}
	
	private String toBytes(long size) {
		String ret = size+" B";
		if(size>1024*1024) {
			ret = String.format("%.2f MB", size/(1024*1024));
		}else if(size>1024) {
			ret = String.format("%.2f KB", size/(1024));
		}
		return ret;
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
		Option o = adapter.getItem(position);
		if(o.data.equalsIgnoreCase("<DIR>")||o.name.equalsIgnoreCase("..")){
				currentDir = new File(o.path);
				fill(currentDir);
		}else{
			onFileClick(o);
		}
	}
	
	private void onFileClick(Option o){
		Intent i = new Intent();
		i.putExtra("path", o.path);
		i.putExtra("data", o.data);
		i.putExtra("name", o.name);
		setResult(1, i);
    	finish();
    }

}
