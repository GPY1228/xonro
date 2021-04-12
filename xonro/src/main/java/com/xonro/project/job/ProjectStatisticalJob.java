package com.xonro.project.job;


import com.actionsoft.bpms.bo.engine.BO;
import com.actionsoft.bpms.commons.database.RowMap;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.sdk.local.SDK;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.List;


/**
 * 项目工时统计
 *
 * @author GPY
 * @date 2021-3-31
 */
public class ProjectStatisticalJob implements Job {
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        String sql = "select PROJECT_CODE,PLAN_DAYS,CONTRACT_DAYS from BO_XR_PM_PROJECT where FLAG = 0 ";
        List<RowMap> orgUsers = DBSql.getMaps(sql);
        orgUsers.forEach(orgUser -> {
            //更新主表数据 （更新实际总人天）
            String projectCode = orgUser.getString("PROJECT_CODE");
            String selectProjectCountSql = "select sum(WORK_HOURS) AS countWorkHours from BO_XR_PM_TIME_COST where PROJECT_CODE = '" + projectCode + "'  ";
            Double countWorkHours = DBSql.getDouble(selectProjectCountSql, "countWorkHours");
            BO bo = SDK.getBOAPI().query("BO_XR_PM_PROJECT").addQuery("PROJECT_CODE=", projectCode).detail();
            //总投入时长
            Double sumHours = countWorkHours;
            //总投入人天
            //Double sumDays = Double.valueOf(orgUser.getString("CONTRACT_DAYS"));
            bo.set("ACTUAL_DAYS", sumHours / 8);
            SDK.getBOAPI().update("BO_XR_PM_PROJECT", bo);
            //（一）更新子表数据先查询工作日
            //1、工作时长求和：该人员工作时长流水之和;(根据人员分组)
            String sumPopHuorsSql = "select USER_ID,sum(WORK_HOURS) as workHours from BO_XR_PM_TIME_COST where PROJECT_CODE = '" + projectCode + "' and IS_OVERTIME = 0 group by USER_ID";
            List<RowMap> popHours = DBSql.getMaps(sumPopHuorsSql);
            popHours.forEach(popHour -> {
                //2、时长占比：个人累计时长除以团队正常总时长
                Double workHours = Double.valueOf(popHour.getString("workHours").toString());
                Double popratio = workHours / sumHours;
                //3、实际天数：个人部分实际天数总时长。
                Double popDays = workHours / 8;
                //4、差额：计划天数-实际天数。如果是负数，则显示负数
                Double planDays = Double.valueOf(orgUser.getString("PLAN_DAYS"));
                Double balance = (planDays - popDays)/8;
                BO projectMember = SDK.getBOAPI().query("BO_XR_PM_PROJECT_MEMBER").addQuery("USER_ID=", popHour.getString("USER_ID")).detail();
                if (projectMember != null) {
                    popratio = Double.valueOf(Math.round(popratio * 100));
                    projectMember.set("WORK_HOURS",workHours);
                    projectMember.set("WORK_RATIO", popratio + "%");
                    projectMember.set("MARGIN", String.format("%.2f", balance));
                    projectMember.set("ACTUAL_DAYS",String.format("%.2f", popDays));
                    SDK.getBOAPI().update("BO_XR_PM_PROJECT_MEMBER", projectMember);
                }
            });
            //（二）更新子表数据再查询加班时长
            String sumPopOverTimeSql = "select USER_ID,sum(WORK_HOURS) as workHours from BO_XR_PM_TIME_COST where PROJECT_CODE = '" + projectCode + "' and IS_OVERTIME = 1 group by USER_ID";
            List<RowMap> popOverTimes = DBSql.getMaps(sumPopOverTimeSql);
            Double sumWorkHours = 0d;
            //计算团队加班总时长
            for (RowMap rowMap : popOverTimes) {
                Double workHours = Double.valueOf("".equals(rowMap.getString("workHours")) ? "0.00" : rowMap.getString("workHours"));
                //Double workHours = Double.valueOf(rowMap.getString("workHours"));
                sumWorkHours += workHours;
            }
            Double finalSumWorkHours = sumWorkHours;
            //循环更新项目团队人员(BO_XR_PM_PROJECT_MEMBER)表
            popOverTimes.forEach(popOverTime -> {
                //加班时长求和：该人员工作时长流水之和；
                Double popOverHours = Double.valueOf(popOverTime.get("workHours").toString());
                //加班时长：个人累计加班时长占团队加班时长总合
                Double overRatio = popOverHours / finalSumWorkHours;
                BO projectMember = SDK.getBOAPI().query("BO_XR_PM_PROJECT_MEMBER").addQuery("USER_ID=", popOverTime.getString("USER_ID")).detail();
                if (projectMember != null) {
                    projectMember.set("OVER_RATIO",String.format("%.2f", overRatio));
                    projectMember.set("OVER_HOURS", popOverHours);
                    SDK.getBOAPI().update("BO_XR_PM_PROJECT_MEMBER", projectMember);
                }
            });
        });
    }
}

