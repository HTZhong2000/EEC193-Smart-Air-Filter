import json
from datetime import datetime

# import the AWS SDK (for Python the package name is boto3)
import boto3
from boto3.dynamodb.conditions import Key, Attr
# import two packages to help us with dates and date formatting
from time import gmtime, strftime
import re

dynamodb = boto3.resource('dynamodb')
table2 = dynamodb.Table('EEC193_LOGIN_REC')
table3 = dynamodb.Table('EEC193_token')

def lambda_handler(event, context):
    token = event["Token"]
    ip = event["IP"]
    timestamp = event["Timestamp"]
    username = event["Username"]
    
    if token == table3.scan()["Items"][0]["Token"]:
        res = "SUCCESS"
        table2.put_item(Item={
            'IP': ip,
            'Time': str(timestamp),
            'Username': username
        })
    else:
        res = "WRONG_TOKEN"
    return {
        'statusCode': 200,
        'body': res
    }
