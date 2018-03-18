/**
 * Created by shays on 07/09/2016.
 * This class contain different object types which can be stored on the state,
 * and implement the converting methods (to csv, to post request representation or to get requests representation
 */
module Fortscale.layouts.users.types {


    export interface IState{
        getAsStateSeperatedValues( value:any,listOfOptions?:{id:any}[]):string;
        getObjectForPostRequest(value:string):any; //For put/ post / fetch
        getObjectForGetRequest(value:string):string; //FOR GET / DELETE

    }

    class NumberState implements IState{
        getAsStateSeperatedValues( value:number):string{
            return value+"";
        }

        getObjectForPostRequest(value:string):any{
            return value;
        }

        getObjectForGetRequest(value:string):string{
            return value;
        }
    }

    class StringState implements IState{
        getAsStateSeperatedValues( value:string):string{
            return value;
        }

        getObjectForPostRequest(value:string):any{
            return value;
        }

        getObjectForGetRequest(value:string):string{
            return value;
        }
    }

    class BooleanState implements IState{
        getAsStateSeperatedValues( value:boolean):string{
            return value.toString();
        }

        getObjectForPostRequest(value:string):any{
            return value;
        }
        getObjectForGetRequest(value:string):string{
            return value;
        }
    }

    class AlertTypesState implements IState{

        /**
         *  The rest return array of strings (alert type name]
         *  The state/combobox need to get those ids as seperated string,
         *  for example: "VPN_lateral_movement , brute_force_normalized_username_hourly@@@brute_force_normalized_username_daily".
         *  The reason is the that "@@@" use as seperater between few alert types which are displayed and choosed together in the combobox

         * @param key
         * @param value
         * @param allAlertTypes - the ids list all the exists alertTypes
         * @returns {string}
         * @private
         */
        getAsStateSeperatedValues( values:any,listOfOptions:{id:any}[]):string{
            if (_.isNil(values)){
                return;
            }

            let response:string[]=[];
            _.each(values, (value:string)=> {
                //If anomaly type not empty - look for match key with can have single anomalyType
                // or a few anomaly types seperated by "@@"

                _.each(listOfOptions,(alertKey:{id:string})=>{
                    let keyParts:string[] = alertKey.id.split("@@@");
                    let index = _.findIndex(keyParts,function(keyPart) { return keyPart === value; });

                    if (index >= 0 ){
                        //value match one of the key parts
                        //So we add the alertKey to the response of keys
                        response.push(alertKey.id);
                    }
                })

            });
            response = _.uniq(response);
            return response.join(",");
        }

        /**
         * This method get a string of alert types list keys, seperated by "," and "@@@"
         * If this is for get request - convert it to string seperated by "," only.
         * If this is for post request we need to generate an object for the response
         * @param value
         * @param isPostPut
         * @returns {string[] or String}
         * @private
         */
        getObjectForPostRequest(value:string):any{
            let SEPERATPR:string = "@@@";

            if (_.isNil(value)){
                return value;
            }

            let alertTypessAsStrings : string[] = value.split(",");
            if (alertTypessAsStrings.length===0){
                return null;
            }

            let response :string[] = [];
            _.each(alertTypessAsStrings,(alertTypesParseAsString:any) =>{
                //If alert types is list of alerts types seperated by "@@@" - split it into array of string
                //and add each item in the list to the response
                let parts:string[] = alertTypesParseAsString.split(SEPERATPR);
                //add parts to the response
                response = _.union(response,parts);


            });

            return response;

        }

        getObjectForGetRequest(value:string):string{
            return _.replace(value,"@@@",",");
        }

    }

    class IndicatorTypesState implements IState{
        /**
         *  The rest return array of object [{dataSource: string, anomalyType string},{{dataSource: string, anomalyType string}}]
         *  The state/combobox need to get those ids as seperated string,
         *  for example: "gwame , kerberos_login@@@time_anomaly , ssh@@@high_number_hourly@@high_number_daiy.
         *  The reason is the that "@@@" use as seperater between the data source and anomaly type,
         *  while the "@@" used to diffriniate two indicators that have the same pretty name, and will display only once
         *  in the combo. for example high_number_hourly@@high_number_daiy will have only one entry in the combo box.
         *

         * @param key
         * @param value
         * @param allIndicatorTypes - the ids list all the exists indicators
         * @returns {string}
         * @private
         */

        getAsStateSeperatedValues( value:any,listOfOptions:{id:any}[]):string{
            if (value.anomalyList) {

                let dataSources:string[]=[];
                _.each(value.anomalyList, (singleRow:{dataSource:string, anomalyType:string})=> {
                    //If anomaly type not empty - look for match key with can have single anomalyType
                    // or a few anomaly types seperated by "@@"
                    if (singleRow.anomalyType){
                        _.each(listOfOptions,(indicatorKey:{id:string})=>{
                            let keyParts:string[] = indicatorKey.id.split("@@@");
                            if(singleRow.dataSource === keyParts[0] && keyParts.length>1){

                                let anomalyParts:string[] = keyParts[1].split("@@");

                                let keyIndex:number = _.indexOf(anomalyParts ,singleRow.anomalyType);
                                if (keyIndex > -1) {
                                    dataSources.push(indicatorKey.id);
                                }

                            }
                        })

                    } else {//Data source only
                        dataSources.push(singleRow.dataSource);
                    }


                });
                dataSources = _.uniqBy(dataSources  , function (indicatorKey) {
                    return indicatorKey;
                });
                return dataSources.join(",");
            }
            return;
        }

        /**
         * This method get a string of indicators list keys
         * If this is for get request - do nothing. Return the string.
         * If this is for post request we need to generate a body of the form
         * anomalyList:{dataSource:string, anomalyType?:string}[]}
         * @param value
         * @param isPostPut
         * @returns {anomalyList:{dataSource:string, anomalyType?:string}[]} or String
         * @private
         */
        getObjectForPostRequest(value:string):any{
            if (_.isNil(value)){
                return value;
            }

            let indicatorsAsStrings : string[] = value.split(",");;
            if (indicatorsAsStrings.length===0){
                return null;
            }

            let response :{anomalyList:{dataSource:string, anomalyType?:string}[]} = {anomalyList:[]};
            _.each(indicatorsAsStrings,(indicatorParseAsString:any) =>{

                let parts:string[] = indicatorParseAsString.split("@@@")
                if (parts.length === 1){
                    let indicator :{dataSource:string, anomalyType?:string} = {dataSource : parts[0]};

                    response.anomalyList.push(indicator);
                } else if (parts.length>1) {
                    _.each(parts[1].split('@@'),(indicatorsOfDataSource:string) => { //one or more
                        let indicator:{dataSource:string, anomalyType?:string} = {
                            dataSource : parts[0],
                            anomalyType : indicatorsOfDataSource
                        };
                        response.anomalyList.push(indicator);
                    });

                }


            });

            return response;
        }

        getObjectForGetRequest(value:string):string{
            return value;
        }
    }

    class StringsArrayState implements IState{
        getAsStateSeperatedValues( value:string[],listOfOptions:{id:any}[]):string{
            return value.join(",");
        }

        getObjectForPostRequest(value:string):any{
            if (_.isNil(value)){
                return value;
            }
            return value.split(",");
        }

        getObjectForGetRequest(value:string):string{
            return value;
        }

    }

    export const STATE_TYPES:{[key:string]:IState} = {
        "NumberState": new NumberState(),
        "StringState": new StringState(),
        "BooleanState": new BooleanState(),
        "IndicatorTypesState": new IndicatorTypesState(),
        "AlertTypesState": new AlertTypesState(),
        "StringsArrayState": new StringsArrayState()

    };


}
