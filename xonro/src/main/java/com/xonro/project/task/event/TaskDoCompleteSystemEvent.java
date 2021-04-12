package com.xonro.project.task.event;

import com.actionsoft.bpms.bo.engine.BO;
import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.servicetask.ServiceDelegate;
import com.actionsoft.bpms.commons.database.RowMap;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.sdk.local.SDK;
import com.actionsoft.sdk.local.api.BOCopyAPI;
import com.actionsoft.sdk.local.api.BOQueryAPI;
import dm.jdbc.util.StringUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TaskDoCompleteSystemEvent extends ServiceDelegate {
    @Override
    public boolean execute(ProcessExecutionContext ctx) throws Exception {
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd" );
        BO bo=ctx.getBO( "BO_XR_PM_TASK_EXECUTE" );
        String taskNo=bo.getString( "TASK_NO" );
        //获取项目编号
        String projectCode=bo.getString( "PROJECT_CODE" );
        if (StringUtil.isEmpty( taskNo )){
            return false;
        }
        String sql="select * from BO_XR_PM_TASK where TASK_NO='"+taskNo+"'";
        RowMap rowMap= DBSql.getMap( sql );
        String id = rowMap.getString( "ID" );
        BOQueryAPI query = SDK.getBOAPI().query("BO_XR_PM_TASK_EXECUTE", true).addQuery("ID=", bo.getId());
        // 指定将要复制到新的bo表以及流程实例ID
        //计算任务耗时
        BO bo1=query.detail();
        //获取流程ID
        String bindid=bo1.getBindId();
        //获取预计执行开始时间
        String planEndDate=bo1.getString( "PLAN_END_DATE" );
        Calendar cal11=Calendar.getInstance();
        Long planTime=sdf.parse( planEndDate ).getTime();
        String endDate=bo1.getString( "END_DATE" );
        Long endTime=sdf.parse( endDate ).getTime();
        int year=0;
        int month=0;
        String day="";
        String week="";
        if (bo1!=null){
            //
            String planStartDate=bo1.getString( "PLAN_START_DATE" );
            cal11.setTime( sdf.parse( planStartDate ) );
            int mon =cal11.get( Calendar.MONTH )+1;
            Date date=sdf.parse( planStartDate );
            Calendar calendar=Calendar.getInstance();
            calendar.setFirstDayOfWeek( 2 );
            calendar.setTime( date );
            month=calendar.get( Calendar.MONTH )+1;
            year=calendar.get( Calendar.YEAR );
            day=String.valueOf(  calendar.get( Calendar.DAY_OF_MONTH ));
            week=String.valueOf(  calendar.get( Calendar.WEEK_OF_YEAR));
        }


        //获取实际开始时间
        Date date=bo1.getCreateDate();
        long sTime=date.getTime();
        Date date1=new Date(  );
        long eTIme=date1.getTime();
        long cost=eTIme-sTime;
        //计算偏差率
        //预估时长
        String planHours=bo1.getString( "PLAN_HOURS" );
        //实际时长
        String workHours=bo1.getString( "WORK_HOURS" );
        BigDecimal bigDecimalPlan=new BigDecimal( planHours );
        BigDecimal bigDecimalWork=new BigDecimal( workHours );
        //偏差率
        BigDecimal result=(bigDecimalPlan.subtract( bigDecimalWork )).divide( bigDecimalPlan,1, RoundingMode.HALF_UP );

        //转换成小时
        Double time=cost/(60*60*1000*1.00);
        BOCopyAPI copyAPI = query.copyTo("BO_XR_PM_TASK", rowMap.getString( "BINDID" ));
        if ((planTime-endTime)<0){
            copyAPI.addNewData( "FLAG","7" );
            //更新绩效台账数据
            //获取任务执行人
            String taskOwner=bo1.getString( "TASK_OWNER" );
            BO bo2=SDK.getBOAPI().query( "BO_XR_CHECK_DATA" ).addQuery( "USER_ID=",taskOwner ).addQuery( "MONTH=",month ).addQuery( "YEAR=",cal11.get( Calendar.YEAR ) ).detail();
            if (bo2!=null){
                String bindid11=bo2.getBindId();
                BO bo3=SDK.getBOAPI().query( "BO_XR_CHECK_LIST" ).bindId( bindid11 ).addQuery( "ITME_CODE=XR-JXZB-001", null).detail();
                if (bo3!=null){
                    String remark=bo3.getString( "REMARK" );
                    String content="任务编号为"+taskNo+"的任务延时完成;";
                    remark=remark+content;
                    bo3.set( "REMARK",remark );
                    SDK.getBOAPI().update( "BO_XR_CHECK_LIST",bo3 );
                }
                BO bo4=SDK.getBOAPI().query( "BO_XR_CHECK_LIST" ).bindId( bindid11 ).addQuery( "ITME_CODE=XR-JXZB-005", null).detail();
                if (bo4!=null){
                    String remark=bo3.getString( "REMARK" );
                    String content="任务编号为"+taskNo+"的任务延时完成;";
                    remark=remark+content;
                    bo3.set( "REMARK",remark );
                    SDK.getBOAPI().update( "BO_XR_CHECK_LIST",bo3 );
                }
                //项目经理连带扣分
                BO projectBo=SDK.getBOAPI().query( "BO_XR_PM_PROJECT" ).addQuery( "PROJECT_CODE=",projectCode ).detail();
                //获取项目经理
//                projectBo.getString( "" );
            }
        }else {
            copyAPI.addNewData( "FLAG","3" );
        }
        copyAPI.addNewData( "ORGID",rowMap.getString( "ORGID" ) );
        copyAPI.addNewData( "CREATEDATE",rowMap.getString( "CREATEDATE" ) );
        copyAPI.addNewData( "CREATEUSER",rowMap.getString( "CREATEUSER" ) );
        copyAPI.addNewData( "TASK_COST",time );
        copyAPI.addNewData( "TASK_RATIO",result );
        copyAPI.addNewData( "P_BINDID",bindid);
        copyAPI.addNewData( "YEAR",year );
        copyAPI.addNewData( "MONTH",month);
        copyAPI.addNewData( "DAY",day);
        copyAPI.addNewData( "WEEK",week);
        // 如果源表和新表的字段名称不一样，请添加字段映射关系
        // 执行复制操作
        copyAPI.exec();
        //删除之前的数据
        String sql1="delete from BO_XR_PM_TASK where ID='"+id+"'";
        DBSql.update( sql1 );
        return true;
    }
}
