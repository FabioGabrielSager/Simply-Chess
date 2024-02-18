package com.fs.playerapi.service.imps;


import com.fs.playerapi.dtos.PlayerDto;
import com.fs.playerapi.entities.PlayerEntity;
import com.fs.playerapi.repositories.PlayerRepository;
import com.fs.playerapi.service.PlayerService;
import jakarta.persistence.EntityNotFoundException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PlayerServiceImp implements PlayerService {
    @NonNull
    private PlayerRepository playerRepository;
    @NonNull
    private ModelMapper modelMapper;

    @Override
    public PlayerDto addPlayer(String playerName) {
        PlayerEntity playerEntity = playerRepository.save(modelMapper.map(PlayerDto.builder().name(playerName).build(),
                PlayerEntity.class));
        return modelMapper.map(playerEntity, PlayerDto.class);
    }

    @Override
    public void deletePlayer(UUID playerId) {
        Optional<PlayerEntity> playerEntityOptional = playerRepository.findById(playerId);

        if(playerEntityOptional.isEmpty()) {
            throw new EntityNotFoundException(String.format("Player with ID: %s not founded", playerId));
        }

        playerRepository.delete(playerEntityOptional.get());
    }

    @Override
    public boolean playerExists(PlayerDto playerDto) {
        Optional<PlayerEntity> playerEntityOptional = playerRepository
                .findByIdAndName(playerDto.getId(), playerDto.getName());

        return playerEntityOptional.isPresent();
    }
}
