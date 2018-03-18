(function () {
    'use strict';


    function stringToDate (assert: any) {

        function stringToDateFilter (stringDate:string, dateFormat:string):string {
            assert.isString(stringDate, 'stringDate', 'stringToDate.filter: ');
            assert.isString(dateFormat, 'dateFormat', 'stringToDate.filter: ');
            return moment(stringDate).format(dateFormat); 
        }


        return stringToDateFilter;
    }

    angular.module('Fortscale.shared.filters')
        .filter('stringToDate', ['assert', stringToDate]);
}());
