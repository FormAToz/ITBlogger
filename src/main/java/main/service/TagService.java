package main.service;

import main.api.response.TagListResponse;
import main.api.response.TagResponse;
import main.model.Tag;
import main.repository.PostRepository;
import main.repository.Tag2PostRepository;
import main.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TagService {
    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    Tag2PostRepository tag2PostRepository;

    /**
     * Получение списка тэгов - GET /api/tag/
     * Метод выдаёт список тэгов. В параметре weight должен быть указан
     * относительный нормированный вес тэга от 0 до 1, соответствующий частоте его встречаемости.
     * Значение 1 означает, что этот тэг встречается чаще всего.
     * weight вычисляется по формуле: кол-во вхождений тэга / количество вхождений самого популярного тэга
     */
    public TagListResponse tags() {
        // Список отсортирован по убыванию популярности тэга
        List<Object[]> tagCountList = tag2PostRepository.allTagsCount();
        List<TagResponse> tags = new ArrayList<>();
        // Первая запись - самый популярный тэг
        Object[] first = tagCountList.get(0);
        long popularTagCount = (long) first[1];
        // Заполняем лист
        tagCountList.forEach(row -> {
            String tagName = (String) row[0];
            long tagCount = (long) row[1];
            // Вычисляем вес (с проверкой исключения деления на ноль)
            if (popularTagCount != 0) {
                float weight = new BigDecimal((float) tagCount / popularTagCount)
                        .setScale(2, RoundingMode.UP).floatValue();
                tags.add(new TagResponse(tagName, weight));
            }
        });
        return new TagListResponse(tags);
    }

    // Проверяем тэги на наличие в базе. При отсутствии тэга в базе - добавляем его в базу.
    public List<Tag> checkDuplicatesInRepo(List<Tag> uncheckedList) {
        List<Tag> checkedList = new ArrayList<>();
        for (Tag tag : uncheckedList) {
            Optional<Tag> tmpTag = tagRepository.findByName(tag.getName());
            // Если тэг есть в базе, берем его оттуда
            if (tmpTag.isPresent()) {
                checkedList.add(tmpTag.get());
                // иначе складем тэг в базу
            }else {
                checkedList.add(tag);
            }
        }
        return checkedList;
    }

    // Проверяем есть ли тэг в списке тэгов
    public boolean tagExistInList(String tagName, List<Tag> tagList) {
        System.out.println(tagName);
        if (tagList == null) {
            return false;
        }
        for (Tag t : tagList) {
            System.out.println(t.getName().compareToIgnoreCase(tagName) == 0);
            return t.getName().compareToIgnoreCase(tagName) == 0;
        }
        return false;
    }
}
