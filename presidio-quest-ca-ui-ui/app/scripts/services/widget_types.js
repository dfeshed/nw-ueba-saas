(function () {
    'use strict';

    angular.module("Fortscale").factory("widgetTypes", ["$q", "$http", "version", function ($q, $http, version) {
        var widgetTypes, cachedDefinitions = {};

        var methods = {
            getWidgetType: function (widgetTypeName) {
                var deferred = $q.defer();

                if (cachedDefinitions[widgetTypeName]) {
                    deferred.resolve(cachedDefinitions[widgetTypeName]);
                } else {
                    $http.get("widgets/" + widgetTypeName + "/" + widgetTypeName + ".definition.json?v=" +
                        version).success(function (definition) {
                        deferred.resolve(definition);
                        cachedDefinitions[widgetTypeName] = definition;
                    }).error(deferred.reject);
                }
                return deferred.promise;
            }, getWidgetTypes: function () {
                var deferred = $q.defer();

                if (widgetTypes) {
                    deferred.resolve(angular.copy(widgetTypes));
                } else {
                    $http.get("data/widget_types/widget_types.json?v=" + version).success(function (widgetTypesList) {
                        var typesData = {}, typesPromises = [];

                        angular.forEach(widgetTypesList, function (widgetTypeName) {
                            typesPromises.push(methods.getWidgetType(widgetTypeName));
                        });

                        $q.all(typesPromises).then(function (_widgetTypes) {
                            angular.forEach(_widgetTypes, function (widgetType, i) {
                                widgetType.type = widgetTypesList[i];
                                typesData[widgetTypesList[i]] = widgetType;
                            });

                            widgetTypes = typesData;
                            deferred.resolve(angular.copy(typesData));
                        }, deferred.reject);
                    }).error(deferred.reject);
                }

                return deferred.promise;
            }
        };

        return methods;
    }]);
}());
