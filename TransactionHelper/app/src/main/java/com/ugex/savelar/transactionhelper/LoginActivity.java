package com.ugex.savelar.transactionhelper;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.ugex.savelar.transactionhelper.Activities.MainActivity;
import com.ugex.savelar.transactionhelper.Entity.SysUser;
import com.ugex.savelar.transactionhelper.Util.ActivityHelper;
import com.ugex.savelar.transactionhelper.Util.SharePfHelper;
import com.ugex.savelar.transactionhelper.Util.StringHelper;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class LoginActivity extends Activity {
    private EditText edtAccount;
    private EditText edtPassword;
    private CheckBox ckbAutoLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        InitActivity();
    }

    private void InitActivity() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{
                    Manifest.permission.INTERNET,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.ACCESS_WIFI_STATE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_CONTACTS,
                    Manifest.permission.WAKE_LOCK
            },0x1001);
        }
        edtAccount=findViewById(R.id.editTextAccount);
        edtPassword=findViewById(R.id.editTextPassword);
        ckbAutoLogin=findViewById(R.id.checkBoxAutoLogin);

        Intent intent=getIntent();
        String account=intent.getStringExtra(ActivityHelper.EXTRA_KEY_ACCOUNT);
        if(StringHelper.isEmptyNull(account,true)==false){
            edtAccount.setText(account);
        }

        tryAutoLogin();
    }

    public void OnClickedLoginButton(View view) {
        String account= ActivityHelper.getTrimedFromView(edtAccount);
        String password=ActivityHelper.getTrimedFromView(edtPassword);
        if(StringHelper.isEmptyNull(account,true) || StringHelper.isEmptyNull(password,true)){
            Toast.makeText(this, "请填写完整登录信息", Toast.LENGTH_SHORT).show();
            return;
        }
        if(ckbAutoLogin.isChecked()){
            saveAutoLogin(account,password);
        }else{
            ActivityHelper.cleanAutoLogin(this);
        }
        login(account,password);
    }

    private void login(String account,String password){
        SysUser user=new SysUser(account);
        user.setPassword(password);
        user.login(new SaveListener<SysUser>() {
            @Override
            public void done(SysUser sysUser, BmobException e) {
                loginDone(sysUser,e);
            }
        });
    }

    private void loginDone(SysUser user,BmobException e){
        if(e==null){
            Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show();
            Intent intent=new Intent(this, MainActivity.class);
            intent.putExtra(ActivityHelper.EXTRA_KEY_ACCOUNT,user.getUsername());
            startActivity(intent);
            finish();
        }else{
            ActivityHelper.cleanAutoLogin(this);
            Toast.makeText(this, "登录失败，请检查用户名或密码是否正确", Toast.LENGTH_SHORT).show();
        }
    }

    public void OnClickedGoRegisterTextView(View view) {
        startActivity(new Intent(this,RegisterActivity.class));
        finish();
    }

    private void tryAutoLogin(){
        Set<String> keys=new HashSet<>();
        keys.add("account");
        keys.add("password");
        keys.add("legaltime");
        Map<String,String> info=SharePfHelper.readFrom(this,"autologin",keys,"");
        if(info.get("account").equals("")){
            return;
        }
        if(StringHelper.isEmptyNull(info.get("legaltime"),true)){
            return;
        }
        if(new Date().getTime()<Long.parseLong(info.get("legaltime"))) {
            edtAccount.setText(info.get("account"));
            edtPassword.setText(info.get("password"));
            Toast.makeText(this, "正在为您自动登录", Toast.LENGTH_SHORT).show();
            login(info.get("account"), info.get("password"));
        }
    }
    private void saveAutoLogin(String account,String password){
        Map<String,String> info=new HashMap<>();
        info.put("account",account);
        info.put("password",password);
        info.put("legaltime",(new Date().getTime()+7*24*60*60*1000)+"");
        SharePfHelper.SaveTo(this,"autologin",info);
    }

    public void OnClickedHelpTextView(View view){
        AlertDialog dlg=new AlertDialog.Builder(this)
                .setTitle("用户隐私协议")
                .setMessage("本程序系个人开发，基于Bmob后端云，所有隐私数据采用双重加密模式保存：\n" +
                        "\t1.Bmob后端云自己的数据安全保护\n" +
                        "\t2.本程序保存的所有数据进行自加密\n" +
                        "因此您可以放心使用本软件，不用担心数据泄露的问题，整个程序除了账号没有进行之外，其余所有数据都进行自加密，即使是聊天记录\n" +
                        "本软件并无任何盈利收入，且用且珍惜")
                .setIcon(R.mipmap.ic_launcher)
                .setNegativeButton("我已阅读",null)
                .create();
        dlg.show();
    }
}
