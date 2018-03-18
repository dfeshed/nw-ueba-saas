(function () {
    'use strict';

    function fsConfigRendererSyslogForwardingTypeDirective () {

        /**
         *
         * @param {object} scope
         * @param {object} element
         * @param {object} attrs
         * @param {array<object>|object} ctrl
         */
        function linkFn (scope, element, attrs, ctrl) {
            // Link function logic
            //if (scope.configItem.value === null || scope.configItem.value === '') {
            //    var selectElement = element.find('select');
            //    var ngModel = selectElement.controller('ngModel');
            //    ngModel.$setViewValue('none');
            //    ngModel.$setDirty('true');
            //    ngModel.$render();
            //}

        }

        /**
         * The directive's controller function
         *
         * @constructor
         */
        function FsConfigRendererSyslogForwardingTypeDirective ($element, $scope) {
            // Put dependencies on the instance
            var ctrl = this;
            ctrl.$element = $element;
            ctrl.$scope = $scope;

            // Invoke init
            ctrl.init();
        }

        angular.extend(FsConfigRendererSyslogForwardingTypeDirective.prototype, {

            /**
             * Init
             */
            init: function init () {

            }
        });

        FsConfigRendererSyslogForwardingTypeDirective.$inject = ['$element', '$scope'];

        return {
            restrict: 'E',
            templateUrl: 'app/layouts/configuration/renderers/syslog-forwarding-type/' +
            'fs-config-renderer-syslog-forwarding-type.view.html',
            link: linkFn,
            controller: FsConfigRendererSyslogForwardingTypeDirective,
            controllerAs: 'configRenderedDefaultCtrl', //Change to the desired controller name
            bindToController: {
            }
        };
    }

    fsConfigRendererSyslogForwardingTypeDirective.$inject = [];

    angular.module('Fortscale.layouts.configuration')
        .directive('fsConfigRendererSyslogForwardingType', fsConfigRendererSyslogForwardingTypeDirective);
}());
