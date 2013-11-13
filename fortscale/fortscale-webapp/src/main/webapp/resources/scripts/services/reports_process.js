angular.module("Fortscale").factory("reportsProcess", ["$q", "DAL", function($q, DAL){
    var processes = {
        getUsersDetails: function(results, params){
            var deferred = $q.defer(),
                usernames = [];

            if (!params || !params.userField || !params.userType){
                deferred.reject("Missing parameters for getUserDetails.");
                return deferred.promise;
            }

            angular.forEach(results.data, function(item){
                usernames.push(item[params.userField]);
            });

            if (!usernames.length)
                deferred.resolve({ data: [], total: 0 });

            DAL.reports.runSearch({
                endpoint: {
                    entity: "app",
                    id: params.userType,
                    method: "usersDetails",
                    usernames: usernames.join(",")
                }
            }).then(function(userDetailsResults){
                angular.forEach(userDetailsResults.data, function(userDetails){
                    for(var i= 0, item; i < results.data.length; i++){
                        item = results.data[i];
                        if (~userDetails.username.indexOf(item[params.userField])){
                            item.userDetails = userDetails;
                            break;
                        }
                    }
                });

                deferred.resolve(results);
            }, deferred.reject);

            return deferred.promise;
        }
    };

    var methods = {
        processData: function(processId, results, params){
            var process = processes[processId];
            if (!process)
                throw new Error("Invalid process, '" + processId + "'.");

            return process(results, params);
        }
    };

    return methods;
}]);