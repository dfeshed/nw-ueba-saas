module Fortscale.appConfigProvider.configContainerProvider {

    export interface IConfigContainerData {
        id: string;
        displayName: string;
        description?: string;
        note?: string;
        parent?: string;
        configurable?: boolean;
        allowUpsert?: boolean;
        doNotShowHeader?: boolean;
    }

    export interface IConfigContainer {
        id: string;
        displayName: string;
        description: string;
        note: string;
        parent: string;
        configurable: boolean;
        allowUpsert: boolean;
    }

    export class ConfigContainer implements IConfigContainer {
        constructor (public id:string, public displayName:string, public description:string, public note:string, public parent:string,
            public configurable:boolean, public allowUpsert:boolean) {
        }


    }

    export class ConfigContainerProvider implements ng.IServiceProvider {

        static $inject = ['assertConstant'];

        constructor (public assert) {
        }

        $get = [() => {
            throw new Error('ConfigContainerProvider works only as a provider and not as a service');
        }];

        /**
         * factory method for ConfigContainer
         * @param configContainerData
         * @returns {Fortscale.appConfig.ConfigContainer.ConfigContainer}
         */
        createContainer (configContainerData:IConfigContainerData):ConfigContainer {
            // Validations
            this.assert.isString(configContainerData.id, 'id', 'appConfig: new ConfigItem: configObj: ',
                IS_NOT_OPTIONAL, CAN_NOT_BE_EMPTY);
            this.assert.isString(configContainerData.displayName, 'displayName',
                'appConfig: new ConfigItem: configObj: ', IS_NOT_OPTIONAL, CAN_NOT_BE_EMPTY);
            this.assert.isString(configContainerData.description, 'description',
                'appConfig: new ConfigItem: configObj: ', IS_OPTIONAL, CAN_NOT_BE_EMPTY);
            this.assert.isString(configContainerData.parent, 'parent', 'appConfig: new ConfigItem: configObj: ',
                IS_OPTIONAL, CAN_NOT_BE_EMPTY);

            let id = configContainerData.id;
            let displayName = configContainerData.displayName;
            let description = configContainerData.description;
            let note = configContainerData.note;
            let parent = ConfigContainerProvider._getParent(configContainerData.parent, id);
            let configurable = typeof configContainerData.configurable === 'undefined' ? true :
                !!configContainerData.configurable;
            let allowUpsert = !!configContainerData.allowUpsert;
            return new ConfigContainer(id, displayName, description, note, parent, configurable, allowUpsert);
        }


        private static _getParent (parent, id) {
            if (!parent) {
                parent = null;

                let nameSpaceNodes = id.split(NAMESPACE_DELIMITER);
                nameSpaceNodes.pop();

                if (nameSpaceNodes.length) {
                    parent = nameSpaceNodes.join(NAMESPACE_DELIMITER);
                }
            }

            return parent;
        }
    }

    angular.module('Fortscale.appConfig.ConfigContainer', ['Fortscale.shared.services.assert'])
        .provider('Fortscale.appConfig.ConfigContainer', ConfigContainerProvider);
}

