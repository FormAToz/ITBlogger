package main.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DefaultController {
    /**
     * Класс для обычных запросов не через API (главная страница - /, в частности)
     * */
    @GetMapping(value = "/")
    public String index() {
        return "index";
    }
}
