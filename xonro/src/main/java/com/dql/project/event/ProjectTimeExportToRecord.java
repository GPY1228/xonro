package com.dql.project.event;

import cn.jpush.api.utils.StringUtils;
import com.actionsoft.bpms.bo.engine.BO;
import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.servicetask.ServiceDelegate;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.util.UtilDate;
import com.actionsoft.sdk.local.SDK;
import com.actionsoft.sdk.local.api.BOCopyAPI;
import com.dql.project.biz.CommonBiz;

import java.util.Date;
import java.util.List;

/**
 * 项目工时导入
 */
public class ProjectTimeExportToRecord extends ServiceDelegate {
    public ProjectTimeExportToRecord() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public boolean execute(ProcessExecutionContext ctx) throws Exception {
        String bindId = ctx.getProcessInstance().getId();

        //加班申请表
        List<BO> projectDailys = SDK.getBOAPI().query("BO_XR_PM_IMP_TIME_COST_LIST").addQuery("BINDID=",bindId).list();
        for (BO projectDaily : projectDailys) {
            String boId = projectDaily.getId();
            //工作时间
            String workDate = projectDaily.getString("WORK_DATE");
            //加班时间
            String overHours = projectDaily.getString("WORK_HOURS");
            //账号
            String uid = projectDaily.getString("USER_ID");
            String roleName = projectDaily.getString("ROLE_NAME");
            if(StringUtils.isEmpty(roleName)){
                roleName = UserContext.fromUID(uid).getRoleModel().getName();
            }
            Float overHour = Float.valueOf(overHours);
            //申请时间
            Date date = UtilDate.parse(workDate);
            int year = UtilDate.getYear(date);
            int month = UtilDate.getMonth(date);

            //加班数据
            BOCopyAPI copyAPI = CommonBiz.createWorkInfo("BO_XR_PM_IMP_TIME_COST_LIST", boId);
            copyAPI.addNewData( "YEAR",year );
            copyAPI.addNewData( "MONTH",month );
            copyAPI.addNewData( "ROLE_NAME",roleName);
            copyAPI.addNewData( "LINK_BINDID","D:"+bindId);
            // 执行复制操作
            copyAPI.exec();
        }

        return true;
    }

}
