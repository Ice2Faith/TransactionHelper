package com.ugex.savelar.transactionhelper.Util;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ListObjectAdapter extends BaseAdapter {
    public interface OnRequeryView{
        View getView(Object obj,int posotion,Context ctx,View convertView);
    }
    public OnRequeryView requeryView=new OnRequeryView() {
        @Override
        public View getView(Object obj, int posotion, Context ctx, View convertView) {
            TextView textView=null;
            if(convertView==null) {
                textView=new TextView(context);
            }else{
                textView=(TextView) convertView;
            }
            textView.setText(obj.toString());
            return textView;
        }
    };
    public List<? extends Object> data=new ArrayList<>();
    private Context context;
    public ListObjectAdapter(Context ctx){
        this.context=ctx;
    }
    public ListObjectAdapter(Context ctx,List<? extends  Object> data){
        this.context=ctx;
        this.data=data;
    }
    public ListObjectAdapter(Context ctx,List<? extends  Object> data,OnRequeryView requeryView){
        this.context=ctx;
        this.data=data;
        this.requeryView=requeryView;
    }
    public void SetData(List<? extends Object> data){
        this.data=data;
    }
    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return requeryView.getView(data.get(position),position,context,convertView);
    }
}
