package com.dql.project.event;

import com.actionsoft.bpms.bo.engine.BO;
import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListener;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListenerInterface;
import com.actionsoft.bpms.util.UtilDate;
import com.actionsoft.sdk.local.SDK;
import com.actionsoft.sdk.local.api.BOCopyAPI;
import com.dql.project.biz.CommonBiz;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.List;

/**
 * 费用报销
 */
public class ExpensePayTaskAfterComplete extends ExecuteListener implements ExecuteListenerInterface {

    @Override
    public String getDescription() {
        return "费用报销-生成费用明细档案";
    }

    @Override
    public void execute(ProcessExecutionContext ctx) throws Exception {
        String bindId = ctx.getProcessInstance().getId();
        String appId = ctx.getProcessDef().getAppId();
        //费用报销
        BO fmExpense = ctx.getBO("BO_XR_FM_EXPENSE");
        String projectCode = fmExpense.getString("PROJECT_CODE");
        String projectName = fmExpense.getString("PROJECT_NAME");
        String applyNo = fmExpense.getString("APPLY_NO");
        //核算成本名称
        String customerName = fmExpense.getString("CUSTOMER_NAME");
        //备注
        String remark = fmExpense.getString("REMARK");
        String userName = fmExpense.getString("USER_NAME");
        String userId = fmExpense.getString("USER_ID");

        String isOpen = "1";
        BO pmProject = SDK.getBOAPI().query("BO_XR_PM_PROJECT").addQuery("PROJECT_CODE=", projectCode).detail();
        if(pmProject != null){
            isOpen = pmProject.getString("IS_OPEN");
        }

        //费用明细表
        List<BO> list = SDK.getBOAPI().query("BO_XR_FM_EXPENSE_LIST").addQuery("BINDID=", bindId).list();
        if(list != null && list.size() > 0){
            for (BO bo : list) {
                String id = bo.getId();
                //费用日期
                String expenseDate = bo.getString("EXPENSE_DATE");
                //发票号
                String invoiceNumber = bo.getString("INVOICE_NUMBER");
                //申请时间
                Date date = UtilDate.parse(expenseDate);
                int year = UtilDate.getYear(date);
                int month = UtilDate.getMonth(date);

                //根据费用科目，推算出费用一级、二级、三级科目
                String subjectCode = bo.getString("SUBJECT_CODE");
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
                System.out.println("yi:" + subjectOne + " ; er:" + subjectTwo + " ; san:" + subjectThree);

                //费用分摊表
                List<BO> shareList = SDK.getBOAPI().query("BO_XR_FM_EXPENSE_SHARE").addQuery("BINDID=", id).list();
                if(shareList != null && shareList.size() > 0){
                    for (BO share : shareList) {
                        String boId = share.getId();
                        //分摊费
                        String amount = share.getString("AMOUNT");
                        BOCopyAPI copyAPI = CommonBiz.expensePayInfo("BO_XR_FM_EXPENSE_SHARE", boId);
                        copyAPI.addNewData( "PROJECT_NAME",projectName);
                        copyAPI.addNewData( "PROJECT_CODE",projectCode);
                        copyAPI.addNewData( "YEAR",year);
                        copyAPI.addNewData( "MONTH",month);
                        copyAPI.addNewData( "SUBJECT_ONE_CODE",bo.getString("SUBJECT_ONE_CODE"));
                        copyAPI.addNewData( "SUBJECT_TWO_CODE",bo.getString("SUBJECT_TWO_CODE"));

                        copyAPI.addNewData( "SUBJECT_ONE",subjectOne);
                        copyAPI.addNewData( "SUBJECT_TWO",subjectTwo);
                        copyAPI.addNewData( "SUBJECT_THREE",subjectThree);
                        //费用支付方
                        copyAPI.addNewData( "CUSTOMER_NAME",customerName);
                        //支付状态
                        copyAPI.addNewData( "FLAG","0");
                        //是否公开
                        copyAPI.addNewData( "IS_OPEN",isOpen);
                        //备注
                        copyAPI.addNewData( "REMARK","来源于【"+userName+"】提交的【"+applyNo+"】，费用日期【"+expenseDate+"】，发票号【"+invoiceNumber+"】，分摊产生费用【"+amount+"】");
                        //关联单据ID
                        copyAPI.addNewData( "LINK_BINDID","BX:"+bindId);
                        //费用事项
                        copyAPI.addNewData( "REASON",bo.getString("REASON"));
                        // 执行复制操作
                        copyAPI.exec();

                    }
                }else{
                    BOCopyAPI copyAPI = CommonBiz.expensePayInfo("BO_XR_FM_EXPENSE_LIST", id);
                    copyAPI.addNewData( "PROJECT_NAME",projectName);
                    copyAPI.addNewData( "PROJECT_CODE",projectCode);
                    copyAPI.addNewData( "YEAR",year);
                    copyAPI.addNewData( "MONTH",month);
                    copyAPI.addNewData( "USER_ID", userId);
                    copyAPI.addNewData( "USER_NAME",userName);

                    copyAPI.addNewData( "SUBJECT_ONE",subjectOne);
                    copyAPI.addNewData( "SUBJECT_TWO",subjectTwo);
                    copyAPI.addNewData( "SUBJECT_THREE",subjectThree);
                    //费用支付方
                    copyAPI.addNewData( "CUSTOMER_NAME",customerName);
                    //支付状态
                    copyAPI.addNewData( "FLAG","0");
                    //是否公开
                    copyAPI.addNewData( "IS_OPEN",isOpen);
                    //备注
                    copyAPI.addNewData( "REMARK","来源于【"+userName+"】提交的【"+applyNo+"】，费用日期【"+expenseDate+"】，发票号【"+invoiceNumber+"】");
                    //关联单据ID
                    copyAPI.addNewData( "LINK_BINDID","BX:"+bindId);
                    // 执行复制操作
                    copyAPI.exec();

                }
            }
        }

    }

    public static void main(String[] args) {

        System.out.println("4030".substring(2,4));
    }
}
