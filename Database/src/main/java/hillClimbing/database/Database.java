package hillClimbing.database;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
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
        eav.put(":params", new AttributeValue().withS(key));

        DynamoDBQueryExpression<ClimbRequestCostEntry> query = new DynamoDBQueryExpression<ClimbRequestCostEntry>()
                .withKeyConditionExpression("params = :params")
                .withExpressionAttributeValues(eav);

        return mapper.query(ClimbRequestCostEntry.class, query);
    }

    public static List<ClimbRequestCostEntry> scanCloseRequests(ClimbRequestCostEntry request) {
        double areaSimilarity = 0.1;
        double startingPointSimilarity = 0.1;

        int mapWidth = request.getxLowerRightPoint() - request.getxUpperLeftPoint();
        int mapHeight = request.getyLowerRightPoint() - request.getyUpperLeftPoint();


        Map<String, AttributeValue> eav = new HashMap<>();
        eav.put(":hill", new AttributeValue().withS(request.getHill()));
        eav.put(":strategy", new AttributeValue().withS(request.getStrategy()));

        eav.put(":minXLowerRightPoint", new AttributeValue().withN(Double.toString(request.getxLowerRightPoint() - (areaSimilarity * mapWidth))));
        eav.put(":maxXLowerRightPoint", new AttributeValue().withN(Double.toString(request.getxLowerRightPoint() + (areaSimilarity * mapWidth))));
        eav.put(":minYLowerRightPoint", new AttributeValue().withN(Double.toString(request.getyLowerRightPoint() - (areaSimilarity * mapHeight))));
        eav.put(":maxYLowerRightPoint", new AttributeValue().withN(Double.toString(request.getyLowerRightPoint() + (areaSimilarity * mapHeight))));

        eav.put(":minXUpperLeftPoint", new AttributeValue().withN(Double.toString(request.getxUpperLeftPoint() - (areaSimilarity * mapWidth))));
        eav.put(":maxXUpperLeftPoint", new AttributeValue().withN(Double.toString(request.getxUpperLeftPoint() + (areaSimilarity * mapWidth))));
        eav.put(":minYUpperLeftPoint", new AttributeValue().withN(Double.toString(request.getyUpperLeftPoint() - (areaSimilarity * mapHeight))));
        eav.put(":maxYUpperLeftPoint", new AttributeValue().withN(Double.toString(request.getyUpperLeftPoint() + (areaSimilarity * mapHeight))));


        eav.put(":minXStartingPoint", new AttributeValue().withN(Double.toString(request.getxStartPoint() - (startingPointSimilarity * mapWidth))));
        eav.put(":maxXStartingPoint", new AttributeValue().withN(Double.toString(request.getxStartPoint() + (startingPointSimilarity * mapWidth))));
        eav.put(":minYStartingPoint", new AttributeValue().withN(Double.toString(request.getyStartPoint() - (startingPointSimilarity * mapHeight))));
        eav.put(":maxYStartingPoint", new AttributeValue().withN(Double.toString(request.getyStartPoint() + (startingPointSimilarity * mapHeight))));


        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                .withFilterExpression(
                        "strategy = :strategy and " +
                        "hill = :hill and " +
                        "xLowerRightPoint >= :minXLowerRightPoint and xLowerRightPoint <= :maxXLowerRightPoint and " +
                        "yLowerRightPoint >= :minYLowerRightPoint and yLowerRightPoint <= :maxYLowerRightPoint and " +
                        "xUpperLeftPoint >= :minXUpperLeftPoint and xUpperLeftPoint <= :maxXUpperLeftPoint and " +
                        "yUpperLeftPoint >= :minYUpperLeftPoint and yUpperLeftPoint <= :maxYUpperLeftPoint and " +
                        "xStartPoint >= :minXStartingPoint and xStartPoint <= :maxXStartingPoint and " +
                        "yStartPoint >= :minYStartingPoint and yStartPoint <= :maxYStartingPoint")
                .withExpressionAttributeValues(eav);

        return mapper.scan(ClimbRequestCostEntry.class, scanExpression);
    }

    public static void insert(ClimbRequestCostEntry entry) {
        checkIfInit();
        mapper.save(entry);
    }
}
