package voja.android.vault;

import java.io.IOException;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class VaultItemAdapter extends BaseAdapter {

	private Context mContext;
	private List<VaultItem> items;

    public VaultItemAdapter(Context c, List<VaultItem> items) {
        mContext = c;
        this.items = items;
    }
    
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return items.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return items.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return items.get(position).hashCode();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LinearLayout lyt = new LinearLayout(mContext);
		
		Resources mRes = mContext.getResources();
		try {
			lyt.setBackgroundDrawable(Drawable.createFromXml(mRes, mRes.getXml(R.drawable.item)));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (convertView == null) {  // if it's not recycled, initialize some attributes
			
			VaultItem item = items.get(position);
			TextView tvTitle = new TextView(mContext);
			TextView tvContent = new TextView(mContext);
			String key=item.key, value=item.value;
			int titleLength = 15, contentLength = 100;
			
			if(key.length() > titleLength) {
				key = key.substring(0, titleLength) + "...";
			}
			if(value.length() > contentLength) {
				value = value.substring(0, contentLength) + "...";
			}
			
			//layout
			lyt.setOrientation(LinearLayout.VERTICAL);
			
			// title
			tvTitle.setGravity(Gravity.TOP);
			tvTitle.setTextColor(Color.BLACK);
			tvTitle.setPadding(5, 5, 5, 5);
			tvTitle.setText(key);
			tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
			lyt.addView(tvTitle);
			
			// content
			//tvContent.setLayoutParams(new GridView.LayoutParams(LayoutParams.FILL_PARENT, 100));
			tvContent.setGravity(Gravity.TOP);
			tvContent.setTextColor(Color.parseColor("#777777"));
			tvContent.setPadding(10, 0, 5, 5);
			tvContent.setText(value);
			tvContent.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 8);
			lyt.addView(tvContent);
			
        } else {
        	lyt = (LinearLayout) convertView;
        }

        return lyt;
	}

}
