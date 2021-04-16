package com.dql.project.event;

import com.actionsoft.bpms.bo.engine.BO;
import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.servicetask.ServiceDelegate;
import com.actionsoft.bpms.util.UtilDate;
import com.actionsoft.sdk.local.SDK;
import com.actionsoft.sdk.local.api.BOCopyAPI;
import com.dql.project.biz.CommonBiz;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 工资支付申请
 */
public class SalaryPayApplyService extends ServiceDelegate {
    public SalaryPayApplyService() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public boolean execute(ProcessExecutionContext ctx) throws Exception {
        String bindId = ctx.getProcessInstance().getId();
        String appId = ctx.getProcessDef().getAppId();
        //工资申请
        BO fmExpense = ctx.getBO("BO_XR_FM_SALARY_PAY");
        String year = fmExpense.getString("YEAR");
        String month = fmExpense.getString("MONTH");
        String remark = fmExpense.getString("REMARK");
        String applyNo = fmExpense.getString("APPLY_NO");
        String applyDate = fmExpense.getString("APPLY_DATE");
        //申请人
        String userName = fmExpense.getString("USER_NAME");

        //生成档案
        List<BO> salarySheet = SDK.getBOAPI().query("BO_XR_FM_SALARY_SHEET").bindId(bindId).list();
        if(salarySheet != null && salarySheet.size() > 0){
            for (BO bo : salarySheet) {
                String boId = bo.getId();
                //出勤天数
                float days = bo.get("DAYS",Float.class);
                float saraly = amountSaraly(bo);
                float results = (float) Math.round(saraly/days*100)/100;
                //复制数据
                BOCopyAPI copyAPI = CommonBiz.salaryPayInfo("BO_XR_FM_SALARY_SHEET", boId);
                copyAPI.addNewData("YEAR",year);
                copyAPI.addNewData("MONTH",month);
                copyAPI.addNewData("DAILY_COST",results);
                // 执行复制操作
                copyAPI.exec();
            }
        }
        if(salarySheet != null && salarySheet.size() > 0){
            for (BO bo : salarySheet) {
                String boId = bo.getId();
                String name = bo.getString("USER_NAME");
                float saraly = amountSaraly(bo);
                float results = (float) Math.round(saraly*100)/100;

                BOCopyAPI copyAPI = CommonBiz.expensePayInfo("BO_XR_FM_SALARY_SHEET", boId);
                copyAPI.addNewData("YEAR",year);
                copyAPI.addNewData("MONTH",month);
                //是否公开
                copyAPI.addNewData("IS_OPEN","0");
                copyAPI.addNewData("REASON",remark);
                copyAPI.addNewData("AMOUNT",results);
                copyAPI.addNewData("FLAG","1");

                //关联单据ID
                copyAPI.addNewData( "LINK_BINDID","GZ:"+bindId);
                copyAPI.addNewData("REMARK","来源于【"+userName+"】提交的【"+applyNo+"】工资发放流程，申请日期【"+applyDate+"】，员工名称【"+name+"】，支付费用【"+results+"】");
                // 执行复制操作
                copyAPI.exec();
            }
        }
        return true;
    }

    public float amountSaraly(BO bo){
        //税前工资
        float salarySq = bo.get("SALARY_SQ",Float.class);
        //补助-通讯费
        float subsidyTxf = bo.get("SUBSIDY_TXF",Float.class);
        //补助-午餐费
        float subsidyWc = bo.get("SUBSIDY_WC",Float.class);
        //补助-笔记本
        float subsidyBjb = bo.get("SUBSIDY_BJB",Float.class);
        //补助-全勤
        float subsidyQq = bo.get("SUBSIDY_QQ",Float.class);
        //奖金
        float bonus = bo.get("BONUS",Float.class);
        //社保_公司部分
        float securityC = bo.get("SOCIAL_SECURITY_C",Float.class);
        //公积金_公司部分
        float surplusC = bo.get("SURPLUS_C",Float.class);
        //补充公积金
        float extraSurplus = bo.get("EXTRA_SURPLUS",Float.class);
        //应扣金额
        float deductAmount = bo.get("DEDUCT_AMOUNT",Float.class);

        float results = (salarySq + subsidyTxf + subsidyWc + subsidyBjb + subsidyQq + bonus + securityC + surplusC + extraSurplus - deductAmount);
        return results;
    }
}
