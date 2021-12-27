package coskun.hanife.objectpool;

/**
 * @author Hanife Coskun
 */
public interface ObjectPool<T> {

    T borrow();
    void release(T t);
}
