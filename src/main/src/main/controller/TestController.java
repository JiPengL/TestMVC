package main.controller;

import main.annotation.Autowired;
import main.annotation.Controller;
import main.annotation.RequestMapping;
import main.service.TestService;

/**
 * @author liujipeng
 * @date 2020/9/4 10:43
 * @mail xuxiejp@163.com
 * @desc ...
 */
@Controller
@RequestMapping("/v1")
public class TestController {

    @Autowired
    private TestService testService;

    @RequestMapping("/teMethod")
    public void teMethod(){
        testService.say();
    }

    @RequestMapping("")
    public void index(){
        System.out.println("TestController.index");
    }
}
