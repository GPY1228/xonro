package com.xonro.project.task.event;

import com.actionsoft.bpms.bo.engine.BO;
import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListener;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.sdk.local.SDK;
import dm.jdbc.util.StringUtil;

/**
 * 任务保存后事件
 */
public class DoTaskAfterSave extends ExecuteListener {
    @Override
    public void execute(ProcessExecutionContext ctx) throws Exception {
        BO bo=ctx.getBO( "BO_XR_PM_TASK_EXECUTE" );
        if (bo!=null){
            //实际开始时间
            String startDate=bo.getString( "START_DATE" );
            //实际结束时间
            String endDate= bo.getString( "END_DATE" );
            //工作时长
            String workHours=bo.getString( "WORK_HOURS" );
            //进度
            String progress=bo.getString( "PROGRESS" );
            //更新任务池的数据
            //获取任务编号
            String taskNo=bo.getString( "TASK_NO" );
            BO bo1=SDK.getBOAPI().getByKeyField( "BO_XR_PM_TASK","TASK_NO",taskNo );
            bo1.set( "START_DATE",startDate );
            bo1.set( "END_DATE",endDate );
            bo1.set( "WORK_HOURS",workHours);
            bo1.set( "PROGRESS",progress );
            if (StringUtil.isNotEmpty( startDate )){
                bo1.set( "FLAG","2" );
            }
            SDK.getBOAPI().update( "BO_XR_PM_TASK",bo1 );
        }
    }
}
