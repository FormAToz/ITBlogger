package main.converter;

import main.model.enums.SortMode;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Класс конвертации String в тип перечисления SortMode
 */
@Component
public class StringToSortModeConverter implements Converter<String, SortMode> {
    @Override
    public SortMode convert(String s) {
        return SortMode.valueOf(s.toUpperCase());
    }
}
