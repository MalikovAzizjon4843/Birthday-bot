package org.birthdaybot.services;
import org.birthdaybot.entity.Group;
import org.birthdaybot.repository.GroupRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

/**
 * @author Malikov Azizjon    birthday-bot       25.02.2025       12:12
 */

@Service
public class GroupService {
    @Autowired
    private GroupRepository groupRepository;

    public Optional<Group> findById(Long groupId) {
        return groupRepository.findById(groupId);
    }

    public Group saveGroup(Group group) {
        return groupRepository.save(group);
    }

    public List<Group> findAllGroups() {
        return groupRepository.findAll();
    }

    public void deleteGroup(Long groupId) {
        groupRepository.findById(groupId)
                .ifPresent(groupRepository::delete);
    }
}
