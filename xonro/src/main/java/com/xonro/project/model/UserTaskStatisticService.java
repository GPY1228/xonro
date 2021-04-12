package com.xonro.project.model;

import com.actionsoft.bpms.commons.database.RowMap;
import com.actionsoft.bpms.util.DBSql;

import java.util.List;

/**
 * @author GPY
 * @date 2021/4/8
 * @des 统计员工任务服务处理类
 */
public class UserTaskStatisticService {

    public List<RowMap> getUserTask(){
        String sql = "SELECT o.USERID AS userId,o.USERNAME AS userName,tc.PROJECT_NAME AS projectName,DATE_FORMAT( tc.CREATEDATE,\"%Y-%m-%d\") AS createTime FROM orguser o" +
                " LEFT JOIN BO_XR_PM_TIME_COST tc ON o.USERID = tc.USER_ID "+
                " WHERE tc.CREATEDATE >= '2021-04-01' " +
                " GROUP BY o.USERID,tc.PROJECT_CODE,DATE_FORMAT( tc.CREATEDATE, \"%Y-%m-%d\" ) ORDER BY o.USERNO,tc.CREATEDATE ";
        List<RowMap> orgUsersTasks = DBSql.getMaps(sql);
        return orgUsersTasks;
    }

    public List<RowMap> getOrgUsersList(String daptId){
        String sql = " SELECT USERID,USERNAME,USERNO FROM orguser ";
        List<RowMap> getOrgUsersList = DBSql.getMaps(sql);
        return getOrgUsersList;
    }

    public List<RowMap> getUsersWeekSumList(String beginTime,String endTime){
        String sql = " SELECT USER_ID,USER_NAME,BINDID FROM bo_xr_week_summary WHERE CREATEDATE >= '"+beginTime+"' and CREATEDATE<= '"+endTime+"' ";
        List<RowMap> getOrgUsersList = DBSql.getMaps(sql);
        return getOrgUsersList;
    }

    //周工作时常
    public List<RowMap> getUserWorkHuors(String beginTime,String endTime){
        String selectUserLists = " select sum(WORK_HOURS) AS value,USER_ID from BO_XR_PM_TIME_COST WHERE CREATEDATE >= '"+beginTime+"'and CREATEDATE<= '"+endTime+"' GROUP BY USER_ID ";
        List<RowMap> userDetailLists = DBSql.getMaps(selectUserLists);
        return userDetailLists;
    }

    //周工作产值项目时常
    public List<RowMap> getIsOpenProjectHours(String beginTime,String endTime){
        String selectUserLists = " select sum(WORK_HOURS) AS value,USER_ID  from BO_XR_PM_TIME_COST c left join bo_xr_pm_project p on c.PROJECT_CODE = p.PROJECT_CODE " +
                " WHERE c.CREATEDATE >= '"+beginTime+"'and c.CREATEDATE<= '"+endTime+"' and p.IS_OPEN = 1 GROUP BY USER_ID ";
        List<RowMap> userDetailLists = DBSql.getMaps(selectUserLists);
        return userDetailLists;
    }
}
