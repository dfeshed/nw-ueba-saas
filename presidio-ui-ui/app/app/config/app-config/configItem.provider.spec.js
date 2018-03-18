describe('configItemProvider', function () {
    'use strict';

    let configItemProvider;
    let configObj, createNewInstance;

    beforeEach(function () {
        module('Fortscale.appConfig.ConfigItem');
    });

    beforeEach(function () {
        module([
            'Fortscale.appConfig.ConfigItemProvider',
            (_configItemProvider_) => {
                configItemProvider = _configItemProvider_;
            }
        ]);

        inject(() => {});
    });

    beforeEach(function () {
        configObj = {
            id: 'someContainer.someId',
            containerId: 'someContainer',
            displayName: 'some display name',
            description: 'some description',
            value: 'some value'
        };

        createNewInstance = function () {
            return configItemProvider.createItem(configObj);
        };
    });

    describe('createItem', function () {
        describe('validations', function () {
            it('should throw if configObj.id is not defined', function () {
                configObj.id = undefined;
                expect(createNewInstance).toThrowError(ReferenceError);
            });
            it('should throw if configObj.id is not a string', function () {
                configObj.id = 3;
                expect(createNewInstance).toThrowError(TypeError);
            });
            it('should throw if configObj.id is an empty string', function () {
                configObj.id = '';
                expect(createNewInstance).toThrowError(RangeError);
            });
            it('should not throw if configObj.containerId is not defined', function () {
                configObj.containerId = undefined;
                expect(createNewInstance).not.toThrow();
            });
            it('should throw if configObj.containerId is not a string', function () {
                configObj.containerId = 3;
                expect(createNewInstance).toThrowError(TypeError);
            });
            it('should throw if configObj.containerId is an empty string', function () {
                configObj.containerId = '';
                expect(createNewInstance).toThrowError(RangeError);
            });
            it('should throw if configObj.displayName is not defined', function () {
                configObj.displayName = undefined;
                expect(createNewInstance).toThrowError(ReferenceError);
            });
            it('should throw if configObj.displayName is not a string', function () {
                configObj.displayName = 3;
                expect(createNewInstance).toThrowError(TypeError);
            });
            it('should throw if configObj.displayName is an empty string', function () {
                configObj.displayName = '';
                expect(createNewInstance).toThrowError(RangeError);
            });
            it('should not throw if configObj.description is not defined', function () {
                configObj.description = undefined;
                expect(createNewInstance).not.toThrow();
            });
            it('should throw if configObj.description is not a string', function () {
                configObj.description = 3;
                expect(createNewInstance).toThrowError(TypeError);
            });
            it('should throw if configObj.description is an empty string', function () {
                configObj.description = '';
                expect(createNewInstance).toThrowError(RangeError);
            });
        });
    });

    describe('instance', function () {
        it('should create an instance with correct values', function () {
            expect(JSON.stringify(createNewInstance())).toBe(JSON.stringify({
                "id": "someContainer.someId",
                "containerId": "someContainer",
                "description": "some description",
                "displayName": "some display name",
                "type": null,
                "validators":[null],
                "formatter":null,
                "value": "some value",
                "_originalValue": "some value",
                "meta":null
            }));
        });

    });

    describe('_getContainerId', function () {

        it('should return configObj.containerId if provided', function () {
            configObj.id = 'someOtherContainerId.someId';
            configObj.containerId = 'someContainerId';
            var configContainer = createNewInstance();
            expect(configContainer.containerId).toBe('someContainerId');
        });

        it('should return id\'s namespace parent if containerId is not provided', function () {
            configObj.id = 'someOtherContainerId.someId';
            configObj.containerId = undefined;
            var configContainer = createNewInstance();
            expect(configContainer.containerId).toBe('someOtherContainerId');

            configObj.id = 'someParent.someOtherContainerId.someId';
            configObj.containerId = undefined;
            configContainer = createNewInstance();
            expect(configContainer.containerId).toBe('someParent.someOtherContainerId');
        });

        it('should throw if id does not have at least two nodes namespace', function () {
            configObj.id = 'someOtherContainerId';
            configObj.containerId = undefined;
            expect(createNewInstance).toThrowError(RangeError);
        });

    });


});
