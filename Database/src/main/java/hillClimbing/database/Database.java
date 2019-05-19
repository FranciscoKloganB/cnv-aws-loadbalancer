package hillClimbing.database;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.UpdateItemOutcome;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ReturnValue;

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

    public static List<ClimbRequestCostEntry> scanCloseRequests(ClimbRequestCostEntry request) {
        double areaSimilarity = 0.1;
        double startingPointSimilarity = 0.1;

        int mapWidth = request.getxLowerRightPoint() - request.getxUpperLeftPoint();
        int mapHeight = request.getyLowerRightPoint() - request.getyUpperLeftPoint();
        int area = mapWidth * mapHeight;


        Map<String, AttributeValue> eav = new HashMap<>();
        eav.put(":hill", new AttributeValue().withS(request.getHill()));
        eav.put(":strategy", new AttributeValue().withS(request.getStrategy()));
        eav.put(":minArea", new AttributeValue().withN(Double.toString(area * (1 - areaSimilarity))));
        eav.put(":maxArea", new AttributeValue().withN(Double.toString(area * (1 + areaSimilarity))));
        eav.put(":minXStartingPoint", new AttributeValue().withN(Double.toString(request.getxStartPoint() - (startingPointSimilarity * mapWidth))));
        eav.put(":maxXStartingPoint", new AttributeValue().withN(Double.toString(request.getxStartPoint() + (startingPointSimilarity * mapWidth))));
        eav.put(":minYStartingPoint", new AttributeValue().withN(Double.toString(request.getyStartPoint() - (startingPointSimilarity * mapHeight))));
        eav.put(":maxYStartingPoint", new AttributeValue().withN(Double.toString(request.getyStartPoint() + (startingPointSimilarity * mapHeight))));


        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                .withFilterExpression(
                        "strategy = :strategy and" +
                        "(xLowerRightPoint - xUpperLeftPoint) * (yLowerRightPoint - yUpperLeftPoint) >= :minArea and" +
                        "(xLowerRightPoint - xUpperLeftPoint) * (yLowerRightPoint - yUpperLeftPoint) <= :maxArea and" +
                        "xStartPoint >= :minXStartingPoint and xStartPoint <= :maxXStartingPoint" +
                        "yStartPoint >= :minYStartingPoint and yStartPoint <= :maxYStartingPoint")
                .withExpressionAttributeValues(eav);

        return mapper.scan(ClimbRequestCostEntry.class, scanExpression);
    }

    public static void insert(ClimbRequestCostEntry entry) {
        checkIfInit();
        mapper.save(entry);
    }

    public static void TESTInsert(TESTClimbRequestCostEntry entry) {
        checkIfInit();
        mapper.save(entry);
    }

    public static void TESTUpdate(String key, long timeDiff) {
        checkIfInit();

        DynamoDB dynamoDB = new DynamoDB(client);
        Table table = dynamoDB.getTable("TESTCNVT17HillClimbDatabase");

        UpdateItemSpec updateItemSpec = new UpdateItemSpec()
                .withPrimaryKey("key", key)
                .withUpdateExpression("set time = :time")
                .withValueMap(new ValueMap().withNumber(":time", timeDiff))
                .withReturnValues(ReturnValue.UPDATED_NEW);

        UpdateItemOutcome outcome = table.updateItem(updateItemSpec);
    }
}
