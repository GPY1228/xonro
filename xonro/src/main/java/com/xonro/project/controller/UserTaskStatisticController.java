package com.xonro.project.controller;

import com.actionsoft.bpms.commons.database.RowMap;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.server.bind.annotation.Controller;
import com.actionsoft.bpms.server.bind.annotation.Mapping;
import com.alibaba.fastjson.JSON;
import com.xonro.project.bean.UserTaskList;
import com.xonro.project.bean.UserWeekTaskList;
import com.xonro.project.model.UserTaskStatisticService;
import com.xonro.project.util.DateUtil;

import java.util.*;

/**
 * @author GPY
 * @date 2021/4/8
 * @des 按周统计员工任务
 */
@Controller
public class UserTaskStatisticController {

    @Mapping(value ="pm.controller.userTaskStatistic")  //,session = false,noSessionEvaluate = "",noSessionReason = ""
    public String userTaskStatistic(UserContext me, String departId, String year, int weekly){
        String beginTime = DateUtil.formatDate(DateUtil.getYearWeeklyDay(2021, 15, 2));
        String endTime = DateUtil.formatDate(DateUtil.getYearWeeklyDay(2021, 16, 1));

        List<UserTaskList> userTaskLists = new ArrayList<>();
        UserTaskStatisticService userTaskStatisticService = new UserTaskStatisticService();

        //每日工作任务
        List<RowMap> listUserTasks = userTaskStatisticService.getUserTask();
        //部门员工
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

        return userTaskListsJson;
    }

    public static void main(String[] args) {
        List list = new ArrayList();
        for (int i=2;i<8;i++) {
            Date date = DateUtil.getYearWeeklyDay(2021, 15, i);
            list.add(DateUtil.formatDate(date));
        }
        list.add(DateUtil.formatDate(DateUtil.getYearWeeklyDay(2021, 16, 1)));
        list.stream().forEach(System.out::println);
    }
}
