package com.xonro.project.task.event;

import com.actionsoft.bpms.bo.design.model.BOItemModel;
import com.actionsoft.bpms.bo.engine.BO;
import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.FormGridFilterListener;
import com.actionsoft.bpms.bpmn.engine.listener.FormGridRowLookAndFeel;
import com.actionsoft.bpms.bpmn.engine.listener.ListenerConst;
import com.actionsoft.bpms.bpmn.engine.listener.ValueListenerInterface;
import com.actionsoft.bpms.form.design.model.FormItemModel;
import jodd.util.StringUtil;

import java.util.List;

public class weekGridEvent extends FormGridFilterListener implements ValueListenerInterface {

    public weekGridEvent() {
        super();
    }

    @Override
    public String getEditGridHeaderJSON(ProcessExecutionContext arg0, FormItemModel arg1, List<String> arg2) {
        return super.getEditGridHeaderJSON( arg0, arg1, arg2 );
    }

    @Override
    public String orderByStatement(ProcessExecutionContext processExecutionContext) {
        return null;
    }

    @Override
    public String getCustomeTableHeaderHtml(ProcessExecutionContext processExecutionContext, FormItemModel formItemModel, List<String> list) {
        return null;
    }

    @Override
    public FormGridRowLookAndFeel acceptRowData(ProcessExecutionContext context, List<BOItemModel> list, BO bo) {
        String sid = context.getUserContext().getSessionId();
        String tableName = context.getParameterOfString( ListenerConst.FORM_EVENT_PARAM_BONAME );
        if (tableName.equals( "BO_XR_WEEK_SUMMARY_TASK" )) {
            //创建一个对象
            FormGridRowLookAndFeel diyLookAndFeel = new FormGridRowLookAndFeel();
            String pbindid = bo.getString( "PBINDID" );
            if (StringUtil.isNotEmpty( pbindid )) {
                String link = "<a href='/portal/r/w?sid=" + sid + "&cmd=CLIENT_DW_FORM_READONLYPAGE&bindid=" + pbindid + "&processDefId=obj_a0d3735afa9c419eadd335cb6e393e44&taskInstId=0' target='_blank'>任务详情";
                bo.set( "PBINDID_SHOW", link );
            }
            String taskRatio = bo.getString( "TASK_RATIO" );
            if (StringUtil.isNotEmpty( taskRatio )) {
                Double radio = Double.valueOf( taskRatio );
                if (radio > 0) {
                    taskRatio = "<img src=../apps/com.xonro.apps.xr/css/向上.png>" + taskRatio;
                } else if (radio < 0) {
                    taskRatio = "<img src=../apps/com.xonro.apps.xr/css/向下.png>" + taskRatio;
                }
                bo.set( "TASK_RATIO_SHOW", taskRatio );
            }
            //处理好之后，将该对象返回
            return diyLookAndFeel;
        }
        return null;
    }
}
