package com.fs.matchapi.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fs.matchapi.dtos.MatchDto;
import com.fs.matchapi.dtos.PieceResponse;
import com.fs.matchapi.entities.PieceEntity;
import com.fs.matchapi.entities.PlayerEntity;
import com.fs.matchapi.model.Match;
import com.fs.matchapi.model.pieces.Bishop;
import com.fs.matchapi.model.pieces.King;
import com.fs.matchapi.model.pieces.Knight;
import com.fs.matchapi.model.pieces.Pawn;
import com.fs.matchapi.model.pieces.PieceFactory;
import com.fs.matchapi.model.pieces.Queen;
import com.fs.matchapi.model.pieces.Rook;
import com.fs.matchapi.model.pieces.common.Pair;
import com.fs.matchapi.model.pieces.common.Piece;
import org.modelmapper.AbstractConverter;
import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

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

        mapper.addConverter(new AbstractConverter<PieceEntity, PieceResponse>() {
            @Override
            protected PieceResponse convert(PieceEntity source) {
                return PieceResponse.builder()
                        .id(source.getId())
                        .position(new Pair(source.getX(), source.getY()))
                        .color(source.getColor())
                        .isAlive(source.isAlive())
                        .type(source.getType())
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
                        piece.setAlive(source.isAlive());
                        return piece;
                    }
                };

        AbstractConverter<PlayerEntity, String> playerEntityStringAbstractConverter = new
                AbstractConverter<PlayerEntity, String>() {
                    @Override
                    protected String convert(PlayerEntity source) {
                        return Objects.isNull(source) ? null : source.getName();
                    }
                };

        mapper.addConverter(pieceEntityPieceAbstractConverter);
        mapper.addConverter(playerEntityStringAbstractConverter);

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