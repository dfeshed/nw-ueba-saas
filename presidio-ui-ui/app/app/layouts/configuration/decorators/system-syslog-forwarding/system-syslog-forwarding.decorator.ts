module Fortscale.layouts.configuration.decorator {
    'use strict';

    angular.module('Fortscale.layouts.configuration')
        .run(
            ['Fortscale.layouts.configuration.decoratorService', 'tagsUtils',
                (decoratorService:IConfigurationDecoratorService,
                    tagsUtils:any) => {
                    decoratorService
                        .addDecoratorForm({
                            containerId: 'system.syslogforwarding'
                        })
                        .addDecoratorItem({
                            id: 'system.syslogforwarding.ip',
                            component: 'ip'
                        })
                        .addDecoratorItem({
                            id: 'system.syslogforwarding.forwardingtype',
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
                        })
                        .addDecoratorItem({
                            id: 'system.syslogforwarding.messageformat',
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
                        })
                        .addDecoratorItem({
                            id: 'system.syslogforwarding.usertypes',
                            component: 'checkbox',
                            config: {
                                selectAll: true
                            },
                            resolve: {
                                items: () => {
                                    return tagsUtils.getTags()
                                        .then((tags:any) => {
                                            return _.map(tags.data, (tag:any) => {
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
                            }
                        })
                        .addDecoratorItem({
                            id: 'system.syslogforwarding.alertseverity',
                            component: 'severity'
                        })
                        .addDecoratorItem({
                            id: 'system.syslogforwarding.enabled',
                            data: {
                                defaultValue: false,
                                trueLabel: 'Yes',
                                falseLabel: 'No'
                            }
                        });
                        //.addDecoratorItem({
                        //    id: 'system.syslogforwarding.forwardHistoricalAlerts',
                        //    displayName: 'Forward Historical Alerts',
                        //    replace: true,
                        //    component: 'forwardHistoricalAlerts'
                        //});
                }])
}
