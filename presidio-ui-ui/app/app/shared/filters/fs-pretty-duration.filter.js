/**
 * Converts integer value to a humanized moment duration.
 * Example: 60, "days" => 2 months
 * example: 7, "days" => 1 week
 */
(function () {
    'use strict';

    var allowedUnits = ['seconds', 'minutes', 'hours', 'days', 'weeks', 'months', 'years'];

    function fsPrettyDuration (assert) {

        function fsPrettyDurationFilter (val, sourceUnits) {

            // Validations
            assert.isString(sourceUnits, 'sourceUnits', 'fsPrettyDuration.filter: ', false, false);
            assert(allowedUnits.indexOf(sourceUnits.trim()) !== -1,
                "fsPrettyDuration.filter: sourceUnits argument must be on of the following: " + allowedUnits.join(', '),
                RangeError);

            val = parseInt(val, 10);
            assert(!isNaN(val), "fsPrettyDuration.filter: value could not be parsed to int.", TypeError);

            // Convert val to string
            var s = moment.duration(val, sourceUnits).humanize();
            // Remove "a ", for example "a month" will become "month"
            s = s.replace('a ', '');

            return s;

        }

        return fsPrettyDurationFilter;

    }

    fsPrettyDuration.$inject = [
        'assert'
    ];

    angular.module('Fortscale.shared.filters')
        .filter('fsPrettyDuration', fsPrettyDuration);
}());
