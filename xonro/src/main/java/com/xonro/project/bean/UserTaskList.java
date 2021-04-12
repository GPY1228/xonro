package com.xonro.project.bean;

import java.util.List;
import java.util.Map;

/**
 * @author GPY
 * @date 2021/4/8
 * @des 员工任务列表
 */
public class UserTaskList {

    private String userName;

    private String UserId;

    private List<UserWeekTaskList> userWeekTaskList;

    private String weekLink = null; //周总结

    private String ratio = null; //资源利用率

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public List<UserWeekTaskList> getUserWeekTaskList() {
        return userWeekTaskList;
    }

    public void setUserWeekTaskList(List<UserWeekTaskList> userWeekTaskList) {
        this.userWeekTaskList = userWeekTaskList;
    }

    public String getWeekLink() {
        return weekLink;
    }

    public void setWeekLink(String weekLink) {
        this.weekLink = weekLink;
    }

    public String getRatio() {
        return ratio;
    }

    public void setRatio(String ratio) {
        this.ratio = ratio;
    }
}
