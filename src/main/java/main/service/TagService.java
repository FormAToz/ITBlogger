package main.service;

import main.api.response.TagListResponse;
import main.api.response.TagResponse;
import main.model.Tag;
import main.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class TagService {
    @Autowired
    private TagRepository tagRepository;

    /**
     * Получение списка тэгов - GET /api/tag/
     * Метод выдаёт список тэгов, начинающихся на строку, заданную в параметре query.
     * В случае, если она не задана, выводятся все тэги. В параметре weight должен быть указан
     * относительный нормированный вес тэга от 0 до 1, соответствующий частоте его встречаемости.
     * Значение 1 означает, что этот тэг встречается чаще всего.
     */
    public TagListResponse tags() {
        List<TagResponse> tags = Arrays.asList(
                new TagResponse("PHP", 1),
                new TagResponse("Python", 0.33f));
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
}
