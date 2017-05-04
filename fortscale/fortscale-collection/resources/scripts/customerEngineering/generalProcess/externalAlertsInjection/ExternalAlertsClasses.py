__author__ = 'idanp'

import os
import sys
import unittest
import logging
from bson.dbref import DBRef
import time
import ast
from collections import OrderedDict
from ExternalAlertInjectionProperties import Properties

sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..']))
from utils.MongoHelper import MongoRepository
from utils.UtilsClasses import Replacer
from utils.UtilsClasses import Utils


# This class is a service that responsible to generate external alerts
# Class members -
# logger -      Parent logger
# utils -       An instance of Utils class
#       properties -  Will contain the class that represent this module properties
#       mongoClient - Instance of the MongoRepository helper
#
class ExternalAlertGeneratorService(object):
    #Constructor get the logger of the parent module
    def __init__(self):
        self.logger = logging.getLogger(__name__)
        self.utils = Utils()
        #Take information from the properties file
        self.properties = Properties()
        self.mongoClient = MongoRepository(mongoHost='localhost', mongoPort=27017)

        #Dyanamic properties
        self.MATCHERS_SEPARATOR = "@@"
        self.REPLACMENT_SEPARATOR = "###"
        self.START_TIME_PATTERN = '%Y-%m-%d %H:%M:%S'
        self.END_TIME_PATTERN = '%Y-%m-%d %H:%M:%S'
        self.START_TIME_FIELD_NAME = 'Start_time'
        self.START_TIME_UNIX_FILED_NAME = 'start_time_unix'
        self.END_TIME_UNIX_FILED_NAME = 'end_time_unix'
        self.END_TIME_FILED_NAME = 'End_time'
        self.USERNAME_FILED_NAME = 'Username'
        self.ALERT_TYPE_FIELD_NAME = 'Alert_type'
        self.ALERT_DESCRIPTION_FIELD_NAME = 'Alert_description'
        self.SCORE_FIELD_NAME = 'Score'
        self.ALERT_DETAILS_FIELD_NAME = 'Alert_detail'
        self.DATA_SOURCE_FIELD_NAME = 'Data_source'
        self.CONFIGURATION_MAP = ''
        self.MANDATORY_FIELDS_LIST = []
        self.FIELD_TO_ADD_TO_ALERT_DETAIL = []
        self.MATCHERS_AS_STRING = ""


    #Thie method is for pre processing execution - override some mandatory fields from the properties class
    def preProcess(self):
        if hasattr(self.properties, 'mandatoryFieldMapping'):
            self.CONFIGURATION_MAP = self.properties.mandatoryFieldMapping
        if hasattr(self.properties, 'fieldToAddToAlertDetail'):
            self.FIELD_TO_ADD_TO_ALERT_DETAIL = self.properties.fieldToAddToAlertDetail
        if hasattr(self.properties, 'startTimeFormat'):
            self.START_TIME_PATTERN = self.properties.startTimeFormat
        if hasattr(self.properties, 'endTimeFormat'):
            self.END_TIME_PATTERN = self.properties.endTimeFormat
        if hasattr(self.properties, 'commaReplacmentValue'):
            self.MATCHERS_AS_STRING = self.properties.commaReplacmentValue

        self.overrideMandatoryFieldsNames()

    #This method is responsible to execute the core logic for external alerts injection
    def injectAlert(self, headers, line):
        RecordAsMap = self.initMapFromHeadersAndRecorrds(headers, line)
        self.generateAlertFromLine(RecordAsMap)


    #This method is override the mandatory fields naming  case its configured at the ExternalAlertInjectionProperties.py
    def overrideMandatoryFieldsNames(self):

        #In case there is a configuration for the field mapping override the globals mandatory field name variables
        if isinstance(self.CONFIGURATION_MAP, dict):
            if self.START_TIME_FIELD_NAME in self.CONFIGURATION_MAP and self.CONFIGURATION_MAP[self.START_TIME_FIELD_NAME]:
                self.START_TIME_FIELD_NAME = self.CONFIGURATION_MAP[self.START_TIME_FIELD_NAME]
                self.MANDATORY_FIELDS_LIST.append(self.START_TIME_FIELD_NAME)
            if self.END_TIME_FILED_NAME in self.CONFIGURATION_MAP and self.CONFIGURATION_MAP[self.END_TIME_FILED_NAME]:
                self.END_TIME_FILED_NAME = self.CONFIGURATION_MAP[self.END_TIME_FILED_NAME]
                self.MANDATORY_FIELDS_LIST.append(self.END_TIME_FILED_NAME)
            if self.USERNAME_FILED_NAME in self.CONFIGURATION_MAP and self.CONFIGURATION_MAP[self.USERNAME_FILED_NAME]:
                self.USERNAME_FILED_NAME = self.CONFIGURATION_MAP[self.USERNAME_FILED_NAME]
                self.MANDATORY_FIELDS_LIST.append(self.USERNAME_FILED_NAME)
            if self.ALERT_TYPE_FIELD_NAME in self.CONFIGURATION_MAP and self.CONFIGURATION_MAP[
                self.ALERT_TYPE_FIELD_NAME]:
                self.ALERT_TYPE_FIELD_NAME = self.CONFIGURATION_MAP[self.ALERT_TYPE_FIELD_NAME]
                self.MANDATORY_FIELDS_LIST.append(self.ALERT_TYPE_FIELD_NAME)
            if self.ALERT_DESCRIPTION_FIELD_NAME in self.CONFIGURATION_MAP and self.CONFIGURATION_MAP[
                self.ALERT_DESCRIPTION_FIELD_NAME]:
                self.ALERT_DESCRIPTION_FIELD_NAME = self.CONFIGURATION_MAP[self.ALERT_DESCRIPTION_FIELD_NAME]
                self.MANDATORY_FIELDS_LIST.append(self.ALERT_DESCRIPTION_FIELD_NAME)
            if self.SCORE_FIELD_NAME in self.CONFIGURATION_MAP and self.CONFIGURATION_MAP[self.SCORE_FIELD_NAME]:
                self.SCORE_FIELD_NAME = self.CONFIGURATION_MAP[self.SCORE_FIELD_NAME]
                self.MANDATORY_FIELDS_LIST.append(self.SCORE_FIELD_NAME)
            if self.ALERT_DETAILS_FIELD_NAME in self.CONFIGURATION_MAP and self.CONFIGURATION_MAP[
                self.ALERT_DETAILS_FIELD_NAME]:
                self.ALERT_DETAILS_FIELD_NAME = self.CONFIGURATION_MAP[self.ALERT_DETAILS_FIELD_NAME]
            if self.DATA_SOURCE_FIELD_NAME in self.CONFIGURATION_MAP and self.CONFIGURATION_MAP[
                self.DATA_SOURCE_FIELD_NAME]:
                self.DATA_SOURCE_FIELD_NAME = self.CONFIGURATION_MAP[self.DATA_SOURCE_FIELD_NAME]
                self.MANDATORY_FIELDS_LIST.append(self.DATA_SOURCE_FIELD_NAME)


    def validateMandatoryField(self, map):
        if not (self.START_TIME_FIELD_NAME in self.START_TIME_FIELD_NAME and map[self.START_TIME_FIELD_NAME]):
            raise ValueError("Mandatory field that represent the alert start time is missing")
        if not (self.END_TIME_FILED_NAME in self.END_TIME_FILED_NAME and map[self.END_TIME_FILED_NAME]) :
            raise ValueError("Mandatory field that represent the alert end time is missing")
        if not (self.USERNAME_FILED_NAME in self.USERNAME_FILED_NAME and map[self.USERNAME_FILED_NAME] ):
            raise ValueError("Mandatory field that represent the alert username is missing")
        if not (self.ALERT_TYPE_FIELD_NAME in self.ALERT_TYPE_FIELD_NAME and map[self.ALERT_TYPE_FIELD_NAME] ):
            raise ValueError("Mandatory field that represent the alert type filed name is missing")
        if not (self.ALERT_DESCRIPTION_FIELD_NAME in self.ALERT_DESCRIPTION_FIELD_NAME and map[
            self.ALERT_DESCRIPTION_FIELD_NAME] ):
            raise ValueError("Mandatory field that represent the alert description filed name is missing")
        if not (self.SCORE_FIELD_NAME in self.SCORE_FIELD_NAME and map[self.SCORE_FIELD_NAME] ):
            raise ValueError("Mandatory field that represent the alert score filed name is missing")
        return True


    #This method getting the input line and the header that represent each column at the line
    #And return a map of key:value  --> for each column name his value from the input line based on the order
    #All the none mandatory fields will be part of the alerts_Detail filed as key:value
    def initMapFromHeadersAndRecorrds(self, headers, line):

        try:

            replacer = Replacer(self.MATCHERS_AS_STRING, self.MATCHERS_SEPARATOR, self.REPLACMENT_SEPARATOR)

            #Handle the commas that part of the some actual values
            line = replacer.replace(line)

            headList = headers.split(',')
            lineAsList = line.split(',')


            #Validate that headers size is bigger or equal to the number of column in record
            if len(headList) < len(self.MANDATORY_FIELDS_LIST):
                raise ValueError(
                    "Cannot convert line to a map based on the header file - Header size less then number of mandatory columns in Record!")

            map = {}
            for index, header in enumerate(headList):
                lineValue = ''
                if lineAsList[index]:
                    lineValue = lineAsList[index]
                    lineValue = replacer.replaceReverse(lineValue)

                map[header] = lineValue;

            if not self.validateMandatoryField(map):
                raise ValueError("Missing mandatory field!!")


            #In case the alert detail is exist in the map switch it to be dict
            if self.ALERT_DETAILS_FIELD_NAME in map and map[self.ALERT_DETAILS_FIELD_NAME] and type(
                    map[self.ALERT_DETAILS_FIELD_NAME]) == str:
                map[self.ALERT_DETAILS_FIELD_NAME] = map[self.ALERT_DETAILS_FIELD_NAME].replace("\"", "")
                map[self.ALERT_DETAILS_FIELD_NAME] = ast.literal_eval(map[self.ALERT_DETAILS_FIELD_NAME])
            else:
                map[self.ALERT_DETAILS_FIELD_NAME] = {}


            #Iterate over the record map and put all the non mandatory fields that was configured at the ExternalAlertInjectionProperties  to be part of the alert_detail as key:value
            for key, value in map.iteritems():
                #if its mandatory field skip it
                if key in self.FIELD_TO_ADD_TO_ALERT_DETAIL and value:
                    map[self.ALERT_DETAILS_FIELD_NAME][key] = value

            return map

        except Exception as e:
            exc_type, exc_obj, exc_tb = sys.exc_info()
            raise ValueError("Cannot init the line and represent it by map with the headers", e, exc_tb.tb_lineno)


    #Generate alerts from given line represented as dict
    def generateAlertFromLine(self, recordAsMap):

        #Convert times to unix time
        try:
            start_time_unix = int(
                time.mktime(time.strptime(recordAsMap[self.START_TIME_FIELD_NAME], self.START_TIME_PATTERN)))
            recordAsMap[self.START_TIME_UNIX_FILED_NAME] = start_time_unix

            end_time_unix = int(
                time.mktime(time.strptime(recordAsMap[self.END_TIME_FILED_NAME], self.END_TIME_PATTERN)))
            recordAsMap[self.END_TIME_UNIX_FILED_NAME] = end_time_unix

        except Exception as e:
            exc_type, exc_obj, exc_tb = sys.exc_info()
            raise ValueError("Cannot convert date time to epoch time ", e, exc_tb.tb_lineno)

        try:
            #Getting the specific user document from the mongo
            userIterator = self.mongoClient.readFromMongo("user", {"username": recordAsMap[self.USERNAME_FILED_NAME]})

            if (userIterator.count() == 0 ):
                raise ValueError(
                    "User %s not found in user collection at mongo" % (recordAsMap[self.USERNAME_FILED_NAME]))

            userDoc = userIterator[0]
            userId = userDoc["_id"]
            userScore = userDoc["score"]

            if "alertsCount" in userDoc:
                userAlertsCount = userDoc["alertsCount"]
            else:
                userAlertsCount = 0
            logging.debug("Query result was: %s" % (userDoc))


            #Calculate the Alert severity based on the alert score
            logging.debug("Calculate the Alert severity based on the alert score")
            alertSevirtyAndScore = self.getAlertSevirityAndScore(int(recordAsMap[self.SCORE_FIELD_NAME]))
            sevirityValue = alertSevirtyAndScore[0]
            sevirityCode = alertSevirtyAndScore[1]
            alertScore = alertSevirtyAndScore[2]
            contributionToUserScore = alertSevirtyAndScore[3]

            #Generate the analystFeedBacks and evidences from the ALERT Detail field

            evidences = []
            logging.debug("Start working on alert creation")
            for key, value in recordAsMap[self.ALERT_DETAILS_FIELD_NAME].iteritems():

                evidenceAsaDict = EvidenceBuilder().setEntityName(recordAsMap[self.USERNAME_FILED_NAME]).setStartDate(
                    self.utils.convertToMilisec(recordAsMap[self.START_TIME_UNIX_FILED_NAME])).setEndDate(
                    self.utils.convertToMilisec(recordAsMap[self.END_TIME_UNIX_FILED_NAME])).setAnomalyTypeFieldName(
                    key).setAnomalyValue(value).setDataEntitiesIds(
                    recordAsMap[self.DATA_SOURCE_FIELD_NAME].upper()).setScore(alertScore).setSeverity(
                    sevirityValue).evidence()


                self.logger.debug("New Evidence is - id: %s , %s" % (id(evidenceAsaDict),evidenceAsaDict))


                evidences.append(evidenceAsaDict)

            self.logger.debug("Evidence list is - %s" % str(evidences))


            #Save evidences to mongo
            evidencesId = []
            evidencesToSave = []
            dbRefs = []

            #If there is evidences to attached to the alert
            if (len(evidences) > 0):
                #For each evidence try to see if it's already exist on Mongo based on the unique key if yes just take his ID and refer it to the alert if not save new evidence to mongo
                for evidence in evidences:

                    where = {}
                    where["startDate"]=evidence["startDate"]
                    where["endDate"]=evidence["endDate"]
                    where["entityType"]=evidence["entityType"]
                    where["entityName"]=evidence["entityName"]
                    where["anomalyTypeFieldName"]=evidence["anomalyTypeFieldName"]
                    where["anomalyValue"]=evidence["anomalyValue"]


                    evidenceResult = self.mongoClient.readFromMongo("evidences", where)

                    if (evidenceResult.count() > 0):
                        self.logger.debug("Going to use existing evidence - %s" % evidenceResult[0])
                        evidencesId.append(evidenceResult[0]["_id"])
                    else:
                        self.logger.debug("Going to add the evidence to the list for saving - %s" % evidence)
                        evidencesToSave.append(evidence)

                evidencesId = self.mongoClient.saveToMongo("evidences", evidencesToSave)
                for evidenceId in evidencesId:
                    dbRefs.append(DBRef(collection="evidences", id=evidenceId))



            #Alert generation
            alertsBuilder = AlertsBuilder()

            alertAsDict = alertsBuilder.setEvidences(dbRefs).setName(
                recordAsMap[self.ALERT_TYPE_FIELD_NAME]).setStartDate(
                self.utils.convertToMilisec(recordAsMap[self.START_TIME_UNIX_FILED_NAME])).setEndDate(
                self.utils.convertToMilisec(recordAsMap[self.END_TIME_UNIX_FILED_NAME])).setEntityName(
                recordAsMap[self.USERNAME_FILED_NAME]).setEntityId(str(userId)).setNumOfIndicators(
                len(dbRefs)).setAlertScore(alertScore).setSeverityCode(sevirityCode).setSeverity(
                sevirityValue).setUserScoreContribution(contributionToUserScore).setanomalyTypes(evidences, recordAsMap[
                self.DATA_SOURCE_FIELD_NAME].upper()).alert()

            alerts = [alertAsDict]

            #If the alert was successfully saved then update the user score
            if self.mongoClient.saveToMongo("alerts", alerts):
                userScore = userScore + contributionToUserScore
                try:
                    mongoUserCollection = self.mongoClient.getMongoCollection("user")
                    mongoUserCollection.update({"_id": userId},
                                               {"$set": {"score": userScore, "alertsCount": userAlertsCount + 1}})
                except Exception as e:
                    raise ValueError("Error update the user score %s" % userId, e)


        except Exception as e:
            exc_type, exc_obj, exc_tb = sys.exc_info()
            raise ValueError(e, exc_tb.tb_lineno)


    def getAlertSevirityAndScore(self, score):
        if score <= 5: return ["Low", 3, 50, 5]
        if score <= 10: return ["Medium", 2, 70, 10]
        if score <= 15: return ["High", 1, 80, 15]
        return ["Critical", 0, 95, 20]




class EvidenceBuilder(object):
    evidenceAsaDict = {}

    def __init__(self):
        self.evidenceAsaDict["_class"] = "fortscale.domain.core.Evidence"
        self.evidenceAsaDict["entityType"] = "User"
        self.evidenceAsaDict["entityTypeFieldName"] = "normalized_username"
        self.evidenceAsaDict["evidenceType"] = "AnomalySingleEvent"
        self.evidenceAsaDict["numOfEvents"] = 1


    def setEntityName(self, entityName):
        self.evidenceAsaDict["entityName"] = entityName
        return self

    def setStartDate(self, startDate):
        self.evidenceAsaDict["startDate"] = startDate
        return self

    def setEndDate(self, endDate):
        self.evidenceAsaDict["endDate"] = endDate
        return self

    def setAnomalyTypeFieldName(self, anomalyTypeFieldName):
        self.evidenceAsaDict["anomalyTypeFieldName"] = anomalyTypeFieldName
        return self

    def setAnomalyValue(self, anomalyValue):
        self.evidenceAsaDict["anomalyValue"] = anomalyValue
        return self

    def setDataEntitiesIds(self, dataEntitiesId):
        dataEntitiesIds = []
        dataEntitiesIds.append(dataEntitiesId)
        self.evidenceAsaDict["dataEntitiesIds"] = dataEntitiesIds
        return self

    def setScore(self, score):
        self.evidenceAsaDict["score"] = score
        return self

    def setSeverity(self, severity):
        self.evidenceAsaDict["severity"] = severity
        return self

    def evidence(self):
        clonedDict={}
        for key in self.evidenceAsaDict:
            clonedDict[key] = self.evidenceAsaDict[key]
        return clonedDict



    def __str__(self):
        return "_class : "+self.evidenceAsaDict["_class"]+"\n"+ \
               "entityType : "+self.evidenceAsaDict["entityType"]+"\n"+ \
               "entityTypeFieldName : "+self.evidenceAsaDict["entityTypeFieldName"]+"\n"+ \
               "evidenceType : "+self.evidenceAsaDict["evidenceType"]+"\n"+ \
               "numOfEvents : "+str(self.evidenceAsaDict["numOfEvents"])+"\n"+ \
               "entityName : "+self.evidenceAsaDict["entityName"]+"\n"+ \
               "startDate : "+str(self.evidenceAsaDict["startDate"])+"\n"+ \
               "endDate : "+str(self.evidenceAsaDict["endDate"])+"\n"+ \
               "anomalyTypeFieldName : "+self.evidenceAsaDict["anomalyTypeFieldName"]+"\n"+ \
               "anomalyValue : "+self.evidenceAsaDict["anomalyValue"]+"\n"+ \
               "dataEntitiesIds : "+str(self.evidenceAsaDict["dataEntitiesIds"])+"\n"+ \
               "score : "+self.evidenceAsaDict["score"]+"\n"+ \
               "severity : "+self.evidenceAsaDict["severity"]+"\n"





class AlertsBuilder(object):
    alertAsDict = {}

    def __init__(self):
        self.alertAsDict["_class"] = "fortscale.domain.core.Alert"
        self.alertAsDict["entityType"] = "User"
        self.alertAsDict["status"] = "Open"
        self.alertAsDict["feedback"] = "None"
        self.alertAsDict["userScoreContributionFlag"] = "true"
        self.alertAsDict["timeframe"] = "Daily"
        self.alertAsDict["analystFeedback"] = []

    def setEvidences(self, evidences):
        self.alertAsDict["evidences"] = evidences
        return self

    def setName(self, name):
        self.alertAsDict["name"] = name
        return self

    def setStartDate(self, startDate):
        self.alertAsDict["startDate"] = startDate
        return self

    def setEndDate(self, endDate):
        self.alertAsDict["endDate"] = endDate
        return self

    def setEntityName(self, entityName):
        self.alertAsDict["entityName"] = entityName
        return self

    def setEntityId(self, entityId):
        self.alertAsDict["entityId"] = entityId
        return self

    def setNumOfIndicators(self, num):
        self.alertAsDict["indicatorsNum"] = num
        return self

    def setAlertScore(self, score):
        self.alertAsDict["score"] = score
        return self

    def setSeverityCode(self, severityCode):
        self.alertAsDict["severityCode"] = severityCode
        return self

    def setSeverity(self, severity):
        self.alertAsDict["severity"] = severity
        return self

    def setUserScoreContribution(self, userScoreContribution):
        self.alertAsDict["userScoreContribution"] = userScoreContribution
        return self

    def setanomalyTypes(self, evidences, dataSource):
        anomalyTypes = []
        for evidence in evidences:
            anomalyType = OrderedDict()
            anomalyType["dataSource"] = dataSource
            anomalyType["anomalyType"] = "Indicators"
            anomalyTypes.append(anomalyType)
        self.alertAsDict["anomalyTypes"] = anomalyTypes
        return self

    def alert(self):
        clonedDict={}
        for key in self.alertAsDict:
            clonedDict[key]=self.alertAsDict[key]
        return clonedDict


#This is the unit test class for testing this module
class TestExternalAlertGeneratorServiceMethods(unittest.TestCase):
    #Unit test for testing the converting string to dict method
    def test_overrideMandatoryFieldsNames(self):
        overrideConfigurationMapping = {"Start_time": "time", "End_time": "end_time", "Username": "user_id",
                                        "Alert_type": "description", "Alert_description": "description",
                                        "Score": "threat_score", "Alert_detail": "detail_json",
                                        "Data_source": "splunk_index"}
        self.CONFIGURATION_MAP = overrideConfigurationMapping


if __name__ == "__main__":
    unittest.main()



