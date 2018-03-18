(function () {
    'use strict';

    function fsConfigRendererAlertSeverityCheckboxDirective () {

        /**
         *
         * @param {object} scope
         * @param {object} element
         * @param {object} attrs
         * @param {array<object>|object} ctrl
         */
        function linkFn (scope, element, attrs, ctrl) {
            // Link function logic

            // Set initial values
            if (scope.configItem.value !== null && scope.configItem.value !== '') {
                ctrl._populateSeveritiesFromCSV(scope.configItem.value);
            }
        }

        /**
         * The directive's controller function
         *
         * @constructor
         */
        function FsConfigRendererAlertSeverityCheckboxController ($element, $scope) {
            // Put dependencies on the instance
            var ctrl = this;
            ctrl.$element = $element;
            ctrl.$scope = $scope;

            ctrl.severities = {
                critical: {
                    name: 'Critical',
                    value: false
                },
                high: {
                    name: 'High',
                    value: false
                },
                medium: {
                    name: 'Medium',
                    value: false
                },
                low: {
                    name: 'Low',
                    value: false
                }
            };

            // Invoke init
            ctrl.init();
        }

        angular.extend(FsConfigRendererAlertSeverityCheckboxController.prototype, {

            /**
             * Takes a string and populates severity object
             * @param {string} csv
             * @private
             */
            _populateSeveritiesFromCSV: function (csv) {
                var severities = csv.split(',');
                _.each(severities, severity => this.severities[severity.toLowerCase()].value = true);
            },

            /**
             * Returns a csv of all severities that have a value set to true
             * @returns {string}
             * @private
             */
            _getCSV: function () {
                return _.map(_.filter(this.severities, severity => severity.value), severity => severity.name)
                    .join(',');
            },

            isEmpty: function () {
                return this._getCSV() === '';
            },

            changeSeverity: function () {

                // Get the csv
                var csv = this._getCSV();

                // Get the ngModel
                var el = this.$element.find('input.hidden-input');
                var ngModel = el.controller('ngModel');

                // Set the ng model value
                ngModel.$setViewValue(csv);
                ngModel.$setDirty('true');
                ngModel.$render();
            },

            /**
             * Init
             */
            init: function init () {

            }
        });

        FsConfigRendererAlertSeverityCheckboxController.$inject = ['$element', '$scope'];

        return {
            restrict: 'E',
            templateUrl: 'app/layouts/configuration/renderers/alert-severity-checkbox/' +
            'fs-config-renderer-alert-severity-checkbox.view.html',
            link: linkFn,
            controller: FsConfigRendererAlertSeverityCheckboxController,
            controllerAs: 'ctrl', //Change to the desired controller name
            bindToController: {}
        };
    }

    fsConfigRendererAlertSeverityCheckboxDirective.$inject = [];

    angular.module('Fortscale.layouts.configuration')
        .directive('fsConfigRendererAlertSeverityCheckbox', fsConfigRendererAlertSeverityCheckboxDirective);
}());
