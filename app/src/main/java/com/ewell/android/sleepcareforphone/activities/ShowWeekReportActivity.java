package com.ewell.android.sleepcareforphone.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ewell.android.sleepcareforphone.R;
import com.ewell.android.sleepcareforphone.ShowWeekReportBinder;
import com.ewell.android.sleepcareforphone.common.fancychart.ChartData;
import com.ewell.android.sleepcareforphone.common.fancychart.FancyChart;
import com.ewell.android.sleepcareforphone.common.fancychart.FancyChartPointListener;
import com.ewell.android.sleepcareforphone.common.fancychart.Point;
import com.ewell.android.sleepcareforphone.viewmodels.ShowWeekReportViewModel;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by lillix on 7/29/16.
 */
public class ShowWeekReportActivity extends Activity implements SendEmailFragment.InputListener {
    private static ShowWeekReportViewModel showweekreport;

    private static TextView TextTitleDate;
    private ImageView ImgSendEmail;

    private static FancyChart hrrrchart;
    private static FancyChart leavebedchart;
    private static FancyChart sleepchart;


    private DatePickerFragment fragment;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        showweekreport = new ShowWeekReportViewModel();
        showweekreport.setParentactivity(this);
        showweekreport.InitData();

        ShowWeekReportBinder binding = DataBindingUtil.setContentView(this, R.layout.activity_showweekreport);
        binding.setShowweekreport(showweekreport);

        TextTitleDate = (TextView) findViewById(R.id.textdate);
        TextTitleDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerFragemnt();
            }
        });

        ImgSendEmail = (ImageView) findViewById(R.id.sendemail);
        ImgSendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(showweekreport.getBedusercode().equals("")){

                }
                else{
                    showLoginDialog();
                }

            }
        });


        hrrrchart = (FancyChart) findViewById(R.id.my_hrrrchart);
        hrrrchart.setOnPointClickListener(new FancyChartPointListener() {

            @Override
            public void onClick(Point point) {
                Toast.makeText(ShowWeekReportActivity.this, point.y + "次/分", Toast.LENGTH_SHORT).show();

            }
        });
        ShowHrrrChart();

        leavebedchart = (FancyChart) findViewById(R.id.my_leavebedchart);
        leavebedchart.setOnPointClickListener(new FancyChartPointListener() {
            @Override
            public void onClick(Point point) {
                Toast.makeText(ShowWeekReportActivity.this, point.y + "次/天", Toast.LENGTH_SHORT).show();

            }
        });
        ShowLeavebedChart();

        sleepchart = (FancyChart) findViewById(R.id.my_sleepchart);
        sleepchart.setOnPointClickListener(new FancyChartPointListener() {
            @Override
            public void onClick(Point point) {
                Toast.makeText(ShowWeekReportActivity.this, point.y + "小时", Toast.LENGTH_SHORT).show();

            }
        });
        ShowSleepChart();

        fragment = new DatePickerFragment();

    }

    public static void ShowHrrrChart() {

        hrrrchart.clearValues();

        ChartData datahr = new ChartData(ChartData.LINE_COLOR_GREEN);
        ChartData datarr = new ChartData(ChartData.LINE_COLOR_BLUE);
        ArrayList<String> tempxlist = showweekreport.getHrrr_xValues();
        ArrayList<String> tempylist_hr = showweekreport.getHr_yValues();
        ArrayList<String> tempylist_rr = showweekreport.getRr_yValues();

        if (tempxlist != null && tempxlist.size() > 0) {
            //   int[] yValues = new int[]{4, 3, 9, 5, 35, 30, 33, 32, 46, 33, 56, 42};
            for (int i = 0; i < tempxlist.size(); i++) {
                Float hr = Float.parseFloat(tempylist_hr.get(i));
                Float rr = Float.parseFloat(tempylist_rr.get(i));
                datahr.addPoint(i, hr.intValue());
                datahr.addXValue(i, tempxlist.get(i));
                datarr.addPoint(i, rr.intValue());
            }
            hrrrchart.addData(datahr);
            hrrrchart.addData(datarr);
        }
    }

    public static void ShowLeavebedChart() {

        leavebedchart.clearValues();

        ChartData dataleavebed = new ChartData(ChartData.LINE_COLOR_RED);
        ArrayList<String> tempxlist = showweekreport.getLeavebed_xValues();
        ArrayList<String> tempylist = showweekreport.getLeavebed_yValues();

        if (tempxlist != null && tempxlist.size() > 0) {
            for (int i = 0; i < tempxlist.size(); i++) {

                dataleavebed.addPoint(i, Integer.parseInt(tempylist.get(i)));
                dataleavebed.addXValue(i, tempxlist.get(i));

            }
            leavebedchart.addData(dataleavebed);

        }
    }

    public static void ShowSleepChart() {

        sleepchart.clearValues();

        ChartData datalightsleep = new ChartData(ChartData.LINE_COLOR_GREEN);
        ChartData datadeepsleep = new ChartData(ChartData.LINE_COLOR_PURPLE);
        ChartData dataawake = new ChartData(ChartData.LINE_COLOR_LIGHTGREEN);

        ArrayList<String> tempxlist = showweekreport.getSleep_xValues();
        ArrayList<String> tempylist_lightsleep = showweekreport.getLightsleep_yValues();
        ArrayList<String> tempylist_deepsleep = showweekreport.getDeepsleep_yValues();
        ArrayList<String> tempylist_awake = showweekreport.getAwake_yValues();

        if (tempxlist != null && tempxlist.size() > 0) {
            for (int i = 0; i < tempxlist.size(); i++) {

                datalightsleep.addPoint(i, Integer.parseInt(tempylist_lightsleep.get(i)));
                datalightsleep.addXValue(i, tempxlist.get(i));
                datadeepsleep.addPoint(i, Integer.parseInt(tempylist_deepsleep.get(i)));
                dataawake.addPoint(i, Integer.parseInt(tempylist_awake.get(i)));
            }
            sleepchart.addData(datalightsleep);
            sleepchart.addData(datadeepsleep);
            sleepchart.addData(dataawake);
        }
    }


    public void ClickClose(View v) {
        this.finish();

    }

    //----------------------选择日期--------------------------
    private void showDatePickerFragemnt() {

        fragment.show(ShowWeekReportActivity.this.getFragmentManager(), "datePicker");

    }

    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        private int _year = 2016;
        private int _month = 1;
        private int _day = 1;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO 日期选择完成事件，取消时不会触发
            _year = year;
            _month = monthOfYear + 1;
            _day = dayOfMonth;
            //  Log.i(Constant.LOG_TAG, "year="+year+",monthOfYear="+monthOfYear+",dayOfMonth="+dayOfMonth);

            //根据选择的日期,刷新整个页面
            if(_month<10&&_day<10){
                showweekreport.RefreshWeekreport(_year+"-0"+_month+"-0"+_day);
            }
            else if(_month>9 && _day>9){
                showweekreport.RefreshWeekreport(_year+"-"+_month+"-"+_day);
            }
            else if(_month<10 && _day>9){
                showweekreport.RefreshWeekreport(_year+"-0"+_month+"-"+_day);
            }
            else if(_month>9 && _day<10){
                showweekreport.RefreshWeekreport(_year+"-"+_month+"-0"+_day);
            }


            ShowHrrrChart();
            ShowLeavebedChart();
            ShowSleepChart();

        }

        @Override
        public void show(FragmentManager manager, String tag) {
            super.show(manager, tag);
        }
//        public String getValue(){
//
//            return _year+"/"+_month+"/"+_day;
//        }

    }


    public void showLoginDialog()
    {
        SendEmailFragment dialog = new SendEmailFragment();
        dialog.show(getFragmentManager(), "sendemailDialog");

    }

    @Override
    public void onInputComplete(String emailaddress)
    {
       // Toast.makeText(this, "邮箱：" + address, Toast.LENGTH_SHORT).show();

       Boolean flag = showweekreport.ClickSendEmail(emailaddress);
        if(flag){
            Toast.makeText(this, "发送成功", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "发送失败!请再试一次", Toast.LENGTH_SHORT).show();
        }
    }
}
