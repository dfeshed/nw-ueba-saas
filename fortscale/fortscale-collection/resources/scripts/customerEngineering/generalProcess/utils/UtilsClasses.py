__author__ = 'idanp'

import unittest

#Replacer  class - This class represent replacement object that will handle a replacement logic
#Will contain as attribute list of  matcher:replacement
#Will expose the replace method
class Replacer(object):

    replacements=[]

    #Constructor
    #Input -
    #       matchersReplacements - String that represent list of matcher:replacement
    #       matchersSeperator - String that represent the separator sign between each matcher:replacement pair
    #       replacementSeperator - String that represent the separator sign between the matcher:replacement pair
    def __init__(self,matchersReplacements,matchersSeperator,replacementSeperator):
        matchers=matchersReplacements.split(matchersSeperator)

        for matcher in matchers:

            keyValue=matcher.split(replacementSeperator)

            if len(keyValue)!=2:
                raise ValueError("Bad format for comma separators replacements")

            matcherToReplacementMap={}
            matcherToReplacementMap["matcher"]=keyValue[0]
            matcherToReplacementMap["replacement"]=keyValue[1]

            self.replacements.append(matcherToReplacementMap)

    #The replace method will get a string and for each matcher:replacement it will replace the 'matcher' in the line with the 'replacement'
    def replace(self,line):

        for replacemnetMethod in self.replacements:
            line = line.replace(replacemnetMethod["matcher"],replacemnetMethod["replacement"])

        return line

        #The replace method will get a string and for each matcher:replacement it will replace the 'matcher' in the line with the 'replacement'
    def replaceReverse(self,line):

        for replacemnetMethod in self.replacements:
            line = line.replace(replacemnetMethod["replacement"],replacemnetMethod["matcher"])

        return line




#Class that will contain some utils methods
class Utils(object):


    #This method can take a list of given key:value paris that splited by <extSpliter> sign and it will break it to dict of the key:value
    #The inlineSpliter param is indicate which sign is used to split between the key and the value pair , by default its ':' sign
    def convertStringToDict(self,string,extSpliter,inlineSpliter=':'):
        dict={}

        if not string :
            return dict

        pairs=string.split(extSpliter)
        for pair in pairs:
            splitedPair=pair.split(inlineSpliter)

            if len(splitedPair) != 2 :
                raise ValueError("Error - some pair does not contain key and value pattern (missing one of them or there is more then a pair) - Spliter sign between paris is - %s  Spliter sign between pair is - %s  String is - %s " % (extSpliter,inlineSpliter,string) )
            key=splitedPair[0]
            value=splitedPair[1]
            dict[key]=value
        return dict

    #Utility to convert timestamp to millisecond in case its represented  in second
    def convertToMilisec(self,timestamp):
        # convert timestamp in seconds to timestamp in milli-seconds
        # 100000000000L is 3/3/1973, assume we won't get data before that....

        if (timestamp < 100000000000L):
            return timestamp*1000
        return timestamp



#This is the unit test class for testing this module
class TestUtilsMethods(unittest.TestCase):

    global utils

    #Unit test for testing the converting string to dict method
    def test_convertStringToDict(self):
        utils = Utils()
        testFlag=False
        str="key1:vla1,key2:val2,key3:val3"
        dictExpected={}
        dictExpected['key1']='vla1'
        dictExpected['key2']='val2'
        dictExpected['key3']='val3'


        dictActual=utils.convertStringToDict(str,",")

        testFlag=len(dictExpected.keys())==len(dictActual.keys())

        if testFlag:
            for key in dictExpected.keys():
                if key in dictActual:
                    testFlag=dictActual[key]==dictExpected[key]
                else:
                    testFlag=False
        self.assertTrue(testFlag)

    #Unit test for testing the convert string to dict with pairs separator '='
    def test_convertStringToDictWithNoDefaultInlineSpliter(self):
        utils = Utils()
        testFlag=False
        str="key1=vla1,key2=val2,key3=val3"
        dictExpected={}
        dictExpected['key1']='vla1'
        dictExpected['key2']='val2'
        dictExpected['key3']='val3'


        dictActual=utils.convertStringToDict(str,",","=")

        testFlag=len(dictExpected.keys())==len(dictActual.keys())

        if testFlag:
            for key in dictExpected.keys():
                if key in dictActual:
                    testFlag=dictActual[key]==dictExpected[key]
                else:
                    testFlag=False
        self.assertTrue(testFlag)


    #Unittest for test the convert to milisec method
    def test_convertToMilisec(self):
        utils = Utils()
        ts=1492124400
        tsExpected=1492124400000
        tsActual=utils.convertToMilisec(ts)
        self.assertTrue(tsExpected==tsActual)

    #Unittest for test the convert to millisec method that get millisecond as input
    def test_convertToMilisecGettingMillisec(self):
        utils = Utils()
        ts=1492124400000
        tsExpected=1492124400000
        tsActual=utils.convertToMilisec(ts)
        self.assertTrue(tsExpected==tsActual)





        #def test_isupper(self):
        #    self.assertTrue('FOO'.isupper())
        #    self.assertFalse('Foo'.isupper())
    #
    #def test_split(self):
    #    s = 'hello world'
    #    self.assertEqual(s.split(), ['hello', 'world'])
    #    # check that s.split fails when the separator is not a string
    #    with self.assertRaises(TypeError):
    #        s.split(2)




if __name__ == "__main__":
    unittest.main()

