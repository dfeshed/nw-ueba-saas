(function () {
    'use strict';

    angular.module("Fortscale").controller("GlobalSettingsController", ["$scope", "utils", function ($scope, utils) {
        $scope.scores = [
            {
                weight: 25,
                name: "Active Directory"
            },
            {
                weight: 25,
                name: "Logins"
            },
            {
                weight: 25,
                name: "VPN"
            },
            {
                weight: 25,
                name: "Group Membership"
            }
        ];

        function getScoresWeightDisplay() {
            var totalWeights = 0;

            angular.forEach($scope.scores, function (score, index) {
                totalWeights += score.weight;
            });

            angular.forEach($scope.scores, function (score, index) {
                score.weightDisplay = Math.round((score.weight / totalWeights) * 100) + "%";
            });
        }

        getScoresWeightDisplay();

        $scope.distributeScoresEvenly = function () {
            var weight = Math.floor(100 / $scope.scores.length),
                firstWeight = weight + 100 - weight * $scope.scores.length;

            angular.forEach($scope.scores, function (score, index) {
                var newWeight = index ? weight : firstWeight;
                if (score.weight !== newWeight) {
                    score.weight = newWeight;
                    $scope.changed = true;
                }
            });

            getScoresWeightDisplay();
        };

        $scope.scoreChange = function (score, scoreIndex) {
            getScoresWeightDisplay();
            $scope.changed = true;
        };


    }]);
}());
