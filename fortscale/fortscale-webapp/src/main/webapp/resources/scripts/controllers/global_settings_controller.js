angular.module("Fortscale").controller("GlobalSettingsController", ["$scope", "$timeout", function($scope, $timeout){
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
            name: "Groups"
        }
    ];

    var slideTimeoutPromise;

    $scope.scoreChange = function(score, scoreIndex){
        $timeout.cancel(slideTimeoutPromise);
        slideTimeoutPromise = $timeout(function(){
            distributeScores(scoreIndex)
        }, 40);
    };

    function distributeScores(scoreIndex){
        var totalWeights = 0;
        angular.forEach($scope.scores, function(otherScore, index){
            totalWeights += otherScore.weight;
        });

        var totalDelta = 100 - totalWeights,
            weightToDistribute = Math.abs(totalDelta),
            direction = weightToDistribute / totalDelta,
            weightChange = Math.ceil(weightToDistribute / ($scope.scores.length - 1)),
            lastScoreToChange = scoreIndex === $scope.scores.length - 1 ? $scope.scores.length - 2 : $scope.scores.length - 1;
             console.log("weight To Dis: ", weightToDistribute, " - ", weightChange, " -- ", totalDelta, " / ", $scope.scores.length - 1)

        for(var index= 0, otherScore; otherScore = $scope.scores[index]; index++){
            if (index !== scoreIndex){
                var newWeight;

                if (index === lastScoreToChange){
                    newWeight = otherScore.weight + weightToDistribute * direction;
                    if (newWeight > 100)
                        newWeight = 100;
                    else if (newWeight < 0)
                        newWeight = 0;
                }
                else{
                    newWeight = otherScore.weight + weightChange * direction;
                    if (newWeight < 0){
                        newWeight = 0;
                        weightToDistribute -= otherScore.weight;
                    }
                    else if (newWeight > 100){
                        newWeight = 100;
                        weightToDistribute -= 100 - otherScore.weight;
                    }
                    else
                        weightToDistribute -= Math.abs(weightChange);
                }

                otherScore.weight = newWeight;
                console.log("LEFT: ", weightToDistribute)
                if (!weightToDistribute)
                    break;
            }
        }

        console.log("------------------------")
    }
}]);