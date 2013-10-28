angular.module("Fortscale").factory("widgetsData", [
    "tableWidgetData",
    "pieChartWidgetData",
    "bubblesChartWidgetData",
    "barsChartWidgetData",
    "percentChartWidgetData",
    "propertiesWidgetData",
    "tabsWidgetData",
    "forceChartWidgetData",
    function(tableWidgetData, pieChartWidgetData, bubblesChartWidgetData, barsChartWidgetData, percentChartWidgetData, propertiesWidgetData, tabsWidgetData, forceChartWidgetData){
        return {
            barsChart: barsChartWidgetData,
            bubblesChart: bubblesChartWidgetData,
            forceChart: forceChartWidgetData,
            table: tableWidgetData,
            percentChart: percentChartWidgetData,
            pieChart: pieChartWidgetData,
            properties: propertiesWidgetData,
            tabs: tabsWidgetData
        }
}]);