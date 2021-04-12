package com.dql.project.event;

import com.actionsoft.bpms.bo.engine.BO;
import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListener;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListenerInterface;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.bpms.util.UtilDate;
import com.actionsoft.sdk.local.SDK;
import com.actionsoft.sdk.local.api.BOCopyAPI;
import com.dql.project.biz.CommonBiz;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 费用报销
 */
public class ExpensePayLastTaskAfterComplete extends ExecuteListener implements ExecuteListenerInterface {
    @Override
    public String getDescription() {
        return "更新支付时间、状态";
    }

    @Override
    public void execute(ProcessExecutionContext ctx) throws Exception {
        String bindId = ctx.getProcessInstance().getId();
        BO xrFmExpense = ctx.getBO("BO_XR_FM_EXPENSE");
        if(ctx.isChoiceActionMenu("支付完毕")){
            String payDate = xrFmExpense.getString("PAY_DATE");
            System.out.println("payDate: "+payDate);
            String sql = "update BO_XR_FM_COST set FLAG='1',PAY_DATE=STR_TO_DATE(?,'%Y-%c-%d') where LINK_BINDID=?";
            System.out.println("sql: " + sql);
            DBSql.update(sql,new Object[]{payDate,"BX:"+bindId});
        }
    }
}
