package queue;

import java.io.Serializable;

/**
 * When the ExecutionEngine execute the process, it wants implementation of this
 * interface. It calls this execute method. So implementer of this interface can
 * get the calls from the engine thread in given cycle as the availability of
 * items
 * in the queue.
 *
 * @param <T> the type of elements held in this queue that implements the
 *            Serializable interface.
 * @see ExecutionEngine
 */
public interface Executor<T extends Serializable> {
    /**
     * This method will be call by the ExecutionEngine in the given cycle as
     * availability of items in the queue.
     *
     * @param t the type of elements held in this queue that implements the
     *          Serializable interface.
     * @throws Exception
     */
    void execute(T t) throws Exception;
}
