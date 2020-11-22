package com.ugex.savelar.transactionhelper.Util;

import android.content.Context;
import android.widget.TextView;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ActivityHelper {
    public static final String EXTRA_KEY_ACCOUNT="account";
    public static final String KEY_REQUEST_ITEM_ID="require_item_id";
    public static final String KEY_ID="key_id";

    public static final String SEX_MAN="男";
    public static final String SEX_WOMAN="女";

    public static String getTrimedFromView(TextView view){
        return view.getText().toString().trim();
    }
    public static void cleanAutoLogin(Context ctx){
        Map<String,String> info=new HashMap<>();
        info.put("account","");
        info.put("password","");
        info.put("legaltime","");
        SharePfHelper.SaveTo(ctx,"autologin",info);
    }
}
