package com.liepin.controller;

import com.liepin.grace.result.GraceJSONResult;
import com.liepin.grace.result.IMOOCJSONResult;
import com.liepin.grace.result.ResponseStatusEnum;
import com.liepin.pojo.test.Stu;
import com.liepin.service.StuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("u")
@Slf4j
public class HelloController {

    @Autowired
    private StuService stuService;

    @GetMapping("stu")
    public Object stu() {

        Stu stu = new Stu();
//        stu.setId("1001");
        stu.setAge(18);
        stu.setName("慕课网 www.imooc.com");

        stuService.save(stu);

        return "OK";
    }

    @GetMapping("hello")
    public Object hello() {

        Stu stu = new Stu(1001, "imooc", 18);

//        System.out.println(stu.toString());
        log.info("info：" + stu.toString());
        log.debug("debug：" + stu.toString());
        log.warn("warn：" + stu.toString());
        log.error("error：{}", stu.toString());

        return "Hello UserService~~~";
    }

    @GetMapping("hello2")
    public IMOOCJSONResult hello2() {
        Stu stu = new Stu(1001, "imooc", 18);

        return IMOOCJSONResult.ok(stu);
//        return IMOOCJSONResult.ok("添加成功！");
//        return IMOOCJSONResult.errorMsg("修改出错，请联系管理员");
//        return IMOOCJSONResult.errorMap(map)
//        return IMOOCJSONResult.errorUserTicket("用户会话校验失败！");
    }

    @GetMapping("hello3")
    public GraceJSONResult hello3() {
        Stu stu = new Stu(1001, "imooc", 18);

//        return GraceJSONResult.ok(stu);
//        return IMOOCJSONResult.ok("添加成功！");
//        return IMOOCJSONResult.errorMsg("修改出错，请联系管理员");
//        return IMOOCJSONResult.errorMap(map)
        return GraceJSONResult.errorCustom(ResponseStatusEnum.SYSTEM_IO);
    }
}
