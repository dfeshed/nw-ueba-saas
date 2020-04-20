describe('appConfig', function () {
    'use strict';

    // Provider instance
    let appConfig;
    let appConfigProvider;

    // Instantiate the module
    beforeEach(function () {
        module('Config');
    });
    beforeEach(function () {
        module('Fortscale.remoteAppConfig');
    });
    beforeEach(function () {
        module('Fortscale.appConfig');
    });

    // Get the appConfigProvider and the appConfigInstance
    beforeEach(function () {
        module(function (_appConfigProvider_) {
            appConfigProvider = _appConfigProvider_;
        });

        inject(function (_appConfig_) {
            appConfig = _appConfig_;
        });
    });


    describe('appConfigProvider', function () {

        describe('addConfigContainer', function () {
            let configObj, MockConfigContainer;
            beforeEach(function () {
                configObj = {
                    id: 'someId',
                    displayName: 'some display name',
                    description: 'some description'
                };

                MockConfigContainer = spyOn(appConfigProvider.ConfigContainerProvider, 'createContainer');
            });

            it('should throw if no configObj is provided', function () {

                expect(appConfigProvider.addConfigContainer.bind(appConfigProvider, undefined))
                    .toThrowError(ReferenceError);
            });

            it('should throw if configObj is not an object', function () {

                expect(appConfigProvider.addConfigContainer.bind(appConfigProvider, 3))
                    .toThrowError(TypeError);
            });

            it('should invoke ConfigContainerProvider.createContainer with configObj', function () {
                appConfigProvider.addConfigContainer(configObj);
                expect(MockConfigContainer).toHaveBeenCalledWith(configObj);
            });

            it('should throw if isUnique and configContainer instance id points to an existing ConfigContainer',
                function () {
                    appConfigProvider._configContainers.someId = {};
                    MockConfigContainer.and.returnValue({id: 'someId'});
                    expect(appConfigProvider.addConfigContainer.bind(appConfigProvider, configObj, true))
                        .toThrowError(RangeError);
                });

            it('should place the instance on _configContainers where the key is the instance\'s id', function () {
                MockConfigContainer.and.returnValue({id: 'someId'});
                appConfigProvider.addConfigContainer(configObj);
                expect(appConfigProvider._configContainers.someId).toBeDefined();
            });

            it('should return the provider', function () {
                expect(appConfigProvider.addConfigContainer(configObj)).toBe(appConfigProvider);
            });
        });

        describe('addConfigItem', function () {
            let configObj, MockConfigItem;
            beforeEach(function () {
                configObj = {
                    id: 'someId',
                    containerId: 'someContainerId',
                    displayName: 'some display name',
                    description: 'some description',
                    value: 'some value'
                };

                MockConfigItem = spyOn(appConfigProvider.ConfigItemProvider, 'createItem')
                    .and.returnValue({id: 'someId', containerId: 'someContainerId'});

                appConfigProvider._configContainers.someContainerId = {};
            });

            it('should throw if no configObj is provided', function () {

                expect(appConfigProvider.addConfigItem.bind(appConfigProvider, undefined))
                    .toThrowError(ReferenceError);
            });

            it('should throw if configObj is not an object', function () {

                expect(appConfigProvider.addConfigItem.bind(appConfigProvider, 3))
                    .toThrowError(TypeError);
            });

            it('should invoke ConfigItem constructor with configObj', function () {
                appConfigProvider.addConfigItem(configObj);
                expect(MockConfigItem).toHaveBeenCalledWith(configObj);
            });

            it('should throw if isUnique and ConfigItem instance id points to an existing ConfigItem',
                function () {
                    appConfigProvider._configItems.someId = {};
                    expect(appConfigProvider.addConfigItem.bind(appConfigProvider, configObj, true))
                        .toThrowError(RangeError);
                });

            it('should throw if configContainer id is not pointing to an existing config container',
                function () {
                    appConfigProvider._configContainers.someContainerId = undefined;
                    expect(appConfigProvider.addConfigItem.bind(appConfigProvider, configObj, true))
                        .toThrowError(ReferenceError);
                });

            it('should place the instance on _configItems where the key is the instance\'s id', function () {
                appConfigProvider.addConfigItem(configObj);
                expect(appConfigProvider._configItems.someId).toBeDefined();
            });

            it('should return the provider', function () {
                expect(appConfigProvider.addConfigItem(configObj)).toBe(appConfigProvider);
            });
        });

    });

    describe('appConfig', function () {

        beforeEach(function () {
            appConfigProvider._configItems = {};
            appConfigProvider._configContainers = {};
            appConfigProvider
                .addConfigContainer({
                    id: 'default',
                    displayName: 'Default',
                    description: 'Default description'
                })
                .addConfigItem({
                    id: 'default.daysRange',
                    displayName: 'Date Range in Days',
                    description: 'The default setting',
                    type: 'integer',
                    value: 10
                })
                .addConfigContainer({
                    id: 'overview',
                    displayName: 'Overview',
                    description: 'Overview description'
                })
                .addConfigItem({
                    id: 'overview.daysRange',
                    displayName: 'Date Range in Days',
                    description: 'The default setting',
                    type: 'integer',
                    value: 20
                })
                .addConfigContainer({
                    id: 'reports',
                    displayName: 'Reports',
                    description: 'Reports description'
                })
                .addConfigItem({
                    id: 'reports.daysRange',
                    displayName: 'Date Range in Days',
                    description: 'The default setting',
                    type: 'integer',
                    value: 30
                })
                .addConfigContainer({
                    id: 'reports.someReport1',
                    displayName: 'Some Report 1',
                    description: 'Reports description'
                })
                .addConfigItem({
                    id: 'reports.someReport1.daysRange',
                    displayName: 'Date Range in Days',
                    description: 'The default setting',
                    type: 'integer',
                    value: 40
                })
                .addConfigContainer({
                    id: 'reports.someReport2',
                    displayName: 'Some Report 2',
                    description: 'Reports description'
                })
                .addConfigItem({
                    id: 'reports.someReport2.daysRange',
                    displayName: 'Date Range in Days',
                    description: 'The default setting',
                    type: 'integer',
                    value: null
                })
                .addConfigContainer({
                    id: 'reports.someReport2.someReport3',
                    displayName: 'Some Report 3',
                    description: 'Reports description'
                })
                .addConfigItem({
                    id: 'reports.someReport2.someReport3.daysRange',
                    displayName: 'Date Range in Days',
                    description: 'The default setting',
                    type: 'integer',
                    value: null
                })
                .addConfigContainer({
                    id: 'takeFromDefault',
                    displayName: 'take from default',
                    description: 'description'
                })
                .addConfigItem({
                    id: 'takeFromDefault.daysRange',
                    displayName: 'Date Range in Days',
                    description: 'The default setting',
                    type: 'integer',
                    value: null
                })
                .addConfigContainer({
                    id: 'someState',
                    displayName: 'some state',
                    description: 'description'
                })
                .addConfigItem({
                    id: 'someState.config1',
                    displayName: 'Config 1',
                    description: 'blah',
                    type: 'integer',
                    value: 10
                })
                .addConfigItem({
                    id: 'someState.config2',
                    displayName: 'Config 2',
                    description: 'blah',
                    type: 'integer',
                    value: 20
                });
        });

        describe('getDerivedConfigItem', function () {

            it('should throw if configId is not provided', function () {

                expect(() => appConfig.getDerivedConfigItem())
                    .toThrowError(ReferenceError);
            });
            it('should throw if configId is not a string', function () {
                expect(() => appConfig.getDerivedConfigItem(3))
                    .toThrowError(TypeError);
            });
            it('should throw if configId is an empty string', function () {
                expect(() => appConfig.getDerivedConfigItem(''))
                    .toThrowError(RangeError);
            });

            it('should get the item for reports.someReport1.daysRange', function () {

                expect(appConfig.getDerivedConfigItem('reports.someReport1.daysRange').id)
                    .toBe('reports.someReport1.daysRange');
            });

            it('should travers up the parenthood chain to bring reports.daysRange when asked for ' +
                'reports.someReport2.someReport3.daysRange', function () {
                expect(appConfig.getDerivedConfigItem('reports.someReport2.someReport3.daysRange').id)
                    .toBe('reports.daysRange');
            });

            it('should travers up the parenthood chain to bring reports.daysRange when asked for ' +
                'reports.someReport2.daysRange', function () {
                expect(appConfig.getDerivedConfigItem('reports.someReport2.daysRange').id)
                    .toBe('reports.daysRange');
            });

            it('should traverse and get from default the item for takeFromDefault.daysRange', function () {
                expect(appConfig.getDerivedConfigItem('takeFromDefault.daysRange').id)
                    .toBe('default.daysRange');
            });

            it('should return null when no compatible item was found', function () {
                expect(appConfig.getDerivedConfigItem('reports.someReport2.nonExistingId'))
                    .toBe(null);
            });
        });

        describe('getConfigValue', function () {

            let mockgetDerivedConfigItem;
            beforeEach(function () {
                mockgetDerivedConfigItem = spyOn(appConfig, 'getDerivedConfigItem');
            });

            it('should throw if stateName is not provided', function () {

                expect(() => appConfig.getConfigValue(undefined, 'propName'))
                    .toThrowError(ReferenceError);
            });
            it('should throw if stateName is not a string', function () {
                expect(() => appConfig.getConfigValue(3, 'propName'))
                    .toThrowError(TypeError);
            });
            it('should throw if stateName is an empty string', function () {
                expect(() => appConfig.getConfigValue('', 'propName'))
                    .toThrowError(RangeError);
            });

            it('should throw if propName is not provided', function () {

                expect(() => appConfig.getConfigValue('stateName'))
                    .toThrowError(ReferenceError);
            });
            it('should throw if propName is not a string', function () {
                expect(() => appConfig.getConfigValue('stateName', 3))
                    .toThrowError(TypeError);
            });
            it('should throw if propName is an empty string', function () {
                expect(() => appConfig.getConfigValue('stateName', ''))
                    .toThrowError(RangeError);
            });

            it('should get the value for reports.someReport1.daysRange', function () {
                mockgetDerivedConfigItem.and.callThrough();
                expect(appConfig.getConfigValue('reports.someReport1', 'daysRange'))
                    .toBe(40);
            });

            it('should travers up the parenthood chain to bring reports.daysRange when asked for ' +
                'reports.someReport2.someReport3.daysRange', function () {
                mockgetDerivedConfigItem.and.callThrough();
                expect(appConfig.getConfigValue('reports.someReport2.someReport3', 'daysRange'))
                    .toBe(30);
            });

            it('should travers up the parenthood chain to bring reports.daysRange when asked for ' +
                'reports.someReport2.daysRange', function () {
                mockgetDerivedConfigItem.and.callThrough();
                expect(appConfig.getConfigValue('reports.someReport2', 'daysRange'))
                    .toBe(30);
            });

            it('should traverse and get from default the item for takeFromDefault.daysRange', function () {
                mockgetDerivedConfigItem.and.callThrough();
                expect(appConfig.getConfigValue('takeFromDefault', 'daysRange'))
                    .toBe(10);
            });

            it('should return null when no compatible item was found', function () {
                mockgetDerivedConfigItem.and.callThrough();
                expect(appConfig.getConfigValue('reports.someReport2', 'nonExistingId'))
                    .toBe(null);
            });
        });

        describe('getConfigItemsByContainer', function () {
            it('should throw if stateName is not provided', function () {

                expect(() => appConfig.getConfigItemsByContainer())
                    .toThrowError(ReferenceError);
            });
            it('should throw if stateName is not a string', function () {
                expect(() => appConfig.getConfigItemsByContainer(3))
                    .toThrowError(TypeError);
            });
            it('should throw if stateName is an empty string', function () {
                expect(() => appConfig.getConfigItemsByContainer(''))
                    .toThrowError(RangeError);
            });

            it('should return a list of config items that point to a container that has the same id as the state name',
                function () {
                    let configItemsList = appConfig.getConfigItemsByContainer('someState');
                    expect(configItemsList[0].id).toBe('someState.config1');
                    expect(configItemsList[1].id).toBe('someState.config2');
                    expect(configItemsList.length).toBe(2);
                });

            it('should return an empty list when state name does not exist', function () {
                let configItemsList = appConfig.getConfigItemsByContainer('someNonState');
                expect(configItemsList.length).toBe(0);
            });
        });

        describe('getConfigNodesTree', function () {
            it('should return the correct node tree', function () {
                expect(JSON.stringify(appConfig.getConfigNodesTree()))
                    .toBe(JSON.stringify({
                        "nodes": {
                            "default": {
                                "nodes": {},
                                "id": "default",
                                "displayName": "Default",
                                "description": "Default description",
                                "parent": null,
                                "configurable": true,
                                "allowUpsert": false
                            },
                            "overview": {
                                "nodes": {},
                                "id": "overview",
                                "displayName": "Overview",
                                "description": "Overview description",
                                "parent": null,
                                "configurable": true,
                                "allowUpsert": false
                            },
                            "reports": {
                                "nodes": {
                                    "someReport1": {
                                        "nodes": {},
                                        "id": "reports.someReport1",
                                        "displayName": "Some Report 1",
                                        "description": "Reports description",
                                        "parent": "reports",
                                        "configurable": true,
                                        "allowUpsert": false
                                    },
                                    "someReport2": {
                                        "nodes": {
                                            "someReport3": {
                                                "nodes": {},
                                                "id": "reports.someReport2.someReport3",
                                                "displayName": "Some Report 3",
                                                "description": "Reports description",
                                                "parent": "reports.someReport2",
                                                "configurable": true,
                                                "allowUpsert": false
                                            }
                                        },
                                        "id": "reports.someReport2",
                                        "displayName": "Some Report 2",
                                        "description": "Reports description",
                                        "parent": "reports",
                                        "configurable": true,
                                        "allowUpsert": false
                                    }
                                },
                                "id": "reports",
                                "displayName": "Reports",
                                "description": "Reports description",
                                "parent": null,
                                "configurable": true,
                                "allowUpsert": false
                            },
                            "takeFromDefault": {
                                "nodes": {},
                                "id": "takeFromDefault",
                                "displayName": "take from default",
                                "description": "description",
                                "parent": null,
                                "configurable": true,
                                "allowUpsert": false
                            },
                            "someState": {
                                "nodes": {},
                                "id": "someState",
                                "displayName": "some state",
                                "description": "description",
                                "parent": null,
                                "configurable": true,
                                "allowUpsert": false
                            }
                        }
                    }));
            });
        });

        describe('getConfigContainer', function () {
            it('should throw if getConfigContainer is not provided', function () {

                expect(() => appConfig.getConfigContainer())
                    .toThrowError(ReferenceError);
            });
            it('should throw if getConfigContainer is not a string', function () {
                expect(() => appConfig.getConfigContainer(3))
                    .toThrowError(TypeError);
            });
            it('should throw if getConfigContainer is an empty string', function () {
                expect(() => appConfig.getConfigContainer(''))
                    .toThrowError(RangeError);
            });

            it('should return the ConfigContainer that has the provided id', function () {

                expect(JSON.stringify(appConfig.getConfigContainer('reports.someReport2.someReport3')))
                    .toBe(JSON.stringify({
                        "id": "reports.someReport2.someReport3",
                        "displayName": "Some Report 3",
                        "description": "Reports description",
                        "parent": "reports.someReport2",
                        "configurable": true,
                        "allowUpsert": false
                    }));
            });

            it('should return null when no config item is found', function () {
                expect(appConfig.getConfigContainer('nonExistingId')).toBe(null);
            });
        });

        describe('isParentOfConfigContainer', function () {
            it('should throw if currentContainerId is not provided', function () {

                expect(() => appConfig.isParentOfConfigContainer(undefined, 'string'))
                    .toThrowError(ReferenceError);
            });
            it('should throw if currentContainerId is not a string', function () {
                expect(() => appConfig.isParentOfConfigContainer(3, 'string'))
                    .toThrowError(TypeError);
            });
            it('should throw if currentContainerId is an empty string', function () {
                expect(() => appConfig.isParentOfConfigContainer('', 'string'))
                    .toThrowError(RangeError);
            });

            it('should throw if targetContainerId is not provided', function () {

                expect(() => appConfig.isParentOfConfigContainer('string'))
                    .toThrowError(ReferenceError);
            });
            it('should throw if targetContainerId is not a string', function () {
                expect(() => appConfig.isParentOfConfigContainer('string', 3))
                    .toThrowError(TypeError);
            });
            it('should throw if targetContainerId is an empty string', function () {
                expect(() => appConfig.isParentOfConfigContainer('string', ''))
                    .toThrowError(RangeError);
            });

            it('should return false when targetContainerId does not exist', function () {

                expect(appConfig.isParentOfConfigContainer('reports', 'nonExistingId'))
                    .toBe(false);
            });

            it('should return false if targetContainer has no parent', function () {
                expect(appConfig.isParentOfConfigContainer('someId', 'reports'))
                    .toBe(false);
            });

            it('should return true when current container is a direct parent of target container', function () {
                expect(appConfig.isParentOfConfigContainer('reports', 'reports.someReport2'))
                    .toBe(true);
            });

            it('should return true when current container is a maternal relative of target container', function () {
                expect(appConfig.isParentOfConfigContainer('reports', 'reports.someReport2.someReport3'))
                    .toBe(true);
            });

        });
    });

});
