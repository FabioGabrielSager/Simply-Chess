package com.fs.matchapi.entities;

import com.fs.matchapi.model.pieces.common.PieceColor;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "pieces")
@Getter @Setter
@Generated
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "piece_type", discriminatorType = DiscriminatorType.STRING)
public class PieceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    protected int x;
    protected int y;

    protected Character type;

    @Enumerated(EnumType.STRING)
    protected PieceColor color;

    @Column(name = "is_alive")
    protected boolean isAlive;
}
