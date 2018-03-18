module Fortscale.layouts.configuration.decorator {
    'use strict';

    angular.module('Fortscale.layouts.configuration')
        .run(['Fortscale.layouts.configuration.decoratorService', (decoratorService:IConfigurationDecoratorService) => {
            decoratorService
                .addDecoratorForm({
                    containerId: 'system.logEmail'
                })
                .addDecoratorItem({
                    id: 'system.logEmail.subscribers', 
                    component: 'usersList'
                });
        }])
}
