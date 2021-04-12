package com.xonro.project.job;

import com.actionsoft.bpms.bo.engine.BO;
import com.actionsoft.bpms.bpmn.engine.model.run.delegate.ProcessInstance;
import com.actionsoft.bpms.org.cache.UserCache;
import com.actionsoft.bpms.org.model.UserModel;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.sdk.local.SDK;
import com.actionsoft.sdk.local.api.ProcessAPI;
import org.apache.commons.lang3.StringUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.sql.Connection;
import java.util.Calendar;
import java.util.List;
/**
 * 每周一到周五生成项目工单数据
 * @author lion
 * @date 2018-10-31
 */

public class DailyDataJob implements Job {
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        Connection conn = null;
        conn = DBSql.open();
        List<UserModel>  list =UserCache.getActiveList();
        for (int i = 0; i < list.size(); i++) {
        	   Calendar cal = Calendar.getInstance();
               int year = cal.get(Calendar.YEAR);
               int Month = cal.get(Calendar.MONTH) + 1;
               int month = Month;
        	UserModel userModel=list.get(i);
            String ext5 = userModel.getExt5();
            if(!userModel.getUID().equals("admin")){
                if(StringUtils.equals(ext5,"1")){
                    UserContext userContext = UserContext.fromUID(userModel.getUID());
                    BO bo = new BO();
                    bo.set("APPLY_DATE",SDK.getRuleAPI().executeAtScript("@date"));
                    bo.set("USER_ID", userModel.getUID());
                    bo.set("USER_NAME", userModel.getUserName());
                    bo.set("DEPARTMENT_NAME", userContext.getDepartmentModel().getName());
                    bo.set("YEAR", year);
                    bo.set("MONTH", month);
                    bo.set("FLAG", "0");
                    bo.set("DELAY_HOURS", 0);

                    bo.set("WORK_DATE",SDK.getRuleAPI().executeAtScript("@date") );
                    //流程实例创建标题
                     String title=userModel.getUserName()+"提交的"+SDK.getRuleAPI().executeAtScript("@date")+"项目日志";
                    ProcessAPI proapi = SDK.getProcessAPI();
                    ProcessInstance processInstance =
                            proapi.createProcessInstance(
                                    "obj_c1b1c59e3afa4af4971708a281aed102",userModel.getUID(), title);
                    //获得usercontext上下文对象
                    SDK.getBOAPI().create("BO_XR_PM_PROJECT_DAILY", bo, processInstance, userContext, conn);
                    //启动了这个流程实例
                    proapi.start(processInstance);
                }
        	}
        }
        DBSql.close(conn);
    }

}