(function () {
    'use strict';

    angular.module("Fortscale").directive("modal", function () {
        return {
            restrict: "E",
            template: ['<div class="modal" ng-show="settings.show" toggle-keys="{{ { escape: \'modalClose\' } }}" ' +
            'toggle-keys-enabled="settings.show" ng-cloak>',
                '<div class="modal-background closes-modal"></div>',
                '<div class="modal-contents" ng-style="modalStyle" ng-transclude>',
                '</div></div>'].join(""),
            transclude: true,
            replace: true,
            require: '?ngModel',
            link: function ($scope, element, attrs, ngModel) {
                element.on("click", function (e) {
                    if (e.target.classList.contains("closes-modal")) {
                        $scope.$apply($scope.modalClose);
                    }
                });

                $scope.modalClose = function () {
                    $scope.settings.show = false;
                    //ngModel.$setViewValue(false);
                    $scope.$emit("modalClose");
                };

                ngModel.$render = function () {
                    $scope.settings = ngModel.$viewValue;
                    if ($scope.settings) {
                        $scope.modalStyle = {
                            width: $scope.settings.width,
                            height: $scope.settings.height,
                            'margin-top': $scope.settings.height / -2,
                            'margin-left': $scope.settings.width / -2
                        };
                    }
                };

                $scope.$on("$destroy", function (e, data) {
                    element.empty();
                    element.off();
                });
            }
        };
    });
}());
