package com.ugex.savelar.transactionhelper.Util;

import android.content.Context;
import android.content.Intent;

public class ItemHelper {
    public String title;
    public String descript;
    public int imgId;
    public Object obj;
    public Context context;
    public Intent intent;
    public DoSomething doSomething;
    public interface DoSomething {
        void doIt();
    }
    public ItemHelper(){}
    public ItemHelper(Context ctx,String title){
        this.context=ctx;
        this.title=title;
    }
    public ItemHelper(Context ctx,String title,int imgId){
        this.context=ctx;
        this.title=title;
        this.imgId=imgId;
    }
    public ItemHelper(Context ctx,Intent intent){
        this.context=ctx;
        this.intent=intent;
    }
    public ItemHelper(Context ctx,Intent intent,String title,int imgId){
        this.context=ctx;
        this.intent=intent;
        this.title=title;
        this.imgId=imgId;
    }
    public ItemHelper(Context ctx,DoSomething doSomething){
        this.context=ctx;
        this.doSomething=doSomething;
    }
    public void JumpToActivity(){
        if(context!=null)
            context.startActivity(intent);
    }
    public void DirectDoIt(){
        if(doSomething!=null)
            doSomething.doIt();
    }
    public void generateDoIt(){
        if(null!=doSomething){
            doSomething.doIt();
        }else if(context!=null){
            context.startActivity(intent);
        }
    }
}
