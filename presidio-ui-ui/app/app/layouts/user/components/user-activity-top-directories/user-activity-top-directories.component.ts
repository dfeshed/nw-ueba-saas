module Fortscale.layouts.user {

    import IActivityTopDirectory = Fortscale.shared.services.entityActivityUtils.IActivityTopDirectory;
    import IDeviceUtilsService = Fortscale.shared.services.deviceUtilsService.IDeviceUtilsService;
    import IUserActivityExtend = Fortscale.shared.services.deviceUtilsService.IUserActivityExtend;
    import IUserTopDirectory = Fortscale.shared.services.deviceUtilsService.IUserTopDirectory;
    import IAppConfigService = Fortscale.appConfigProvider.IAppConfigService;

    class AppSettings{
        icon:string;
        name:string;
        displayName:string;
    }

    class ActivityTopDirectoriesController {

        _directories: IActivityTopDirectory[] = [];
        directories: IUserTopDirectory[] = null;

        appsSettings:{ [id:string] : AppSettings};


        //Remove all directory with no count, if any returned from server.
        _removeZeroCount (directory: IUserTopDirectory[]) {
            //return new array of IUserTopDirectories with directory
            return _.filter(directory, (app) => app.count > 0);
        }

        /**
         * Takes received source directory, sorts, repositions 'other', and adds percent to each, then stores on directory
         * @private
         */
        _digestDirectories ():void  {

            let directory: IUserTopDirectory[];

            // sort _directory
            directory = _.orderBy<IUserTopDirectory>(_.cloneDeep(this._directories), 'count', 'desc');

            // // pluck "other" and push to the end
            //this.deviceUtilsService.repositionOthers(directory);

            let directories:IUserActivityExtend[] = directory;
            // remove all items with zero count
            directories = this.deviceUtilsService.removeZeroCount(directories);
            directories = this.deviceUtilsService.updatePercentageOnDevice(directories);


            this.directories = <IUserTopDirectory[]>directories;
        }

        _sortDirectories (): void {
            this.directories = _.orderBy(this.directories, [
                (userDirectory:IUserTopDirectory) => userDirectory.name === 'Others',
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
            _.each(this.directories, (directory: IUserTopDirectory, index:number) => {
                this.$timeout(() => {
                    directory.active = true;
                }, ((this.directories.length-1)-index)*400 + 200);
            });
        }

        /**
         * Initiates watch on received source directory
         * @private
         */
        _initDirectoriesWatch () {
            this.$scope.$watch(
                () => this._directories,
                (directory) => {
                    if (directory && directory.length) {
                        this._digestDirectories();
                        this._sortDirectories();
                        this._activateBars();
                    } else if (directory) {
                        this.directories = [];
                    }
                }
            );
        }

        $onInit () {
            this._initDirectoriesWatch();

        }

        static $inject = ['$scope', '$timeout','deviceUtilsService'];

        constructor (public $scope:ng.IScope, public $timeout:ng.ITimeoutService,
                     public deviceUtilsService:IDeviceUtilsService) {
        }
    }

    let activityTopDirectoriesComponent:ng.IComponentOptions = {
        controller: ActivityTopDirectoriesController,
        templateUrl: 'app/layouts/user/components/user-activity-top-directories/user-activity-top-directories.component.html',
        bindings: {
            _directories: '<directories',
            description: '@'
        }
    };
    angular.module('Fortscale.layouts.user')
        .component('userActivityTopDirectories', activityTopDirectoriesComponent);
}
