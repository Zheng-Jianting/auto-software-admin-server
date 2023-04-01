package com.zhengjianting.autosoftware.repository;

import com.zhengjianting.autosoftware.entity.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends MongoRepository<Project, String> {
    Page<Project> findByProjectNameLikeIgnoreCase(String projectName, Pageable pageable);
}
