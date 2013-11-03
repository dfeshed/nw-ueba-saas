angular.module("Fortscale").controller("GlobalSettingsController", ["$scope", "utils", function($scope, utils){
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

    getScoresWeightDisplay();

    $scope.distributeScoresEvenly = function(){
        var total = 0,
            weight = Math.floor(100 / $scope.scores.length),
            firstWeight = weight + 100 - weight * $scope.scores.length;

        angular.forEach($scope.scores, function(score, index){
            var newWeight = index ? weight : firstWeight;
            if (score.weight !== newWeight){
                score.weight = newWeight;
                $scope.changed = true;
            }
        });

        getScoresWeightDisplay();
    };

    $scope.scoreChange = function(score, scoreIndex){
        getScoresWeightDisplay();
        $scope.changed = true;
    };

    function sortAscending(a, b){
        if (a.weight === b.weight)
            return 0;

        return a.weight > b.weight ? 1 : -1;
    }

    function sortDescending(a, b){
        if (a.weight === b.weight)
            return 0;

        return a.weight > b.weight ? -1 : 1;
    }

    function getScoresWeightDisplay(){
        var totalWeights = 0,
            distributionOrder = [];

        angular.forEach($scope.scores, function(score, index){
            totalWeights += score.weight;
        });

        angular.forEach($scope.scores, function(score, index){
            score.weightDisplay = Math.round((score.weight / totalWeights) * 100) + "%";
        });
    }
}]);