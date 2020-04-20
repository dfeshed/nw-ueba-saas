(function () {
    'use strict';

    angular.module("TimelineWidget").controller("TimelineController",
        ["$scope", "timelineService", function ($scope, timelineService) {
            if ($scope.view.settings.getPageReport) {
                $scope.getPage = function (firstTime) {
                    timelineService.getEarlierData($scope.view.settings, {timestamp: firstTime.valueOf()},
                        $scope.view.data).then(function (data) {
                        data.legend = $scope.view.data.legend;
                        $scope.view.data = data;
                    });
                };
            }
        }]);
}());
