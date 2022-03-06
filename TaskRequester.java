public class TaskRequester implements Runnable {
    private ObjectPool server;

    public TaskRequester(ObjectPool p) {
        this.server = p;
    }

    @Override
    public void run() {
        Agent_IF agent;
        try {
            agent = (Agent_IF) server.waitForObject();
            agent.startTask();
            Thread.sleep(2000);
            agent.stopTask();
            server.release(agent);
        } catch (InterruptedException error) {
            error.printStackTrace();
        }
    }
}
