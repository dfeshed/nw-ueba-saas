(function () {
    'use strict';

    function TableWidgetConfigFactory () {
        function TableConfig (config) {
            this.validate(config);

            this.fields = config.fields.map(function (field) {
                return new TableFieldConfig(field);
            });

            this.caption = config.caption;

            /**
             * dataField is used for arrays within a data item, to render all the inner rows.
             */
            this.dataField = config.dataField;

            this.sortParam = config.sortParam;
        }

        TableConfig.prototype.validate = function (config) {
            var ERROR_PREFIX = "Can't create TableConfig, ";

            if (!config) {
                throw new Error(ERROR_PREFIX + "missing the config object.");
            }

            if (Object(config) !== config) {
                throw new TypeError(ERROR_PREFIX + "config isn't an object.");
            }

            if (!config.fields) {
                throw new Error(ERROR_PREFIX + "missing the fields property.");
            }

            if (config.fields.constructor !== Array) {
                throw new TypeError(ERROR_PREFIX + "fields must be an array.");
            }

            if (config.caption && typeof(config.caption) !== "string") {
                throw new TypeError(ERROR_PREFIX + "invalid caption field. Expected string but got " +
                    typeof(config.caption));
            }

            if (config.dataField && typeof(config.dataField) !== "string") {
                throw new TypeError(ERROR_PREFIX + "invalid dataField field. Expected string but got " +
                    typeof(config.dataField));
            }
        };

        function TableFieldConfig (config) {
            this.validate(config);
            this.name = config.name;
            this.field = config.field;
            this.value = config.value;
            this.link = config.link;
            this.noValueDisplay = config.noValueDisplay;
            this.spanRowsIfEqual = config.spanRowsIfEqual;
            this.menu = config.menu;
            this.tags = config.tags; // TODO: Create a tags class and use it here.
            this.icon = config.icon; // TODO: Create an Icon class and use it here.
            this.valueTooltip = config.valueTooltip;
            this.map = config.map; // TODO: validate this
            this.renderHeader = config.renderHeader !== false;
            this.sortBy = config.sortBy;
            this.sortDirection = config.sortDirection;
            this.externalLinks = config.externalLinks;
        }

        TableFieldConfig.prototype.validate = function (config) {
            var ERROR_PREFIX = "Invalid field for table, ";

            if (!config) {
                throw new Error(ERROR_PREFIX + "no configuration specified.");
            }

            if (Object(config) !== config) {
                throw new TypeError(ERROR_PREFIX + "config isn't an object.");
            }

            if (!config.name || typeof(config.name) !== "string") {
                throw new Error(ERROR_PREFIX + "expected a string field 'name' but got " + String(config.name));
            }

            if (!config.field && !config.value) {
                throw new Error(ERROR_PREFIX + "missing a 'field' or 'value' property.");
            }

            if (config.field && typeof(config.field) !== "string") {
                throw new TypeError(ERROR_PREFIX + "expected a string 'field' property, but got " +
                    typeof(config.field));
            }

            if (config.value && typeof(config.value) !== "string") {
                throw new TypeError(ERROR_PREFIX + "expected a string 'value' property, but got " +
                    typeof(config.value));
            }

            if (config.link && typeof(config.link) !== "string") {
                throw new TypeError(ERROR_PREFIX + "expected a string 'link' property, but got " + typeof(config.link));
            }

            if (config.noValueDisplay && typeof(config.noValueDisplay) !== "string") {
                throw new TypeError(ERROR_PREFIX + "expected a string 'noValueDisplay' property, but got " +
                    typeof(config.noValueDisplay));
            }

            if (config.spanRowsIfEqual && typeof(config.spanRowsIfEqual) !== "boolean") {
                throw new TypeError(ERROR_PREFIX + "expected a boolean 'spanRowsIfEqual' property, but got " +
                    typeof(config.spanRowsIfEqual));
            }
        };

        return TableConfig;
    }

    angular.module("TableWidget").factory("TableWidgetConfig", TableWidgetConfigFactory);

})();
