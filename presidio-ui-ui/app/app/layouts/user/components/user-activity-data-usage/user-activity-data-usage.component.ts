module Fortscale.layouts.user {

    import IActivityUserDataUsage = Fortscale.shared.services.entityActivityUtils.IActivityUserDataUsage;

    interface IDataUsage {
        icon:string,
        title:string,
        value:string
    }


    class ActivityDataUsagesController {

        _dataUsages:IActivityUserDataUsage[] = [];
        dataUsages:IDataUsage[];
        title:string;
        keys:string[]; // Which keys to display in the component

        _prettyBytes (val) {
            let prettyBytesFilter:Function = this.$filter('prettyBytes');
            return prettyBytesFilter(val);
        }

        _vpnValueProcess (val) {
            return this._prettyBytes(val);
        }

        _oracleValueProcess (val) {
            return val !== 1 ? `${val} Accesses` : `1 Access`;
        }

        _printValueProcess (val) {
            return this._prettyBytes(val);
        }

        /**
         * Object used as a map for received values
         * @type {{[vpn_session.databucket_histogram]: {title: string, icon: string, value: any}, [oracle.db_object_histogram]: {title: string, icon: string, value: any}, [prnlog.file_size_histogram]: {title: string, icon: string, value: any}}}
         */
        dataUsageMap = {
            'vpn_session.databucket_histogram': {
                title: 'VPN',
                icon: 'data-usage-vpn-icon',
                value: this._vpnValueProcess.bind(this)
            },
            'oracle.db_object_histogram': {
                title: 'Oracle DB',
                icon: 'data-usage-oracle-icon',
                value: this._oracleValueProcess.bind(this)
            },
            'prnlog.sum_of_file_size': {
                title: 'Print',
                icon: 'data-usage-print-icon',
                value: this._printValueProcess.bind(this)
            },
            'dlpmail.attachment_file_size_histogram': {
                title: 'Mail Attachments',
                icon: 'data-usage-attachment-icon',
                value: this._printValueProcess.bind(this)
            },
            'dlpfile.sum_of_copied_files_to_removable_device_size': {
                title: 'Removable Media',
                icon: 'data-usage-usb-icon',
                value: this._printValueProcess.bind(this)
            }

        };

        /**
         * Digests received dataUsages. Uses map to parse received values.
         * @private
         */
        _digestDataUsages () {
            let ctrl:any=this;
            let relevantDataUsageMap = {};

            _.each(this.keys,(key:string)=>{
                relevantDataUsageMap[key] = ctrl.dataUsageMap[key];
            });
            this.dataUsages =  _.map(this._dataUsages, (dataUsage:IActivityUserDataUsage) => {
                let dataUsageMap = relevantDataUsageMap[dataUsage.dataEntityId];
                if (!dataUsageMap){
                    return;
                }
                return {
                    icon: dataUsageMap.icon,
                    title: dataUsageMap.title,
                    value: dataUsageMap.value(dataUsage.value || 0)
                }
            });

            this.dataUsages = _.without(this.dataUsages, undefined);

            // populate missing rows
            // Iterate through the keys, and find if any are missing, if so populate with zero value
            _.each(this.keys,
                (dataEntityId) => {
                    if (!_.some(this._dataUsages,
                            (dataUsage:IActivityUserDataUsage) => dataUsage.dataEntityId === dataEntityId)) {
                        let dataUsageMap = relevantDataUsageMap[dataEntityId];

                        this.dataUsages.push({
                            icon: dataUsageMap.icon,
                            title: dataUsageMap.title,
                            value: dataUsageMap.value(0)
                        });
                    }
                });
        }

        /**
         * Initiates watch on received dataUsages
         * @private
         */
        _initDataUsagesWatch () {
            this.$scope.$watch(
                () => this._dataUsages,
                () => {
                    if (this._dataUsages && _.isArray(this._dataUsages)) {
                        this._digestDataUsages();
                    }
                }
            );
        }

        $onInit () {

            this._initDataUsagesWatch();
        }

        static $inject = ['$scope', '$timeout', '$filter'];

        constructor (public $scope:ng.IScope, public $timeout:ng.ITimeoutService,
            public $filter:(name:string) => (val) => any) {
        }
    }

    let activityDataUsagesComponent:ng.IComponentOptions = {
        controller: ActivityDataUsagesController,
        templateUrl: 'app/layouts/user/components/user-activity-data-usage/user-activity-data-usage.component.html',
        bindings: {
            _dataUsages: '<ddataUsages',
            title: '@',
            keys: '='
        }
    };
    angular.module('Fortscale.layouts.user')
        .component('userActivityDataUsages', activityDataUsagesComponent);
}
