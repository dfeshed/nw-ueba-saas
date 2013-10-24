angular.module("Fortscale").factory("users", ["$q", "reports", function($q, reports){
    var userDetailsReport = {
        "query": {
            "dataSource": "api",
            "endpoint": {
                "entity": "user",
                "method": "usersDetails"
            },
            "searchId": "user_details",
            "params": [
                {
                    "field": "ids",
                    "name": "ids",
                    "dashboardParam": "userIds"
                }
            ]
        }
    };

    var methods = {
        getSearchSettings: function(){
            return {
                "reports": [{
                    "query": {
                        "searchId": "search",
                        "dataSource": "api",
                        "endpoint": {
                            "entity": "user",
                            "method": "search"
                        },
                        "options": {
                            "count": 10
                        },
                        "fields": {
                            "name":{"type":"string"},
                            "id": {"type": "string"}
                        },
                        "params": [
                            {
                                "field": "prefix",
                                "type": "string",
                                "dashboardParam": "term"
                            }
                        ]
                    }
                }],
                "resultField": "name",
                "value": "{{id}}",
                "showValueOnSelect": false,
                "placeholder": "Users search"
            };
        },
        getUsersDetails: function(userIds){
            var deferred = $q.defer();

            if (!angular.isArray(userIds))
                userIds = [userIds];

            reports.runReport(userDetailsReport, { userIds: userIds.join(",") }).then(function(result){
                deferred.resolve(result.data);
            }, deferred.reject);

            return deferred.promise;
        },
        getUsersMachines: function(userIds){

        }
    };

    return methods;
}]);