angular.module("Fortscale").controller("ButtonsBarWidgetController", ["$scope", "widgets", function($scope, widgets){
    $scope.btnClick = function(button, $event){
        if (button.toggleOnClick){
            button.on = !button.on;
        }

        angular.forEach($scope.view.settings.events, function(event){
            if (event.eventName === "click")
                $scope.dashboardEvent({ event: event }, $event, { on: button.on }, button);
        });
    };
}]);