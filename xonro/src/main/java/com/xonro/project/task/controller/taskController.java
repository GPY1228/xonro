package com.xonro.project.task.controller;

import com.actionsoft.bpms.commons.mvc.view.ResponseObject;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.server.bind.annotation.Controller;
import com.actionsoft.bpms.server.bind.annotation.Mapping;
import com.actionsoft.bpms.util.DBSql;
import com.xonro.project.task.biz.TaskBIz;
import dm.jdbc.util.StringUtil;

@Controller
public class taskController {
    //分配任务
    @Mapping( value = "com.xonro.apps.xr.startTask")
    public static String startTask(UserContext userContext,String boId){
        ResponseObject responseObject=null;
        try {
            responseObject=ResponseObject.newOkResponse();
            String url=TaskBIz.createTask(userContext ,boId );
            responseObject.put( "msg",url );
        }catch (Exception e){
            e.printStackTrace();
            responseObject=ResponseObject.newErrResponse();
            responseObject.put( "msg","流程分配发起出错，请联系管理员" );
        }finally {
            return responseObject.toString();
        }
    }
    //取消任务
    public static String cancelTask(UserContext userContext,String boId){
        //发起取消流程

        return null;
    }
    //变更任务
    @Mapping( value = "com.xonro.apps.xr.changeTask")
    public static String changeTask(UserContext userContext,String boId){
        ResponseObject responseObject=null;
        try {
            responseObject=ResponseObject.newOkResponse();
            String url=TaskBIz.changeTask(userContext ,boId );
            responseObject.put( "msg",url );
        }catch (Exception e){
            e.printStackTrace();
            responseObject=ResponseObject.newErrResponse();
            responseObject.put( "msg","任务变更发起出错，请联系管理员" );
        }finally {
            return responseObject.toString();
        }
    }
}
