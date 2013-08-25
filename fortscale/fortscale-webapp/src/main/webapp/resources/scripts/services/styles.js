angular.module("Fortscale").factory("styles", ["$q", "$http", "conditions", "format", function($q, $http, conditions, format){
    var savedStyles;

    function getColor(r,g,b){
        return "rgb(" + [Math.floor(r),Math.floor(g),Math.floor(b)].join(",") + ")";
    }

    function getSavedStyle(styleName){
        var deferred = $q.defer(),
            style;

        function resolve(){
            if (style = savedStyles[styleName])
                deferred.resolve(style);
            else
                deferred.reject("Style '" + styleName + "' not found.");
        }

        if (savedStyles)
            resolve();
        else{
            $http.get("data/styles.json")
                .success(function(stylesData){
                    savedStyles = stylesData;
                    resolve();
                })
                .error(function(error){
                    deferred.reject(error);
                })
        }

        return deferred.promise;
    }

    var presets = {
        severity: function(settings){
            var colors = {
                    medium: "rgb(226, 172, 1)",
                    high: "#cc0000"
                };

            function getCondition(properties, value){
                return {
                    properties: properties,
                    "conditions": [
                        {
                            "field": settings.conditionField,
                            "fieldType": settings.conditionType,
                            "operator": "greaterThanOrEqual",
                            "value": value
                        }
                    ]
                };
            }

            return [
                getCondition({ color: colors.medium, "font-weight": "bold" }, settings.medium),
                getCondition({ color: colors.high, "font-weight": "bold" }, settings.high)
            ];
        }
    };

    function getStylePreset(options){
        if (!options.preset || !presets[options.preset])
            throw new Error("Invalid style preset, '" + options.preset + "'.");

        var preset = presets[options.preset];
        return preset(options.settings);
    }

    function parseStyle(styleSettings, data, params){
        var style = {};
        function addItemProperties(styleItem){
            for(var property in styleItem.properties){
                style[property] = styleItem.properties[property];
            }
        }
        angular.forEach(styleSettings, function(styleItem){
            if (!styleItem.conditions)
                addItemProperties(styleItem);
            else{
                var conditionValue,
                    conditionField,
                    dataValue,
                    paramMatch;

                for(var i= 0, condition; condition = styleItem.conditions[i]; i++){
                    conditionValue = condition.value;
                    if (/^@/.test(condition.value))
                        conditionValue = data[condition.value];

                    conditionField = condition.field;

                    if (paramMatch = conditionField.match(/^\{\{([^\}]+)\}\}$/))
                        dataValue = params[paramMatch[1]];
                    else
                        dataValue = data[conditionField];

                    if (condition.fieldType)
                        dataValue = format[condition.fieldType](dataValue);

                    if (!conditions.validateCondition(dataValue, condition.operator, condition.value))
                        return;
                }

                addItemProperties(styleItem);
            }
        });

        return style;
    }

    function getStyleParams(field, data){
        if (!field.styleParams)
            return {};

        var styleParams = {};
        for(var paramName in field.styleParams){
            styleParams[paramName] = data[field.styleParams[paramName]];
        }

        return styleParams;
    }

    var methods = {
        getStyle: function(field, data){
            var deferred = $q.defer();

            if (field.style.preset)
                deferred.resolve(getStylePreset(field.style));
            else{
                if (typeof(field.style) === "string"){
                    getSavedStyle(field.style).then(function(styleSettings){
                        deferred.resolve(parseStyle(styleSettings, data, getStyleParams(field, data)));
                    }, deferred.reject);
                }
                else{
                    deferred.resolve(parseStyle(field.style, data, getStyleParams(field, data)));
                }
            }
            return deferred.promise;
        },
        getParseStyleFunction: function(field){
            var deferred = $q.defer();

            if (typeof(field.style) === "string"){
                getSavedStyle(field.style).then(function(styleSettings){
                    deferred.resolve(function(data){
                        return parseStyle(styleSettings, data, getStyleParams(field, data));
                    });
                }, deferred.reject);
            }
            else{
                deferred.resolve(function(data){
                    return parseStyle(field.style, data, getStyleParams(field, data));
                });
            }

            return deferred.promise;
        }
    };

    return methods;
}]);
