module Fortscale.layouts.user {

    import IActivityUserDevice = Fortscale.shared.services.entityActivityUtils.IActivityUserDevice;
    import IUserDevice = Fortscale.shared.services.deviceUtilsService.IUserDevice;
    import IDeviceUtilsService = Fortscale.shared.services.deviceUtilsService.IDeviceUtilsService;
    import IUserActivityExtend = Fortscale.shared.services.deviceUtilsService.IUserActivityExtend;

    class ActivityDevicesController {

        _devices: IActivityUserDevice[] = [];
        devices: IUserDevice[] = null;


        //Remove all devices with no count, if any returned from server.
        _removeZeroCount (devices: IUserDevice[]) {
            //return new array of IUserDevices with devices
            return _.filter(devices, (device) => device.count > 0);
        }

        /**
         * Takes received source devices, sorts, repositions 'other', and adds percent to each, then stores on devices
         * @private
         */
        _digestDevices ():void  {

            let devices: IUserDevice[];

            // sort _devices
            devices = _.orderBy<IUserDevice>(_.cloneDeep(this._devices), 'count', 'desc');

            // pluck "other" and push to the end
            this.deviceUtilsService.repositionOthers(devices);

            // remove all items with zero count
            let devicesAsExtend:IUserActivityExtend[] = devices;
            devicesAsExtend = this.deviceUtilsService.removeZeroCount(devicesAsExtend);
            devicesAsExtend = this.deviceUtilsService.updatePercentageOnDevice(devicesAsExtend);


            this.devices = <IUserDevice[]>devicesAsExtend;
        }

        _sortDevices (): void {
            this.devices = _.orderBy(this.devices, [
                (userDevice:IUserDevice) => userDevice.deviceName === 'Others',
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
            _.each(this.devices, (device: IUserDevice, index:number) => {
                this.$timeout(() => {
                    device.active = true;
                }, ((this.devices.length-1)-index)*400 + 200);
            });
        }

        /**
         * Initiates watch on received source devices
         * @private
         */
        _initDevicesWatch () {
            this.$scope.$watch(
                () => this._devices,
                (devices) => {
                    if (devices && devices.length) {
                        this._digestDevices();
                        this._sortDevices();
                        this._activateBars();
                    } else if (devices) {
                        this.devices = [];
                    }
                }
            );
        }

        $onInit () {

            this._initDevicesWatch();
        }

        static $inject = ['$scope', '$timeout','deviceUtilsService'];

        constructor (public $scope:ng.IScope, public $timeout:ng.ITimeoutService, public deviceUtilsService:IDeviceUtilsService) {
        }
    }

    let activityDevicesComponent:ng.IComponentOptions = {
        controller: ActivityDevicesController,
        templateUrl: 'app/layouts/user/components/user-activity-devices/user-activity-devices.component.html',
        bindings: {
            _devices: '<devices',
            description: '@'
        }
    };
    angular.module('Fortscale.layouts.user')
        .component('userActivityDevices', activityDevicesComponent);
}
