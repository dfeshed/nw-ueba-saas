(function () {
    'use strict';

    angular.module("Styles", ["Format", "Conditions"])
        .factory("styles", ["$q", "$http", "conditions", "format", "utils",
            function ($q, $http, conditions, format, utils) {
                var savedStyles,
                    onStylesLoad = [],
                    loadingStyles;

                function getSavedStyle(styleName) {
                    if (savedStyles) {
                        var style = savedStyles[styleName];
                        if (style) {
                            return $q.when(style);
                        } else {
                            return $q.reject("Style '" + styleName + "' not found.");
                        }
                    }

                    if (loadingStyles) {
                        var deferred = $q.defer();
                        onStylesLoad.push({name: styleName, deferred: deferred});
                        return deferred.promise;
                    } else {
                        loadingStyles = true;
                        return utils.http.wrappedHttpGet("data/styles.json").then(function (stylesData) {
                            savedStyles = stylesData;
                            loadingStyles = false;

                            if (onStylesLoad.length) {
                                onStylesLoad.forEach(function (onLoad) {
                                    getSavedStyle(onLoad.name).then(function (style) {
                                        onLoad.deferred.resolve(style);
                                    });
                                });
                                onStylesLoad = null;
                            }
                            return getSavedStyle(styleName);
                        });
                    }
                }

                var presets = {
                    severity: function (settings) {
                        var colors = {
                            medium: "rgb(226, 172, 1)",
                            high: "#cc0000"
                        };

                        function getCondition(properties, value) {
                            return {
                                properties: properties,
                                "conditions": [
                                    {
                                        "field": settings.conditionField,
                                        "fieldType": settings.conditionType,
                                        "operator": "greaterThanOrEquals",
                                        "value": value
                                    }
                                ]
                            };
                        }

                        return [
                            getCondition({color: colors.medium, "font-weight": "bold"}, settings.medium),
                            getCondition({color: colors.high, "font-weight": "bold"}, settings.high)
                        ];
                    }
                };

                function getStylePreset(options) {
                    if (!options.preset || !presets[options.preset]) {
                        throw new Error("Invalid style preset, '" + options.preset + "'.");
                    }

                    var preset = presets[options.preset];
                    return preset(options.settings);
                }

                function parseStyle(styleSettings, data, params) {
                    var style = {};

                    function addItemProperties(styleItem) {
                        for (var property in styleItem.properties) {
                            if (styleItem.properties.hasOwnProperty(property)) {
                                style[property] = styleItem.properties[property];
                            }
                        }
                    }

                    angular.forEach(styleSettings, function (styleItem) {
                        if (!styleItem.conditions) {
                            addItemProperties(styleItem);

                        } else {
                            var conditionValue,
                                conditionField,
                                dataValue,
                                paramMatch;

                            for (var i = 0, condition; (condition = styleItem.conditions[i]) !== undefined; i++) {
                                conditionValue = condition.value;
                                if (/^@/.test(condition.value)) {
                                    conditionValue = data[condition.value];
                                }

                                conditionField = condition.field;

                                if (!!(paramMatch = conditionField.match(/^\{\{([^\}]+)\}\}$/))) {
                                    dataValue = params[paramMatch[1]];
                                } else {
                                    dataValue = data[conditionField];
                                }

                                if (condition.fieldType) {
                                    dataValue = format[condition.fieldType](dataValue);
                                }

                                if (!conditions.validateCondition(dataValue, condition.operator, condition.value)) {
                                    return;
                                }
                            }

                            addItemProperties(styleItem);
                        }
                    });

                    return style;
                }

                function getStyleParams(field, data) {
                    if (!field.styleParams) {
                        return {};
                    }

                    var styleParams = {};
                    for (var paramName in field.styleParams) {
                        if (field.styleParams.hasOwnProperty(paramName)) {
                            styleParams[paramName] = data[field.styleParams[paramName]];
                        }
                    }

                    return styleParams;
                }

                var methods = {
                    getStyle: function (field, data) {
                        if (field.style.preset) {
                            return $q.when(getStylePreset(field.style));

                        } else {
                            if (typeof(field.style) === "string") {
                                return getSavedStyle(field.style).then(function (styleSettings) {
                                    return parseStyle(styleSettings, data, getStyleParams(field, data));
                                });
                            } else {
                                return parseStyle(field.style, data, getStyleParams(field, data));
                            }
                        }
                    },
                    getParseStyleFunction: function (field) {
                        if (typeof(field.style) === "string") {
                            return getSavedStyle(field.style).then(function (styleSettings) {
                                return function (data) {
                                    return parseStyle(styleSettings, data, getStyleParams(field, data));
                                };
                            });
                        } else {
                            return function (data) {
                                return parseStyle(field.style, data, getStyleParams(field, data));
                            };
                        }
                    }
                };

                return methods;
            }]);
}());
