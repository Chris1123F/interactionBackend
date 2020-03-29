package com.interaction.gesture.Controller;

import com.alibaba.fastjson.JSON;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController("/api/point")
public class PointController {

    @RequestMapping("/addPoint")
    public String addPoint(@RequestParam String projectName) {
        return JSON.toJSONString("result");
    }


    @RequestMapping("/openProject")
    public String openProject(@RequestParam String projectName) {
        return JSON.toJSONString("result");
    }
}