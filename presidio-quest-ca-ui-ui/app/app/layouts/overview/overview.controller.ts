module Fortscale.layouts.overview {

    import IHighRiskUsersUtils = Fortscale.layouts.overview.services.highRiskUsersUtils.IHighRiskUsersUtils;
    import INanobarAutomationService = Fortscale.shared.services.fsNanobarAutomation.INanobarAutomationService;
    import INanobarAutomation = Fortscale.shared.services.fsNanobarAutomation.INanobarAutomation;
    import ITopAlertsUtilService = Fortscale.layouts.overview.services.topAlertsUtil.ITopAlertsUtilService;
    import List = _.List;
    import IAlertStatsUtils = Fortscale.layouts.overview.services.alertStatsUtils.IAlertStatsUtils;
    import IAlertStatus = Fortscale.layouts.overview.services.alertStatsUtils.IAlertStatus;
    import IAlertSeverityByDay = Fortscale.layouts.overview.services.alertStatsUtils.IAlertSeverityByDay;
    import ITagsUtilsService = Fortscale.shared.services.tagsUtilsService.ITagsUtilsService;
    import ITagDefinition = Fortscale.shared.services.tagsUtilsService.ITagDefinition;


    class OverviewController {

        NANOBAR_ID:string = 'overview-page';
        users:any;

        /**
         * Assets
         */
        highRiskUsers:any[];
        topAlerts:any[];
        usersTagsCount:{};
        alertStatusShort:IAlertStatus;
        alertStatusLong:IAlertStatus;
        alertsSeverityByDay:IAlertSeverityByDay[];
        tags: ITagDefinition[];


        _enrichUsers (users) {
            this.userUtils.setFallBackDisplayNames(users);
            this.userUtils.setUsersFullAddress(users);
        }


        _initLoadingAlertUsers (ids:any):void {
            if (ids && ids.length) {
                this.userUtils.getUsersDetails(ids)
                    .then((users:any) => {
                        this.users = _.keyBy(users, 'id');
                        this._enrichUsers(this.users);
                    });
            } else {
                this.users = {};
            }
        }

        /**
         * Initiates high risk users asset loading
         * @private
         */
        _initLoadingHighRiskUsers ():ng.IPromise<void> {
            return this.highRiskUsersUtils.getUsers()
                .then(users => {
                    this.highRiskUsers = users;
                    this._enrichUsers(this.highRiskUsers);
                })
                .catch(err => {
                    this.highRiskUsers = null;
                })
        }

        /**
         * Initiates top ten alerts asset loading
         * @private
         */
        _initLoadingTopAlerts ():ng.IPromise<void> {
            return this.topAlertsUtils.getAlerts()
                .then(topAlerts => {
                    this.topAlerts = topAlerts;
                    return _.uniq(_.map(this.topAlerts, 'entityId'));
                })
                .then(userIds => {
                    this._initLoadingAlertUsers(userIds);
                })
                .catch(err => {
                    this.highRiskUsers = null;
                })
        }

        /**
         * Initiates loading of system tags count
         * @private
         */
        _initLoadingUsersTagsStatistics ():ng.IPromise<void> {
            return this.userUtils.getUsersTagsCount()
                .then((usersTagsCount:any) => {
                    this.usersTagsCount = usersTagsCount;
                })
                .catch((err:ng.IHttpPromiseCallbackArg<any>) => {
                    this.usersTagsCount = {};
                })
        }

        /**
         * Initiates loading of alert stats short and long
         *
         * @private
         */
        _initLoadingAlertStats ():ng.IPromise<void>[] {
            return [
                this.alertStatsUtils.getShortAlertsStatus()
                    .then((alertStatus:IAlertStatus) => {
                        this.alertStatusShort = alertStatus;
                    })
                    .catch((err:any) => {
                        this.alertStatusShort = <IAlertStatus>{
                            alert_status: {},
                            alert_open_severity: {}
                        };
                    }),
                this.alertStatsUtils.getLongAlertsStatus()
                    .then((alertStatus:IAlertStatus) => {
                        this.alertStatusLong = alertStatus;
                    })
                    .catch((err:any) => {
                        this.alertStatusLong = <IAlertStatus>{
                            alert_status: {},
                            alert_open_severity: {}
                        };
                    })
            ];
        }

        /**
         * Initiates loading of alert severity by day
         *
         * @private
         */
        _initLoadingAlertsSeverityByDay ():ng.IPromise<void> {
            return this.alertStatsUtils.getAlertsSeverityByDay()
                .then((alertsSeverityByDay:IAlertSeverityByDay[]) => {
                    this.alertsSeverityByDay = alertsSeverityByDay;

                })
                .catch((err:any) => {
                    this.alertsSeverityByDay = [];
                });
        }

        _initLoadingTags ():ng.IPromise<void> {
            return this.tagsUtils.getTags()
                .then((res:ng.IHttpPromiseCallbackArg<any>) => {
                    this.tags = <ITagDefinition[]>res.data;
                })
                .catch((err) => {
                    console.error('There was an error fetching tags.', err);
                    this.tags = [];
                });

        }

        /**
         * Initiates assets loading sequence
         *
         * @private
         */
        _initLoadingSequence ():void {
            let promises:ng.IPromise<void>[] = [
                this._initLoadingTags(),
                this._initLoadingHighRiskUsers(),
                this._initLoadingTopAlerts(),
                this._initLoadingUsersTagsStatistics(),
                ...this._initLoadingAlertStats(),
                this._initLoadingAlertsSeverityByDay()
            ];

            this.fsNanobarAutomationService.addPromises(this.NANOBAR_ID, promises);
        }

        /**
         * Controller's init function
         * @private
         */
        _init ():void {

            // Start loading assets
            this._initLoadingSequence();

            this.page.setPageTitle('Overview');
        }


        static $inject = ['$scope', '$element', 'fsNanobarAutomationService', 'highRiskUsersUtils',
            'topAlertsUtils', 'userUtils', 'alertStatsUtils', 'page', 'tagsUtils'];

        constructor (public scope:ng.IScope, public element:ng.IAugmentedJQuery,
            public fsNanobarAutomationService:INanobarAutomationService,
            public highRiskUsersUtils:IHighRiskUsersUtils, public topAlertsUtils:ITopAlertsUtilService,
            public userUtils:any, public alertStatsUtils:IAlertStatsUtils, public page:any,
            public tagsUtils:ITagsUtilsService) {

            this._init();

        }
    }

    angular.module('Fortscale.layouts.overview')
        .controller('OverviewController', OverviewController)

}
