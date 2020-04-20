(function () {
    'use strict';

    function fsReportHeaderDirective () {

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
        function FsReportHeaderController ($element, $scope) {
            // Put dependencies on the instance
            var ctrl = this;
            ctrl.$element = $element;
            ctrl.$scope = $scope;


            // Invoke init
            ctrl.init();
        }

        angular.extend(FsReportHeaderController.prototype, {

            /**
             * Init
             */
            init: function init () {
            }
        });

        FsReportHeaderController.$inject = ['$element', '$scope'];

        return {
            restrict: 'E',
            templateUrl: 'app/layouts/reports/components/fs-report-header.view.html',
            scope: {},
            link: linkFn,
            controller: FsReportHeaderController,
            controllerAs: 'ctrl',
            bindToController: {
                headerClass: '@',
                headerTitle: '@',
                headerDescription: '@'
            }
        };
    }

    fsReportHeaderDirective.$inject = [];

    angular.module('Fortscale.layouts.reports')
        .directive('fsReportHeader', fsReportHeaderDirective);
    angular.module('Fortscale.layouts.reports')
        .directive('fsTableHeader', fsReportHeaderDirective);
}());
