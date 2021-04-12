package com.xonro.project.task.controller;

import com.actionsoft.bpms.commons.database.RowMap;
import com.actionsoft.bpms.commons.htmlframework.HtmlPageTemplate;
import com.actionsoft.bpms.commons.mvc.view.ResponseObject;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.server.bind.annotation.Controller;
import com.actionsoft.bpms.server.bind.annotation.Mapping;
import com.actionsoft.bpms.util.DBSql;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
public class rili {

    @Mapping("rili.riliPage")
    public String  rili (UserContext us) throws Exception{
        String uid =  us.getUID();
        /**
        * 请假
        */
        String dates = getQingJia(us);
        /**
         * 未完成任务
         */
        String noDays = getNoDo(us);
        /**
         * 已完成任务
         */
        String doDays = getDo(us);
        Map map = new HashMap();
        map.put("date",dates);
        map.put("noDo",noDays);
        map.put("do",doDays);
         return HtmlPageTemplate.merge("com.xonro.apps.xr","rili.html",map);
    }
    /**
     * 已完成任务
     */
    private String getDo(UserContext uc) throws ParseException {
        String doDays = "";

        List<RowMap> list = DBSql.getMaps("select END_DATE,COUNT(1) as COUNT  from BO_XR_PM_TASK where FLAG  " +
                "=3 and TASK_OWNER ='"+uc.getUID()+"' group by END_DATE");
        String doSql="select END_DATE,COUNT(1) as COUNT  from BO_XR_PM_TASK where FLAG=3 and TASK_OWNER ='"+uc.getUID()+"' group by END_DATE";
        System.out.println( "doSql:"+doSql );
        for(RowMap rowMap:list){
            Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(rowMap.getString("END_DATE"));
            String all = getStringDate(date);
            String count = rowMap.getString("COUNT");
            String all1 = all+"."+ count;
            doDays+=all1+",";
        }
        return doDays;
    }
    /**
     * 未完成任务
     */
    private String getNoDo(UserContext uc) throws ParseException {
        String noDays = "";
        List<RowMap> list = DBSql.getMaps("select PLAN_START_DATE,COUNT(1) as COUNT  from BO_XR_PM_TASK where FLAG  " +
                "in ('1','2','5') and TASK_OWNER ='"+uc.getUID()+"' group by PLAN_START_DATE");
        String sql="select PLAN_START_DATE,COUNT(1) as COUNT  from BO_XR_PM_TASK where FLAG in ('1','2','5') and TASK_OWNER ='"+uc.getUID()+"' group by PLAN_START_DATE";
        System.out.println( "sql:"+sql );
        for(RowMap rowMap:list){
            Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(rowMap.getString("PLAN_START_DATE"));
            String all = getStringDate(date);
            String count = rowMap.getString("COUNT");
            String all1 = all+"."+ count;
            noDays+=all1+",";
        }
        return noDays;
    }

    private String getQingJia(UserContext uc) throws ParseException {
        String dates = "";
        //根据用户id查询其当月是否有请假
        List<RowMap> maps=  DBSql.getMaps("select START_DATE ,END_DATE ,DAYS " +
                "from BO_XR_HOLIDAY where USER_ID = '"+uc.getUID()+"'");
        for(RowMap rowMap:maps){
            Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(rowMap.getString("START_DATE"));
            String all = getStringDate(date);
            dates+=all+",";
        }
        return dates;
    }

    //拼接年月日字符串
    private String getStringDate(Date date) {
        Calendar now = Calendar.getInstance();
        now.setTime(date);
        int year = now.get(Calendar.YEAR);
        int month = now.get(Calendar.MONTH)+1;
        int day = now.get(Calendar.DAY_OF_MONTH);
        String days="";
        String months="";
        if(month<10){
            months = "0"+month;
        }else{
            months=""+month;
        }
        if(day<10){
            days="0"+day;
        }else {
            days=""+day;
        }
        return year+""+months+days;
    }


}
