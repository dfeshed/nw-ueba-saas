#!/usr/bin/python
import os
import datetime
from pymongo import MongoClient
import logging

ABOUT_TO_LEAVE_TAG="About_To_Leave"
TERMINATED_TAG="Terminated"
REHIRE_REVIEW_STATUS="Review"
REHIRE_REVIEW_TAG="Rehire-Review"
REHIRE_NO_STATUS="No"
REHIRE_NO_TAG="Rehire-No"
DIRECTORY='/home/termdata/logs/'
COMPLETED_DIRECTORY='/home/termdata/processed/'
CUSTOM_TAGS_CONFIGURATION_FILE='/home/cloudera/fortscale/fortscale-core/fortscale/fortscale-collection/target/resources/lists/userCustomTags.csv'
LOG_FILE='/home/cloudera/fortscale/fortscale-core/fortscale/fortscale-collection/target/termDataLogFile.log'

logging.basicConfig(filename=LOG_FILE,format='%(asctime)s %(levelname)s %(message)s', level=logging.DEBUG)

def applyCustomTag(username, tag, forRemoval=False) :

    writeFlag=False
    # Go to read the user from mongo and get his tags list
    try :
        users = mongoUserCollection.find({"username":username})
        for user in users:
            tags = user["tags"]
            #logging.debug("User : %s  have the tags: %s" %(username,','.join(tags))
    except Exception as e:
        logging.error("Could not get the user: %s document from mongo" %(username))

    #In case this is remove request
    if forRemoval is True:
        logging.info("Going to remove tag %s from user %s" %(tag, username))
        if tag in tags:
            logging.debug("Tag %s was exist on the tags list for user %s and are about to remove!!" %(tag,username))
            tags.remove(tag)
            writeFlag=True
    #In case its tag addition request
    else:
        logging.info("Going to add tag %s to user %s" %(tag, username))
        if tag not in tags :
            logging.debug("Tag %s was not exist on the tags list for user %s and are about to add!!" %(tag,username))
            tags.append(tag)
            writeFlag=True

    #Update the user tag list
    if writeFlag is True :
        try :
            mongoUserCollection.update({"username":username},{"$set":{"tags":tags}})
        except Exception as e:
            logging.error("Could not remove tag: %s for user:  %s" %(tag,username))
            logging.error(e)

def getTermFiles() :
    mtime = lambda f: os.stat(os.path.join(DIRECTORY, f)).st_mtime
    return list(sorted(os.listdir(DIRECTORY), key=mtime))


def processTermDataFiles() :
    # Go over all relevant files under the configured directory
    logging.info("Started processing new termdata files")
    for termFile in getTermFiles() :
        termFilePath=DIRECTORY+termFile
        logging.info("Processing file %s" %(termFilePath))

        # Go over each record in file to apply its termination data
        for line in sorted(set(open(termFilePath,'r').readlines()[2:])) :

            if "INNER JOIN" in line:
                continue
            # Parse the record into fields
            try :
                (employeeID, username, terminationDateString, lastWorkDate, rehireStatus, originalHireDate, EmployeeRehireDateString) = line.rstrip().split(",")
                if username == 'None': continue
                terminationDate = datetime.datetime.strptime(terminationDateString, "%Y-%m-%d").date()
                terminationDateTime = datetime.datetime(terminationDate.year, terminationDate.month, terminationDate.day, 23, 59, 59)
                logging.debug("New User: %s,%s,%s,%s,%s,%s,%s" %(employeeID, username, terminationDateString, lastWorkDate, rehireStatus, originalHireDate, EmployeeRehireDateString))
            except Exception as e :
                logging.error("Could not parse line: %s" %(line))
                logging.error(e)
                continue

            # Ignore this user record if its Rehire data is later than its Termination date
            try :
                if EmployeeRehireDateString=="UNKNOWN" : continue
                EmployeeRehireDate = datetime.datetime.strptime(EmployeeRehireDateString, "%Y-%m-%d").date()
                if EmployeeRehireDate >= terminationDate:
                    logging.info("User was rehired after termination, No update will be done. User: %s, Termination Date: %s, Rehire Date: %s." %(username, terminationDateString, EmployeeRehireDateString))
                    continue
            except Exception as e:
                logging.error("Could not compare Termination and Rehire dates for username: %s" %(username))
                logging.error(e)


            # Update the termination date in Mongo user collection
            try :
                logging.info("Updating termination date. User: %s, Date: %s." %(username, terminationDateString))
                full_username = mongoUserCollection.find({"adInfo.sAMAccountName": username},{"username" :1})[0]["username"]
                mongoUserCollection.update({"username": full_username},{"$set": {"adInfo.terminationDate": terminationDateTime}})
                mongoUserCollection.update({"username": full_username},{"$set": {"terminationDate": terminationDateTime}})
            except Exception as e:
                logging.error("Could not update termination date correctly. Username: %s, Date: %s." %(username, terminationDateString))
                logging.error(e)
                continue

            # Update 'Terminated' / 'About to Leave' tags
            try :
                today = datetime.date.today()
                tag = ABOUT_TO_LEAVE_TAG if terminationDate >= today else TERMINATED_TAG
                applyCustomTag(full_username,tag)
            except Exception as e:
                logging.error("Could not update Terminated/About to Leave tags for username: %s" %(username))
                logging.error(e)
                continue

            # Update 'Rehire-No/Review' tags
            try :
                logging.debug("REHIRE STATUS IS: '%s'" %rehireStatus)
                if rehireStatus == REHIRE_REVIEW_STATUS :
                    applyCustomTag(full_username,REHIRE_REVIEW_TAG)
                if rehireStatus == REHIRE_NO_STATUS :
                    applyCustomTag(full_username,REHIRE_NO_TAG)
            except Exception as e:
                logging.error("Could not update Rehire tags for username: %s" %(username))
                logging.error(e)
                continue

        # Mark file as processed to avoid future parsing
        try :
            termFileProcessedPath = COMPLETED_DIRECTORY+termFile
            os.rename(termFilePath,termFileProcessedPath)
        except Exception as e :
            logging.error("Could not archive processed file %s to %s" %(termFilePath, termFileProcessedPath))
            logging.error(e)
            continue
    logging.info("Finished processing new termdata files")


# Go over all of the 'About to Leave' users, to see if their termination date has passed
# and their status should be changed to 'Terminated'
def updateAboutToLeaveUsers() :
    logging.info("Looking for all 'About To Leave' users")
    try :
        for user in mongoUserCollection.find({"tags":ABOUT_TO_LEAVE_TAG}):
            try :
                full_username = user["username"]
                terminationDate = user["terminationDate"].date()
                today = datetime.date.today()
                if today > terminationDate :
                    logging.info("Termination date has passed. User: %s, Termination Date: %s. Updating 'About To Leave' tag to 'Terminated'" %(full_username, terminationDate))
                    applyCustomTag(full_username,ABOUT_TO_LEAVE_TAG,forRemoval=True)
                    applyCustomTag(full_username,TERMINATED_TAG)
            except Exception as e :
                logging.error("Could not update 'About to Leave' tag to 'Terminated'. User: %s, Termination Date: %s" %(full_username, terminationDate))
                logging.error(e)
                continue
    except Exception as e :
        logging.error("Error while trying to update 'About To Leave' users")
        logging.error(e)
    logging.info("Finished updating all 'About To Leave' users")

logging.info('--------------------------------------------')
logging.info('Termination Data Updater script began to run')
# Prepare the custom tag file for updating
customTagFile = open(CUSTOM_TAGS_CONFIGURATION_FILE,'a')

# Prepare mongo client connection to 'user' collection
mongoClient = MongoClient('localhost', 27017)
mongoUserCollection = mongoClient.fortscale.user

#Process the new term data files
processTermDataFiles()

# Update status from 'About to Leave' to 'Terminated' (if needed)
updateAboutToLeaveUsers()

mongoClient.close()
customTagFile.close()

logging.info('Termination Data Updater script completed successfully')
logging.info('------------------------------------------------------')
