package org.kolis1on.telegrambot.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.checkerframework.checker.units.qual.N;
import org.kolis1on.telegrambot.entity.User;
import org.kolis1on.telegrambot.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
@Data
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public void addUser(String username, String invitationLink) {
        User user = User.builder()
                .username(username)
                .invitationLink(invitationLink)
                .channelFollow(false)
                .chatFollow(false)
                .ifParticipate(false)
                .peopleJoined(0l)
                .build();

        userRepository.save(user);
    }

    public boolean ifUserExist(String username) {
        return userRepository.existsByUsername(username);
    }

    public User findByLink(String link) {
        return userRepository.findByInvitationLink(link);
    }


    public void incrementPeople(String link) {
        User user = userRepository.findByInvitationLink(link);
        user.incrementPeopleJoined();

        userRepository.save(user);

    }
    public void save(User user){
        userRepository.save(user);
    }

    public User findByUsername(String username){
        return userRepository.findByUsername(username);
    }

    public boolean existsByLink(String link) {
        return userRepository.existsByInvitationLink(link);
    }

}
