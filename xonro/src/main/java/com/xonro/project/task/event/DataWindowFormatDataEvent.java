package com.xonro.project.task.event;

import com.actionsoft.bpms.dw.design.event.DataWindowFormatDataEventInterface;
import com.actionsoft.bpms.dw.exec.data.DataSourceEngine;
import com.actionsoft.bpms.server.UserContext;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import dm.jdbc.util.StringUtil;

public class DataWindowFormatDataEvent implements DataWindowFormatDataEventInterface {
    @Override
    public void formatData(UserContext userContext, JSONArray datas) {
        for (Object datao : datas) {
            JSONObject data = (JSONObject) datao;
            String columnValue = data.getString("TASK_RATIO"); // 注意有些特殊组件的值为JSONObject，请根据情况使用getJSONObject获取相应值
            if (StringUtil.isNotEmpty( columnValue )){
                Double radio = Double.valueOf( columnValue );
                if (radio>0){
                    columnValue = "<img src=../apps/com.xonro.apps.xr/css/向上.png>"+columnValue;
                }else if (radio<0){
                    columnValue = "<img src=../apps/com.xonro.apps.xr/css/向下.png>"+columnValue;
                }
                //虚拟字段VIRTUALBUTTON ，组件为按钮不显示
                //data.put("VIRTUALBUTTON" ,"");
                //data.put("COLUMNTYPE_VIRTUALBUTTON", "");
                data.put("TASK_RATIO" + DataSourceEngine.AWS_DW_FIXED_CLOMUN_SHOW_RULE_SUFFIX, columnValue);
            }
        }
    }
}
