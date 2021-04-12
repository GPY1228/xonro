package com.xonro.project.performance;

import com.actionsoft.bpms.bo.engine.BO;
import com.actionsoft.bpms.bpmn.engine.model.run.delegate.ProcessInstance;
import com.actionsoft.bpms.commons.database.RowMap;
import com.actionsoft.bpms.org.model.UserModel;
import com.actionsoft.bpms.schedule.IJob;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.sdk.local.SDK;
import dm.jdbc.util.StringUtil;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 绩效定时器
 *
 * @author zhaoqt
 */
public class performanceJob implements IJob {
    @Override
    public void execute(JobExecutionContext ctx) throws JobExecutionException {
        //获取项目经理角色组
        String projectManagers = SDK.getAppAPI().getProperty( "com.xonro.apps.xr", "projectManagers" );
        //获取技术经理角色组
        String technicalManagers = SDK.getAppAPI().getProperty( "com.xonro.apps.xr", "technicalManagers" );
        //获取工程师角色组
        String engineers = SDK.getAppAPI().getProperty( "com.xonro.apps.xr", "engineers" );

        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime( date );
        int year = calendar.get( Calendar.YEAR );
        int month = calendar.get( Calendar.MONTH ) + 1;
        String sql = "select * from orguser where userid !='admin' ";
        List<RowMap> orgUsers = DBSql.getMaps( sql );
        //根据年月周数及用户编号获取bo
        for (RowMap rowMap : orgUsers) {
            String userId = rowMap.getString( "USERID" );
            String result = SDK.getORGAPI().validateUsers( userId );
            //判断账户是否合法，若合法则返回空
            if (StringUtil.isNotEmpty( result )) {
                continue;
            }
            String userName = rowMap.getString( "USERNAME" );
            //获取人员角色
            UserModel userModel = SDK.getORGAPI().getUser( userId );
            //获取角色
            String roleId = userModel.getRoleId();
            //子表数据
            //项目工程师
            if (engineers.contains( roleId )) {
                ProcessInstance processInstance = SDK.getProcessAPI().createProcessInstance( "obj_c9a53605e1e84d29a9ae3fad9b66d369", userId, "月度绩效考核" );
                SDK.getProcessAPI().start( processInstance );
                //创建数据
                //主表数据
                BO bo = new BO();
                bo.set( "USER_NAME", userName );
                bo.set( "USER_ID", userId );
                bo.set( "APPLY_DATE", date );
                bo.set( "YEAR", year );
                bo.set( "MONTH", month );
                SDK.getBOAPI().create( "BO_XR_CHECK_MONTH", bo, processInstance, UserContext.fromUID( userId ) );
                getPerformance( processInstance, "1" );
            }
            //技术经理
            if (technicalManagers.contains( roleId )) {
                ProcessInstance processInstance = SDK.getProcessAPI().createProcessInstance( "obj_c9a53605e1e84d29a9ae3fad9b66d369", userId, "月度绩效考核" );
                SDK.getProcessAPI().start( processInstance );
                //创建数据
                //主表数据
                BO bo = new BO();
                bo.set( "USER_NAME", userName );
                bo.set( "USER_ID", userId );
                bo.set( "APPLY_DATE", date );
                bo.set( "YEAR", year );
                bo.set( "MONTH", month );
                SDK.getBOAPI().create( "BO_XR_CHECK_MONTH", bo, processInstance, UserContext.fromUID( userId ) );
                getPerformance( processInstance, "3" );

            }
            //项目经理
            if (projectManagers.contains( roleId )) {
                ProcessInstance processInstance = SDK.getProcessAPI().createProcessInstance( "obj_c9a53605e1e84d29a9ae3fad9b66d369", userId, "月度绩效考核" );
                SDK.getProcessAPI().start( processInstance );
                //创建数据
                //主表数据
                BO bo = new BO();
                bo.set( "USER_NAME", userName );
                bo.set( "USER_ID", userId );
                bo.set( "APPLY_DATE", date );
                bo.set( "YEAR", year );
                bo.set( "MONTH", month );
                SDK.getBOAPI().create( "BO_XR_CHECK_MONTH", bo, processInstance, UserContext.fromUID( userId ) );
                getPerformance( processInstance, "4" );
            }
            rulerBiz.DayRuler( userId, "6" );
            try {
                rulerBiz.DeductRuler( userId, "6" );
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    //获取指标类
    public List<BO> getPerformance(ProcessInstance processInstance, String type) {
        List<BO> list = SDK.getBOAPI().query( "BO_XR_CHECK_ITEM" ).addQuery( "ROLE_NAME=", type ).list();
        for (BO bo1 : list) {
            String dataType = bo1.getString( "DATA_TYPE" );
            //系统汇总
            BO gridBo = new BO();
            gridBo.set( "ITME_CODE", bo1.getString( "ITME_CODE" ) );
            gridBo.set( "ITEM_NAME", bo1.getString( "ITEM_NAME" ) );
            gridBo.set( "ITEM_DESC", bo1.getString( "ITEM_DESC" ) );
            gridBo.set( "SCORE", bo1.getString( "SCORE" ) );
            if ("1".equals( dataType )) {
                gridBo.set( "ACTUAL_SCORE", bo1.getString( "SCORE" ) );
            }
            SDK.getBOAPI().create( "BO_XR_CHECK_MONTH_LIST", gridBo, processInstance, UserContext.fromUID( "admin" ) );
        }
        return list;
    }
}
