package main.service;

import main.api.response.tag.TagListResponse;
import main.api.response.tag.TagResponse;
import main.model.Post;
import main.model.Tag;
import main.repository.Tag2PostRepository;
import main.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TagService {
    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private Tag2PostRepository tag2PostRepository;

    /**
     * Получение списка всех тэгов
     * Метод выдаёт список тэгов. В параметре weight должен быть указан
     * относительный нормированный вес тэга от 0 до 1, соответствующий частоте его встречаемости.
     * Значение 1 означает, что этот тэг встречается чаще всего.
     * weight вычисляется по формуле: кол-во вхождений тэга / количество вхождений самого популярного тэга
     */
    public TagListResponse tags() {
        // Список отсортирован по убыванию популярности тэга
        List<Object[]> tagCountList = tag2PostRepository.allTagsCount();
        List<TagResponse> tags = new ArrayList<>();

        if (tagCountList.isEmpty()) {
            return new TagListResponse(new ArrayList<>());
        }

        // Первая запись - самый популярный тэг
        Object[] first = tagCountList.get(0);
        long popularTagCount = (long) first[1];

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

    /**
     * Проверяем тэги на наличие в базе и перекладываем в проверенный список.
     * Если тэг есть в базе, берем его оттуда, иначе перекладываем из непроверенного списка.
     */
    public List<Tag> checkDuplicatesInRepo(List<Tag> uncheckedList) {
        List<Tag> checkedList = new ArrayList<>();

        for (Tag tag : uncheckedList) {
            Optional<Tag> tmpTag = tagRepository.findByName(tag.getName());

            if (tmpTag.isPresent()) {
                checkedList.add(tmpTag.get());
            }else {
                checkedList.add(tag);
            }
        }
        return checkedList;
    }

    public List<String> migrateToListTagName(Post post) {
        return post.getTags()
                .stream()
                .map(Tag::getName)
                .collect(Collectors.toList());
    }
}