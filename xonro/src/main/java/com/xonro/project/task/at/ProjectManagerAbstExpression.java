package com.xonro.project.task.at;

import com.actionsoft.bpms.commons.at.AbstExpression;
import com.actionsoft.bpms.commons.at.ExpressionContext;
import com.actionsoft.bpms.util.DBSql;

/**
 * 发返回当前项目编号下对应的项目执行人
 * @author lion
 *
 */
public class ProjectManagerAbstExpression extends AbstExpression {

    public ProjectManagerAbstExpression(ExpressionContext atContext, String expressionValue) {
        super(atContext, expressionValue);
    }

    @Override
    public String execute(String s) {
        //获取第一个参数，表名
        String xmCode = getParameter(s,1);
        String sql = "SELECT PROJECT_MANAGER  FROM BO_XR_PM_PROJECT WHERE PROJECT_CODE='"+xmCode+"' ";
        String pm =DBSql.getString(sql, "PROJECT_MANAGER");
        return pm;
    }
}
