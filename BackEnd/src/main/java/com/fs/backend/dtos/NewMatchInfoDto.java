package com.fs.backend.dtos;

import com.fs.backend.domain.Player;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NewMatchInfoDto {
    private String id;
    private Player creator;
}
