import json
import time
import dateutil.tz as tz
from time import gmtime, strftime, time
from datetime import datetime, timedelta, timezone

time_local=datetime.now().astimezone(tz.gettz('US/Pacific'))
# import the AWS SDK (for Python the package name is boto3)
import boto3
from boto3.dynamodb.conditions import Key, Attr
# import two packages to help us with dates and date formatting
from time import gmtime, strftime, time
import re

dynamodb = boto3.resource('dynamodb')
table1 = dynamodb.Table('EEC193_data')
table2 = dynamodb.Table('EEC193_Record')


    
def lambda_handler(event, context):
    # TODO implement
    query = table1.scan()
    parse = query['Items']
    for items in parse:
        id = items["Device"]
        fan = items["Fan"]
        ts1=items["Current_time"]
        ts = int(time())
        tmpF = items["TemperatureF"]
        tmpC = items["TemperatureC"]
        humid = items["Humidity"]
        PM25 = items["PM25"]
        PM10 = items["PM10"]
        PIR = items["PIR"]
        if ts-ts1<30:
            table2.put_item(Item = {
                'DeviceTag': id,
                'fan': fan,
                'pir': PIR,
                'UpdateTime': ts,
                'tmpf': tmpF,
                'tmpc': tmpC,
                'humid': humid,
                'PM25':PM25,
                'PM10':PM10
            })

    
    return {
        'statusCode': 200,
        'body': "123123"
            
    }
