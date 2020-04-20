(function () {
    'use strict';

    function fsConfigRendererDefaultDirective () {

        /**
         *
         * @param {object} scope
         * @param {object} element
         * @param {object} attrs
         * @param {array<object>|object} ctrl
         */
        function linkFn (scope, element, attrs, ctrl) {
            // Link function logic
        }

        /**
         * The directive's controller function
         *
         * @constructor
         */
        function FsConfigRendererDefaultController ($element, $scope) {
            // Put dependencies on the instance
            var ctrl = this;
            ctrl.$element = $element;
            ctrl.$scope = $scope;


            // Invoke init
            ctrl.init();
        }

        angular.extend(FsConfigRendererDefaultController.prototype, {

            /**
             * Init
             */
            init: function init () {

            }
        });

        FsConfigRendererDefaultController.$inject = ['$element', '$scope'];

        return {
            restrict: 'E',
            templateUrl: 'app/layouts/configuration/renderers/default/fs-config-renderer-default.view.html',
            link: linkFn,
            controller: FsConfigRendererDefaultController,
            controllerAs: 'configRenderedDefaultCtrl', //Change to the desired controller name
            bindToController: {
            }
        };
    }

    fsConfigRendererDefaultDirective.$inject = [];

    angular.module('Fortscale.layouts.configuration')
        .directive('fsConfigRendererDefault', fsConfigRendererDefaultDirective);
}());
