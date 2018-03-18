declare var moment: any;

(function () {
    'use strict';

    /**
     * Returns a filter
     *
     * @returns {function(any): number}
     */
    function fsDTHumanize ():(val: string, timeUnit: string)=>string {

        return function (val: string, timeUnit: string):string {

            if (!timeUnit) {
                return val;
            }

            let durationInt = parseInt(val, 10);
            let duration = moment.duration(durationInt, timeUnit);
            let humanized: string = <string>duration.humanize();
            if (humanized.indexOf('a ') === 0) {
                return `Last ${humanized.substr(2)}`
            }

            return `Last ${humanized}`;
        }
    }

    function fsDTDiffHumanize ():(val: string)=>string  {
        return function (val: string): string {
            let time = parseInt(val, 10);
            let diff = moment().diff(time);
            let duration = moment.duration(diff, 'milliseconds');
            return duration.humanize();
        }
    }


    angular.module('Fortscale.shared.filters')
        .filter('fsDTHumanize', [fsDTHumanize])
        .filter('fsDTDiffHumanize', [fsDTDiffHumanize]);
}());
