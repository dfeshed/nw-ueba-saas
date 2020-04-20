/**
 * This service is a util service for top alerts
 */
module Fortscale.layouts.overview.services.alertStatsUtils {

    import IAppConfigService = Fortscale.appConfigProvider.IAppConfigService;

    export interface IAlertStatus {
        alert_status:{
            Closed?:number,
            Open?:number
        }
        alert_open_severity:{
            Low?:number,
            Medium?:number,
            High?:number,
            Critical:number
        }
    }

    export interface IAlertSeverityByDay {
        day:number,
        severities:{
            severity:string,
            count:number
        }[]
    }

    interface IAlertStatusHttpReturn {
        data:IAlertStatus
    }

    export interface IAlertStatsUtils {
        shortDaysRange:number,
        longDaysRange:number,
        shortDateRange:string,
        longDateRange:string,
        getShortAlertsStatus ():ng.IPromise<IAlertStatus>
        getLongAlertsStatus ():ng.IPromise<IAlertStatus>
        getAlertsSeverityByDay ():ng.IPromise<IAlertSeverityByDay[]>
    }


    const ALERTS_STATUS_URL:string = 'alerts/statistics';
    const ALERTS_SEVERITIES_BY_DAY:string = 'alerts/alert-by-day-and-severity';
    const START_RANGE_PARAM_NAME = 'start_range';
    const ALERTS_SEVERITIES_START_RANGE_PARAM_NAME = 'alert_start_range';
    const ERR_MSG = 'alertStatsUtils.service: ';

    class AlertStatsUtils implements IAlertStatsUtils {

        get _errMsg ():string {
            return `${ERR_MSG}Controller: `;
        }

        /**
         * Returns the config value for shortDaysRange.
         *
         * @returns {number}
         * @private
         */
        get shortDaysRange () {
            return this.appConfig.getConfigValue('ui.overview', 'shortDaysRange');
        }

        /**
         * Returns the config value for longDaysRange.
         *
         * @returns {number}
         * @private
         */
        get longDaysRange () {
            return this.appConfig.getConfigValue('ui.overview', 'longDaysRange');
        }

        /**
         * Returns a CSV string representing a short date range.
         * @returns {string}
         * @private
         */
        get shortDateRange () {
            return this.dateRanges.getByDaysRange(this.shortDaysRange, 'short');
        }

        /**
         * Returns a CSV string representing a long date range.
         * @returns {string}
         * @private
         */
        get longDateRange ():string {
            return this.dateRanges.getByDaysRange(this.longDaysRange, 'short');
        }

        /**
         * Returns a promise that resolves on alerts stats.
         *
         * @param {string} range
         * @param {string} errMsg
         * @returns {IPromise<{alert_status: {}, alert_open_severity: {}}>}
         * @private
         */
        _getAlertStatus (range:string, errMsg:string):ng.IPromise<IAlertStatus> {

            // Make the http call and return the stats
            return this.$http.get<IAlertStatusHttpReturn>(`${this.BASE_URL}/${ALERTS_STATUS_URL}`, {
                params: {
                    [START_RANGE_PARAM_NAME]: range
                }
            })
            // Validate response and extract data
                .then((res:ng.IHttpPromiseCallbackArg<IAlertStatusHttpReturn>) => {

                    if (!res.data || !res.data.data) {
                        throw new ReferenceError(`${errMsg}Server responded without data.`);
                    }

                    return res.data.data;
                })

                // Return an empty object on error
                .catch((err) => {
                    this.$log.error(err);
                    return {
                        alert_status: {},
                        alert_open_severity: {}
                    };
                });
        }

        /**
         * Returns a promise that resolves on IAlertStatus for short range received from the server
         *
         * @returns {IPromise<IAlertStatus>}
         */
        getShortAlertsStatus ():ng.IPromise<IAlertStatus> {
            return this._getAlertStatus(this.shortDateRange, `${this._errMsg}getShortAlertsStatus: `);
        }

        /**
         * Returns a promise that resolves on IAlertStatus for long range received from the server
         *
         * @returns {IPromise<IAlertStatus>}
         */
        getLongAlertsStatus ():ng.IPromise<IAlertStatus> {
            return this._getAlertStatus(this.longDateRange, `${this._errMsg}getLongAlertsStatus: `);
        }

        /**
         * Returns a promise that resolves on a list of IAlertSeverityByDay
         *
         * @returns {IPromise<IAlertSeverityByDay[]>}
         */
        getAlertsSeverityByDay ():ng.IPromise<IAlertSeverityByDay[]> {
            // Make the http call and return the stats
            return this.$http.get<IAlertSeverityByDay[]>(`${this.BASE_URL}/${ALERTS_SEVERITIES_BY_DAY}`, {
                params: {
                    [ALERTS_SEVERITIES_START_RANGE_PARAM_NAME]: this.longDateRange
                }
            })
                .then((res:ng.IHttpPromiseCallbackArg<IAlertSeverityByDay[]>) => {
                    if (!res.data || !_.isArray(res.data)) {
                        throw new ReferenceError(`${this._errMsg}getAlertsSeverityByDay: Server responded without data.`);
                    }

                    return res.data;
                })
                // Return an empty array on error
                .catch((err) => {
                    this.$log.error(err);
                    return [];
                });
        }

        static $inject = ['appConfig', 'BASE_URL', '$http', '$log', 'dateRanges'];

        constructor (public appConfig:IAppConfigService, public BASE_URL:string, public $http:ng.IHttpService,
            public $log:ng.ILogService, public dateRanges:any) {
        }
    }

    angular.module('Fortscale.layouts.overview')
        .service('alertStatsUtils', AlertStatsUtils);
}
