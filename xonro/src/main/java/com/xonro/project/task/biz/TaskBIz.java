package com.xonro.project.task.biz;

import com.actionsoft.bpms.bo.engine.BO;
import com.actionsoft.bpms.bpmn.engine.model.run.delegate.ProcessInstance;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.sdk.local.SDK;
import com.actionsoft.sdk.local.api.BOCopyAPI;
import com.actionsoft.sdk.local.api.BOQueryAPI;
import com.actionsoft.sdk.local.api.ProcessExecuteQuery;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

//任务公共方法
public class TaskBIz {
    //创建任务反馈流程
    public static String createTask(UserContext userContext,String boId){
        String ip="http://www.xonro.com";
        //获取任务池中此条流程的具体数据
        BO bo=SDK.getBOAPI().getByKeyField( "BO_XR_PM_TASK","ID",boId );
        //获取创建人
        String createUser=bo.getString( "TASK_CREATER" );
        //任务编号
        String taskNo=bo.getString( "TASK_NO" );
        //获取执行人
        String doUser=bo.getString( "TASK_OWNER" );
        //流程标题
        String title=userContext.getUserName()+"提交的任务编号为"+taskNo+"的任务审批";
        //创建任务分配流程
        ProcessInstance processInstance=SDK.getProcessAPI().createProcessInstance( "obj_b0f8242fd1a6476d800426b1834326ce",doUser,title);
        //创建表单数据
        //指定将要复制到新的bo表以及流程实例ID
        BOQueryAPI query = SDK.getBOAPI().query("BO_XR_PM_TASK", true).addQuery("ID=", boId);
        BOCopyAPI copyAPI = query.copyTo("BO_XR_PM_TASK_EXECUTE", processInstance.getId());
        copyAPI.addNewData( "USER_NAME",SDK.getORGAPI().getUser( createUser ).getUserName() );
        copyAPI.addNewData( "USER_ID",createUser );
        // 执行复制操作
        copyAPI.exec();
        SDK.getProcessAPI().start( processInstance );
        String taskinstId=processInstance.getStartTaskInstId();
        //获取表单链接
        String url=ip+"/portal/r/w?sid="+userContext.getSessionId()+"&cmd=CLIENT_BPM_FORM_MAIN_PAGE_OPEN&processInstId="+processInstance.getId()+"&taskInstId="+taskinstId+"&openState=1";
        String sql = "update BO_XR_PM_TASK set IS_SEND = '1' where ID='"+boId+"'";
        DBSql.update( sql );
        return url;
    }
    //日期加
    public static String dataAdd(Date date,int day) {
        String returnDate = null;
        try {
            Calendar calendar = new GregorianCalendar();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            calendar.setTime(date);
            calendar.add(Calendar.DATE, day);
            date = calendar.getTime();
            returnDate = sdf.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnDate;
    }
    //任务取消流程
    public static String cancelTask(UserContext userContext,String boId){
        String ip="http://www.xonro.com";
        //获取任务池中此条流程的具体数据
        BO bo=SDK.getBOAPI().getByKeyField( "BO_XR_PM_TASK","ID",boId );
        //获取创建人
        String createUser=bo.getCreateUser();
        //获取执行人
        String doUser=bo.getString( "TASK_OWNER" );
        //创建任务分配流程
        ProcessInstance processInstance=SDK.getProcessAPI().createProcessInstance( "obj_b0f8242fd1a6476d800426b1834326ce",createUser,"由"+SDK
                .getORGAPI().getUser( createUser ).getUserName()+"发起的【"+bo.getString( "TASK_NAME" )+"】任务的移交流程"+SDK.getORGAPI().getUser( doUser ).getUserName()+"的任务反馈流程" );
        //创建表单数据
        //指定将要复制到新的bo表以及流程实例ID
        BOQueryAPI query = SDK.getBOAPI().query("BO_XR_PM_TASK", true).addQuery("ID=", boId);
        BOCopyAPI copyAPI = query.copyTo("BO_XR_PM_TASK_EXECUTE", processInstance.getId());
        // 执行复制操作
        copyAPI.exec();
        SDK.getProcessAPI().start( processInstance );
        //获取表单链接
        String url=ip+"/portal/r/w?sid="+userContext.getSessionId()+"&cmd=CLIENT_BPM_FORM_MAIN_PAGE_OPEN&processInstId="+processInstance.getId();
        return url;
    }
    //任务变更流程
    public static String changeTask(UserContext userContext,String boId){
        String ip="http://www.xonro.com";
        //获取任务池中此条流程的具体数据
        BO bo=SDK.getBOAPI().getByKeyField( "BO_XR_PM_TASK","ID",boId );
        //获取创建人
        String createUser=bo.getString( "TASK_CREATER" );
        //获取执行人
        String doUser=bo.getString( "TASK_OWNER" );
        String title=userContext.getUserName()+"提交的任务编号为"+bo.getString( "TASK_NO" )+"的任务审批";
//        String title=SDK.getRuleAPI().executeAtScript( "@form(BO_XR_PM_TASK_MODIFY,USER_NAME)提交的任务编号@form(BO_XR_PM_TASK_MODIFY,TASK_NO)任务审批" );
        //创建任务分配流程
        ProcessInstance processInstance=SDK.getProcessAPI().createProcessInstance( "obj_48ed13ecb15442c8a451329de2bb0360",createUser,title );
        //创建表单数据
        //指定将要复制到新的bo表以及流程实例ID
        BOQueryAPI query = SDK.getBOAPI().query("BO_XR_PM_TASK", true).addQuery("ID=", boId);
        BOCopyAPI copyAPI = query.copyTo("BO_XR_PM_TASK_MODIFY", processInstance.getId());
        copyAPI.addNewData( "USER_NAME", SDK.getORGAPI().getUser( createUser ).getUserName());
        copyAPI.addNewData( "USER_ID",createUser);
        copyAPI.addNewData( "DEPARTMENT_NAME",SDK.getORGAPI().getDepartmentById( SDK.getORGAPI().getUser( createUser ).getDepartmentId()  ).getName());
        copyAPI.removeMapping( "REMARK" );
        // 执行复制操作
        copyAPI.exec();
        SDK.getProcessAPI().start( processInstance );
        String taskinstId=processInstance.getStartTaskInstId();
        //更新任务状态为执行中
        String sql = "update BO_XR_PM_TASK set FLAG = '5' where ID='"+boId+"'";
        System.out.println( "---------------sql:"+sql );
        DBSql.update( sql );
        //获取表单链接
        String url=ip+"/portal/r/w?sid="+userContext.getSessionId()+"&cmd=CLIENT_BPM_FORM_MAIN_PAGE_OPEN&processInstId="+processInstance.getId()+"&taskInstId="+taskinstId+"&openState=1";
        return url;
    }
}
