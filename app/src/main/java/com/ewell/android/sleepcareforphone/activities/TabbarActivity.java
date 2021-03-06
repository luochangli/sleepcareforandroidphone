package com.ewell.android.sleepcareforphone.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.ewell.android.bll.DataFactory;
import com.ewell.android.common.Grobal;
import com.ewell.android.common.exception.EwellException;
import com.ewell.android.common.xmpp.XmppMsgDelegate;
import com.ewell.android.ibll.SleepcareforPhoneManage;
import com.ewell.android.model.AlarmInfo;
import com.ewell.android.model.AlarmList;
import com.ewell.android.sleepcareforphone.R;
import com.ewell.android.sleepcareforphone.common.pushnotification.PushService;
import com.readystatesoftware.viewbadger.BadgeView;

import java.util.ArrayList;

/**
 * Created by lillix on 7/29/16.
 */
public class TabbarActivity extends FragmentActivity implements View.OnClickListener, XmppMsgDelegate<AlarmList> {
    private FragmentRr fragmentRr;
    private FragmentHr fragmentHr;
    private FragmentSleep fragmentSleep;
    private FragmentMe fragmentMe;

    private FrameLayout rrFl, hrFl, sleepFl, meFl;
    private ImageView rrIv, hrIv, sleepIv, meIv;

    private SharedPreferences mSP;
    private String curUserName;
    private String curUserCode;

    private static final int MSG_SUCCESS = 0;//
    private BadgeView badge;
    private Thread mThread;
    private ArrayList<String> unhandledCodes = new ArrayList<String>();




    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {//此方法在ui线程运行
            switch (msg.what) {
                case MSG_SUCCESS:
                    if(unhandledCodes.size()>0) {
                        badge.setText(Integer.toString(unhandledCodes.size()));
                        badge.show();
                    }
                    else{
                        badge.hide();
                    }
                    break;
            }


        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabbar);

        initView();

        initData();

        clickHrBtn();

        //开关远程通知
        String deviceid = getSharedPreferences(PushService.TAG, MODE_PRIVATE).getString("deviceID","");
        String loginname = Grobal.getInitConfigModel().getLoginUserName();
        try {
            if (getSharedPreferences("config", MODE_PRIVATE).getBoolean("notificationflag", true)) {
                DataFactory.GetSleepcareforPhoneManage().OpenNotificationForAndroid(deviceid, loginname);
                PushService.actionStart(TabbarActivity.this);
            } else {
                DataFactory.GetSleepcareforPhoneManage().CloseNotificationForAndroid(deviceid, loginname);
                PushService.actionStop(TabbarActivity.this);
            }
        }catch (EwellException ex) {

            Toast.makeText(TabbarActivity.this,ex.getExceptionMsg(), Toast.LENGTH_SHORT).show();
        }

        //"me"上的报警数字提醒
        badge = new BadgeView(this, meFl);
        badge.setTextSize(12);
        badge.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
        //从服务器获取当前账户下未处理的报警数
        LoadUnhandledAlarms();


        //新线程,每3秒刷新报警数
        mThread = new Thread(runnable);
        mThread.start();


    }


    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            //run()在新的线程中运行
            while (true) {
                //获取当前未处理的报警数
                mHandler.obtainMessage(MSG_SUCCESS, unhandledCodes.size()).sendToTarget();

                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };





    private void initView() {

        rrFl = (FrameLayout) findViewById(R.id.layout_rr);
        hrFl = (FrameLayout) findViewById(R.id.layout_hr);
        sleepFl = (FrameLayout) findViewById(R.id.layout_sleep);
        meFl = (FrameLayout) findViewById(R.id.layout_me);


        rrIv = (ImageView) findViewById(R.id.image_rr);
        hrIv = (ImageView) findViewById(R.id.image_hr);
        sleepIv = (ImageView) findViewById(R.id.image_sleep);
        meIv = (ImageView) findViewById(R.id.image_me);

        //完成sp的初始化。
        mSP = getSharedPreferences("config", MODE_PRIVATE);

    }

    private void initData() {

        rrFl.setOnClickListener(this);
        hrFl.setOnClickListener(this);
        sleepFl.setOnClickListener(this);
        meFl.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.layout_rr:
                clickRrBtn();
                break;

            case R.id.layout_hr:
                clickHrBtn();
                break;

            case R.id.layout_sleep:
                clickSleepBtn();
                break;

            case R.id.layout_me:
                clickMeBtn();
                break;

            default:
                System.out.print("============================none");
                break;
        }
    }

    private void clickRrBtn() {

        // fragmentRr = new FragmentRr();
        curUserCode = mSP.getString("curusercode", "");
        curUserName = mSP.getString("curusername", "");
        fragmentRr = FragmentRr.newInstance(curUserCode, curUserName);

        FragmentTransaction fragmentTransaction = this
                .getSupportFragmentManager().beginTransaction();

        fragmentTransaction.replace(R.id.frame_content, fragmentRr);

        fragmentTransaction.commit();

        rrFl.setSelected(true);
        rrIv.setSelected(true);

        hrFl.setSelected(false);
        hrIv.setSelected(false);

        sleepFl.setSelected(false);
        sleepIv.setSelected(false);

        meFl.setSelected(false);
        meIv.setSelected(false);
    }

    private void clickHrBtn() {


        //  fragmentAuth = new FragmentAuth();
        curUserCode = mSP.getString("curusercode", "");
        curUserName = mSP.getString("curusername", "");
        fragmentHr = FragmentHr.newInstance(curUserCode, curUserName);

        FragmentTransaction fragmentTransaction = this
                .getSupportFragmentManager().beginTransaction();


        fragmentTransaction.replace(R.id.frame_content, fragmentHr);

        fragmentTransaction.commit();

        rrFl.setSelected(false);
        rrIv.setSelected(false);

        hrFl.setSelected(true);
        hrIv.setSelected(true);

        sleepFl.setSelected(false);
        sleepIv.setSelected(false);

        meFl.setSelected(false);
        meIv.setSelected(false);
    }

    private void clickSleepBtn() {

        // fragmentSleep = new FragmentSleep();
        curUserCode = mSP.getString("curusercode", "");
        curUserName = mSP.getString("curusername", "");
        fragmentSleep = FragmentSleep.newInstance(curUserCode, curUserName);

        FragmentTransaction fragmentTransaction = this
                .getSupportFragmentManager().beginTransaction();

        fragmentTransaction.replace(R.id.frame_content, fragmentSleep);

        fragmentTransaction.commit();

        rrFl.setSelected(false);
        rrIv.setSelected(false);

        hrFl.setSelected(false);
        hrIv.setSelected(false);

        sleepFl.setSelected(true);
        sleepIv.setSelected(true);

        meFl.setSelected(false);
        meIv.setSelected(false);
    }


    private void clickMeBtn() {

        // fragmentMore = new FragmentMore();
        curUserCode = mSP.getString("curusercode", "");
        fragmentMe = FragmentMe.newInstance(curUserCode);

        FragmentTransaction fragmentTransaction = this
                .getSupportFragmentManager().beginTransaction();

        fragmentTransaction.replace(R.id.frame_content, fragmentMe);

        fragmentTransaction.commit();

        rrFl.setSelected(false);
        rrIv.setSelected(false);

        hrFl.setSelected(false);
        hrIv.setSelected(false);

        sleepFl.setSelected(false);
        sleepIv.setSelected(false);

        meFl.setSelected(true);
        meIv.setSelected(true);
    }

    private void LoadUnhandledAlarms() {
        SleepcareforPhoneManage sleepcareforPhoneManage = DataFactory.GetSleepcareforPhoneManage();

        try {
            if (Grobal.getXmppManager().Connect()) {
                String username = Grobal.getInitConfigModel().getLoginUserName();

                AlarmList tempAlarmlist = sleepcareforPhoneManage.GetSingleAlarmByLoginUser(username, "", "", "", "001", "", "");
                ArrayList<AlarmInfo> alarminfoList = tempAlarmlist.getAlarmInfoList();
                for (int i = 0; i < alarminfoList.size(); i++) {
                    unhandledCodes.add(alarminfoList.get(i).getAlarmCode());

                }
                Grobal.getInitConfigModel().setUnhandledAlarmcodeList(unhandledCodes);
            }
        } catch (EwellException ex) {
            //做消息弹窗提醒
        } catch (Exception e) {
            //做消息弹窗提醒

        }

    }

    @Override
    public void ReciveMessage(AlarmList alarmlist) {
        //通过bedusercodelist过滤为需要的报警信息
        ArrayList<String> bedusercodelist = Grobal.getInitConfigModel().getBedusercodeList();
        if (bedusercodelist.size() > 0) {
            ArrayList<AlarmInfo> alarminfolist = alarmlist.getAlarmInfoList();
            for (int i = 0; i < alarminfolist.size(); i++) {
                for (String tempbedusercode : bedusercodelist) {
                    //过滤出当前关注的bedusercode的报警信息
                    String tempalarmcode = alarminfolist.get(i).getAlarmCode();
                    if (tempbedusercode.equals(alarminfolist.get(i).getUserCode())) {
                        //若unhandledcodes里存在,则通过handleflag检查是否需要从列表里删除
                        if (isCodeExist(tempalarmcode)) {
                            if (alarminfolist.get(i).getHandleFlag().equals("1") || alarminfolist.get(i).getHandleFlag().equals("2")) {
                                for (int j = 0; j < unhandledCodes.size(); j++) {
                                    if (unhandledCodes.get(j).equals(tempalarmcode)) {
                                        unhandledCodes.remove(j);
                                        break;
                                    }
                                }
                            }
                        }
                        //若不存在,则加入到unhandledcodes列表中
                        else {
                            if (alarminfolist.get(i).getHandleFlag().equals("0")) {
                                unhandledCodes.add(tempalarmcode);
                            }
                        }
                        Grobal.getInitConfigModel().setUnhandledAlarmcodeList(unhandledCodes);
                        break;
                    }
                }
            }
        }

    }

    public boolean isCodeExist(String code) {
        if (unhandledCodes.size() > 0) {
            for (String tempcode : unhandledCodes) {
                if (tempcode.equals(code)) {
                    return true;
                }

            }
        }
        return false;
    }

}

