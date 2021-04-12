package com.xonro.base.plugin;

import com.actionsoft.apps.listener.PluginListener;
import com.actionsoft.apps.resource.AppContext;
import com.actionsoft.apps.resource.plugin.profile.AWSPluginProfile;
import com.actionsoft.apps.resource.plugin.profile.AtFormulaPluginProfile;

import com.xonro.base.plugin.expression.*;

import java.util.ArrayList;
import java.util.List;
public class XonroPluginListener implements PluginListener {
    @Override
    public List<AWSPluginProfile> register(AppContext appContext) {

    	
    	
        List<AWSPluginProfile> pluginProfiles = new ArrayList<AWSPluginProfile>();
        //项目
        pluginProfiles.add(new AtFormulaPluginProfile("上海象融","@projectManager(xmcode)",
        		ProjectManagerAbstExpression.class.getName(),"账户","获取配置项目的编号，返回对应项目经理ID"));
        pluginProfiles.add(new AtFormulaPluginProfile("上海象融","@projectExecutor(xmcode)",
        		ProjectExecutorAbstExpression.class.getName(),"账户","获取配置项目的编号，返回对应项目负责人ID"));
        
        //项目编号
        pluginProfiles.add(new AtFormulaPluginProfile("上海象融","@projectCode()",
        		ProjectCodeAbstExpression.class.getName(),"账户","获取配置项目的编号"));
        //客户编号
        pluginProfiles.add(new AtFormulaPluginProfile("上海象融","@customerCode()",
        		CustomerCodeAbstExpression.class.getName(),"账户","获取配置项目的编号"));
        //合同编号
        pluginProfiles.add(new AtFormulaPluginProfile("上海象融","@contractCode()",
        		ContractCodeAbstExpression.class.getName(),"账户","获取配置项目的编号"));
        
        
        
        return pluginProfiles;
        
    }
    
    
    
    
    
 
}
