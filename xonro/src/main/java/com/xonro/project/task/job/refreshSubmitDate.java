package com.xonro.project.task.job;

import com.actionsoft.bpms.bo.engine.BO;
import com.actionsoft.bpms.commons.database.RowMap;
import com.actionsoft.bpms.schedule.IJob;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.sdk.local.SDK;
import dm.jdbc.util.StringUtil;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.List;

public class refreshSubmitDate implements IJob {
    @Override
    public void execute(JobExecutionContext ctx) throws JobExecutionException {
        List<BO> list= SDK.getBOAPI().query( "BO_XR_WEEK_SUMMARY" ).list();
        for (BO bo:list){
            String submitDate=bo.getString( "SUBMIT_DATE" );
            if (StringUtil.isEmpty( submitDate )){
                //找对应的任务数据
                String bindid=bo.getBindId();
                String sql="select * from wfh_task where ACTIVITYDEFID='obj_6d8cfc62fae947cfa82e7cfccda3914b' and processinstid='"+bindid+"' ORDER BY ENDTIME";
                RowMap rowMap=DBSql.getMap( sql );
                if (rowMap!=null){
                    String endtime=rowMap.getString( "ENDTIME" );
                    String updateSql="update BO_XR_WEEK_SUMMARY set SUBMIT_DATE='"+endtime+"' where bindid ='"+bindid+"'";
                    DBSql.update( updateSql );
                }
            }
        }
    }
}
