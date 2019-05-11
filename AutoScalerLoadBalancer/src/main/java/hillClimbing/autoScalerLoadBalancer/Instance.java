package hillClimbing.autoScalerLoadBalancer;

class Instance {

    private com.amazonaws.services.ec2.model.Instance instance;
    private long load;
    private int noRequests;

    public Instance(com.amazonaws.services.ec2.model.Instance instance) {
        this.instance = instance;
        this.load = 0;
        this.noRequests = 0;
    }

    public long getLoad() {
        return this.load;
    }

    public int getNoRequests() {
        return this.noRequests;
    }

    public String getInstanceIP() {
        return instance.getPrivateIpAddress();
    }

    public String getInstanceID() {
        return instance.getInstanceId();
    }

    public synchronized void newRequest(long cost) {
        this.load += cost;
        this.noRequests++;
    }

    public synchronized void requestCompleted(long cost) {
        this.load -= cost;
        this.noRequests--;
    }
}