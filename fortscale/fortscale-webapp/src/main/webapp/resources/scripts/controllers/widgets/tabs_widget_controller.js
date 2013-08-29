angular.module("Fortscale").controller("TabsWidgetController", ["$scope", function($scope){
    var selectedDashboardParam;

    setCurrentTab(0);

    $scope.selectTab = function(tab, tabIndex){
        setCurrentTab(tabIndex);

        if ($scope.view.settings.events){
            angular.forEach($scope.view.settings.events, function(eventSettings){
                if (eventSettings.eventName === "select")
                    $scope.$emit("widgetEvent", { event: eventSettings, data: tab, widget: $scope.widget });
            });
        }
    };

    if ($scope.view.settings.tab.selected){
        var selectedMatch = $scope.view.settings.tab.selected.match(/^@(.*)$/);
        if (selectedMatch){
            selectedDashboardParam = selectedMatch[1];
            $scope.$on("dashboardParamsChange", function(e, changedParams){
                if (changedParams[selectedDashboardParam]){
                    var tabToSelect = findTab(changedParams[selectedDashboardParam]);
                    if (tabToSelect)
                        setCurrentTab(tabToSelect.tabIndex);
                    else{
                        $scope.currentTab = null;
                        $scope.currentTabIndex = null;
                    }
                }
            });
        }
        else{
            console.error("Invalid selected property for tabs - must be a dashboard param and start with '@'.");
        }
    }

    function setCurrentTab(tabIndex){
        if (tabIndex !== $scope.currentTabIndex && $scope.view.data){
            $scope.currentTab = $scope.view.data[tabIndex];
            $scope.currentTabIndex = tabIndex;
        }
    }

    function findTab(id){
        if (!id)
            return null;

        for(var i= 0, tab; tab = $scope.view.data[i]; i++){
            if(tab.id === id)
                return { tab: tab, tabIndex: i };
        }

        return null;
    }
}]);