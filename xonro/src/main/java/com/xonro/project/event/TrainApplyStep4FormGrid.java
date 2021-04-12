package com.xonro.project.event;

import com.actionsoft.bpms.bo.design.model.BOItemModel;
import com.actionsoft.bpms.bo.engine.BO;
import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.FormGridFilterListener;
import com.actionsoft.bpms.bpmn.engine.listener.FormGridRowLookAndFeel;
import com.actionsoft.bpms.bpmn.engine.listener.ListenerConst;
import com.actionsoft.bpms.bpmn.engine.listener.ValueListenerInterface;
import com.actionsoft.bpms.form.design.model.FormItemModel;

import java.util.List;

public class TrainApplyStep4FormGrid extends FormGridFilterListener implements ValueListenerInterface {
    public TrainApplyStep4FormGrid() {
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
        String tableName = context.getParameterOfString( ListenerConst.FORM_EVENT_PARAM_BONAME);
        if (tableName.equals( "BO_XR_TRAIN_APPLY_GRID" )){
            //创建一个对象
            FormGridRowLookAndFeel diyLookAndFeel = new FormGridRowLookAndFeel();
            String uid = bo.getString("PARTICIPANTS_ID");
            if (!uid.equals( context.getUserContext().getUID() )){
                diyLookAndFeel.setDisplay(false);//不显示这条数据
            }
            //处理好之后，将该对象返回
            return diyLookAndFeel;
        }
        return null;
    }
}
