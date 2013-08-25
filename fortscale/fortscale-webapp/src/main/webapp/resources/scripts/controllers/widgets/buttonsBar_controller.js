angular.module("Fortscale").controller("ButtonsBarWidgetController", ["$scope", "widgets", function($scope, widgets){
    $scope.btnClick = function(button, $event){
        if (button.toggleOnClick){
            button.on = !button.on;
        }

        $scope.fireEvent(button.events.onClick, $event, { on: button.on }, button);
    };
}]);