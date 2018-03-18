module Fortscale.layouts.configuration.configurationForm {
    import IConfigurationNavigationService = Fortscale.layouts.configuration.configurationNavigation.IConfigurationNavigationService;
    import IConfigurationPageData = Fortscale.layouts.configuration.configurationNavigation.IConfigurationPageData;
    import IConfigContainerData = Fortscale.appConfigProvider.configContainerProvider.IConfigContainerData;

    export interface IConfigurationFormService {
        isCustomForm (containerId:string):boolean;
        generateConfigContainer (containerId:string):IConfigurationPageData;
        renderCustomPage (configurationPageId:string, $scope: ng.IScope):ng.IPromise<any>;
    }

    'use strict';

    class configurationFormService implements IConfigurationFormService{
        static $inject = ['assert', '$q', 'stringUtils', '$compile',
            'Fortscale.layouts.configuration.configurationNavigationService'];

        constructor (public assert, public $q, public stringUtils: any, public $compile: ng.ICompileService,
            public configurationNavigationService:IConfigurationNavigationService) {
        }

        private _errMsg = 'Fortscale.layouts.configuration.configurationFormService: ';

        private onRenderComplete (resolve, reject) {

        }

        /**
         * Renders a custom component/directive
         *
         * @param {string} componentName
         * @param {IScope} $scope
         * @param {IQResolveReject<JQuery>} resolve
         * @param {IQResolveReject<any>} reject
         * @private
         */
        private _renderCustomComponent (componentName:string, $scope: ng.IScope, resolve:ng.IQResolveReject<JQuery>,
            reject:ng.IQResolveReject<any>):void {
            try {
                let element = angular.element('<div class="configuration-form-component-wrapper"></div>');
                let componentSlug = this.stringUtils.toSlugCase(componentName);
                let component = angular.element(`<${componentSlug}></${componentSlug}>`);
                element.append(component);
                resolve(this.$compile(element)($scope));
            } catch (err) {
                reject(err);
            }
        }


        /**
         * Checks if a containerId refers to a custom form or a predefined dynamic form
         *
         * @param containerId
         * @returns {any}
         */
        isCustomForm (containerId:string):boolean {
            // Validations
            this.assert.isString(containerId, 'containerId', `${this._errMsg}isCustomForm: `);

            // get configuration page
            let configurationPage:IConfigurationPageData = this.configurationNavigationService.getConfigurationPage(
                containerId);

            // if configuration page exists return configurationPage.customPage
            if (configurationPage) {
                return !!configurationPage.customPage;
            }

            return null;

        }

        generateConfigContainer (containerId:string):IConfigurationPageData {
            let configurationPage = this.configurationNavigationService.getConfigurationPage(containerId);
            if (configurationPage) {
                return {
                    id: configurationPage.id,
                    displayName: configurationPage.displayName,
                    description: configurationPage.description,
                    doNotShowHeader: configurationPage.doNotShowHeader,
                    formClassNames: configurationPage.formClassNames
                };
            }
            return null;
        }

        renderCustomPage (configurationPageId:string, $scope: ng.IScope):ng.IPromise<any> {

            // Validations
            let errMsg = this._errMsg + 'renderCustomPage: ';
            this.assert.isString(configurationPageId, 'configurationPageId', errMsg);
            this.assert.isObject($scope, '$scope', errMsg);

            // Create promise and return
            return this.$q((resolve, reject) => {
                // get configuration page
                let configurationPage = this.configurationNavigationService.getConfigurationPage(configurationPageId);
                if (!configurationPage) {
                    return reject(`Configuration page for id ${configurationPageId} was not found.`);
                }

                // if it has 'component' then render component
                if (configurationPage.component) {
                    return this._renderCustomComponent(configurationPage.component, $scope, resolve, reject);
                }

            });
        };
    }

    angular.module('Fortscale.layouts.configuration')
        .service('Fortscale.layouts.configuration.configurationFormService', configurationFormService)

}
