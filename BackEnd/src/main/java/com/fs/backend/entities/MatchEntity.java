package com.fs.backend.entities;

import com.fs.backend.domain.MatchStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "matches")
@Getter @Setter
public class MatchEntity {
    @Id
    private String id;

    @Enumerated(EnumType.STRING)
    private MatchStatus status;

    @Column(name = "is_white_turn")
    private boolean isWhiteTurn;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.MERGE})
    @JoinColumn(name = "white_player")
    private PlayerEntity whitePlayer;
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.MERGE})
    @JoinColumn(name = "black_player")
    private PlayerEntity blackPlayer;
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.MERGE})
    private PlayerEntity winner;

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.MERGE})
    private List<PieceEntity> whitePieces;
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.MERGE})
    private List<PieceEntity> blackPieces;

    private LocalDateTime createdAt;
}
