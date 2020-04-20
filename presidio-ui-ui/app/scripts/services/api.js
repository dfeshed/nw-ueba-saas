(function () {
    'use strict';

    /**
     * Handles data requests from/to the Fortscale API
     * @param $q
     * @param $resource
     * @param $rootScope
     * @param utils
     * @returns {{query: queryApi}}
     */
    function api($q, $resource, $rootScope, utils) {

        function getParamsData(data) {
            var resourceParams = ["entity", "id", "method"],
                params = {},
                paramsData = {};

            for (var paramName in data) {
                if (data.hasOwnProperty(paramName)) {
                    if (~resourceParams.indexOf(paramName)) {
                        params[paramName] = data[paramName];
                    } else {
                        paramsData[paramName] = data[paramName];
                    }
                }
            }

            return {data: paramsData, params: params};
        }
        function queryApi(query, params, options) {
            var timeStart = new Date();

            function promiseSuccess(queryResult) {
                if (angular.isObject(queryResult)) {
                    queryResult.time = new Date() - timeStart;
                }

                return queryResult || $q.reject("No response received for query: " + JSON.stringify(query));
            }

            function promiseError(error) {
                if (error.status === 401 || error.status === 403) {
                    $rootScope.$broadcast("authError", error.data || {status: error.status});
                    return $q.reject("Authentication error.");
                }

                if (error.status === 404) {
                    return $q.reject("API unavailable.");
                }

                if (typeof(error) === "string") {
                    return $q.reject(error);
                }

                return $q.reject("API call failed: " + error.data.message + " (" + error.data.code + ")." +
                    (error.data.developerMessage ? " Description: " + error.data.developerMessage : ""));
            }

            if (typeof(query.path) === "string") {
                return utils.http.wrappedHttpGet(utils.strings.parseValue(query.path, params))
                    .then(promiseSuccess, promiseError);
            }

            var resource = query.endpoint && query.endpoint.subEntityName ? apiWithSubEntityResource : apiResource,
                resourceData = angular.extend({}, options, params, query.endpoint);

            for (var property in resourceData) {
                if (resourceData.hasOwnProperty(property)) {
                    if (angular.isString(resourceData[property])) {
                        if (property === "query" && options && options.isCount) {
                            continue;

                        } else if ((!options || !options.isCount) && property === "countQuery") {
                            continue;
                        }

                        resourceData[property] = utils.strings.parseValue(resourceData[property], {}, params);
                    }
                }
            }

            if (resourceData.paging) {
                var page = resourceData.paging.param ?
                params[resourceData.paging.param] && parseInt(params[resourceData.paging.param], 10) :
                    utils.strings.parseValue(resourceData.paging.page, {}, params);
                resourceData.size = resourceData.limit = resourceData.size || resourceData.paging.pageSize || 10;
                resourceData.page = page || 0;
                resourceData.offset = ((page - 1) * resourceData.size) || 0;

                delete resourceData.paging;
            }

            if (query.method && query.method.toLowerCase() === "post") {
                var resourcesParams = getParamsData(resourceData);
                return resource.save(resourcesParams.params, resourcesParams.data).$promise
                    .then(promiseSuccess, promiseError);
            }
            else if (query.openInIframe) {
                var IFRAME_ID = "reportIframe";
                var iframe = document.querySelector("#" + IFRAME_ID);
                if (!iframe) {
                    iframe = document.createElement('iframe');
                    iframe.id = IFRAME_ID;

                    iframe.style.display = 'none';
                    document.body.appendChild(iframe);
                }

                var iframeSrc = "api/" + resourceData.api + "?",
                    queryParams = [];

                for (var paramName in resourceData) {
                    if (resourceData.hasOwnProperty(paramName)) {
                        var paramValue = resourceData[paramName];

                        if (angular.isObject(paramValue)) {
                            paramValue = JSON.stringify(paramValue);
                        }

                        if (paramValue !== null && paramValue !== undefined && paramValue !== "") {
                            queryParams.push(paramName + "=" + encodeURIComponent(paramValue));
                        }
                    }
                }
                iframeSrc += queryParams.join("&");
                iframe.src = iframeSrc;
                var deferred = $q.defer();
                deferred.resolve({});
                return deferred.promise.then(promiseSuccess, promiseError);
            } else {
                return resource.get(resourceData).$promise.then(promiseSuccess, promiseError);
            }
        }


        var apiResource = $resource("api/:api/:entity/:id/:method", {
            id: "@id"
        });

        var apiWithSubEntityResource = $resource("" +
            "api/:api/:entity/:id/:subEntityName/:subEntityId/:method", {
            id: "@id",
            subEntityName: "@subEntityName",
            subEntityId: "@subEntityId"
        });

        return {
            query: queryApi
        };

    }

    api.$inject = ["$q", "$resource", "$rootScope", "utils"];

    angular.module("DAL").factory("api", api);

})();
