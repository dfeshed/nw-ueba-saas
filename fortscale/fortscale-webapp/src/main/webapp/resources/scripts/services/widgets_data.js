angular.module("Fortscale").factory("widgetsData", [
    "tableWidgetData",
    "pieChartWidgetData",
    "bubblesChartWidgetData",
    "barsChartWidgetData",
    "percentChartWidgetData",
    "propertiesWidgetData",
    "tabsWidgetData",
    function(tableWidgetData, pieChartWidgetData, bubblesChartWidgetData, barsChartWidgetData, percentChartWidgetData, propertiesWidgetData, tabsWidgetData){
        return {
            barsChart: barsChartWidgetData.getData,
            bubblesChart: bubblesChartWidgetData.getData,
            table: tableWidgetData.getData,
            percentChart: percentChartWidgetData.getData,
            pieChart: pieChartWidgetData.getData,
            properties: propertiesWidgetData.getData,
            tabs: tabsWidgetData.getData
        }
}]);