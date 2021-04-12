package com.xonro.project.task.at;

import com.actionsoft.bpms.bo.engine.BO;
import com.actionsoft.bpms.commons.at.AbstExpression;
import com.actionsoft.bpms.commons.at.ExpressionContext;
import com.actionsoft.bpms.commons.database.RowMap;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.exception.AWSExpressionException;
import com.actionsoft.sdk.local.SDK;
import com.google.common.base.Joiner;
import dm.jdbc.util.StringUtil;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 获取项目经理下的所有项目
 */
public class TotalManager extends AbstExpression {
    public TotalManager(final ExpressionContext atContext, String expressionValue) {
        super( atContext, expressionValue );
    }

    @Override
    public String execute(String expression) throws AWSExpressionException {
        // 取第1个参数  bindid
        String str1 = getParameter( expression, 1 );
        Set<String> manager=new HashSet<>(  );
        if (StringUtil.isNotEmpty( str1 )) {
            String sql="select PROJECT_CODE from BO_XR_SUBSIDY_LIST where bindid ='"+str1+"' group by PROJECT_CODE";
            List<RowMap> list=DBSql.getMaps( sql );
            for (RowMap rowMap:list){
                String projectCode=rowMap.getString( "PROJECT_CODE" );
                //查询项目经理
                BO bo=SDK.getBOAPI().query( "BO_XR_PM_PROJECT" ).addQuery( "PROJECT_CODE=",projectCode ).detail();
                String proectExecutor=bo.getString( "PROECT_EXECUTOR" );
                manager.add( proectExecutor );
            }
        }
        if (manager!=null && manager.size()>0){
            String a=Joiner.on(" ").join(manager);
            return a;
        }
        return "";
    }
}
