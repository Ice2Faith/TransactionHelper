package com.ugex.savelar.transactionhelper.Util;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SharePfHelper {
    public static void SaveTo(Context ctx,String spName, Map<String,String>values){
        SharedPreferences sp=ctx.getSharedPreferences(spName,ctx.MODE_PRIVATE);
        SharedPreferences.Editor edt=sp.edit();
        for(String key: values.keySet()){
            edt.putString(key,values.get(key));
        }
        edt.commit();
    }
    public static Map<String,String> readFrom(Context ctx, String spName, Set<String> keys,String defValue){
        Map<String,String> ret=new HashMap<>();
        SharedPreferences sp=ctx.getSharedPreferences(spName,ctx.MODE_PRIVATE);
        for(String key :keys){
            ret.put(key,sp.getString(key,defValue));
        }
        return ret;
    }
}
