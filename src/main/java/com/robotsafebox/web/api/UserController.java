package com.robotsafebox.web.api;

import com.robotsafebox.base.json.JsonResult;
import com.robotsafebox.dto.demo1.Exposer;
import com.robotsafebox.dto.demo1.SeckillExecution;
import com.robotsafebox.dto.demo1.SeckillResult;
import com.robotsafebox.entity.demo1.Seckill;
import com.robotsafebox.enums.demo1.SeckillStateEnums;
import com.robotsafebox.exception.demo1.RepeatKillException;
import com.robotsafebox.exception.demo1.SeckillCloseException;
import com.robotsafebox.framework.model.Pager;
import com.robotsafebox.framework.properties.Constant;
import com.robotsafebox.framework.sms.ihuyi.SmsSendUtils;
import com.robotsafebox.service.demo1.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/api/v1")  // url:  /模块/资源/{id}细分
public class UserController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SeckillService seckillService;


    @RequestMapping(value = "/demo", method = RequestMethod.GET, produces = {Constant.CONTENT_TYPE_JSON})
    @ResponseBody
    public JsonResult demo() {
        JsonResult jsonResult = new JsonResult();
        try {
            Pager pager = new Pager();
            pager.setPageSize(5);
            List<Seckill> list = seckillService.getSeckillListByPager(pager);
            pager.setResults(list);
            jsonResult.setData(pager);
            jsonResult.setMessage("获取数据成功！");
            jsonResult.setStateSuccess();
        } catch (Exception ex) {
            ex.printStackTrace();
            jsonResult.setMessage(Constant.EXCEPTION_MESSAGE);
        }
        return jsonResult;
    }

    @RequestMapping(value = "/demo/sendSms", method = RequestMethod.GET, produces = {Constant.CONTENT_TYPE_JSON})
    @ResponseBody
    public JsonResult sendSms() {
        JsonResult jsonResult = new JsonResult();
        try {
            String s = SmsSendUtils.sendSms("phonexxx", "codexxx", "");
            jsonResult.setData(s);
            jsonResult.setMessage("发送短信成功！");
            jsonResult.setStateSuccess();
        } catch (Exception ex) {
            ex.printStackTrace();
            jsonResult.setMessage(Constant.EXCEPTION_MESSAGE);
        }
        return jsonResult;
    }

}