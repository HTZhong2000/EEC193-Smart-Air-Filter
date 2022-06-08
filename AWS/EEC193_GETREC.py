import json

# import the AWS SDK (for Python the package name is boto3)
import boto3
from boto3.dynamodb.conditions import Key, Attr
# import two packages to help us with dates and date formatting
from time import gmtime, strftime
import dateutil.tz as tz
from datetime import datetime, timedelta, timezone
import re

dynamodb = boto3.resource('dynamodb')
table = dynamodb.Table('EEC193_Record')


    
def lambda_handler(event, context):
    # TODO implement
    time_local=datetime.now().astimezone(tz.gettz('US/Pacific'))
    response=[]
    device=event["Device"]
    ttype=event["Ttype"]
    objlist = table.query(
              ScanIndexForward = False,
              KeyConditionExpression= Key('DeviceTag').eq(device)
           )
    if ttype=='one':
        for item in objlist['Items']:
            itemtime=datetime.fromtimestamp(item['UpdateTime']).astimezone(tz.gettz('US/Pacific'))
            dayc=(time_local-itemtime).days
            if dayc==0:
                response.append(item)
            
        
    return {
        'statusCode': 200,
        'body': objlist
    }
