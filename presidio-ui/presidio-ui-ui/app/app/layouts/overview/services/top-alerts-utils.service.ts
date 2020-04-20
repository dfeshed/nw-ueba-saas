/**
 * This service is a util service for top alerts
 */
module Fortscale.layouts.overview.services.topAlertsUtil {

    import IAppConfigService = Fortscale.appConfigProvider.IAppConfigService;

    const ERR_MSG = 'Fortscale.layouts.overview: topAlertsUtil: ';
    const PAGE_NUMBER = 1;
    const SIZE = 10;
    const SORT_DIRECTION = 'DESC';
    const SORT_FIELD = 'score';
    const STATUS = 'open';

    /**
     * Interface to describe params object
     */
    interface IAlertParams {
        alert_start_range: string;
        page: number;
        size: number;
        sort_direction: string;
        sort_field: string;
        status: string;
    }

    /**
     * Service's interface
     */
    export interface ITopAlertsUtilService {
        getAlerts ():ng.IPromise<any[]>;
    }

    class TopAlertsUtilService implements ITopAlertsUtilService {

        /**
         * Returns a csv representing epoch date range
         * @returns {string}
         * @private
         */
        _getAlertsStartRange (): string {
            let daysRange = this.appConfig.getConfigValue('overview', 'daysRange');
            return this.dateRanges.getByDaysRange(daysRange);
        }

        /**
         * Returns the params for the GET alerts call.
         * @returns {{alert_start_range: string, page: number, size: number, sort_direction: string, sort_field: string, status: string}}
         * @private
         */
        _getParams (): IAlertParams{
            return {
                alert_start_range: this._getAlertsStartRange(),
                page: PAGE_NUMBER,
                size: SIZE,
                sort_direction: SORT_DIRECTION,
                sort_field: SORT_FIELD,
                status: STATUS
            };
        }

        /**
         * Returns a promise that resolves on the alerts
         *
         * @returns {IPromise<TResult>}
         */
        getAlerts ():ng.IPromise<any[]> {
            return this.$http.get(`${this.BASE_URL}/alerts`, {params: this._getParams()})
                .then((res:any) => {
                    // Validate data
                    if (!res.data.data) {
                        throw new ReferenceError(`${ERR_MSG}getAlerts: The was no "data" property in the response body.`);
                    }

                    return res.data.data;
                })
                .catch(err => {
                    this.$log.error(`${ERR_MSG}getAlerts: Couldn't get alerts data`, err);
                    return null;
                })
        }

        static $inject = ['BASE_URL', '$http', 'appConfig', 'dateRanges', '$log'];

        constructor (public BASE_URL:string, public $http:ng.IHttpService, public appConfig:IAppConfigService,
            public dateRanges:any, public $log: ng.ILogService) {
        };
    }

    angular.module('Fortscale.layouts.overview')
        .service('topAlertsUtils', TopAlertsUtilService);
}
