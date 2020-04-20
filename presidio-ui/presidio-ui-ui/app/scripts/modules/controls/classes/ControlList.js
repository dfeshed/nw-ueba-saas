(function () {
    'use strict';

    function ControlListClass (controls) {
        function ControlList (config) {
            if (!config) {
                return this;
            }

            if (config.constructor === Array) {
                config = {controls: config};
            }

            this.validate(config);

            var controlList = this;
            if (!controlList.controls) {
                controlList.controls = [];
            }
            config.controls.forEach(function (controlConfig) {
                controlList.controls.push(controls.loadControl(controlConfig));
            });
        }

        ControlList.prototype.validate = function (config) {
            if (!config.controls) {
                throw new Error("Can't create ControlList, missing the 'controls' property.");
            }
        };

        ControlList.prototype.getParams = function () {
            var params = {};

            if (!this.controls) {
                return params;
            }

            this.controls.forEach(function (control) {
                controls.getControlValue(control, {}, params);
            });

            return params;
        };

        return ControlList;
    }

    ControlListClass.$inject = ["controls"];

    angular.module("Controls").factory("ControlList", ControlListClass);
})();
