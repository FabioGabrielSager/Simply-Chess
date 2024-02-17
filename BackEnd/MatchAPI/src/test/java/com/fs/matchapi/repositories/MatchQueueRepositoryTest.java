package com.fs.matchapi.repositories;

import com.fs.matchapi.entities.PlayerInQueueEntity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@DataJpaTest
public class MatchQueueRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private MatchQueueRepository matchQueueRepository;

    @Test
    public void getFirstPlayerTest() {
        entityManager.persist(PlayerInQueueEntity.builder().position(1).build());
        entityManager.persist(PlayerInQueueEntity.builder().position(2).build());
        entityManager.flush();

        Optional<PlayerInQueueEntity> result = matchQueueRepository.getFirstPlayer();

        assertNotNull(result.get());
        assertEquals(1, result.get().getPosition());
    }

    @Test
    public void getLastPositionTest() {
        entityManager.persist(PlayerInQueueEntity.builder().position(1).build());
        entityManager.persist(PlayerInQueueEntity.builder().position(2).build());
        entityManager.flush();

        Optional<Integer> result = matchQueueRepository.getLastPosition();

        assertNotNull(result.get());
        assertEquals((Integer) 2, result.get());
    }

    @Test
    public void getLastPositionTest2() {

        Optional<Integer> result = matchQueueRepository.getLastPosition();

        assertFalse(result.isPresent());
    }


    @Test
    public void findAllOrderByPositionTest() {
        entityManager.persist(PlayerInQueueEntity.builder().position(1).build());
        entityManager.persist(PlayerInQueueEntity.builder().position(2).build());
        entityManager.flush();

        List<PlayerInQueueEntity> result = matchQueueRepository.findAllOrderByPosition();

        assertEquals(2, result.size());
        assertEquals(1, result.get(0).getPosition());
        assertEquals(2, result.get(1).getPosition());
    }
}
