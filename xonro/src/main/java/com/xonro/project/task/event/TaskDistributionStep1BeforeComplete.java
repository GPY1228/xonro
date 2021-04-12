package com.xonro.project.task.event;

import com.actionsoft.bpms.bo.engine.BO;
import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.model.run.delegate.ProcessInstance;
import com.actionsoft.bpms.bpmn.engine.servicetask.ServiceDelegate;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.sdk.local.SDK;
import com.actionsoft.sdk.local.api.BOCopyAPI;
import com.actionsoft.sdk.local.api.BOQueryAPI;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 任务分配流程系统事件——更新任务台账
 * @author zhaoqt
 * @date 20200213
 */
public class TaskDistributionStep1BeforeComplete extends ServiceDelegate {
    @Override
    public boolean execute(ProcessExecutionContext ctx) throws Exception {
        //bindid
        String bindid = ctx.getProcessInstance().getId();
        //任务池uuid
        String DWID=SDK.getAppAPI().getProperty( "","" );
        DWID="obj_07e0522439554d4aa5da1f34b004da6d";
        //获取主表数据
        BO mainBo=ctx.getBO( "BO_XR_PM_TASK_ARRANGE" );
        //获取主表申请人
        String applyUser=mainBo.getString( "USER_ID" );
        //获取子表数据
        List<BO> gridBo=SDK.getBOAPI().query( "BO_XR_PM_TASK_ARRANGE_LIST" ).bindId( bindid).list();
        //将数据复制给项目任务池
        if (!gridBo.isEmpty()){
            for (BO bo:gridBo){
                //获取任务执行人
                String doUser=bo.getString( "TASK_OWNER" );
                String result="";
                List<BO> list =SDK.getBOAPI().query( "BO_XR_PM_TASK" ).addQuery( "PROJECT_CODE=",bo.getString( "PROJECT_CODE" ) ).list();
                if (!list.isEmpty()){
                    int num =list.size()+1;
                    if (num<10){
                        result = "000"+num;
                    }else if (9<num && num<100){
                        result = "00"+num;
                    }else if (99<num){
                        result = "0"+num;
                    }
                }else {
                    result = "0001";
                }
                //创建任务池数据
                ProcessInstance processInstance = SDK.getProcessAPI().createBOProcessInstance( DWID, ctx.getProcessInstance().getCreateUser(), SDK.getORGAPI().getUser( ctx.getProcessInstance().getCreateUser() ).getUserName()+"给"+SDK.getORGAPI().getUser( doUser ).getUserName()+"安排的任务，编号:"+ bo.getString( "PROJECT_CODE" )+"-"+result);
                //获取BO的ID
                String id = bo.getId();
                BOQueryAPI query = SDK.getBOAPI().query("BO_XR_PM_TASK_ARRANGE_LIST", true).addQuery("ID=", id);
                BO bo1=query.detail();
                int year=0;
                int month=0;
                String day="";
                String week="";
                if (bo1!=null){
                    //
                    String planStartDate=bo1.getString( "PLAN_START_DATE" );
                    SimpleDateFormat sdf=new SimpleDateFormat( "yyyy-MM-dd" );
                    Date date=sdf.parse( planStartDate );
                    Calendar calendar=Calendar.getInstance();
                    calendar.setFirstDayOfWeek( 2 );
                    calendar.setTime( date );
                    month=calendar.get( Calendar.MONTH )+1;
                    year=calendar.get( Calendar.YEAR );
                    day=String.valueOf(  calendar.get( Calendar.DAY_OF_MONTH ));
                    week=String.valueOf( calendar.get( Calendar.WEEK_OF_YEAR ) );
                }
                // 指定将要复制到新的bo表以及流程实例ID
                BOCopyAPI copyAPI = query.copyTo("BO_XR_PM_TASK", processInstance.getId());
                // 如果源表和新表的字段名称不一样，请添加字段映射关系
                //任务创建人
                copyAPI.addNewData( "TASK_CREATER",applyUser );
                //任务状态
                copyAPI.addNewData( "FLAG","1" );
                //进度
                copyAPI.addNewData( "PROGRESS",0 );
                //任务编号    任务池中根据项目分类，然后依次增序
                copyAPI.addNewData( "TASK_NO", bo.getString( "PROJECT_CODE" )+"-"+result);
                copyAPI.addNewData( "YEAR",year );
                copyAPI.addNewData( "MONTH",month);
                copyAPI.addNewData( "DAY",day);
                copyAPI.addNewData( "WEEK",week );
                copyAPI.addNewData( "IS_SEND","0");
                // 执行复制操作
                copyAPI.exec();
            }
        }
        return true;
    }
}
