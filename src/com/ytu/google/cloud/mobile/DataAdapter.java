package com.ytu.google.cloud.mobile;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class DataAdapter extends BaseAdapter{

	private Context context;
	private List<FileOop> file_items = new ArrayList<FileOop>();
	
	public DataAdapter(Context mContext){
	this.context = mContext;
	}
	public void addItem(FileOop file){
		file_items.add(file);
	}
	public void setListItems(List<FileOop> list){
		file_items = list;
	}
	public boolean areAllItemsSelectable(){
		return false;
	}
	public boolean isSelectable(int pos){
		return file_items.get(pos).isSelectable();
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return file_items.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return file_items.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		 ListLayout lyt ;
		if( convertView == null)
			lyt = new ListLayout(context, file_items.get(position));
		else {
			lyt = (ListLayout) convertView;
			lyt.setText(file_items.get(position).getFileName());
			lyt.setIcon(file_items.get(position).getIcon());
		}
			
		return lyt;
	}
	
}
