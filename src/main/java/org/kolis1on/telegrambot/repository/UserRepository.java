package org.kolis1on.telegrambot.repository;

import org.kolis1on.telegrambot.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByUsername(String username);
    boolean existsByInvitationLink(String link);

    User findByInvitationLink(String link);
    User findByUsername(String username);
}
