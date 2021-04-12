package com.xonro.base.plugin.expression;

import com.actionsoft.bpms.commons.at.AbstExpression;
import com.actionsoft.bpms.commons.at.ExpressionContext;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.sdk.local.SDK;

/**
 * 发返回当前项目编号
 * @author lion
 *
 */
public class ProjectCodeAbstExpression extends AbstExpression {

    public ProjectCodeAbstExpression(ExpressionContext atContext, String expressionValue) {
        super(atContext, expressionValue);
    }

    @Override
    public String execute(String s) {
        //获取第一个参数，表名
        
        //XR-XM-@year@month-@sequence(XMDA,2,0)
        String str="XR-XM-"+SDK.getRuleAPI().executeAtScript("@year")+SDK.getRuleAPI().executeAtScript("@month");
        
        String sql = "SELECT count(*) as COUNT  FROM BO_XR_PM_PROJECT WHERE PROJECT_CODE  like '%"+str+"%' ";
        int count=DBSql.getInt( sql,"COUNT")+1;
        String pmCode ;
        if(count<10){
        	pmCode = str+"-0"+count;
        }else{
        	pmCode = str+"-"+count;
        }
        System.out.println(sql+"："+ pmCode);
        return pmCode;
    }
}
