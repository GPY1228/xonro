package com.xonro.project.event;

import com.actionsoft.bpms.bo.design.model.BOItemModel;
import com.actionsoft.bpms.bo.engine.BO;
import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.FormGridFilterListener;
import com.actionsoft.bpms.bpmn.engine.listener.FormGridRowLookAndFeel;
import com.actionsoft.bpms.bpmn.engine.listener.ListenerConst;
import com.actionsoft.bpms.form.design.model.FormItemModel;
import org.apache.commons.lang.StringUtils;

import java.util.List;

/**
 * 知会物控计划和销售计划。只能看到自己所在工厂或部门的数据
 */
public class OrderAdjustmentYWStep12FormGridFilter extends FormGridFilterListener {
    @Override
    public String getDescription() {
        return "如果子表状态为关闭，将改行显示为蓝色";
    }

    @Override
    public FormGridRowLookAndFeel acceptRowData(ProcessExecutionContext ctx, List<BOItemModel> list, BO boData) {
        String tableName = ctx.getParameterOfString(ListenerConst.FORM_EVENT_PARAM_BONAME);

        if (tableName.equals("BO_XR_AM_SUMMARY_S1")) {
            String userId = ctx.getProcessInstance().getCreateUser();
            FormGridRowLookAndFeel diyLookAndFeel = new FormGridRowLookAndFeel();
            //当状态不为关闭时，原计划回复交期=二次计划回复交期，标红颜色
            if(StringUtils.equals( "1",boData.getString( "FLAG" ) )){
                    diyLookAndFeel.setCellCSS("style='background-color:#FF0000;");
            }
            //如果计划类型为计划外，则整行背景色改为黄色
            if(StringUtils.equals("2", boData.getString("PLAN_TYPE"))){
                diyLookAndFeel.setCellCSS("style='background-color:#EEEE11;");
            }
            return diyLookAndFeel;
        }
        return null;
    }

    @Override
    public String getCustomeTableHeaderHtml(ProcessExecutionContext processExecutionContext, FormItemModel formItemModel, List<String> list) {
        return null;
    }

    @Override
    public String orderByStatement(ProcessExecutionContext processExecutionContext) {
        return null;
    }

}
