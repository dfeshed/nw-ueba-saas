(function () {
    'use strict';

    angular.module("Fortscale")
        .filter('dataFieldType', function () {
            return function (fields, dataFieldType) {
                if (!dataFieldType) {
                    return fields;
                }

                var filteredFields = [];

                angular.forEach(fields, function (field) {
                    if (field.type === dataFieldType) {
                        filteredFields.push(field);
                    }
                });

                return filteredFields;
            };
        });
}());
