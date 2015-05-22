package com.cgi.activities;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cgi.gaswarnings.R;

import data.Warning;

public class WarningsAdapter extends BaseAdapter {

	private List<Warning> list;
	private LayoutInflater inflater = null;
	private Activity context;
	Typeface face;
	DateFormat df = new SimpleDateFormat("HH:mm:ss-dd/MM/yyyy");
	
	public WarningsAdapter(Activity context, List<Warning> list){
		this.list = list;
		this.context = context;
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		face = Typeface.createFromAsset(context.getAssets(), "fonts/LinLibertine_R.ttf");
	}
	
	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int arg0) {
		return list.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v  = convertView;
		ViewHolder holder;
		if(convertView == null){
			LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = li.inflate(R.layout.warning_item, null);
			holder = new ViewHolder(v);
			v.setTag(holder);
		}
		else{
			holder = (ViewHolder) v.getTag();
		}
		Warning w = list.get(position);
		holder.item.setTypeface(face);
		String s = "Start : ";
		s += df.format(w.getStart());
		s += ". End : ";
		String end;
		try{
			 end = df.format(w.getEnd());
		}
		catch(Exception e){
			end = "STILL ON";
		}
		s += end;
		s += ".";
		
		holder.item.setText(s);
		if(w.getEnd() == null){
			holder.item.setBackgroundResource(R.drawable.red_button);
		}
		else{
			holder.item.setBackgroundResource(R.drawable.green_button);
		}
		return v;
	}

}

class ViewHolder {
	public TextView item;
	public ViewHolder(View base){
		item = (TextView) base.findViewById(R.id.activity_warnings_list_item);
	}
}
