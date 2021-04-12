package com.xonro.project.task.event;

import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.ExcelTransformListener;
import com.actionsoft.bpms.bpmn.engine.listener.ListenerConst;
import com.actionsoft.bpms.server.UserContext;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.util.Iterator;

public class GridExcelTransform extends ExcelTransformListener {
    @Override
    public Workbook fixExcel(ProcessExecutionContext ctx, Workbook wb) {
        //参数获取
        //注意：除特殊说明外，下列参数仅在该事件中场景有效
        UserContext userContext=ctx.getUserContext();// 用户上下文对象
        // 时间点的常量见上表
        String timeState = ctx.getParameterOfString( ListenerConst.FORM_EVENT_PARAM_EXCEL_TIMESTATE);// 通过该值判断当前事件所处的时间点
        // 判断方式
        if (ListenerConst.FORM_EXCEL_TIMESTATE_IMPORT_BEFORE.equals(timeState)) {
            //wb对象可以构造为HSSFWorkbook或者SXSSFWorkbook
            if (wb instanceof HSSFWorkbook) {
                Sheet sheet=wb.getSheetAt( 0 );
                int allNum=sheet.getLastRowNum();
                //获取标题行
                Row titleRow = sheet.getRow(3);
                //获取子表所有列的迭代器
                Iterator<Cell> cellIterator = titleRow.cellIterator();
                while (cellIterator.hasNext()){
                    Cell titleCell=cellIterator.next();
                    System.out.println( "--------------a" );
                }
                for (int i=4;i<=allNum;i++){
                    Row row=sheet.getRow( i );
                    short cell=row.getLastCellNum();
                    for (int j=0;j<cell;j++){
                        Cell result=row.getCell( j );
                        System.out.println( "-------------aaaa" );
                    }
                }
                System.out.println( "aaaaa" );
                // 解析Excel（xls格式）
            }
            if (wb instanceof SXSSFWorkbook) {
                // 解析Excel（xlsx格式）
                System.out.println( "bbbbb" );
            }
            // ...
        }
        // 如果想要阻止下载或者上传的后续操作，可以return null;
        return wb;//注意，即使对该对象进行修改，上层程序也不会读取新的数据。
    }
}
