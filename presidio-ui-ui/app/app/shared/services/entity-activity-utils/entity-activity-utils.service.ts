module Fortscale.shared.services.entityActivityUtils {

    import IAppConfigService = Fortscale.appConfigProvider.IAppConfigService;
    import IDataBean = Fortscale.shared.interfaces.IDataBean;
    export enum eEntityType {
        USER,
        DEVICE,
        ORGANIZATION
    }

    export interface IEntityActivityUtilsService {
        getTopCountries <IActivityUserCountry>(entityType:number, entity?:any, daysRange?:number,
            limit?:number):ng.IPromise<IActivityUserCountry[]>
        getTopCountries <IActivityOrganizationCountry>(entityType:number, entity?:any, daysRange?:number,
            limit?:number):ng.IPromise<IActivityOrganizationCountry[]>
        getTopCountries (entityType:number, entity?:any, daysRange?:number, limit?:number):ng.IPromise<any>
        getAuthentications <IActivityUserAuthentication>(entityType:eEntityType, entity:any,
            daysRange?:number):ng.IPromise<IActivityUserAuthentication>
        getWorkingHours <IActivityUserWorkingHour>(entityType:number, entity?:any,
            daysRange?:number):ng.IPromise<IActivityUserWorkingHour[]>
        getSourceDevices <IActivityUserDevice>(entityType:number, entity?:any,
            daysRange?:number):ng.IPromise<IActivityUserDevice[]>
        getTargetDevices <IActivityUserDevice>(entityType:number, entity?:any,
            daysRange?:number):ng.IPromise<IActivityUserDevice[]>
        getDataUsages <IActivityUserDataUsage>(entityType:number, entity?:any,
            daysRange?:number):ng.IPromise<IActivityUserDataUsage[]>,
        getTopApplications <IActivityTopApplication>(entityType:number, entity?:any,
               daysRange?:number):ng.IPromise<IActivityTopApplication[]>,
        getTopDirectories <IActivityTopDirectory>(entityType:number, entity?:any,
                         daysRange?:number):ng.IPromise<IActivityTopDirectory[]>,
        getTopRecipientDomain <IActivityTopRecipientDomain>(entityType:number, entity?:any,
                                                  daysRange?:number):ng.IPromise<IActivityTopRecipientDomain[]>
        getClassificationExposure <IActivityUserClassificationExposure>(entityType:number, entity?:any,
                                                            daysRange?:number):ng.IPromise<IActivityUserClassificationExposure[]>
        getDlpMode():boolean;
        updateDlpMode():boolean;
        reloadDlpModeFromConfiguration():boolean;

    }


    class EntityActivityUtilsService implements IEntityActivityUtilsService {

        dlpMode:boolean;

        getDlpMode():boolean{
            return this.dlpMode;
        }
        updateDlpMode():boolean{
            this.dlpMode = !this.dlpMode;
            return this.dlpMode;
        }

        reloadDlpModeFromConfiguration():boolean{
            this.dlpMode = !this.appConfig.getConfigValue('ui.userProfile', 'activities_auth_mode')
            return this.dlpMode;
        }

        /**
         * Returns the url for user activity
         * @param {{id: string}} user
         * @param {string} endPoint
         * @returns {string}
         * @private
         */
        _getUserActivityUrl (user, endPoint):string {
            return `${this.BASE_URL}/user/${user.id}/activity${endPoint ? '/' + endPoint : ''}`;
        }

        /**
         * Returns the url for organization activity
         * @param {string} endPoint
         * @returns {string}
         * @private
         */
        _getOrganizationActivityUrl (endPoint):string {
            return `${this.BASE_URL}/organization/activity${endPoint ? '/' + endPoint : ''}`;
        };

        /**
         * Returns a promise that resolves on a user's Top-Countries activity
         * @param {{}=} user
         * @param {number=} daysRange
         * @param {number=} limit
         * @returns {IHttpPromise<IEntityActivityLocationUser>}
         * @private
         */
        _getUserTopCountries (user:any, daysRange:number, limit:number):ng.IHttpPromise<IEntityActivityLocationUser> {
            let url = this._getUserActivityUrl(user, 'locations');
            return this.$http.get(url, {
                params: {
                    time_range: daysRange,
                    limit: limit
                }
            });
        }

        /**
         * Returns a promise that resolves on organization Top-Countries activity
         * @param {number=} daysRange
         * @param {number=} limit
         * @returns {IHttpPromise<IEntityActivityLocationOrganization>}
         * @private
         */
        _getOrganizationTopCountries (daysRange:number,
            limit:number):ng.IHttpPromise<IEntityActivityLocationOrganization> {

            let url = this._getOrganizationActivityUrl('locations');
            return this.$http.get(url, {
                params: {
                    time_range: daysRange,
                    limit: limit
                }
            });
        }

        /**
         * Returns a promise that resolves on a user's authentications activity
         * @param {{}=} user
         * @param {number=} daysRange
         * @returns {IHttpPromise<IEntityActivityLocationUser>}
         * @private
         */
        _getUserAuthentications (user:any, daysRange:number):ng.IHttpPromise<IEntityActivityLocationUser> {
            let url = this._getUserActivityUrl(user, 'authentications');
            return this.$http.get(url, {
                params: {
                    time_range: daysRange
                }
            });
        }

        _getUserWorkingHours (user:any, daysRange:number):ng.IHttpPromise<IEntityActivityWorkingHoursUser> {
            let url = this._getUserActivityUrl(user, 'working-hours');
            return this.$http.get(url, {
                params: {
                    time_range: daysRange
                }
            });
        }

        _getUserSourceDevices (user:any, daysRange:number):ng.IHttpPromise<IEntityActivitySourceDevicesUser> {
            let url = this._getUserActivityUrl(user, 'source-devices');
            return this.$http.get(url, {
                params: {
                    time_range: daysRange
                }
            });
        }

        _getUserTargetDevices (user:any, daysRange:number):ng.IHttpPromise<IEntityActivityTargetDevicesUser> {
            let url = this._getUserActivityUrl(user, 'target-devices');
            return this.$http.get(url, {
                params: {
                    time_range: daysRange
                }
            });
        }

        _getUserDataUsages (user:any, daysRange:number):ng.IHttpPromise<IEntityActivityDataUsagesUser> {
            let url = this._getUserActivityUrl(user, 'data-usage');
            return this.$http.get(url, {
                params: {
                    time_range: daysRange
                }
            });
        }

        getTopApplications <IActivityTopApplication>(entityType:number, entity?:any,
                                                     daysRange?:number):ng.IPromise<IActivityTopApplication[]>{
            let entityTypeSwitch = function () {
                switch (entityType) {
                    case eEntityType.USER:
                        return this._getTopApplications(entity, daysRange);
                    default:
                        return null;
                }
            }.bind(this);

            return this.$q.when(entityTypeSwitch())
                .then((res:ng.IHttpPromiseCallbackArg<IEntityActivity>) => {
                    return res.data.data;
                })
        }

        _getTopApplications (user:any, daysRange:number):ng.IHttpPromise<IEntityActivitySourceDevicesUser> {
            let url = this._getUserActivityUrl(user, 'top-applications');
            return this.$http.get(url, {
                params: {
                    time_range: daysRange
                }
            });
        }



        getTopDirectories <IActivityTopDirectory>(entityType:number, entity?:any,
                                                     daysRange?:number):ng.IPromise<IActivityTopDirectory[]>{
            let entityTypeSwitch = function () {
                switch (entityType) {
                    case eEntityType.USER:
                        return this._getTopDirectories(entity, daysRange);
                    default:
                        return null;
                }
            }.bind(this);

            return this.$q.when(entityTypeSwitch())
                .then((res:ng.IHttpPromiseCallbackArg<IEntityActivity>) => {
                    return res.data.data;
                })
        }

        _getTopDirectories (user:any, daysRange:number):ng.IHttpPromise<IEntityActivitySourceDevicesUser> {
            let url = this._getUserActivityUrl(user, 'top-directories');
            return this.$http.get(url, {
                params: {
                    time_range: daysRange
                }
            });
        }

        getTopRecipientDomain <IActivityTopRecipientDomain>(entityType:number, entity?:any,
                                                            daysRange?:number):ng.IPromise<IActivityTopRecipientDomain[]>{
            let entityTypeSwitch = function () {
                switch (entityType) {
                    case eEntityType.USER:
                        return this._getToRecipientsDomains(entity, daysRange);
                    default:
                        return null;
                }
            }.bind(this);

            return this.$q.when(entityTypeSwitch())
                .then((res:ng.IHttpPromiseCallbackArg<IEntityActivity>) => {
                    return res.data.data;
                })
        }



        _getToRecipientsDomains (user:any, daysRange:number):ng.IHttpPromise<IEntityActivitySourceDevicesUser> {
            let url = this._getUserActivityUrl(user, 'email-recipient-domain');
            return this.$http.get(url, {
                params: {
                    time_range: daysRange
                }
            });
        }

        getClassificationExposure <IActivityUserClassificationExposure>(entityType:number, entity?:any,
                                                                        daysRange?:number):ng.IPromise<IActivityUserClassificationExposure[]>{
            let entityTypeSwitch = function () {
                switch (entityType) {
                    case eEntityType.USER:
                        return this._getClassificationExposure(entity, daysRange);
                    default:
                        return null;
                }
            }.bind(this);

            return this.$q.when(entityTypeSwitch())
                .then((res:ng.IHttpPromiseCallbackArg<IEntityActivity>) => {
                    return res.data.data;
                })
        }

        _getClassificationExposure (user:any, daysRange:number):ng.IHttpPromise<IEntityActivityClassificationExposure> {
            let url = this._getUserActivityUrl(user, 'classification-exposure');
            return this.$http.get(url, {
                params: {
                    time_range: daysRange
                }
            });
        }

        /**
         * Returns a promise that resolves on Top-Countries activity
         *
         * @param {number} entityType
         * @param {{}=} entity
         * @param {number=} daysRange
         * @param {number=} limit
         * @returns {IPromise<any>}
         */
        getTopCountries (entityType:eEntityType, entity?:any, daysRange:number = 90,
            limit:number = 3):ng.IPromise<any> {

            let entityTypeSwitch = function () {
                switch (entityType) {
                    case eEntityType.USER:
                        return this._getUserTopCountries(entity, daysRange, limit);
                    case eEntityType.ORGANIZATION:
                        return this._getOrganizationTopCountries(daysRange, limit);
                    default:
                        return null;
                }
            }.bind(this);

            return this.$q.when(entityTypeSwitch())
                .then((res:ng.IHttpPromiseCallbackArg<IEntityActivity>) => {
                    return res.data.data;
                })

        }

        /**
         * Returns a promise that resolves on authentications activity
         *
         * @param {number} entityType
         * @param {{}=} entity
         * @param {number=} daysRange
         * @returns {IPromise<any>}
         */
        getAuthentications (entityType:eEntityType, entity:any, daysRange:number = 90):ng.IPromise<any> {
            let entityTypeSwitch = function () {
                switch (entityType) {
                    case eEntityType.USER:
                        return this._getUserAuthentications(entity, daysRange);
                    default:
                        return null;
                }
            }.bind(this);

            return this.$q.when(entityTypeSwitch())
                .then((res:ng.IHttpPromiseCallbackArg<IEntityActivityAuthenticationUser>) => {
                    if (res.data && res.data.data && res.data.data.length === 1) {
                        return res.data.data[0];
                    }

                    return {
                        success:0,
                        failed:0
                    };
                })
        }

        getWorkingHours (entityType:eEntityType, entity:any, daysRange:number = 90):ng.IPromise<any> {
            let entityTypeSwitch = function () {
                switch (entityType) {
                    case eEntityType.USER:
                        return this._getUserWorkingHours(entity, daysRange);
                    default:
                        return null;
                }
            }.bind(this);

            return this.$q.when(entityTypeSwitch())
                .then((res:ng.IHttpPromiseCallbackArg<IEntityActivity>) => {
                    return res.data.data;
                })
        }

        getSourceDevices (entityType:eEntityType, entity:any, daysRange:number = 90):ng.IPromise<any> {
            let entityTypeSwitch = function () {
                switch (entityType) {
                    case eEntityType.USER:
                        return this._getUserSourceDevices(entity, daysRange);
                    default:
                        return null;
                }
            }.bind(this);

            return this.$q.when(entityTypeSwitch())
                .then((res:ng.IHttpPromiseCallbackArg<IEntityActivity>) => {
                    return res.data.data;
                })
        }

        getTargetDevices (entityType:eEntityType, entity:any, daysRange:number = 90):ng.IPromise<any> {
            let entityTypeSwitch = function () {
                switch (entityType) {
                    case eEntityType.USER:
                        return this._getUserTargetDevices(entity, daysRange);
                    default:
                        return null;
                }
            }.bind(this);

            return this.$q.when(entityTypeSwitch())
                .then((res:ng.IHttpPromiseCallbackArg<IEntityActivity>) => {
                    return res.data.data;
                })
        }

        getDataUsages (entityType:eEntityType, entity:any, daysRange:number = 90):ng.IPromise<any> {
            let entityTypeSwitch = function () {
                switch (entityType) {
                    case eEntityType.USER:
                        return this._getUserDataUsages(entity, daysRange);
                    default:
                        return null;
                }
            }.bind(this);

            return this.$q.when(entityTypeSwitch())
                .then((res:ng.IHttpPromiseCallbackArg<IEntityActivity>) => {
                    return res.data.data;
                })
        }


        static $inject = ['$http', '$q', 'BASE_URL','appConfig'];

        constructor (public $http:ng.IHttpService, public $q:ng.IQService, public BASE_URL:string,public appConfig:IAppConfigService) {
        }

    }

    angular.module('Fortscale.shared.services.entityActivityUtils', [])
        .service('entityActivityUtils', EntityActivityUtilsService);
}
