(function () {
    'use strict';

    function DataEntitySortClass () {
        function DataEntitySort (config) {
            this.validate(config);

            this.field = config.field.id;
            this.direction = config.direction ? config.direction.toUpperCase() : "ASC";
        }

        DataEntitySort.prototype.validate = function (config) {
            if (!config.field) {
                throw new Error("Can't create DataEntitySort, missing field.");
            }

            if (config.direction) {
                if (typeof(config.direction) !== "string") {
                    throw new TypeError("Invalid 'direction' for DataEntitySort, expected a string but got " +
                        typeof(config.direction));
                }

                var directionStr = config.direction.toUpperCase();
                if (directionStr !== "ASC" && directionStr !== "DESC") {
                    throw new Error("Unknown direction for DataEntitySort, '" + config.direction + "'.");
                }
            }

        };

        return DataEntitySort;
    }

    angular.module("DataEntities").factory("DataEntitySort", DataEntitySortClass);

})();
