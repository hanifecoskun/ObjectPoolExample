package coskun.hanife.objectpool;

import coskun.hanife.objectpool.model.Entity;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Hanife Coskun
 */
public class EntityPool implements ObjectPool<Entity> {

    private static final int MAX_OBJECTS = 3;

    private static volatile EntityPool instance;

    private List<Entity> available = new LinkedList<>();
    private List<Entity> inUsed = new LinkedList<>();

    private EntityPool() {

    }

    public static synchronized EntityPool getInstance() {

        if (instance == null) {
            instance = new EntityPool();
        }

        return instance;
    }

    @Override
    public Entity borrow() {

        Entity entity = null;

        synchronized (this) {

            int queueSize = available.size() + inUsed.size();

            if (available.size() > 0) { // if there is element in the pool
                entity = available.remove(0);
                inUsed.add(entity);
                System.out.println("Borrowed entity-" + entity.getId() + " in the pool.");
            } else if (queueSize < MAX_OBJECTS) { // create new entity if pool size available
                entity = new Entity(queueSize + 1);
                inUsed.add(entity);
                System.out.println("Create entity-" + entity.getId() + " in the pool.");
            } else {
                System.out.println("There is not any entity in the pool.");
            }
        }

        return entity;
    }

    @Override
    public void release(Entity entity) {

        synchronized (this) {
            int idx = inUsed.indexOf(entity);
            if (idx != -1) {
                Entity released = inUsed.remove(idx);
                available.add(released);
                System.out.println("Released entity-" + released.getId());
            }
        }
    }
}
