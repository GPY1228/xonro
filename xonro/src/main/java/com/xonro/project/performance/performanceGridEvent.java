package com.xonro.project.performance;

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

public class performanceGridEvent extends FormGridFilterListener implements ValueListenerInterface {
    @Override
    public String getEditGridHeaderJSON(ProcessExecutionContext arg0, FormItemModel arg1, List<String> arg2) {
        return super.getEditGridHeaderJSON( arg0, arg1, arg2 );
    }

    public performanceGridEvent() {
        super();
    }

    @Override
    public String orderByStatement(ProcessExecutionContext processExecutionContext) {
        return null;
    }

    @Override
    public FormGridRowLookAndFeel acceptRowData(ProcessExecutionContext context, List<BOItemModel> list, BO bo) {
        String sid = context.getUserContext().getSessionId();
        String tableName = context.getParameterOfString( ListenerConst.FORM_EVENT_PARAM_BONAME );
        if (tableName.equals( "BO_XR_CHECK_MONTH_LIST" )) {
            //创建一个对象
            FormGridRowLookAndFeel diyLookAndFeel = new FormGridRowLookAndFeel();
            String score = bo.getString( "SCORE" );
            Double score1 = Double.valueOf( score );
            String actualScore = bo.getString( "ACTUAL_SCORE" );
            if (StringUtil.isEmpty( actualScore )){
                return diyLookAndFeel;
            }
            Double actualScore1 = Double.valueOf( actualScore );
            String actualScoreShow = bo.getString( "ACTUAL_SCORE" );
            if (score1 > actualScore1) {
                actualScoreShow = "<img src=../apps/com.xonro.apps.xr/css/向下.png>" + actualScore;
            }
            bo.set( "ACTUAL_SCORE_SHOW", actualScoreShow );
            //处理好之后，将该对象返回
            return diyLookAndFeel;
        }
        return null;
    }

    @Override
    public String getCustomeTableHeaderHtml(ProcessExecutionContext processExecutionContext, FormItemModel formItemModel, List<String> list) {
        return null;
    }
}
