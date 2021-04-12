package com.xonro.project.echarts;

import com.actionsoft.bpms.commons.database.RowMap;
import com.actionsoft.bpms.commons.htmlframework.HtmlPageTemplate;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.server.bind.annotation.Controller;
import com.actionsoft.bpms.server.bind.annotation.Mapping;
import com.actionsoft.bpms.util.DBSql;
import com.alibaba.fastjson.JSON;
import com.xonro.project.bean.UserTaskList;
import com.xonro.project.bean.UserWeekTaskList;
import com.xonro.project.model.UserTaskStatisticService;
import com.xonro.project.util.DateUtil;
import net.sf.json.JSONObject;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

/**
 * @author GPY
 * @date 2021/4/6
 * @des 项目-人力投入明细汇总
 */

@Controller
public class ProjectUserDetailEcharts {

    @Mapping("pm.controller.projectDetailEcharts")
    public String projectDetailEcharts(UserContext me, String projectCode){
        String selectUserLists = " select sum(WORK_HOURS)/8 AS value,USER_NAME AS name from BO_XR_PM_TIME_COST WHERE PROJECT_CODE = '"+projectCode+"' GROUP BY USER_ID ";
        List<RowMap> userDetailLists = DBSql.getMaps(selectUserLists);
        List<Map> userDetailListsTwo = new ArrayList<>();
        for (RowMap userDetailList : userDetailLists) {
            userDetailListsTwo.add(toLowerKey(userDetailList));
        }
        String json = JSON.toJSONString(userDetailListsTwo);
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("userDetailLists", json); // 返 回 参数

        //测试
        List<UserTaskList> userTaskLists = new ArrayList<>();
        System.out.println("======================测试");
        String beginTime = DateUtil.formatDate(DateUtil.getYearWeeklyDay(2021, 15, 2));
        String endTime = DateUtil.formatDate(DateUtil.getYearWeeklyDay(2021, 16, 1));
        UserTaskStatisticService userTaskStatisticService = new UserTaskStatisticService();
        List<RowMap> listUserTasks = userTaskStatisticService.getUserTask();
        List<RowMap> getOrgUsersLists = userTaskStatisticService.getOrgUsersList("");

        //周报查询
        List<RowMap> usersWeekSumLists = userTaskStatisticService.getUsersWeekSumList(beginTime,endTime);
        //周工作时长
        List<RowMap> getUserWorkHuors = userTaskStatisticService.getUserWorkHuors(beginTime,endTime);
        //产值工作时长
        List<RowMap> getIsOpenProjectHours = userTaskStatisticService.getIsOpenProjectHours(beginTime,endTime);
        getOrgUsersLists.forEach(getOrgUsersList->{

            UserTaskList userTaskList = new UserTaskList();
            List<UserWeekTaskList> userWeekTaskLists = new ArrayList<>();

            userTaskList.setUserId(getOrgUsersList.getString("USERID"));
            userTaskList.setUserName(getOrgUsersList.getString("USERNAME"));
            for (int i=1;i<8;i++) {
                UserWeekTaskList userWeekTaskList = new UserWeekTaskList();
                int finalI = i;
                listUserTasks.forEach(listUserTask -> {
                    if (getOrgUsersList.getString("USERID").equals(listUserTask.getString("userId"))) {
                        //1表示周日，2表示周一，7表示周六
                        Date date = DateUtil.getYearWeeklyDay(2021, 15, finalI);
                        if (DateUtil.formatDate(date).equals(listUserTask.getString("createTime"))) {
                            List<Map> list = new ArrayList<>();
                            Map map = new HashMap();
                            map.put("projectName", listUserTask.getString("projectName"));
                            map.put("createTime",DateUtil.formatDate(date));
                            list.add(map);
                            switch (finalI) {
                                case 1: //周日
                                    userWeekTaskList.setSundayTaskList(list);
                                    break;
                                case 2: //周一
                                    userWeekTaskList.setMondayTaskList(list);
                                    break;
                                case 3: //周二
                                    userWeekTaskList.setTuesdayTaskList(list);
                                    break;
                                case 4: //周三
                                    userWeekTaskList.setWednesdayTaskList(list);
                                    break;
                                case 5: //周四
                                    userWeekTaskList.setThursdayTaskList(list);
                                    break;
                                case 6: //周五
                                    userWeekTaskList.setFridayTaskList(list);
                                    break;
                                case 7: //周六
                                    userWeekTaskList.setSaturdayTaskList(list);
                                    break;
                                default:
                                    break;
                            }
                        }
                    }

                });
                userWeekTaskLists.add(userWeekTaskList);
            }
            userTaskList.setUserWeekTaskList(userWeekTaskLists);
            //统计本周员工是否已提交周报
            usersWeekSumLists.forEach(usersWeekSumList->{
                if (usersWeekSumList.getString("USERID").equals(getOrgUsersList.getString("USERID"))){
                    userTaskList.setWeekLink(usersWeekSumList.getString("BINDID"));
                }else {
                    userTaskList.setWeekLink("无提交记录");
                }
            });

            //计算员工资源利用率（当前周内，产值项目占全部工作时常比率）
            getUserWorkHuors.forEach(getUserWorkHuor->{
                Double sumHours = Double.valueOf(getUserWorkHuor.getString("value"));
                if (getOrgUsersList.getString("USERID").equals(getUserWorkHuor.getString("USER_ID"))){
                    getIsOpenProjectHours.forEach(getIsOpenProjectHour->{
                        Double isOpenSumHours = Double.valueOf(getIsOpenProjectHour.getString("value"));
                        if (getOrgUsersList.getString("USERID").equals(getIsOpenProjectHour.getString("USER_ID"))){
                            userTaskList.setRatio(isOpenSumHours/sumHours*100+"%");
                        }
                    });
                }else {
                    userTaskList.setRatio(0.0+"%");
                }
            });
            userTaskLists.add(userTaskList);
        });

        String userTaskListsJson = JSON.toJSONString(userTaskLists);
        result.put("userTaskLists", userTaskListsJson); // 返 回 参数

        return HtmlPageTemplate.merge("com.xonro.apps.xr", "echarts.html", result); // 参数说明：appId，返回页面， 返回参数 }
    }

    @Mapping("pm.controller.projectMemberDetailEcharts")
    public String projectMemberDetailEcharts(UserContext me, String projectCode){
        System.out.println(projectCode);
        String selectMemberLists = " SELECT USER_NAME AS name,USER_ID,PLAN_DAYS AS planDays,ACTUAL_DAYS AS actualDays FROM BO_XR_PM_PROJECT_MEMBER WHERE PROJECT_CODE = '"+projectCode+"' GROUP BY USER_ID  ";
        List<RowMap> memberDetailLists = DBSql.getMaps(selectMemberLists);
        List listName = new ArrayList();
        List listplanDays = new ArrayList();
        List listactualDays = new ArrayList();
        memberDetailLists.forEach(memberDetailList->{
            listName.add(memberDetailList.getString("name"));
            listplanDays.add(memberDetailList.getString("planDays"));
            listactualDays.add(memberDetailList.getString("actualDays"));
        });
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("listName", JSON.toJSONString(listName));
        result.put("listplanDays", JSON.toJSONString(listplanDays));
        result.put("listactualDays", JSON.toJSONString(listactualDays));
        return HtmlPageTemplate.merge("com.xonro.apps.xr", "memberEcharts.html", result); // 参数说明：appId，返回页面， 返回参数 }
    }

    public static Map<String, Object> toLowerKey(Map<String, Object> map) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        Set<String> sets = map.keySet();
        for (String key : sets) {
            resultMap.put(key.toLowerCase(), map.get(key));
        }
        return resultMap;
    }

    public static void main(String[] args) {
        List<Map> strList = new ArrayList<>();
        Map map = new HashMap();
        map.put("name","zhangshan");
        map.put("value","1111");
        strList.add(map);
        String json = JSON.toJSONString(strList);
        System.out.println(json);

    }
}
