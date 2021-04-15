package com.yangzinan.dev.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

/**
 * 热部署控制类
 * @author yangzinan
 * @date 2021/04/15
 */
@RestController
public class DevController {

    @GetMapping("/dev")
    public HashMap<String,Object> dev(){
        Integer code = 200;
        String message = "fail";
        String data = this.getClass().getClassLoader().toString();

        HashMap<String,Object> result = new HashMap<String,Object>();
        result.put("code",code);
        result.put("message",message);
        result.put("data",data);

        return result;
    }

}
