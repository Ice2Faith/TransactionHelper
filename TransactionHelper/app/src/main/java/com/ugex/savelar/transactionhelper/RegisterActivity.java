package com.ugex.savelar.transactionhelper;

import androidx.appcompat.app.AppCompatActivity;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.ugex.savelar.transactionhelper.Entity.SysUser;
import com.ugex.savelar.transactionhelper.Util.ActivityHelper;
import com.ugex.savelar.transactionhelper.Util.StringHelper;

public class RegisterActivity extends Activity {
    private EditText edtAccount;
    private EditText edtPassword;
    private EditText edtRePassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        InitActivity();
    }

    private void InitActivity() {
        edtAccount=findViewById(R.id.editTextAccount);
        edtPassword=findViewById(R.id.editTextPassword);
        edtRePassword=findViewById(R.id.editTextRepeatPassword);
    }

    public void OnClickedRegisterButton(View view) {
        String account= ActivityHelper.getTrimedFromView(edtAccount);
        String password=ActivityHelper.getTrimedFromView(edtPassword);
        String repassword=ActivityHelper.getTrimedFromView(edtRePassword);
        if(StringHelper.isEmptyNull(account,true) || StringHelper.isEmptyNull(password,true) || StringHelper.isEmptyNull(repassword,true)){
            Toast.makeText(this, "请填写完整登录信息", Toast.LENGTH_SHORT).show();
            return;
        }
        if(password.equals(repassword)==false){
            Toast.makeText(this, "两次输入密码不一致", Toast.LENGTH_SHORT).show();
            return;
        }
        SysUser user=new SysUser(account);
        user.setPassword(password);
        user.signUp(new SaveListener<SysUser>() {
            @Override
            public void done(SysUser sysUser, BmobException e) {
                registerDone(sysUser,e);
            }
        });
    }
    private void registerDone(SysUser user,BmobException e){
        if(e==null){
            Toast.makeText(this, "注册成功", Toast.LENGTH_SHORT).show();
            Intent intent=new Intent(this, LoginActivity.class);
            intent.putExtra(ActivityHelper.EXTRA_KEY_ACCOUNT,user.getUsername());
            startActivity(intent);
            finish();
        }else{
            Toast.makeText(this, "注册失败，请检查用户名或密码是否正确", Toast.LENGTH_SHORT).show();
        }
    }

    public void OnClickedGoLoginTextView(View view) {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}
