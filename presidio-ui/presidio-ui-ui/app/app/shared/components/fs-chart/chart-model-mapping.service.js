(function () {
    'use strict';

    function ChartModelMapping() {
    }

    _.merge(ChartModelMapping.prototype, {

        /**
         * An adapter that takes the data model and creates a new array with new objects
         * that are mapped according to the provided mapObject.
         *
         * @param {object} mapObject
         * @param {Array<object>} data
         * @returns {Array<object>}
         */
        mapData: function (mapObject, data) {

            // Return a map
            return _.map(data, function (dataRow) {

                // Create a new object for each member in the array
                var obj = {};

                // Iterate through the map object, and attach a new property to obj
                // for each property on mapObject
                _.each(mapObject, function (mapProp, key) {

                    // When the map property value is a string,
                    // use key as prop name on the new obj, and then use the string value
                    // as key name for dataRow, and set the value to the object.
                    // Example: if mapObject is {y: 'count'} then new object will be:
                    // {y: dataRow.count}
                    if (_.isString(mapProp)) {

                        obj[key] = dataRow[mapProp];

                    } else if (_.isObject(mapProp)){

                        // If mapProp is an object, this means that we need to do something
                        // more complex then simple mapping.
                        // The value of the map property should be an object
                        // that has two properties: key, fn. Key will be the key on dataRow,
                        // and fn will be a function that takes:
                        // dataRow[mapProp.key], dataRow, data, mapProp
                        // and should return a value. That value will be set to obj.

                        obj[key] = mapProp.fn(dataRow[mapProp.key], dataRow, data, mapObject);
                    } else {

                        // This will throw if mapProp is not a string or an object.
                        throw new Error('map property must be a string or an object.');
                    }
                    if (obj[key] === undefined) {
                        delete obj[key];
                    }
                });

                return obj;
            });
        }
    });

    ChartModelMapping.$inject = [];

    angular.module('Fortscale.shared.components.fsChart')
        .service('chartModelMapping', ChartModelMapping);
}());
