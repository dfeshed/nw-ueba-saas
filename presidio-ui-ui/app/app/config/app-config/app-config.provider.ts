module Fortscale.appConfigProvider {

    import ConfigContainerProvider = Fortscale.appConfigProvider.configContainerProvider.ConfigContainerProvider;
    import ConfigItemProvider = Fortscale.appConfigProvider.configItemProvider.ConfigItemProvider;
    import ConfigItem = Fortscale.appConfigProvider.configItemProvider.ConfigItem;
    import IConfigContainerData = Fortscale.appConfigProvider.configContainerProvider.IConfigContainerData;
    import IConfigItemData = Fortscale.appConfigProvider.configItemProvider.IConfigItemData;
    import IConfigContainer = Fortscale.appConfigProvider.configContainerProvider.IConfigContainer;
    import ConfigContainer = Fortscale.appConfigProvider.configContainerProvider.ConfigContainer;
    import List = _.List;


    export interface IAppConfigProvider {
        ConfigContainerProvider: ConfigContainerProvider;
        ConfigItemProvider: ConfigItemProvider;
        getConfigItem (configId:string): ConfigItem;
        getConfigItems (): ConfigItem[];
        getConfigContainer (containerId:string): ConfigContainer;
        getConfigContainers (containerId:string): ConfigContainer[];
        addConfigContainer (configContainerData:IConfigContainerData, isUnique?:boolean): AppConfigProvider;
        addConfigItem (configItemData:IConfigItemData, isUnique?:boolean): AppConfigProvider;
        changeDefaultName (newName:string): void;
        addFormatter (formatterId:string, formatterFn:Function, isUnique?:boolean): AppConfigProvider;
        getFormatter (formatterId:string): (value:any) => any;
        addValidator (validatorId:string, validatorFn:Function, isUnique?:boolean): AppConfigProvider;
        getValidator (validatorId:string): (value:any) => boolean;

    }

    export interface IKeyValueConfig {
        key: string,
        value: any
    }

    export interface IAppConfigService {
        getConfigItem (configId:string, _doNotValidate?:boolean):ConfigItem;
        getDerivedConfigItem (configId:string, _doNotValidate?:boolean):ConfigItem;
        getConfigValue (stateName:string, propName:string):any;
        getConfigItemsByContainer (containerId:string):ConfigItem[];
        getConfigNodesTree ():any;
        getConfigContainer (configContainerId:string):ConfigContainer;
        isParentOfConfigContainer (currentContainerId:string, targetContainerId:string):boolean;
        getAffectedConfigItems (configId:string):ConfigItem[];
        isConfigItem (obj:any):boolean;
        duplicateConfigItem (configItem:ConfigItem):ConfigItem;
        canTargetDeriveFromContainer (targetConfigId:string, containerId:string):boolean;
        getFormatter (formatterId): (value:any) => any;
        getValidator (validatorId): (value:any) => boolean;
        digestRemoteConfig (remoteConfigList:IKeyValueConfig[]):void;
        formatConfigItem (keyValueConfig:IKeyValueConfig): any
        validateConfigItem (keyValueConfig:IKeyValueConfig):boolean;
        updateConfigItems (configItemsList:IKeyValueConfig[]):ng.IPromise<void>;
        _init ():void;
    }

    'use strict';

    export const IS_OPTIONAL = true;
    export const IS_NOT_OPTIONAL = false;
    export const CAN_NOT_BE_EMPTY = false;

    export let DO_NOT_VALIDATE = true;

    let DEFAULT_CONFIG_NAME = 'default';
    export let NAMESPACE_DELIMITER = '.';

    let assert;

    class AppConfigService implements IAppConfigService {

        constructor (public provider:AppConfigProvider, public $q:ng.IQService, public assert:any,
            public remoteAppConfig:any) {
        }

        /**
         * Validates configId param
         *
         * @param {string} configId
         * @param {string} methodName
         *
         * @private
         */
        private _validateConfigId (configId, methodName):void {
            this.assert.isString(configId, 'configId', 'appConfig: ' + methodName + ': ', IS_NOT_OPTIONAL,
                CAN_NOT_BE_EMPTY);
            this.assert(configId.split(NAMESPACE_DELIMITER).length >= 2,
                'appConfig: ' + methodName + ': configId should be a namespace with at least two nodes; ' +
                'configId: ' + configId, RangeError);
        }


        /**
         * Takes a key and traverses up (recursively) to see if any of the parent config containers exist,
         * and if so if any of them specify allowUpsert
         * @param {string} key
         * @returns {boolean}
         * @private
         */
        private _shouldUpsert (key):boolean {
            let namespace = key.split(NAMESPACE_DELIMITER);
            if (namespace.length > 1) {
                namespace.pop();
                let configContainer = this.getConfigContainer(namespace.join(NAMESPACE_DELIMITER));
                if (configContainer) {
                    return !!configContainer.allowUpsert;
                } else {
                    return this._shouldUpsert(namespace.join(NAMESPACE_DELIMITER));
                }
            } else {
                return false;
            }
        }


        /**
         * Builds recursively the missing configContainers al the way to the existing configContainer.
         * If build fails, false is returned. If success the true is returned.
         *
         * @param {string} key
         * @returns {boolean}
         * @private
         */
        private _buildContainerPath (key:string):boolean {
            let namespace = key.split(NAMESPACE_DELIMITER);
            if (namespace.length > 1) {
                // try and get container
                namespace.pop();
                let configContainer = this.getConfigContainer(namespace.join(NAMESPACE_DELIMITER));
                if (configContainer) {
                    return true;

                    // if no container build the parent then build the current
                } else {

                    //build the parent
                    if (this._buildContainerPath(namespace.join(NAMESPACE_DELIMITER))) {
                        //get the parent
                        let parentId = namespace.slice(0, namespace.length - 1).join(NAMESPACE_DELIMITER);
                        let parent = this.getConfigContainer(parentId);
                        // build the current
                        this.provider.addConfigContainer({
                            id: namespace.join(NAMESPACE_DELIMITER),
                            displayName: namespace.pop(),
                            allowUpsert: true,
                            configurable: parent.configurable
                        });
                        return true;

                    } else {
                        // if could not build the parent
                        return false;
                    }

                }

            } else {
                return false;
            }
        }


        /**
         * Takes a remote config item and inserts it into the config items.
         *
         * @param {{key: string, value: string, type: string=}} remoteConfigItem
         * @private
         */
        private _upsertRemoteConfigItem (remoteConfigItem:{key: string, value: string, type?: string}):void {
            let messageName = remoteConfigItem.key.split(NAMESPACE_DELIMITER).pop();
            let newConfigItem = this.provider.ConfigItemProvider.createItem({
                id: remoteConfigItem.key,
                displayName: messageName,
                value: remoteConfigItem.value,
                type: remoteConfigItem.type || "string",
                validators: ['required']
            });
            this.provider.addConfigItem(newConfigItem);
        }

        /**
         * Returns configItem by id
         *
         * @param {string} configId
         * @param {boolean=} _doNotValidate
         * @returns {*|null}
         */
        getConfigItem (configId:string, _doNotValidate?:boolean):ConfigItem {

            // Validations
            if (!_doNotValidate) {
                this._validateConfigId(configId, 'getDerivedConfigItem');
            }

            // Get the configItem
            return this.provider.getConfigItem(configId);
        };

        /**
         * Traverses the config node tree to find the closest node item.
         *
         * @param {string} configId
         * @param {boolean=} _doNotValidate
         * @returns {ConfigItem|null}
         */
        getDerivedConfigItem (configId:string, _doNotValidate?:boolean):ConfigItem {

            // Validations
            if (!_doNotValidate) {
                this._validateConfigId(configId, 'getDerivedConfigItem');
            }

            // Get the configItem
            let configItem = this.provider.getConfigItem(configId);

            // If the value is not undefined and it's value property is not null or undefined return it's value.
            if (configItem !== undefined && configItem !== null && configItem.value !== undefined &&
                configItem.value !== null) {
                return configItem;
            }

            let propName = configId.split(NAMESPACE_DELIMITER).pop();

            if (configItem !== undefined && configItem !== null) {
                let container = this.provider.getConfigContainer(configItem.containerId);

                // If there's a parent, return parent's value recursively
                if (container.parent) {
                    return this.getDerivedConfigItem(container.parent + NAMESPACE_DELIMITER + propName,
                        DO_NOT_VALIDATE);
                }
            }

            // If stateName is not 'default' and: [configItem item is undefined or value is undefined or value is
            // null] then try and get the default value.
            if (configId.indexOf(DEFAULT_CONFIG_NAME + NAMESPACE_DELIMITER) !== 0 &&
                (configItem === undefined || configItem === null || configItem.value === undefined ||
                configItem.value === null)) {
                return this.getDerivedConfigItem(DEFAULT_CONFIG_NAME + NAMESPACE_DELIMITER + propName, DO_NOT_VALIDATE);
            }

            return null;

        };


        /**
         * Returns a config item's value. If configItem is not found, or its value is undefined or null, the parent
         * is accessed for the same property key. If no parent is found, the "default" config state is accessed for
         * the same property key. If no value is found on the parent, null is returned.
         *
         * @param {string} stateName
         * @param {string} propName
         * @returns {*}
         */
        getConfigValue (stateName:string, propName:string):any {
            // Validations
            this.assert.isString(stateName, 'stateName', 'appConfig: getConfigValue: ', IS_NOT_OPTIONAL,
                CAN_NOT_BE_EMPTY);
            this.assert.isString(propName, 'propName', 'appConfig: getConfigValue: ', IS_NOT_OPTIONAL,
                CAN_NOT_BE_EMPTY);

            // Get the config item
            let configValue = this.getDerivedConfigItem(stateName + NAMESPACE_DELIMITER + propName, DO_NOT_VALIDATE);

            // If configItem has a value that is different than null, return the value. If not return null.
            if (configValue !== null && configValue.value !== null && configValue.value !== undefined) {
                return configValue.value;
            }

            return null;
        };

        /**
         * Returns a list of configItems of a specific container.
         *
         * @param {string} containerId
         * @returns {Array}
         */
        getConfigItemsByContainer (containerId:string):ConfigItem[] {

            // Validations
            this.assert.isString(containerId, 'stateName', 'appConfig: getConfigItemsByContainer: ', IS_NOT_OPTIONAL,
                CAN_NOT_BE_EMPTY);

            // Iterate through config keys, and find all configItems where the parent equals the stateName and are
            // not nodes
            return _.filter<ConfigItem>(this.provider.getConfigItems(),
                (configItem:ConfigItem) => configItem.containerId === containerId)
        };

        /**
         * Returns an object that represents all config nodes as a tree. each node has a 'nodes' object that holds
         * all child nodes
         *
         * @returns {object}
         */
        getConfigNodesTree ():any {

            let configNodeTree = {nodes: {}};
            _.each(this.provider.getConfigContainers(), (configContainer:IConfigContainer) => {

                // place on nodeTree by breaking to namespace
                let nameSpaceNodes:string[] = configContainer.id.split(NAMESPACE_DELIMITER);
                let traversed = configNodeTree.nodes;

                // Create entire namespace if it doesn't exist
                _.each(nameSpaceNodes, (nameSpaceNode:string, index:number) => {

                    // build or traverse nodes as long as its not the last node
                    if (index < nameSpaceNodes.length - 1) {
                        traversed[nameSpaceNode] = traversed[nameSpaceNode] || {nodes: {}};
                        traversed = traversed[nameSpaceNode].nodes;
                    } else {
                        // On the last node place the config container on the last created 'nodes'
                        traversed[nameSpaceNode] = _.merge({nodes: {}}, configContainer);
                    }
                });

            });

            return configNodeTree;
        };

        /**
         * Returns a ConfigContainer by a configContainerId
         *
         * @param {string} configContainerId
         * @returns {ConfigContainer|null}
         */
        getConfigContainer (configContainerId:string):ConfigContainer {
            // Validations
            this.assert.isString(configContainerId, 'configContainerId', 'appConfig: getConfigContainer: ',
                IS_NOT_OPTIONAL, CAN_NOT_BE_EMPTY);

            return this.provider.getConfigContainer(configContainerId);
        }

        /**
         * Checks if current container (by id) is a maternal relative of target container (by id)
         *
         * @param {string} currentContainerId
         * @param {string} targetContainerId
         * @returns {boolean}
         */
        isParentOfConfigContainer (currentContainerId:string, targetContainerId:string):boolean {
            // Validations
            this.assert.isString(currentContainerId, 'currentContainerId', 'appConfig: isParentOfConfigContainer: ',
                IS_NOT_OPTIONAL, CAN_NOT_BE_EMPTY);
            // Validations
            this.assert.isString(targetContainerId, 'targetContainerId', 'appConfig: isParentOfConfigContainer: ',
                IS_NOT_OPTIONAL, CAN_NOT_BE_EMPTY);

            // Get config container
            let targetConfigContainer = this.getConfigContainer(targetContainerId);

            // If no config container was found, return false
            if (targetConfigContainer === null) {
                return false;
            }

            // If configContainer has a parent, and the parent points to currentContainerId return true
            if (targetConfigContainer.parent && targetConfigContainer.parent === currentContainerId) {
                return true;
            }

            // If configContainer has a parent, and the parent does not point to currentContainerId then run the
            // function recursively with the parent
            if (targetConfigContainer.parent) {
                return this.isParentOfConfigContainer(currentContainerId, targetConfigContainer.parent);
            }

            // If configContainer has no parent then return false
            return false;
        }

        /**
         * Takes a config id and returns a list of all possible affected config items
         *
         * @param {string} configId
         * @returns {Array<ConfigItem>}
         */
        getAffectedConfigItems (configId:string):ConfigItem[] {

            // Validations
            this._validateConfigId(configId, 'getAffectedConfigItems');

            // get config item
            let configItem:ConfigItem = this.getConfigItem(configId, DO_NOT_VALIDATE);

            // get a list of possible affected items (use last node of configId)
            let propName = configId.split(NAMESPACE_DELIMITER).pop();
            let propNameRegExp = new RegExp(propName + '$');
            let possibleAffectedConfigItems = _.filter<ConfigItem>(this.provider.getConfigItems(),
                (configItem) => propNameRegExp.test(configItem.id));

            // filter list for configItem's container is a parent of list config item's container. return list.
            return _.filter(possibleAffectedConfigItems, (paConfigItem:ConfigItem) => {
                // DEFAULT_CONFIG_NAME is excepted because all items are affected by default
                return (configItem.containerId === DEFAULT_CONFIG_NAME &&
                    paConfigItem.containerId !== DEFAULT_CONFIG_NAME) ||
                    this.isParentOfConfigContainer(configItem.containerId, paConfigItem.containerId);
            });
        }

        /**
         * Verifies an object is an instance of ConfigItem
         *
         * @param obj
         * @returns {boolean}
         */
        isConfigItem (obj:any):boolean {
            return (obj instanceof ConfigItem);
        }

        /**
         * Duplicates a ConfigItem. This is done so original config items will not be affected.
         *
         * @param {ConfigItem} configItem
         * @returns {ConfigItem}
         */
        duplicateConfigItem (configItem:ConfigItem):ConfigItem {
            this.assert(this.isConfigItem(configItem),
                'appConfig: duplicateConfigItem: provided configItem must be an instance of ConfigItem', TypeError);

            return this.provider.ConfigItemProvider.createItem(_.merge({}, configItem));
        }

        /**
         * This method checks if a target config item can inherit from a specific config container.
         *
         * @param {string} targetConfigId
         * @param {string} containerId
         * @returns {boolean}
         */
        canTargetDeriveFromContainer (targetConfigId:string, containerId:string):boolean {

            // Get the config item
            let configItem:ConfigItem = this.provider.getConfigItem(targetConfigId);
            if (!configItem) {
                return false;
            }

            // Get the prop
            let prop = targetConfigId.split(NAMESPACE_DELIMITER).pop();

            // Check if target's container is a child of containerId and target's container is not default return
            // false
            if (!this.isParentOfConfigContainer(containerId, configItem.containerId) &&
                containerId !== DEFAULT_CONFIG_NAME) {
                return false;
            }

            // Travers up to targetConfigId (not including) and check if it has value
            // If value exists before the parent, then return false
            let cursorConfigContainer, cursorValue;
            cursorValue = null;
            cursorConfigContainer = this.getConfigContainer(configItem.containerId);

            while (cursorValue === null) {
                cursorValue = (this.provider.getConfigItem(cursorConfigContainer.id + NAMESPACE_DELIMITER + prop) ||
                {value: null}).value;

                if (cursorValue !== null) {
                    return false;
                }

                if (!cursorConfigContainer.parent || cursorConfigContainer.parent === containerId) {
                    return true;
                }

                cursorConfigContainer = this.getConfigContainer(cursorConfigContainer.parent);

            }

            return true;

        }

        /**
         * Returns a formatter if on exists. If it does not, it returns null
         *
         * @param formatterId
         * @returns {function|null}
         */
        getFormatter (formatterId):(value:any) => any {
            return this.provider.getFormatter(formatterId) || null;
        }

        /**
         * Returns a validator if on exists. If it does not, it returns null
         *
         * @param validatorId
         * @returns {function|null}
         */
        getValidator (validatorId):(value:any) => boolean {
            return this.provider.getValidator(validatorId) || null;
        }

        /**
         * Takes a list of objects, and updates the local config. Remote config must relate to a local config. If
         * it does not, than remote config is ignored. If validator exists for the type, and validation fails. The
         * remote config will be ignored.
         *
         * @param {Array<{key: string, value: string}>} remoteConfigList
         */
        digestRemoteConfig (remoteConfigList:IKeyValueConfig[]):void {
            _.each(remoteConfigList, (remoteConfigItem:IKeyValueConfig) => {

                let configItem = this.getConfigItem(remoteConfigItem.key);
                let finalValue;

                if (configItem) {
                    finalValue = this.formatConfigItem(remoteConfigItem);
                    // Validate value based on type. If valid or no validator, than set value to configItem.
                    if (this.validateConfigItem({key: remoteConfigItem.key, value: finalValue})) {
                        configItem.value = finalValue;
                    }

                    // Upsert if container's allowUpsert is true
                } else {
                    // Check if any parent container allows upsert
                    if (this._shouldUpsert(remoteConfigItem.key)) {
                        // build the path up to the parent container
                        if (this._buildContainerPath(remoteConfigItem.key)) {
                            // insert the remote config item
                            this._upsertRemoteConfigItem(remoteConfigItem);
                        } else {
                            console.warn('Could not upsert remote config item.' + remoteConfigItem.key + ': ' +
                                remoteConfigItem.value);
                        }
                    }
                }
            });
        }

        /**
         * Validates a config item based on its validators
         *
         * @param {IKeyValueConfig} keyValueConfig
         * @returns {boolean}
         */
        validateConfigItem (keyValueConfig:IKeyValueConfig):boolean {
            let configItem = this.getConfigItem(keyValueConfig.key);
            this.assert.isObject(configItem, 'configItem', 'validateConfigItem: ');

            let validators = _.map(configItem.validators, validatorName => this.getValidator(validatorName));
            return _.every(validators, validatorFn => {
                if (validatorFn === null) {
                    return true;
                }

                return validatorFn(keyValueConfig.value);
            });
        }

        /**
         * Takes a formatter and converts it to its proper format.
         *
         * @param {IKeyValueConfig} keyValueConfig
         * @returns {any}
         */
        formatConfigItem (keyValueConfig:IKeyValueConfig):any {
            let configItem = this.getConfigItem(keyValueConfig.key);
            this.assert.isObject(configItem, 'configItem', 'formatConfigItem: ');

            let formatterFn:(value:any) => any = this.getFormatter(configItem.formatter);
            if (formatterFn) {
                return formatterFn(keyValueConfig.value);
            }

            return keyValueConfig.value
        }


        /**
         * Accepts a list of objects, validates, updates the config items in the db, then locally.
         *
         * @param {Array<{key: string, value: string}>} configItemsList
         * @returns {*}
         */
        updateConfigItems (configItemsList:IKeyValueConfig[]):ng.IPromise<void> {

            // Validate list
            this.assert.isArray(configItemsList, 'configItemsList', 'appConfig: updateConfigItems: ', IS_NOT_OPTIONAL);
            _.each(configItemsList, (configItem, index) => {
                this.assert.isString(configItem.key, 'key',
                    'appConfig: updateConfigItems: configItemsList: item ' + index + ': ', IS_NOT_OPTIONAL,
                    CAN_NOT_BE_EMPTY);

                this.assert(this.getConfigItem(configItem.key) !== undefined,
                    'appConfig: updateConfigItems: configItemsList: item ' + index +
                    ': Item\'s key does not point to a valid config item.', ReferenceError);
            });

            let clonedList:IKeyValueConfig[] = _.cloneDeep<IKeyValueConfig[]>(configItemsList);
            return this.$q((resolve, reject) => {
                    _.each(clonedList, (configItem:IKeyValueConfig) => {
                        // format output
                        configItem.value = this.formatConfigItem(configItem);
                        // validate
                        if (!this.validateConfigItem(configItem)) {
                            return reject(
                                new RangeError(`Trying to update a config value with an invalid value. key: ${configItem.key}  -  value: ${configItem.value}`));
                        }

                        resolve();

                    });
                })
                .then(() => {
                    return this.remoteAppConfig.updateConfigItems(clonedList)
                })
                .then(() => this.digestRemoteConfig(clonedList));
        }

        /**
         * Init function. Digests remote config.
         *
         * @private
         */
        _init ():void {
            this.digestRemoteConfig(this.remoteAppConfig.getRemoteConfigList());
        }
    }


    class AppConfigProvider implements ng.IServiceProvider, IAppConfigProvider {
        static $inject = ['assertConstant', 'Fortscale.appConfig.ConfigContainerProvider',
            'Fortscale.appConfig.ConfigItemProvider'];

        private _configContainers:any;
        private _configItems:any;
        private _formatters:any;
        private _validators:any;

        constructor (public assert, public ConfigContainerProvider:ConfigContainerProvider,
            public ConfigItemProvider:ConfigItemProvider) {

            // Initialize privates
            this._configContainers = {};
            this._configItems = {};
            this._formatters = {};
            this._validators = {};
        }

        private appConfigFactory ($q, assert, remoteAppConfig) {
            let service = new AppConfigService(this, $q, assert, remoteAppConfig);
            service._init();
            return service;
        }

        $get = ['$q', 'assert', 'remoteAppConfig', this.appConfigFactory.bind(this)];

        getConfigItem (configId:string):ConfigItem {
            return this._configItems[configId] || null;
        }

        getConfigItems ():ConfigItem[] {
            return _.values<ConfigItem>(this._configItems);
        }

        getConfigContainer (containerId:string):ConfigContainer {
            return this._configContainers[containerId] || null;
        }

        getConfigContainers ():ConfigContainer[] {
            return _.values<ConfigContainer>(this._configContainers);
        }

        /**
         * Adds a config container
         *
         * @param {IConfigContainerData} configContainerData
         * @param {boolean} isUnique
         * @returns {AppConfigProvider}
         */
        addConfigContainer (configContainerData:IConfigContainerData, isUnique:boolean = true):AppConfigProvider {
            // Validations
            this.assert.isObject(configContainerData, 'configContainerData', 'AppConfigProvider: addConfigContainer: ',
                IS_NOT_OPTIONAL);

            // Create new ConfigItem instance
            let configContainer = this.ConfigContainerProvider.createContainer(configContainerData);

            //Validations: make sure that if isUnique is true, the config item does not override an existing one.
            this.assert((isUnique && this._configContainers[configContainerData.id] === undefined),
                'appConfig: addConfigContainer: When isUnique is true, config container id must be unique and it is ' +
                'not; id: ' + configContainerData.id, RangeError);

            // Place config item on the config items object
            this._configContainers[configContainerData.id] = configContainer;

            // Return the config item instance from configItems
            return this;
        }

        /**
         * Adds a config item
         * @param {IConfigItemData} configItemData
         * @param isUnique
         * @returns {AppConfigProvider}
         */
        addConfigItem (configItemData:IConfigItemData, isUnique:boolean = true):AppConfigProvider {

            // Validations
            this.assert.isObject(configItemData, 'configItemData', 'AppConfigProvider: addConfigItem: ',
                IS_NOT_OPTIONAL);

            // Create new ConfigItem instance
            let configItem = this.ConfigItemProvider.createItem(configItemData);

            // If isUnique make sure its not pointing to an existing object
            this.assert((isUnique && this._configItems[configItem.id] === undefined),
                'AppConfigProvider: addConfigItem: When isUnique is true, config item id must be unique and it is ' +
                'not; id: ' + configItemData.id, RangeError);

            // Make sure the config item is pointing to an existing container
            this.assert(!!this._configContainers[configItem.containerId],
                'AppConfigProvider: addConfigItem: config item containerId must point to an existing container; id: ' +
                configItemData.id, ReferenceError);

            // Place config item on the config items object
            this._configItems[configItem.id] = configItem;

            // Return the config item instance from configItems
            return this;
        }

        /**
         * Changes the name of the default container in the config phase.
         *
         * @param {string} newName
         */
        changeDefaultName (newName:string):void {

            // Validations
            this.assert.isString(newName, 'newName', 'appConfigProvider: changeDefaultName: ', IS_NOT_OPTIONAL,
                CAN_NOT_BE_EMPTY);

            DEFAULT_CONFIG_NAME = newName;
        };

        /**
         * Adds a formatter. Formatter will be consumed when config item type equals formatter id.
         *
         * @param {string} formatterId
         * @param {function} formatterFn
         * @param {boolean=} isUnique
         * @returns {AppConfigProvider}
         */
        addFormatter (formatterId:string, formatterFn:Function, isUnique:boolean = true):AppConfigProvider {

            // Validations
            this.assert.isString(formatterId, 'formatterId', 'appConfigProvider: addFormatter: ', IS_NOT_OPTIONAL,
                CAN_NOT_BE_EMPTY);
            this.assert.isFunction(formatterFn, 'formatterFn', 'appConfigProvider: addFormatter: ', IS_NOT_OPTIONAL);

            // If isUnique make sure its not pointing to an existing object
            this.assert((isUnique && this._formatters[formatterId] === undefined),
                'appConfigProvider: addFormatter: When isUnique is true, formatter must be unique and it is ' +
                'not; formatterId: ' + formatterId, RangeError);

            this._formatters[formatterId] = formatterFn;

            return this;
        };

        /**
         * Returns a formatter by id
         *
         * @param formatterId
         * @returns {*}
         */
        getFormatter (formatterId:string):(value:any) => any {
            return this._formatters[formatterId];
        }


        /**
         * Adds a validator. Validator will be consumed when config item type equals validator id.
         *
         * @param {string} validatorId
         * @param {function} validatorFn
         * @param {boolean=} isUnique
         * @returns {AppConfigProvider}
         */
        addValidator (validatorId:string, validatorFn:Function, isUnique:boolean = true):AppConfigProvider {

            // Validations
            this.assert.isString(validatorId, 'validatorId', 'appConfigProvider: addValidator: ', IS_NOT_OPTIONAL,
                CAN_NOT_BE_EMPTY);
            this.assert.isFunction(validatorFn, 'validatorFn', 'appConfigProvider: addValidator: ', IS_NOT_OPTIONAL);

            // If isUnique make sure its not pointing to an existing object
            this.assert((isUnique && this._validators[validatorId] === undefined),
                'appConfigProvider: addValidator: When isUnique is true, validator must be unique and it is ' +
                'not; validatorId: ' + validatorId, RangeError);

            this._validators[validatorId] = validatorFn;

            return this;
        };

        /**
         * Returns a validator by id
         *
         * @param validatorId
         * @returns {*}
         */
        getValidator (validatorId:string):(value:any) => boolean {
            return this._validators[validatorId];
        }

    }


    function containerIdToDisplayNameFilter (appConfig) {
        return function (containerId) {
            return (appConfig.getConfigContainer(containerId) || {displayName: 'N/A'}).displayName;
        };
    }

    function containerIdToDisplayCrumb (appConfig) {
        return function fn (containerId) {
            if (containerId) {
                let configContainer = appConfig.getConfigContainer(containerId);
                return (configContainer.parent ? fn(configContainer.parent) + ': ' : '') + configContainer.displayName;
            }

            return '';
        };
    }


    angular.module('Fortscale.appConfig',
        ['Fortscale.shared.services.assert', 'Fortscale.remoteAppConfig', 'Fortscale.appConfig.ConfigContainer',
            'Fortscale.appConfig.ConfigItem'])
        .provider('appConfig', AppConfigProvider)
        .filter('containerIdToDisplayName', ['appConfig', containerIdToDisplayNameFilter])
        .filter('containerIdToDisplayCrumb', ['appConfig', containerIdToDisplayCrumb]);

}



