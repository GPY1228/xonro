package com.dql.project.event;

import cn.jpush.api.utils.StringUtils;
import com.actionsoft.bpms.bo.engine.BO;
import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.servicetask.ServiceDelegate;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.bpms.util.UtilDate;
import com.actionsoft.sdk.local.SDK;
import com.actionsoft.sdk.local.api.BOCopyAPI;
import com.dql.project.biz.CommonBiz;

import java.util.Date;
import java.util.List;

/**
 * 项目立项申请
 */
public class ProjectItemApplyService extends ServiceDelegate {
    public ProjectItemApplyService() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public boolean execute(ProcessExecutionContext ctx) throws Exception {
        String appId = ctx.getProcessDef().getAppId();
        //部门
        String dept = SDK.getAppAPI().getProperty(appId, "DEPARTMENT_ID");
        String bindId = ctx.getProcessInstance().getId();
        String createUser = ctx.getProcessInstance().getCreateUser();
        //项目立项
        BO projectDaily = SDK.getBOAPI().query("BO_XR_PM_PROJECT_APPLY").detailByBindId(bindId);
        String boId = projectDaily.getId();
        //项目名称
        String projectName = projectDaily.getString("PROJECT_NAME");
        //项目成员集合
        String uid = "";
        //项目团队人员
        List<BO> pmProjectMember = SDK.getBOAPI().query("BO_XR_PM_PROJECT_MEMBER").addQuery("BINDID=",bindId).list();
        if(pmProjectMember != null && pmProjectMember.size() > 0){
            for (BO bo : pmProjectMember) {
                uid += bo.getString("USER_ID") + " ";
            }
        }else{
            if(StringUtils.isNotEmpty(dept)){
                dept = dept.replace(",","','");
            }
            String sql = "SELECT replace(wm_concat(DISTINCT TO_CHAR(USERID)),',',' ') USERIDS FROM orguser WHERE " +
                    "DEPARTMENTID in ('"+ dept +"')";
            uid = DBSql.getString(sql,"USERIDS");
        }
        //项目编号
        String projectCode = SDK.getRuleAPI().executeAtScript("@projectCode()");
        System.out.println("projectCode：" + projectCode);
        //项目立项档案
        BOCopyAPI copyAPI = CommonBiz.createProjectRecordInfo("BO_XR_PM_PROJECT_APPLY", boId);
        //项目成员
        copyAPI.addNewData( "PROJECT_TEAM",uid);
        //项目编号
        copyAPI.addNewData( "PROJECT_CODE",projectCode);
        //项目状态
        copyAPI.addNewData( "FLAG","0");
        //更新项目团队成员
        SDK.getBOAPI().updateByBindId("BO_XR_PM_PROJECT_MEMBER",bindId,"PROJECT_NAME",projectName);
        SDK.getBOAPI().updateByBindId("BO_XR_PM_PROJECT_MEMBER",bindId,"PROJECT_CODE",projectCode);
        //更新项目里程碑
        SDK.getBOAPI().updateByBindId("BO_XR_PM_PROJECT_MILESTONE",bindId,"PROJECT_NAME",projectCode);
        SDK.getBOAPI().updateByBindId("BO_XR_PM_PROJECT_MILESTONE",bindId,"PROJECT_CODE",projectCode);
        // 执行复制操作
        copyAPI.exec();

        return true;
    }

    public static void main(String[] args) {
        String a = "asdf,wr3";
        a = a.replace(",","','");
        System.out.println("('"+a+"')");
    }
}
