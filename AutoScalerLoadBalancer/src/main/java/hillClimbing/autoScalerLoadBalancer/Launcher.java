package hillClimbing.autoScalerLoadBalancer;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Launcher {

    public static void main(String[] args) {

        try {
            Properties properties = new Properties();
            String propertiesFilename = "application.properties";
            InputStream inputStream = Launcher.class.getClassLoader().getResourceAsStream(propertiesFilename);
            properties.load(inputStream);
            new InstanceManager(properties);
            new LoadBalancer(properties);
            new AutoScaler(properties);
        } catch (IOException e) {
            System.err.println(String.format("[Launcher - Thread %d] There was an error loading the properties file: %s", Thread.currentThread().getId(), e.getMessage()));
        }
    }
}
