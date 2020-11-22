package com.ugex.savelar.transactionhelper.Entity;

import cn.bmob.v3.BmobObject;

public class NoteMap extends BmobObject {
    public String owner;
    public String title;
    public String info;
    public NoteMap(){}
    public NoteMap(String owner, String title, String info){
        this.owner=owner;
        this.title=title;
        this.info=info;
    }
}
