module Fortscale {


    export interface IIndicatorErrorCodesService {

        /**
         * Adds an error codes object
         *
         * @param dataEntityId
         * @param errorCodesObject
         */
        addErrorObject (dataEntityId: string, errorCodesObject: any): void;
        /**
         * Get the display message of an error code of a specific data entity
         * @param dataEntityId
         * @param errorCode
         */
        getDisplayMessage (dataEntityId:string, errorCode:string): string;


    }

    (function () {
        'use strict';

        class IndicatorErrorCodesService implements IIndicatorErrorCodesService {


            static NAME_SPACE = 'fsIndicatorErrorCodes';

            static $inject = ['assert'];
            constructor(public assert) {
            }

            // Holds the error codes map
            private _errorCodesMap: Map<string, any> = new Map();
            // Holds the error message for validations
            private _errorMsg = 'IndicatorErrorCodesService: ';

            /**
             * Gets the errors object that relates to the data entity id
             * @param dataEntityId
             * @private
             */
            private _getErrorsObject(dataEntityId:string):{}|void {
                let errorsObject = this._errorCodesMap.get(dataEntityId);
                return errorsObject || null;
            }

            /**
             * Adds an error object to error object map
             * @param dataEntityId
             * @param errorCodesObject
             */
            addErrorObject(dataEntityId:string, errorCodesObject:any):void {
                // Validations
                let errMsg = `${this._errorMsg}addErrorObject: `;
                this.assert.isString(dataEntityId, 'dataEntityId', errMsg);
                this.assert.isObject(errorCodesObject, 'errorCodesObject', errMsg);

                // Add error codes object
                this._errorCodesMap.set(dataEntityId, errorCodesObject);
            }


            /**
             * Returns a prettified error code if supplied properties are correct,
             * otherwise the original errorCode is returned.
             *
             * @param dataEntityId
             * @param errorCode
             * @returns {string}
             */
            getDisplayMessage(dataEntityId:string, errorCode:string):string {
                // Validations
                let errMsg = `${this._errorMsg}getDisplayMessage: `;
                this.assert.isString(dataEntityId, 'dataEntityId', errMsg);
                this.assert.isString(errorCode, 'errorCode', errMsg);

                // Get the error object
                let errorObject = this._getErrorsObject(dataEntityId);
                if (errorObject) {
                    // Get the error message treating the errorCode as string
                    let errorDisplayObject = errorObject[errorCode];

                    // If not found try the errorCode as HEX
                    if (_.isUndefined(errorDisplayObject)) {
                        errorDisplayObject = errorObject[parseInt(errorCode)];
                    }

                    return errorDisplayObject ? errorDisplayObject.displayMessage : errorCode;
                }

                // Return null if no errorObject
                return errorCode;
            }
        }



        angular.module('Fortscale.shared.services.fsIndicatorErrorCodes', [])
            .service('fsIndicatorErrorCodes', IndicatorErrorCodesService);

    }());
}
