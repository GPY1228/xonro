package com.xonro.project.job;

import com.actionsoft.bpms.commons.database.RowMap;
import com.actionsoft.bpms.schedule.IJob;
import com.actionsoft.bpms.util.DBSql;
import com.xonro.project.biz.countInvoiceBiz;
import dm.jdbc.util.StringUtil;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.List;
import java.util.Map;

public class refreshJob implements IJob {
    public void execute(JobExecutionContext ctx) throws JobExecutionException {
        String sql="select * from BO_XR_CM_INVOICE";
        List<RowMap> list=DBSql.getMaps( sql );
        for (RowMap rowMap:list){
            String invoiceDate=rowMap.getString( "INVOICE_DATE" );
            String amount=rowMap.getString( "AMOUNT" );
            String bindid=rowMap.getString( "BINDID" );
            if (StringUtil.isNotEmpty( invoiceDate ) && StringUtil.isNotEmpty( amount ) && StringUtil.isNotEmpty( bindid )){
                countInvoiceBiz.updateData(invoiceDate,amount,bindid);
            }
        }
    }
}
