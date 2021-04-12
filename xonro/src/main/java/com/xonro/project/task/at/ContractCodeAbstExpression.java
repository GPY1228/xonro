package com.xonro.project.task.at;

import com.actionsoft.bpms.commons.at.AbstExpression;
import com.actionsoft.bpms.commons.at.ExpressionContext;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.sdk.local.SDK;

/**
 * 发返回当前合同编号
 * @author lion
 *
 */
public class ContractCodeAbstExpression extends AbstExpression {

    public ContractCodeAbstExpression(ExpressionContext atContext, String expressionValue) {
        super(atContext, expressionValue);
    }

    @Override
    public String execute(String s) {
        //获取第一个参数，表名
        String name = SDK.getAppAPI().getProperty("com.xonro.apps.xr", "COMPANY_CODE");

        //XR-XM-@year@month-@sequence(XMDA,2,0)
        String str= name + "-KH-"+SDK.getRuleAPI().executeAtScript("@year")+SDK.getRuleAPI().executeAtScript("@month");
        
        String sql = "SELECT count(*) as COUNT  FROM BO_XR_CM_CONTRACT  WHERE CONTRACT_CODE  like '%"+str+"%' ";
        
     
        int count=DBSql.getInt( sql,"COUNT")+1;
        String hTCode ;
        if(count<10){
        	hTCode = str+"-0"+count;
        }else{
        	hTCode = str+"-"+count;
        }
        System.out.println(sql+"："+ hTCode);
        return hTCode;
    }
}
