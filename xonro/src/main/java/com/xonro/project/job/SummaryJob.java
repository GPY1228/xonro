package com.xonro.project.job;

import com.actionsoft.bpms.bpmn.engine.model.run.delegate.ProcessInstance;
import com.actionsoft.bpms.commons.database.RowMap;
import com.actionsoft.bpms.org.OrgUserMapController;
import com.actionsoft.bpms.schedule.IJob;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.sdk.local.SDK;
import com.actionsoft.bpms.bo.engine.BO;
import com.actionsoft.sdk.local.api.ProcessAPI;
import com.actionsoft.sdk.local.api.cc.RDSAPI;
import dm.jdbc.util.StringUtil;
import org.quartz.JobExecutionContext;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @Description:  周工作总结流程
 * @Param:
 * @return:
 * @Author:   Zhang
 * @Date:   2020/02/07 09:00
 */
public class SummaryJob implements IJob
{
    public void execute(JobExecutionContext jobExecutionContext){
        //获取当前系统年,月,周数
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int week = calendar.get(Calendar.WEEK_OF_YEAR);
        System.out.println("year="+year+"month="+month+"week="+week);
        //判断是否是第一周
        int selectWeek;
        if(week == 1){
            selectWeek = 52;
        }else{
            selectWeek = week - 1;
        }
        //数据库查询获取user
        String sql = "select * from orguser where userid !='admin' ";
        List<RowMap> orgUsers = DBSql.getMaps(sql);
        //根据年月周数及用户编号获取bo
        for (RowMap rowMap : orgUsers) {
            String userId = rowMap.getString("USERID");
            String result=SDK.getORGAPI().validateUsers( userId );
            //判断账户是否合法，若合法则返回空
            if (StringUtil.isNotEmpty( result )){
                continue;
            }
            String userName = rowMap.getString("USERNAME");
            String departmentId = rowMap.getString("DEPARTMENTID");
            //数据库查询获取departmentName
            String departmentName = DBSql.getString("select departmentname from orgdepartment where id = '"+departmentId+"'");
           
            //创建主表流程实例
            BO mainBo = new BO();
            mainBo.set("YEAR",year);
            mainBo.set("MONTH",month);
            mainBo.set("WEEK",week);
            String processDefId ="obj_bfc4b655206848ef89bcba0a6e70f603";//获取processDefId
            String title = "【"+departmentName+"】【"+userName+"】发起的第"+week+"周工作总结";
            System.out.println(title);
            ProcessInstance processInstance = SDK.getProcessAPI().createProcessInstance(processDefId, userId, title);
            SDK.getProcessAPI().start(processInstance);
            BO bo =  SDK.getBOAPI().query("BO_XR_AM_SUMMARY").addQuery("USER_ID=",userId).addQuery("YEAR=",year).addQuery("WEEK=",selectWeek).detail();
            if(bo != null){
                //通过bindId查询子表2中下周计划
                String bindId = bo.getBindId();
                List<BO> list = SDK.getBOAPI().query("BO_XR_AM_SUMMARY_S2").bindId(bindId).list();
                System.out.println("list size = "+list.size());
                //若下周计划不为空，将下周计划移至本周计划
                if(list != null && list.size() > 0){
                    for (BO sanBo : list){
                        sanBo.remove("ID");
                        sanBo.remove("BINDID");
                        sanBo.set("PLAN_TYPE","1");
                        sanBo.set("FLAG","1");
                        int createSReturn = SDK.getBOAPI().create("BO_XR_AM_SUMMARY_S1", sanBo, processInstance,UserContext.fromUID(userId));
                        System.out.println("子表create return =" + createSReturn);
                    }
                }
            }
            int createSumReturn = SDK.getBOAPI().create("BO_XR_AM_SUMMARY", mainBo, processInstance,UserContext.fromUID(userId));
            System.out.println("主表create return =" + createSumReturn);


        }

    }
}
