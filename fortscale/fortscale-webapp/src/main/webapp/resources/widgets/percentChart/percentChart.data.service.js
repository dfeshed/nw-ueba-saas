angular.module("PercentChartWidget").factory("percentChartWidgetData", ["chartWidgetsData", function(chartWidgetsData){
    return {
        getData: chartWidgetsData.getData
    }
}]);