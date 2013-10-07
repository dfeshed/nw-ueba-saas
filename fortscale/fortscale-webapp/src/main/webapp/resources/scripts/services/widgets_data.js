angular.module("Fortscale").factory("widgetsData", [
    "tableWidgetData",
    "pieChartWidgetData",
    "bubblesChartWidgetData",
    "barsChartWidgetData",
    "percentChartWidgetData",
    function(tableWidgetData, pieChartWidgetData, bubblesChartWidgetData, barsChartWidgetData, percentChartWidgetData){
        return {
            barsChart: barsChartWidgetData.getData,
            bubblesChart: bubblesChartWidgetData.getData,
            table: tableWidgetData.getData,
            percentChart: percentChartWidgetData.getData,
            pieChart: pieChartWidgetData.getData
        }
}]);