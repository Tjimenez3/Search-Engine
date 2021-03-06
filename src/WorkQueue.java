import java.util.LinkedList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A simple work queue implementation based on the IBM developerWorks article by
 * Brian Goetz. It is up to the user of this class to keep track of whether
 * there is any pending work remaining.
 *
 * @see <a href=
 *      "http://www.ibm.com/developerworks/library/j-jtp0730/index.html">Java
 *      Theory and Practice: Thread Pools and Work Queues</a>
 */
public class WorkQueue {
	// end of lab10
	public static final Logger log = LogManager.getLogger(Driver.class);

	/**
	 * Pool of worker threads that will wait in the background until work is
	 * available.
	 */
	private final PoolWorker[] workers;

	/** Queue of pending work requests. */
	private final LinkedList<Runnable> queue;

	/** Used to signal the queue should be shutdown. */
	private volatile boolean shutdown;
	private volatile int pending;

	/** The default number of threads to use when not specified. */
	public static final int DEFAULT = 5;

	/**
	 * Starts a work queue with the default number of threads.
	 *
	 * @see #WorkQueue(int)
	 */
	public WorkQueue() {
		this(DEFAULT);
	}

	/**
	 * Starts a work queue with the specified number of threads.
	 *
	 * @param threads number of worker threads; should be greater than 1
	 * @return
	 */
	public int getpending() {
		synchronized (queue) {
			return pending;
		}
	}

	public WorkQueue(int threads) {
		this.queue = new LinkedList<Runnable>();
		this.workers = new PoolWorker[threads];

		this.shutdown = false;

		// start the threads so they are waiting in the background
		for (int i = 0; i < threads; i++) {
			this.workers[i] = new PoolWorker();
			this.workers[i].start();
		}
	}

	/**
	 * Adds a work request to the queue. A thread will process this request when
	 * available.
	 *
	 * @param r work request (in the form of a {@link Runnable} object)
	 */
	public void execute(Runnable r) {
		synchronized (queue) {
			pending++;
			queue.addLast(r);
			queue.notifyAll();
		}
	}

	/**
	 * Waits for all pending work to be finished.
	 */
	public void finish() {
		// finish = true;
		synchronized (queue) {
			while (pending > 0) {
				try {
					//synchronized (queue) {
						queue.wait();
						//System.out.println("We have awaken and pending is" + pending);
					//}
				} catch (InterruptedException e) {
					log.debug("queue.wait failed", e);
				}
			}
			shutdown();
		}
	}

	/**
	 * Asks the queue to shutdown. Any unprocessed work will not be finished, but
	 * threads in-progress will not be interrupted.
	 */
	public void shutdown() {
		// safe to do unsynchronized due to volatile keyword
		shutdown = true;

		synchronized (this.queue) {
			queue.notifyAll();
		}
	}

	/**
	 * Returns the number of worker threads being used by the work queue.
	 *
	 * @return number of worker threads
	 */
	public int size() {
		return workers.length;
	}

	/**
	 * Waits until work is available in the work queue. When work is found, will
	 * remove the work from the queue and run it. If a shutdown is detected, will
	 * exit instead of grabbing new work from the queue. These threads will continue
	 * running in the background until a shutdown is requested.
	 */
	private class PoolWorker extends Thread {

		@Override
		public void run() {
			Runnable r = null;

			while (true) {
				synchronized (queue) {
					while (queue.isEmpty() && !shutdown) {
						try {
							queue.wait();
						} catch (InterruptedException ex) {
							System.err.println("Warning: Work queue interrupted.");
							Thread.currentThread().interrupt();
						}
					}

					// exit while for one of two reasons:
					// (a) queue has work, or (b) shutdown has been called

					if (shutdown) {
						break;
					} else {
						r = queue.removeFirst();
					}
				}

				try {
					log.debug("Pending is:" + pending);
					r.run();
				} catch (RuntimeException ex) {
					System.err.println("Warning: Work queue encountered an exception while running.");
				}
				synchronized (queue) {
					pending--;
					queue.notifyAll();
					log.debug("Pending has been decremented and threads have been notified" + pending);
					// System.out.println("Pending has been decremented and threads have been
					// notified"+pending);
				}
				synchronized (queue) {
					queue.notifyAll();
				}
				// System.out.println("Pending after everything is done"+pending);
				log.debug("Pending after:" + pending);
			}
		}
	}
}
