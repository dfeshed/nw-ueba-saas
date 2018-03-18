describe('configContainer.provider', function () {
    'use strict';
    let configContainerProvider;
    let configObj, createNewInstance;

    beforeEach(function () {
        module('Fortscale.appConfig.ConfigContainer');
    });

    beforeEach(function () {
        module([
            'Fortscale.appConfig.ConfigContainerProvider',
            (_configContainerProvider_) => {
                configContainerProvider = _configContainerProvider_;
            }
        ]);

        inject(() => {});
    });

    beforeEach(function () {
        configObj = {
            id: 'someId',
            displayName: 'some display name',
            description: 'some description'
        };

        createNewInstance = function () {
            return configContainerProvider.createContainer(configObj);
        };
    });

    describe('createContainer', function () {


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
            it('should not throw if configObj.parent is not defined', function () {
                configObj.parent = undefined;
                expect(createNewInstance).not.toThrow();
            });
            it('should throw if configObj.parent is not a string', function () {
                configObj.parent = 3;
                expect(createNewInstance).toThrowError(TypeError);
            });
            it('should throw if configObj.parent is an empty string', function () {
                configObj.parent = '';
                expect(createNewInstance).toThrowError(RangeError);
            });
        });

        describe('instance', function () {
            it('should create an instance with correct values', function () {
                expect(JSON.stringify(createNewInstance())).toBe(JSON.stringify({
                    id: 'someId',
                    displayName: 'some display name',
                    description: 'some description',
                    parent: null,
                    configurable: true,
                    allowUpsert: false
                }));
            });

        });

    });



    describe('_getParent', function () {

        it('should return null if parent is undefined and id is one node namespace', function () {
            configObj.id = 'noNameSpaceNodes';
            var configContainer = createNewInstance();
            expect(configContainer.parent).toBe(null);
        });

        it('should return the parent namespace if namespace is one then one node', function () {
            configObj.id = 'some.name';
            var configContainer = createNewInstance();
            expect(configContainer.parent).toBe('some');
        });
    });

});
