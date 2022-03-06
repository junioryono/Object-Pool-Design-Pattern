public class CIA_Agent implements Runnable, Agent_IF {
    private boolean workingInProgress;
    private String myFootPrint;
    private int taskId;

    public CIA_Agent(String footprint) {
        this.myFootPrint = footprint;
    }

    @Override
    public void run() {
        while (true) {
            try {
                if (workingInProgress) {
                    Thread.sleep(100);
                    System.out.println(this.myFootPrint);
                } else {
                    Thread.sleep(500);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void processing() {}

    @Override
    public void startTask() {
        this.workingInProgress = true;
    }

    @Override
    public void stopTask() {
        this.workingInProgress = false;
    }

    @Override
    public void setTaskId(int id) {
        this.taskId = id;
    }
}
