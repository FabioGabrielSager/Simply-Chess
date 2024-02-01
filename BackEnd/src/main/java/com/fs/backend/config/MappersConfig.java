package com.fs.backend.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fs.backend.domain.Match;
import com.fs.backend.domain.Player;
import com.fs.backend.domain.pieces.Bishop;
import com.fs.backend.domain.pieces.King;
import com.fs.backend.domain.pieces.Knight;
import com.fs.backend.domain.pieces.Pawn;
import com.fs.backend.domain.pieces.PieceFactory;
import com.fs.backend.domain.pieces.Queen;
import com.fs.backend.domain.pieces.Rook;
import com.fs.backend.domain.pieces.common.Pair;
import com.fs.backend.domain.pieces.common.Piece;
import com.fs.backend.entities.MatchEntity;
import com.fs.backend.entities.PieceEntity;
import org.modelmapper.AbstractConverter;
import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Configuration
public class MappersConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();

        TypeMap<Piece, PieceEntity> typeMap = mapper.createTypeMap(Piece.class, PieceEntity.class);

        mapper.addConverter(new AbstractConverter<Rook, PieceEntity>() {
            @Override
            protected PieceEntity convert(Rook source) {
                return PieceEntity.builder()
                        .id(source.getId())
                        .x(source.getPosition().getX())
                        .y(source.getPosition().getY())
                        .color(source.getColor())
                        .isAlive(source.isAlive())
                        .type('R')
                        .build();
            }
        });

        mapper.addConverter(new AbstractConverter<King, PieceEntity>() {
            @Override
            protected PieceEntity convert(King source) {
                return PieceEntity.builder()
                        .id(source.getId())
                        .x(source.getPosition().getX())
                        .y(source.getPosition().getY())
                        .color(source.getColor())
                        .isAlive(source.isAlive())
                        .type('K')
                        .build();
            }
        });

        mapper.addConverter(new AbstractConverter<Queen, PieceEntity>() {
            @Override
            protected PieceEntity convert(Queen source) {
                return PieceEntity.builder()
                        .id(source.getId())
                        .x(source.getPosition().getX())
                        .y(source.getPosition().getY())
                        .color(source.getColor())
                        .isAlive(source.isAlive())
                        .type('Q')
                        .build();
            }
        });

        mapper.addConverter(new AbstractConverter<Bishop, PieceEntity>() {
            @Override
            protected PieceEntity convert(Bishop source) {
                return PieceEntity.builder()
                        .id(source.getId())
                        .x(source.getPosition().getX())
                        .y(source.getPosition().getY())
                        .color(source.getColor())
                        .isAlive(source.isAlive())
                        .type('B')
                        .build();
            }
        });

        mapper.addConverter(new AbstractConverter<Pawn, PieceEntity>() {
            @Override
            protected PieceEntity convert(Pawn source) {
                return PieceEntity.builder()
                        .id(source.getId())
                        .x(source.getPosition().getX())
                        .y(source.getPosition().getY())
                        .color(source.getColor())
                        .isAlive(source.isAlive())
                        .type('P')
                        .build();
            }
        });

        mapper.addConverter(new AbstractConverter<Knight, PieceEntity>() {
            @Override
            protected PieceEntity convert(Knight source) {
                return PieceEntity.builder()
                        .id(source.getId())
                        .x(source.getPosition().getX())
                        .y(source.getPosition().getY())
                        .color(source.getColor())
                        .isAlive(source.isAlive())
                        .type('N')
                        .build();
            }
        });


        AbstractConverter<PieceEntity, Piece> pieceEntityPieceAbstractConverter = new
                AbstractConverter<>() {
                    @Override
                    protected Piece convert(PieceEntity source) {
                        Piece piece = PieceFactory.create(source.getType(),
                                source.getColor(),
                                new Pair(source.getX(), source.getY()));
                        piece.setId(source.getId());
                        return piece;
                    }
                };

        mapper.addConverter(pieceEntityPieceAbstractConverter);

        return mapper;
    }

    @Bean("mergerMapper")
    public ModelMapper mergerMapper(){
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());
        return mapper;
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }

}
