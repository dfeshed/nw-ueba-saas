(function () {
    'use strict';

    function fsConfigRendererIsEnabledDirective () {

        /**
         *
         * @param {object} scope
         * @param {object} element
         * @param {object} attrs
         * @param {array<object>|object} ctrl
         */
        function linkFn (scope, element, attrs, ctrl) {

            // Set default value
            if (scope.configItem.value === null || scope.configItem.value === '') {
                var ngModel = ctrl._getElementNgModelCtrl(element, 'input');
                ctrl._changeModel(ngModel, false);
            }
        }

        /**
         * The directive's controller function
         *
         * @constructor
         */
        function FsConfigRendererIsEnabledController ($element, $scope) {
            // Put dependencies on the instance
            var ctrl = this;
            ctrl.$element = $element;
            ctrl.$scope = $scope;

            // Invoke init
            ctrl.init();
        }

        angular.extend(FsConfigRendererIsEnabledController.prototype, {

            /**
             * Init
             */
            init: function init () {

            },

            /**
             * Extracts the ng model controller from an element
             * @param {jQueryElement} element
             * @param {string} selector
             * @returns {*}
             * @private
             */
            _getElementNgModelCtrl: function (element, selector) {
                var el = element.find(selector);
                return el.controller('ngModel');
            },

            /**
             * Changes model value, sets dirty and renders
             * @param {{}} ngModelCtrl
             * @param {*} value
             * @private
             */
            _changeModel: function (ngModelCtrl, value) {
                ngModelCtrl.$setViewValue(value);
                ngModelCtrl.$setDirty('true');
                ngModelCtrl.$render();
            },

            /**
             * Toggles the state of the model
             * @param {{}} modelsObj
             * @param {string} propertyName
             */
            toggleState: function (modelsObj, propertyName) {

                modelsObj[propertyName] = !modelsObj[propertyName];
                var ngModel = this._getElementNgModelCtrl(this.$element, 'input');
                this._changeModel(ngModel, modelsObj[propertyName]);
            }
        });

        FsConfigRendererIsEnabledController.$inject = ['$element', '$scope'];

        return {
            restrict: 'E',
            templateUrl: 'app/layouts/configuration/renderers/is-enabled/' +
            'fs-config-renderer-is-enabled.view.html',
            link: linkFn,
            controller: FsConfigRendererIsEnabledController,
            controllerAs: 'ctrl',
            bindToController: {
            }
        };
    }

    fsConfigRendererIsEnabledDirective.$inject = [];

    angular.module('Fortscale.layouts.configuration')
        .directive('fsConfigRendererIsEnabled', fsConfigRendererIsEnabledDirective);
}());
