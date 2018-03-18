module Fortscale.layouts.configuration {
    import IConfigurationNavigationService =
        Fortscale.layouts.configuration.configurationNavigation.IConfigurationNavigationService;
    'use strict';

    class ConfigurationContoller {
        static $inject = ['$scope', '$element', 'page',
            'Fortscale.layouts.configuration.configurationNavigationService',
            'VERSION_NUMBER', 'VERSION_YEAR', 'VERSION_COMPANY'
        ];

        mainState:any;
        version: {number:string, year: string, company: string};

        constructor (public $scope, public $element, public page:any,
            public configurationNavigationService:IConfigurationNavigationService, VERSION_NUMBER:string,
            VERSION_YEAR:string, VERSION_COMPANY:string) {
            this.mainState = {};
            this.version = {
                number: VERSION_NUMBER,
                year: VERSION_YEAR,
                company: VERSION_COMPANY
            };
            this._init();
        }

        private _init () {
            // this._setupSplitterSetting();
            this.page.setPageTitle('System Configuration');
            var navContainer = this.$element.find(NAV_BAR_ELEMENT_SELECTOR);//configuration.scss
            this.configurationNavigationService.renderNavigation(navContainer, this.$scope);
        }
    }

    angular.module('Fortscale.layouts.configuration')
        .controller('ConfigurationController', ConfigurationContoller);
}
