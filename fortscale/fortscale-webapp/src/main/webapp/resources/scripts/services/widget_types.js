angular.module("Fortscale").factory("widgetTypes", ["$q", "$http", "version", function($q, $http, version){
    var widgetTypes;

    var methods = {
        getWidgetType: function(widgetTypeName){
            var deferred = $q.defer();

            $http.get("data/widget_types/" + widgetTypeName + ".json?v=" + version)
                .success(deferred.resolve)
                .error(deferred.reject);

            return deferred.promise;
        },
        getWidgetTypes: function(){
            var deferred = $q.defer();

            if (widgetTypes)
                deferred.resolve(widgetTypes);
            else{
                $http.get("data/widget_types/widget_types.json?v=" + version)
                    .success(function(widgetTypesList){
                        var typesData = {},
                            typesPromises = [];

                        angular.forEach(widgetTypesList, function(widgetTypeName){
                            typesPromises.push(methods.getWidgetType(widgetTypeName));
                        });


                        $q.all(typesPromises).then(function(widgetTypes){
                            angular.forEach(widgetTypes, function(widgetType, i){
                                widgetType.type = widgetTypesList[i];
                                typesData[widgetTypesList[i]] = widgetType;
                            });

                            deferred.resolve(typesData);
                        }, deferred.reject);
                    })
                    .error(deferred.reject);
            }

            return deferred.promise;
        }
    };

    return methods;
}]);