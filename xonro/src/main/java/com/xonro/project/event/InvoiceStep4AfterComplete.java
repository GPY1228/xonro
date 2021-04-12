package com.xonro.project.event;

import com.actionsoft.bpms.bo.engine.BO;
import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListener;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListenerInterface;
import com.xonro.project.biz.countInvoiceBiz;
import dm.jdbc.util.StringUtil;

/**
 * 开票流程第四节点完成后事件
 *
 * @author zhaoqt
 * @date 20191022
 */
public class InvoiceStep4AfterComplete extends ExecuteListener implements ExecuteListenerInterface {
    public void execute(ProcessExecutionContext ctx) throws Exception {
        if (ctx.isChoiceActionMenu( "快递完毕" )) {
            //获取主表数据
            BO mainBO = ctx.getBO( "BO_XR_CM_INVOICE" );
            if (mainBO != null) {
                //发票日期
                String invoiceDate = mainBO.getString( "INVOICE_DATE" );
                //开票金额
                String amount = mainBO.getString( "AMOUNT" );
                if (StringUtil.isNotEmpty( amount ) && StringUtil.isNotEmpty( invoiceDate )) {
                    countInvoiceBiz.updateData(invoiceDate,amount,ctx.getProcessInstance().getId());
                }
            }
        }
    }
}
