package commandline;

import metricspaces.Progress;

/**
 * Provides commandline feedback of the progress of tasks.
 * @author stewart
 *
 */
public class ProgressReporter implements Runnable {
    private Thread monitorThread;
    private Progress progress;
    private int interval;

    public ProgressReporter(Progress progress, int interval) {
        this.progress = progress;
        this.interval = interval;

        monitorThread = new Thread(this);
        monitorThread.start();
    }


    @Override
    public void run() {
    	//don't bother if in eclipse
    	if (System.console() == null)
    		return;
    	
        try {
            String op = "";

            while (progress.getOperation() != null) {
                if (progress.getOperation() != op) {
                    if (!op.equals("")) {
                        System.out.printf("100%%\n%s: ", progress.getOperation());
                    }
                    else {
                        System.out.printf("\n%s: ", progress.getOperation());
                    }

                    op = progress.getOperation();
                }

                if (Double.isNaN(progress.getFractionDone())) {
                	System.out.printf("\rInitialising...");
                }
                else {
                	System.out.printf("%3.0f%%\b\b\b\b", progress.getFractionDone() * 100);
                }
                
                Thread.sleep(interval);
            }
        }
        catch (InterruptedException e) {

        }

        System.out.printf("100%%\n");
    }


    /**
     * Stops monitoring and blocks until the monitor thread exits.
     */
    public void stop() {
        try {
            progress.setOperation(null);
            monitorThread.join();
        } catch (InterruptedException e) {

        }
    }
}