package ndi.webapi.test;

import metricspaces.util.Progress;

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
        try {
            String op = "";
            int p = -1;
            
            while (progress.getOperation() != null) {
                if (progress.getOperation() != op) {
                    if (!op.equals("")) {
                        System.out.printf("100%%\n%s: ", progress.getOperation());
                        p = -1;
                    }
                    else {
                        System.out.printf("\n%s: ", progress.getOperation());
                    }

                    op = progress.getOperation();
                }

                if (!Double.isNaN(progress.getFractionDone())) {
                	int percent = (int)(progress.getFractionDone() * 100);
                	
                	if ((percent % 5) == 0 && percent != p && percent != 100) {
                		p = percent;
                		System.out.printf("%d%%...", percent);
                	}
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