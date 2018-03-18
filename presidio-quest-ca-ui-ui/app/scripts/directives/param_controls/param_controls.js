(function () {
    'use strict';

    function paramControls (conditions, eventBus, utils, search, $timeout, controls, FilterValidators,
                            popupConditions) {
        return {
            restrict: 'E',
            templateUrl: "scripts/directives/param_controls/param_controls.template.html",
            replace: true,
            scope: {
                controls: "=",
                buttons: "=",
                search: "=",
                getParams: "&",
                setParams: "&",
                ready: "=",
                classname: "@",
                widgetControlId: "@",
                widget: "=",
                controlsTitle: "@"
            },
            link: function postLink (scope) {
                var requiredParams,
                    controlParamNames = [],
                    changedParams = {};

                scope.renderUpdateButton = false;
                scope.$on("$destroy", function () {
                    eventBus.unsubscribe("dashboardParamsChange", onParamsChange);
                });

                // in the cases controls selection have changed
                // load controls from control service and update the update button if needed.
                scope.$watch("controls", function (_controls) {
                    scope.controls = controls.loadControls(_controls);
                    setUpdateButton();
                    init();
                });

                scope.charEntered = function () {
                    scope.paramChange = true;
                };
                scope.onControlChange = function (control, value, label) {
                    scope.paramChange = true;
                    changedParams[control.param] = true;

                    control.value = value;

                    if (control.type === "search" && label) {
                        control.valueLabel = label;
                    }

                    setAllRequiredParamsAvailable(false);
                    setDependantControls(control, value);

                    if (control.warning) {
                        control.showWarning =
                            conditions.validateConditions(control.warning.conditions, {value: control.value});
                    }

                    if (control.autoUpdate) {
                        scope.updateParams();
                    }
                };

                scope.onControlBlur = function (control) {
                    if (!control.value && control.value !== 0 && control.default) {
                        control.value = control.default;
                    }
                };

                scope.updateParams = function () {
                    if (!scope.paramChange || !scope.paramsReadyToRun) {
                        return;
                    }

                    var params = getParamsFromControls(true);

                    var popupMessage = popupConditions.shouldNotifyPopup(params);
                    if (popupMessage !== "") {
                        bootbox.confirm({
                            message: popupMessage,
                            buttons: {
                                'cancel': {
                                    label: 'No'
                                },
                                'confirm': {
                                    label: 'Yes'
                                }
                            },
                            callback: function (result) {
                                if (result) {
                                    updateParamsAux(params);
                                }
                            }
                        });
                    } else {
                        updateParamsAux(params);
                    }

                };

                function updateParamsAux (params) {
                    setAllRequiredParamsAvailable(true);

                    //if the new changed params doesn't contain a specific page but the current params does, reset the
                    // paging and go to the first page.
                    var scopeParams = scope.getParams();
                    if (scopeParams && scopeParams.page && params.urlParams && !params.urlParams.page) {
                        params.urlParams.page = 1;
                    }

                    // Timeout so the setParams runs AFTER the dashboard scope params are changed by
                    // setAllRequiredParamsAvailable. Otherwise, widgets in the dashboard that listen to changes in
                    // params/paramsReady are not updated.
                    $timeout(function () {
                        scope.setParams({params: params.urlParams});

                        if (params.innerParams && Object.keys(params.innerParams).length) {
                            scope.setParams({params: params.innerParams, setToUrl: false});
                        }
                    }, 1);

                    scope.paramChange = false;
                    updateButtons(angular.extend({}, params.innerParams, params.urlParams));
                    changedParams = {};
                }

                function setUpdateButton () {
                    if (scope.controls) {
                        for (var i = 0, control; !!(control = scope.controls[i]); i++) {
                            if (!control.autoUpdate && !control.hide) {
                                scope.renderUpdateButton = true;
                                return;
                            }
                        }
                    }

                    scope.renderUpdateButton = false;
                }

                function setDependantControls (srcControl, value) {
                    scope.controls.forEach(function (control) {
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

                scope.requiredValueEmpty = function (control) {
                    return (control && control.isRequired && (!control.value && control.value !== 0) && control.label);

                };

                //check if the control value is valid according to the control validator
                scope.isNotValidValue = function (control) {
                    if (control && control.filterValidator) {
                        try {
                            var validator = FilterValidators.getValidator(control.filterValidator, control.value);
                            validator.validate();
                        } catch (errorMessage) {
                            return true;
                        }
                    }
                    return false;
                };

                function setAllRequiredParamsAvailable (setParamsReady) {
                    var allRequiredParamsAvailable = true,
                        allValueParamsValid = true,
                        requiredParamNames = [];

                    if (scope.controls) {
                        for (var i = 0, control; scope.controls[i]; i++) {
                            control = scope.controls[i];

                            //in the case the control is required check if it have value
                            if (scope.requiredValueEmpty(control)) {
                                allRequiredParamsAvailable = false;
                                requiredParamNames.push(control.label.toLowerCase());
                            }
                            //in the case the control have a validator check that the value is valid
                            if (scope.isNotValidValue(control)) {
                                allValueParamsValid = false;
                            }

                            if (control.disableConditions) {
                                control.disabled = conditions.validateConditions(control.disableConditions,
                                    getParamsFromControls().urlParams, {});
                            }
                        }
                    }
                    //uses for the ng-disable of the update button
                    scope.paramsReadyToRun = allRequiredParamsAvailable && allValueParamsValid;
                    if (setParamsReady) {
                        scope.paramsReady = allRequiredParamsAvailable && allValueParamsValid;
                    }

                    scope.requiredParamNames = scope.paramsReady ? null : utils.arrays.toSentence(requiredParamNames);
                }

                function getParamsFromControls () {
                    var params = {urlParams: {}},
                        widgetParams = scope.getParams();

                    if (scope.controls) {
                        scope.controls.forEach(function (control) {

                            if (control.defaultValueOnly && widgetParams[control.param]) {
                                return true;
                            }

                            var innerParam,
                                paramValue = null;

                            if (control.settings && control.settings.useItemParams) {
                                control.settings.items.forEach(function (item) {
                                    params.urlParams[item.param] =
                                        control.value && ~control.value.indexOf(item.param) ? "true" : null;
                                });
                            }
                            else {
                                controls.getControlValue(control, widgetParams, params.urlParams);
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
                    }

                    return params;
                }

                function getInnerParamValue (control, paramValue, params) {
                    var value = control.innerParam.type && control.innerParam.type !== "equals" &&
                    (paramValue || paramValue === 0) ?
                    conditions.getParamOperator(control.innerParam.type) + paramValue : paramValue;
                    if (control.innerParam.formatParam) {
                        return utils.strings.parseValue(control.innerParam.formatParam, {value: value}, params);
                    }

                    return value;
                }

                /**
                 * Takes the current params and fills the controls values accordingly
                 */
                function setControlParams () {
                    var widgetParams = scope.getParams() || {},
                        innerParams = null;

                    requiredParams = {};

                    if (scope.controls) {
                        scope.controls.forEach(function (control) {
                            if (control.settings && control.settings.useItemParams) {
                                control.value = [];
                                control.settings.items.forEach(function (item) {
                                    if (widgetParams[item.param]) {
                                        control.value.push(item.param);
                                    }
                                });
                            }
                            else {
                                setParamsToControl(control, widgetParams, innerParams);
                            }

                            if (control.isRequired) {
                                requiredParams[control.param] = control.label;
                            }
                        });
                    }

                    setAllRequiredParamsAvailable(true);
                    if (innerParams) {
                        scope.setParams({params: innerParams, setToUrl: false});
                    }

                    updateButtons(angular.extend({}, scope.getParams(), innerParams));
                }

                /**
                 * Sets the contents of a control according to the available params.
                 * @param control
                 * @param params
                 * @param innerParams
                 */
                function setParamsToControl (control, params, innerParams) {
                    var paramValue;
                    var dates;

                    if (params[control.param] || params[control.param] === 0) {
                        paramValue = params[control.param];
                    } else {
                        paramValue = control.defaultValue;
                    }

                    if (paramValue || paramValue === 0) {
                        if (control.type === "date") {
                            control.value = utils.date.getMoment(paramValue) || null;
                            if (control.value) {
                                control.value = control.value.toDate();
                            }
                        }
                        else if (control.type === "dateRange") {
                            if (paramValue && typeof paramValue === "string") {
                                dates = paramValue.match(/^(\d+)\,(\d+)$/);
                            }

                            if (dates) {
                                control.value = {
                                    timeStart: utils.date.getMoment(dates[1]).toDate(),
                                    timeEnd: utils.date.getMoment(dates[2]).toDate()
                                };
                            }
                        }
                        else if (control.type === "number") {
                            control.value =
                                typeof(paramValue) === "string" ? parseInt(paramValue.match(/\d+/), 10) : paramValue;
                        } else if (control.type === "boolean") {
                            control.value = paramValue !== "false";
                        } else if (control.type === "search") {
                            control.value = params[control.param];
                            control.valueLabel = params[control.param + "_label"];
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

                            innerParam.push(getInnerParamValue(control, paramValue, params));
                        }
                    }
                }

                function onParamsChange (e, changedParams) {
                    if (utils.objects.containsAnyParam(changedParams, controlParamNames)) {
                        scope.$broadcast("packageParamsChange", changedParams);
                    }

                    updateButtons(scope.getParams());
                    setControlParams();
                }

                var buttonsUpdateFunctions = {
                    link: function (button, data, params) {
                        button.settings.href = utils.strings.parseValue(button.settings.url, data, params);
                        button.settings.parsedText = utils.strings.parseValue(button.settings.text, data, params);
                    },
                    multiLink: function (button, data, params) {
                        button.settings.links.forEach(function (link) {
                            link.href = utils.url.escapeUrl(utils.strings.parseValue(link.url, data, params));
                            link.parsedText = utils.strings.parseValue(link.text, data, params);
                        });
                    }
                };

                function updateButtons (params) {
                    if (!scope.buttons || !scope.buttons.length) {
                        return;
                    }

                    var urlParams = [],
                        paramValue,
                        linkParams;

                    function populateUrlParam (d) {
                        if (d) {
                            urlParams.push(paramName + "=" + encodeURIComponent(d));
                        }
                    }

                    for (var paramName in params) {
                        if (params.hasOwnProperty(paramName)) {
                            paramValue = params[paramName];
                            if (angular.isArray(paramValue)) {
                                paramValue.forEach(populateUrlParam);
                            }
                            else if (paramValue) {
                                urlParams.push(paramName + "=" + encodeURIComponent(paramValue));
                            }
                        }
                    }

                    var controlParams = getParamsFromControls();
                    linkParams = angular.extend(controlParams.urlParams, controlParams.innerParams, params);

                    scope.buttons.forEach(function (button) {
                        button._show =
                            !button.requiredParams || utils.objects.containsAllParams(params, button.requiredParams);
                        var updater = buttonsUpdateFunctions[button.type];
                        if (updater) {
                            updater(button, {params: urlParams.join("&")}, linkParams);
                        }
                    });
                }

                function setSearchSettings (control) {

                    // if this is the second time for this control, we already binded the search function
                    if (typeof(control.settings.search) === "function") {
                        return;
                    }

                    if (!control.settings || !control.settings.search) {
                        throw new Error("Can't set search for control, missing the search object.");
                    }

                    if (Object(control.settings.search) !== control.settings.search) {
                        throw new TypeError("Invalid search settings for control, expected an object but got " +
                            control.settings.search + ".");
                    }

                    if (!control.settings.search.dataEntity || !control.settings.search.dataEntityField) {
                        throw new Error("Can't set search for control, both settings.search.dataEntity and " +
                            "settings.search.dataEntityField properties are required.");
                    }

                    /* jshint validthis: true */
                    if (control.settings.search) {
                        control.settings.search =
                            search.searchDataEntityField.bind(this, control.settings.search.dataEntity,
                                control.settings.search.dataEntityField, control.settings.search.labelField,
                                control.settings.search.extraTerms);
                    }
                }

                function init () {
                    if (scope.search) {
                        setSearchSettings(scope.search);
                    }

                    eventBus.subscribe("dashboardParamsChange", onParamsChange);

                    if (scope.controls) {
                        setControlParams();
                        scope.controls.forEach(function (control) {
                            if (control.type === "search") {
                                setSearchSettings(control);
                            }
                        });

                        scope.controls.forEach(function (control) {
                            controlParamNames.push(control.param);
                        });

                        setAllRequiredParamsAvailable();
                        getParamsFromControls();
                    }
                    else {
                        scope.paramsReady = true;
                        scope.paramsReadyToRun = true;
                        updateButtons(scope.getParams());
                    }
                }
            }
        };
    }

    paramControls.$inject =
        ["conditions", "eventBus", "utils", "search", "$timeout", "controls", "FilterValidators",
            "popupConditions"];

    angular.module("Fortscale").directive("paramControls", paramControls);

})();
