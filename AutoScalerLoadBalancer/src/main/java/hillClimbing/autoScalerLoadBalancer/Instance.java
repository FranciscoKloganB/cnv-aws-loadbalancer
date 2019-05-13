package hillClimbing.autoScalerLoadBalancer;

import com.amazonaws.services.ec2.model.InstanceState;

class Instance {

    private com.amazonaws.services.ec2.model.Instance instance;
    private long load;
    private int noRequests;

    Instance(com.amazonaws.services.ec2.model.Instance instance) {
        this.instance = instance;
        this.load = 0;
        this.noRequests = 0;
    }

    long getLoad() {
        return this.load;
    }

    int getNoRequests() {
        return this.noRequests;
    }


    void setInstance(com.amazonaws.services.ec2.model.Instance awsInstance) {
        this.instance = awsInstance;
    }

    void setState(InstanceState currentState) {
        this.instance.setState(currentState);
    }

    String getInstanceIP() {
        return instance != null ? instance.getPrivateIpAddress() : null;
    }

    String getInstanceID() {
        return instance != null ? instance.getInstanceId() : null;
    }

    synchronized void newRequest(long cost) {
        this.load += cost;
        this.noRequests++;
    }

    synchronized void requestCompleted(long cost) {
        this.load -= cost;
        this.noRequests--;
    }

    synchronized void resetCounters() {
        this.load = 0;
        this.noRequests = 0;
    }

    boolean stoppable() {
        return load == 0 && noRequests == 0;
    }
}