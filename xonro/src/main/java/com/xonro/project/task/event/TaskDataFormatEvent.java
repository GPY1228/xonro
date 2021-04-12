package com.xonro.project.task.event;

import com.actionsoft.bpms.bo.engine.BO;
import com.actionsoft.bpms.dw.design.event.DataWindowBeforeLoadEventInterface;
import com.actionsoft.bpms.dw.exec.component.DataView;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.sdk.local.SDK;

import java.util.List;

public class TaskDataFormatEvent  implements DataWindowBeforeLoadEventInterface {
    @Override
    public boolean excute(UserContext userContext, DataView dataView) {
        //获取当前人
        String uid=userContext.getUID();
        //获取当前人下的项目
        List<BO> list=SDK.getBOAPI().query( "PROECT_EXECUTOR" ).addQuery( "PROECT_EXECUTOR=",uid ).list();
        if (list.isEmpty()){
            return false;
        }else {
            //遍历任务
            for (BO bo:list){
                String projectCode=bo.getString( "PROJECT_CODE" );

            }
        }
        return true;
    }
}
