package com.xonro.project.task.event;

import com.actionsoft.bpms.bo.engine.BO;
import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.InterruptListener;
import com.actionsoft.bpms.bpmn.engine.listener.InterruptListenerInterface;
import com.actionsoft.exception.BPMNError;
import com.actionsoft.sdk.local.SDK;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class TaskDisTriButionStep1Validate extends InterruptListener implements InterruptListenerInterface {
    @Override
    public boolean execute(ProcessExecutionContext ctx) throws Exception {
        SimpleDateFormat sdf=new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
        //获取当前时间
        Date date =new Date();
        long nowDate=date.getTime();
        String bindid=ctx.getProcessInstance().getId();
        //获取子表数据
        List<BO> list= SDK.getBOAPI().query( "BO_XR_PM_TASK_ARRANGE_LIST" ).bindId( bindid ).list();
        for (BO bo:list){
            String planStartDate=bo.getString( "PLAN_START_DATE" );
            planStartDate = planStartDate+" 23:59:59";
            Long planTime=sdf.parse( planStartDate ).getTime();
            if (planTime<nowDate){
                throw new BPMNError("0312","计划开始时间必须为当天或当天以后！");
            }
            String planEndDate=bo.getString( "PLAN_END_DATE" );
            planEndDate = planEndDate + " 23:59:59";
            if (!planStartDate.equals( planEndDate )){
                throw new BPMNError("0312","请控制计划开始时间与计划结束时间在同一天！");
            }
            String taskName=bo.getString( "TASK_NAME" );
            String taskDesc=bo.getString( "TASK_DESC" );
            if (taskName.equals( taskDesc )){
                throw new BPMNError("0312","任务名称与任务描述不能相同！");
            }
        }
        return true;
    }
}
