package com.robotsafebox.web.demo1;

import com.robotsafebox.base.json.JsonResult;
import com.robotsafebox.dto.demo1.Exposer;
import com.robotsafebox.dto.demo1.SeckillExecution;
import com.robotsafebox.dto.demo1.SeckillResult;
import com.robotsafebox.entity.demo1.Seckill;
import com.robotsafebox.enums.demo1.SeckillStateEnums;
import com.robotsafebox.exception.demo1.RepeatKillException;
import com.robotsafebox.exception.demo1.SeckillCloseException;
import com.robotsafebox.framework.model.Pager;
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
@RequestMapping("/seckill")  // url:  /模块/资源/{id}细分
public class SeckillController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SeckillService seckillService;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public String list(Model model) {
        //获取列表页 方法一
//        List<Seckill> list = seckillService.getSeckillList();
//        model.addAttribute("list", list);


        //获取列表页 方法二，测试分页拦截器
        Pager pager = new Pager();
        System.out.println(pager.toString());
        pager.setPageSize(5);
        List<Seckill> list = seckillService.getSeckillListByPager(pager);
        pager.setResults(list);
        System.out.println(pager.toString());
        model.addAttribute("list", list);


        //list.jsp + model = ModelAndView
        return "demo1/list";  /*   /WEB-INF/jsp/"list".jsp  */
    }

    @RequestMapping(value = "/{seckillId}/detail", method = RequestMethod.GET)
    public String detail(@PathVariable("seckillId") int seckillId, Model model) {
        if (String.valueOf(seckillId) == null) {   // int --> String
            return "redirect:/seckill/demo1/list";
        }
        Seckill seckill = seckillService.getById(seckillId);
        if (seckill == null) {
            return "forward:/seckill/demo1/list";
        }
        model.addAttribute("seckill", seckill);
        return "demo1/detail";
    }

    //ajax json
    @RequestMapping(value = "/{seckillId}/exposer",
            method = RequestMethod.POST,
            produces = {"application/json;charset=UTF-8"})
    @ResponseBody  //SpringMVC会将SeckillResult<Exposer>封装为json
    public SeckillResult<Exposer> exposer(@PathVariable("seckillId") int seckillId) {

        SeckillResult<Exposer> result;

        try {
            Exposer exposer = seckillService.exportSeckillUrl(seckillId);
            result = new SeckillResult<Exposer>(true, exposer);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result = new SeckillResult<Exposer>(false, e.getMessage());
        }

        return result;
    }

    //dto是web层跟service层的数据传递
    @RequestMapping(value = "/{seckillId}/{md5}/execution",
            method = RequestMethod.POST,
            produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public SeckillResult<SeckillExecution> execute(@PathVariable("seckillId") int seckillId,
                                                   @CookieValue(value = "killPhone", required = false) String phone,
                                                   @PathVariable("md5") String md5) {
        if (phone == null) {
            return new SeckillResult<SeckillExecution>(false, "未注册");
        }

        SeckillResult<SeckillExecution> result;

        try {
            SeckillExecution seckillExecution = seckillService.executeSeckill(seckillId, phone, md5);
            return new SeckillResult<SeckillExecution>(true, seckillExecution);
        } catch (RepeatKillException e) {
            //捕获executeSeckill函数抛出的重复秒杀异常，并返回异常数据类型到前端
            SeckillExecution seckillExecution = new SeckillExecution(seckillId, SeckillStateEnums.REPEAT_KILL);
            return new SeckillResult<SeckillExecution>(true, seckillExecution);
        } catch (SeckillCloseException e) {
            SeckillExecution seckillExecution = new SeckillExecution(seckillId, SeckillStateEnums.END);
            return new SeckillResult<SeckillExecution>(true, seckillExecution);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            SeckillExecution seckillExecution = new SeckillExecution(seckillId, SeckillStateEnums.INNER_ERROR);
            return new SeckillResult<SeckillExecution>(true, seckillExecution);
        }

    }

    @RequestMapping(value = "/time/now", method = RequestMethod.GET)
    @ResponseBody
    public SeckillResult<Long> time() {
        Date now = new Date();
        return new SeckillResult(true, now.getTime());
    }

    @RequestMapping(value = "/jsontime/now", method = RequestMethod.GET)
    @ResponseBody
    public JsonResult jtime() {
        Date now = new Date();
        JsonResult jsonResult = new JsonResult();
        jsonResult.setData(now);
        jsonResult.setMessage("获取数据成功！");
        jsonResult.setStateSuccess();
        return jsonResult;
    }

    @RequestMapping(value = "/JsonResult", method = RequestMethod.GET)
    @ResponseBody
    public JsonResult JsonList(Model model) {
        Pager pager = new Pager();
        pager.setPageSize(5);
        List<Seckill> list = seckillService.getSeckillListByPager(pager);
        pager.setResults(list);
        JsonResult jsonResult = new JsonResult();
        jsonResult.setData(pager);
        jsonResult.setMessage("获取数据成功！");
        jsonResult.setStateSuccess();
        return jsonResult;
    }

}