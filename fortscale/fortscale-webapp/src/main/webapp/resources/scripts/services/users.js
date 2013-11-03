angular.module("Fortscale").factory("users", ["$q", "reports", function ($q, reports) {
    var userReports = {
        details: {
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
        },
        machines: {
            query: {
                dataSource: "api",
                "endpoint": {
                    entity: "user",
                    method: "usersMachines"
                },
                searchId: "user_machines",
                params: [
                    {
                        field: "ids",
                        name: "ids",
                        dashboardParam: "userIds"
                    }
                ]
            }
        },
        features: {
            query: {
                dataSource: "api",
                "endpoint": {
                    "query": "SELECT distinguishedname, badpwdcount, logoncount, primarygroupid, useraccountcontrol, memberof, company, department, lastlogon, pwdlastset, whencreated, whencreated, badpasswordtime FROM ldapusers WHERE ",
                    "entity": "investigate"
                }
            }
        }
    };

    var featuresMap = {
        "accountIsDisabled": "Account is Disabled",
        "interdomainTrustAccountValue": "Inter-domain trust account",
        "lockout": "Locked out",
        "noPasswordRequiresValue": "No password required",
        "normalUserAccountValue": "Normal user account",
        "passwordExpired": "Password expired",
        "passwordNeverExpiresValue": "Password never expires",
        "serverTrustAccount": "Server-trusted account",
        "smartcardRequired": "Smart-card required",
        "trustedForDelegation": "Trusted for delegation",
        "trustedToAuthForDelegation": "Trusted to authenticate for delegation",
        "workstationTrustAccount": "Workstation-trusted account"
    };

    function convertUserFeatures(rawData) {
        var users = [];
        angular.forEach(rawData, function (rawUser) {
            var userAccountControl = parseInt(rawUser.useraccountcontrol),
                convertedFeatures = {
                    badPasswordCount: parseInt(rawUser.badpwdcount),
                    logonCount: parseInt(rawUser.logoncount),
                    accountDisabled: !!(userAccountControl & 0x00000002),
                    noPasswordRequired: !!(userAccountControl & 0x00000020),
                    passwordNeverExpires: !!(userAccountControl & 0x00010000)
                };

            var name = rawUser.distinguishedname.match(/^CN=([^\,]+)/);
            if (name)
                convertedFeatures.name = name[1];

            if (rawUser.department)
                convertedFeatures.department = rawUser.department;

            users.push(convertedFeatures);
        });

        return users;
    }

    function prepareGetMethod(reportId) {
        return function (userIds) {
            var deferred = $q.defer();

            if (!angular.isArray(userIds))
                userIds = [userIds];

            if (reportId === "features") {
                var report = userReports.features,
                    usersQuery = [];

                angular.forEach(userIds, function (userName) {
                    usersQuery.push("lcase(userprincipalname) = '" + userName.toLocaleLowerCase() + "'");
                });

                reports.runReport({
                    query: {
                        endpoint: {
                            query: "SELECT MAX(runtime) as runtime FROM ldapusers",
                            entity: "investigate"
                        }
                    }
                }, {}).then(function (runtimeResult) {
                        report.query.endpoint.query = "SELECT distinguishedname, badpwdcount, logoncount, primarygroupid, useraccountcontrol, memberof, company, department, lastlogon, pwdlastset, whencreated, whencreated, badpasswordtime FROM ldapusers WHERE runtime = '" + runtimeResult.data[0].runtime + "' AND (" + usersQuery.join(" OR ") + ")";
                        reports.runReport(userReports[reportId], {}).then(function (result) {
                            deferred.resolve(convertUserFeatures(result.data));
                        }, deferred.reject);
                    }, deferred.reject);
            }
            else {
                reports.runReport(userReports[reportId], { userIds: userIds.join(",") }).then(function (result) {
                    deferred.resolve(result.data);
                }, deferred.reject);
            }

            return deferred.promise;
        }
    }

    var methods = {
        getSearchSettings: function () {
            return {
                "reports": [
                    {
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
                                "name": {"type": "string"},
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
                    }
                ],
                "resultField": "name",
                "value": "{{id}}",
                "showValueOnSelect": false,
                "placeholder": "Users search"
            };
        },
        getUsersDetails: prepareGetMethod("details"),
        getUserFeatures: prepareGetMethod("features"),
        getUserFeaturesForComparison: function (user) {
            var features = [];

            for(var featureName in featuresMap){
                if (user[featureName]){
                    features.push({
                        username: user.name,
                        feature: featuresMap[featureName]
                    });
                }
            }

            return features;
        },
        getUsersMachines: prepareGetMethod("machines")
    };

    return methods;
}]);