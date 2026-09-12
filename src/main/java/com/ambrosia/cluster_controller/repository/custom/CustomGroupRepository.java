package com.ambrosia.cluster_controller.repository.custom;

import java.util.List;

import com.ambrosia.cluster_controller.model.DTO.filters.GroupFilter;
import com.ambrosia.cluster_controller.model.entity.Group;

public interface CustomGroupRepository {
    List<Group> search(GroupFilter groupFilter);
}
