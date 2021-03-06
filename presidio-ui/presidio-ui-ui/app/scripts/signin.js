(function () {
    'use strict';

    angular.module("FortscaleSignin", ["FortscaleAuth", "Utils", "FocusWhen"]).run(function ($rootScope) {
        if (!$rootScope.safeApply) {
            $rootScope.safeApply = function (fn) {
                var phase = this.$root.$$phase;
                if (phase === '$apply' || phase === '$digest') {
                    if (fn && (typeof(fn) === 'function')) {
                        fn();
                    }
                } else {
                    this.$apply(fn);
                }
            };
        }
    });
}());
