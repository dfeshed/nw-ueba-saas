(function () {
    'use strict';

    function durationToPrettyTime (utils) {


        function durationToPrettyTimeFilter(duration) {

            if (duration === undefined || duration === null) {
                return duration;
            }

            return utils.duration.prettyTime(duration * 1000);
        }

        return durationToPrettyTimeFilter;
    }

    durationToPrettyTime.$inject = ['utils'];

    angular.module('Fortscale.shared.filters')
        .filter('durationToPrettyTime', durationToPrettyTime);
}());
