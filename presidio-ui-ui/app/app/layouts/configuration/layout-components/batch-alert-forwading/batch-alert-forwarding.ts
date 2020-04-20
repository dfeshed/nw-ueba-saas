module Fortscale.layouts.configuration.components.batchAlertForwarding {
    import IConfigurationNavigationService = Fortscale.layouts.configuration.configurationNavigation.ConfigurationNavigationService;
    import IAppConfigProvider = Fortscale.appConfigProvider.IAppConfigProvider;

    'use strict';
    class BatchAlertForwardingController {

        isLoading:boolean;
        _initialState:any;
        models:any;
        configModels:any;
        tagsList:{value:string, label:string}[];
        dates:any;
        message:string;
        updateDates:(res:{value:any}) => void;

        /**
         * Gets all tags in the system
         * @returns {IPromise<Array>|Promise<Array>|Promise<T>}
         * @private
         */
        _getTags () {
            return this.tagsUtils.getTags()
                .then((tags:any) => {
                    this.tagsList = _.map(tags.data, (tag:any) => {
                        return {
                            value: tag.name,
                            label: tag.displayName
                        }
                    });
                })
                .catch((err:any) => {
                    console.error(
                        'Fortscale.layouts.configuration: There was an error trying to fetch tags',
                        err);
                    return [];
                })
        }

        /**
         * Sets the initial dates value. Will be consumed if date range controller is not used.
         * @private
         */
        _setInitialDates ():void {
            this.dates = this.dateRanges.getByDaysRange(7, 'short');
        };

        /**
         * Sets the initial state
         *
         * @private
         */
        _setInitialState () {
            this._initialState = {
                'system.syslogforwarding.forwardingtype': {
                    component: 'checkbox',
                    config: {
                        radioBox: true,
                        horizontal: true
                    },
                    data: {
                        items: [
                            {
                                value: 'ALERT',
                                label: 'Alerts'
                            }, {
                                value: 'ALERT_AND_INDICATORS',
                                label: 'Alerts and Indicators'
                            }
                        ],
                        checked: ['ALERT']
                    }
                },
                'system.syslogforwarding.ip': {
                    id: 'system.syslogforwarding.ip',
                    component: 'ip',
                    data: {},
                    config: {}
                },
                'system.syslogforwarding.port': {},
                'system.syslogforwarding.alertseverity': {
                    component: 'severity',
                    data: {},
                    config: {}
                },
                'system.syslogforwarding.messageformat': {
                    component: 'checkbox',
                    config: {
                        radioBox: true,
                        horizontal: true
                    },
                    data: {
                        items: [
                            {
                                value: 'RFC_3164',
                                label: 'RFC 3164'
                            }, {
                                value: 'RFC_5424',
                                label: 'RFC 5424'
                            }
                        ],
                        checked: ['RFC_3164']
                    }
                },
                'system.syslogforwarding.usertypes': {
                    component: 'checkbox',
                    config: {
                        selectAll: true
                    },
                    data: {
                        items: this.tagsList
                    }
                }
            }
        }

        /**
         * Sets initial values to all models
         *
         * @private
         */
        _setInitialValues () {
            this.models = {};
            this.dates = this.dateRanges.getByDaysRange(7, 'short').split(',');


            _.each(this._initialState, (stateItem:any, stateItemId:string) => {
                let configItem = this.appConfig.getConfigItem(stateItemId);
                this.models[stateItemId] = _.merge(configItem, stateItem);
            });

        }

        /**
         * Handler to update dates.
         *
         * @param {{value: *}}res
         * @private
         */
        _updateDates (res:{value:any}) {
            this.dates = res.value.split(',');
        }

        /**
         * An action to request forwarding
         *
         */
        forward () {
            this.isLoading = true;
            this.$http.post(this.BASE_URL + '/syslogforwarding/forward_alerts', {
                start_time: parseInt(this.dates[0]),
                end_time: parseInt(this.dates[1]),
                ip: this.configModels['system.syslogforwarding.ip'],
                port: this.configModels['system.syslogforwarding.port'],
                forwarding_type: this.configModels['system.syslogforwarding.forwardingtype'],
                message_format: this.configModels['system.syslogforwarding.messageformat'],
                user_tags: this.configModels['system.syslogforwarding.usertypes'] ?
                    this.configModels['system.syslogforwarding.usertypes'].split(',') : [],
                alert_severities: this.configModels['system.syslogforwarding.alertseverity'] ?
                    this.configModels['system.syslogforwarding.alertseverity'].split(',') : []
            })
                .then(res => {
                    this.message = res && res.data && (<any>res.data).message;
                    this.isLoading = false;
                })
                .catch(err => {
                    this.message = err.data.message;
                    this.isLoading = false;
                })
        }

        $onInit ():void {
            this.isLoading = true;
            this._getTags()
                .then(() => {
                    this._setInitialDates();
                    this._setInitialState();
                    this._setInitialValues();
                    this.isLoading = false;
                })
                .catch(() => {
                    this.isLoading = false;
                })

        }

        static $inject = ['tagsUtils', 'appConfig', '$http', 'BASE_URL', 'dateRanges'];

        constructor (public tagsUtils:any, public appConfig:IAppConfigProvider, public $http:ng.IHttpService,
            public BASE_URL:string, public dateRanges:any) {
            // Initialize properties
            this.isLoading = false;
            this.models = {};
            this.configModels = {};
            this.updateDates = (res:{value:any}) => {
                return this._updateDates(res);
            }
        }
    }

    let configurationComponentBatchAlertForwarding:ng.IComponentOptions = {
        controller: BatchAlertForwardingController,
        controllerAs: 'baCtrl',
        templateUrl: 'app/layouts/configuration/layout-components/batch-alert-forwading/batch-alert-forwarding.html',
    };

    angular.module('Fortscale.layouts.configuration')
        .component('configurationComponentBatchAlertForwarding', configurationComponentBatchAlertForwarding)
        .run(['Fortscale.layouts.configuration.configurationNavigationService',
            (configurationNavigationService:IConfigurationNavigationService) =>configurationNavigationService.addConfigurationPage(
                {
                    id: 'system.syslogforwarding.batchAlertForwarding',
                    displayName: 'Batch Alert Forwarding via Syslog',
                    component: 'configurationComponentBatchAlertForwarding',
                    doNotShowHeader: true,
                    formClassNames: 'batch-alert-forwarding'
                })]);
}
