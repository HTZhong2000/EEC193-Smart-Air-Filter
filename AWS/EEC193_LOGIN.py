import json
from datetime import datetime

# import the AWS SDK (for Python the package name is boto3)
import boto3
from boto3.dynamodb.conditions import Key, Attr
# import two packages to help us with dates and date formatting
from time import gmtime, strftime
import re

dynamodb = boto3.resource('dynamodb')
table1 = dynamodb.Table('EEC193_LOGIN')
table3 = dynamodb.Table('EEC193_token')
client = boto3.client('ses')

def lambda_handler(event, context):
    # TODO implement
    
    auth = 0;
    submitType = event["Type"]
    
    username = event["Username"]
    password = event["Password"]
    ip = event["IP"]
    timestamp=str(event["Timestamp"])
    
    query = table1.scan()["Items"]
    token = table3.scan()["Items"][0]["Token"]
    for item in query:
        if username == item["Username"]:
            x = item["Password"]
            auth = 1
            if password == item["Password"]:
                auth = 2
            break
        
    if submitType == 0:
        if auth == 2:
            res = "SUCCESS"
            payload = f"Your verification code is: {token}. The code will be expired in 5 minutes."
            try:
                response = client.send_email(
                    Source='meali@ucdavis.edu',
                    Destination={
                        'ToAddresses': [
                            username
                        ]
                    },
                    Message={
                        'Subject': {
                            'Data': 'EEC193 Verification Code',
                            'Charset': 'UTF-8'
                        },
                        'Body': {
                            'Text': {
                                'Data': payload,
                                'Charset': 'UTF-8'
                            }
                        }
                    }
                )
            except Exception as e:
                res = "INVALID_EMAIL"
                
        elif auth == 1:
            res = "WRONG_PASSWORD"
        else:
            res = "NOT_EXIST"
            
    elif submitType == 1:
        if auth == 0:
            res = "ACC_CREATE"
            table1.put_item(Item={
            'Username': username,
            'Password': password
            })
        else:
            res = "ACC_EXIST"
    
    return {
        'statusCode': 200,
        'body': res
    }
