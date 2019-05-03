package hillClimbing.database;

import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Database {

    private final static String AWS_REGION = "us-east-1";

    private static Database instance;
    private static AmazonDynamoDB client;
    private static DynamoDBMapper mapper;

    private Database() {
        Database.client = AmazonDynamoDBClientBuilder
                .standard()
                .withRegion(AWS_REGION)
                .withCredentials(new InstanceProfileCredentialsProvider(false))
                .build();

        Database.mapper = new DynamoDBMapper(client);
    }

    private static void checkIfInit() {
        if (Database.instance == null) {
            Database.instance = new Database();
        }
    }

    public static List<ClimbRequestCostEntry> query(String key) {
        checkIfInit();
        Map<String, AttributeValue> eav = new HashMap<>();
        eav.put(":key", new AttributeValue().withS(key));

        DynamoDBQueryExpression<ClimbRequestCostEntry> query = new DynamoDBQueryExpression<ClimbRequestCostEntry>()
                .withKeyConditionExpression("key = :key")
                .withExpressionAttributeValues(eav);

        return mapper.query(ClimbRequestCostEntry.class, query);
    }

    public static void insert(ClimbRequestCostEntry entry) {
        checkIfInit();
        mapper.save(entry);
    }
}
