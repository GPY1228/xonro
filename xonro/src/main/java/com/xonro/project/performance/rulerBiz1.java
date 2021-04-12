package com.xonro.project.performance;

import com.actionsoft.bpms.bo.engine.BO;
import com.actionsoft.bpms.org.model.UserModel;
import com.actionsoft.sdk.local.SDK;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class rulerBiz1 {
    //考勤
    public static void DeductRuler(String uid, String month) throws ParseException {
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat( "yyyy-MM-dd" );
        //获取项目经理角色组
        String projectManagers = SDK.getAppAPI().getProperty( "com.xonro.apps.xr", "projectManagers" );
        //获取技术经理角色组
        String technicalManagers = SDK.getAppAPI().getProperty( "com.xonro.apps.xr", "technicalManagers" );
        //获取工程师角色组
        String engineers = SDK.getAppAPI().getProperty( "com.xonro.apps.xr", "engineers" );
        List<BO> list = SDK.getBOAPI().query( "BO_XR_DEDUCT" ).addQuery( "DEDUCT_USER_ID=", uid ).list();
        BO bo111111=SDK.getBOAPI().query( "BO_XR_CHECK_DATA" ).addQuery( "USER_ID=",uid ).detail();
        String content = "";
        Double sum = 0.0;
        UserModel userModel = SDK.getORGAPI().getUser( uid );
        String userName1=userModel.getUserName();
        String roleId = userModel.getRoleId();
        //项目工程师
        if (engineers.contains( roleId )) {
            for (BO bo : list) {
                String content1="";
                double sc=0.0;
                String projectCode=bo.getString( "PROJECT_CODE" );
                BO bo2=SDK.getBOAPI().query( "BO_XR_PM_PROJECT" ).addQuery( "PROJECT_CODE=",projectCode ).detail();
                if (bo2==null){
                    continue;
                }
                String proectExecutor=bo2.getString( "PROECT_EXECUTOR" );







                String applyDate = bo.getString( "APPLY_DATE" );
                Date date=simpleDateFormat.parse( applyDate );
                Calendar calendar=Calendar.getInstance();
                calendar.setTime( date );
                String nowMonth= String.valueOf( calendar.get( Calendar.MONTH ) + 1 );
                if (nowMonth.equals( month )){
                    String userName = bo.getString( "USER_NAME" );
                    if (userModel != null) {
                        String deductType = bo.getString( "DEDUCT_TYPE" );
                        //考勤问题
                        if ("1".equals( deductType )) {
                            sum = sum + 1;
                            sc=0.5;
                            content = content+applyDate + "被" + userName + "记录考勤问题;";
                            content1=userName1+applyDate + "日被" + userName + "记录考勤问题;";
                        }
                        //记录问题
                        if ("2".equals( deductType )) {
                            sum = sum + 3;
                            sc=1.5;
                            content = content+applyDate + "被" + userName + "记录纪律问题;";
                            content1=userName1+applyDate + "日被" + userName + "记录纪律问题;";

                        }
                        //客户投诉问题
                        if ("4".equals( deductType )) {
                            sum = sum + 10;
                            sc=5;
                            content = content+applyDate + "被" + userName + "记录客户投诉问题;";
                            content1=userName1+applyDate + "日被" + userName + "记录客户投诉问题;";

                        }
                        //形象问题
                        if ("3".equals( deductType )) {
                            sum = sum + 5;
                            sc=2.5;
                            content = content+applyDate + "被" + userName + "记录形象问题;";
                            content1=userName1+applyDate + "日被" + userName + "记录形象问题;";
                        }
                    }
                }

                if (uid!=proectExecutor){
                    String proectName=bo2.getString( "PROECT_NAME" );
                    BO bo3=SDK.getBOAPI().query( "BO_XR_CHECK_DATA" ).addQuery( "USER_ID=",proectExecutor ).addQuery( "MONTH=",month ).detail();
                    String managerBindid=bo3.getBindId();
                    Double score=Double.valueOf( bo3.getString( "SCORE" ) );
                    score=score-sc;
                    bo3.set( "SCORE",score );
                    SDK.getBOAPI().update( "BO_XR_CHECK_DATA", bo3);
                    //新插入一条数据
                    BO bo4=new BO();
                    bo4.set( "PROJECT_CODE",projectCode );
                    bo4.set( "PROJECT_NAME",proectName );
                    bo4.set( "FROMTO", uid);
                    bo4.set( "REMARK", content1);
                    bo4.set( "SCORE",sc );
                    SDK.getBOAPI().create( "BO_XR_CHECK_XMJLLDKF", bo4,managerBindid,proectExecutor);
                }
            }
            BO bo1 = SDK.getBOAPI().query( "BO_XR_CHECK_LIST" ).addQuery( "ITME_CODE=", "XR-JXZB-003" ).bindId( bo111111.getBindId() ).detail();
            Double score = Double.valueOf( bo1.getString( "SCORE" ) );
            Double result = score - sum;
            bo1.set( "ACTUAL_SCORE", result );
            bo1.set( "REMARK", content );
            SDK.getBOAPI().update( "BO_XR_CHECK_LIST", bo1 );
        }
        //技术经理
        if (technicalManagers.contains( roleId )) {
            for (BO bo : list) {
                String applyDate = bo.getString( "APPLY_DATE" );
                Date date=simpleDateFormat.parse( applyDate );
                Calendar calendar=Calendar.getInstance();
                calendar.setTime( date );
                String nowMonth= String.valueOf( calendar.get( Calendar.MONTH ) + 1 );
                if (nowMonth.equals( month )){
                    String userName = bo.getString( "USER_NAME" );
                    if (userModel != null) {
                        String deductType = bo.getString( "DEDUCT_TYPE" );
                        //考勤问题
                        if ("1".equals( deductType )) {
                            sum = sum + 1;
                            content = content+applyDate + "被" + userName + "记录考勤问题;";
                        }
                        //记录问题
                        if ("2".equals( deductType )) {
                            sum = sum + 3;
                            content = content+applyDate + "被" + userName + "记录纪律问题;";
                        }
                        //客户投诉问题
                        if ("4".equals( deductType )) {
                            sum = sum + 10;
                            content = content+applyDate + "被" + userName + "记录客户投诉问题;";
                        }
                        //形象问题
                        if ("3".equals( deductType )) {
                            sum = sum + 5;
                            content = content+applyDate + "被" + userName + "记录形象问题;";
                        }
                    }
                }
            }
            BO bo1 = SDK.getBOAPI().query( "BO_XR_CHECK_LIST" ).addQuery( "ITME_CODE=", "XR-JXZB-007" ).bindId( bo111111.getBindId() ).detail();
            Double score = Double.valueOf( bo1.getString( "SCORE" ) );
            Double result = score - sum;
            bo1.set( "ACTUAL_SCORE", result );
            bo1.set( "REMARK", content );
            SDK.getBOAPI().update( "BO_XR_CHECK_LIST", bo1 );
        }
    }

    //工单
    public static void DayRuler(String uid, String month) {
        //获取项目经理角色组
        String projectManagers = SDK.getAppAPI().getProperty( "com.xonro.apps.xr", "projectManagers" );
        //获取技术经理角色组
        String technicalManagers = SDK.getAppAPI().getProperty( "com.xonro.apps.xr", "technicalManagers" );
        //获取工程师角色组
        String engineers = SDK.getAppAPI().getProperty( "com.xonro.apps.xr", "engineers" );
        //工单
        List<BO> list = SDK.getBOAPI().query( "BO_XR_PM_TASK" ).addQuery( "TASK_OWNER=", uid ).addQuery( "MONTH=", month ).addQuery( "FLAG=", "7" ).list();
        //周总结
        List<BO> list1 = SDK.getBOAPI().query( "BO_XR_WEEK_SUMMARY" ).addQuery( "MONTH=", month ).addQuery( "USER_ID=", uid ).addQuery( "WEEK_STATUS=", "3" ).list();
        String content = "";
        Double sum = 0.0;
        UserModel userModel = SDK.getORGAPI().getUser( uid );
        String roleId = userModel.getRoleId();
        //项目工程师
        if (engineers.contains( roleId )) {
            for (BO bo : list) {
                String taskNo = bo.getString( "TASK_NO" );
                //获取此项目的项目经理
                BO bo1=SDK.getBOAPI().query( "BO_XR_PM_TASK" ).addQuery( "TASK_NO=",taskNo ).detail();
                String projectCode=bo1.getString( "PROJECT_CODE" );
                BO bo2=SDK.getBOAPI().query( "BO_XR_PM_PROJECT" ).addQuery( "PROJECT_CODE=",projectCode ).detail();
                if (bo2==null){
                    continue;
                }
                if (userModel != null) {
                    //考勤问题
                    sum = sum + 1;
                    content = content+"单号为:" + taskNo + "的任务延时完成;";
                }
                String content1=SDK.getORGAPI().getUser( uid ).getUserName()+"的单号为:" + taskNo + "的任务延时完成;";
                String proectExecutor=bo2.getString( "PROECT_EXECUTOR" );
                String proectName=bo2.getString( "PROECT_NAME" );
                BO bo3=SDK.getBOAPI().query( "BO_XR_CHECK_DATA" ).addQuery( "USER_ID=",proectExecutor ).addQuery( "MONTH=",month ).detail();
                String managerBindid=bo3.getBindId();
                Double score=Double.valueOf( bo3.getString( "SCORE" ) );
                score=score-0.5;
                bo3.set( "SCORE",score );
                SDK.getBOAPI().update( "BO_XR_CHECK_DATA", bo3);
                //新插入一条数据
                BO bo4=new BO();
                bo4.set( "PROJECT_CODE",projectCode );
                bo4.set( "PROJECT_NAME",proectName );
                bo4.set( "FROMTO", uid);
                bo4.set( "REMARK", content1);
                bo4.set( "SCORE",0.5 );
                SDK.getBOAPI().create( "BO_XR_CHECK_XMJLLDKF", bo4,managerBindid,proectExecutor);
            }
            for (BO bo11 : list1) {
                String applyDate = bo11.getString( "APPLY_DATE" );
                sum = sum + 5;
                content = content+applyDate + "日周总结延时提交";
            }
            BO bo1 = SDK.getBOAPI().query( "BO_XR_CHECK_LIST" ).addQuery( "ITME_CODE=", "XR-JXZB-001" ).detail();
            Double score = Double.valueOf( bo1.getString( "SCORE" ) );
            Double result = score - sum;
            bo1.set( "ACTUAL_SCORE", result );
            bo1.set( "REMARK", content );
            SDK.getBOAPI().update( "BO_XR_CHECK_LIST", bo1 );
        }
        //技术经理
        if (technicalManagers.contains( roleId )) {
            for (BO bo : list) {
                String taskNo = bo.getString( "TASK_NO" );
                String flag = bo.getString( "FLAG" );
                if (userModel != null) {
                    //考勤问题
                    sum = sum + 1;
                    content = content+"单号为:" + taskNo + "的任务延时完成;";
                }
            }
            for (BO bo11 : list1) {
                String applyDate = bo11.getString( "APPLY_DATE" );
                sum = sum + 5;
                content = content+applyDate + "日周总结延时提交";
            }
            BO bo1 = SDK.getBOAPI().query( "BO_XR_CHECK_LIST" ).addQuery( "ITME_CODE=", "XR-JXZB-005" ).detail();
            Double score = Double.valueOf( bo1.getString( "SCORE" ) );
            Double result = score - sum;
            bo1.set( "ACTUAL_SCORE", result );
            bo1.set( "REMARK", content );
            SDK.getBOAPI().update( "BO_XR_CHECK_LIST", bo1 );
        }
    }

}
