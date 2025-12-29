package com.spring.splitwise.service;

import com.spring.splitwise.model.Friend;
import com.spring.splitwise.model.Group;
import com.spring.splitwise.model.Member;
import com.spring.splitwise.model.User;
import com.spring.splitwise.repository.FriendRepository;
import com.spring.splitwise.repository.GroupRepository;
import com.spring.splitwise.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class GroupService {
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FriendRepository friendRepository;

    @Transactional
    public void createNewGroup(Group group, Long creatorId, List<String> emails, List<String> names) {
        User creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        group.setCreatedBy(creator);
        Group savedGroup = groupRepository.save(group);

        addMemberToGroup(savedGroup, creator, creator.getUsername(), creator.getEmail(), "ACCEPTED");
        processMembersAndFriendships(savedGroup, creator, emails, names);

        groupRepository.save(savedGroup);
    }

    @Transactional
    public void updateExistingGroup(Long groupId, String newName, List<String> names, List<String> emails) {
        Group existingGroup = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        existingGroup.setGroupName(newName);
        User creator = existingGroup.getCreatedBy();

        existingGroup.getMembers().clear();
        addMemberToGroup(existingGroup, creator, creator.getUsername(), creator.getEmail(), "ACCEPTED");
        processMembersAndFriendships(existingGroup, creator, emails, names);

        groupRepository.save(existingGroup);
    }

    private void processMembersAndFriendships(Group group, User creator, List<String> emails, List<String> names) {
        if (emails == null) return;

        for (int i = 0; i < emails.size(); i++) {
            String email = emails.get(i);
            String name = names.get(i);

            if (email == null || email.isBlank() || email.equalsIgnoreCase(creator.getEmail())) continue;

            Optional<User> userOpt = userRepository.findByEmail(email);
            User memberUser;
            String status;

            if (userOpt.isPresent()) {
                memberUser = userOpt.get();
                status = "ACCEPTED";
            } else {
                memberUser = new User();
                memberUser.setUsername(name);
                memberUser.setEmail(email);
                memberUser.setPassword("default123");
                memberUser.setActive(false);
                memberUser = userRepository.save(memberUser);
                status = "INVITED";
            }

            createFriendshipIfNotExist(creator, memberUser);
            addMemberToGroup(group, memberUser, name, email, status);
        }
    }

    private void createFriendshipIfNotExist(User user, User friend) {
        boolean exists = friendRepository.existsByUserAndFriend(user, friend);

        if (!exists && !user.getId().equals(friend.getId())) {
            Friend f1 = new Friend();
            f1.setUser(user);
            f1.setFriend(friend);
            f1.setStatus("ACCEPTED");
            friendRepository.save(f1);

            Friend f2 = new Friend();
            f2.setUser(friend);
            f2.setFriend(user);
            f2.setStatus("ACCEPTED");
            friendRepository.save(f2);
        }
    }

    private void addMemberToGroup(Group group, User user, String name, String email, String status) {
        Member member = new Member();
        member.setGroup(group);
        member.setUser(user);
        member.setDisplayName(name);
        member.setInvitedEmail(email);
        member.setStatus(status);
        group.getMembers().add(member);
    }

    public List<Group> getGroupsForUser(Long userId) {
        return groupRepository.findGroupsByUserId(userId);
    }

    public Group findById(Long id) {
        return groupRepository.findById(id).orElseThrow(() -> new RuntimeException("Group not found"));
    }

    public void deleteGroup(Long id) {
        if (groupRepository.existsById(id)) {
            groupRepository.deleteById(id);
        } else {
            throw new RuntimeException("Group not found with id: " + id);
        }
    }
}