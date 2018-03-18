(function () {
    'use strict';

    angular.module("RepeaterWidget", ["Utils", "Widgets"])

        .run(["widgetViews", function (widgetViews) {
            widgetViews.registerView("repeater", {});
        }])

        .controller("RepeaterWidgetController",
        ["$scope", "widgets", "$q", "utils", function ($scope, widgets, $q, utils) {

            $scope.widgetype = $scope.view.settings.widgetType;
            var promises = [];

            function addView (viewData) {
                var view = {
                    settings: $scope.view.settings.widgetSettings,
                    type: $scope.view.settings.widgetType,
                    rawData: viewData
                };

                $scope.repeaterViews.push(view);

                promises.push(widgets.setViewValues(view, viewData, $scope.getWidgetParams()));
            }

            function init () {
                $scope.repeaterViews = [];
                promises = [];

                if ($scope.view.settings.title && $scope.view.data && $scope.view.data.length) {
                    $scope.repeaterTitle = utils.strings.parseValue($scope.view.settings.title, $scope.view.data[0],
                        $scope.getWidgetParams());
                }

                angular.forEach($scope.view.data, function (viewData) {
                    if ($scope.view.settings.repeatField) {
                        angular.forEach(viewData[$scope.view.settings.repeatField], addView);
                    } else {
                        addView(viewData);
                    }
                });

                $q.all(promises).then(function (viewsData) {
                    angular.forEach(viewsData, function (viewData, i) {
                        $scope.repeaterViews[i].data = viewData;
                    });
                });
            }

            $scope.$on("onWidgetData", init);
            init();
        }]);
}());
