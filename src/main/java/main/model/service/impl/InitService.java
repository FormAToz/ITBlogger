package main.model.service.impl;

import main.model.service.JSONService;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * Общие данные блога - GET /api/init/
 *
 * Возвращение общей информации о блоге: название блога и подзаголовок для размещения в хэдере сайта,
 * а также номер телефона, e-mail и информацию об авторских правах для размещения в футере.
 *
 * Авторизация: не требуется
 *
 * Формат ответа:
 *
 * {
 * 	"title": "DevPub",
 * 	"subtitle": "Рассказы разработчиков",
 * 	"phone": "+7 903 666-44-55",
 * 	"email": "mail@mail.ru",
 * 	"copyright": "Дмитрий Сергеев",
 * 	"copyrightFrom": "2005"
 * }
 *
 */
@Service
public class InitService implements JSONService {
    private String title;
    private String subtitle;
    private String phone;
    private String email;
    private String copyright;
    private String copyrightfrom;

    @PostConstruct
    void init() {
        title = "IT-Blogger";
        subtitle =  "Рассказы разработчиков";
        phone = "+7 999 777-77-77";
        email = "7.danilov@gmail.com";
        copyright = "Андрей Данилов";
        copyrightfrom = "2020";
    }

    @Override
    public JSONObject toJSON() {
        JSONObject jObj = new JSONObject();
        jObj.put("title", title);
        jObj.put("subtitle", subtitle);
        jObj.put("phone", phone);
        jObj.put("email", email);
        jObj.put("copyright", copyright);
        jObj.put("copyrightFrom", copyrightfrom);
        return jObj;
    }
}
