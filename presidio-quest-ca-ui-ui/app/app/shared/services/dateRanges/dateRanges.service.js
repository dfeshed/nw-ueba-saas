(function () {
    'use strict';

    function DateRanges (utils) {
        this.utils = utils;
    }

    /**
     * Returns a number that is divided by 1000 and floored.
     *
     * @param {number} value
     * @returns {number}
     * @private
     */
    DateRanges.prototype._truncate = function (value) {
        return Math.floor(value/1000);
    };

    /**
     * A dummy function that returns what it gets. Used as a 'through' alternative.
     *
     * @param {*} value
     * @returns {*}
     * @private
     */
    DateRanges.prototype._through = function (value) {
        return value;
    };

    /**
     * Returns the start of days 'days' days ago.
     *
     * @param {number} days
     * @returns {number}
     */
    DateRanges.prototype.getStartOfDayByDaysAgo = function (days) {
        return this.utils.date.getMoment()
            .endOf('day').subtract(days, 'days').startOf('day').valueOf();
    };

    /**
     * Returns the end of the current day
     *
     * @returns {*}
     */
    DateRanges.prototype.getEndOfCurrentDay = function () {
        return this.utils.date.getMoment()
            .endOf('day').valueOf();
    };

    /**
     * Returns a CSV string that represent a date range; from(now->days-end->minus-days->days-start),to(now->days-end).
     *
     * @param {number} days
     * @param {string=} type If set to 'short' will return a 10 digits timestamp.
     * @returns {string}
     */
    DateRanges.prototype.getByDaysRange = function (days, type) {

        var transformFn;
        switch (type) {
            case 'short' :
                transformFn = this._truncate;
                break;
            default:
                transformFn = this._through;
        }

        return _.map([this.getStartOfDayByDaysAgo(days), this.getEndOfCurrentDay()], transformFn).join(',');
    };


    /**
     * Returns a range that's 8 days from today's end of day
     *
     * @param {string=} type
     * @returns {string} csv of startDate, endDate
     * @private
     */
    DateRanges.prototype.getLast7Days = function (type) {
        return this.getByDaysRange(7, type);
    };

    /**
     * Returns a range that's 8 days from today's end of day
     *
     * @param {string=} type
     * @returns {string} csv of startDate, endDate
     * @private
     */
    DateRanges.prototype.getLastDay = function (type) {
        return this.getByDaysRange(1, type);
    };

    DateRanges.$inject = ['utils'];

    angular.module('Fortscale.shared.services.dateRanges', [])
        .service('dateRanges', DateRanges);
}());
