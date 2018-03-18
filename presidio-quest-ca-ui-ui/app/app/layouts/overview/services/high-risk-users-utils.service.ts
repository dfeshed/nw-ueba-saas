/**
 * This service is a util service for high risk users asset
 */
module Fortscale.layouts.overview.services.highRiskUsersUtils {

    import IAppConfigService = Fortscale.appConfigProvider.IAppConfigService;
    const ERR_MSG = 'Fortscale.layouts.overview: highRiskUsersUtils: ';
    // const TOP_USERS_AMOUNT = 5;
    const TOP_USERS_SORT_FIELD_NAME = 'score';
    const TOP_USERS_SORT_DIRECTION = 'DESC';

    export interface IHighRiskUsersUtils {
        getUsers ():ng.IPromise<any[]>;
    }

    class HighRiskUsersUtils implements IHighRiskUsersUtils {
        /**
         * Gets the top scored users.
         *
         * @returns {IPromise<TResult>}
         */
        getUsers ():ng.IPromise<any[]> {
            return this.$http.get(`${this.BASE_URL}/user`, {
                params: {
                    size: this.appConfig.getConfigValue('ui.overview', 'numberOfRiskUsers'),
                    sort_field: TOP_USERS_SORT_FIELD_NAME,
                    sort_direction: TOP_USERS_SORT_DIRECTION,
                    min_score: 0
                }
            })
                .then((res:any) => {
                    // Validate data
                    if (!res.data.data) {
                        throw new ReferenceError(`${ERR_MSG}getUsers: The was no "data" property in the response body.`);
                    }

                    return res.data.data;
                })
                .catch(err => {
                    this.$log.error(`${ERR_MSG}getUsers: Couldn't get users data`, err);
                    return null;
                })
        }


        static $inject = ['BASE_URL', '$http', '$log', 'appConfig'];

        constructor (public BASE_URL:string, public $http:ng.IHttpService, public $log:ng.ILogService,
            public appConfig:IAppConfigService) {
        };
    }

    angular.module('Fortscale.layouts.overview')
        .service('highRiskUsersUtils', HighRiskUsersUtils);
}
