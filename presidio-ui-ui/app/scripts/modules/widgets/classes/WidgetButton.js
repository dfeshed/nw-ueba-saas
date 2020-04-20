(function () {
    'use strict';

    function WidgetButtonClass ($location, utils) {
        var supportedButtonTypes = new Set(["button", "multiLink", "link"]);

        /**
         * To avoid infinite recursion when working with widgets and widgetButton , a widget can't be set as a property
         * of the widgetButton i holds
         * (It's possible, but Angular dies), just the properties required for the widgetButton to work are set as the
         * parent.
         * @param parentObj
         * @returns {{getState: (Function|*|getState)}}
         */
        function getNonRecursiveParent (parentObj) {
            if (parentObj && parentObj.getState) {
                return {
                    getState: parentObj.getState.bind(parentObj),
                    isReady: parentObj.isReady ? parentObj.isReady.bind(parentObj) : function () {
                        return true;
                    }
                };
            }

            return null;
        }

        function WidgetButton (config, parent) {
            this.validate(config);

            if (config.type) {
                this.type = config.type;
                this.settings = config.settings;
            }
            else {
                this.type = config.type || "button";
                this._text = config.text;
                this.icon = config.icon;
                this.onClick = config.onClick;
                this.url = config.url;
                this.title = config.title;
                this.parent = getNonRecursiveParent(parent);
                this.text = utils.strings.parseValue(this._text, {}, this.parent.getState());
            }

        }

        WidgetButton.prototype.redirect = function () {
            $location.url(utils.strings.parseValue(this.url, {}, this.parent.getState()));
        };

        /**
         * update the button text
         */
        WidgetButton.prototype.refresh = function () {
            this.text = utils.strings.parseValue(this._text, {}, this.parent.getState());
        };

        WidgetButton.prototype.validate = function (config) {
            if (config.type) {
                if (!supportedButtonTypes.has(config.type)) {
                    throw new Error("Unknown widget button type, '" + config.type + "'.");
                }
            }
            else {
                if (!config.onClick && !config.url) {
                    throw new Error("Can't create WidgetButton, missing onClick.");
                }

                if (!config.onClick && config.url) {
                    config.onClick = this.redirect;
                }
                if (!angular.isFunction(config.onClick)) {
                    throw new TypeError("Invalid onClick for WidgetButton, expected a function but got " +
                        config.onClick);
                }

                if (!config.text && !config.icon) {
                    throw new Error("Can't create WidgetButton, text or icon is required.");
                }
            }
        };

        return WidgetButton;

    }

    WidgetButtonClass.$inject = ["$location", "utils"];

    angular.module("Widgets").factory("WidgetButton", WidgetButtonClass);
})();
