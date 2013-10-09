angular.module("Icons", ["Conditions", "Format"]).factory("icons", ["$q", "$http", "conditions", "format", function($q, $http, conditions, format){
    var savedIcons;

    function getSavedIcon(presetName){
        var deferred = $q.defer(),
            icon;

        function resolve(){
            if (icon = savedIcons[presetName])
                deferred.resolve(icon);
            else
                deferred.reject("Icon '" + presetName + "' not found.");
        }

        if (savedIcons)
            resolve();
        else{
            $http.get("data/icons.json")
                .success(function(iconsData){
                    savedIcons = iconsData;
                    resolve();
                })
                .error(function(error){
                    deferred.reject(error);
                })
        }

        return deferred.promise;
    }

    function parseIcon(iconPreset, data, params){
        var icon = {};

        angular.forEach(iconPreset, function(iconItem){
            if (!iconItem.conditions)
                icon.type = iconItem.type;
            else{
                var conditionValue,
                    conditionField,
                    dataValue,
                    paramMatch;

                for(var i= 0, condition; condition = iconItem.conditions[i]; i++){
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

                icon.type = iconItem.type;
            }
        });

        return icon;
    }

    function getIconParams(iconSettings, data){
        if (!iconSettings.presetParams)
            return {};

        var iconParams = {};
        for(var paramName in iconSettings.presetParams){
            iconParams[paramName] = data[iconSettings.presetParams[paramName]];
        }

        return iconParams;
    }

    var methods = {
        getIcon: function(iconSettings, data){
            var deferred = $q.defer();

            if (typeof(iconSettings) === "string"){
                deferred.resolve({ type: iconSettings });
                return deferred.promise;
            }

            if (iconSettings.type)
                deferred.resolve(iconSettings.type);
            else if (iconSettings.preset){
                getSavedIcon(iconSettings.preset).then(function(iconPreset){
                    deferred.resolve(parseIcon(iconPreset, data, getIconParams(iconSettings, data)));
                }, deferred.reject);
            }
            else{
                deferred.resolve(parseIcon(field.icon, data, getIconParams(iconSettings, data)));
            }
            
            return deferred.promise;
        },
        getParseIconFunction: function(iconSettings){
            var deferred = $q.defer();

            if (typeof(iconSettings) === "string"){
                getSavedIcon(iconSettings).then(function(iconPreset){
                    deferred.resolve(function(data){
                        return parseIcon(iconPreset, data, getIconParams(iconSettings, data));
                    });
                }, deferred.reject);
            }
            else{
                deferred.resolve(function(data){
                    return parseIcon(iconSettings, data, getIconParams(iconSettings, data));
                });
            }

            return deferred.promise;
        }
    };

    return methods;
}]);
