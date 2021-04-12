package com.xonro.project.task.job;

import com.actionsoft.bpms.bo.engine.BO;
import com.actionsoft.bpms.schedule.IJob;
import com.actionsoft.sdk.local.SDK;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class refreshTaskJob implements IJob {
    @Override
    public void execute(JobExecutionContext ctx) throws JobExecutionException {
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd" );
        List<BO> list=SDK.getBOAPI().query( "BO_XR_PM_TASK" ).list();
        for (BO bo:list){
            String planStartDate=bo.getString( "PLAN_START_DATE" );
            Date date= null;
            try {
                date = sdf.parse( planStartDate );
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Calendar calendar=Calendar.getInstance();
            calendar.setFirstDayOfWeek( 2 );
            calendar.setTime( date );
            String week=String.valueOf(  calendar.get( Calendar.WEEK_OF_YEAR));
            bo.set( "WEEK",week );
            SDK.getBOAPI().update( "BO_XR_PM_TASK",bo );
        }
    }
}
