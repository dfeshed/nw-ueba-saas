(function () {
    'use strict';

    function FsUserTagDetails() {


        /**
         * The directive's controller function
         *
         * @constructor
         */
        function FsUserTagDetailsController($scope, $element, $attrs ) {
            // Put dependencies on the controller instance
            this.$scope = $scope;
            this.$element = $element;
            this.$attrs = $attrs;

        }

        FsUserTagDetailsController.$inject = ['$scope', '$element', '$attrs'];

        return {
            restrict: 'E',
            replace: true,
            templateUrl: 'app/shared/directives/fs-user-tag-details/fs-user-tag-details.html',
            scope: {},
            controller: FsUserTagDetailsController,
            controllerAs: 'details',
            bindToController: {
                bottomLabel: '@',
                rightLeftCornerLabel: '@',
                imgSrc: '@',
                tagHref: '@'
            }
        };
    }

    FsUserTagDetails.$inject = [];

    angular.module('Fortscale.shared.directives.fsUserTagDetails',[])
        .directive('fsUserTagDetails', FsUserTagDetails);


}());
