/**
 *
 */
module Fortscale.shared.services.deviceUtilsService{
    'use strict';

    const OTHERS_NAME = 'Others';

    import IActivityUserDevice = Fortscale.shared.services.entityActivityUtils.IActivityUserDevice;
    import IActivityTopApplication = Fortscale.shared.services.entityActivityUtils.IActivityTopApplication;
    import IActivityTopDirectory = Fortscale.shared.services.entityActivityUtils.IActivityTopDirectory;



    export interface IUserActivityExtend{
        percent?: number,
        active?: boolean,
        count: number
    }

    export interface IUserDevice extends IActivityUserDevice,IUserActivityExtend {

    }

    export interface IUserTopApplication extends IActivityTopApplication,IUserActivityExtend {
    }
    export interface IUserTopRecipientDomain extends IActivityTopApplication,IUserActivityExtend {
    }


    export interface IUserTopDirectory extends IActivityTopApplication,IUserActivityExtend {
    }

    /**
     * Service interfaces
     */

     export interface IDeviceUtilsService{
        updatePercentageOnDevice(devices: IUserActivityExtend[]): IUserActivityExtend[];
        removeZeroCount (devices: IUserActivityExtend[]):IUserActivityExtend[];
        repositionOthers (devices: IUserDevice[]);
     }

     class DeviceUtilsService implements IDeviceUtilsService {
         /**
          * Calculates a device's percent, and store on the device object
          * @param {number} sum
          * @param {Array<IUserDevice>} device
          * @private
          */
         private _calcDevicePercent (sum: number, device: IUserActivityExtend) {
             if (sum) {
                 device.percent = Math.round(device.count / sum * 10000) / 100;
             } else {
                 device.percent = 0;
             }
         }

         /**
          * This method calculate the sum of all count fields on user device,
          * and for each device calculate its count / sum * 100 and save it as percentage field on the device
          * @param devices
          * @returns {IUserActivityExtend[]}
          */
         updatePercentageOnDevice(devices: IUserActivityExtend[]): IUserActivityExtend[]{
            // calc sum
            let sum = _.sumBy<IUserActivityExtend>(<any>devices, 'count');

            // iterate and calc percent
            _.each(devices, this._calcDevicePercent.bind(this, sum));
             return devices;

         }

         /**
          * Create and return new device list which not contain the devices with count  = 0
          * @param devices
          * @returns {T[]}
          */
         removeZeroCount (devices: IUserActivityExtend[]):IUserActivityExtend[] {
             return _.filter(devices, (device) => device.count > 0);
         }

         /**
          * Pulls out 'other' member and puts it in the end of the list.
          * @param devices
          * @private
          */
         repositionOthers (devices: IUserDevice[]) {
             let pluckIndex = -1;
             _.some(devices, (device: IUserDevice, index: number) => {
                 if (device.deviceName === OTHERS_NAME) {
                     pluckIndex = index;
                     return true;
                 }
             });
             if (pluckIndex !== -1) {
                 let device = devices[pluckIndex];
                 device.deviceName = 'Others';
                 devices.splice(pluckIndex, 1);
                 devices.push(device);
             }
         }

        constructor() {

        }
     }


    angular.module('Fortscale.shared.services')
        .service('deviceUtilsService', DeviceUtilsService)
}


