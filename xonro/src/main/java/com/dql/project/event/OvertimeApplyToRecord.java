package com.dql.project.event;

import com.actionsoft.bpms.bo.engine.BO;
import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.servicetask.ServiceDelegate;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.util.UtilDate;
import com.actionsoft.sdk.local.SDK;
import com.actionsoft.sdk.local.api.BOCopyAPI;
import com.dql.project.biz.CommonBiz;

import java.util.Date;

/**
 * 加班申请
 */
public class OvertimeApplyToRecord extends ServiceDelegate {
    public OvertimeApplyToRecord() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public boolean execute(ProcessExecutionContext ctx) throws Exception {
        String bindId = ctx.getProcessInstance().getId();
        String createUser = ctx.getProcessInstance().getCreateUser();
        String roleName = UserContext.fromUID(createUser).getRoleModel().getName();
        //加班申请表
        BO projectDaily = SDK.getBOAPI().query("BO_XR_OVERTIME").detailByBindId(bindId);
        String boId = projectDaily.getId();
        //工作时间
        String workDate = projectDaily.getString("WORK_DATE");
        //加班时间
        String overHours = projectDaily.getString("WORK_HOURS");
        Float overHour = Float.valueOf(overHours);
        //申请时间
        Date date = UtilDate.parse(workDate);
        int year = UtilDate.getYear(date);
        int month = UtilDate.getMonth(date);

        //获取项目角色
        String projectName = projectDaily.getString("PROJECT_NAME");
        //项目立项申请
        BO pmProjectApply = SDK.getBOAPI().query("BO_XR_PM_PROJECT_APPLY").addQuery("PROJECT_NAME=", projectName).detail();
        if(pmProjectApply != null){
            String bindId1 = pmProjectApply.getBindId();
            //项目团队人员
            BO pmProjectMember = SDK.getBOAPI().query("BO_XR_PM_PROJECT_MEMBER").bindId(bindId1).addQuery("USER_ID=",createUser).detail();
            if(pmProjectMember != null){
                roleName = pmProjectMember.getString("ROLE_NAME");
            }
        }

        //加班数据
        BOCopyAPI copyAPI = CommonBiz.createWorkInfo("BO_XR_OVERTIME", boId);
        copyAPI.addNewData( "ROLE_NAME",roleName);
        copyAPI.addNewData( "YEAR",year );
        copyAPI.addNewData( "MONTH",month );
        copyAPI.addNewData( "IS_OVERTIME","1" );
        //节假日加班
        copyAPI.addNewData( "DATA_TYPE","3" );
        copyAPI.addNewData( "LINK_BINDID","J:"+bindId);
        // 执行复制操作
        copyAPI.exec();

        return true;
    }
}
