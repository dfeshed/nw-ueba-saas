module Fortscale.layouts.configuration.decorator {
    'use strict';

    angular.module('Fortscale.layouts.configuration')
        .run(['Fortscale.layouts.configuration.decoratorService', (decoratorService:IConfigurationDecoratorService) => {
            decoratorService
                .addDecoratorForm({
                    containerId: 'system.email'
                })
                .addDecoratorItem({
                    id: 'system.email.auth',
                    component: 'dropdown',
                    data: {
                        items: [
                            {
                                value: 'ssl',
                                label: 'SSL'
                            },
                            {
                                value: 'tsl',
                                label: 'TSL'
                            },
                            {
                                value: 'none',
                                label: 'None'
                            }
                        ],
                        defaultSelect: 'none'
                    }
                })
                .addDecoratorItem({
                    id: 'system.email.test',
                    component: 'testEmail'
                });
        }])
}

/*
 {
 ssl: 'SSL',
 tsl: 'TSL',
 none: 'None'
 }
 */
