package com.fs.matchapi.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter @Setter
@Builder
@Generated
public class PlayerInQueueEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID queueId;

    @Column(unique = true)
    private int position;

    @OneToOne
    private PlayerEntity player;
}
