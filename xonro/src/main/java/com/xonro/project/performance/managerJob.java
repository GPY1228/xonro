package com.xonro.project.performance;

import com.actionsoft.bpms.bo.engine.BO;
import com.actionsoft.bpms.bpmn.engine.model.run.delegate.ProcessInstance;
import com.actionsoft.bpms.schedule.IJob;
import com.actionsoft.sdk.local.SDK;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * 项目经理评分流程
 */
public class managerJob implements IJob {
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        create("XR-XM-201909-02");
        create("XR-XM-202001-04");
        create("XR-XM-201910-02");
        create("XR-XM-201912-01");
    }
    public void create(String projectId){
        //创建项目经理评分流程
        BO bo=SDK.getBOAPI().query( "BO_XR_PM_PROJECT" ).addQuery( "PROJECT_CODE=",projectId ).detail();
        String projectName=bo.getString( "PROJECT_NAME" );
        //项目经理
        String proectExecutor=bo.getString( "PROECT_EXECUTOR" );
        //项目成员
        String projectTeam=bo.getString( "PROJECT_TEAM" );
        String []users=projectTeam.split( " " );
        for (String user:users){
            ProcessInstance processInstance=SDK.getProcessAPI().createProcessInstance( "obj_616e96941b024bbfacc1f825b5155560",user,SDK.getORGAPI().getUser( user ).getUserName()+"提交的2020年6月对"+SDK.getORGAPI().getUser( proectExecutor ).getUserName()+"的评分流程" );
            SDK.getProcessAPI().start( processInstance );
            BO bo1=new BO();
            bo1.set( "PROJECT_CODE", projectId);
            bo1.set( "MONTH", 6);
            bo1.set( "PROJECT_NAME", projectName);
            bo1.set( "ZRR", proectExecutor);
            SDK.getBOAPI().create( "BO_XR_PM_GRADE",bo1,processInstance.getId(),user );
        }
    }
}
