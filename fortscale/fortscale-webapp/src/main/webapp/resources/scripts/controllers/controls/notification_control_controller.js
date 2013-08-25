angular.module("Fortscale").controller("NotificationControlController", ["$scope", "widgets", function($scope, widgets){
    $scope.$on("dashboardParamsChange", function(e, params){
        $scope.notificationShow = !!params[$scope.control.showIf];
        setValue();
    });

    $scope.notificationButtonClick = function(button){
        $scope.$emit("dashboardEvent", { event: button.onClick, data: {} });
    };

    setValue();

    function setValue(){
        $scope.notificationValue = widgets.parseFieldValue({}, $scope.control.value, {}, 0, $scope.dashboardParams);
    }
}]);
