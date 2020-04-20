(function () {
    'use strict';


    function fsControlsDirective () {

        /**
         * The directive's controller function
         *
         * @constructor
         */
        function FsControlsController () {}

        angular.extend(FsControlsController.prototype, {

            /**
             * PUBLIC METHODS
             */

            /**
             * On submit, it resets the form (to disable submit button until next change),
             * then invokes submitDelegate if it's defined
             */
            submit: function submit (formCtrl) {

                // Reset form
                formCtrl.$setPristine();

                // If submitDelegate was provided, and it is a function,
                // invoke it.
                if (angular.isFunction(this.submitDelegate)) {
                    this.submitDelegate();
                }
            }
        });

        return {
            restrict: 'E',
            templateUrl: 'app/shared/components/fs-controls/fs-controls.view.html',
            scope: true,
            transclude: true,
            controller: FsControlsController,
            controllerAs: 'controls',
            bindToController: {
                title: '@',
                className: '@',
                hideUpdate: '@',
                submitDelegate: '='
            }
        };
    }

    fsControlsDirective.$inject = [];

    angular.module('Fortscale.shared.components.fsControls', [])
        .directive('fsControls', fsControlsDirective);
}());
