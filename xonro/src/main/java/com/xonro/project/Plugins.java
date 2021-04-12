package com.xonro.project;

import
com.actionsoft.apps.listener.PluginListener;
import com.actionsoft.apps.resource.AppContext;
import com.actionsoft.apps.resource.plugin.profile.*;
import com.xonro.project.task.at.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: Alex
 * @Date: 2019/11/5$ 17:07$
 * @Description:
 */
public class Plugins implements PluginListener {
    public List<AWSPluginProfile> register(AppContext appContext) {
        List<AWSPluginProfile> list = new ArrayList<AWSPluginProfile>();
        //改造代码
        list.add(new AtFormulaPluginProfile("动起来", "@projectManager(xmcode)", ProjectManagerAbstExpression.class.getName(), "账户", "获取配置项目的编号，返回对应项目经理ID"));
        list.add(new AtFormulaPluginProfile("动起来", "@projectExecutor(xmcode)", ProjectExecutorAbstExpression.class.getName(), "账户", "获取配置项目的编号，返回对应项目负责人ID"));
        list.add(new AtFormulaPluginProfile("动起来", "@projectCode()", ProjectCodeAbstExpression.class.getName(), "账户", "获取配置项目的编号"));
        list.add(new AtFormulaPluginProfile("动起来", "@customerCode()", CustomerCodeAbstExpression.class.getName(), "账户", "获取配置项目的编号"));
        list.add(new AtFormulaPluginProfile("动起来", "@contractCode()", ContractCodeAbstExpression.class.getName(), "账户", "获取配置项目的编号"));

        //原代码
        list.add(new AtFormulaPluginProfile("项目经理", "@projectManager(*str)", FindManager.class.getName(), "根据项目编号寻找项目经理", "返回项目经理账号"));
        list.add(new AtFormulaPluginProfile("项目经理下所有项目", "@managerProject(*str)", ManagerProject.class.getName(), "根据当前人，寻找此人下的所有项目编号", "项目编号"));
        list.add(new AtFormulaPluginProfile("根据子表项目编号，获取对应项目的所有项目经理", "@totalManager(*bindid)", TotalManager.class.getName(), "根据当前人，寻找此人下的所有项目编号", "项目编号"));
        return list;
    }
}
