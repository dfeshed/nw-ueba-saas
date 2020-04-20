describe('fsResourceStore.provider', function () {
    'use strict';


    var fsResourceStoreProvider;
    var fsResourceStore;
    var assert;

    beforeEach(module('Fortscale.shared.components.fsResourceStore',
        function (_fsResourceStoreProvider_) {
            fsResourceStoreProvider = _fsResourceStoreProvider_;
        }));

    beforeEach(inject(
        function (_fsResourceStore_, _assert_) {
            fsResourceStore = _fsResourceStore_;
            assert = _assert_;
        }));

    describe('provider', function () {

        describe('defaults', function () {


            it('should have a default of on hour for expireDuration', function () {

                expect(fsResourceStoreProvider.getExpireDuration()).toBe(1000 * 60 * 60);
            });

            it('should have a default of false for purgeOnExpire', function () {

                expect(fsResourceStoreProvider.getPurgeOnExpire()).toBe(false);
            });
        });


        describe('setExpireDuration', function () {
            it('should set the value of expireDuration', function () {
                fsResourceStoreProvider.setExpireDuration(1000);
                expect(fsResourceStoreProvider.getExpireDuration()).toBe(1000);
            });

        });

        describe('setPurgeOnExpire', function () {

            it('should set the value of purgeOnExipre', function () {
                fsResourceStoreProvider.setPurgeOnExpire(true);
                expect(fsResourceStoreProvider.getPurgeOnExpire()).toBe(true);
            });


        });
    });

    describe('service', function () {
        describe('private methods', function () {
            describe('_isExpired', function () {
                it('should return true if a resourceWrapper\'s update time plus expireDuration ' +
                    'is before now', function () {

                    var now = new Date();

                    var resourceWrapper = {
                        updateTime: new Date(now.valueOf() -
                            (fsResourceStoreProvider.getExpireDuration() + 1))
                    };

                    expect(fsResourceStore._isExpired(resourceWrapper)).toBe(true);
                });

                it('should return false if a resourceWrapper\'s update time plus expireDuration ' +
                    'is after now', function () {

                    var now = new Date();

                    var resourceWrapper = {
                        updateTime: now
                    };

                    expect(fsResourceStore._isExpired(resourceWrapper)).toBe(false);
                });
            });
        });

        describe('public methods', function () {

            describe('storeResource', function () {
                it('should create a resource wrapper for any resource stored', function () {
                    fsResourceStore.storeResource('someResource', {some: 'resource'});
                    expect(fsResourceStore._resources.someResource).toBeDefined();
                    expect(fsResourceStore._resources.someResource.updateTime instanceof Date)
                        .toBe(true);
                    expect(fsResourceStore._resources.someResource.resource)
                        .toEqual({some: 'resource'});
                    expect(fsResourceStore._resources.someResource.purgeOnExpire)
                        .toBe(false);

                });
            });

            it('should use default for purgeOnExpire if it was not provided', function () {
                fsResourceStoreProvider.setPurgeOnExpire(true);
                fsResourceStore.storeResource('someResource', {some: 'resource'});

                expect(fsResourceStore._resources.someResource.purgeOnExpire)
                    .toBe(true);
            });

            it('should set purgeOnExpire if it was provided', function () {
                fsResourceStoreProvider.setPurgeOnExpire(true);
                fsResourceStore.storeResource('someResource', {some: 'resource'}, false);

                expect(fsResourceStore._resources.someResource.purgeOnExpire)
                    .toBe(false);
            });


            describe('fetchResource', function () {

                beforeEach(function () {
                    fsResourceStore.storeResource('someResource', {some: 'resource'});
                });

                it('should return null if resource is not found', function () {

                    expect(fsResourceStore.fetchResource('someNoneResource'))
                        .toBe(null);
                });

                it('should purge the resource if expired and purgeOnExpired is true', function () {
                    spyOn(fsResourceStore, '_isExpired').and.returnValue(true);
                    fsResourceStore._resources.someResource.purgeOnExpire = true;

                    fsResourceStore.fetchResource('someResource');

                    expect(fsResourceStore._resources.someResource)
                        .toBe(undefined);
                });

                it('should add an "_isExpired" property to the resource if expired and ' +
                    'purgeOnExpired is false', function () {
                    spyOn(fsResourceStore, '_isExpired').and.returnValue(true);
                    fsResourceStore._resources.someResource.purgeOnExpire = false;

                    expect(fsResourceStore.fetchResource('someResource')._isExpired)
                        .toBe(true);
                });

                it('should return the resource', function () {

                    expect(fsResourceStore.fetchResource('someResource'))
                        .toEqual({some: 'resource'});
                });
            });

            describe('fetchResourceItemById', function () {

                var resource, mockIsExpired;

                beforeEach(function () {
                    resource = [
                        {id: '1'},
                        {id: '2'},
                        {differentId: '3'}
                    ];

                    mockIsExpired = spyOn(fsResourceStore, '_isExpired').and.returnValue(false);

                    fsResourceStore.storeResource('someResource', resource);
                });

                it('should return null if no resource was found', function () {

                    expect(fsResourceStore.fetchResourceItemById('noResource', 'someId'))
                        .toBe(null);
                });

                it('should return the resource item that its "id" equals ' +
                    'resourceId', function () {


                    expect(fsResourceStore.fetchResourceItemById('someResource', '2'))
                        .toEqual({id: '2'});
                });

                it('should return null if no item matches the id', function () {


                    expect(fsResourceStore.fetchResourceItemById('someResource', '3'))
                        .toBe(null);
                });

                it('should accept idKey value and search by that key', function () {

                    expect(fsResourceStore.fetchResourceItemById('someResource', '3',
                        'differentId'))
                        .toEqual({differentId: '3'});
                });
            });
        });
    });
});
