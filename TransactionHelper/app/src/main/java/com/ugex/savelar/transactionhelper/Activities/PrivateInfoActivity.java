package com.ugex.savelar.transactionhelper.Activities;

import androidx.appcompat.app.AppCompatActivity;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.ugex.savelar.transactionhelper.Entity.SysUser;
import com.ugex.savelar.transactionhelper.LoginActivity;
import com.ugex.savelar.transactionhelper.R;
import com.ugex.savelar.transactionhelper.Util.ActivityHelper;
import com.ugex.savelar.transactionhelper.Util.StringHelper;

public class PrivateInfoActivity extends Activity {
    private SysUser user;
    private EditText edtNickName;
    private RadioGroup rgpSex;
    private CheckBox ckbModifyPassword;
    private EditText edtPassword;
    private EditText edtRePassword;
    private EditText edtBrith;
    private EditText edtAddress;
    private EditText edtEmail;
    private EditText edtQQ;
    private EditText edtWechat;
    private EditText edtTel1;
    private EditText edtTel2;
    private EditText edtIntroduce;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_private_info);

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

        edtNickName=findViewById(R.id.editTextNickName);
        rgpSex=findViewById(R.id.radioGroupSex);
        ckbModifyPassword=findViewById(R.id.checkBoxModifyPassword);
        edtPassword=findViewById(R.id.editTextPassword);
        edtRePassword=findViewById(R.id.editTextRepeatPassword);
        edtBrith=findViewById(R.id.editTextBrith);
        edtAddress=findViewById(R.id.editTextAddress);
        edtEmail=findViewById(R.id.editTextEmail);
        edtQQ=findViewById(R.id.editTextQQ);
        edtWechat=findViewById(R.id.editTextWechat);
        edtTel1=findViewById(R.id.editTextTel1);
        edtTel2=findViewById(R.id.editTextTel2);
        edtIntroduce=findViewById(R.id.editTextIntroduce);

        showDataToView();
    }

    private void showDataToView() {
        edtNickName.setText(StringHelper.decrypt(user.nickName));
        String sex=StringHelper.decrypt(user.sex);
        if(ActivityHelper.SEX_MAN.equals(sex)){
            rgpSex.check(R.id.radioButtonMan);
        }else if(ActivityHelper.SEX_WOMAN.equals(sex)){
            rgpSex.check(R.id.radioButtonWoman);
        }
        edtBrith.setText(StringHelper.decrypt(user.brith));
        edtAddress.setText(StringHelper.decrypt(user.address));
        edtEmail.setText(StringHelper.getSafe(user.getEmail(),true));
        edtQQ.setText(StringHelper.decrypt(user.qq));
        edtWechat.setText(StringHelper.decrypt(user.wechat));
        edtTel1.setText(StringHelper.decrypt(user.tel1));
        edtTel2.setText(StringHelper.decrypt(user.tel2));
        edtIntroduce.setText(StringHelper.decrypt(user.introduce));
    }

    public void OnClickedUpdateButton(View view) {
        user.nickName=StringHelper.encrypt(ActivityHelper.getTrimedFromView(edtNickName));
        int sexid=rgpSex.getCheckedRadioButtonId();
        String sex="";
        switch (sexid){
            case R.id.radioButtonMan:
                sex=ActivityHelper.SEX_MAN;
                break;
            case R.id.radioButtonWoman:
                sex=ActivityHelper.SEX_WOMAN;
                break;
        }
        user.sex=StringHelper.encrypt(sex);
        if(ckbModifyPassword.isChecked()){
            String pwd=ActivityHelper.getTrimedFromView(edtPassword);
            String rpwd=ActivityHelper.getTrimedFromView(edtRePassword);
            if(pwd.equals(rpwd)){
                user.setPassword(pwd);
            }else{
                Toast.makeText(this, "两次密码不一致", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        user.brith=StringHelper.encrypt(ActivityHelper.getTrimedFromView(edtBrith));
        user.address=StringHelper.encrypt(ActivityHelper.getTrimedFromView(edtAddress));
        String email=ActivityHelper.getTrimedFromView(edtEmail);
        user.setEmail(email);
        user.qq=StringHelper.encrypt(ActivityHelper.getTrimedFromView(edtQQ));
        user.wechat=StringHelper.encrypt(ActivityHelper.getTrimedFromView(edtWechat));
        user.tel1=StringHelper.encrypt(ActivityHelper.getTrimedFromView(edtTel1));
        user.tel2=StringHelper.encrypt(ActivityHelper.getTrimedFromView(edtTel2));
        user.introduce=StringHelper.encrypt(ActivityHelper.getTrimedFromView(edtIntroduce));

        user.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                updateDone(e);
            }
        });
    }

    private void updateDone(BmobException e){
        if(e==null){
            Toast.makeText(this, "更新信息成功", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "更新失败："+e.toString(), Toast.LENGTH_SHORT).show();
        }
    }
}
