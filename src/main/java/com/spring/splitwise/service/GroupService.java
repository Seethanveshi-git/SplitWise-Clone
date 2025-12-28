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

        Member admin = new Member();
        admin.setUser(creator);
        admin.setGroup(savedGroup);
        admin.setDisplayName(creator.getUsername());
        admin.setInvitedEmail(creator.getEmail());
        admin.setStatus("ACCEPTED");

        savedGroup.getMembers().add(admin);

        for (int i = 0; i < emails.size(); i++) {
            String email = emails.get(i);
            String name = names.get(i);

            if (email == null || email.isBlank()) continue;

            Member member = new Member();
            member.setGroup(savedGroup);
            member.setDisplayName(name);
            member.setInvitedEmail(email);

            Optional<User> user = userRepository.findByEmail(email);
            if(user.isPresent()){
                member.setUser(user.get());
                member.setStatus("ACCEPTED");
            }
            else{
                member.setStatus("INVITED");
            }

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

    public void updateExistingGroup(Long groupId, String newName, List<String> names, List<String> emails) {
        Group existingGroup = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        existingGroup.setGroupName(newName);

        User creator = existingGroup.getCreatedBy();

        existingGroup.getMembers().clear();

        Member admin = new Member();
        admin.setUser(creator);
        admin.setGroup(existingGroup);
        admin.setDisplayName(creator.getUsername());
        admin.setInvitedEmail(creator.getEmail());
        admin.setStatus("ACCEPTED");
        existingGroup.getMembers().add(admin);
        if (emails != null) {
            for (int i = 0; i < emails.size(); i++) {
                String email = emails.get(i);
                String name = names.get(i);

                if (email == null || email.isBlank() || email.equals(creator.getEmail())) continue;

                Member member = new Member();
                member.setGroup(existingGroup);
                member.setDisplayName(name);
                member.setInvitedEmail(email);

                Optional<User> user = userRepository.findByEmail(email);
                if(user.isPresent()){
                    member.setUser(user.get());
                    member.setStatus("ACCEPTED");
                }
                else {
                    member.setStatus("INVITED");
                }

                existingGroup.getMembers().add(member);
            }
        }
        groupRepository.save(existingGroup);
    }

    public void deleteGroup(Long id) {
        if (groupRepository.existsById(id)) {
            groupRepository.deleteById(id);
        } else {
            throw new RuntimeException("Group not found with id: " + id);
        }
    }
}
