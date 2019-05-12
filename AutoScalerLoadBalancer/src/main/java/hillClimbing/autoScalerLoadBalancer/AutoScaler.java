package hillClimbing.autoScalerLoadBalancer;

import com.amazonaws.services.cloudwatch.AmazonCloudWatch;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClientBuilder;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;

import java.util.Properties;
import java.util.Set;

public class AutoScaler implements Runnable {

    private final int MIN_INSTANCES;
    private final int MAX_INSTANCES;
    private final int CHECK_PERIOD;
    private final int ALARMS_TO_TRIGGER;
    private final int CPU_TO_UPSCALE;
    private final int CPU_TO_DOWNSCALE;

    private final String AMI_ID;
    private final String IAM_INSTANCE_PROFILE;
    private final String INSTANCE_TYPE;
    private final String SECURITY_GROUP;
    private final String REGION;
    private final String KEY_PAIR_NAME;

    private static AmazonEC2 ec2Client;
    private static AmazonCloudWatch cloudWatchClient;

    public AutoScaler(Properties properties) {

        MIN_INSTANCES = Integer.parseInt(properties.getProperty("autoScaler.minInstances", "1"));
        MAX_INSTANCES = Integer.parseInt(properties.getProperty("autoScaler.maxInstances", "10"));
        CHECK_PERIOD = Integer.parseInt(properties.getProperty("autoScaler.checkPeriod", "30000"));
        ALARMS_TO_TRIGGER = Integer.parseInt(properties.getProperty("autoScaler.alarmsToTrigger", "2"));
        CPU_TO_UPSCALE = Integer.parseInt(properties.getProperty("autoScaler.cpuToUpscale", "70"));
        CPU_TO_DOWNSCALE = Integer.parseInt(properties.getProperty("autoScaler.cpuToDownscale", "40"));

        //TODO: Change defaults
        AMI_ID = properties.getProperty("instance.amiID", "DEFAULT");
        IAM_INSTANCE_PROFILE = properties.getProperty("instance.iamProfile", "DEFAULT");
        INSTANCE_TYPE = properties.getProperty("instance.type", "t2.micro");
        SECURITY_GROUP = properties.getProperty("instance.securityGroup", "DEFAULT");
        REGION = properties.getProperty("instance.region", "us-east-1");
        KEY_PAIR_NAME = properties.getProperty("keyPairName", "DEFAULT");

        new Thread(this).start();
    }

    public void run() {
        ec2Client = AmazonEC2ClientBuilder.standard()
                .withRegion(REGION)
                .build();

        cloudWatchClient = AmazonCloudWatchClientBuilder.standard()
                .withRegion(REGION)
                .build();

        InstanceManager.updateInstances();
        int numberOfRunningInstances = InstanceManager.getNumberOfRunningInstances();
        if (numberOfRunningInstances < MIN_INSTANCES) {
            InstanceManager.lauchInstance(MIN_INSTANCES - numberOfRunningInstances);
        } else if (numberOfRunningInstances > MAX_INSTANCES) {
            InstanceManager.terminateInstance(numberOfRunningInstances - MAX_INSTANCES);
        }

        autoScale();
    }

    private void autoScale() {
        int consecutiveHighCPUAlarms = 0;
        int consecutiveLowCPUAlarms = 0;
        while (true) {
            try {

                InstanceManager.updateInstances();
                int cpuUsage = InstanceManager.getGroupCPUUsage();

                if (cpuUsage >= CPU_TO_UPSCALE) {
                    consecutiveLowCPUAlarms = 0;
                    consecutiveHighCPUAlarms++;
                } else if (cpuUsage <= CPU_TO_DOWNSCALE) {
                    consecutiveHighCPUAlarms = 0;
                    consecutiveLowCPUAlarms++;
                }

                if (consecutiveHighCPUAlarms == ALARMS_TO_TRIGGER) {
                    consecutiveHighCPUAlarms = 0;
                    if (InstanceManager.getNumberOfRunningInstances() < MAX_INSTANCES) {
                        InstanceManager.lauchInstance();
                    }
                } else if (consecutiveLowCPUAlarms == ALARMS_TO_TRIGGER) {
                    consecutiveLowCPUAlarms = 0;
                    if (InstanceManager.getNumberOfRunningInstances() > MIN_INSTANCES) {
                        InstanceManager.stopInstance();
                    }
                }

                if (InstanceManager.getNumberOfRunningInstances() < MIN_INSTANCES) {
                    InstanceManager.lauchInstance(MIN_INSTANCES - InstanceManager.getNumberOfRunningInstances());
                } else if (InstanceManager.getNumberOfRunningInstances() > MAX_INSTANCES) {
                    InstanceManager.terminateInstance(InstanceManager.getNumberOfRunningInstances() - MAX_INSTANCES);
                }

                Thread.sleep(CHECK_PERIOD);
            } catch (InterruptedException ie) {

            }
        }
    }

    private static void log(String logMessage) {
        System.out.println(String.format("[Auto Scaler - Thread %d] %s", Thread.currentThread().getId(), logMessage));
    }

    private static void printErr(String errorMessage) {
        System.err.println(String.format("[Auto Scaler - Thread %d] %s", Thread.currentThread().getId(), errorMessage));
    }

}
