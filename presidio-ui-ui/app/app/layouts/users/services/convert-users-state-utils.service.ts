/**
 * Attributes of the users state has several possible format.
 * Each value should might have different represntations for:
 * - How it stored on the state
 * - How it sent on the url to get & delete requests
 * - How it sent in the body for post / fetch / put request.
 *
 * The goals of this service is to convert any of the types between the different representations.
 * The list of types and implementations defined in app/app/layouts/users/services/users-state-types.ts
 *
 */
module Fortscale.layouts.users {

    import IState = Fortscale.layouts.users.types.IState;
    import STATE_TYPES = Fortscale.layouts.users.types.STATE_TYPES;

    /**
     *
     */
    export interface IConvertUsersStateUtils {

        /**
         * Helper method which convert the object value into seperated comma string
         * @param key
         * @param value
         * @param allOptionsList - a dictionary from attribute name (I.E. alertTypes) to list of options of the attribute.
         *                          each option is an object that must have id member.
         */
        getAsStateSeperatedValues(key:string, value:any,allOptionsList:{[key:string]:{id:any,value:any}[]}):any;

        /**
         * Helper method which coverte the seperated comma string from objectData[key] to the get/delete representation or to
         * post / put representation
         * @param objectData
         * @param key the key of the attribute on the objectData
         * @param isPostPut - if true the string will converted to the post / put representation
         */
        buildParam(objectData:any, key:string,isPostPut: boolean):any;
    }

    class ConvertUsersStateUtils implements IConvertUsersStateUtils {

        /**
         * Helper method which convert the object value into seperated comma string
         * @param key
         * @param value
         * @param allIndicatorTypes
         * @param allAlertTypes
         */
        getAsStateSeperatedValues(key:string, value:any,allOptionsList:{[key:string]:{id:any,value:any}[]}):any{

            //If no values return null
            if (!value){
                return;
            }

            //Return the value as string.
            let stateTypeImplementation:IState = STATE_TYPES[this.attributeToTypeMap[key]];

            //If this attribute need the list for possible to build the values (I.E. all indicator types, all alert types)
            //It might be empty
            let optionsListForAttribute:{id:any,value:any}[] = allOptionsList[key];
            return stateTypeImplementation.getAsStateSeperatedValues(value,optionsListForAttribute);

        }


        /**
         * Helper method which coverte the seperated comma string from objectData[key] to the get/delete representation or to
         * post / put representation
         * @param objectData
         * @param key the key of the attribute on the objectData
         * @param isPostPut - if true the string will converted to the post / put representation
         */
        buildParam(objectData:any, key:string,isPostPut: boolean):any{
            let value:string = objectData[key];
            if (_.isNil(value)){
                return value;
            }
            let stateTypeImplementation:IState = STATE_TYPES[this.attributeToTypeMap[key]];
            if (!isPostPut){
                return stateTypeImplementation.getObjectForGetRequest(value);
            } else {

                return stateTypeImplementation.getObjectForPostRequest(value);
            }
        }


        static $inject = [];
        constructor (public attributeToTypeMap:{[key:string]:string}) {

        }
    }



    /**
     * The factory service user to init the real service
     */
    export interface IConvertUsersStateUtilsFactory {
        getConvertUsersStateUtil(attributeToTypeMap:{[key:string]:string}):IConvertUsersStateUtils;
    }
    class ConvertUsersStateUtilsFactory implements IConvertUsersStateUtilsFactory {
        getConvertUsersStateUtil(attributeToTypeMap:{[key:string]:string}):IConvertUsersStateUtils{
            return new ConvertUsersStateUtils(attributeToTypeMap);
        }
    }
    // ********************************** End of Factory

    angular.module('Fortscale.layouts.users')
        .service('convertUsersStateUtilsFactory', ConvertUsersStateUtilsFactory);
}
