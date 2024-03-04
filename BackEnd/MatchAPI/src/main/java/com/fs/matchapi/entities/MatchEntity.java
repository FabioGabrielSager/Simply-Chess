package com.fs.matchapi.entities;

import com.fs.matchapi.model.MatchStatus;
import com.fs.matchapi.model.pieces.common.PieceColor;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Generated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "matches")
@Getter @Setter
@Generated
public class MatchEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    private MatchStatus status;

    @Column(name = "is_white_turn")
    private boolean isWhiteTurn;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "white_player")
    private PlayerEntity whitePlayer;
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "black_player")
    private PlayerEntity blackPlayer;
    @Enumerated(EnumType.STRING)
    private PieceColor winner;

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.EAGER)
    private List<PieceEntity> whitePieces;
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.EAGER)
    private List<PieceEntity> blackPieces;

    private LocalDateTime createdAt;
}
