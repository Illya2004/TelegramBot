package org.kolis1on.telegrambot.entity;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.units.qual.N;

@Data
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String invitationLink;

    private Long peopleJoined;

    private boolean channelFollow;
    private boolean chatFollow;
    private boolean ifParticipate;

    @PostConstruct
    public void construct(){
        peopleJoined = 0l;
    }

    public void incrementPeopleJoined(){
        this.peopleJoined = this.peopleJoined + 1;
    }

    public void decrementPeopleJoined(){
        this.peopleJoined = this.peopleJoined - 1;
    }

}
