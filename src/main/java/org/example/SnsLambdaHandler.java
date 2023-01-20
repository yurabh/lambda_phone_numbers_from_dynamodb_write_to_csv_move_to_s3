package org.example;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SNSEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.model.PhoneNumber;

import java.util.Objects;

import static org.example.utils.LambdaUtils.CREDENTIALS;

public class SnsLambdaHandler implements RequestHandler<SNSEvent, Object> {

    private static final Logger LOGGER = LogManager.getLogger(SnsLambdaHandler.class);

    private static final String MESSAGE = "Read phones number from Dynamodb";

    private static final String PHONE_NUMBER = "phoneNumber";

    private static final String TABLE_NAME = "phone_numbers";

    private static final String MESSAGE_FUNCTION_FAILED_RESPONSE = "Message is empty or null";

    private static final String MESSAGE_FUNCTION_SUCCESS_RESPONSE = "Function executed successfully";

    private static final String HASH_KEY_NAME = "Id";

    private static final AmazonDynamoDB amazonDynamoDB = AmazonDynamoDBClientBuilder
            .standard()
            .withRegion(Regions.US_EAST_1)
            .withCredentials(new AWSStaticCredentialsProvider(CREDENTIALS))
            .build();

    private static final DynamoDB db = new DynamoDB(amazonDynamoDB);

    @Override
    public String handleRequest(SNSEvent snsEvent, Context context) {
        String message = snsEvent.getRecords().get(0).getSNS().getMessage();
        if (!Objects.nonNull(message)) {
            LOGGER.error(MESSAGE_FUNCTION_FAILED_RESPONSE);
            return MESSAGE_FUNCTION_FAILED_RESPONSE;
        } else if (message.equals(MESSAGE)) {
            PhoneNumber phoneNumber = addPhoneNumbers();

            return MESSAGE_FUNCTION_SUCCESS_RESPONSE;
        }
        return MESSAGE_FUNCTION_FAILED_RESPONSE;
    }

    private static PhoneNumber addPhoneNumbers() {
        PhoneNumber phoneNumbers = new PhoneNumber();
        int countItemsInDynamoDb = countItemsInDynamoDb();
        if (countItemsInDynamoDb != 0) {
            for (int i = 0; i < countItemsInDynamoDb; i++) {
                Integer phoneNumberFromDb = getPhoneNumber(i);
                if (Objects.nonNull(phoneNumberFromDb)) {
                    phoneNumbers.addNumber(phoneNumberFromDb);
                }
            }
        }
        return phoneNumbers;
    }

    private static Integer getPhoneNumber(int key) {
        Table table = db.getTable(TABLE_NAME);
        GetItemSpec phoneNumberItemSpec = new GetItemSpec().withPrimaryKey(HASH_KEY_NAME, key);
        Item item = table.getItem(phoneNumberItemSpec);
        return retrievePhoneNumber(item);
    }

    private static Integer retrievePhoneNumber(Item item) {
        if (Objects.nonNull(item)) {
            return Integer.valueOf(item.get(PHONE_NUMBER).toString());
        }
        return null;
    }

    private static int countItemsInDynamoDb() {
        Long itemCounts = db.getTable(TABLE_NAME).describe().getItemCount();
        return itemCounts.intValue();
    }
}
