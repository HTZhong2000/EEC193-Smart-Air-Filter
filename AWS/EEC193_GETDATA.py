import json

# import the AWS SDK (for Python the package name is boto3)
import boto3
from boto3.dynamodb.conditions import Key, Attr
# import two packages to help us with dates and date formatting
from time import gmtime, strftime,time
import re

dynamodb = boto3.resource('dynamodb')
table = dynamodb.Table('EEC193_data')


    
def lambda_handler(event, context):
    # TODO implement
    
   # query = table.scan()
    response = table.query(
              Limit = 1,
              ScanIndexForward = False,
              KeyConditionExpression= Key('Device').eq('test') & Key('Current_state').eq(1)
           )
    response["Items"][0]["Current_time"]=int(time())    
    return {
        'statusCode': 200,
        'body': response["Items"][0]
    }
