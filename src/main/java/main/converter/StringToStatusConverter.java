package main.converter;

import main.model.enums.Status;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Класс конвертации String в тип перечисления Status
 */
@Component
public class StringToStatusConverter implements Converter<String, Status> {
    @Override
    public Status convert(String s) {
        return Status.valueOf(s.toUpperCase());
    }
}
