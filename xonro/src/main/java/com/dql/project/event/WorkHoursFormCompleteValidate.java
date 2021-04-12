package com.dql.project.event;

import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.InterruptListener;
import com.actionsoft.bpms.bpmn.engine.listener.InterruptListenerInterface;
import com.actionsoft.bpms.util.DBSql;

import java.sql.Connection;

/**
 * @author GPY
 * @date 2021/4/6
 * @des 工作时长校验
 */
public class WorkHoursFormCompleteValidate extends InterruptListener implements InterruptListenerInterface {

    @Override
    public String getDescription() {
        return "工作时长导入时不能超过8小时";
    }

    @Override
    public boolean execute(ProcessExecutionContext processExecutionContext) throws Exception {
        String selectCountSql = "select count(1) AS countWorkHours from BO_XR_PM_IMP_TIME_COST_LIST where WORK_HOURS > 8 ";
        int countHours = DBSql.getInt((Connection) null,selectCountSql);
        if (countHours>0){
            return false;
        }
        return true;
    }
}
