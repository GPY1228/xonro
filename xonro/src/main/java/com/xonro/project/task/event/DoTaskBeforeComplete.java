package com.xonro.project.task.event;

import com.actionsoft.bpms.bo.engine.BO;
import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.InterruptListener;
import com.actionsoft.bpms.bpmn.engine.listener.InterruptListenerInterface;
import com.actionsoft.exception.BPMNError;

public class DoTaskBeforeComplete extends InterruptListener implements InterruptListenerInterface {
    @Override
    public boolean execute(ProcessExecutionContext ctx) throws Exception {
        BO bo=ctx.getBO( "BO_XR_PM_TASK_EXECUTE" );
        String progress=bo.getString( "PROGRESS" );
        int a=Integer.valueOf( progress );
        if (a<100){
        throw new BPMNError( "001","当前任务进度仅为:"+progress+",请在进度为100后提交！" );
        }
        return true;
    }
}
