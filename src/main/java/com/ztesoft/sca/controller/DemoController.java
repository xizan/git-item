package com.ztesoft.sca.controller;

import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * 测试spring boot 配置jsp页面
 * @author zxz
 *
 */
@Controller
public class DemoController {
    //从application.properties中读取配置，如取不到默认值为Hello World!
    @Value("${application.hello:Hello World!}")
    private String hello = "Hello World!";


    /**
     * 默认页
     */
    @RequestMapping(value = {"/","/index"})
    public String index(Map<String, Object> model){
        model.put("time", new Date());
        model.put("message", this.hello);
        return "index";     
    }

    /**
     * 视图绑定
     * @return
     */
    @RequestMapping("/page1")
    public ModelAndView page1(){
        ModelAndView mav = new ModelAndView("page1");
        mav.addObject("content", "1：通过返回ModelAndView对象，将属性值传递给jsp页面");
        return mav;     
    }

    /**
     * 接收Model对象
     * 设置属性值
     * 返回String字符串（页面的路径）
     * @param model
     * @return
     */
    @RequestMapping("/page2")
    public String page2(Model model){
        model.addAttribute("content", "2：通过接收Model对象，设置属性的方式传递给jsp页面");
        return "page1"; 
    }
}