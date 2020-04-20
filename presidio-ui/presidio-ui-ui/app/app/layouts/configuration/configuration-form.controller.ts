module Fortscale.layouts.configuration.configurationFormController {

    import IConfigContainerData = Fortscale.appConfigProvider.configContainerProvider.IConfigContainerData;
    import IConfigItem = Fortscale.appConfigProvider.configItemProvider.IConfigItem;
    import IAppConfigService = Fortscale.appConfigProvider.IAppConfigService;
    import IStateService = angular.ui.IStateService;
    import IConfigurationFormService = Fortscale.layouts.configuration.configurationForm.IConfigurationFormService;
    import ConfigItem = Fortscale.appConfigProvider.configItemProvider.ConfigItem;
    import IConfigurationDecoratorService = Fortscale.layouts.configuration.decorator.IConfigurationDecoratorService;
    import IFormDecorator = Fortscale.layouts.configuration.decorator.IFormDecorator;
    import IConfigItemDecorator = Fortscale.layouts.configuration.decorator.IConfigItemDecorator;
    'use strict';

    export interface IConfigurationFormController {
        configModels: any[];
        loader: boolean;
        error: {display: boolean, title: string, description: string};
        configContainer:IConfigContainerData;
        configItems: IConfigItem[];
        submitNewConfig (): any;
    }


    class ConfigurationFormController implements IConfigurationFormController {
        static $inject = ['appConfig', '$scope', '$element', '$compile', '$state',
            'Fortscale.layouts.configuration.configurationFormService',
            'Fortscale.layouts.configuration.decoratorService'];

        constructor (public appConfig:IAppConfigService, public $scope:ng.IScope, public $element:JQuery,
            public $compile:ng.ICompileService, public $state:IStateService,
            public configurationFormService:IConfigurationFormService,
            public decoratorService:IConfigurationDecoratorService) {

            // Initialize properties
            this.configModels = [];
            this.loader = false;
            this.error = {
                display: false, title: '', description: ''
            };
            this.submitNewConfig = () => {
                this._submitNewConfig();
            };

            this._init();
        };

        configModels:any[];
        loader:boolean;
        error:{display: boolean, title: string, description: string};
        configContainer:IConfigContainerData;
        decoratedForm:IFormDecorator;
        configItems:any[];
        submitNewConfig:() => any;


        /**
         * Sets the config container and decorated form
         *
         * @private
         */
        _setConfigContainer ():void {
            this.configContainer = this.appConfig.getConfigContainer(this.$state.params['stateName']);
            this.decoratedForm = this.decoratorService.getDecoratorForm(this.$state.params['stateName']);
        }

        /**
         * returns a list of  IConfigItems relevant to the state
         * @returns {IConfigItem[]}
         * @private
         */
        _getConfigItemsByContainer (): IConfigItem[] {
            if (!this.configContainer) {
                return null;
            }

            // Make a list of config item duplications, that belong to this state
            let configItems = <IConfigItem[]>this.appConfig.getConfigItemsByContainer(this.configContainer.id);
            configItems = _.map(configItems,
                (configItem:IConfigItem) => <IConfigItem>this.appConfig.duplicateConfigItem(configItem));
            return configItems;
        }

        /**
         * Adds derived config item for each config item
         *
         * @returns {any[]}
         * @private
         */
        _populateDerivedConfigItems (): any[] {
            if (!this.configItems) {
                return null;
            }

            // Place the derived value config item on each config item. If no config item is found, or its pointing to
            // itself, derivedConfigItem property will not be set.
            return _.map(this.configItems, (configItem:any) => {
                var derivedConfigItem = this.appConfig.getDerivedConfigItem(configItem.id);
                if (derivedConfigItem && configItem.id !== derivedConfigItem.id) {
                    configItem.derivedConfigItem = _.merge({}, derivedConfigItem);
                }
                return configItem;
            });
        }

        /**
         * Merges decorated item into a config items object
         *
         * @param {{}} configItemsObject
         * @param {IConfigItemDecorator} decoratedItem
         * @private
         */
        _mergeDecoratedFormItem(configItemsObject: {[key:string]: IConfigItem}, decoratedItem: IConfigItemDecorator) {
            let item:any = configItemsObject[decoratedItem.id];
            item = item || {id: decoratedItem.id};
            _.merge(item, decoratedItem);

            configItemsObject[item.id] = item;
        }

        /**
         * Merges into config items any decorated items found in decorated form
         *
         * @returns {any[]}
         * @private
         */
        _mergeDecoratedFormItems ():any[] {
            if (!this.configItems || !this.configContainer) {
                return null;
            }

            // Merge in decorated items if there's a decorated form
            let decoratedForm = this.decoratorService.getDecoratorForm(this.configContainer.id);
            if (decoratedForm) {
                // Get decorated items
                let decoratedItems = this.decoratorService.getDecoratedItemsByContainerId(this.configContainer.id);
                // Convert configItems into an object to make merging in the items easy
                let configItemsObject = _.keyBy(this.configItems, 'id');
                // Iterate through decorated items and merge each one into a config item, or a new item.
                _.each(decoratedItems, decoratedItem => this._mergeDecoratedFormItem(configItemsObject, decoratedItem));

                // Convert configItemsObject back to array
                return _.values(configItemsObject);

            }

            return this.configItems;
        }

        /**
         * Adds missing properties to config items, like showComplex and sort
         *
         * @returns {any[]}
         * @private
         */
        _polyfillConfigItems ():any[] {
            if (!this.configItems) {
                return null;
            }
            return _.map(this.configItems, (configItem:any) => {
                if (this.decoratedForm) {
                    configItem.showComplex =
                        _.isUndefined(configItem.showComplex) ? this.decoratedForm.showComplex : configItem.showComplex;
                } else {
                    configItem.showComplex = true;
                }
                configItem.sort = _.isUndefined(configItem.sort) ? 0 : configItem.sort;
                return configItem;
            });
        };

        /**
         * Sorts config items by 'sort'
         *
         * @returns {any[]}
         * @private
         */
        _sortConfigItems ():any[] {
            if (!this.configItems) {
                return null;
            }

            return _.sortBy(this.configItems, 'sort');
        }

        /**
         * Gets the relevant config items. extracts derived config items. Merges in decorated items.
         *
         * @private
         */
        _setConfigItems ():void {

            this.configItems = this._getConfigItemsByContainer();
            this.configItems = this._populateDerivedConfigItems();
            this.configItems = this._mergeDecoratedFormItems();
            this.configItems = this._polyfillConfigItems();
            this.configItems = this._sortConfigItems();

        }

        _submitNewConfig ():void {

            var ctrl = this;

            ctrl.loader = true;

            var configItemsList = [];

            _.each(Object.keys(this.configModels), (configModelKey) => {
                var value = this.configModels[configModelKey];
                var configItem = this.appConfig.getConfigItem(configModelKey);
                if (configItem && configItem.value !== value) {
                    let item: any = {key: configModelKey, value: value};
                    if (configItem.meta) {
                        item.meta = configItem.meta;
                    }
                    configItemsList.push(item);
                }
            });

            if (configItemsList.length) {
                this.appConfig.updateConfigItems(configItemsList)
                    .then(function () {
                        ctrl.$state.go(ctrl.$state.current, {
                            stateName: ctrl.$state.params['stateName']
                        }, {reload: true});
                    })
                    .catch(function (err) {
                        ctrl.loader = false;
                        ctrl.error.display = true;
                        ctrl.error.title = 'Update Error';
                        ctrl.error.description = 'There was an unknown server error. Could not update configuration.';
                        console.error(err);
                    });
                // If no items need to be updated, just reload the state.
            } else {
                ctrl.$state.go(ctrl.$state.current, {
                    stateName: ctrl.$state.params['stateName']
                }, {reload: true});
            }
        }

        _init ():void {
            // determine flow

            // Custom form flow
            if (this.configurationFormService.isCustomForm(this.$state.params['stateName'])) {

                // when custom form, generate a logical config container
                this.configContainer = <IConfigContainerData>this.configurationFormService.generateConfigContainer(
                    this.$state.params['stateName']);


                this.configurationFormService.renderCustomPage(this.$state.params['stateName'], this.$scope)
                    .then(component => {
                        let customWrapper = this.$element.find('.form-pane--custom-form');
                        customWrapper.append(component);
                    })
                    .catch(err => {
                        console.error(`Failed to render custom form for state: ${this.$state.params['stateName']}`,
                            err);
                    });

                // Dynamic form flow
            } else {
                this._setConfigContainer();
                if (!this.configContainer) {
                    throw new RangeError('Trying to render a form that is not declared dynamically via ' +
                        'appConfig.provider, and not declared statically via configurationNavigation.service');
                }
                this._setConfigItems();
            }
        }
    }


    angular.module('Fortscale.layouts.configuration')
        .controller('ConfigurationFormController', ConfigurationFormController);
}
