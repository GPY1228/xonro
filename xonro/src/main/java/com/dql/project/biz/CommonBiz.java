package com.dql.project.biz;

import com.actionsoft.bpms.bpmn.engine.model.run.delegate.ProcessInstance;
import com.actionsoft.sdk.local.SDK;
import com.actionsoft.sdk.local.api.BOCopyAPI;
import com.actionsoft.sdk.local.api.BOQueryAPI;

public class CommonBiz {

    /**
     * 项目耗时明细
     * @param boName
     * @param boId
     * @return
     */
    public static BOCopyAPI createWorkInfo(String boName,String boId){
        //创建任务分配流程
        ProcessInstance processInstance= SDK.getProcessAPI().createProcessInstance( "obj_188fb1479f35492580bf8b15ff05a262","admin","工时明细");
        //指定将要复制到新的bo表以及流程实例ID
        BOQueryAPI query = SDK.getBOAPI().query(boName, true).addQuery("ID=", boId);
        //档案表
        BOCopyAPI copyAPI = query.copyTo("BO_XR_PM_TIME_COST", processInstance.getId());
        return copyAPI;
    }

    /**
     * 项目档案库
     * @param boName
     * @param boId
     * @return
     */
    public static BOCopyAPI createProjectRecordInfo(String boName,String boId){
        //创建任务分配流程
        ProcessInstance processInstance= SDK.getProcessAPI().createProcessInstance( "obj_9c33173a0c604238b5cd522695d708ed","admin","项目立项档案");
        //指定将要复制到新的bo表以及流程实例ID
        BOQueryAPI query = SDK.getBOAPI().query(boName, true).addQuery("ID=", boId);
        //档案表
        BOCopyAPI copyAPI = query.copyTo("BO_XR_PM_PROJECT", processInstance.getId());
        return copyAPI;
    }

    /**
     * 费用成本明细管理
     * @param boName
     * @param boId
     * @return
     */
    public static BOCopyAPI expensePayInfo(String boName,String boId){
        //创建任务分配流程
        ProcessInstance processInstance= SDK.getProcessAPI().createProcessInstance( "obj_c6241dc677b244df90e53d01da829b2f","admin","费用成本明细管理");
        //指定将要复制到新的bo表以及流程实例ID
        BOQueryAPI query = SDK.getBOAPI().query(boName, true).addQuery("ID=", boId);
        //档案表
        BOCopyAPI copyAPI = query.copyTo("BO_XR_FM_COST", processInstance.getId());
        return copyAPI;
    }

    /**
     * 费用工资台帐
     * @param boName
     * @param boId
     * @return
     */
    public static BOCopyAPI salaryPayInfo(String boName,String boId){
        //创建任务分配流程
        ProcessInstance processInstance= SDK.getProcessAPI().createProcessInstance( "obj_59e21005ef9b445093924585db94c41a","admin","费用成本明细管理");
        //指定将要复制到新的bo表以及流程实例ID
        BOQueryAPI query = SDK.getBOAPI().query(boName, true).addQuery("ID=", boId);
        //档案表
        BOCopyAPI copyAPI = query.copyTo("BO_XR_FM_SALARY", processInstance.getId());
        return copyAPI;
    }
}
