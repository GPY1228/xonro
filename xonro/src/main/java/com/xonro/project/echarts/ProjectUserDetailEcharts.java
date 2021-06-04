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
