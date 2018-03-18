/**
 * Filter that returns only items without matching items in the provided list.
 */
(function () {
    'use strict';

    function notIn(assert:any):(val:Array<any>, arg2:any) => Array<any> {

        /**
         *
         * @param {Array<*>} val
         * @param {{compareKey: string, list: Array<*>}} mapObj
         * @returns {Array<*>}
         */
        function argIsObject(val:Array<any>, mapObj:{compareKey:string, list:Array<any>}):Array<any> {
            if (!mapObj || !mapObj.list) {
                return val;
            }

            // Validations
            assert.isString(mapObj.compareKey, 'mapObj.compareKey', 'fsNotIn.filter: ');
            assert.isArray(mapObj.list, 'mapObj.list', 'fsNotIn.filter: ');

            // Returns only items that have no matching item in mapObj.list
            return _.filter(val, (item:any) => {
                return _.every(mapObj.list, listItem => {
                    return listItem[mapObj.compareKey] !== item[mapObj.compareKey];
                })
            });
        }


        return function (val:Array<any>, arg2:any):Array<any> {

            if (!val) {
                return val;
            }

            // Validations
            if (!_.isArray(val)) {
                console.warn('fsNotIn.filter: Trying to filter a non Array argument', val);
                return val;
            }


            // Check overloading and invoke right method
            if (_.isArray(arg2)) {
                // TODO: Compare array in array (should be for primitives only)
            } else if (_.isObject(arg2)) {
                return argIsObject(val, arg2);
            } else {
                console.error('fsNotIn.filter: Second argument must be an array or an object.');
                return val;
            }


        }
    }


    angular.module('Fortscale.shared.filters')
        .filter('fsNotIn', ['assert', notIn]);
}());
