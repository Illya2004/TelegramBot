package org.kolis1on.telegrambot.repository;

import org.kolis1on.telegrambot.entity.Invitees;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InviteesRepository extends JpaRepository<Invitees, Long> {
    boolean existsByUsername(String username);

    Invitees findByUsername(String username);
}
