(function () {
    'use strict';

    angular.module("Colors", []).factory("colors", [function () {
        var colors = {
            success: "rgb(50, 146, 36)",
            failure: "#D77576"
        };

        var colorMaps = {
            severity: {
                low: '#80BFF0',
                medium: '#F1CC37',
                high: '#F59925',
                critical: '#D77576'
            },
            scale: ['#D6D6D6', '#880000'],
            binary: ['#D6D6D6', '#880000'],
            sshStatus: {
                accepted: colors.success,
                failed: colors.failure
            },
            status: {
                "success": colors.success,
                "failure": colors.failure
            }
        };

        var defaultRange = ['#D6D6D6', '#880000'];

        var methods = {
            getScale: function (colorSettings) {
                if (colorSettings && angular.isObject(colorSettings)) {
                    if (colorSettings.map) {
                        var defaultColor = colorSettings.map._default || "#000000",
                            field = colorSettings.field,
                            map = typeof(colorSettings.map) === "string" ? colorMaps[colorSettings.map] :
                                colorSettings.map;

                        if (!map || !angular.isObject(map)) {
                            throw new Error("Invalid color map, " + colorSettings.map);
                        }

                        return function (d) {
                            var value = angular.isObject(d) ? d[field] : d,
                                mapValue = map[value.toLowerCase()];
                            if (!value || !mapValue) {
                                return defaultColor;
                            }
                            return mapValue;
                        };
                    }
                    else if (colorSettings.domain) {
                        var rangeCopy;
                        if (colorSettings.range && colorSettings.rangeMap) {
                            var rangeMap = colorMaps[colorSettings.rangeMap];
                            rangeCopy = colorSettings.range && angular.copy(colorSettings.range);

                            if (rangeMap) {
                                for (var i = 0, color; i < rangeCopy.length; i++) {
                                    color = rangeMap[rangeCopy[i]];
                                    if (color) {
                                        rangeCopy[i] = color;
                                    }
                                }
                            }
                        }
                        return d3.scale.linear().domain(colorSettings.domain)
                            .range(rangeCopy || colorSettings.range || defaultRange);
                    }
                }

                if (colorSettings) {
                    if (colorSettings === "score" || colorSettings === "severity") {
                        return d3.scale.linear().domain([0, 50, 51, 79, 80, 94, 95, 100])
                            .range([
                                colorMaps.severity.low, colorMaps.severity.low,
                                colorMaps.severity.medium, colorMaps.severity.medium,
                                colorMaps.severity.high, colorMaps.severity.high,
                                colorMaps.severity.critical, colorMaps.severity.critical
                            ]);
                    }

                    if (colorSettings === "binary" || colorSettings === "scale") {
                        return d3.scale.linear().domain([0, 1]).range([colorMaps[colorSettings][0],
                            colorMaps[colorSettings][1]]);
                    }

                    var d3Scale = d3.scale[colorSettings];
                    if (d3Scale) {
                        return d3Scale();
                    }
                }

                return d3.scale.category20c();
            },
            getColors: function (type) {
                return type ? colorMaps[type] : colorMaps;
            }
        };

        return methods;
    }]);
}());
