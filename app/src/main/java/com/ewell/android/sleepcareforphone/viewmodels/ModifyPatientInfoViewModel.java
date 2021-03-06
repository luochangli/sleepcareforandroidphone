package com.ewell.android.sleepcareforphone.viewmodels;

import android.databinding.Bindable;
import android.view.View;
import android.widget.Toast;

import com.ewell.android.bll.DataFactory;
import com.ewell.android.common.Grobal;
import com.ewell.android.common.exception.EwellException;
import com.ewell.android.ibll.SleepcareforPhoneManage;
import com.ewell.android.model.BedUserInfo;
import com.ewell.android.model.EquipmentInfo;
import com.ewell.android.model.ServerResult;
import com.ewell.android.sleepcareforphone.activities.ModifyPatientInfoActivity;

import java.util.ArrayList;

/**
 * Created by lillix on 7/21/16.
 */
public class ModifyPatientInfoViewModel extends BaseViewModel {
    private ModifyPatientInfoActivity modifypatientinfoActivity;

    public void SetParentActivity(ModifyPatientInfoActivity mmodifyActivity) {
        modifypatientinfoActivity = mmodifyActivity;
    }

    private String textpatientname = "";

    public void setPatientName(String name) {
        textpatientname = name;
    }

    @Bindable
    public String getPatientName() {
        return textpatientname;
    }


    private String textpatienttelephone;

    @Bindable
    public String getPatientTelephone() {
        return textpatienttelephone;
    }

    public void setPatientTelephone(String telephone) {
        this.textpatienttelephone = telephone;
    }


    private String textpatientaddress = "";

    @Bindable
    public String getPatientAddress() {
        return textpatientaddress;
    }

    public void setPatientAddress(String address) {
        this.textpatientaddress = address;
    }

    private Boolean isFemale = false;

    public Boolean getIsFemale() {
        return isFemale;
    }

    public void setIsFemale(Boolean isfemale) {
        this.isFemale = isfemale;
    }


    private Boolean isMale = false;

    public Boolean getIsMale() {
        return isMale;
    }

    public void setIsMale(Boolean ismale) {
        this.isMale = ismale;
    }

    private SleepcareforPhoneManage sleepcareforPhoneManage = DataFactory.GetSleepcareforPhoneManage();

    private String sex = "";
    private String textpatientcode = "";


    private String equipmentID = "";


    //-----------界面处理事件
    public void InitData() {
        try {
            if (Grobal.getXmppManager().Connect()) {
String loginuser = Grobal.getInitConfigModel().getLoginUserName();
                ArrayList<EquipmentInfo> tempequipmentlist = sleepcareforPhoneManage.GetEquipmentsByLoginName(loginuser).getEquipmentInfoList();
                String curbedusercode = Grobal.getInitConfigModel().getCurUserCode();
                for (EquipmentInfo equipment : tempequipmentlist) {
                    if (equipment.getBedUserCode().equals(curbedusercode)) {
                        equipmentID = equipment.getEquipmentID();
                        break;
                    }
                }

                if (equipmentID.equals("")) {
                    Toast.makeText(modifypatientinfoActivity, "当前账户下没有绑定设备!请先去'我的设备'进行添加", Toast.LENGTH_SHORT).show();
                } else {

                    BedUserInfo tempBedUserInfo = sleepcareforPhoneManage.GetBedUserInfoByEquipmentID(equipmentID);
                    this.textpatientname = tempBedUserInfo.getBedUserName();
                    this.textpatientcode = tempBedUserInfo.getBedUserCode();
                    this.textpatienttelephone = tempBedUserInfo.getMobilePhone();
                    this.textpatientaddress = tempBedUserInfo.getAddress();
                    this.sex = tempBedUserInfo.getSex();
                    if (sex.equals("1")) {
                        this.isMale = true;
                    } else if (sex.equals("2")) {
                        this.isFemale = true;
                    }

                }
            } else {
                Toast.makeText(modifypatientinfoActivity,"无法连接服务器!", Toast.LENGTH_SHORT).show();
            }

        } catch
                (EwellException ex) {
            //做消息弹窗提醒
            Toast.makeText(modifypatientinfoActivity, ex.getExceptionMsg(), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            //做消息弹窗提醒
            e.printStackTrace();
        }

    }

    public void ConfirmCommand(View view) {
        try {

            if (Grobal.getXmppManager().Connect()) {
                if (isFemale) {
                    sex = "2";
                } else if (isMale) {
                    sex = "1";
                } else {
                    sex = "";
                }

                ServerResult result = sleepcareforPhoneManage.ModifyBedUserInfo(this.textpatientcode, this.textpatientname, this.sex, this.textpatienttelephone, this.textpatientaddress);
                if (result.getResult()) {
                    Toast.makeText(modifypatientinfoActivity, "修改老人信息成功!", Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(modifypatientinfoActivity, "修改老人信息失败!", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(modifypatientinfoActivity,"无法连接服务器!", Toast.LENGTH_SHORT).show();
            }
        } catch (EwellException ex) {
            //做消息弹窗提醒
            Toast.makeText(modifypatientinfoActivity, ex.getExceptionMsg(), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            //做消息弹窗提醒
            e.printStackTrace();
        }

    }

}
