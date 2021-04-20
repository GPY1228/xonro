package com.dql.project.event;

import com.actionsoft.apps.resource.db.DBScript;
import com.actionsoft.bpms.bo.engine.BO;
import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListener;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListenerInterface;
import com.actionsoft.bpms.commons.database.RowMap;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.sdk.local.SDK;
import com.actionsoft.sdk.local.api.BOAPI;

import java.util.List;

/**
 * @author GPY
 * @date 2021/4/14
 * @des 工资发放完成后事件（将工资明细添加到员工工资台账中）
 */
public class SalaryTaskAfterComplete extends ExecuteListener implements ExecuteListenerInterface {

    @Override
    public String getDescription() {
        return "工资发放完成后事件（将工资明细添加到员工工资台账中）";
    }

    @Override
    public void execute(ProcessExecutionContext processExecutionContext) throws Exception {
        String bindId = processExecutionContext.getProcessInstance().getId();
        String selectSalary = " SELECT s.*,p.YEAR,p.`MONTH` FROM BO_XR_FM_SALARY_SHEET s left join BO_XR_FM_SALARY_PAY p on s.BINDID = p.BINDID WHERE s.BINDID = '"+bindId+"' ";
        List<RowMap> getUsersSalaryLists = DBSql.getMaps(selectSalary);
        getUsersSalaryLists.forEach(s->{
            BO bo = new BO();
            bo.set("YEAR",s.getString("YEAR"));
            bo.set("MONTH",s.getString("MONTH"));
            bo.set("USER_ID",s.getString("USER_ID"));
            bo.set("USER_NAME",s.getString("USER_NAME"));
            bo.set("DEPT_NAME",s.getString("DEPT_NAME"));
            bo.set("SALARY_SQ",s.getString("SALARY_SQ"));
            bo.set("SUBSIDY_TXF",s.getString("SUBSIDY_TXF"));
            bo.set("SUBSIDY_WC",s.getString("SUBSIDY_WC"));
            bo.set("SUBSIDY_BJB",s.getString("SUBSIDY_BJB"));
            bo.set("SUBSIDY_QQ",s.getString("SUBSIDY_QQ"));
            bo.set("BONUS",s.getString("BONUS"));
            bo.set("SOCIAL_SECURITY_P",s.getString("SOCIAL_SECURITY_P"));
            bo.set("SOCIAL_SECURITY_C",s.getString("SOCIAL_SECURITY_C"));
            bo.set("SURPLUS_P",s.getString("SURPLUS_P"));
            bo.set("SURPLUS_C",s.getString("SURPLUS_C"));
            bo.set("EXTRA_SURPLUS",s.getString("EXTRA_SURPLUS"));
            bo.set("DEDUCT_AMOUNT",s.getString("DEDUCT_AMOUNT"));
            bo.set("SALARY",s.getString("SALARY"));
            bo.set("DAYS",s.getString("DAYS"));
            //计算日均成本
            Double salaryCount = Double.valueOf(s.getString("SALARY_SQ"));
            salaryCount += Double.valueOf(s.getString("SUBSIDY_TXF"));
            salaryCount += Double.valueOf(s.getString("SUBSIDY_WC"));
            salaryCount += Double.valueOf(s.getString("SUBSIDY_BJB"));
            salaryCount += Double.valueOf(s.getString("SUBSIDY_QQ"));
            salaryCount += Double.valueOf(s.getString("BONUS"));
            salaryCount += Double.valueOf(s.getString("SOCIAL_SECURITY_C"));
            salaryCount += Double.valueOf(s.getString("SURPLUS_C"));
            salaryCount += Double.valueOf(s.getString("EXTRA_SURPLUS"));
            Double deductAmount = Double.valueOf(s.getString("DEDUCT_AMOUNT"));
            Double days = Double.valueOf(s.getString("DAYS"));
            bo.set("DAILY_COST",String.format("%.2f", (salaryCount-deductAmount)/days));
            String managerBindid=s.getString("BINDID");
            SDK.getBOAPI().create("BO_XR_FM_SALARY",bo,managerBindid,"");
        });

    }
}
