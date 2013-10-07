angular.module("BarsChartWidget").factory("barsChartWidgetData", ["chartWidgetsData", function(chartWidgetsData){
    return {
        getData: chartWidgetsData.getData
    }
}]);