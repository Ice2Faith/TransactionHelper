package com.ugex.savelar.transactionhelper.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.ugex.savelar.transactionhelper.Entity.FriendMap;
import com.ugex.savelar.transactionhelper.Entity.SysUser;
import com.ugex.savelar.transactionhelper.R;
import com.ugex.savelar.transactionhelper.Util.ActivityHelper;
import com.ugex.savelar.transactionhelper.Util.StringHelper;

import java.util.List;

import androidx.annotation.Nullable;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobQueryResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SQLQueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class ManageFriendActivity extends Activity {
    private SysUser user;
    private EditText tvFriendAccount;
    private EditText tvDescript;
    private EditText tvOther;
    private FriendMap currentFriend;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_friend);

        InitActivity();
    }

    private void InitActivity() {
        String account=getIntent().getStringExtra(ActivityHelper.EXTRA_KEY_ACCOUNT);
        if(BmobUser.isLogin()){
            user=BmobUser.getCurrentUser(SysUser.class);
        }

        tvFriendAccount =findViewById(R.id.textViewFriendAccount);
        tvDescript =findViewById(R.id.textViewDescript);
        tvOther=findViewById(R.id.textViewOther);
    }


    public void OnClickedAdd(View view) {
        BmobQuery<SysUser> query=new BmobQuery<>();
        query.addWhereEqualTo("username",ActivityHelper.getTrimedFromView(tvFriendAccount));
        query.findObjects(new FindListener<SysUser>() {
            @Override
            public void done(List<SysUser> list, BmobException e) {
                if(e==null && list.size()>0)
                {
                    beginAddNewFriend();
                }else{
                    accountNotExsit();
                }
            }
        });

    }
    private void accountNotExsit() {
        Toast.makeText(this, "该账号不存在，请确认后重试", Toast.LENGTH_SHORT).show();
    }
    private void beginAddNewFriend(){
        String sql="select * from FriendMap where (owner=? and friend=?)";
        BmobQuery<FriendMap> query=new BmobQuery<>();
        query.doSQLQuery(sql, new SQLQueryListener<FriendMap>() {
            @Override
            public void done(BmobQueryResult<FriendMap> bmobQueryResult, BmobException e) {
                if(e==null && bmobQueryResult.getResults().size()>0)
                {
                        addNewFriendIsExist();
                }else{
                    addNewFriendDirect();
                }
            }
        },StringHelper.encrypt(user.getUsername()),StringHelper.encrypt(ActivityHelper.getTrimedFromView(tvFriendAccount)));
    }
    private void addNewFriendIsExist(){
        Toast.makeText(this, "好友已经添加，请勿重复添加", Toast.LENGTH_SHORT).show();
    }
    private void addNewFriendDirect(){
        Log.i("debuginfo","begin add friend");
        currentFriend =new FriendMap();
        currentFriend.owner= StringHelper.encrypt(user.getUsername());
        currentFriend.friend=StringHelper.encrypt(ActivityHelper.getTrimedFromView(tvFriendAccount));
        currentFriend.descript=StringHelper.encrypt(ActivityHelper.getTrimedFromView(tvDescript));
        currentFriend.other=StringHelper.encrypt(ActivityHelper.getTrimedFromView(tvOther));
        currentFriend.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if(e==null){
                    Toast.makeText(ManageFriendActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(ManageFriendActivity.this, "保存失败："+e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void OnClickedDelete(View view) {
        if(currentFriend ==null){
            Toast.makeText(this, "删除失败，请先选择或者添加", Toast.LENGTH_SHORT).show();
            return;
        }
        currentFriend.delete(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e==null){
                    Toast.makeText(ManageFriendActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(ManageFriendActivity.this, "删除失败："+e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void OnCLickedUpdate(View view) {
        if(currentFriend ==null){
            Toast.makeText(this, "更新失败，请先选择或者添加", Toast.LENGTH_SHORT).show();
            return;
        }
        currentFriend.friend=StringHelper.encrypt(ActivityHelper.getTrimedFromView(tvFriendAccount));
        currentFriend.descript=StringHelper.encrypt(ActivityHelper.getTrimedFromView(tvDescript));
        currentFriend.other=StringHelper.encrypt(ActivityHelper.getTrimedFromView(tvOther));
        currentFriend.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e==null){
                    Toast.makeText(ManageFriendActivity.this, "更新成功", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(ManageFriendActivity.this, "更新失败："+e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    public void OnClickedQuery(View view) {
        Intent intent=new Intent(this, ViewFriendActivity.class);
        intent.putExtra(ActivityHelper.EXTRA_KEY_ACCOUNT,user.getUsername());
        intent.putExtra(ActivityHelper.KEY_REQUEST_ITEM_ID,"require");
        startActivityForResult(intent,0x101);
    }

    private void resultSelected(String id){
        currentFriend =new FriendMap();
        currentFriend.setObjectId(id);
        BmobQuery<FriendMap> query=new BmobQuery<>();
        query.getObject(currentFriend.getObjectId(), new QueryListener<FriendMap>() {
            @Override
            public void done(FriendMap friendMap, BmobException e) {
                if(e!=null){
                    currentFriend =null;
                }else{
                    currentFriend =friendMap;
                    tvFriendAccount.setText(StringHelper.decrypt(currentFriend.friend));
                    tvDescript.setText(StringHelper.decrypt(currentFriend.descript));
                    tvOther.setText(StringHelper.decrypt(currentFriend.other));
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
