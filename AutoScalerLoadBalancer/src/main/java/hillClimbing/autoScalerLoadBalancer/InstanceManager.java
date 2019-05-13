package hillClimbing.autoScalerLoadBalancer;

import com.amazonaws.services.cloudwatch.AmazonCloudWatch;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClientBuilder;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.*;

import java.util.*;
import java.util.stream.Collectors;

class InstanceManager {

    //TODO: THIS HAS TO BE SYNC
    private static String AMI_ID;
    private static String IAM_INSTANCE_PROFILE_ARN;
    private static String INSTANCE_TYPE;
    private static String SECURITY_GROUP;
    private static String REGION;
    private static String KEY_PAIR_NAME;

    private static AmazonEC2 ec2Client;
    private static AmazonCloudWatch cloudWatchClient;

    private static Map<String, Instance> runningInstances;
    private static Map<String, Instance> stoppedInstances;

    private static Set<String> instancesToStop;
    private static Set<String> instancesToTerminate;

    private static final Object instancesLock = new Object();

    public InstanceManager(Properties properties) {

        //TODO: Change defaults
        AMI_ID = properties.getProperty("instance.amiID", "DEFAULT");
        IAM_INSTANCE_PROFILE_ARN = properties.getProperty("instance.iamProfileARN", "DEFAULT");
        INSTANCE_TYPE = properties.getProperty("instance.type", "t2.micro");
        SECURITY_GROUP = properties.getProperty("instance.securityGroup", "DEFAULT");
        REGION = properties.getProperty("instance.region", "us-east-1");
        KEY_PAIR_NAME = properties.getProperty("keyPairName", "DEFAULT");

        ec2Client = AmazonEC2ClientBuilder.standard()
                .withRegion(REGION)
                .build();

        cloudWatchClient = AmazonCloudWatchClientBuilder.standard()
                .withRegion(REGION)
                .build();

        runningInstances = new HashMap<>();
        stoppedInstances = new HashMap<>();

        instancesToStop = new HashSet<>();
        instancesToTerminate = new HashSet<>();
    }

    static void updateInstances() {

        Set<com.amazonaws.services.ec2.model.Instance> awsInstances = getAllInstances();
        Set<String> awsInstancesIds = awsInstances.stream().map(com.amazonaws.services.ec2.model.Instance::getInstanceId).collect(Collectors.toSet());

        synchronized (instancesLock) {
            runningInstances.entrySet().removeIf(entry -> !awsInstancesIds.contains(entry.getKey()));
            stoppedInstances.entrySet().removeIf(entry -> !awsInstancesIds.contains(entry.getKey()));

            for (com.amazonaws.services.ec2.model.Instance awsInstance : awsInstances) {
                String instanceState = awsInstance.getState().getName();
                String instanceID = awsInstance.getInstanceId();

                if ((instanceState.equals("pending") ||
                        instanceState.equals("running")) &&
                        !runningInstances.containsKey(instanceID)) {
                    if (stoppedInstances.containsKey(instanceID)) {
                        Instance instance = stoppedInstances.get(instanceID);
                        instance.setInstance(awsInstance);
                        instance.resetCounters();
                        runningInstances.put(instanceID, instance);
                        stoppedInstances.remove(instanceID);
                    } else {
                        runningInstances.put(instanceID, new Instance(awsInstance));
                    }
                } else if ((instanceState.equals("stopping") ||
                        instanceState.equals("stopped")) &&
                        !stoppedInstances.containsKey(instanceID)) {
                    if (runningInstances.containsKey(instanceID)) {
                        Instance instance = runningInstances.get(instanceID);
                        instance.setInstance(awsInstance);
                        instance.resetCounters();
                        stoppedInstances.put(instanceID, instance);
                        runningInstances.remove(instanceID);
                    } else {
                        // This should happen very very rarely
                        stoppedInstances.put(instanceID, new Instance(awsInstance));
                    }
                }
            }

            stopInstances();
            terminateInstances();
        }
    }

    static int getNumberOfRunningInstances() {
        return runningInstances.size();
    }

    static void launchInstance() {
        synchronized (instancesLock) {
            if (stoppedInstances.isEmpty()) {
                RunInstancesRequest runInstancesRequest = new RunInstancesRequest();

                runInstancesRequest.withImageId(AMI_ID)
                        .withInstanceType(INSTANCE_TYPE)
                        .withMinCount(1)
                        .withMaxCount(1)
                        .withKeyName(KEY_PAIR_NAME)
                        .withSecurityGroups(SECURITY_GROUP)
                        .withIamInstanceProfile(new IamInstanceProfileSpecification().withArn(IAM_INSTANCE_PROFILE_ARN));

                RunInstancesResult runInstancesResult = ec2Client.runInstances(runInstancesRequest);
                Instance instance = new Instance(runInstancesResult.getReservation().getInstances().get(0));
                runningInstances.put(instance.getInstanceID(), instance);
            } else {
                String stoppedInstanceID = (String) stoppedInstances.keySet().toArray()[0];
                StartInstancesRequest startInstancesRequest = new StartInstancesRequest().withInstanceIds(stoppedInstanceID);
                StartInstancesResult startInstancesResult = ec2Client.startInstances(startInstancesRequest);

                Instance instance = stoppedInstances.get(stoppedInstanceID);
                instance.setState(startInstancesResult.getStartingInstances().get(0).getCurrentState());
                runningInstances.put(stoppedInstanceID, instance);
                stoppedInstances.remove(stoppedInstanceID);
            }
        }
    }

    static void launchInstance(int numberOfInstances) {
        for (int i = 0; i < numberOfInstances; i++) {
            launchInstance();
        }
    }

    static void prepareTerminateInstance(int numberOfInstances) {

    }

    static void prepareStopInstance() {

    }

    static float getGroupCPUUsage() {
        return 0;
    }

    static Instance getLeastUsedInstance() {
        return null;
    }

    private static void stopInstances() {
        synchronized (instancesLock) {
            Set<String> stoppableInstances = instancesToStop.stream()
                    .filter(instanceID ->
                            runningInstances.getOrDefault(instanceID, new Instance(null)).stoppable()
                    ).collect(Collectors.toSet());
            StopInstancesRequest stopInstancesRequest = new StopInstancesRequest().withInstanceIds(stoppableInstances);
            StopInstancesResult stopInstancesResult = ec2Client.stopInstances(stopInstancesRequest);
            instancesToStop.removeAll(stoppableInstances);
            for (InstanceStateChange instanceStateChange : stopInstancesResult.getStoppingInstances()) {
                if (!runningInstances.containsKey(instanceStateChange.getInstanceId()))
                    continue;
                Instance instance = runningInstances.get(instanceStateChange.getInstanceId());
                instance.setState(instanceStateChange.getCurrentState());
                instance.resetCounters();
                stoppedInstances.put(instanceStateChange.getInstanceId(), instance);
                runningInstances.remove(instanceStateChange.getInstanceId());
            }
        }
    }

    private static void terminateInstances() {
        synchronized (instancesLock) {
            Set<String> terminableInstances = instancesToTerminate.stream()
                    .filter(instanceID ->
                            runningInstances.getOrDefault(instanceID, new Instance(null)).stoppable()
                    ).collect(Collectors.toSet());
            TerminateInstancesRequest terminateInstancesRequest = new TerminateInstancesRequest().withInstanceIds(terminableInstances);
            ec2Client.terminateInstances(terminateInstancesRequest);
            instancesToTerminate.removeAll(terminableInstances);
            for (String instanceID : terminableInstances) {
                runningInstances.remove(instanceID);
                stoppedInstances.remove(instanceID);
            }
        }
    }

    private static Set<com.amazonaws.services.ec2.model.Instance> getAllInstances() {
        DescribeInstancesRequest request = new DescribeInstancesRequest();
        Set<com.amazonaws.services.ec2.model.Instance> instances = new HashSet<>();
        boolean done = false;
        while (!done) {
            DescribeInstancesResult response = ec2Client.describeInstances(request);
            for (Reservation reservation : response.getReservations()) {
                instances.addAll(reservation.getInstances()
                        .stream()
                        .filter(instance ->
                                instance.getImageId().equals(AMI_ID) &&
                                !instance.getState().getName().equals("shutting-down") &&
                                !instance.getState().getName().equals("terminated")
                        ).collect(Collectors.toSet()));
            }

            request.setNextToken(response.getNextToken());
            if(response.getNextToken() == null) {
                done = true;
            }
        }

        return instances;
    }

    private static void log(String logMessage) {
        System.out.println(String.format("[Instance Manager - Thread %d] %s", Thread.currentThread().getId(), logMessage));
    }

    private static void printErr(String errorMessage) {
        System.err.println(String.format("[Instance Manager - Thread %d] %s", Thread.currentThread().getId(), errorMessage));
    }
}
