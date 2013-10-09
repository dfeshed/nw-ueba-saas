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
            barsChart: barsChartWidgetData.getData,
            bubblesChart: bubblesChartWidgetData.getData,
            forceChart: forceChartWidgetData.getData,
            table: tableWidgetData.getData,
            percentChart: percentChartWidgetData.getData,
            pieChart: pieChartWidgetData.getData,
            properties: propertiesWidgetData.getData,
            tabs: tabsWidgetData.getData
        }
}]);