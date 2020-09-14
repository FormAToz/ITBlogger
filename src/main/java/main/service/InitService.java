package main.service;

import main.api.response.InitResponse;
import org.springframework.stereotype.Service;

@Service
public class InitService {

    /**
     * Метод возвращает общую информацию о блоге: название блога и подзаголовок для размещения в хэдере сайта,
     * а также номер телефона, e-mail и информацию об авторских правах для размещения в футере.
     */
    public InitResponse init() {
        return new InitResponse()
                .title("IT-Blogger")
                .subtitle("Рассказы разработчиков")
                .phone("+7 999 777-77-77")
                .email("7.danilov@gmail.com")
                .copyright("Андрей Данилов")
                .copyrightFrom("2020");
    }
}