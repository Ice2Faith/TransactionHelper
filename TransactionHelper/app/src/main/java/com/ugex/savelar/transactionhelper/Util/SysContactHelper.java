package com.ugex.savelar.transactionhelper.Util;

import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.ContactsContract;

import java.util.ArrayList;
import java.util.List;

public class SysContactHelper {
    //联系人实体类
    public static class ContactUser{
        public int _id;
        public String displayName;
        public String tel;
        public ContactUser(){}
        public ContactUser(int id,String name,String tel){
            this._id=id;
            this.displayName=name;
            this.tel=tel;
        }
    }

    //使用老方式进行读取
    public static  List<ContactUser> getContactsList(ContentResolver cr) {
        List<ContactUser> userList=new ArrayList<>();

        //查询所有联系人名称表
        Cursor curName=cr.query(ContactsContract.Contacts.CONTENT_URI,
                null,null,null,null);
        //遍历结果
        while(curName.moveToNext()){
            //获取该联系人名对应的ID
            String cid=curName.getString(curName.getColumnIndex(ContactsContract.Contacts._ID));
            //根据ID去查询电话表
            Cursor curTel=cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID+"=?",
                    new String[]{cid},
                    null);
            //遍历这个联系人的所有电话结果
            while(curTel.moveToNext()){
                //获取联系人的电话并创建实体对象加入List
                String cphone=curTel.getString(curTel.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                ContactUser user=new ContactUser(Integer.parseInt(cid),
                        curName.getString(curName.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)),
                        cphone);
                userList.add(user);
            }
        }
        return userList;
    }
}
