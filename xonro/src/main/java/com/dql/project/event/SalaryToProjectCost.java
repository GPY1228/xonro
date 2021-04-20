package com.dql.project.event;

import com.actionsoft.bpms.bo.engine.BO;
import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListener;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListenerInterface;
import com.actionsoft.bpms.commons.database.RowMap;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.sdk.local.SDK;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * @author GPY
 * @date 2021/4/20
 * @des 工资流水部分，费用拆分至项目明细中
 */
public class SalaryToProjectCost extends ExecuteListener implements ExecuteListenerInterface {

    @Override
    public String getDescription() {
        return "工资流水部分，费用拆分至项目明细中";
    }
    @Override
    public void execute(ProcessExecutionContext processExecutionContext) throws Exception {
        String bindId = processExecutionContext.getProcessInstance().getId();
        String selectSalary = " SELECT s.*,p.YEAR,p.`MONTH`,p.BINDID,p.APPLY_NO FROM BO_XR_FM_SALARY_SHEET s left join BO_XR_FM_SALARY_PAY p on s.BINDID = p.BINDID WHERE s.BINDID = '"+bindId+"' ";
        List<RowMap> getUsersSalaryLists = DBSql.getMaps(selectSalary);
        getUsersSalaryLists.forEach(getUsersSalaryList->{
            String amount = "";
            String year = getUsersSalaryList.getString("YEAR");
            String month = getUsersSalaryList.getString("MONTH");
            String userId = getUsersSalaryList.getString("USER_ID");
            String userName = getUsersSalaryList.getString("USER_NAME");
            String applyNo = getUsersSalaryList.getString("APPLY_NO"); //申请单号
            //String bindId = getUsersSalaryList.getString("BINDID");

            amount = getUsersSalaryList.getString("SUBSIDY_TXF");//补助-通讯费
            if (!amount.equals("0")||amount!=null){
                createBo_Xr_Fm_Cost(processExecutionContext,year,month,userId,userName,amount,bindId,applyNo,"SUBSIDY_TXF","补助-通讯费");
            }
            amount = getUsersSalaryList.getString("SUBSIDY_WC");//补助-午餐费
            if (!amount.equals("0")||amount!=null){
                createBo_Xr_Fm_Cost(processExecutionContext,year,month,userId,userName,amount,bindId,applyNo,"SUBSIDY_WC","补助-午餐费");
            }
            amount = getUsersSalaryList.getString("SUBSIDY_BJB");//补助-笔记本
            if (!amount.equals("0")||amount!=null){
                createBo_Xr_Fm_Cost(processExecutionContext,year,month,userId,userName,amount,bindId,applyNo,"SUBSIDY_BJB","补助-笔记本");
            }
            amount = getUsersSalaryList.getString("SUBSIDY_QQ");//补助-全勤
            if (!amount.equals("0")||amount!=null){
                createBo_Xr_Fm_Cost(processExecutionContext,year,month,userId,userName,amount,bindId,applyNo,"SUBSIDY_QQ","补助-全勤");
            }
            amount = getUsersSalaryList.getString("BONUS");//奖金
            if (!amount.equals("0")||amount!=null){
                createBo_Xr_Fm_Cost(processExecutionContext,year,month,userId,userName,amount,bindId,applyNo,"BONUS","奖金");
            }
            amount = getUsersSalaryList.getString("SOCIAL_SECURITY_C");//社保公司部分
            if (!amount.equals("0")||amount!=null){
                createBo_Xr_Fm_Cost(processExecutionContext,year,month,userId,userName,amount,bindId,applyNo,"SOCIAL_SECURITY_C","社保公司部分");
            }
            amount = getUsersSalaryList.getString("SURPLUS_C");//公积金_公司部分
            if (!amount.equals("0")||amount!=null){
                createBo_Xr_Fm_Cost(processExecutionContext,year,month,userId,userName,amount,bindId,applyNo,"SURPLUS_C","公积金_公司部分");
            }
            amount = getUsersSalaryList.getString("EXTRA_SURPLUS");//补充公积金
            if (!amount.equals("0")||amount!=null){
                createBo_Xr_Fm_Cost(processExecutionContext,year,month,userId,userName,amount,bindId,applyNo,"EXTRA_SURPLUS","补充公积金");
            }
            amount = getUsersSalaryList.getString("SALARY_SQ");//税前工资
            if (!amount.equals("0")||amount!=null){
                createBo_Xr_Fm_Cost(processExecutionContext,year,month,userId,userName,amount,bindId,applyNo,"SALARY_SQ","税前工资");
            }
            //amount = getUsersSalaryList.getString("DEDUCT_AMOUNT");//应扣工资
        });
    }

    //创建项目费用明细实体类
    public void createBo_Xr_Fm_Cost(ProcessExecutionContext processExecutionContext,String year,String month,String userId,String userName,String amount,String bindId,String applyNo,String code,String codeName) {
        String processId =  processExecutionContext.getProcessInstance().getProcessDefId();
        String appId = processExecutionContext.getProcessDef().getAppId();
        BO bo = new BO();
        bo.set("YEAR",year);
        bo.set("MONTH",month);
        bo.set("USER_ID",userId);
        bo.set("USER_NAME",userName);
        bo.set("BINDID",bindId);

        bo.set("CUSTOMER_NAME",processExecutionContext.getVariable("CUSTOMER_NAME"));
        bo.set("PROJECT_CODE","DQL-XM-202103-07");
        bo.set("PROJECT_NAME","2021公司内部建设");

        bo.set("IS_OPEN","0");
        bo.set("REASON",codeName);
        bo.set("AMOUNT",amount);
        bo.set("FLAG","1");
        bo.set("LINK_BINDID","GZ:"+bindId);//关联单据ID
        bo.set("REMARK","来源于admin提交的【"+applyNo+"】工资发放流程，员工名称【"+userName+"】，支付费用【"+codeName+"】");

        //通过工资发放明细字段查找对应3级科目bo_xr_subject_pz_list
        String selectSalary = " SELECT FIELD_NAME,SUBJECT_CODE,SUBJECT_NAME FROM bo_xr_subject_pz p left join  bo_xr_subject_pz_list l on p.PROCESSDEFID = l.PROCESSDEFID where FIELD_NAME = '"+code+"' and p.PROCESSDEFID = '"+processId+"' ";
        List<RowMap> getUsersSalaryLists = DBSql.getMaps(selectSalary);

        //核算费用科目
        String subjectCode = "";
        if (getUsersSalaryLists!=null && getUsersSalaryLists.size()>0){
            subjectCode = getUsersSalaryLists.get(0).getString("SUBJECT_CODE");
        }
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

        bo.set("SUBJECT_ONE",subjectOne);
        bo.set("SUBJECT_TWO",subjectTwo);
        bo.set("SUBJECT_THREE",subjectThree);
        SDK.getBOAPI().create("BO_XR_FM_COST",bo,"","");

    }

}
