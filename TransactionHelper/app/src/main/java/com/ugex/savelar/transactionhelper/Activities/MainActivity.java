package com.ugex.savelar.transactionhelper.Activities;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ugex.savelar.transactionhelper.Entity.FileMap;
import com.ugex.savelar.transactionhelper.Entity.SysUser;
import com.ugex.savelar.transactionhelper.LoginActivity;
import com.ugex.savelar.transactionhelper.R;
import com.ugex.savelar.transactionhelper.Util.ActivityHelper;
import com.ugex.savelar.transactionhelper.Util.ItemHelper;
import com.ugex.savelar.transactionhelper.Util.ListObjectAdapter;
import com.ugex.savelar.transactionhelper.Util.StringHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
    private SysUser user;
    private ListView lsvMain;
    private ListObjectAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        InitActivity();
    }

    private void InitActivity() {
        String account=getIntent().getStringExtra(ActivityHelper.EXTRA_KEY_ACCOUNT);
        if(BmobUser.isLogin()){
            user=BmobUser.getCurrentUser(SysUser.class);
        }
        if(StringHelper.isEmptyNull(account,true) || user==null){
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

        lsvMain=findViewById(R.id.listViewMain);

        List<ItemHelper> data=new ArrayList<>();
        adapter=new ListObjectAdapter(this, data, new ListObjectAdapter.OnRequeryView() {
            @Override
            public View getView(Object obj, int posotion, Context ctx, View convertView) {
                View view=null;
                if(convertView==null){
                    view= LayoutInflater.from(ctx).inflate(R.layout.item_list_view_main,null);
                }else{
                    view=convertView;
                }
                TextView tvTitle=view.findViewById(R.id.textViewTitle);
                TextView tvDescript=view.findViewById(R.id.textViewDescript);
                ImageView ivIcon=view.findViewById(R.id.imageViewIcon);
                ItemHelper helper=(ItemHelper)obj;
                tvTitle.setText(helper.title);
                tvDescript.setText(helper.descript);
                ivIcon.setImageResource(helper.imgId);
                return view;
            }
        });
        lsvMain.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ((ItemHelper)adapter.getItem(position)).generateDoIt();
            }
        });
        lsvMain.setAdapter(adapter);

        ItemHelper item=null;
        Intent intent=null;
        ItemHelper.DoSomething doSomething=null;

        intent=new Intent(this,PrivateInfoActivity.class);
        intent.putExtra(ActivityHelper.EXTRA_KEY_ACCOUNT,user.getUsername());
        item=new ItemHelper(this,intent,"个人信息",R.mipmap.ic_launcher);
        data.add(item);

        intent=new Intent(this,ManageNoteActivity.class);
        intent.putExtra(ActivityHelper.EXTRA_KEY_ACCOUNT,user.getUsername());
        item=new ItemHelper(this,intent,"随手记",R.mipmap.ic_launcher);
        data.add(item);

        intent=new Intent(this,ManagePasswordActivity.class);
        intent.putExtra(ActivityHelper.EXTRA_KEY_ACCOUNT,user.getUsername());
        item=new ItemHelper(this,intent,"密码册",R.mipmap.ic_launcher);
        data.add(item);

        intent=new Intent(this,ManageContactActivity.class);
        intent.putExtra(ActivityHelper.EXTRA_KEY_ACCOUNT,user.getUsername());
        item=new ItemHelper(this,intent,"通讯录",R.mipmap.ic_launcher);
        data.add(item);

        intent=new Intent(this,ManageFriendActivity.class);
        intent.putExtra(ActivityHelper.EXTRA_KEY_ACCOUNT,user.getUsername());
        item=new ItemHelper(this,intent,"好友圈",R.mipmap.ic_launcher);
        data.add(item);

        intent=new Intent(this,MessageCommunicationActivity.class);
        intent.putExtra(ActivityHelper.EXTRA_KEY_ACCOUNT,user.getUsername());
        item=new ItemHelper(this,intent,"聊天吧",R.mipmap.ic_launcher);
        data.add(item);

        doSomething=new ItemHelper.DoSomething() {
            @Override
            public void doIt() {
                ActivityHelper.cleanAutoLogin(MainActivity.this);
                MainActivity.this.finishAffinity();
                MainActivity.this.startActivity(new Intent(MainActivity.this,LoginActivity.class));
            }
        };
        item=new ItemHelper(this,doSomething);
        item.title="注销登录";
        item.imgId=R.mipmap.ic_launcher;
        data.add(item);

        adapter.notifyDataSetChanged();

        //文件上传下载测试，已经失败,原因未知，错误码也无法查询到,代码逻辑也没有错
        //debuginfo: 上传失败：errorCode:9015,errorMsg:cn.bmob.v3.http.This$5.subscribe(BmobClient.java:521)
       // fileUpDownTest();
    }

    private void fileUpDownTest() {
        final FileMap file=new FileMap();
        file.filename="testFile.mp4";
        file.descript="测试文件";
        File upfile=new File(Environment.getExternalStorageDirectory(),"test1.mp4");
        Log.i("debuginfo","upfileinfo:"+upfile.getAbsolutePath()+" isFile:"+upfile.isFile()+" size:"+upfile.length());
        file.file=new BmobFile(upfile);
        file.file.upload(new UploadFileListener() {
            @Override
            public void done(BmobException e) {
                if(e==null){
                    Log.i("debuginfo","文件上传成功");
                    file.save(new SaveListener<String>() {
                        @Override
                        public void done(String s, BmobException e) {
                            if(e==null){
                                Log.i("debuginfo","DB记录文件上传成功");
                                testDownloadFile(s);
                            }else{
                                Log.i("debuginfo","DB记录失败："+e.toString());
                            }
                        }
                    });
                }else{
                    Log.i("debuginfo","上传失败："+e.toString());
                }
            }
        });

    }
    private void testDownloadFile(String fileId){
        BmobQuery<FileMap> query=new BmobQuery<>();
        query.getObject(fileId, new QueryListener<FileMap>() {
            @Override
            public void done(FileMap fileMap, BmobException e) {
                if(e==null){
                    Log.i("debuginfo","DB记录文件读取成功");
                    BmobFile fp=fileMap.file;
                    fp.download(new File("/sdcard/", fileMap.filename), new DownloadFileListener() {
                        @Override
                        public void done(String s, BmobException e) {
                            if(e==null){
                                Log.i("debuginfo","文件下载成功");
                                testDownloadFile(s);
                            }else{
                                Log.i("debuginfo","下载失败："+e.toString());
                            }
                        }

                        @Override
                        public void onProgress(Integer integer, long l) {

                        }
                    });
                }else{
                    Log.i("debuginfo","DB记录读取失败："+e.toString());
                }
            }
        });
    }
}
