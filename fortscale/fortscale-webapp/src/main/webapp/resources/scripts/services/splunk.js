angular.module("Splunk", []).factory("splunk", ["$http", "$q", "$rootScope", function($http, $q, $rootScope){
    var configDefaults = {
            port: "8089",
            scheme: "https",
            host: "localhost",
            version: "5.03"
        },
        config = getConfig();

    if (!config){
        $rootScope.requireSplunkConfig = true;
        return;
    }

    var http = new splunkjs.ProxyHttp("/proxy");
    var service = new splunkjs.Service(http, config);

    var APP_NAME = "FortscaleUI";

    var mySavedSearches,
        onSearches;

    function getConfig(){
        var splunkConfigKeys = ["username", "password", "port", "scheme", "host", "version"],
            config = {},
            allFieldsExist = true;

        for(var i= 0, property; property = splunkConfigKeys[i]; i++){
            config[property] = localStorage["splunk_config_" + property];
            if (!config[property]){
                if (configDefaults[property])
                    config[property] = localStorage["splunk_config_" + property] = configDefaults[property];
                else
                    allFieldsExist = false;
            }
        }

        return allFieldsExist ? config : null;
    }

    function getAppSearches(){
        var deferred = $q.defer();

        if (mySavedSearches){
            setTimeout(function(){
                $rootScope.safeApply(function(){
                    deferred.resolve(mySavedSearches);
                });
            });
        }
        else{
            if (!onSearches){
                onSearches = [];

                service.savedSearches({ app: APP_NAME }).fetch(function(err, searches) {
                    if (err){
                        deferred.reject(err.data.messages ? err.data.messages[0].text : err.error);
                        if (err.status === 401 || err.status === 500)
                            $rootScope.requireSplunkConfig = true;
                    }
                    else{
                        $rootScope.$apply(function(){
                            if (searches)
                                mySavedSearches = searches;

                            angular.forEach(onSearches, function(deferred){
                                if (searches)
                                    deferred.resolve(mySavedSearches);
                                else
                                    deferred.reject("No searches found");
                            });
                        });
                    }
                });
            }

            onSearches.push(deferred);
        }

        return deferred.promise;
    }

    var methods = {
        runSearch: function(searchName, params, options){
            var deferred = $q.defer();
            options = options || { count: 0 };

            function reportError(errorMessage){
                $rootScope.safeApply(function(){
                    deferred.reject(errorMessage);
                });
            }

            getAppSearches().then(function(){
                if (!mySavedSearches){
                    reportError("Search failed.");
                    return;
                }

                try{
                    // Retrieve the saved search that was created earlier
                    var mySavedSearch = mySavedSearches.item(searchName),
                        dispatchOptions = {};

                    if (!mySavedSearch){
                        reportError("Search '" + searchName + "' not found.");
                        return;
                    }
                    if (params){
                        for(var param in params){
                            dispatchOptions["args." + param] = params[param];
                        }
                    }

                    // Run the saved search and poll for completion
                    mySavedSearch.dispatch(dispatchOptions, function(err, job) {
                        if (!job){
                            reportError("Search failed.");
                            return;
                        }

                        job.track({
                            period: 300
                        }, {
                            done: function(job) {
                                try{
                                    var totalResultsCount = job.properties().resultCount;

                                    // Get 10 results and print them
                                    job.results(options, function(err, results, job) {
                                        var data = [];
                                        angular.forEach(results.rows, function(row){
                                            var rowObject = {};
                                            angular.forEach(results.fields, function(field, fieldIndex){
                                                rowObject[field] = row[fieldIndex];
                                            });
                                            data.push(rowObject);
                                        });

                                        $rootScope.safeApply(function(){
                                            deferred.resolve({ data: data, rawData: results, totalResults: totalResultsCount });
                                        });
                                    });
                                } catch(error){
                                    reportError(error.message);
                                }
                            },
                            failed: function(job) {
                                reportError("Search failed.");
                            },
                            error: function(err) {
                                reportError("Search failed.");
                            }
                        });
                    });
                } catch(e){
                    reportError("Search failed.");
                    console.error("Search failed: ", e.message);
                }
            }, deferred.reject);

            return deferred.promise;
        }
    };

    return methods;
}]);