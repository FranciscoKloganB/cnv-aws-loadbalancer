package hillClimbing.autoScalerLoadBalancer;

import java.util.Properties;

public class AutoScaler implements Runnable {

    private final int MIN_INSTANCES;
    private final int MAX_INSTANCES;
    private final int CHECK_PERIOD;
    private final int ALARMS_TO_TRIGGER;
    private final int CPU_TO_UPSCALE;
    private final int CPU_TO_DOWNSCALE;


    public AutoScaler(Properties properties) {

        MIN_INSTANCES = Integer.parseInt(properties.getProperty("autoScaler.minInstances", "1"));
        MAX_INSTANCES = Integer.parseInt(properties.getProperty("autoScaler.maxInstances", "10"));
        CHECK_PERIOD = Integer.parseInt(properties.getProperty("autoScaler.checkPeriod", "30000"));
        ALARMS_TO_TRIGGER = Integer.parseInt(properties.getProperty("autoScaler.alarmsToTrigger", "2"));
        CPU_TO_UPSCALE = Integer.parseInt(properties.getProperty("autoScaler.cpuToUpscale", "70"));
        CPU_TO_DOWNSCALE = Integer.parseInt(properties.getProperty("autoScaler.cpuToDownscale", "40"));

        new Thread(this).start();
    }

    public void run() {

        InstanceManager.updateInstances();
        int numberOfRunningInstances = InstanceManager.getNumberOfRunningInstances();
        if (numberOfRunningInstances < MIN_INSTANCES) {
            InstanceManager.launchInstance(MIN_INSTANCES - numberOfRunningInstances);
        } else if (numberOfRunningInstances > MAX_INSTANCES) {
            InstanceManager.prepareTerminateInstance(numberOfRunningInstances - MAX_INSTANCES);
        }

        autoScale();
    }

    private void autoScale() {
        int consecutiveHighCPUAlarms = 0;
        int consecutiveLowCPUAlarms = 0;
        while (true) {
            try {

                InstanceManager.updateInstances();
                float cpuUsage = InstanceManager.getGroupCPUUsage();

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
                        InstanceManager.launchInstance();
                    }
                } else if (consecutiveLowCPUAlarms == ALARMS_TO_TRIGGER) {
                    consecutiveLowCPUAlarms = 0;
                    if (InstanceManager.getNumberOfRunningInstances() > MIN_INSTANCES) {
                        InstanceManager.prepareStopInstance();
                    }
                }

                if (InstanceManager.getNumberOfRunningInstances() < MIN_INSTANCES) {
                    InstanceManager.launchInstance(MIN_INSTANCES - InstanceManager.getNumberOfRunningInstances());
                } else if (InstanceManager.getNumberOfRunningInstances() > MAX_INSTANCES) {
                    InstanceManager.prepareTerminateInstance(InstanceManager.getNumberOfRunningInstances() - MAX_INSTANCES);
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
