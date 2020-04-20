module Fortscale.appConfigProvider.configItemProvider {


    export interface IConfigItemData {
        id: string;
        containerId?: string;
        description?: string;
        displayName?: string;
        type?: string;
        validators?: string[];
        formatter?: string;
        value?: any;
        _originalValue?: any;
        meta?: any
    }

    export interface IConfigItem {
        id: string;
        containerId: string;
        description: string;
        displayName: string;
        type: string;
        validators: string[];
        formatter: string;
        value: any;
        _originalValue: any;
        meta: any
    }

    export class ConfigItem implements IConfigItem {
        constructor (public id:string, public containerId:string, public description:string, public displayName:string,
            public type:string, public validators:string[], public formatter:string, public value:any,
            public _originalValue:any, public meta: any) {
        }
    }

    export class ConfigItemProvider implements ng.IServiceProvider {
        static $inject = ['assertConstant'];

        constructor (public assert) {
        }

        $get = [() => {
            throw new Error('ConfigItemProvider works only as a provider and not as a service');
        }];

        private _getContainerId (containerId:string, id:string) {

            if (!containerId) {
                var nameSpaceNodes = id.split(NAMESPACE_DELIMITER);

                // Make sure there are at least two nodes
                this.assert((nameSpaceNodes.length >= 2),
                    'appConfig: new ConfigItem: configObj: id must be a namespace with at least one dot. example: ' +
                    'someConfig.configId. id: ' + id, RangeError);

                nameSpaceNodes.pop();
                containerId = nameSpaceNodes.join(NAMESPACE_DELIMITER);
            }

            return containerId;
        };

        createItem (configItemData:IConfigItemData) {
            // Validations
            this.assert.isString(configItemData.id, 'id', 'appConfig: new ConfigItem: configObj: ', IS_NOT_OPTIONAL,
                CAN_NOT_BE_EMPTY);
            this.assert.isString(configItemData.containerId, 'containerId', 'appConfig: new ConfigItem: configObj: ',
                IS_OPTIONAL, CAN_NOT_BE_EMPTY);
            this.assert.isString(configItemData.displayName, 'displayName', 'appConfig: new ConfigItem: configObj: ',
                IS_NOT_OPTIONAL, CAN_NOT_BE_EMPTY);
            this.assert.isString(configItemData.description, 'description', 'appConfig: new ConfigItem: configObj: ',
                IS_OPTIONAL, CAN_NOT_BE_EMPTY);


            // Assignments
            let id = configItemData.id;
            let containerId = this._getContainerId(configItemData.containerId, id);
            let description = configItemData.description;
            let displayName = configItemData.displayName;
            let type = configItemData.type || null;
            let validators = configItemData.validators ? configItemData.validators :
                [configItemData.type];
            let formatter = configItemData.formatter || configItemData.type || null;

            let value = configItemData.value === undefined ? null : configItemData.value;
            let _originalValue = configItemData.value;
            let meta = configItemData.meta || null;

            return new ConfigItem(id, containerId, description, displayName, type, validators, formatter, value, _originalValue, meta);
        }
    }


    angular.module('Fortscale.appConfig.ConfigItem', ['Fortscale.shared.services.assert'])
        .provider('Fortscale.appConfig.ConfigItem', ConfigItemProvider);
}
