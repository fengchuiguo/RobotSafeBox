package com.robotsafebox.framework.interceptor.web;


import com.alibaba.fastjson.JSONObject;
import com.robotsafebox.base.json.JsonResult;
import com.robotsafebox.framework.properties.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.DigestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class JsonApiInterceptor extends HandlerInterceptorAdapter {

//    private final Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * 在业务处理器处理请求之前被调用
     * 如果返回false
     * 从当前的拦截器往回执行所有拦截器的afterCompletion(),再退出拦截器链
     * 如果返回true
     * 执行下一个拦截器,直到所有的拦截器都执行完毕
     * 再执行被拦截的Controller
     * 然后进入拦截器链,
     * 从最后一个拦截器往回执行所有的postHandle()
     * 接着再从最后一个拦截器往回执行所有的afterCompletion()
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        log.info("==============执行顺序: 1、preHandle================");
//        log.info(DigestUtils.md5DigestAsHex(Constant.API_CHECK_TOKEN.getBytes()));
        String apicheck = request.getParameter(Constant.API_CHECK);

        Boolean noPermission = false;
        if (apicheck == null) {
            noPermission = true;
        } else {
            if (!apicheck.equals(DigestUtils.md5DigestAsHex(Constant.API_CHECK_TOKEN.getBytes()))) {
                noPermission = true;
            }
        }

        if (noPermission) {
            response.setCharacterEncoding(Constant.UTF_8);
            response.setContentType(Constant.CONTENT_TYPE_JSON);
            JsonResult jsonResult = new JsonResult();
            jsonResult.setMessage(Constant.NO_PERMISSION_MESSAGE);
            jsonResult.setStateNoPermission();
            response.getWriter().append(JSONObject.toJSONString(jsonResult).toString());
            return false;
        }

        return true;
    }


    /**
     * 在业务处理器处理请求执行完成后,生成视图之前执行的动作
     * 可在modelAndView中加入数据，比如当前时间
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

//        log.info("==============执行顺序: 2、postHandle================");

//        if(modelAndView != null){
//            modelAndView.addObject("var", "测试postHandle");
//        }

//        super.postHandle(request, response, handler, modelAndView);
    }

    /**
     * 在DispatcherServlet完全处理完请求后被调用,可用于清理资源等
     * 当有拦截器抛出异常时,会从当前拦截器往回执行所有的拦截器的afterCompletion()
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
//        log.info("==============执行顺序: 3、afterCompletion================");

//        super.afterCompletion(request, response, handler, ex);
    }


}
