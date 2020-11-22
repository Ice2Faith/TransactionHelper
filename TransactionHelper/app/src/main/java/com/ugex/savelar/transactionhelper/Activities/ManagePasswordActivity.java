package com.ugex.savelar.transactionhelper.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.ugex.savelar.transactionhelper.Entity.PasswordMap;
import com.ugex.savelar.transactionhelper.Entity.SysUser;
import com.ugex.savelar.transactionhelper.R;
import com.ugex.savelar.transactionhelper.Util.ActivityHelper;
import com.ugex.savelar.transactionhelper.Util.StringHelper;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class ManagePasswordActivity extends Activity {
    private SysUser user;
    private EditText tvAccount;
    private EditText tvPassword;
    private EditText tvDescript;
    private EditText tvOther;
    private PasswordMap currentPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_password);

        InitActivity();
    }

    private void InitActivity() {
        String account=getIntent().getStringExtra(ActivityHelper.EXTRA_KEY_ACCOUNT);
        if(BmobUser.isLogin()){
            user=BmobUser.getCurrentUser(SysUser.class);
        }

        tvAccount =findViewById(R.id.textViewAccount);
        tvPassword =findViewById(R.id.textViewPassword);
        tvDescript=findViewById(R.id.textViewDescript);
        tvOther=findViewById(R.id.textViewOther);
    }


    public void OnClickedAdd(View view) {
        currentPassword =new PasswordMap();
        currentPassword.owner= StringHelper.encrypt(user.getUsername());
        currentPassword.account=StringHelper.encrypt(ActivityHelper.getTrimedFromView(tvAccount));
        currentPassword.password=StringHelper.encrypt(ActivityHelper.getTrimedFromView(tvPassword));
        currentPassword.descript =StringHelper.encrypt(ActivityHelper.getTrimedFromView(tvDescript));
        currentPassword.other =StringHelper.encrypt(ActivityHelper.getTrimedFromView(tvOther));
        currentPassword.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if(e==null){
                    Toast.makeText(ManagePasswordActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(ManagePasswordActivity.this, "保存失败："+e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void OnClickedDelete(View view) {
        if(currentPassword ==null){
            Toast.makeText(this, "删除失败，请先选择或者添加", Toast.LENGTH_SHORT).show();
            return;
        }
        currentPassword.delete(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e==null){
                    Toast.makeText(ManagePasswordActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(ManagePasswordActivity.this, "删除失败："+e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void OnCLickedUpdate(View view) {
        if(currentPassword ==null){
            Toast.makeText(this, "更新失败，请先选择或者添加", Toast.LENGTH_SHORT).show();
            return;
        }
        currentPassword.account=StringHelper.encrypt(ActivityHelper.getTrimedFromView(tvAccount));
        currentPassword.password=StringHelper.encrypt(ActivityHelper.getTrimedFromView(tvPassword));
        currentPassword.descript =StringHelper.encrypt(ActivityHelper.getTrimedFromView(tvDescript));
        currentPassword.other =StringHelper.encrypt(ActivityHelper.getTrimedFromView(tvOther));
        currentPassword.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e==null){
                    Toast.makeText(ManagePasswordActivity.this, "更新成功", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(ManagePasswordActivity.this, "更新失败："+e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    public void OnClickedQuery(View view) {
        Intent intent=new Intent(this, ViewPasswordActivity.class);
        intent.putExtra(ActivityHelper.EXTRA_KEY_ACCOUNT,user.getUsername());
        intent.putExtra(ActivityHelper.KEY_REQUEST_ITEM_ID,"require");
        startActivityForResult(intent,0x101);
    }

    private void resultSelected(String id){
        currentPassword =new PasswordMap();
        currentPassword.setObjectId(id);
        BmobQuery<PasswordMap> query=new BmobQuery<>();
        query.getObject(currentPassword.getObjectId(), new QueryListener<PasswordMap>() {
            @Override
            public void done(PasswordMap passwordMap, BmobException e) {
                if(e!=null){
                    currentPassword =null;
                }else{
                    currentPassword =passwordMap;
                    tvAccount.setText(StringHelper.decrypt(currentPassword.account));
                    tvPassword.setText(StringHelper.decrypt(currentPassword.password));
                    tvDescript.setText(StringHelper.decrypt(currentPassword.descript));
                    tvOther.setText(StringHelper.decrypt(currentPassword.other));
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
}
