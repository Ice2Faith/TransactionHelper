package com.ugex.savelar.transactionhelper.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.ugex.savelar.transactionhelper.Entity.NoteMap;
import com.ugex.savelar.transactionhelper.Entity.SysUser;
import com.ugex.savelar.transactionhelper.R;
import com.ugex.savelar.transactionhelper.Util.ActivityHelper;
import com.ugex.savelar.transactionhelper.Util.StringHelper;

public class ManageNoteActivity extends Activity {
    private SysUser user;
    private EditText tvTitle;
    private EditText tvInfo;
    private NoteMap currentNote;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_note);

        InitActivity();
    }

    private void InitActivity() {
        String account=getIntent().getStringExtra(ActivityHelper.EXTRA_KEY_ACCOUNT);
        if(BmobUser.isLogin()){
            user=BmobUser.getCurrentUser(SysUser.class);
        }

        tvTitle =findViewById(R.id.textViewTitle);
        tvInfo =findViewById(R.id.textViewInfo);
    }


    public void OnClickedAdd(View view) {
        currentNote =new NoteMap();
        currentNote.owner= StringHelper.encrypt(user.getUsername());
        currentNote.title=StringHelper.encrypt(ActivityHelper.getTrimedFromView(tvTitle));
        currentNote.info=StringHelper.encrypt(ActivityHelper.getTrimedFromView(tvInfo));
        currentNote.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if(e==null){
                    Toast.makeText(ManageNoteActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(ManageNoteActivity.this, "保存失败："+e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void OnClickedDelete(View view) {
        if(currentNote ==null){
            Toast.makeText(this, "删除失败，请先选择或者添加", Toast.LENGTH_SHORT).show();
            return;
        }
        currentNote.delete(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e==null){
                    Toast.makeText(ManageNoteActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(ManageNoteActivity.this, "删除失败："+e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void OnCLickedUpdate(View view) {
        if(currentNote ==null){
            Toast.makeText(this, "更新失败，请先选择或者添加", Toast.LENGTH_SHORT).show();
            return;
        }
        currentNote.title=StringHelper.encrypt(ActivityHelper.getTrimedFromView(tvTitle));
        currentNote.info=StringHelper.encrypt(ActivityHelper.getTrimedFromView(tvInfo));
        currentNote.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e==null){
                    Toast.makeText(ManageNoteActivity.this, "更新成功", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(ManageNoteActivity.this, "更新失败："+e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    public void OnClickedQuery(View view) {
        Intent intent=new Intent(this, ViewNoteActivity.class);
        intent.putExtra(ActivityHelper.EXTRA_KEY_ACCOUNT,user.getUsername());
        intent.putExtra(ActivityHelper.KEY_REQUEST_ITEM_ID,"require");
        startActivityForResult(intent,0x101);
    }

    private void resultSelected(String id){
        currentNote =new NoteMap();
        currentNote.setObjectId(id);
        BmobQuery<NoteMap> query=new BmobQuery<>();
        query.getObject(currentNote.getObjectId(), new QueryListener<NoteMap>() {
            @Override
            public void done(NoteMap noteMap, BmobException e) {
                if(e!=null){
                    currentNote=null;
                }else{
                    currentNote=noteMap;
                    tvTitle.setText(StringHelper.decrypt(currentNote.title));
                    tvInfo.setText(StringHelper.decrypt(currentNote.info));
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
