package com.spring.splitwise.service;

import com.spring.splitwise.model.Group;
import com.spring.splitwise.model.Member;
import com.spring.splitwise.model.User;
import com.spring.splitwise.repository.GroupRepository;
import com.spring.splitwise.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GroupService {

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private UserRepository userRepository;

    public void createNewGroup(Group group,
                               Long creatorId,
                               List<String> emails,
                               List<String> names) {

        User creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        group.setCreatedBy(creator);
        Group savedGroup = groupRepository.save(group);

        // 🔹 Creator as member
        Member admin = new Member();
        admin.setUser(creator);
        admin.setGroup(savedGroup);
        admin.setDisplayName(creator.getUsername());
        admin.setInvitedEmail(creator.getEmail());
        admin.setSTATUS("ACCEPTED");

        savedGroup.getMembers().add(admin);

        // 🔹 Other members
        for (int i = 0; i < emails.size(); i++) {

            String email = emails.get(i);
            String name = names.get(i);

            if (email == null || email.isBlank()) continue;

            Member member = new Member();
            member.setGroup(savedGroup);
            member.setDisplayName(name);
            member.setInvitedEmail(email);

            userRepository.findByEmail(email).ifPresentOrElse(
                    user -> {
                        member.setUser(user);
                        member.setSTATUS("ACCEPTED");
                    },
                    () -> member.setSTATUS("INVITED")
            );

            savedGroup.getMembers().add(member);
        }

        groupRepository.save(savedGroup);
    }

    public List<Group> getGroupsForUser(Long userId) {
        return groupRepository.findGroupsByUserId(userId);
    }

    public Group findById(Long id){
        Group group = groupRepository.findById(id).get();

        return group;
    }
}
