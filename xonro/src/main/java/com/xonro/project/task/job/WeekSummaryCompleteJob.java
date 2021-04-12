package com.xonro.project.task.job;

import com.actionsoft.bpms.bo.engine.BO;
import com.actionsoft.bpms.commons.database.RowMap;
import com.actionsoft.bpms.schedule.IJob;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.sdk.local.SDK;
import dm.jdbc.util.StringUtil;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.Date;
import java.util.List;

public class WeekSummaryCompleteJob implements IJob {
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        String sql="select * from wfc_task where PROCESSDEFID ='obj_ec6c10bb1d6c4762b9999e13563682f2' and ACTIVITYDEFID ='obj_6d8cfc62fae947cfa82e7cfccda3914b'";
        //获取当前日期
        Date date = new Date();
        List<RowMap> list=DBSql.getMaps( sql );
        if (list!=null && list.size()>0){
            for (RowMap rowMap:list){
                try{
                    String taskId=rowMap.getString( "ID" );
                    //获取办理人
                    String target=rowMap.getString( "TARGET" );
                    if (StringUtil.isEmpty( target )){
                        continue;
                    }
                    String bindid=rowMap.getString( "PROCESSINSTID" );
                    //更新主表状态为3
                    BO bo=SDK.getBOAPI().getByProcess( "BO_XR_WEEK_SUMMARY",bindid );
                    bo.set( "WEEK_STATUS","3" );
                    bo.set( "SUBMIT_DATE",date );
                    System.out.println( target );
                    SDK.getBOAPI().update( "BO_XR_WEEK_SUMMARY",bo );
                    //提交流程
                    SDK.getTaskAPI().completeTask( taskId, target);
                    SDK.getTaskAPI().commitComment( taskId,"admin","系统处理","<span color=\"red\"><b>周总结超时，系统自动提交</b><span>",true );
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
}
