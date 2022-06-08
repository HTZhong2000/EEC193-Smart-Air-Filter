import json
from datetime import datetime

# import the AWS SDK (for Python the package name is boto3)
import boto3
from boto3.dynamodb.conditions import Key, Attr
# import two packages to help us with dates and date formatting
from time import gmtime, strftime
import re

dynamodb = boto3.resource('dynamodb')
table1 = dynamodb.Table('EEC193_data')
table2 = dynamodb.Table('EEC193_plan')
client = boto3.client('events')

def lambda_handler(event, context):
    
    device=event["Device"]
    state = event["State"]
    type = event["Type"]
    threshpm10=event["Threshold_pm10"]
    threshpm25=event["Threshold_pm25"]
    starttime=event["Start_time"]
    endtime=event["End_time"]
    
    table2.update_item(
            Key={
                "Device": device,
            },
            UpdateExpression= "SET Statec = :newstate, Typec = :newtype, Threshold_pm10= :newthreshold_pm10, Threshold_pm25= :newthreshold_pm25, Start_time= :newstart, End_time= :newend",
            ExpressionAttributeValues={
                ':newstate': state,
                ':newtype': type,
                ':newthreshold_pm10': threshpm10,
                ':newthreshold_pm25': threshpm25,
                ':newstart': starttime,
                ':newend': endtime
            },
            ReturnValues="UPDATED_NEW"
    )
    if state=="1":
        if type=="thresh":
            response = client.enable_rule(
                Name='UPDATE_FAST',
            )
        if type=="schedule":
            expstart='cron(0 {hour} * * ? *)'.format(hour=starttime)
            expend='cron(0 {hour} * * ? *)'.format(hour=endtime)
            response = client.put_rule(
                Name='FanOn',
                ScheduleExpression=expstart
            )
            response = client.put_rule(
                Name='FanOff',
                ScheduleExpression=expend
            )
            response = client.enable_rule(
                Name='UPDATE_FAST'
            )
            client.enable_rule(
                Name='FanOn'
            )
            client.enable_rule(
                Name='FanOff'
            )
        else:
            response="None"
    elif state=="0":
        response = client.disable_rule(
                Name='UPDATE_FAST'
            )
        client.disable_rule(
                Name='FanOn'
            )
        client.disable_rule(
                Name='FanOff'
            )
    else:
        response="Invalid"
    return {
        'statusCode': 200,
        'body': response
    }
