module Fortscale.layouts.configuration.decorator {
    'use strict';

    angular.module('Fortscale.layouts.configuration')
        .run(['Fortscale.layouts.configuration.decoratorService', (decoratorService:IConfigurationDecoratorService) => {
            decoratorService
                .addDecoratorForm({
                    containerId: 'system.siem'
                })
                .addDecoratorItem({
                    id: 'system.siem.type',
                    component: 'dropdown',
                    data: {
                        items: [
                            {
                                value: 'splunk',
                                label: 'Splunk'
                            },
                            {
                                value: 'qradar',
                                label: 'QRadar'
                            }
                        ],
                        defaultSelect: 'splunk'
                    }
                })
        }])
}
