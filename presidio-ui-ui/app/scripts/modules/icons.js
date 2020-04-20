(function () {
    'use strict';

    angular.module("Icons", ["Conditions", "Format", "Styles"])
        .factory("icons", ["$q", "$http", "conditions", "format", "styles", "utils",
            function ($q, $http, conditions, format, styles, utils) {
                var savedIcons,
                    onLoad = [],
                    loadingData;

                function getSavedIcon(presetName) {
                    if (savedIcons) {
                        if (savedIcons[presetName]) {
                            return $q.when(savedIcons[presetName]);
                        } else {
                            return $q.reject("Icon '" + presetName + "' not found.");
                        }
                    }
                    else {
                        if (loadingData) {
                            var deferred = $q.defer();
                            onLoad.push({name: presetName, deferred: deferred});
                            return deferred.promise;
                        }

                        loadingData = true;
                        return utils.http.wrappedHttpGet("data/icons.json").then(function (iconsData) {
                            savedIcons = iconsData;

                            onLoad.forEach(function (_onLoad) {
                                getSavedIcon(_onLoad.name).then(function (icon) {
                                    _onLoad.deferred.resolve(icon);
                                }, _onLoad.deferred.reject);
                            });

                            return getSavedIcon(presetName);
                        });
                    }
                }

                function getIconPresetStyles(iconPreset) {
                    var iconItemIndex = 0,
                        iconItem = iconPreset[iconItemIndex],
                        styleParsers = {},
                        stylePromises = [];

                    do {
                        iconItem.__id = iconItemIndex;

                        if (iconItem.style) {
                            stylePromises.push(styles.getParseStyleFunction(iconItem));
                        } else {
                            stylePromises.push($q.when(null));
                        }

                    } while (!!(iconItem = iconPreset[++iconItemIndex]));

                    return $q.all(stylePromises).then(function (_styleParsers) {
                        _styleParsers.forEach(function (styleParser, i) {
                            if (styleParser) {
                                styleParsers[i] = styleParser;
                            }
                        });
                        return styleParsers;
                    });
                }

                function parseIconSync(iconPreset, data, params, styles) {
                    var icon = {},
                        iconItemIndex = 0,
                        iconItem = iconPreset[iconItemIndex];

                    do {
                        if (!iconItem.conditions) {
                            icon.type = iconItem.type;

                        } else {
                            var conditionValue,
                                conditionField,
                                dataValue,
                                paramMatch;

                            for (var i = 0, condition; !!(condition = iconItem.conditions[i]); i++) {
                                conditionValue = condition.value;
                                if (/^@/.test(condition.value)) {
                                    conditionValue = data[condition.value];
                                }

                                conditionField = condition.field;
                                paramMatch = conditionField.match(/^\{\{([^\}]+)\}\}$/);

                                if (paramMatch) {
                                    dataValue = params[paramMatch[1]];
                                } else {
                                    dataValue = data[conditionField];
                                }

                                if (condition.fieldType) {
                                    dataValue = format[condition.fieldType](dataValue);
                                }

                                if (conditions.validateCondition(dataValue, condition.operator, condition.value)) {
                                    icon.type = iconItem.type;
                                    break;
                                }
                            }
                        }

                        if (iconItem.tooltip) {
                            icon.tooltip = utils.strings.parseValue(iconItem.tooltip, data, params);
                        }

                        if (icon.type) {
                            if (styles[iconItemIndex]) {
                                icon.style = styles[iconItemIndex](data);
                            }

                            return icon;
                        }
                    } while (!!(iconItem = iconPreset[++iconItemIndex]));

                    return icon;
                }

                function parseIcon(iconPreset, data, params) {
                    return getIconPresetStyles(iconPreset, params).then(function (styles) {
                        return parseIconSync(iconPreset, data, params, styles);
                    });
                }

                function getIconParams(iconSettings, data) {
                    if (!iconSettings.presetParams) {
                        return {};
                    }

                    var iconParams = {};
                    for (var paramName in iconSettings.presetParams) {
                        if (iconSettings.presetParams.hasOwnProperty(paramName)) {
                            iconParams[paramName] = utils.objects.getObjectByPath(data,
                                iconSettings.presetParams[paramName]);
                        }
                    }

                    return iconParams;
                }

                var methods = {
                    getIcon: function (iconSettings, data) {
                        function parseAndReturnIcon(iconPreset) {
                            var params = getIconParams(iconSettings, data);

                            return parseIcon(iconPreset, data, params).then(function (icon) {
                                if (iconSettings.tooltip) {
                                    icon.tooltip = utils.strings.parseValue(iconSettings.tooltip, data, params);
                                }

                                return icon;
                            });
                        }

                        if (typeof(iconSettings) === "string") {
                            return $q.when({type: iconSettings});
                        }

                        if (iconSettings.type) {
                            return $q.when(iconSettings.type);
                        } else if (iconSettings.map) {
                            var fieldValue = data[iconSettings.mapField];
                            if (fieldValue) {
                                return $q.when(iconSettings.map[fieldValue]);
                            }
                        } else if (iconSettings.preset) {
                            return getSavedIcon(iconSettings.preset).then(parseAndReturnIcon);
                        } else {
                            return parseAndReturnIcon(iconSettings);
                        }


                    },
                    getIconParsers: function (iconsSettings) {
                        var promises = [];
                        iconsSettings.forEach(function (iconSettings) {
                            promises.push(methods.getParseIconFunction(iconSettings));
                        });

                        return $q.all(promises);
                    },
                    getParseIconFunction: function (iconSettings) {
                        if (typeof(iconSettings) === "string") {
                            return getSavedIcon(iconSettings).then(function (iconPreset) {
                                return function (data) {
                                    return parseIcon(iconPreset, data, getIconParams(iconSettings, data));
                                };
                            }, function (error) {
                                return function () {
                                    return {type: iconSettings};
                                };
                            });
                        } else if (iconSettings.preset) {
                            return getSavedIcon(iconSettings.preset).then(function (iconPreset) {
                                var presetCopy = angular.copy(iconPreset);
                                presetCopy.forEach(function (iconItem) {
                                    angular.extend(iconItem, iconSettings);
                                });
                                iconPreset = presetCopy;

                                return getIconPresetStyles(iconPreset, iconSettings).then(function (styles) {
                                    return function (data) {
                                        var params = getIconParams(iconSettings, data),
                                            icon = parseIconSync(iconPreset, data, params, styles);

                                        if (iconSettings.tooltip) {
                                            icon.tooltip = utils.strings.parseValue(iconSettings.tooltip, data,
                                                params);
                                        }

                                        return icon;
                                    };
                                });
                            });
                        }
                        else {
                            return $q.when(function (data) {
                                return parseIcon(iconSettings, data, getIconParams(iconSettings, data));
                            });
                        }
                    }
                };

                return methods;
            }]);
}());
