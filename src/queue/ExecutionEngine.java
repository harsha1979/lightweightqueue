package queue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.Serializable;

/**
 * ExecutionEngine is an entry in this process. When any code point needs some
 * queue operation, can be used this Queue Execution Engine. This has
 * synchronized queue, user can put the Serializable custom bean to the queue
 * and specify the time delay for the queue element execution cycle. Executor
 * interface is given to implement the user own execute method.
 * <p/>
 * <p/>
 * Examples of expected usage:
 * <p/>
 * <blockquote>
 * <p/>
 * <pre>
 *
 *  //Create an ExecutionEngine and start.
 *  boolean isAutoStart = false ;
 *  boolean isAutoRestart = false ;
 *  //Time Delay in Millisecond
 *  int timeDelay = 100 ;
 *  int queueLength - 100 ;
 *
 *  //ExecutorImpl is an implementation of the Executor to do the task when the executor do execute.
 *  Executor executor = new ExecutorImpl();
 *  ExecutionEngine executionEngine =  new ExecutionEngine<CustomBean>(executor,isAutoStart,
 *  															isAutoRestart,timeDelay,queueLength);
 *  executionEngine.startEngine();
 *
 *  //Put an element to the queue
 *  CustomBean customBean = new CustomBean();
 *  executionEngine.getSynchQueue().put(customBean);
 *
 * </pre>
 * <p/>
 * </blockquote>
 *
 * @param <T> the type of elements held in this queue that implements the
 *            Serializable interface.
 */
public class ExecutionEngine<T extends Serializable> {

    /**
	 * @uml.property  name="synchQueue"
	 * @uml.associationEnd  
	 */
    private SynchQueue<T> synchQueue = null;
    /**
	 * @uml.property  name="executor"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
    private Executor<T> executor = null;

    /**
	 * @uml.property  name="callDelay"
	 */
    private long callDelay = 0;
    /**
	 * @uml.property  name="isAutoRestart"
	 */
    private boolean isAutoRestart = false;

    /**
	 * @uml.property  name="queueProcessor"
	 * @uml.associationEnd  multiplicity="(1 1)" inverse="executionEngine:queue.QueueProcessor"
	 */
    private QueueProcessor<T> queueProcessor = null;

    /**
	 * @uml.property  name="log"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
    private final Log log = LogFactory.getLog(ExecutionEngine.class);

    /**
     * @param executor    is an interface that wants to implement to get the call for
     *                    execute.
     * @param isAutoStart is true then server will be stated when create an instance of
     *                    ExecutionEngine.
     * @param callDelay   is the time delay in between the poll of queue.
     * @param queueLength is maximum number of elements that can held by the queue.
     * @throws QueueException
     */
    public ExecutionEngine(Executor<T> executor, boolean isAutoStart, boolean isAutoRestart,
                           long callDelay, int queueLength) throws QueueException {
        this.synchQueue = new SynchQueue<T>(queueLength);
        this.executor = executor;
        this.callDelay = callDelay;
        this.isAutoRestart = isAutoRestart;
        if (isAutoStart) {
            startEngine();
        }

    }

    /**
     * @param executor    is an interface that wants to implement to get the call for
     *                    execute.
     * @param isAutoStart is true then server will be stated when create an instance of
     *                    ExecutionEngine.
     * @param callDelay   is the time delay in between the poll of queue.
     * @throws QueueException
     */
    public ExecutionEngine(Executor<T> executor, boolean isAutoStart, boolean isAutoRestart,
                           long callDelay) throws QueueException {

        this.executor = executor;
        this.callDelay = callDelay;
        this.isAutoRestart = isAutoRestart;
        if (isAutoStart) {
            startEngine();
        }

    }

    /**
     * @param executor    is an interface that wants to implement to get the call for
     *                    execute.
     * @param isAutoStart is true then server will be stated when create an instance of
     *                    ExecutionEngine.
     * @param queueLength is maximum number of elements that can held by the queue.
     * @throws QueueException
     */
    public ExecutionEngine(Executor<T> executor, boolean isAutoStart, boolean isAutoRestart,
                           int queueLength) throws QueueException {

        this.synchQueue = new SynchQueue<T>(queueLength);
        this.executor = executor;
        this.isAutoRestart = isAutoRestart;
        if (isAutoStart) {
            startEngine();
        }

    }

    /**
     * @param executor    is an interface that wants to implement to get the call for
     *                    execute.
     * @param isAutoStart is true then server will be stated when create an instance of
     *                    ExecutionEngine.
     * @throws QueueException
     */
    public ExecutionEngine(Executor<T> executor, boolean isAutoStart, boolean isAutoRestart)
            throws QueueException {

        this.synchQueue = new SynchQueue<T>();
        this.executor = executor;
        this.isAutoRestart = isAutoRestart;
        if (isAutoStart) {
            startEngine();
        }

    }

    public SynchQueue<T> getSynchQueue() {
        return synchQueue;
    }

    /**
	 * @return
	 * @uml.property  name="callDelay"
	 */
    public long getCallDelay() {
        return callDelay;
    }

    /**
	 * @param callDelay
	 * @uml.property  name="callDelay"
	 */
    public void setCallDelay(long callDelay) {
        this.callDelay = callDelay;
    }

    /**
	 * @return
	 * @uml.property  name="isAutoRestart"
	 */
    public boolean isAutoRestart() {
        return isAutoRestart;
    }

    public Executor<T> getExecutor() {
        return executor;
    }

    /**
     * Start the Execution Engine if not set isAutoStart = true
     *
     * @throws QueueException
     */
    public synchronized void startEngine() throws QueueException {
        try {
            if (this.queueProcessor == null) {
                this.queueProcessor = new QueueProcessor<T>(this);
            }
            if (!this.queueProcessor.isAlive()) {
                this.queueProcessor = new QueueProcessor<T>(this);
                this.queueProcessor.start();
            }
        } catch (Exception e) {
            String errorMsg = "Error occured when starting the execution thread, " + e.getMessage();
            log.error(errorMsg, e);
            throw new QueueException(errorMsg, e);
        }
    }

}
