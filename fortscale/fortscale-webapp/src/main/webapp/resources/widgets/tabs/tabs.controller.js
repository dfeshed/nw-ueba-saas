angular.module("TabsWidget").controller("TabsWidgetController", ["$scope", function($scope){
    var selectedDashboardParam;

    $scope.selectTab = function(tab, tabIndex, isDefaultTab){
        setCurrentTab(tabIndex);

        if ($scope.view.settings.events){
            angular.forEach($scope.view.settings.events, function(eventSettings){
                if (eventSettings.eventName === "select"){
                    var eventSettingsCopy = angular.copy(eventSettings);
                    if (isDefaultTab && eventSettingsCopy.actionOptions)
                        eventSettingsCopy.actionOptions.updateUrl = false;

                    $scope.$emit("widgetEvent", { event: eventSettingsCopy, data: tab, widget: $scope.widget });
                }
            });
        }

        if ($scope.view.settings.onSelect)
            $scope.view.settings.onSelect(tab);
    };

    function setCurrentTabFromDashboardParam(){
        var currentlySelectedTab = $scope.getWidgetParams()[selectedDashboardParam];
        if (currentlySelectedTab){
            var tabToSelect = findTab(currentlySelectedTab);
            if (tabToSelect){
                if (setCurrentTab(tabToSelect.tabIndex))
                    return true;
                else
                    return -1;
            }
        }

        return false;
    }

    var widgetDataListener;

    function init(){
        if (!$scope.view.data){
            widgetDataListener = $scope.$on("onWidgetData", function(e, data){
                var currentTabSet = setCurrentTabFromDashboardParam();
                if (currentTabSet !== -1)
                    widgetDataListener();
            });
        }
        else if (!setCurrentTabFromDashboardParam())
            selectFirstTab();

        if ($scope.view.settings.refreshOn){
            angular.forEach($scope.view.settings.refreshOn, function(refreshParam){
                $scope.$on("dashboardParamsChange", function(e, changedParams){
                    if (changedParams[refreshParam]){
                        setCurrentTabFromDashboardParam();
                    }
                });
            })
        }
    }

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
        if ($scope.view.settings.tabs && $scope.view.settings.tabs.length)
            $scope.selectTab($scope.view.settings.tabs[0], 0, true);
    }

    function setCurrentTab(tabIndex){
        if (tabIndex !== $scope.currentTabIndex && $scope.view.data){
            $scope.currentTab = $scope.view.data[tabIndex];
            $scope.currentTabIndex = tabIndex;
            return true;
        }

        return false;
    }

    function findTab(id){
        if (!id || !$scope.view.data)
            return null;

        for(var i= 0, tab; tab = $scope.view.data[i]; i++){
            if(tab.id === id)
                return { tab: tab, tabIndex: i };
        }

        return null;
    }

    init();
}]);