(function () {
    'use strict';

    angular.module("Fortscale").controller("PackageController",
        ["$scope", "packages", "utils", "conditions", "search", "eventBus",
            function ($scope, packages, utils, conditions, search, eventBus) {
                var packageId,
                    requiredParams,
                    controlParamNames = [];

                $scope.$on("$destroy", function () {
                    eventBus.unsubscribe("dashboardParamsChange", onParamsChange);
                });

                $scope.onControlChange = function (control, value, label) {
                    $scope.paramChange = true;

                    control.value = value;

                    if (control.type === "search" && label) {
                        control.valueLabel = label;
                    }

                    setAllRequiredParamsAvailable(false);
                    setDependantControls(control, value);
                };

                $scope.onControlBlur = function (control) {
                    if (!control.value && control.default) {
                        control.value = control.default;
                    }
                };

                $scope.updateParams = function () {
                    if (!$scope.paramChange || !$scope.paramsReadyToRun) {
                        return;
                    }

                    var params = getParamsFromControls();
                    setAllRequiredParamsAvailable(true);

                    $scope.setParams(params.urlParams);
                    $scope.setParams(params.innerParams, false);

                    $scope.paramChange = false;
                    setLinkToHref(params.innerParams);
                };

                $scope.setPackageError = function (error) {
                    clearMessages();
                    $scope.packageError = error;
                };

                $scope.setPackageLoading = function (isLoading) {
                    clearMessages();
                    $scope.packageLoading = isLoading;
                };

                $scope.setPackageNoData = function (noData) {
                    clearMessages();
                    $scope.packageNoData = !!noData;
                };

                function clearMessages () {
                    $scope.packageError = $scope.packageLoading = $scope.packageNoData = null;
                }

                function setDependantControls (srcControl, value) {
                    $scope.currentPackage.controls.forEach(function (control) {
                        if (srcControl !== control) {
                            var dependency = control.dependencies && control.dependencies[srcControl.param];
                            if (dependency) {
                                if (control.type === "date") {
                                    if (dependency === "lesserThan") {
                                        control.maxValue =
                                            value !== undefined && value !== null ? value : control.settings.maxDate;
                                    }
                                    else if (dependency === "greaterThan") {
                                        control.minValue =
                                            value !== undefined && value !== null ? value : control.settings.minDate;
                                    }
                                }
                            }
                        }
                    });
                }

                function setLinkToHref (params) {
                    var urlParams = [],
                        paramValue,
                        linkParams;

                    function populateUrlParams (d) {
                        if (d) {
                            urlParams.push(paramName + "=" + encodeURIComponent(d));
                        }
                    }

                    for (var paramName in params) {
                        if (params.hasOwnProperty(paramName)) {
                            paramValue = params[paramName];
                            if (angular.isArray(paramValue)) {
                                paramValue.forEach(populateUrlParams);
                            }
                            else if (paramValue) {
                                urlParams.push(paramName + "=" + encodeURIComponent(paramValue));
                            }
                        }
                    }

                    linkParams = angular.extend(getParamsFromControls(), params);

                    if ($scope.currentPackage.linkTo) {
                        $scope.currentPackage.links =
                            angular.isArray($scope.currentPackage.linkTo) ? $scope.currentPackage.linkTo :
                                [$scope.currentPackage.linkTo];

                        $scope.currentPackage.links.forEach(function (link) {
                            link.href = utils.strings.parseValue(link.url, {params: urlParams.join("&")}, linkParams);
                        });
                    }

                    if ($scope.currentPackage.multiLink) {
                        $scope.currentPackage.multiLink.links.forEach(function (link) {
                            link.href =
                                utils.url.escapeUrl(utils.strings.parseValue(link.url, {params: urlParams.join("&")},
                                    linkParams));
                        });
                    }
                }

                function getParamsFromControls () {
                    if (!$scope.currentPackage.controls) {
                        return;
                    }

                    var params = {urlParams: {}},
                        widgetParams = $scope.getWidgetParams();

                    $scope.currentPackage.controls.forEach(function (control) {
                        var innerParam,
                            paramValue;

                        paramValue = params.urlParams[control.param] = !control.value ? null :
                            control.formatParam ?
                                utils.strings.parseValue(control.formatParam, {value: control.value}, widgetParams) :
                                control.value;

                        if (paramValue !== undefined && paramValue !== null) {
                            params[control.param] = paramValue;
                        }

                        if (control.value && control.valueLabel) {
                            params.urlParams[control.param + "_label"] = control.valueLabel;
                        }

                        if (control.innerParam) {
                            if (!params.innerParams) {
                                params.innerParams = {};
                            } else {
                                innerParam = params.innerParams[control.innerParam.name];
                            }

                            var innerParamValue = getInnerParamValue(control, paramValue);

                            if (innerParam) {
                                params.innerParams[control.innerParam.name + "_conjuction"] = "AND";

                                if (!angular.isArray(innerParam)) {
                                    params.innerParams[control.innerParam.name] = [innerParam];
                                }

                                params.innerParams[control.innerParam.name].push(innerParamValue);
                            }
                            else {
                                params.innerParams[control.innerParam.name] = innerParamValue;
                            }
                        }
                    });
                    return params;
                }

                function getInnerParamValue (control, paramValue) {
                    return control.innerParam.type && control.innerParam.type !== "equals" && paramValue ?
                    conditions.getParamOperator(control.innerParam.type) + paramValue : paramValue;
                }

                function setControlParams () {
                    var widgetParams = $scope.getWidgetParams(),
                        innerParams;

                    requiredParams = {};

                    $scope.currentPackage.controls.forEach(function (control) {
                        var paramValue = widgetParams[control.param] || control.defaultValue;
                        if (paramValue) {
                            if (control.type === "date") {
                                control.value = utils.date.getMoment(paramValue) || null;
                                if (control.value) {
                                    control.value = control.value.toDate();
                                }
                            }
                            else if (control.type === "number") {
                                control.value = parseInt(paramValue, 10);
                            } else if (control.type === "boolean") {
                                control.value = paramValue !== "false";
                            } else if (control.type === "search") {
                                control.value = widgetParams[control.param];
                                control.valueLabel = widgetParams[control.param + "_label"];
                            }
                            else {
                                control.value = paramValue;
                            }

                            if (control.innerParam) {
                                if (!innerParams) {
                                    innerParams = {};
                                }

                                var innerParam = innerParams[control.innerParam.name];
                                if (!innerParam) {
                                    innerParam = innerParams[control.innerParam.name] = [];
                                } else {
                                    innerParams[control.innerParam.name + "_conjuction"] = "AND";
                                }

                                innerParam.push(getInnerParamValue(control, paramValue));
                            }
                        }

                        if (control.isRequired) {
                            requiredParams[control.param] = control.label;
                        }
                    });

                    setAllRequiredParamsAvailable(true);
                    if (innerParams) {
                        $scope.setParams(innerParams, false);
                    }

                    setLinkToHref(innerParams || {});
                }

                function setAllRequiredParamsAvailable (setParamsReady) {
                    var allRequiredParamsAvailable = true,
                        requiredParamNames = [];

                    if ($scope.currentPackage.controls) {
                        for (var i = 0, control; !!(control = $scope.currentPackage.controls[i]); i++) {
                            if (control.isRequired && !control.value) {
                                allRequiredParamsAvailable = false;
                                requiredParamNames.push(control.label.toLowerCase());
                            }

                            if (control.disableConditions) {
                                control.disabled = conditions.validateConditions(control.disableConditions,
                                    getParamsFromControls().urlParams, {});
                            }
                        }
                    }

                    $scope.paramsReadyToRun = allRequiredParamsAvailable;
                    if (setParamsReady) {
                        $scope.paramsReady = allRequiredParamsAvailable;
                    }

                    $scope.requiredParamNames = $scope.paramsReady ? null : utils.arrays.toSentence(requiredParamNames);
                }

                function setSearchSettings () {
                    $scope.currentPackage.controls.forEach(function (control) {
                        if (control.type === "search" && control.settings.search) {
                            control.settings.search = control.settings.search.params ?
                                search[control.settings.search.entity].bind(this, control.settings.search.params) :
                                search[control.settings.search.entity];
                        }
                    });
                }

                function init () {
                    var params = $scope.getWidgetParams();
                    packageId = params.entityId ||
                        $scope.dashboard.navigation.children[0].children[0].url.match(/package\/(.*)$/)[1];

                    packages.getPackageById(packageId).then(function (packageConfig) {
                        $scope.setParams({
                            packageName: {value: packageConfig.name, setToUrl: false},
                            packageDescription: {value: packageConfig.description, setToUrl: false}
                        }, false);
                        $scope.currentPackage = packageConfig;
                        $scope.currentPackage.renderParams =
                            packageConfig.controls || packageConfig.linkTo || packageConfig.multiLink;

                        if ($scope.currentPackage.controls) {
                            setControlParams();
                            setSearchSettings();

                            $scope.currentPackage.controls.forEach(function (control) {
                                controlParamNames.push(control.param);
                            });

                            eventBus.subscribe("dashboardParamsChange", onParamsChange);
                        }
                        else {
                            $scope.paramsReady = true;
                            $scope.paramsReadyToRun = true;

                            setLinkToHref(params);
                        }
                    }, function (error) {
                        console.error("Can't load package: ", error);
                    });
                }

                function onParamsChange (e, changedParams) {
                    if (utils.objects.containsAnyParam(changedParams, controlParamNames)) {
                        $scope.$broadcast("packageParamsChange", changedParams);
                    }
                }

                init();
            }]);
}());
