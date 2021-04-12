package com.xonro.project.task.event;

import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.InterruptListener;
import com.actionsoft.bpms.bpmn.engine.listener.InterruptListenerInterface;

public class BXStep1BeforeComplete extends InterruptListener implements InterruptListenerInterface {
    @Override
    public boolean execute(ProcessExecutionContext ctx) throws Exception {
        ctx.getBO( "BO_XR_FM_EXPENSE" );
        return false;
    }
}
