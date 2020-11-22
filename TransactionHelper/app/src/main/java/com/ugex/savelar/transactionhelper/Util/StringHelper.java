package com.ugex.savelar.transactionhelper.Util;

public class StringHelper {
    public static boolean isEmptyNull(String str,boolean needTrim){
        if(str==null)
            return true;
        if(needTrim && "".equals(str.trim())){
            return true;
        }
        if("".equals(str.trim())){
            return true;
        }
        return false;
    }
    public static String getSafe(String str,boolean needTrim) {
        if (str == null)
            return "";
        if (needTrim)
            return str.trim();
        return str;
    }

    private static final Base64Ex base64=new Base64Ex("QWERTYUIOPASDFGHJKLZXCVBNM6@8#0$2%4&zxcvbnmasdfghjklqwertyuiop/+",Base64Ex.OT_NormalString);
    public static String encrypt(String str){
        if(isEmptyNull(str,false))
            return "";
        return base64.DataToBase64(str.getBytes());
    }
    public static String decrypt(String str){
        if(isEmptyNull(str,false))
            return "";
        return new String(base64.Base64ToData(str));
    }
}
