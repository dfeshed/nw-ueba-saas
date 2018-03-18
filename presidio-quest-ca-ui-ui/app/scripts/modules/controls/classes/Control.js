(function () {
    'use strict';

    var controlTypes = new Set(["search", "constant", "date", "dateRange", "number", "select", "checklist", "duration",
        "buttonSelect", "checkbox", "multiSelect", "paging", "simplePagination", "text"]);

    function ControlClass () {
        function Control (config) {
            this.validate(config);

            this._config = config;

            this.hide = config.hide;
            /**
             * The type of the control. Has to be one of the known control types, in the controlTypes Set above.
             * @type {config.type|*}
             */
            this.type = config.type;

            /**
             * The name of the param this Control controls.
             */
            this.param = config.param;

            /**
             * The name of the param this Control controls.
             */
            this.paramGroup = config.paramGroup;

            /**
             * [Optional] If specified, the url is parsed according to the formatParam string before the param controls
             * are updated.
             */
            this.formatParam = config.formatParam;
            /**
             * If isRequired is true, the param controls can't be updated if the Control has no value.
             * @type {boolean}
             */
            this.isRequired = !!config.isRequired;

            /**
             * If the control need a specific validator which is defined in the filterValidators
             * @type {string}
             */
            if (config.filterValidator) {
                this.filterValidator = config.filterValidator;
            }

            /**
             * If autoUpdate is true, the param controls are updated when the control is changed, no need for the
             * update button
             * @type {boolean}
             */
            this.autoUpdate = !!config.autoUpdate;
            this.disabled = !!config.disabled;

            /**
             * Text to display near the control
             * @type {*|string}
             */
            this.label = config.label && String(config.label);

            /**
             * Text to add by the control template after the control's input
             * @type {parseLanguage.suffix|*|obj.suffix|string}
             */
            this.suffix = config.suffix && String(config.suffix);

            /**
             * type-specific configuration for the control
             * @type {view.settings|*|$scope.view.settings|Function|widgetConfig.settings|exports.translations.settings}
             */
            this.settings = config.settings;
            this.value = config.value;
        }

        Control.prototype.validate = function (config) {
            if (!config.type) {
                throw new Error("Can't create Control, missing the 'type' property.");
            }

            if (!controlTypes.has(config.type)) {
                throw new Error("Unknown Control type, '" + config.type + "'.");
            }

            if (!config.param) {
                throw new Error("Can't create Control, missing the 'param' property.");
            }

            if (typeof(config.param) !== "string") {
                throw new TypeError("Invalid param for Control, expected a string but got " + typeof(config.param) +
                    ".");
            }

            if (config.paramGroup && typeof(config.paramGroup) !== "string") {
                throw new TypeError("Invalid paramGroup for Control, expected a string but got " +
                    typeof(config.paramGroup) + ".");
            }

            if (config.formatParam && typeof(config.formatParam) !== "string") {
                throw new TypeError("Invalid formatParam for Control, expected a string but got " +
                    typeof(config.formatParam) + ".");
            }
        };

        Control.prototype.clone = function () {
            return Control.copy(this);
        };

        Control.copy = function (otherControl) {
            if (!otherControl) {
                throw new Error("No Control provided to copy.");
            }

            if (!(otherControl instanceof Control)) {
                throw new TypeError("Can't copy Control, expected an instance of Control but got " +
                    otherControl.constructor.name + ".");
            }

            return new Control(otherControl._config);
        };

        return Control;
    }

    ControlClass.$inject = [];

    angular.module("Controls").factory("Control", ControlClass);

})();
