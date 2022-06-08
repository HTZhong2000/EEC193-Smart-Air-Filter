import json
import time
import dateutil.tz as tz
from time import gmtime, strftime, time
from datetime import datetime, timedelta, timezone

# import the AWS SDK (for Python the package name is boto3)
import boto3
from boto3.dynamodb.conditions import Key, Attr
# import two packages to help us with dates and date formatting
from time import gmtime, strftime, time
import re

dynamodb = boto3.resource('dynamodb')
table1 = dynamodb.Table('EEC193_data')
table2 = dynamodb.Table('EEC193_plan')


    
def lambda_handler(event, context):
    de="test"
    query1 = table1.scan()
    json1 = query1['Items'][0]
    query2 = table2.scan()
    json2 = query2['Items'][0]
    thresh10=json2["Threshold_pm10"]
    thresh25=json2["Threshold_pm25"]
    pm10=json1["PM10"]
    pm25=json1["PM25"]
    if pm10>thresh10 or pm25>thresh25:
        table1.update_item(
            Key={
                "Device": "test",
                "Current_state": 1,
            },
            UpdateExpression= "SET Fan = :newfan",
            ExpressionAttributeValues={
                ':newfan': "1"
            },
            ReturnValues="UPDATED_NEW"
        )
    elif pm10<=thresh10 and pm25<=thresh25:
        table1.update_item(
            Key={
                "Device": "test",
                "Current_state": 1,
            },
            UpdateExpression= "SET Fan = :newfan",
            ExpressionAttributeValues={
                ':newfan': "0"
            },
            ReturnValues="UPDATED_NEW"
        )
    # TODO implement
    return {
        'statusCode': 200,
        'body': "0"
    }
