package com.xonro.project.job;

import com.actionsoft.bpms.bo.engine.BO;
import com.actionsoft.bpms.schedule.IJob;
import com.actionsoft.sdk.local.SDK;
import dm.jdbc.util.StringUtil;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class refreshDay implements IJob {
    @Override
    public void execute(JobExecutionContext ctx) throws JobExecutionException {
        try {
            List<BO> list=SDK.getBOAPI().query( "BO_XR_PM_TASK" ).list();
            for (BO bo:list){
                String planStartDate=bo.getString( "PLAN_START_DATE" );
                //获取日
                SimpleDateFormat sdf=new SimpleDateFormat( "yyyy-MM-dd" );
                Date date=sdf.parse( planStartDate );
                Calendar calendar=Calendar.getInstance();
                calendar.setTime( date );
                String day=String.valueOf(  calendar.get( Calendar.DAY_OF_MONTH ));
                bo.set( "DAY",day );
                String startDate=bo.getString( "START_DATE" );
                if (StringUtil.isEmpty( startDate )){
                    bo.set( "FLAG" ,"1");
                }
                SDK.getBOAPI().update( "BO_XR_PM_TASK",bo );
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
