package com.fs.backend;

import com.fs.backend.controller.MatchController;
import com.fs.backend.service.MatchService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class SmokeTest {
    @Autowired
    private MatchService matchService;

    @Autowired
    private MatchController matchController;

    @Test
    public void contextLoads() {
        assertThat(matchService).isNotNull();
        assertThat(matchController).isNotNull();
    }
}
