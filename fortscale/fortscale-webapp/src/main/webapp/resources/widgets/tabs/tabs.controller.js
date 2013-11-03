angular.module("TabsWidget").controller("TabsWidgetController", ["$scope", function($scope){
    var selectedDashboardParam;

    $scope.selectTab = function(tab, tabIndex){
        setCurrentTab(tabIndex);

        if ($scope.view.settings.events){
            angular.forEach($scope.view.settings.events, function(eventSettings){
                if (eventSettings.eventName === "select")
                    $scope.$emit("widgetEvent", { event: eventSettings, data: tab, widget: $scope.widget });
            });
        }

        if ($scope.view.settings.onSelect)
            $scope.view.settings.onSelect(tab);
    };

    $scope.initTabs = function(){
        var currentlySelectedTab = $scope.getWidgetParams()[selectedDashboardParam];
        if (currentlySelectedTab){
            var tabToSelect = findTab(currentlySelectedTab);
            if (tabToSelect)
                setCurrentTab(tabToSelect.tabIndex);
            else
                selectFirstTab();
        }
        else
            selectFirstTab();
    };

    if ($scope.view.settings.tab && $scope.view.settings.tab.selected){
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
    else if ($scope.view.settings.tabs && $scope.view.settings.selectedTabId){
        var selectedMatch = $scope.view.settings.selectedTabId.match(/^@(.*)$/);
        if (selectedMatch)
            selectedDashboardParam = selectedMatch[1];
    }

    function selectFirstTab(){
        $scope.selectTab($scope.view.settings.tabs[0], 0);
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