package com.xonro.project.task.at;

import com.actionsoft.bpms.commons.at.AbstExpression;
import com.actionsoft.bpms.commons.at.ExpressionContext;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.sdk.local.SDK;

/**
 * 发返回当前客户编号
 * @author lion
 *
 */
public class CustomerCodeAbstExpression extends AbstExpression {

    public CustomerCodeAbstExpression(ExpressionContext atContext, String expressionValue) {
        super(atContext, expressionValue);
    }

    @Override
    public String execute(String s) {
        //获取第一个参数，表名
        String name = SDK.getAppAPI().getProperty("com.xonro.apps.xr", "COMPANY_CODE");

        //XR-XM-@year@month-@sequence(XMDA,2,0)
        String str= name + "-KH-"+SDK.getRuleAPI().executeAtScript("@year")+SDK.getRuleAPI().executeAtScript("@month");
        
        String sql = "SELECT count(*) as COUNT  FROM BO_XR_XCM_CUSTOMER  WHERE CUSTOMER_CODE  like '%"+str+"%' ";
        
     
        int count=DBSql.getInt( sql,"COUNT")+1;
        String khCode =str+"-"+count;
        if(count<10){
        	khCode = str+"-0"+count;
        }else{
        	khCode = str+"-"+count;
        }
        
        System.out.println(sql+"："+ khCode);
        
        return khCode;
    }
}
