package org.launchcode.hello_spring.data;

import org.launchcode.hello_spring.models.Tag;
import org.springframework.data.repository.CrudRepository;

public interface TagRepository extends CrudRepository<Tag, Integer> {
}
