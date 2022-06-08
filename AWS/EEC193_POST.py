import json

# import the AWS SDK (for Python the package name is boto3)
import boto3
from boto3.dynamodb.conditions import Key, Attr
# import two packages to help us with dates and date formatting
from time import gmtime, strftime, time
import re

dynamodb = boto3.resource('dynamodb')
table = dynamodb.Table('EEC193_data')


    
def lambda_handler(event, context):
    # TODO implement
    ctrl="0"
    if "ctrlMode" in event:
        ctrl=event["ctrlMode"]
    fan = event["Fan"]
    device = event["Device"]
    timestamp = event["Timestamp"]
    client = event["Client"]
    tmpF = event["tmpF"]
    tmpC = event["tmpC"]
    humid = event["humid"]
    PM25 = event["PM25"]
    PM10 = event["PM10"]
    PIR = event["PIR"]
    
    query = table.scan()
    response1 = query['Items'][-1]
    
    if client == "Web":
        response = response1
        table.update_item(
            Key={
                "Device": "test",
                "Current_state": 1,
            },
            UpdateExpression= "SET Fan = :newfan, Current_time = :newtime, ctrlMode = :newMode",
            ExpressionAttributeValues={
                ':newfan': fan,
                ':newtime': timestamp,
                ':newMode': '1'
            },
            ReturnValues="UPDATED_NEW"
            )
        
            
    elif client == "MCU":
            if ctrl=="0":
                response = response1["Fan"]
                table.update_item(
                Key={
                    "Device": "test",
                    "Current_state": 1,
                },
                UpdateExpression= "SET Current_time = :newtime, Humidity = :newhumid, PIR = :newPIR, PM10 = :newPM10, PM25 = :newPM25, TemperatureC = :newtmpC, TemperatureF = :newtmpF, ctrlMode = :newMode",
                ExpressionAttributeValues={
                    ':newtime': int(time()),
                    ':newhumid': humid,
                    ':newPIR': PIR,
                    ':newPM10': PM10,
                    ':newPM25': PM25,
                    ':newtmpC': tmpC,
                    ':newtmpF': tmpF,
                    ':newMode': '0' 
                },
                ReturnValues="UPDATED_NEW"
                )
                
            else:
                response = fan
                table.update_item(
                Key={
                    "Device": "test",
                    "Current_state": 1,
                },
                UpdateExpression= "SET Fan = :newfan, Current_time = :newtime, Humidity = :newhumid, PIR = :newPIR, PM10 = :newPM10, PM25 = :newPM25, TemperatureC = :newtmpC, TemperatureF = :newtmpF, ctrlMode = :newMode",
                ExpressionAttributeValues={
                    ':newfan': fan,
                    ':newtime': int(time()),
                    ':newhumid': humid,
                    ':newPIR': PIR,
                    ':newPM10': PM10,
                    ':newPM25': PM25,
                    ':newtmpC': tmpC,
                    ':newtmpF': tmpF,
                    ':newMode': '0' 
                },
                ReturnValues="UPDATED_NEW"
                )
                
   
    else:
        response = "Unknown client type"

        

    return {
        'statusCode': 200,
        'body': response
            
    }
