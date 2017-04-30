__author__ = 'idanp'

from pymongo import MongoClient
from UtilsClasses import Utils
import logging

import sys

# This class is mongo repository helper
class MongoRepository(object):
    #Constructor that will get the mongo host and port and the logger
    def __init__(self, mongoHost, mongoPort):
        self.mongoHost = mongoHost
        self.mongoPort = mongoPort
        self.mongoClient = MongoClient(self.mongoHost, self.mongoPort)
        self.logger = logging.getLogger(__name__)
        self.utils = Utils()


    #Get the mongo repository instance
    def getRepository(self):
        if self.mongoClient:
            return self.mongoClient

    #Get the mongo collection helper
    def getMongoCollection(self, collectionName):
        return self.mongoClient.fortscale[collectionName]

    #Save to Mongo Method
    #Input -
    #       collection - The collection to save for
    #       documentsList - List of document to save
    #Retunr value - List of Ids the represent the documents that were inserted
    def saveToMongo(self, collection, documentsList):

        ids = []

        try:

            mongoCollection = self.getMongoCollection(collection)
            self.logger.debug("Going to save Documents to mongo at collection: %s " % (collection))

            for doc in documentsList:
                result = mongoCollection.save(doc)
                if result:
                    self.logger.debug("Document with id %s inserted to collection %s " % (result, collection))
                    ids.append(result)
            return ids

        except Exception as e:
            if isinstance(e, DuplicateKeyError):
                self.logger.warn("Record not saved to Mongo - %s" % e)
            else:
                exc_type, exc_obj, exc_tb = sys.exc_info()
                raise ValueError("Error saving to mongo", e, exc_tb.tb_lineno)


    #Read from mongo method - This method is a helper method for reading from mongo
    #Input -
    #       collection - The collection to read from
    #       where - String that represent where filter over mongo i.e - 'key1:val1,key2:val2'
    #       projection - String that represent the projection clause in mongo  'key1:1'
    def readFromMongo(self, collection, where={}, projection={}):
        try:
            #whereDict = self.utils.convertStringToDict(where, extSpliter,inlineSpliter)
            #projectionDict = self.utils.convertStringToDict(projection, extSpliter,inlineSpliter)

            mongoCollection = self.getMongoCollection(collection)

            query = str(where) + "," + str(projection)
            self.logger.debug("Going to execute the query : %s on %s collection" % (query, collection))

            if len(projection) > 0:
                res = mongoCollection.find(where, projection)
            else:
                res = mongoCollection.find(where)

            return res
        except Exception as e:
            exc_type, exc_obj, exc_tb = sys.exc_info()
            raise ValueError("Error retrieving from mongo", e, exc_tb.tb_lineno)




