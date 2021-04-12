package com.xonro.job;

import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.sdk.local.SDK;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;


/**
 * 每天计算工单的拖延时间
 * @author lion
 * @date 2018-10-31
 */

public class DailyDataStatisticsJob implements Job {
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
    	
    	String bz_hour = SDK.getAppAPI().getProperty("com.xonro.apps.xr", "STANDARD_HOUR");
        Connection conn = null;
        String  sql="SELECT ID,  TIMESTAMPDIFF(HOUR,apply_date,finish_date)-"+bz_hour+"  as  HOUR  from  BO_XR_PM_PROJECT_DAILY where finish_date is NOT  null and flag='0'  ORDER BY HOUR desc ";
        System.out.println("执行------:"+sql);
        Statement stmt=null;
        ResultSet rs =null;
        try{
          conn = DBSql.open();

         stmt=conn.createStatement();
         rs = stmt.executeQuery(sql);
         int i=0;
        while(rs.next()){
        	     i++;
	        	String id=rs.getString("ID");
	        	int hour=rs.getInt("HOUR");
	        	String update="";
	        	if(hour>=0){
	        	  update="update BO_XR_PM_PROJECT_DAILY set DELAY_HOURS ="+hour+" , FLAG='2'  where ID='"+id+"'";
	        	}
	        	if(hour < 0 ){
	        	  update="update BO_XR_PM_PROJECT_DAILY set DELAY_HOURS =0 , FLAG ='1'     where ID='"+id+"'";
	        	}
	        	int res =DBSql.update(conn, update);
		    	System.out.println("执行更新："+update+" 结果："+res);

        }
        }catch(Exception e){
        	e.printStackTrace();
        	
        }finally{
        	 DBSql.close(conn, stmt, rs);
        }
    }
    
}