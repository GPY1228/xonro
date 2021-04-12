package com.xonro.project.task.job;

import com.actionsoft.bpms.bo.engine.BO;
import com.actionsoft.bpms.bpmn.engine.model.run.delegate.ProcessInstance;
import com.actionsoft.bpms.commons.database.RowMap;
import com.actionsoft.bpms.schedule.IJob;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.sdk.local.SDK;
import com.actionsoft.sdk.local.api.BOCopyAPI;
import com.actionsoft.sdk.local.api.BOQueryAPI;
import com.xonro.project.biz.DataBiz;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 自动生成周总结
 * @author zhaoqt
 * @date 20200213
 */
public class WeekSummaryJob implements IJob {
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        //获取任务表
        String sql="SELECT * FROM BO_XR_PM_TASK  WHERE YEARWEEK(date_format(PLAN_END_DATE,'%Y-%m-%d')) = YEARWEEK(now(),-1)";
        List<RowMap> list= DBSql.getMaps( sql );
        //匹配人员
        String sqlUser="select * from orguser where ext4='任务' and closed ='0'";
        List<RowMap> users=DBSql.getMaps( sqlUser );
        for (RowMap rowMap:users){
            String userid=rowMap.getString( "USERID" );
            //获取任务中属于此人的任务清单
            List<RowMap> taskList=new ArrayList<>(  );
            taskList=list.stream().filter( rowMap1 -> userid.equals( rowMap1.getString( "TASK_OWNER" ) )).collect( Collectors.toList());
            //创建流程
            ProcessInstance processInstance=SDK.getProcessAPI().createProcessInstance( "obj_ec6c10bb1d6c4762b9999e13563682f2",userid,SDK.getORGAPI().getUser( userid ).getUserName()+"创建的周总结" );
            SDK.getProcessAPI().start( processInstance );
            //创建数据
            BO mainBO=new BO();
            mainBO.set( "USER_ID",userid );
            mainBO.set( "USER_NAME",SDK.getORGAPI().getUser( userid ).getUserName() );
            mainBO.set( "WEEK_STATUS" ,"1");
            SDK.getBOAPI().create( "BO_XR_WEEK_SUMMARY",mainBO,processInstance, UserContext.fromUID( userid ) );
            //创建子表数据
            for (RowMap rowMap1:taskList){
                String id=rowMap1.getString( "ID" );
                //获取bindid
                String oldBindid=rowMap1.getString( "BINDID" );
                BOQueryAPI query = SDK.getBOAPI().query("BO_XR_PM_TASK", true).addQuery("ID =", id);
                // 指定将要复制到新的bo表以及流程实例ID
                BOCopyAPI copyAPI = query.copyTo("BO_XR_WEEK_SUMMARY_TASK", processInstance.getId());
                // 如果新表需要新的值，请添加新数据
                copyAPI.addNewData("PBINDID", oldBindid);
                // 执行复制操作
                copyAPI.exec();
            }
        }
    }
}
