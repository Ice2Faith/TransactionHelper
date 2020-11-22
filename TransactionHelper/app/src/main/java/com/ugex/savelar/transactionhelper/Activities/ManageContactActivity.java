package com.ugex.savelar.transactionhelper.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.ugex.savelar.transactionhelper.Entity.ContactMap;
import com.ugex.savelar.transactionhelper.Entity.SysUser;
import com.ugex.savelar.transactionhelper.R;
import com.ugex.savelar.transactionhelper.Util.ActivityHelper;
import com.ugex.savelar.transactionhelper.Util.StringHelper;
import com.ugex.savelar.transactionhelper.Util.SysContactHelper;

import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class ManageContactActivity extends Activity {
    private SysUser user;
    private EditText tvNickName;
    private EditText tvTel1;
    private EditText tvTel2;
    private EditText tvEmail;
    private EditText tvQQ;
    private EditText tvWechat;
    private EditText tvAddress;
    private EditText tvOther;
    private ContactMap currentContact;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_contact);

        InitActivity();
    }

    private void InitActivity() {
        String account=getIntent().getStringExtra(ActivityHelper.EXTRA_KEY_ACCOUNT);
        if(BmobUser.isLogin()){
            user=BmobUser.getCurrentUser(SysUser.class);
        }

        tvNickName =findViewById(R.id.textViewNickName);
        tvTel1 =findViewById(R.id.textViewTel1);
        tvTel2=findViewById(R.id.textViewTel2);
        tvEmail=findViewById(R.id.textViewEmail);
        tvQQ=findViewById(R.id.textViewQQ);
        tvWechat=findViewById(R.id.textViewWechat);
        tvAddress=findViewById(R.id.textViewAddress);
        tvOther=findViewById(R.id.textViewOther);
    }


    public void OnClickedAdd(View view) {
        currentContact =new ContactMap();
        currentContact.owner= StringHelper.encrypt(user.getUsername());
        currentContact.name=StringHelper.encrypt(ActivityHelper.getTrimedFromView(tvNickName));
        currentContact.tel1=StringHelper.encrypt(ActivityHelper.getTrimedFromView(tvTel1));
        currentContact.tel2=StringHelper.encrypt(ActivityHelper.getTrimedFromView(tvTel2));
        currentContact.email=StringHelper.encrypt(ActivityHelper.getTrimedFromView(tvEmail));
        currentContact.qq=StringHelper.encrypt(ActivityHelper.getTrimedFromView(tvQQ));
        currentContact.wechat=StringHelper.encrypt(ActivityHelper.getTrimedFromView(tvWechat));
        currentContact.address=StringHelper.encrypt(ActivityHelper.getTrimedFromView(tvAddress));
        currentContact.other=StringHelper.encrypt(ActivityHelper.getTrimedFromView(tvOther));
        currentContact.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if(e==null){
                    Toast.makeText(ManageContactActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(ManageContactActivity.this, "保存失败："+e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void OnClickedDelete(View view) {
        if(currentContact ==null){
            Toast.makeText(this, "删除失败，请先选择或者添加", Toast.LENGTH_SHORT).show();
            return;
        }
        currentContact.delete(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e==null){
                    Toast.makeText(ManageContactActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(ManageContactActivity.this, "删除失败："+e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void OnCLickedUpdate(View view) {
        if(currentContact ==null){
            Toast.makeText(this, "更新失败，请先选择或者添加", Toast.LENGTH_SHORT).show();
            return;
        }
        currentContact.name=StringHelper.encrypt(ActivityHelper.getTrimedFromView(tvNickName));
        currentContact.tel1=StringHelper.encrypt(ActivityHelper.getTrimedFromView(tvTel1));
        currentContact.tel2=StringHelper.encrypt(ActivityHelper.getTrimedFromView(tvTel2));
        currentContact.email=StringHelper.encrypt(ActivityHelper.getTrimedFromView(tvEmail));
        currentContact.qq=StringHelper.encrypt(ActivityHelper.getTrimedFromView(tvQQ));
        currentContact.wechat=StringHelper.encrypt(ActivityHelper.getTrimedFromView(tvWechat));
        currentContact.address=StringHelper.encrypt(ActivityHelper.getTrimedFromView(tvAddress));
        currentContact.other=StringHelper.encrypt(ActivityHelper.getTrimedFromView(tvOther));
        currentContact.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e==null){
                    Toast.makeText(ManageContactActivity.this, "更新成功", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(ManageContactActivity.this, "更新失败："+e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    public void OnClickedQuery(View view) {
        Intent intent=new Intent(this, ViewContactActivity.class);
        intent.putExtra(ActivityHelper.EXTRA_KEY_ACCOUNT,user.getUsername());
        intent.putExtra(ActivityHelper.KEY_REQUEST_ITEM_ID,"require");
        startActivityForResult(intent,0x101);
    }

    private void resultSelected(String id){
        currentContact =new ContactMap();
        currentContact.setObjectId(id);
        BmobQuery<ContactMap> query=new BmobQuery<>();
        query.getObject(currentContact.getObjectId(), new QueryListener<ContactMap>() {
            @Override
            public void done(ContactMap contactMap, BmobException e) {
                if(e!=null){
                    currentContact =null;
                }else{
                    currentContact =contactMap;
                    tvNickName.setText(StringHelper.decrypt(currentContact.name));
                    tvTel1.setText(StringHelper.decrypt(currentContact.tel1));
                    tvTel2.setText(StringHelper.decrypt(currentContact.tel2));
                    tvEmail.setText(StringHelper.decrypt(currentContact.email));
                    tvQQ.setText(StringHelper.decrypt(currentContact.qq));
                    tvWechat.setText(StringHelper.decrypt(currentContact.wechat));
                    tvAddress.setText(StringHelper.decrypt(currentContact.address));
                    tvOther.setText(StringHelper.decrypt(currentContact.other));
                }
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode==0x101 && resultCode==0x101){
            resultSelected(data.getStringExtra(ActivityHelper.KEY_ID));
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private int preImportDoneCount =0;
    private int successImportCount =0;
    public void OnClickedImportContactTextView(View view){
        final List<SysContactHelper.ContactUser> contactUserList=SysContactHelper.getContactsList(getContentResolver());
        Toast.makeText(this, "正在导入通讯录：共"+contactUserList.size()+" 过程中请不要关闭程序，以免数据丢失或者发生错误", Toast.LENGTH_LONG).show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                preImportDoneCount =0;
                successImportCount=0;
                for(SysContactHelper.ContactUser u : contactUserList){
                    ContactMap p=new ContactMap();
                    p.owner=StringHelper.encrypt(user.getUsername());
                    p.name=StringHelper.encrypt(u.displayName);
                    p.tel1=StringHelper.encrypt(u.tel);
                    p.save(new SaveListener<String>() {
                        @Override
                        public void done(String s, BmobException e) {
                            preImportDoneCount++;
                            if(e==null){
                                successImportCount++;
                                Toast.makeText(ManageContactActivity.this, "导入联系人，成功", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(ManageContactActivity.this, "导入联系人，失败", Toast.LENGTH_SHORT).show();
                            }
                            if(preImportDoneCount ==contactUserList.size()){
                                Toast.makeText(ManageContactActivity.this, "所有联系人导入完毕，总："+contactUserList.size()+
                                        " 成功："+successImportCount+" 失败："+(contactUserList.size()-successImportCount),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        }).start();
    }
}
