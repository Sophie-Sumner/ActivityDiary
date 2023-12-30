/*
 * ActivityDiary
 *
 * Copyright (C) 2023 Raphael Mack http://www.raphael-mack.de
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.rampro.activitydiary;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSON;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;

import de.rampro.activitydiary.model.MessageData;

/*
*
* 聊天主界面
* */
public class CommActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

   // private String url = "ws://169.254.52.119:8080/websocket/";
    private String url = "ws://192.168.1.111:9090/websocket/";

    private TextView tv_msg;
    private EditText et_msg, et_toUser;
    private Button btn_sendMsg;

    private MyWebSocket myWebSocketClient;
    private String userId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comm);
        Intent intent = this.getIntent();
        userId = intent.getStringExtra("userName");
        // 注册EventBus事件
        EventBus.getDefault().register(this);

        initView();
        connServer(); // 登录成功后开始连接服务器
    }

    /*
    *
    * 绑定控件
    * */
    private void initView(){

        tv_msg = findViewById(R.id.tv_msg);
        // 实现滚动条效果
        tv_msg.setMovementMethod(ScrollingMovementMethod.getInstance());

        et_toUser = findViewById(R.id.et_toUser);
        et_msg = findViewById(R.id.et_msg);
        btn_sendMsg = findViewById(R.id.btn_sendMsg);
    }

    /*
    *
    * 发送消息
    * */
    public void sendMsg(View view) {

        MessageData messageData = new MessageData();
        String toUser = et_toUser.getText().toString();

        // 消息类型
        if ( !toUser.equals("") ){
            messageData.setMsgType(1);
            messageData.setToUserId(toUser);
        }else {
            messageData.setMsgType(2);
        }

        messageData.setFromUserId(userId);
        messageData.setMsgData(et_msg.getText().toString());

        // 转化成JSON格式提交
        myWebSocketClient.send(JSON.toJSONString(messageData));
        Log.i(TAG, "sendMsg: 发送消息" + JSON.toJSONString(messageData));
    }


    /*
     *
     *  连接服务器
     *  可以使用EvenBus代替runnable
     * */
    public void connServer(){

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    myWebSocketClient = new MyWebSocket(url + userId);
                    if (myWebSocketClient.connectBlocking()) {
                        Log.i(TAG, "run: 连接服务器成功");
                    } else {
                        Log.i(TAG, "run: 连接服务器失败");
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }
        };
        runnable.run();
    }


    /*
    *
    * 将消息加入TextView
    * */
    private void addTextView(String s){
        tv_msg.append(Html.fromHtml(s));
        tv_msg.append("\n");
    }


    /*
    *
    * 客户端的WebSocket类
    * */
    public class MyWebSocket extends WebSocketClient {

        private static final String TAG = "MyWebSocket";

        /*
         *
         * url:"ws://服务器地址:端口号/websocket"
         * */
        public MyWebSocket(String url) throws URISyntaxException {
            super(new URI(url));
        }

        /*
         *
         * 打开webSocket时回调
         * */
        @Override
        public void onOpen(ServerHandshake serverHandshake) {
            addTextView(toHtmlString("您已进入聊天室", "#FFFFFF"));
            Log.i(TAG, "onOpen: 打开webSocket连接");
        }


        /*
         *
         * 接收到消息时回调
         * */
        @Override
        public void onMessage(String s) {
            Log.i(TAG, "收到消息" + s);
            MessageData messageData = JSON.parseObject(s, MessageData.class);
            EventBus.getDefault().post(messageData);
        }

        /*
         *
         * 断开连接时回调
         * */
        @Override
        public void onClose(int i, String s, boolean b) {
            Log.i(TAG, "断开webSocket连接");
            try {
                myWebSocketClient.closeBlocking();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        /*
         *
         * 出现异常时回调
         * */
        @Override
        public void onError(Exception e) {

        }
    }

    /*
    *
    * EvenBus接收事件
    * */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateMsgView(MessageData messageData){
        addTextView(toHtmlString(messageData.getFromUserId(), "#0000FF"));
        addTextView(toHtmlString(messageData.getMsgData(), "#000000"));
    }

    /*
    *
    * 将字符串转成html表达, 实现颜色变化
    * */
    public String toHtmlString(String s, String color){
        Log.i(TAG, "<font color= " + "\"" + color + "\"" + ">" + s + "</font>");
        return "<font color= " + "\"" + color + "\"" + ">" + s + "</font>";
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 解除EventBus注册
        EventBus.getDefault().unregister(this);
        try {
            myWebSocketClient.closeBlocking();
            Log.i(TAG, "run: 断开服务器成功");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}

