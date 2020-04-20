describe('fs-resource-store.directive', function () {
    'use strict';

    var $rootScope;
    var $scope;
    var $compile;

    var preCompiledElement;
    var element;
    var controller;

    var fsResourceStoreService;

    /**
     * Helper functions
     */

    /**
     * Renders a pre-compiled element and gets it's Controller
     */
    function renderElement() {
        element = $compile(preCompiledElement)($scope);
        $scope.$digest();
        controller = $scope.$$childTail.fsResource;
    }

    /**
     * Setup
     */

    beforeEach(function () {
        // Load required Modules
        module('Fortscale.shared.components.fsResourceStore');
    });

    beforeEach(inject(function ($injector, _$rootScope_, _$compile_, _fsResourceStore_) {
        $rootScope = _$rootScope_;
        $compile = _$compile_;
        $scope = $rootScope.$new();
        fsResourceStoreService = _fsResourceStore_;

        // Cache a precompiled element of the tested directive
        preCompiledElement = angular.element('<fs-resource-store></fs-resource-store>');

        // Reset previously set values
        element = undefined;
        controller = undefined;
    }));


    describe('Arguments', function () {

        beforeEach(function () {
            $scope.someResource = {some: 'resource'};
            preCompiledElement.attr({
                'resource-name': 'someName',
                'resource': 'someResource',
                'purge-on-expire': 'true'
            });
            renderElement();
        });
        it('should accept resourceName as a string to _resourceName', function () {

            expect(controller._resourceName).toBe('someName');
        });

        it('should accept resource expression as _resource', function () {
            expect(controller._resource).toEqual({some: 'resource'});
        });

        it('should accept purgeOnExpire string as _purgeOnExpire', function () {
            expect(controller._purgeOnExpire).toBe('true');
        });
    });


    describe('Controller', function () {


        beforeEach(function () {
            $scope.someResource = {some: 'resource'};
            preCompiledElement.attr({
                'resource-name': 'someName',
                'resource': 'someResource',
                'purge-on-expire': 'true'
            });
            renderElement();
        });

        it('should be defined', function () {
            expect(controller).toBeDefined();
        });

        describe('private methods', function () {

            describe('_resourceWatchActionFn', function () {
                it('should invoke fsResourceStoreService.storeResource with ' +
                    'this._resourceName, resource, !!this._purgeOnExpire', function () {

                    spyOn(fsResourceStoreService, 'storeResource');
                    var resource = {some: 'resource'};
                    controller._resourceWatchActionFn(resource);
                    expect(fsResourceStoreService.storeResource)
                        .toHaveBeenCalledWith(controller._resourceName, resource,
                        !!controller._purgeOnExpire);
                });
            });

        });

    });

});
