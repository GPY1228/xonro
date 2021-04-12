package com.xonro.project.bean;

import java.util.List;
import java.util.Map;

/**
 * @author GPY
 * @date 2021/4/8
 * @des 员工各工作日任务列表
 */
public class UserWeekTaskList {

    private List<Map> mondayTaskList = null;
    private List<Map> tuesdayTaskList = null;
    private List<Map> wednesdayTaskList = null;
    private List<Map> thursdayTaskList = null;
    private List<Map> fridayTaskList = null;
    private List<Map> saturdayTaskList = null;
    private List<Map> sundayTaskList = null;

    public UserWeekTaskList(){

    }

    public List<Map> getMondayTaskList() {
        return mondayTaskList;
    }

    public void setMondayTaskList(List<Map> mondayTaskList) {
        this.mondayTaskList = mondayTaskList;
    }

    public List<Map> getTuesdayTaskList() {
        return tuesdayTaskList;
    }

    public void setTuesdayTaskList(List<Map> tuesdayTaskList) {
        this.tuesdayTaskList = tuesdayTaskList;
    }

    public List<Map> getWednesdayTaskList() {
        return wednesdayTaskList;
    }

    public void setWednesdayTaskList(List<Map> wednesdayTaskList) {
        this.wednesdayTaskList = wednesdayTaskList;
    }

    public List<Map> getThursdayTaskList() {
        return thursdayTaskList;
    }

    public void setThursdayTaskList(List<Map> thursdayTaskList) {
        this.thursdayTaskList = thursdayTaskList;
    }

    public List<Map> getFridayTaskList() {
        return fridayTaskList;
    }

    public void setFridayTaskList(List<Map> fridayTaskList) {
        this.fridayTaskList = fridayTaskList;
    }

    public List<Map> getSaturdayTaskList() {
        return saturdayTaskList;
    }

    public void setSaturdayTaskList(List<Map> saturdayTaskList) {
        this.saturdayTaskList = saturdayTaskList;
    }

    public List<Map> getSundayTaskList() {
        return sundayTaskList;
    }

    public void setSundayTaskList(List<Map> sundayTaskList) {
        this.sundayTaskList = sundayTaskList;
    }

}
