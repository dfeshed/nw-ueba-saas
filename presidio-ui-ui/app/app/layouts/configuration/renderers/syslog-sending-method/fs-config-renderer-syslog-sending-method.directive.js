(function () {
    'use strict';

    function fsConfigRendererSyslogSendingMethodDirective () {

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
        function FsConfigRendererSyslogSendingMethodDirective ($element, $scope) {
            // Put dependencies on the instance
            var ctrl = this;
            ctrl.$element = $element;
            ctrl.$scope = $scope;

            // Invoke init
            ctrl.init();
        }

        angular.extend(FsConfigRendererSyslogSendingMethodDirective.prototype, {

            /**
             * Init
             */
            init: function init () {

            }
        });

        FsConfigRendererSyslogSendingMethodDirective.$inject = ['$element', '$scope'];

        return {
            restrict: 'E',
            templateUrl: 'app/layouts/configuration/renderers/syslog-sending-method/' +
            'fs-config-renderer-syslog-sending-method.view.html',
            link: linkFn,
            controller: FsConfigRendererSyslogSendingMethodDirective,
            controllerAs: 'configRenderedDefaultCtrl', //Change to the desired controller name
            bindToController: {
            }
        };
    }

    fsConfigRendererSyslogSendingMethodDirective.$inject = [];

    angular.module('Fortscale.layouts.configuration')
        .directive('fsConfigRendererSyslogSendingMethod', fsConfigRendererSyslogSendingMethodDirective);
}());
