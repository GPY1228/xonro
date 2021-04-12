package com.dql.project.event;

import com.actionsoft.bpms.bo.engine.BO;
import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.servicetask.ServiceDelegate;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.bpms.util.UtilDate;
import com.actionsoft.sdk.local.SDK;
import com.actionsoft.sdk.local.api.BOCopyAPI;
import com.dql.project.biz.CommonBiz;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.List;

/**
 * 支付申请
 */
public class PayApplyService extends ServiceDelegate {
    public PayApplyService() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public boolean execute(ProcessExecutionContext ctx) throws Exception {
        String bindId = ctx.getProcessInstance().getId();
        String appId = ctx.getProcessDef().getAppId();
        //支付申请
        BO fmExpense = ctx.getBO("BO_XR_FM_PAY");
        String applyNo = fmExpense.getString("APPLY_NO");
        String boId = fmExpense.getId();
        //客户名称
        String customerName = fmExpense.getString("CUSTOMER_NAME");
        String customerCode = fmExpense.getString("CUSTOMER_CODE");

        //申请人
        String userName = fmExpense.getString("USER_NAME");
        //支付金额
        String payAmount = fmExpense.getString("PAY_AMOUNT");

        //项目名称/编号
        String isOpen = "1";
        String projectCode = "";
        String projectName = "";
        BO pmProject = SDK.getBOAPI().query("BO_XR_PM_PROJECT").addQuery("CUSTOMER_CODE=", customerCode).detail();
        if(pmProject != null){
            isOpen = pmProject.getString("IS_OPEN");
            projectName = pmProject.getString("PROJECT_NAME");
            projectCode = pmProject.getString("PROJECT_CODE");
        }

        //核算费用科目
        String subjectCode = fmExpense.getString("SUBJECT_CODE");
        System.out.println("subjectCode: " + subjectCode);
        String subjectOne = "";
        String subjectTwo = "";
        String subjectThree = "";
        if(StringUtils.isNotEmpty(subjectCode) && subjectCode.length() > 0){
            int len = subjectCode.length();
            System.out.println("len:"+ len);
            if(len == 2){
                subjectOne = SDK.getDictAPI().getValue(appId, "EXPENSE_ACCOUNT", subjectCode);
            }else if(len == 4){
                subjectOne = SDK.getDictAPI().getValue(appId, "EXPENSE_ACCOUNT", subjectCode.substring(0,2));
                subjectTwo = SDK.getDictAPI().getValue(appId, "EXPENSE_ACCOUNT", subjectCode.substring(0,4));
            }else if(len == 6){
                subjectOne = SDK.getDictAPI().getValue(appId, "EXPENSE_ACCOUNT", subjectCode.substring(0,2));
                subjectTwo = SDK.getDictAPI().getValue(appId, "EXPENSE_ACCOUNT", subjectCode.substring(0,4));
                subjectThree = SDK.getDictAPI().getValue(appId, "EXPENSE_ACCOUNT", subjectCode.substring(0,6));
            }
        }

        //支付日期
        String expenseDate = fmExpense.getString("PAY_DATE");
        //申请时间
        Date date = UtilDate.parse(expenseDate);
        int year = UtilDate.getYear(date);
        int month = UtilDate.getMonth(date);

        BOCopyAPI copyAPI = CommonBiz.expensePayInfo("BO_XR_FM_PAY", boId);
        copyAPI.addNewData( "PROJECT_NAME",projectName);
        copyAPI.addNewData( "PROJECT_CODE",projectCode);
        copyAPI.addNewData( "YEAR",year);
        copyAPI.addNewData( "MONTH",month);
//        copyAPI.addNewData( "SUBJECT_ONE_CODE",bo.getString("SUBJECT_ONE_CODE"));
//        copyAPI.addNewData( "SUBJECT_TWO_CODE",bo.getString("SUBJECT_TWO_CODE"));

        copyAPI.addNewData( "SUBJECT_ONE",subjectOne);
        copyAPI.addNewData( "SUBJECT_TWO",subjectTwo);
        copyAPI.addNewData( "SUBJECT_THREE",subjectThree);
        //费用支付方
        copyAPI.addNewData( "CUSTOMER_NAME",customerName);
        //支付状态
        copyAPI.addNewData( "FLAG","1");
        //是否公开
        copyAPI.addNewData( "IS_OPEN",isOpen);
        //备注
        copyAPI.addNewData( "REMARK","来源于【"+userName+"】提交的【"+applyNo+"】，费用日期【"+expenseDate+"】，客户名称【"+customerName+"】，支付费用【"+payAmount+"】");
        //关联单据ID
        copyAPI.addNewData( "LINK_BINDID","PA:"+bindId);
        //费用事项
        copyAPI.addNewData( "REASON",fmExpense.getString("REASON"));
        // 执行复制操作
        copyAPI.exec();

        return true;
    }
}
