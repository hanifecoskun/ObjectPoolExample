package coskun.hanife.objectpool;

import coskun.hanife.objectpool.model.Entity;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * @author Hanife Coskun
 */
public class EntityPoolTest {

    @Test
    public void test() {

        List<Entity> entityList = new ArrayList<>();

        // reached max pool size and borrowed all entities in the pool
        ExecutorService threadPool = Executors.newFixedThreadPool(3);
        List<CompletableFuture<List<Entity>>> list = Arrays.asList(1, 2, 3).stream()
                .map(i -> CompletableFuture.supplyAsync(() -> {

                    Entity entity = EntityPool.getInstance().borrow();
                    if (entity != null) {
                        entityList.add(entity);
                    }

                    return entityList;
                }, threadPool)).collect(Collectors.toList());

        List<Entity> outputList = list.stream().map(CompletableFuture::join)
                .flatMap(Collection::stream).collect(Collectors.toList());
        threadPool.shutdown();

        Entity fourthEntity = EntityPool.getInstance().borrow();
        Assert.assertNull(fourthEntity); // all entities are not available in the pool

        EntityPool.getInstance().release(outputList.get(0)); // released the entity
        fourthEntity = EntityPool.getInstance().borrow(); // borrowed that released entity
        Assert.assertEquals(outputList.get(0).getId(), fourthEntity.getId());
    }
}
