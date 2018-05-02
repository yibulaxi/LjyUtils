package com.ljy.ljyutils.activity;

import android.Manifest;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ljy.ljyutils.ICompute;
import com.ljy.ljyutils.ISecurityCenter;
import com.ljy.ljyutils.R;
import com.ljy.ljyutils.base.BaseActivity;
import com.ljy.ljyutils.bean.Book;
import com.ljy.ljyutils.bean.ProcessBean;
import com.ljy.ljyutils.bean.SeriBean;
import com.ljy.ljyutils.provider.BookProvider;
import com.ljy.ljyutils.service.MessengerService;
import com.ljy.ljyutils.service.TcpServerService;
import com.ljy.ljyutils.stub.BinderPool;
import com.ljy.ljyutils.stub.ComputeImpl;
import com.ljy.ljyutils.stub.SecurityCenterImpl;
import com.ljy.util.LjyFileUtil;
import com.ljy.util.LjyLogUtil;
import com.ljy.util.LjyPermissionUtil;
import com.ljy.util.LjySystemUtil;
import com.ljy.util.LjyTimeUtil;
import com.ljy.util.LjyToastUtil;
import com.ljy.view.LjyMDDialogManager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 多进程, 跨进程通信, 序列化
 * <p>
 * Serializable: java提供,空接口,开销大,大量的IO操作
 * Parcelable: android提供,通过intent,binder传递
 */

public class ProcessActivity extends BaseActivity {

    @BindView(R.id.text_info)
    TextView mTextViewInfo;
    @BindView(R.id.btnSend)
    Button btnSend;

    private static final int MESSAGE_RECEIVE_NEW_MSG = 1;
    private static final int MESSAGE_SOCKET_CONNECTED = 2;

    private MyHandler mHandler = new MyHandler(this);
    private Socket mClientSocket;
    private PrintWriter mPrintWriter;
    private ISecurityCenter mSecurityCenter;
    private ICompute mCompute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process);
        ButterKnife.bind(mActivity);
        LjyLogUtil.i("ProcessBean.count=" + ProcessBean.count);
    }

    //Messenger
    private Messenger mService;
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = new Messenger(service);
            Message msg = Message.obtain(null, MessengerService.MSG_FROM_CLIENT);
            Bundle data = new Bundle();
            data.putString("msg", "Hello Messenger ~~~");
            msg.setData(data);
            try {
                mService.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };


    public void onBtnClick(View view) {
        mTextViewInfo.append("----------" + ((Button) view).getText() + "----------\n");
        LjyLogUtil.setAppendLogMsg(true);
        switch (view.getId()) {
            case R.id.btn1:
                if (checkPremission(Manifest.permission.WRITE_EXTERNAL_STORAGE, 101))
                    methodOut();
                break;
            case R.id.btn2:
                if (checkPremission(Manifest.permission.READ_EXTERNAL_STORAGE, 102))
                    methodIn();
                break;
            case R.id.btn3:
                Intent intent = new Intent(this, MessengerService.class);
                bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
                break;
            case R.id.btn4:
                methodProvider();
                break;
            case R.id.btn5:
                methodSocket();
                break;
            case R.id.btnSend:
                new LjyMDDialogManager(ProcessActivity.this).alertEditTextMD("请输入消息内容:", new LjyMDDialogManager.PositiveListenerText() {
                    @Override
                    public void positive(String text) {
                        final String msg = text;
                        if (!TextUtils.isEmpty(msg) && mPrintWriter != null) {
                            new Thread() {
                                @Override
                                public void run() {
                                    mPrintWriter.println(msg);
                                }
                            }.start();
                            String time = LjyTimeUtil.timestampToDate(System.currentTimeMillis());
                            final String showedMsg = "self " + time + ":" + msg + "\n";
                            mTextViewInfo.append(showedMsg);
                        }
                    }
                }, null);
                break;
            case R.id.btn6:
                new Thread() {
                    @Override
                    public void run() {
                        methodBinderPool();
                    }
                }.start();
                break;
        }
        mTextViewInfo.append(LjyLogUtil.getAllLogMsg());
        LjyLogUtil.setAppendLogMsg(false);
    }

    /**
     * 建议在子线程中执行
     */
    private void methodBinderPool() {
        LjyLogUtil.i("ProcessActivity.mContext:" + mContext);

        BinderPool binderPool = BinderPool.getInstance(mContext);

        IBinder securityBinder = binderPool.queryBinder(BinderPool.BINDER_SECURITY_CENTER);
        mSecurityCenter = SecurityCenterImpl.asInterface(securityBinder);
        LjyLogUtil.i("visit ISecurityCenter");
        String msg = "你好,Android";
        LjyLogUtil.i("msg:" + msg);
        try {
            String pwd = mSecurityCenter.encrypt(msg);
            LjyLogUtil.i("encrypt:" + pwd);
            LjyLogUtil.i("decrypt:" + mSecurityCenter.decrypt(pwd));
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        IBinder computeBinder = binderPool.queryBinder(BinderPool.BINDER_COMPUTE);
        mCompute = ComputeImpl.asInterface(computeBinder);
        try {
            LjyLogUtil.i("2+3=" + mCompute.add(2, 3));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void methodSocket() {
        Intent intent = new Intent(mContext, TcpServerService.class);
        startService(intent);
        new Thread() {
            @Override
            public void run() {
                connectTCPServer();
            }
        }.start();

    }

    private void connectTCPServer() {
        Socket socket = null;
        //超时重连策略
        while (socket == null) {
            try {
                socket = new Socket("localhost", TcpServerService.port);
                mClientSocket = socket;
                mPrintWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                mHandler.sendEmptyMessage(MESSAGE_SOCKET_CONNECTED);
                LjyLogUtil.i("connect server success");
            } catch (IOException e) {
                SystemClock.sleep(1000);
                LjyLogUtil.i("connect tcp server failed,retry...");
            }
        }

        try {
            //接收服务端消息
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            while (!ProcessActivity.this.isFinishing()) {
                String msg = br.readLine();
                LjyLogUtil.i("receive: " + msg);
                if (!TextUtils.isEmpty(msg)) {
                    String time = LjyTimeUtil.timestampToDate(System.currentTimeMillis());
                    String showedMsg = "server " + time + ":" + msg + "\n";
                    mHandler.obtainMessage(MESSAGE_RECEIVE_NEW_MSG, showedMsg).sendToTarget();
                }
            }
            LjyLogUtil.i("quit...");
            LjySystemUtil.clostStream(mPrintWriter);
            LjySystemUtil.clostStream(br);
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void methodProvider() {
        Uri bookUri = BookProvider.BOOK_CONTENT_URI;
        ContentValues values = new ContentValues();
        values.put("_id", 6);
        values.put("name", "红楼梦");
        getContentResolver().insert(bookUri, values);
        Cursor bookCursor = getContentResolver().query(bookUri, new String[]{"_id", "name"}, null, null, null);
        while (bookCursor.moveToNext()) {
            Book book = new Book();
            LjyLogUtil.i("id:" + bookCursor.getInt(0) + ",name:" + bookCursor.getString(1));
            book.name = bookCursor.getString(1);
            LjyLogUtil.i(book.toString());
        }
        bookCursor.close();

        Uri userUri = BookProvider.USER_CONTENT_URI;
        Cursor userCursor = getContentResolver().query(userUri, new String[]{"_id", "name", "age"}, null, null, null);
        while (userCursor.moveToNext()) {
            LjyLogUtil.i("user--> id:" + userCursor.getInt(0) + ",name:" + userCursor.getString(1) + ",age:" + userCursor.getInt(2));
        }
        userCursor.close();
    }

    @LjyPermissionUtil.GetPermission(permissionResult = true, requestCode = 102)
    private void methodIn() {
        //反序列化
        ObjectInputStream in = null;
        try {
            in = new ObjectInputStream(new FileInputStream(LjyFileUtil.getSDCardPath() + "cache.txt"));
            SeriBean seriBeanNew = (SeriBean) in.readObject();
            LjyLogUtil.i("seriBeanNew =" + seriBeanNew.toString());
            LjyToastUtil.toast(mContext, "seriBeanNew =" + seriBeanNew.toString());
//            ParceBean parceBeanNew = (ParceBean) in.readObject();
//            LjyLogUtil.i("parceBeanNew =" + parceBeanNew.toString());
//            LjyToastUtil.toast(mContext, "parceBeanNew =" + parceBeanNew.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @LjyPermissionUtil.GetPermission(permissionResult = true, requestCode = 101)
    private void methodOut() {
        //创建
        SeriBean seriBean = new SeriBean("小明");
        LjyLogUtil.i("seriBean =" + seriBean.toString());
        LjyToastUtil.toast(mContext, "seriBean =" + seriBean.toString());
//        Parce2Bean parce2Bean = new Parce2Bean("我是info啊");
//        ParceBean parceBean = new ParceBean("刘备", parce2Bean);
//        LjyLogUtil.i("parceBean =" + parceBean.toString());
//        LjyToastUtil.toast(mContext, "parceBean =" + parceBean.toString());
        //序列化
        ObjectOutputStream out = null;
        try {
            out = new ObjectOutputStream(new FileOutputStream(LjyFileUtil.getSDCardPath() + "cache.txt"));
            out.writeObject(seriBean);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    static class MyHandler extends Handler {

        private final ProcessActivity processActivity;

        public MyHandler(ProcessActivity processActivity) {
            this.processActivity = processActivity;
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_RECEIVE_NEW_MSG:
                    processActivity.mTextViewInfo.append((String) msg.obj);
                    break;
                case MESSAGE_SOCKET_CONNECTED:
                    processActivity.btnSend.setClickable(true);
                    break;
                default:
                    break;
            }
        }
    }
}