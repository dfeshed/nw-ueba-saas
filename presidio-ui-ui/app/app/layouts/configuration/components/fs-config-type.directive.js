(function () {
    'use strict';

    function fsConfigTypeDirective () {

        /**
         *
         * @param {object} scope
         * @param {object} element
         * @param {object} attrs
         * @param {array<object>|object} ctrl
         */
        function linkFn (scope, element, attrs, ctrl) {
            // Link function logic
            scope.ctrl.ngModelController = ctrl[0];
            scope.ctrl._linkInit();

        }

        /**
         * The directive's controller function
         *
         * @constructor
         */
        function FsConfigurationAffectedItemsController ($element, $scope, appConfig, assert) {
            // Put dependencies on the instance
            var ctrl = this;
            ctrl.$element = $element;
            ctrl.$scope = $scope;
            ctrl.appConfig = appConfig;
            ctrl.assert = assert;

            // Invoke init
            ctrl.init();
        }

        angular.extend(FsConfigurationAffectedItemsController.prototype, {
            _errMsg: 'Fortscale.appConfig: fsConfigType.directive: ',

            _linkInit: function () {
                var ctrl = this;

                // Get formatter
                var formatter = this.appConfig.getFormatter(this.configItem.type);

                // Inject formatter into ngModel
                if (formatter) {
                    this.ngModelController.$formatters.push(formatter);
                }

                // Get validator
                var validator = this.appConfig.getValidator(this.configItem.type);

                // Inject validator into ngModel
                if (validator) {
                    this.ngModelController.$validators[this.configItem.type] = function (modelValue, viewValue) {

                        // Value should be modelValue or viewValue or configItem.value
                        var value = modelValue !== undefined ? modelValue : viewValue;
                        value = value !== undefined ? value : ctrl.configItem.value;

                        // run formatter
                        if (formatter) {
                            value = formatter(value);
                        }

                        // Run validator
                        return validator(value);
                    };
                }

            },

            /**
             * Init
             */
            init: function init () {
            }
        });

        FsConfigurationAffectedItemsController.$inject = ['$element', '$scope', 'appConfig', 'assert'];

        return {
            restrict: 'A', // Change To EA if not only element. Change to A if only attribute
            link: linkFn,
            controller: FsConfigurationAffectedItemsController,
            controllerAs: 'ctrl',
            bindToController: {
                configItem: '<'
            },
            require: ['ngModel'],
            priority: -1000
        };
    }

    fsConfigTypeDirective.$inject = [];

    angular.module('Fortscale.appConfig')
        .directive('fsConfigType', fsConfigTypeDirective);
}());
