(function () {
    'use strict';

    angular.module("Controls").factory("controls",
        ["Control", "$q", "utils", "conditions", function (Control, $q, utils, conditions) {

            function checkInit () {
                if (!controls) {
                    throw new Error("Controls are not initialized yet.");
                }
            }

            function setControls () {
                controls = new Map();
                window.__controlsConfig__.forEach(function (controlConfig) {
                    var control = new Control(controlConfig);
                    controls.set(controlConfig.controlId, control);
                });

                // Clean-up, remove the temporary controls:
                delete window.__controlsConfig__;
            }

            var controls;

            /**
             * update control specific properties according to control type
             **/
            var controlInitMethods = {
                link: function (control, data, params) {
                    if (!control._href) {
                        control._href = control.href;
                    }

                    control.href = utils.strings.parseValue(control._href, data, params);
                    control.text = utils.strings.parseValue(control.text, data, params);
                },
                select: function (control) {

                    function checkConditions (option) {
                        return !(option.conditions && !conditions.validateConditions(option.conditions, null, null));
                    }

                    if (!control.settings._options) {
                        control.settings._options = control.settings.options;
                    }

                    control.settings._options = control.settings._options.filter(checkConditions);
                    control.settings.options = control.settings._options.map(function (option) {
                        var parsedOption = {};

                        if (Object(option) === option) {
                            if (option.label === undefined && option.value === undefined) {
                                throw new Error("Can't create select control, option missing value and label.");
                            }

                            parsedOption.label = String(option.label || option.value);
                            parsedOption.value = option.value && String(option.value);
                        }
                        else {
                            parsedOption.label = String(option);
                            parsedOption.value = String(option);
                        }

                        return parsedOption;
                    });
                }
            };

            // Getting pre-loaded controls:
            if (window.__controlsConfig__) {
                setControls();
            }

            return {
                getControlById: function (controlId) {
                    checkInit();
                    // Because of the caching mechanism, there was reuse of controls, which lead to shared behavior
                    // between controls. Changed to a deep copy, instead of returning a reference Originally: return
                    // controls.get(controlId);
                    return angular.copy(controls.get(controlId));
                },

                loadControl: function (controlConfig) {
                    if (controlConfig instanceof Control) {
                        return controlConfig.clone();
                    }

                    if (controlConfig.controlId) {
                        var existingControl = this.getControlById(controlConfig.controlId);
                        var fullControlConfig = angular.extend({}, existingControl._config, controlConfig);
                        return new Control(fullControlConfig);
                    }
                    else {
                        var controlInit = controlInitMethods[controlConfig.type];
                        if (controlInit) {
                            controlInit(controlConfig);
                        }
                        return new Control(controlConfig);
                    }
                },

                /**
                 * Inits the entities. This should be done before any usage of other methods in this service.
                 * Runs in the Loader app, NOT in Fortscale app!
                 * @returns {*}
                 */
                initControl: function (controlId) {
                    if (controls.has(controlId)) {
                        return $q.when(angular.copy(controls.get(controlId)));
                    }
                    return utils.http.wrappedHttpGet("data/controls/" + controlId.replace(/\./g, "/") +
                        ".json").then(function (resourceControlConfig) {
                        return resourceControlConfig;
                    }, function (error) {
                        var errorMessage = error.status === 404 ?
                        "Control '" + controlId + "' not found." :
                        "Can't get control '" + controlId + "'. Error: " + error.data;
                        return $q.reject(errorMessage);
                    });
                },

                /*
                 *
                 * Loads all the currently known param, done manually, since there is no way to load all file exists
                 * in a folder.
                 *
                 */
                initControls: function () {
                    controls = new Map();
                    var controlPromise = [this.initControl("account_properties"), this.initControl("minscore"),
                        this.initControl("user_types")];

                    return $q.all(controlPromise).then(function (promise) {
                        var array = [];
                        promise.forEach(function (controlConfig) {
                            array.push(controlConfig);
                        });
                        window.__controlsConfig__ = array;
                    });
                },

                /*
                 *
                 * Load params according to given configuration
                 *
                 */
                loadControls: function (controlsToLoad) {
                    if (!controls) {
                        return null;
                    }

                    controlsToLoad.forEach(function (control, i) {
                        if (control.controlId && !control._ready) {
                            var loadedControl = this.getControlById(control.controlId) || {};

                            controls[i] = jQuery.extend(true, loadedControl, control);
                            controls[i]._ready = true;
                            var controlInit = controlInitMethods[control.type];
                            if (controlInit) {
                                controlInit(control);
                            }
                        }
                    });
                    return controlsToLoad;
                },

                /*
                 *
                 * Load params according to given configuration
                 *
                 */
                getControlValue: function (control, inputParams, outputParams) {
                    var paramValue = this.getControlValueForParam(control, inputParams);
                    if (control.paramGroup) {
                        var groupParamValue = control.param + "=" +
                            ((paramValue === null) || (typeof paramValue === 'undefined') ? "_null_" : paramValue);
                        if (!outputParams[control.paramGroup]) {
                            outputParams[control.paramGroup] = groupParamValue;
                        } else {
                            outputParams[control.paramGroup] += "," + groupParamValue;
                        }
                    }
                    else {
                        outputParams[control.param] = paramValue;
                    }
                },

                /**
                 * Returns the value to be used in the param the specified control is for. The return value is a string.
                 * @param control
                 * @param params
                 * @returns {String|undefined}
                 */
                getControlValueForParam: function (control, params) {
                    if (!control.value && control.value !== 0) {
                        return;
                    }

                    var value = angular.copy(control.value);
                    var timeStart;
                    var timeEnd;

                    if (angular.isArray(value)) {
                        if (!value.length) {
                            return;
                        }

                        value = value.join(",");
                    }

                    // If we have a format, parse it and return the result
                    if (control.formatParam) {
                        return utils.strings.parseValue(control.formatParam, {
                            value: control.value
                        }, params);
                    }

                    if (control.type === "date") {
                        if (control.settings && control.settings.endOfDay) {
                            timeEnd = angular.isDate(value) ? value : parseInt(value, 10);
                            value = utils.date.getMoment(timeEnd).endOf("day").valueOf();
                        }
                        else if (control.settings && control.settings.startOfDay) {
                            timeStart = angular.isDate(value) ? value : parseInt(value, 10);
                            value = utils.date.getMoment(timeStart).startOf("day").valueOf();
                        }
                    }
                    else if (control.type === "dateRange") {
                        // Get time values of the start of the day and end of the day
                        timeStart = utils.date.getMoment(value.timeStart)
                            .startOf("day").valueOf();
                        timeEnd = utils.date.getMoment(value.timeEnd)
                            .endOf("day").valueOf();

                        value = timeStart + "," + timeEnd;
                    }

                    return value;
                }

            };

        }]);

}());
