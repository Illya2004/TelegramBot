package org.kolis1on.telegrambot.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.kolis1on.telegrambot.entity.Invitees;
import org.kolis1on.telegrambot.entity.User;
import org.kolis1on.telegrambot.repository.InviteesRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;

@Service
@Data
@AllArgsConstructor
public class InviteesService {

    private final InviteesRepository inviteesRepository;
    private final UserService userService;

    public void save(String username, String link){
        Invitees invitees = Invitees.builder()
                .username(username)
                .users(Arrays.asList(userService.findByLink(link)))
                .build();

        inviteesRepository.save(invitees);
    }

    public boolean ifExists(String username){
        return inviteesRepository.existsByUsername(username);
    }

    public User getUserIdByInvitees(String username){
        return inviteesRepository.findByUsername(username).getUsers().get(0);
    }

    public void removeByUsername(String username){
        Invitees invitees = inviteesRepository.findByUsername(username);
        inviteesRepository.delete(invitees);

    }



}
