(function () {
    "use strict";

    angular.module("Fortscale").factory("packages", ["$q", "utils", function ($q, utils) {
        var cachedPackages = {};

        var methods = {
            getPackageById: function (packageId) {
                if (cachedPackages[packageId]) {
                    return $q.when(angular.copy(cachedPackages[packageId]));
                }
                else {
                    return utils.http.wrappedHttpGet("packages/" + packageId + "/" + packageId +
                        ".package.json").then(function (packageConfig) {
                        try {
                            cachedPackages[packageId] = packageConfig;
                            return angular.copy(packageConfig);
                        }
                        catch (error) {
                            return $q.reject(error);
                        }
                    });
                }
            }
        };

        return methods;
    }]);
}());
