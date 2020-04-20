(function () {
    'use strict';

    angular.module("Fortscale").controller("ItemsListController", ["$scope", function ($scope) {
        var defaultListItemDisplayCount = 5,
            graceLimitCount = 3;


        function setLimit() {
            $scope.limit = 999999999;
            $scope.disableLimit = false;
            $scope.showAllItems = false;

            if ($scope.view.settings.limit && $scope.items.length > $scope.view.settings.limit + graceLimitCount) {
                $scope.limit = $scope.view.settings.limit;
            }
            else {
                $scope.disableLimit = true;
            }
        }

        $scope.showAll = function (item) {
            item.enableShowLess = true;
            angular.forEach(item.list, function (listItem) {
                listItem.enabled = true;
            });
        };

        $scope.showLess = function (item) {
            item.enableShowLess = false;
            angular.forEach(item.list, function (listItem, index) {
                listItem.enabled = index < defaultListItemDisplayCount;
            });
        };

        $scope.setLimit = function (limit, showAll) {
            $scope.limit = limit;
            $scope.showAllItems = !!showAll;
        };

        $scope.$on("onWidgetData", setLimit);
        setLimit();

    }]);
}());
