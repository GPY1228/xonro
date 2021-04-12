package com.xonro.project.task.event;

import com.actionsoft.bpms.bo.engine.BO;
import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.model.run.delegate.ProcessInstance;
import com.actionsoft.bpms.bpmn.engine.model.run.delegate.TaskInstance;
import com.actionsoft.bpms.bpmn.engine.servicetask.ServiceDelegate;
import com.actionsoft.bpms.commons.database.RowMap;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.sdk.local.SDK;
import dm.jdbc.util.StringUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class TaskChangeSystemEvent extends ServiceDelegate {
    @Override
    public boolean execute(ProcessExecutionContext ctx) throws Exception {
        SimpleDateFormat sdf=new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
        Date date=new Date(  );
        String time=sdf.format( date );
        //获取表单数据
        BO bo=ctx.getBO( "BO_XR_PM_TASK_MODIFY" );
        String userName=bo.getString( "USER_NAME" );
        //任务编号
        String taskNo=bo.getString( "TASK_NO" );
        //获取原执行人
        String oldUser=bo.getString( "TASK_OWNER" );
        //变更执行人
        String changeuser=bo.getString( "NEW_TASK_OWNER" );
        //获取理由
        String reason=bo.getString( "REASON" );
        //获取变更类型 1:取消任务  2：变更执行人
        String type = bo.getString( "MODIFY_TYPE" );
        if ("1".equals( type )){
            //如果是取消，更新任务状态为 4
            String sql="update BO_XR_PM_TASK set flag ='4' where TASK_NO ='"+taskNo+"'";
            //查询是否存在任务
            String sql1="select * from BO_XR_PM_TASK_EXECUTE where TASK_NO='"+taskNo+"'";
            RowMap rowMap1=DBSql.getMap( sql1 );
            if (rowMap1!=null){
                String bindid=rowMap1.getString( "BINDID" );
                ProcessInstance processInstance=SDK.getProcessAPI().getInstanceById( bindid );
                if (processInstance!=null){
                    SDK.getProcessAPI().cancel( processInstance,ctx.getUserContext());
                }
            }
            DBSql.update( sql );
            //任务单中追加任务取消记录
            String title=userName+"于"+time+"将此任务取消";
            String updateSql="update BO_XR_PM_TASK set TASK_REMARK = '"+title+"' where TASK_NO='"+taskNo+"'";
            DBSql.update( updateSql );
        }else{
                //获取原有流程
                String sql1="select * from BO_XR_PM_TASK_EXECUTE where TASK_NO='"+taskNo+"'";
                RowMap rowMap1=DBSql.getMap( sql1 );
                if (rowMap1!=null){
                String bindid=rowMap1.getString( "BINDID" );
                String sql2="select * from wfc_task where processinstid = '"+bindid+"' and activitydefid = 'obj_c8c9b8b775200001dc74e1081a307920'";
                RowMap rowMap=DBSql.getMap( sql2 );
                if (rowMap!=null){
                    String id=rowMap.getString( "ID" );
                    TaskInstance taskInstance=SDK.getTaskAPI().getInstanceById( id );
                    if (taskInstance!=null){
                        SDK.getTaskAPI().delegateTask( taskInstance, UserContext.fromUID( ctx.getProcessInstance().getCreateUser() ),changeuser,reason );
                        String sql3="update BO_XR_PM_TASK set TASK_OWNER ='"+changeuser+"',FLAG='2',TASK_TYPE='2' where TASK_NO = '"+taskNo+"'";
                        DBSql.update( sql3 );
                        String sql4="update BO_XR_PM_TASK_EXECUTE set TASK_OWNER ='"+changeuser+"' where TASK_NO = '"+taskNo+"'";
                        DBSql.update( sql4 );
                    }
                }
                }else {
                String sql3="update BO_XR_PM_TASK set TASK_OWNER ='"+changeuser+"',FLAG='1',TASK_TYPE='2' where TASK_NO = '"+taskNo+"'";
                DBSql.update( sql3 );
            }
            //任务单中追加任务取消记录
            String sql="select * from BO_XR_PM_TASK where TASK_NO='"+taskNo+"'";
            String taskRemark=DBSql.getString( sql,"TASK_REMARK" );
            String title=taskRemark+"<span>"+userName+"于"+time+"将此任务由"+SDK.getORGAPI().getUser( oldUser ).getUserName()+"移交给"+SDK.getORGAPI().getUser( changeuser ).getUserName()+"</span>";
            String updateSql="update BO_XR_PM_TASK set TASK_REMARK = '"+title+"' where TASK_NO='"+taskNo+"'";
            DBSql.update( updateSql );
        }
        return true;
    }
}
