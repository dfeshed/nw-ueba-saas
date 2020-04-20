(function() {
    'use strict';

    /**
     * Holds the current state of the app - global params.
     * Listens for changes to state in the browser's URL and updates accordingly, then triggers an event.
     * @param $rootScope
     * @param $routeParams
     * @param utils
     * @returns {{currentParams, setParams: setParams}}
     */
    function state($rootScope, $routeParams, $location, $route, $timeout, utils, EventBus){
        var currentParams = {},

            locked = false,
            // Lock is available to avoid an infinite loop when the service updates the URL.
            // In case the service updates URL, it first locks temporarily the URL listener.

            lockTimeout,
            eventBus;

        function init(){
            setParams();

            /*
             * Notice the difference between locationChangeSuccess and routeChangeSuccess:
             *
             * locationChangeSuccess is triggered when the URL parameters
             * are changed, i.e. when there's a change after the ?
             * (for example: /#/d/explore/ssh?...)
             *
             * routeChangeSuccess is triggered when the URL route is changed,
             * i.e. when something after the #, but before the ? is changed
             * (for example: /#/d/explore/ssh?... ---> /#/d/explore/vpn)
             */
            $rootScope.$on("$locationChangeSuccess", function(e, params){
                if (!locked) {
                    setParams();
                }
            });

            $rootScope.$on("$routeChangeSuccess", function(e, params){
                if (!locked) {
                    setParams();
                }
            });
        }

        function lockUrlListener(){
            $timeout.cancel(lockTimeout);
            locked = true;

            lockTimeout = $timeout(function(){
                locked = false;
            }, 15);
        }

        function setParamsToUrl(params){
            var paramValue;
            for(var paramName in params){
                if (params.hasOwnProperty(paramName)) {
                    paramValue = params[paramName];

                    // If the param exists in the URL, remove it.
                    if ($routeParams[paramName]) {
                        $location.search(paramName, null);
                    }

                    // Make sure that the parameter has value.
                    if (paramValue !== null && (typeof paramValue !== 'undefined')) {
                        $location.search(paramName, getParamQueryValue(paramValue));
                    }
                }
            }
        }

        function getParamQueryValue(paramValue){
            var queryValue;

            if (paramValue.constructor === Array){
                queryValue = paramValue.map(function(member){
                    return getParamQueryValue(member);
                });
                queryValue = queryValue.join(",");
            } else if (Object(paramValue) === paramValue){
                queryValue = [];
                for(var p in paramValue){
                    if (paramValue.hasOwnProperty(p)) {
                        queryValue.push(p + "=" + paramValue[p]);
                    }
                }
                queryValue = queryValue.join(",");
            } else {
                queryValue = paramValue;
            }

            return queryValue;
        }

        function getChangedParams(params){
            var changedParams = {};

            var paramValue;

            for(var p in params){
                if (params.hasOwnProperty(p)) {
                    paramValue = parseParamValue(params[p]);
                    if (paramValue === "") {
                        paramValue = null;
                    }

                    if (currentParams[p] !== paramValue) {
                        changedParams[p] = paramValue;
                    }
                }
            }

            for(p in currentParams){
                if (params[p] === null) {
                    changedParams[p] = null;
                }
            }

            return changedParams;
        }

        function parseParamValue(paramValue){
            var paramValueCopy = angular.copy(paramValue);

            if (Object(paramValueCopy) === paramValueCopy){
                if (paramValueCopy.constructor === Array) {
                    var arrayMembers = [],
                        property;

                    for(var member of paramValueCopy){
                        if (Object(member) === member){
                            for(property in member) {
                                if (member.hasOwnProperty(property)) {
                                    arrayMembers.push(property + "=" + member[property]);
                                }
                            }
                        } else {
                            arrayMembers.push(member);
                        }
                    }

                    paramValueCopy = arrayMembers.join(",");
                }
            }

            return paramValueCopy;
        }

        function parseParams(params){
            var parsedParams = {};
            var paramValue;
            for(var p in params){
                if (params.hasOwnProperty(p)) {
                    paramValue = params[p];

                    if (paramValue.constructor === Array) {
                        parsedParams[p] = paramValue.join(",");
                    } else {
                        parsedParams[p] = paramValue;
                    }
                }
            }

            return parsedParams;
        }

        function setParams(){
            var changedParams = getChangedParams($routeParams);
            currentParams = parseParams($routeParams);
            notifyParamsChange(changedParams);
        }

        function notifyParamsChange(changedParams){
            if (eventBus && Object.keys(changedParams).length) {
                eventBus.triggerEvent("stateChange", {params: changedParams});
            }
        }

        function notifyStateChange(){
            if (eventBus){
                eventBus.triggerEvent("stateChange", {});
            }
        }

        /**
         * Gets a mapping of parameters and returns a flat object with the params and their parsed value according
         * to the data
         * Example:
         * mapParams({ param1: { value: 'Hello {{testParam}}' },
         * param2: { value: 'Not included', setIf: 'nonExistingProperty' } },
         * { testParam: 'World' }) returns { param1: 'Hello World' }
         * @param {Object} paramsMap
         * @param {Object} data
         */
        function mapParams(paramsMap, data){
            var params = {};

            var paramConfig,
                useParam,
                paramValue;

            for(var param in paramsMap){
                if (paramsMap.hasOwnProperty(param)) {
                    paramConfig = paramsMap[param];
                    useParam = true;

                    if (Object(paramConfig) === paramConfig) {
                        if (paramConfig.setIf) {
                            useParam = !!data[paramConfig.setIf];
                        }

                        if (useParam) {
                            paramValue = paramConfig.dataValue ? data[paramConfig.dataValue] :
                                utils.strings.parseValue(paramConfig.value, data);
                        }
                    } else {
                        paramValue = utils.strings.parseValue(paramConfig, data);
                    }

                    if (useParam) {
                        params[param] = paramValue;
                    }
                }
            }

            return params;
        }

        init();

        var stateObj = {
            get currentParams(){
                return currentParams;
            },
            /**
             * Returns a URL-ready string of params. If params are not specified, uses the current state params.
             * @param params
             */
            getUrlParams: function(params){
                params = params || currentParams;

                var urlParams = [];
                for(var param in params){
                    if (param !== "dashboardId" && param !== "entityId") {
                        urlParams.push(param + "=" + encodeURIComponent(getParamQueryValue(params[param])));
                    }
                }
                return urlParams.join("&");
            },
            mapParams: mapParams,
            setParams: function(params, setToUrl, notify){
                var changedParams = getChangedParams(params);
                if (Object.keys(changedParams).length) {
                    var paramValue;
                    for(var p in changedParams){
                        if (changedParams.hasOwnProperty(p)) {
                            paramValue = changedParams[p];
                            if (paramValue === null) {
                                delete currentParams[p];
                            } else {
                                currentParams[p] = paramValue;
                            }
                        }
                    }

                    lockUrlListener();

                    if (setToUrl !== false) {
                        setParamsToUrl(params);
                    }

                    if (notify !== false) {
                        notifyParamsChange(changedParams);
                    }
                }
            },
            refresh: function(){
                notifyStateChange();
            }
        };

        eventBus = EventBus.setToObject(stateObj, ["stateChange"]);
        return stateObj;


    }
    state.$inject = ["$rootScope", "$routeParams", "$location", "$route", "$timeout", "utils", "EventBus"];

    angular.module("State", ["Utils", "EventBus"]).factory("state", state);


})();
