package queue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.Serializable;

/**
 * Read the elements from the queue and execute the element are done by the
 * QueueProcessor. Process running until it has some interruption or state
 * change in isProcessStopped=true. If isAutoRestart=true in executionEngine,
 * then
 * QueueProcessor will be restarted if it is crashed.
 *
 * @param <T> the type of elements held in this queue that implements the
 *            Serializable interface.
 * @see ExecutionEngine
 */
public class QueueProcessor<T extends Serializable> extends Thread {

    /**
	 * @uml.property  name="log"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
    private final Log log = LogFactory.getLog(QueueProcessor.class);

    /**
	 * @uml.property  name="executionEngine"
	 * @uml.associationEnd  multiplicity="(1 1)" inverse="queueProcessor:queue.ExecutionEngine"
	 */
    private ExecutionEngine<T> executionEngine = null;
    /**
	 * @uml.property  name="isProcessStopped"
	 */
    private boolean isProcessStopped = false;

    public QueueProcessor(ExecutionEngine<T> executionEngine) {
        this.executionEngine = executionEngine;
    }

    /**
	 * @return
	 * @uml.property  name="isProcessStopped"
	 */
    public boolean isProcessStopped() {
        return isProcessStopped;
    }

    public void setProcessStopped(boolean isProcessStopped) {
        this.isProcessStopped = isProcessStopped;
    }

    // Thread run method
    public void run() {
        try {
            try {
                Thread.sleep(2000);
            } catch (Exception e) {
                log.error("Thread interruption in AppCration Process.");
            }
            execute();
        } catch (Exception e) {
            String errorMsg =
                    "Execution thread got the " + e.getMessage() +
                    " and application creation thread is stopped.";
            log.error(errorMsg, e);
            // Here not going to throw an exception. If isAutoRestart=true then
            // it will be restarted.
        }

        // isAutoRestart=true, then user wants to auto restart when server is
        // stopped by exception.
        // isProcessStopped=true means, user stopped the service by manually.
        // Then restart process will not be happened.
        if (this.executionEngine.isAutoRestart() && !isProcessStopped) {
            String infoMsg = "Execution thread restarting ...";
            log.info(infoMsg);
            new QueueProcessor<T>(this.executionEngine).start();
        }

    }

    /**
     * Execution process is done in this method
     *
     * @throws QueueException
     */
    public void execute() throws Exception {
        // Running this loop to poll and execute the item from the queue.
        while (true && !isProcessStopped) {

            // Poll the item from the queue. If the queue is empty, this
            // call wait until an item is come to the queue.
            T t = executionEngine.getSynchQueue().poll();

            log.info("ExecuteEngine Execute an element. Element Info : " + t.toString());
            executionEngine.getExecutor().execute(t);

            // Waiting a specific time interval before poll again from the
            // queue. Default is 0.
            if (executionEngine.getCallDelay() > 0) {
                try {
                    Thread.sleep(executionEngine.getCallDelay());
                } catch (Exception e) {
                    String errorMsg = "Error occured in thread sleep, " + e.getMessage();
                    log.error(errorMsg, e);
                }
            }

        }
    }
}
