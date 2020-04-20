module Fortscale.layouts.configuration.decorator {
    'use strict';

    export interface IFormDecoratorData {
        containerId: string
        showComplex?: boolean
    }

    export interface IFormDecorator {
        containerId: string
        showComplex: boolean
    }

    export interface IConfigItemDecoratorResolve {
        [key: string]: () => ng.IPromise<any>|any
    }

    export interface IConfigItemDecoratorData {
        id: string
        containerId?: string
        description?: string
        displayName?: string
        showComplex?: boolean
        component?: string
        templateUrl?: string,
        replace?: boolean
        resolve?: IConfigItemDecoratorResolve
        config?: {},
        data?: {},
        sort?: number,
        classNames?: string,
        showLoader?: boolean,
    }

    export interface IConfigItemDecorator {
        id: string
        containerId: string
        description?: string
        displayName?: string
        showComplex: boolean
        component: string
        templateUrl: string
        replace: boolean
        resolve: IConfigItemDecoratorResolve
        config: any,
        data: any,
        sort: number,
        classNames: string,
        showLoader: boolean,
        value?: any
    }

    export interface IConfigurationDecoratorService {
        addDecoratorForm (formDecoratorData:IFormDecoratorData): IConfigurationDecoratorService
        getDecoratorForm (containerId:string): IFormDecorator
        addDecoratorItem (configItemDecoratorData:IConfigItemDecoratorData): IConfigurationDecoratorService
        getDecoratorItem (id:string): IConfigItemDecorator
        getDecoratedItemsByContainerId (containerId:string):IConfigItemDecorator[]
    }

    class DecoratorService implements IConfigurationDecoratorService {
        static $inject = ['assert'];

        constructor (public assert:any) {

            // Initialize properties
            this._decoratorForms = new Map();
            this._decoratorItems = new Map();
            this._errMsg = 'Fortscale.layouts.configuration.DecoratorService: ';
        }

        private _decoratorForms:Map<string, IFormDecorator>;
        private _decoratorItems:Map<string, IConfigItemDecorator>;
        private _errMsg:string;

        /**
         * Adds a decorator form object
         * @param {IConfigurationDecoratorService} formDecoratorData
         * @returns {DecoratorService}
         */
        addDecoratorForm (formDecoratorData:IFormDecoratorData):IConfigurationDecoratorService {
            // Validations
            let errMsg = this._errMsg + 'addDecoratorForm: ';
            this.assert.isString(formDecoratorData.containerId, 'formDecoratorData.containerId', errMsg);

            // Set a new decorator form
            this._decoratorForms.set(formDecoratorData.containerId, {
                containerId: formDecoratorData.containerId,
                showComplex: _.isUndefined(formDecoratorData.showComplex) ? false : !!formDecoratorData.showComplex
            });

            return this;
        }

        /**
         * Derives a containerId from config item id by "popping" its last node
         *
         * @param {string} itemId
         * @returns {string}
         * @private
         */
        private _itemIdToFormId (itemId:string):string {
            let nodes = itemId.split('.');
            nodes.pop();
            return nodes.join('.');
        }

        /**
         * Returns a form decorator by a config item id
         *
         * @param {string} itemId
         * @returns {IFormDecorator}
         * @private
         */
        private _getFormByItem (itemId:string):IFormDecorator {
            return this.getDecoratorForm(this._itemIdToFormId(itemId));
        }

        private _isFormShowComplex (itemId:string):boolean {
            let formDecorator = this._getFormByItem(itemId);
            return formDecorator.showComplex;
        }

        /**
         * returns a decorator form object (or null)
         *
         * @param containerId
         * @returns {IFormDecorator|null}
         */
        getDecoratorForm (containerId:string):IFormDecorator {
            // Validations
            let errMsg = this._errMsg + 'getDecoratorForm: ';
            this.assert.isString(containerId, 'containerId', errMsg);

            return this._decoratorForms.get(containerId) || null;
        }

        /**
         * Adds a decorator item.
         *
         * @param {IConfigItemDecoratorData} configItemDecoratorData
         * @returns {DecoratorService}
         */
        addDecoratorItem (configItemDecoratorData:IConfigItemDecoratorData):IConfigurationDecoratorService {
            // Validations
            let errMsg = this._errMsg + 'addDecoratorItem: ';
            this.assert.isString(configItemDecoratorData.id, 'configItemDecoratorData.id', errMsg);
            this.assert(configItemDecoratorData.id.split(".").length > 1, errMsg +
                'Config item id have at least two nodes in its namespace, i.e. string.string . Current name: ' +
                configItemDecoratorData.id);
            this.assert(this._getFormByItem(configItemDecoratorData.id), errMsg +
                'Adding config item decorator without first adding a container form decorator is not allowed. id: ' +
                configItemDecoratorData.id);
            this.assert.isString(configItemDecoratorData.component, 'configItemDecoratorData.id', errMsg,
                true);
            this.assert.isObject(configItemDecoratorData.resolve, 'configItemDecoratorData.resolve', errMsg, true);
            this.assert.isObject(configItemDecoratorData.data, 'configItemDecoratorData.data', errMsg, true);

            let id = configItemDecoratorData.id;
            let containerId = this._itemIdToFormId(id);
            let showComplex = _.isUndefined(configItemDecoratorData.showComplex) ?
                this._isFormShowComplex(id) : !!configItemDecoratorData.showComplex;
            let displayName = configItemDecoratorData.displayName;
            let description = configItemDecoratorData.description;
            let component = configItemDecoratorData.component || null;
            let templateUrl = configItemDecoratorData.templateUrl || null;
            let replace = !!configItemDecoratorData.replace;
            let resolve = configItemDecoratorData.resolve || {};
            let config = configItemDecoratorData.config || {};
            let data = configItemDecoratorData.data || {};
            let sort = configItemDecoratorData.sort || 0;
            let classNames = configItemDecoratorData.classNames || '';
            let showLoader = _.isUndefined(configItemDecoratorData.showLoader) ? true :
                !!configItemDecoratorData.showLoader;


            let decoratorItem:IConfigItemDecorator = {
                id: id,
                containerId: containerId,
                displayName: displayName,
                description: description,
                showComplex: showComplex,
                component: component,
                templateUrl: templateUrl,
                replace: replace,
                resolve: resolve,
                config: config,
                data: data,
                sort: sort,
                classNames: classNames,
                showLoader: showLoader
            };


            if (!configItemDecoratorData.displayName) {
                delete decoratorItem.displayName;
            }

            if (!configItemDecoratorData.description) {
                delete decoratorItem.description;
            }

            this._decoratorItems.set(id, decoratorItem);


            return this;
        }

        /**
         * Returns a decorator item
         *
         * @param id
         * @returns {V|IConfigItemDecorator|null}
         */
        getDecoratorItem (id:string):IConfigItemDecorator {
            // Validations
            let errMsg = this._errMsg + 'getDecoratorItem: ';
            this.assert.isString(id, 'id', errMsg);

            return this._decoratorItems.get(id) || null;
        }

        getDecoratedItemsByContainerId (containerId:string):IConfigItemDecorator[] {
            // Validations
            let errMsg = this._errMsg + 'getDecoratorForm: ';
            this.assert.isString(containerId, 'containerId', errMsg);

            if (!this._decoratorItems.size) {
                return [];
            }

            let configItems:IConfigItemDecorator[] = [];
            let values = Array.from(this._decoratorItems.values());

            _.each(values, value => {
                if (value.containerId === containerId) {
                    configItems.push(value);
                }
            });

            return configItems;

        }

    }

    angular.module('Fortscale.layouts.configuration')
        .service('Fortscale.layouts.configuration.decoratorService', DecoratorService)
}
