package com.xonro.project.task.at;

import com.actionsoft.bpms.bo.engine.BO;
import com.actionsoft.bpms.commons.at.AbstExpression;
import com.actionsoft.bpms.commons.at.ExpressionContext;
import com.actionsoft.exception.AWSExpressionException;
import com.actionsoft.sdk.local.SDK;
import dm.jdbc.util.StringUtil;

public class FindManager extends AbstExpression {
    public FindManager(final ExpressionContext atContext, String expressionValue) {
        super(atContext, expressionValue);
    }
    @Override
    public String execute(String expression) throws AWSExpressionException {
        // 取第1个参数  项目编号
        String str = getParameter(expression, 1);
        if (StringUtil.isNotEmpty( str )){
            //获取项目经理
            BO bo =SDK.getBOAPI().getByKeyField( "BO_XR_PM_PROJECT","PROJECT_CODE",str );
            if (bo!=null){
                String proectExecutor=bo.getString( "PROECT_EXECUTOR" );
                if (StringUtil.isNotEmpty( proectExecutor )){
                    return proectExecutor;
                }
            }
        }
        return null;
    }
}
