angular.module("Fortscale").factory("whitelist", ["$q", "DAL", function($q, DAL){
    var reports = {
        add: {
            "query": {
                "dataSource": "splunk",
                "searchId": "Whitelist_Domains_Add",
                "fields": [],
                "params": [
                    {
                        "dashboardParam": "domain",
                        "field": "domain"
                    },
                    {
                        "dashboardParam": "comment",
                        "field": "comment"
                    }
                ]
            }
        },
        edit: {
            "query": {
                "dataSource": "splunk",
                "searchId": "Whitelist_Domains_Edit",
                "fields": [],
                "params": [
                    {
                        "dashboardParam": "domain",
                        "field": "newDomain"
                    },
                    {
                        "dashboardParam": "comment",
                        "field": "newComment"
                    },
                    {
                        "dashboardParam": "domainIndex",
                        "field": "domainIndex"
                    }
                ]
            }
        },
        getByName: {
            "query": {
                "dataSource": "splunk",
                "searchId": "Whitelist_Domains_Get_By_Name",
                "fields": [],
                "params": [
                    {
                        "dashboardParam": "domain",
                        "field": "domain"
                    }
                ]
            }
        },
        getById: {
            "query": {
                "dataSource": "splunk",
                "searchId": "Whitelist_Domains_Get_By_ID",
                "fields": [],
                "params": [
                    {
                        "dashboardParam": "domain",
                        "field": "domainIndex"
                    }
                ]
            }
        },
        remove: {
            "query": {
                "dataSource": "splunk",
                "searchId": "Whitelist_Domains_Remove",
                "fields": [],
                "params": [
                    {
                        "dashboardParam": "domainIndex",
                        "field": "domainIndex"
                    }
                ]
            }
        }
    };

    function runReport(report, params){
        var deferred = $q.defer(),
            searchParams = {};

        if (report.query.params){
            angular.forEach(report.query.params, function(param){
                searchParams[param.field] = params[param.dashboardParam];
            });
        }

        DAL.reports.runSearch(report.query.searchId, report.query.dataSource, searchParams, report.query.options)
            .then(function(results){
                deferred.resolve(results.data);
            }, function(error){
                console.error("ERROR: ", error);
            });

        return deferred.promise;
    }

    return {
        addDomain: function(params){
            return runReport(reports.add, params);
        },
        editDomain: function(params){
            return runReport(reports.edit, params);
        },
        getDomain: function(domain){
            return runReport(typeof(domain) === "number" || (typeof(domain) === "string" && /^\d+$/.test(domain)) ? reports.getById : reports.getByName, { domain: domain });
        },
        removeDomain: function(params){
            if (!window.confirm("Are you sure you wish to remove domain '" + params.domain + "' from the whitelist?"))
                return null;

            return runReport(reports.remove, params);
        }
    }
}]);
