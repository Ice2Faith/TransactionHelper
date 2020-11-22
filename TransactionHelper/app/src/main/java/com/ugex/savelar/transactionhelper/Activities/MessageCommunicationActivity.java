
package com.ugex.savelar.transactionhelper.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobQueryResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SQLQueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.ugex.savelar.transactionhelper.Entity.FriendMap;
import com.ugex.savelar.transactionhelper.Entity.MessageMap;
import com.ugex.savelar.transactionhelper.Entity.SysUser;
import com.ugex.savelar.transactionhelper.R;
import com.ugex.savelar.transactionhelper.Util.ActivityHelper;
import com.ugex.savelar.transactionhelper.Util.ListObjectAdapter;
import com.ugex.savelar.transactionhelper.Util.StringHelper;

import java.util.ArrayList;
import java.util.List;

public class MessageCommunicationActivity extends Activity {
    public static final String EXTRA_KEY_FRIEND_ACCOUNT="friend_account";
    private SysUser friend;
    private SysUser user;
    private EditText edtNickName;
    private EditText edtAccount;
    private ListView lsvHistoryMsg;
    private EditText edtMessage;
    private Button btnSend;
    private CheckBox ckbAutoSync;
    ;
    private ListObjectAdapter adapter;

    private static final int WHAT_GOT_ALL_HISTORY=0x108;
    private static final int WHAT_GOT_NEW_MESSAGES=0x109;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            if(msg.what==WHAT_GOT_NEW_MESSAGES)
            {
                List<MessageMap> lstold=new ArrayList<>();
                lstold.addAll(((List<MessageMap>)adapter.data));
                MessageMap endmap=lstold.get(lstold.size()-1);
                List<MessageMap> lstnew=(List<MessageMap>)msg.obj;
                for(MessageMap p : lstnew)
                {
                    if(p.getObjectId().equals(endmap.getObjectId())==false)
                    {
                        lstold.add(p);
                    }
                }
                adapter.data=lstold;
                adapter.notifyDataSetChanged();
                lsvHistoryMsg.setSelection(lsvHistoryMsg.getBottom());
            }else if(msg.what==WHAT_GOT_ALL_HISTORY){
                adapter.data=(List<MessageMap>)msg.obj;
                if(adapter.data.size()==0)
                    Toast.makeText(MessageCommunicationActivity.this, "你们还没有聊天记录哦，打个招呼吧", Toast.LENGTH_SHORT).show();
                adapter.notifyDataSetChanged();
                lsvHistoryMsg.setSelection(lsvHistoryMsg.getBottom());
                deleteMoreMessage();
            }
            super.handleMessage(msg);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_communication);

        InitActivity();
    }

    private void InitActivity() {
        String account=getIntent().getStringExtra(ActivityHelper.EXTRA_KEY_ACCOUNT);
        if(BmobUser.isLogin()){
            user=BmobUser.getCurrentUser(SysUser.class);
        }

        getFriendToCommunicate();

        edtNickName=findViewById(R.id.editTextName);
        edtAccount=findViewById(R.id.editTextAccount);
        edtMessage=findViewById(R.id.editTextMessage);
        lsvHistoryMsg=findViewById(R.id.listViewHistoryMsg);
        btnSend=findViewById(R.id.buttonSend);
        btnSend.setEnabled(false);
        edtNickName.setKeyListener(null);
        edtAccount.setKeyListener(null);
        ckbAutoSync=findViewById(R.id.checkBoxAutoSync);

        adapter=new ListObjectAdapter(this, new ArrayList<MessageMap>(), new ListObjectAdapter.OnRequeryView() {
            @Override
            public View getView(Object obj, int posotion, Context ctx, View convertView) {
                EditText editText=null;
                if(convertView==null)
                    editText=new EditText(ctx);
                else
                    editText=(EditText) convertView;
                MessageMap msg=(MessageMap)obj;
                editText.setText(StringHelper.decrypt(msg.message));
                editText.setKeyListener(null);
                editText.setTextIsSelectable(true);

                if(user.getUsername().equals(StringHelper.decrypt(msg.sendOne))) {
                    editText.setTextColor(Color.rgb(200, 0, 128));
                    editText.setGravity(Gravity.RIGHT);
                }else {
                    editText.setTextColor(Color.rgb(255, 128, 64));
                    editText.setGravity(Gravity.LEFT);
                }
                return editText;
            }
        });

        lsvHistoryMsg.setAdapter(adapter);


    }
    private String friendDescript;
    private void gotFriendAndGetMessages(String friendMapId) {
        BmobQuery<FriendMap> query=new BmobQuery<>();
        query.getObject(friendMapId, new QueryListener<FriendMap>() {
            @Override
            public void done(FriendMap friendMap, BmobException e) {
                if(e==null){
                    friendDescript=StringHelper.decrypt(friendMap.descript);
                    getFriendSysUser(friendMap.friend);
                }
            }
        });
    }

    private void getFriendSysUser(String account){
        BmobQuery<SysUser> query=new BmobQuery<>();
        query.addWhereEqualTo("username",StringHelper.decrypt(account));
        query.findObjects(new FindListener<SysUser>() {
            @Override
            public void done(List<SysUser> list, BmobException e) {
                if(e==null){
                    friend=list.get(0);
                    edtNickName.setText(StringHelper.decrypt(friend.nickName));
                    edtAccount.setText(friend.getUsername());
                    btnSend.setEnabled(true);
                    if(StringHelper.isEmptyNull(friendDescript,false)==false)
                    {
                        edtNickName.setText(friendDescript);
                    }
                    getHistoryMessageList();
                }

            }
        });
    }

    private void getFriendToCommunicate() {
        Toast.makeText(this, "请选择好友之后聊天哦", Toast.LENGTH_SHORT).show();
        Intent intent=new Intent(this, ViewFriendActivity.class);
        intent.putExtra(ActivityHelper.EXTRA_KEY_ACCOUNT,user.getUsername());
        intent.putExtra(ActivityHelper.KEY_REQUEST_ITEM_ID,"require");
        startActivityForResult(intent,0x101);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode==0x101 && resultCode==0x101){
            String friendId=data.getStringExtra(ActivityHelper.KEY_ID);
            gotFriendAndGetMessages(friendId);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void getHistoryMessageList(){
        String sql="select * from MessageMap where ( ( sendOne = ? and recvOne = ?) or (sendOne=? and recvOne=?) ) order by createdAt";
        BmobQuery<MessageMap> query=new BmobQuery<>();
        query.doSQLQuery(sql, new SQLQueryListener<MessageMap>() {
                    @Override
                    public void done(BmobQueryResult<MessageMap> bmobQueryResult, BmobException e) {
                        //注意这个方法有点坑，就是这个方法是异步运行的，也就是说涉及到线程通信，因此这里直接设置外部参数可能是会无效的，这里使用Handler解决
                        //Bmob也提供了回调接口，还没了解这个接口的使用
                        if(e==null){
                            Message msg=new Message();
                            msg.what=WHAT_GOT_ALL_HISTORY;
                            msg.obj=bmobQueryResult.getResults();
                            handler.sendMessage(msg);
                        }else{
                            Toast.makeText(MessageCommunicationActivity.this, "没有查询到聊天记录", Toast.LENGTH_SHORT).show();
                        }
                    }
                },StringHelper.encrypt(user.getUsername()),StringHelper.encrypt(friend.getUsername()),
                StringHelper.encrypt(friend.getUsername()),StringHelper.encrypt(user.getUsername()));

        adapter.notifyDataSetChanged();
        lsvHistoryMsg.setSelection(lsvHistoryMsg.getBottom());
    }

    private boolean openTimerGetNewMsg=true;
    private static final int TIMER_SLEEP_MILLSECOND=30*60*1000/200;//9秒自动更新一次信息列表，半小时单人并发控制到200次，这样就可以5对人在线聊天了，毕竟Bmob免费半小时并发只到2000
    private void timerGetNewMessage()
    {
        openTimerGetNewMsg=true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(openTimerGetNewMsg)
                {
                    try {
                        Thread.sleep(TIMER_SLEEP_MILLSECOND);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if(openTimerGetNewMsg==false)
                        break;
                    getNewMessage();
                }
            }
        }).start();

    }
    public void OnClickedAutoSyncCheckBox(View view) {
        openTimerGetNewMsg=false;
        if(ckbAutoSync.isChecked()){
            openTimerGetNewMsg=true;
            timerGetNewMessage();
        }else{
            openTimerGetNewMsg=false;
        }
    }

    @Override
    protected void onDestroy() {
        openTimerGetNewMsg=false;
        super.onDestroy();
    }

    private void deleteMoreMessage(){
        //系统最多保存2048条数据，多余的历史记录将会被删除
        int maxMsgCount=2048;
        if(adapter.data.size()>maxMsgCount){
            MessageMap msg=(MessageMap) adapter.data.get(adapter.data.size()-maxMsgCount);
            BmobQuery<MessageMap> query=new BmobQuery<>();
            String sql="select objectId from MessageMap where ( ( sendOne = ? and recvOne = ?) or (sendOne=? and recvOne=?) ) and createdAt < date(?)";
            query.doSQLQuery(sql, new SQLQueryListener<MessageMap>() {
                @Override
                public void done(BmobQueryResult<MessageMap> bmobQueryResult, BmobException e) {
                    if(e==null){
                        for(MessageMap m : bmobQueryResult.getResults()){
                            m.delete(new UpdateListener() {
                                @Override
                                public void done(BmobException e) {
                                    //do nothing
                                }
                            });
                        }
                    }
                }
            },StringHelper.encrypt(user.getUsername()),StringHelper.encrypt(friend.getUsername()),
                    StringHelper.encrypt(friend.getUsername()),StringHelper.encrypt(user.getUsername()),msg.getCreatedAt());
        }
    }

    private void getNewMessage(){
        MessageMap msg=(MessageMap) adapter.data.get(adapter.data.size()-1);
        String sql="select * from MessageMap where ( ( ( sendOne = ? and recvOne = ?) or (sendOne=? and recvOne=?) ) and createdAt > date(?) )order by createdAt";
        BmobQuery<MessageMap> query=new BmobQuery<>();
        query.doSQLQuery(sql, new SQLQueryListener<MessageMap>() {
                    @Override
                    public void done(BmobQueryResult<MessageMap> bmobQueryResult, BmobException e) {
                        if(e==null){
                           Message msg=new Message();
                           msg.what=WHAT_GOT_NEW_MESSAGES;
                           msg.obj=bmobQueryResult.getResults();
                           handler.sendMessage(msg);
                        }else{
                            Toast.makeText(MessageCommunicationActivity.this, "没有查询到聊天记录", Toast.LENGTH_SHORT).show();
                        }
                    }
                },StringHelper.encrypt(user.getUsername()),StringHelper.encrypt(friend.getUsername()),
                StringHelper.encrypt(friend.getUsername()),StringHelper.encrypt(user.getUsername()),msg.getCreatedAt());
    }
    public void OnClickedSendMsgButton(View view) {
        String msgStr=StringHelper.getSafe(ActivityHelper.getTrimedFromView(edtMessage),true);
        if(StringHelper.isEmptyNull(msgStr,true)){
            getNewMessage();
            return;
        }
        MessageMap msg=new MessageMap();
        msg.message=StringHelper.encrypt(msgStr);
        msg.sendOne=StringHelper.encrypt(user.getUsername());
        msg.recvOne=StringHelper.encrypt(friend.getUsername());
        msg.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if(e==null){
                    Toast.makeText(MessageCommunicationActivity.this, "发送成功", Toast.LENGTH_SHORT).show();
                    edtMessage.setText("");
                    getNewMessage();
                }else{
                    Toast.makeText(MessageCommunicationActivity.this, "发送失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}
