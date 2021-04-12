package com.xonro.project.biz;

import com.actionsoft.bpms.util.DBSql;
import com.xonro.project.util.MathUtil;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class countInvoiceBiz {
    public static void updateData(String invoiceDate, String amount, String bindid) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat( "yyyyMM" );
            SimpleDateFormat sdf1 = new SimpleDateFormat( "yyyy-MM-dd" );
            Date date = sdf1.parse( invoiceDate );
            String YM = sdf.format( date );
            BigDecimal NO_TAX = MathUtil.formatKeepDigits( MathUtil.divide( new BigDecimal( amount ), new BigDecimal( Double.valueOf( 1.03 ) ) ), 2 );
            BigDecimal TAX = MathUtil.formatKeepDigits( MathUtil.multiply( NO_TAX, new BigDecimal( Double.valueOf( 0.06 ) ) ), 2 );
            String updateSql = "update BO_XR_CM_INVOICE set YM='" + YM + "',NO_TAX=" + NO_TAX + ",TAX='" + TAX + "' where bindid='" + bindid + "'";
            DBSql.update( updateSql );
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
