package com.ugex.savelar.transactionhelper.Entity;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;

public class SysUser extends BmobUser {
    public String nickName;
    public BmobFile photo;
    public String sex;
    public String brith;
    public String address;
    public String qq;
    public String wechat;
    public String tel1;
    public String tel2;
    public String introduce;

    public SysUser(){}
    public SysUser(String account){
        this.setUsername(account);
    }

}
