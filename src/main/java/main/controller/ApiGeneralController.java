package main.controller;

import main.model.service.impl.InitService;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *  Класс для прочих запросов к API
 * */
@RestController
@RequestMapping("api")
public class ApiGeneralController {

    @Autowired
    InitService initService;

    @GetMapping("init")
    public JSONObject init() {
        return initService.toJSON();
    }

    @GetMapping("settings")
    public JSONObject settings() {
        JSONObject jObj = new JSONObject();
        jObj.put("MULTIUSER_MODE", false);
        jObj.put("POST_PREMODERATION", true);
        jObj.put("STATISTICS_IS_PUBLIC", null);
        return jObj;
    }

    @GetMapping("tag")
    public JSONObject tags() {
        JSONObject jObj = new JSONObject();
        JSONArray jArr = new JSONArray();
        JSONObject jTag1 = new JSONObject();
        JSONObject jTag2 = new JSONObject();
        jTag1.put("name", "PHP");
        jTag1.put("weight", 1);
        jTag2.put("name", "Python");
        jTag2.put("weight", 0.33);
        jArr.add(jTag1);
        jArr.add(jTag2);
        jObj.put("tags", jArr);
        return jObj;
    }
}
