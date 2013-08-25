angular.module("Fortscale").controller("WhitelistAddController", ["$scope", "$rootScope", "$q", "whitelist", "$timeout", function($scope, $rootScope, $q, whitelist, $timeout){
    var reports = {
        add: {
            updateOnResults: "whitelistLastAdd",
            closeOnResults: true,
            method: "addDomain"
        },
        remove: {
            updateOnResults: "whitelistLastAdd",
            closeOnResults: true,
            method: "removeDomain"
        },
        edit: {
            updateOnResults: "whitelistLastAdd",
            closeOnResults: true,
            method: "editDomain"
        }
    };

    var originalDomain = $scope.whitelistAddShowDialog,
        originalComment,
        domainIndex;

    function init(){
        whitelist.getDomain($scope.whitelistAddShowDialog).then(function(results){
            if (results.length){
                angular.forEach($scope.fields, function(field){
                    if (results[0][field.id])
                        field.value = results[0][field.id];

                    if (field.id === "comment")
                        originalComment = field.value;
                });

                domainIndex = results[0].domainIndex;
            }

            $scope.domainExists = !!results.length;
        });
    }

    $scope.fields = [
        {
            label: "Domain",
            id: "domain",
            tooltip: "The domain to whitelist. It's possible to use wildcards, such as '*.google.*', 'facebook.*', etc.",
            value: $scope.whitelistAddShowDialog,
            width: 320,
            required: true,
            validation: function(value){
                if (/^[\*\.]+$/.test(value))
                    return { success: false, error: "Invalid domain" };

                if (!/^[a-zA-Z0-9*.-]+$/.test(value))
                    return {success: false, error: "Invalid domain" };

                return { success: true };
            }
        },
        {
            label: "Comment",
            id: "comment",
            tooltip: "(Optional) a comment which will be displayed in the whitelisted domains page",
            width: 420,
            value: ""
        }
    ];

    $scope.close = function($event){
        if (!$event || ~$event.target.className.indexOf("modal-overlay"))
            $rootScope.whitelistAddShowDialog = null;
    };

    $scope.runReport = function(reportId){
        var report = reports[reportId];
        if (!report)
            return;

        var params = {},
            error,
            validationResult;

        angular.forEach($scope.fields, function(field){
            if (field.required && !field.value){
                field.error = "Required";
                error = true;
            }
            else if (field.validation){
                validationResult = field.validation(field.value);
                if (!validationResult.success){
                    error = true;
                    field.error = validationResult.error;
                }
                else
                    params[field.id] = field.value;
            }
            else
                params[field.id] = field.value;
        });

        params.oldDomain = originalDomain;
        params.oldComment = originalComment;
        params.domainIndex = domainIndex;
        params.comment = params.comment || "";

        if (!error){
            whitelist[report.method](params).then(function(results){
                if (report.updateOnResults)
                    $timeout(function(){
                        $rootScope.$broadcast(report.updateOnResults, results);
                    }, 1500)

                if (report.closeOnResults)
                    $scope.close();
            });
        }
    };

    init();
}]);