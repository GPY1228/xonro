package com.xonro.project.event;

import com.actionsoft.bpms.bo.engine.BO;
import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.InterruptListener;
import com.actionsoft.bpms.bpmn.engine.listener.InterruptListenerInterface;
import com.actionsoft.exception.BPMNError;
import com.actionsoft.sdk.local.SDK;
import dm.jdbc.util.StringUtil;

/**
 * 培训申请第三节点完成后事件
 */
public class TrainApplyStep3AfterComplete  extends InterruptListener implements InterruptListenerInterface {
    public boolean execute(ProcessExecutionContext ctx) throws Exception {
        //获取主表bo数据
        BO bo=ctx.getBO( "BO_XR_TRAIN_APPLY" );
        //获取实际参会人员
        String actualParticipantsId=bo.getString( "ACTUAL_PARTICIPANTS_ID" );
        //通过空字符串进行切割
        if (StringUtil.isNotEmpty( actualParticipantsId )){
            String[] allId=actualParticipantsId.split( " " );
            if (allId.length>0){
                for (int i=0;i<allId.length;i++){
                    //根据id获取人姓名
                    String userName=SDK.getORGAPI().getUser( allId[i] ).getUserName();
                    //为当前子表创建数据
                    BO bo1=new BO();
                    bo1.set( "PARTICIPANTS",userName );
                    bo1.set( "PARTICIPANTS_ID",allId[i]  );
                    SDK.getBOAPI().create( "BO_XR_TRAIN_APPLY_GRID",bo1,ctx.getProcessInstance(),ctx.getUserContext() );
                }
            }
            return true;
        }else {
            throw new BPMNError( "0331","获取实际参会人员出错，请联系管理员！" );
        }
    }
}
