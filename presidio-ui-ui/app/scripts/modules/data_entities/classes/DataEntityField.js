(function () {
    'use strict';

    angular.module("DataEntities").factory("DataEntityField", ["dataEntityFieldTypes", function (dataEntityFieldTypes) {

        /**
         * Constructor for fields in Data Entities
         * @param config
         * @param dataEntity
         * @constructor
         */
        function DataEntityField (config, dataEntity) {
            this.validate(config);
            this.entity = dataEntity;
            this.id = config.id;
            this.name = config.name;
            this.type = dataEntityFieldTypes[config.type.toLowerCase()];
            this.scoreField = config.scoreField || null;
            this.isSearchable = !!config.searchable;
            this.isDefaultEnabled = !!config.isDefaultEnabled;
            this.attributes = config.attributes || [];
            this.tags = config.tags || [];
            this.format = config.format;
            this.valueList = config.valueList;
            this.shownForSpecificEntity = config.shownForSpecificEntity;

            /**
             * joinFrom and joinTo are keys which are used to connect entities. From and to describe directionality, so
             * a join can be done only from one entity to another but not in reverse.
             * @type {null|*}
             */
            this.joinFrom = config.joinFrom || null;
            this.joinTo = config.joinTo || null;
        }

        DataEntityField.prototype.validate = function (config) {
            if (Object(config) !== config) {
                throw new TypeError("Invalid configuration for DataEntityField, expected an object, got " +
                    typeof(config) + ".");
            }

            if (config.name) {
                if (typeof(config.name) !== "string") {
                    throw new TypeError("Invalid name for DataEntityField, expected a string but got " +
                        typeof(config.name));
                }
            }
            else {
                throw new Error("Can't instantiate DataEntityField, missing the 'name' property.");
            }

            if (config.id) {
                if (typeof(config.id) !== "string") {
                    throw new TypeError("Invalid id for DataEntityField, expected a string but got " +
                        typeof(config.id));
                }
            }
            else {
                throw new Error("Can't instantiate DataEntityField, missing the 'id' property.");
            }

            if (config.type) {
                if (typeof(config.type) !== "string") {
                    throw new TypeError("Invalid type for DataEntityField, expected a string but got " +
                        typeof(config.type) + ".");
                }

                if (!dataEntityFieldTypes[config.type.toLowerCase()]) {
                    throw new Error("Can't instantiate DataEntityField, unknown field type, '" + config.type + "'.");
                }
            }
            else {
                throw new Error("Can't instantiate DataEntityField, missing the 'id' property.");
            }

            if (config.scoreField) {
                if (typeof(config.id) !== "string") {
                    throw new TypeError("Invalid scoreField for DataEntityField, expected a string but got " +
                        typeof(config.scoreField) + ".");
                }
            }

            if (config.valueList) {
                if (!angular.isArray(config.valueList)) {
                    throw new TypeError("Invalid valueList for DataEntityField, expected and array but got " +
                        config.valueList + ".");
                }
            }

            if (config.attributes) {
                if (!angular.isArray(config.attributes)) {
                    throw new TypeError("Invalid attributes for DataEntityField, expected and array but got " +
                        config.attributes + ".");
                }
            }

            if (config.tags) {
                if (!angular.isArray(config.tags)) {
                    throw new TypeError("Invalid tags for DataEntityField, expected and array but got " + config.tags +
                        ".");
                }
            }
        };

        return DataEntityField;
    }]);
}());
