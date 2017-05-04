__author__ = 'idanp'
__Note__ = 'Support python 2.6 and above'

import os
from os.path import isfile, join
import logging
import sys
import argparse

from ExternalAlertsClasses import ExternalAlertGeneratorService

sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..']))
from utils.UtilsClasses import Utils



#Statics Final properties
DEFAULT_INPUT_FOLDER_PATH='/home/cloudera/fortscale/externalAlerts/'
FINISH_FOLDER_PATH='/home/cloudera/fortscale/externalAlerts/finish/'
LOG_PATH='/home/cloudera/fortscale/fortscale-core/fortscale/fortscale-collection/target/externalAlertInjection.log'
LOG_LEVEL=logging.INFO
utils = Utils()
externalAlertsInjectionService=ExternalAlertGeneratorService()



#This is the Main function
def main(argv):

    #retrive the input params
    input_file_path= ''
    global LOG_LEVEL
    try:
        parser = argparse.ArgumentParser(description=getProgDescription())
        parser.add_argument("--filesPath", help="Will represent the path that contain the source files for alerts generation",action="store_true")
        parser.add_argument("-d", help="Flag that will change the log level to debug",action="store_true")
        args = parser.parse_args()

        if args.d :
            LOG_LEVEL=logging.DEBUG
        if args.filesPath :
            input_file_path = args.filesPath


    except Exception as e:
        logging.error("Exception while trying to parse the input parameters")
        logging.error(e)
        sys.exit(2)



    #TODO - SOLVE THE LOGGING NAMES BETWEEN THE MODULES
    #logging.basicConfig(filename=LOG_PATH,format='%(asctime)s - %(name)s - %(levelname)s %(message)s', level=LOG_LEVEL)
    logging.basicConfig(filename=LOG_PATH,format='%(asctime)s - %(levelname)s %(message)s', level=LOG_LEVEL)
    logger = logging.getLogger(__name__)



    externalAlertsInjectionService.preProcess()
    processFiles(input_file_path)


def getProgDescription():
    return 'External Alerts Injection process that enable to inject external alerts to Fortscale system'+ \
           'This python process is responsible to external alerts injection' + \
           'It will take a given path to the files that contain the external alerts or use the %s as default path' %DEFAULT_INPUT_FOLDER_PATH + \
           'And for each file in the path it will convert the lines into alerts and inject them to the Fortscale Alerts collection' + \
           'It is also enable to add override the properties like "stat time" or "end time" pattern and the "mandatory filed mapping" and "field to add to alertDetail" at the ExternalAlertInjectionProperties file '



#This method will process each file in the input file path
def  processFiles(input_file_path):

    logging.info("")
    logging.info("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@")
    logging.info("Start processing external alerts injection")

    if not input_file_path:
        input_file_path = DEFAULT_INPUT_FOLDER_PATH
    listOfFiles = getExternalAlertsFiles(input_file_path)

    if len(listOfFiles) == 0:
        logging.info("No Files for processing - DONE")

    #print(listOfFiles)
    for file in listOfFiles:
        externalAlertFile=input_file_path+file
        logging.info("################################################")
        logging.info("Processing file %s" %(externalAlertFile))

        processFile(externalAlertFile)

        #Move file to finish folder
        if not os.path.exists(FINISH_FOLDER_PATH):
            os.makedirs(FINISH_FOLDER_PATH)
        os.rename(externalAlertFile, FINISH_FOLDER_PATH+file)



#Thie method will process single file
def processFile(externalAlertFile):

    #Open the current file
    with open(externalAlertFile,'r') as f:
        #Get the header
        headers = f.readline().rstrip()

        #For each line map it's columns to the headers based on the order
        for index,line in enumerate([x.strip() for x in f.readlines()]):
            logging.debug("Processing new line %s #########################"%(index+1))
            try:
                externalAlertsInjectionService.injectAlert(headers,line)
            except Exception as e:
                logging.error("Cannot inject line number: %s from file %s , going to skip it."%(index+1,externalAlertFile))
                logging.error(e)





def getExternalAlertsFiles(input_file_path) :
    mtime = lambda f: os.stat(os.path.join(input_file_path, f)).st_mtime
    return list(sorted([f for f in os.listdir(input_file_path) if isfile(join(input_file_path,f))], key=mtime))



if __name__ == "__main__":
    # Prepare mongo client connection to 'user' collection


    main(sys.argv[1:])


