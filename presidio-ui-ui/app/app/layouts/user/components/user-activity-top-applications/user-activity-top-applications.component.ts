module Fortscale.layouts.user {

    import IActivityTopApplication = Fortscale.shared.services.entityActivityUtils.IActivityTopApplication;
    import IDeviceUtilsService = Fortscale.shared.services.deviceUtilsService.IDeviceUtilsService;
    import IUserActivityExtend = Fortscale.shared.services.deviceUtilsService.IUserActivityExtend;
    import IUserTopApplication = Fortscale.shared.services.deviceUtilsService.IUserTopApplication;
    import IAppConfigService = Fortscale.appConfigProvider.IAppConfigService;

    class AppSettings{
        icon:string;
        name:string;
        displayName:string;
    }

    class ActivityTopApplicationsController {

        _applications: IActivityTopApplication[] = [];
        applications: IUserTopApplication[] = null;

        appsSettings:{ [id:string] : AppSettings};


        //Remove all application with no count, if any returned from server.
        _removeZeroCount (application: IUserTopApplication[]) {
            //return new array of IUserTopApplications with application
            return _.filter(application, (app) => app.count > 0);
        }

        /**
         * Takes received source application, sorts, repositions 'other', and adds percent to each, then stores on application
         * @private
         */
        _digestApplications ():void  {

            let application: IUserTopApplication[];

            // sort _application
            application = _.orderBy<IUserTopApplication>(_.cloneDeep(this._applications), 'count', 'desc');

            // // pluck "other" and push to the end
            //this.deviceUtilsService.repositionOthers(application);

            let applications:IUserActivityExtend[] = application;
            // remove all items with zero count
            applications = this.deviceUtilsService.removeZeroCount(applications);
            applications = this.deviceUtilsService.updatePercentageOnDevice(applications);


            this.applications = <IUserTopApplication[]>applications;
        }

        _sortApplications (): void {
            this.applications = _.orderBy(this.applications, [
                (userApplication:IUserTopApplication) => userApplication.name === 'Others',
                'count'
            ], [
                'asc',
                'desc'
            ]);

        }

        /**
         * Activates the bars
         * @private
         */
        _activateBars () {
            _.each(this.applications, (application: IUserTopApplication, index:number) => {
                this.$timeout(() => {
                    application.active = true;
                }, ((this.applications.length-1)-index)*400 + 200);
            });
        }

        /**
         * Initiates watch on received source application
         * @private
         */
        _initApplicationsWatch () {
            this.$scope.$watch(
                () => this._applications,
                (application) => {
                    if (application && application.length) {
                        this._digestApplications();
                        this._sortApplications();
                        this._activateBars();
                    } else if (application) {
                        this.applications = [];
                    }
                }
            );
        }

        /**
         * Read from configuration to build map of the application to display namd and icon
         * @private
         */
        _buildIconsAndDisplayNames(){

            this.appsSettings=[];
           _.each(this.appConfig.getConfigNodesTree().nodes.messages.nodes.en.nodes.top_applications.nodes,(node:any)=>{
               //For each application:
                let appKey=node.id; //Get partial key of the application's attributes

                let appSettings = this.appConfig.getConfigItemsByContainer(appKey); //Get sub values for the application
                let currentApp: AppSettings = new AppSettings();

                _.each(appSettings,(setting)=>{
                    currentApp[setting.displayName] = setting.value;
                });

                this.appsSettings[currentApp.name] = currentApp;

            });
        }

        /**
         * Read the settings for application by name, return default values if not found
         * @param appName
         * @returns {AppSettings}
         */
        getSettingsForApp(appName:string):AppSettings{
            let app:AppSettings = this.appsSettings[appName];
            if (_.isNil(app)){
                //No configurations use defaults
                app = new AppSettings;

                app.name = appName;
                app.displayName = appName;
                app.icon = "application.png";
            }
            return app;

        }

        /**
         * Build style object for image
         * @param appName
         * @returns {{background-image: string, border: string}}
         */
        getImageStyle(appName:string):any{
            let iconPath:string = this.getSettingsForApp(appName).icon;
            iconPath="assets/images/icons/top_applications/"+iconPath;
            return { 'background-image': 'url('+iconPath+')', border: 'none'}
        }


        $onInit () {
            this._buildIconsAndDisplayNames();
            this._initApplicationsWatch();

        }

        static $inject = ['$scope', '$timeout','deviceUtilsService','appConfig'];

        constructor (public $scope:ng.IScope, public $timeout:ng.ITimeoutService,
                     public deviceUtilsService:IDeviceUtilsService,public appConfig:IAppConfigService) {
        }
    }

    let activityTopApplicationsComponent:ng.IComponentOptions = {
        controller: ActivityTopApplicationsController,
        templateUrl: 'app/layouts/user/components/user-activity-top-applications/user-activity-top-applications.component.html',
        bindings: {
            _applications: '<applications',
            description: '@'
        }
    };
    angular.module('Fortscale.layouts.user')
        .component('userActivityTopApplications', activityTopApplicationsComponent);
}
