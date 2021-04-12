package com.xonro.project.task.at;

import ch.qos.logback.core.util.StringCollectionUtil;
import com.actionsoft.bpms.bo.engine.BO;
import com.actionsoft.bpms.commons.at.AbstExpression;
import com.actionsoft.bpms.commons.at.ExpressionContext;
import com.actionsoft.exception.AWSExpressionException;
import com.actionsoft.sdk.local.SDK;
import dm.jdbc.util.StringUtil;

import java.util.List;

import static org.icepdf.core.pobjects.fonts.nfont.instructions.g.bo;

/**
 * 获取项目经理下的所有项目
 */
public class ManagerProject extends AbstExpression {
    public ManagerProject(final ExpressionContext atContext, String expressionValue) {
        super( atContext, expressionValue );
    }

    @Override
    public String execute(String expression) throws AWSExpressionException {
        // 取第1个参数  人员名称
        String str = getParameter( expression, 1 );
        if (StringUtil.isNotEmpty( str )) {
            List<BO> list = SDK.getBOAPI().query( "BO_XR_PM_PROJECT" ).addQuery( "PROECT_EXECUTOR=", str ).list();
            String projectCode = "";
            if (!list.isEmpty()){
                for (BO bo:list){
                    String projectCode1=bo.getString( "PROJECT_CODE" );
                    projectCode=projectCode+projectCode1+",";
                }
                projectCode=projectCode.substring( 0,projectCode.length()-1 );
            }
            return projectCode;
        }
        return "空";
    }
}
