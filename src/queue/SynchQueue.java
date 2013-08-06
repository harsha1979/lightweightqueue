package queue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.Serializable;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * This SynchQueue is a Specific queue that can use to hold the callers of the
 * poll method until queue gets the items. If one put the item to queue then it
 * will notify to all the caller to get the item from queue as they requested
 * order. We can specify the maximum queue length.
 *
 * @param <T> the type of elements held in this queue that implements the
 *            Serializable interface.
 * @see ExecutionEngine
 */
public class SynchQueue<T extends Serializable> {

    /**
	 * @uml.property  name="log"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
    private final Log log = LogFactory.getLog(SynchQueue.class);

    // Internally use Synchronized Queue
    /**
	 * @uml.property  name="queue"
	 * @uml.associationEnd  multiplicity="(0 -1)" elementType="java.io.Serializable"
	 */
    private Queue<T> queue = new ConcurrentLinkedQueue<T>();

    // Maximum elements that can be held in the queue. -1 (Unlimited) is
    // default.
    /**
	 * @uml.property  name="queueLength"
	 */
    private int queueLength = -1;

    public SynchQueue() {

    }

    public SynchQueue(int queueLength) {
        this.queueLength = queueLength;
    }

    /**
	 * @return
	 * @uml.property  name="queueLength"
	 */
    public int getQueueLength() {
        return this.queueLength;
    }

    /**
	 * @param queueLength
	 * @uml.property  name="queueLength"
	 */
    public void setQueueLength(int queueLength) {
        this.queueLength = queueLength;
    }

    /**
     * Retrieves and removes the head of this queue if elements available in
     * queue, or waiting if this queue is empty until get the item to the queue.
     *
     * @return the head of this queue.
     */
    public T poll() {

        T t = this.queue.poll();

        synchronized (this.queue) {
            if (t == null) {
                try {
                    this.queue.wait();
                } catch (InterruptedException e) {
                    String errorMsg = "Thread is interrupted. " + e.getMessage();
                    log.error(errorMsg, e);
                }
                return poll();
            }
        }

        return t;
    }

    /**
     * Inserts the specified element into this queue if it is possible to do so
     * immediately without violating capacity restrictions, notify to the queue
     * to release the waiting request upon success and throwing an
     * <tt>QueueException</tt> if no space is currently available.
     *
     * @param t the element to add to the queue.
     * @throws QueueException if maximum limit in queue that specified by user is exceeded.
     */
    public boolean put(T t) throws QueueException {

        boolean isAdded = false;
        if (t != null) {
            if (this.queue.size() == getQueueLength()) {
                String warnMsg =
                        "Maximum limit of the queue is exceeded. Please try again with in few seconds.";
                log.warn(warnMsg);
                throw new QueueException(warnMsg);
            }

            this.queue.add(t);

            synchronized (this.queue) {
                this.queue.notifyAll();
            }

            isAdded = true;
            String infoMsg =
                    "Element was added to the queue successfully. Element Info : " +
                    t.toString();
            log.info(infoMsg);

        }

        return isAdded;
    }

    /**
     * @param t the element available in the queue.
     * @return if element available in the queue, returns <tt>true</tt>.
     */
    public boolean isAvailableInQueue(T t) {
        return this.queue.contains(t);
    }

    /**
     * @param t the element available in the queue.
     * @return size of the queue.
     */
    public int queuedCount(T t) {
        return this.queue.size();
    }
}
