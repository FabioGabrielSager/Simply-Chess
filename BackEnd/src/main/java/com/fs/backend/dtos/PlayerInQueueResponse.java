package com.fs.backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Generated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Generated
public class PlayerInQueueResponse {
    private UUID queueId;
    private int position;
}
