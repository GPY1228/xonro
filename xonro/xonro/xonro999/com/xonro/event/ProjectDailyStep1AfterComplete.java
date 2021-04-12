package com.xonro.event;

import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListener;
import com.actionsoft.bpms.util.DBSql;


/**
 *
 */
public class ProjectDailyStep1AfterComplete extends ExecuteListener {
    @Override
    public String getDescription() {
        return "提交工单，更新数据！" ;
    }
    @Override
    public void execute(ProcessExecutionContext ctx) throws Exception {
    	   //获取当前流程Id
        String bindId=ctx.getProcessInstance().getId();
        //根据事业伙伴编号更新对应门店信息
        StringBuffer str=new StringBuffer();
        str.append("UPDATE BO_XR_PM_PROJECT_DAILY SET ");
        str.append(" MONTH = MONTH(WORK_DATE) ,  ");
        str.append("FINISH_DATE=sysdate()  WHERE BINDID='"+bindId+"'");
        int res =DBSql.update(str.toString());
        System.out.println("执行SQL："+str.toString()+"；结果："+res);
    }
}
