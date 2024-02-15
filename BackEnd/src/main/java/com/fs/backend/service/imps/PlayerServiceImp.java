package com.fs.backend.service.imps;

import com.fs.backend.entities.PlayerEntity;
import com.fs.backend.model.Player;
import com.fs.backend.repositories.PlayerRepository;
import com.fs.backend.service.PlayerService;
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
    public Player addPlayer(Player player) {
        PlayerEntity playerEntity = playerRepository.save(modelMapper.map(player, PlayerEntity.class));
        return modelMapper.map(playerEntity, Player.class);
    }

    @Override
    public void deletePlayer(UUID playerId) {
        Optional<PlayerEntity> playerEntityOptional = playerRepository.findById(playerId);

        if(playerEntityOptional.isEmpty()) {
            throw new EntityNotFoundException(String.format("Player with ID: %s not founded", playerId));
        }

        playerRepository.delete(playerEntityOptional.get());
    }
}
