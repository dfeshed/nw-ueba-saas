module Fortscale.layouts.user {



    import INanobarAutomation = Fortscale.shared.services.fsNanobarAutomation.INanobarAutomation;
    import ITagDefinition = Fortscale.shared.services.tagsUtilsService.ITagDefinition;
    import IEntityActivityUtilsService = Fortscale.shared.services.entityActivityUtils.IEntityActivityUtilsService;
    import IActivityUserCountry = Fortscale.shared.services.entityActivityUtils.IActivityUserCountry;
    import IActivityTopApplication = Fortscale.shared.services.entityActivityUtils.IActivityTopApplication;
    import IActivityTopDirectory = Fortscale.shared.services.entityActivityUtils.IActivityTopDirectory;
    import IActivityTopRecipientDomain = Fortscale.shared.services.entityActivityUtils.IActivityTopRecipientDomain;
    import IActivityUserClassificationExposure = Fortscale.shared.services.entityActivityUtils.IActivityUserClassificationExposure;

    import eEntityType = Fortscale.shared.services.entityActivityUtils.eEntityType;
    import IActivityOrganizationCountry = Fortscale.shared.services.entityActivityUtils.IActivityOrganizationCountry;
    import IActivityUserAuthentication = Fortscale.shared.services.entityActivityUtils.IActivityUserAuthentication;
    import IActivityUserWorkingHour = Fortscale.shared.services.entityActivityUtils.IActivityUserWorkingHour;
    import IActivityUserDevice = Fortscale.shared.services.entityActivityUtils.IActivityUserDevice;
    import IActivityUserDataUsage = Fortscale.shared.services.entityActivityUtils.IActivityUserDataUsage;
    import INanobarAutomationService = Fortscale.shared.services.fsNanobarAutomation.INanobarAutomationService;

    const NUMBER_OF_TICKS:number = 11;

    class UserAttributesController {


        loadingProgress:number;
        nanobarAutomation:INanobarAutomation;
        user:any;
        activities:IActivities;

        /**
         * Start the user top countries load
         * @returns {IPromise<void>}
         * @private
         */
        _initLoadUserTopCountriesActivity ():ng.IPromise<void> {
            return this.entityActivityUtils.getTopCountries<IActivityUserCountry>(eEntityType.USER, this.user)
                .then((countries:IActivityUserCountry[]) => {
                    this.activities.user.topCountries = countries;
                })
                .catch((err) => {
                    console.error('There was an error loading organization top countries.', err);
                    this.activities.user.topCountries = [];
                });
        }


        /**
         * Starts the organization top countries load
         * @returns {IPromise<void>}
         * @private
         */
        _initLoadOrganizationTopCountriesActivity ():ng.IPromise<void> {
            return this.entityActivityUtils.getTopCountries<IActivityOrganizationCountry>(eEntityType.ORGANIZATION)
                .then((countries:IActivityOrganizationCountry[]) => {
                    this.activities.organization.topCountries = countries;
                })
                .catch((err) => {
                    console.error('There was an error loading organization top countries.', err);
                    this.activities.organization.topCountries = [];
                });
        }

        /**
         * Start the user's authentications load
         * @returns {IPromise<void>}
         * @private
         */
        _initLoadingAuthenticationsActivity ():ng.IPromise<void> {
            return this.entityActivityUtils.getAuthentications<IActivityUserAuthentication>(eEntityType.USER, this.user)
                .then((authentications:IActivityUserAuthentication) => {
                    this.activities.user.authentications = authentications;
                })
                .catch((err) => {
                    console.error('There was an error loading user authentications.', err);
                    this.activities.user.authentications = {
                        success: 0,
                        failed: 0
                    };
                });
        }

        /**
         * Start the user's working hours load
         * @returns {IPromise<void>}
         * @private
         */
        _initLoadingWorkingHourActivity ():ng.IPromise<void> {
            return this.entityActivityUtils.getWorkingHours<IActivityUserWorkingHour>(eEntityType.USER, this.user)
                .then((workingHours:IActivityUserWorkingHour[]) => {
                    this.activities.user.workingHours = workingHours;
                })
                .catch((err) => {
                    console.error('There was an error loading user working-hours.', err);
                    this.activities.user.workingHours = [];
                });
        }

        /**
         * Start the user's source devices load
         * @returns {IPromise<void>}
         * @private
         */
        _initLoadingSourceDevicesActivity ():ng.IPromise<void> {
            return this.entityActivityUtils.getSourceDevices<IActivityUserDevice>(eEntityType.USER, this.user)
                .then((sourceDevices:IActivityUserDevice[]) => {
                    this.activities.user.sourceDevices = sourceDevices;
                })
                .catch((err) => {
                    console.error('There was an error loading user source devices.', err);
                    this.activities.user.sourceDevices = [];
                });
        }

        /**
         * Start the user's top applications load
         * @returns {IPromise<void>}
         * @private
         */
        _initLoadingTopApplciationsActivity ():ng.IPromise<void> {
            return this.entityActivityUtils.getTopApplications<IActivityTopApplication>(eEntityType.USER, this.user)
                .then((topApplications:IActivityTopApplication[]) => {
                    this.activities.user.topApplications = topApplications;
                })
                .catch((err) => {
                    console.error('There was an error loading user source devices.', err);
                    this.activities.user.topApplications = [];
                });
        }

        /**
         * Start the user's top recipients load
         * @returns {IPromise<void>}
         * @private
         */
        _initLoadingTopRecipientsActivity ():ng.IPromise<void> {
            return this.entityActivityUtils.getTopRecipientDomain<IActivityTopRecipientDomain>(eEntityType.USER, this.user)
                .then((topRecipientsDomain:IActivityTopRecipientDomain[]) => {
                    this.activities.user.topRecipientsDomains = topRecipientsDomain;
                })
                .catch((err) => {
                    console.error('There was an error loading user source devices.', err);
                    this.activities.user.topRecipientsDomains = [];
                });
        }

        /**
         * Start the user's top directories load
         * @returns {IPromise<void>}
         * @private
         */
        _initLoadingTopDirectoriesActivity ():ng.IPromise<void> {
            return this.entityActivityUtils.getTopDirectories<IActivityTopDirectory>(eEntityType.USER, this.user)
                .then((topDirectories:IActivityTopDirectory[]) => {
                    this.activities.user.topDirectories = topDirectories;
                })
                .catch((err) => {
                    console.error('There was an error loading user source devices.', err);
                    this.activities.user.topDirectories = [];
                });
        }

        /**
         * Start the user's target devices load
         * @returns {IPromise<void>}
         * @private
         */
        _initLoadingTargetDevicesActivity ():ng.IPromise<void> {
            return this.entityActivityUtils.getTargetDevices<IActivityUserDevice>(eEntityType.USER, this.user)
                .then((targetDevices:IActivityUserDevice[]) => {
                    this.activities.user.targetDevices = targetDevices;
                })
                .catch((err) => {
                    console.error('There was an error loading user source devices.', err);
                    this.activities.user.targetDevices = [];
                });
        }

        /**
         * Start the user's data usages load
         * @returns {IPromise<void>}
         * @private
         */
        _initLoadingDataUsagesActivity ():ng.IPromise<void> {
            return this.entityActivityUtils.getDataUsages<IActivityUserDataUsage>(eEntityType.USER, this.user)
                .then((dataUsages:IActivityUserDataUsage[]) => {
                    this.activities.user.dataUsages = dataUsages;
                })
                .catch((err) => {
                    console.error('There was an error loading user data usages.', err);
                    this.activities.user.dataUsages = [];
                });
        }

        /**
         * Start the user's data usages load
         * @returns {IPromise<void>}
         * @private
         */
        _initLoadingClassificationExposureActivity ():ng.IPromise<void> {
            return this.entityActivityUtils.getClassificationExposure<IActivityUserClassificationExposure>(eEntityType.USER, this.user)
                .then((data:IActivityUserClassificationExposure[]) => {
                    this.activities.user.classificationExposure = data.length>0? data[0]:{ classified:0,     total:0};
                })
                .catch((err) => {
                    console.error('There was an error loading user data usages.', err);
                    this.activities.user.classificationExposure = null;
                });
        }

        _initLoadingSequence () {
            let promises = [
                this._initLoadUserTopCountriesActivity(),
                this._initLoadOrganizationTopCountriesActivity(),
                this._initLoadingAuthenticationsActivity(),
                this._initLoadingWorkingHourActivity(),
                this._initLoadingSourceDevicesActivity(),
                this._initLoadingTargetDevicesActivity(),
                this._initLoadingDataUsagesActivity(),
                this._initLoadingTopApplciationsActivity(),
                this._initLoadingTopDirectoriesActivity(),
                this._initLoadingTopRecipientsActivity(),
                this._initLoadingClassificationExposureActivity()
            ];

            this.fsNanobarAutomationService.addPromises('user-page', promises);
        }

        _initUserWatch () {
            this.$scope.$watch(
                () => this.$scope.userCtrl.user,
                (user:any) => {
                    if (user) {
                        this.user = user;
                        this._initLoadingSequence();
                        this.page.setPageTitle(`${user.username} - User Profile`);
                    }
                });
        }

        getDlpMode():boolean{
            return this.entityActivityUtils.getDlpMode();
        }

        _init () {

            this.activities = new Activities();
            this._initUserWatch();

        }

        static $inject = ['$scope', 'entityActivityUtils', 'fsNanobarAutomationService', 'page'];

        constructor (public $scope:any, public entityActivityUtils:IEntityActivityUtilsService, public fsNanobarAutomationService: INanobarAutomationService, public page: any
        ) {
            this._init();
        }
    }

    angular.module('Fortscale.layouts.user')
        .controller('userAttributesController', UserAttributesController);
}
