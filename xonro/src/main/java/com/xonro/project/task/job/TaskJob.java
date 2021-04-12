package com.xonro.project.task.job;

import com.actionsoft.bpms.bo.engine.BO;
import com.actionsoft.bpms.commons.database.RowMap;
import com.actionsoft.bpms.schedule.IJob;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.sdk.local.SDK;
import com.xonro.project.task.biz.TaskBIz;
import dm.jdbc.util.StringUtil;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.Date;
import java.util.List;
import java.util.Map;

//自动生成任务
public class TaskJob implements IJob {
    @Override
    public void execute(JobExecutionContext ctx) throws JobExecutionException {
        //获取配置的执行时间
        // 读管理员配置的扩展参数串，支持简单的@公式
        JobDataMap param=ctx.getJobDetail().getJobDataMap();
        String value=param.getString( "aws_schedule_user_param" );
        int num=Integer.valueOf( value );
        //获取当前时间
        Date date=new Date(  );
        String resultdate=TaskBIz.dataAdd( date,num );
        //遍历任务池  获取未开始的任务
        String sql="select * from BO_XR_PM_TASK where IS_SEND = '0' and PLAN_START_DATE < '"+resultdate+"'";
        List<RowMap> list1= DBSql.getMaps( sql );
        for (RowMap map:list1){
            String taskCreater=map.getString( "TASK_CREATER" );
            String boid =map.getString( "ID" );
            //创建任务反馈流程
            if (StringUtil.isNotEmpty( taskCreater )){
                TaskBIz.createTask( UserContext.fromUID( taskCreater ),boid);
            }
        }
    }
}
