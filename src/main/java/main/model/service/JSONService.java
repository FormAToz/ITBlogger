package main.model.service;

import org.json.simple.JSONObject;

/**
 *  Интерфейс для реализации объекта в JSON-объект
 */
public interface JSONService {
    JSONObject toJSON();
}
