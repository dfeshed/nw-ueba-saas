describe('fs-state-container: url-state-manager.service', function () {
    'use strict';

    var $location;
    var urlStateManager;
    var objectUtils;

    beforeEach(function () {
        module('Fortscale.shared.fsStateContainer.urlStateManager');
    });

    beforeEach(inject(function (_$location_, $injector) {
        $location = _$location_;
        urlStateManager = $injector.get('urlStateManager');
        objectUtils = $injector.get('objectUtils');
    }));


    it('should be defined', function () {
        expect(urlStateManager).toBeDefined();
    });

    describe('private methods', function () {

        describe('_setValueByPath', function () {
            var obj, path, value;
            var mockSetUrlByPath;

            beforeEach(function () {
                obj = {};
                mockSetUrlByPath = spyOn(urlStateManager, '_setValueByPath').and.callThrough();
            });

            it('should set the value on obj as path if path string has no dots', function () {
                path = 'noDots';
                value = 'someValue';

                urlStateManager._setValueByPath(obj, path, value);
                expect(obj.noDots).toBe(value);
            });

            it('should not start the recursion if path has no dots in it', function () {
                path = 'noDots';
                value = 'someValue';

                urlStateManager._setValueByPath(obj, path, value);
                expect(urlStateManager._setValueByPath.calls.count()).toBe(1);
            });

            it('should run the function recursively if path has dots in it', function () {
                path = 'node1.node2.node3';
                value = 'someValue';
                urlStateManager._setValueByPath(obj, path, value);
                expect(urlStateManager._setValueByPath.calls.count()).toBe(3);
            });

            it('should set the value to the path', function () {
                path = 'node1.node2.node3';
                value = 'someValue';
                urlStateManager._setValueByPath(obj, path, value);
                expect(obj.node1.node2.node3).toBe(value);
            });
        });

        describe('_getSearchObject', function () {
            var mockLocationSearch;
            var mockAngularEquals;
            var mockInflate;

            beforeEach(function () {
                mockLocationSearch = spyOn($location, 'search').and.returnValue(null);
                mockInflate = spyOn(objectUtils, 'createFromFlattened');
                mockAngularEquals = spyOn(angular, 'equals').and.callThrough();
                spyOn(urlStateManager, '_setValueByPath');
            });

            it('should get the search object from $location', function () {
                urlStateManager._getSearchObject();
                expect($location.search).toHaveBeenCalledWith();
            });

            it('should return urlStateManager._searchObject if self._locationSearchObject ' +
                'and $location.search() are equal', function () {
                mockAngularEquals.and.returnValue(true);
                urlStateManager._searchObject = 'some old value';
                expect(urlStateManager._getSearchObject()).toBe('some old value');
            });

            it('should should invoke objectUtils.createFromFlattened with the searchObject ' +
                'and self._searchObject', function () {
                urlStateManager._searchObject = {};
                mockLocationSearch.and
                    .returnValue({key1: 'keyValue1', key2: 'keyValue2', key3: 'keyValue3'});
                mockAngularEquals.and.returnValue(false);
                urlStateManager._getSearchObject();
                expect(objectUtils.createFromFlattened)
                    .toHaveBeenCalledWith(
                    {key1: 'keyValue1', key2: 'keyValue2', key3: 'keyValue3'},
                    {});
            });

            it('should return urlStateManager._searchObject', function () {
                mockLocationSearch.and.returnValue({});
                mockAngularEquals.and.returnValue(false);

                expect(urlStateManager._getSearchObject()).toBe(urlStateManager._searchObject);
            });


        });
    });

    describe('public methods', function () {

        describe('getStateByContainerId', function () {
            var mockAngularIsDefined;
            var mockGetSearchObject;

            beforeEach(function () {
                mockAngularIsDefined = spyOn(angular, 'isDefined').and.callThrough();
                mockGetSearchObject = spyOn(urlStateManager, '_getSearchObject');
            });

            it('should invoke urlStateManager._getSearchObject', function () {
                mockGetSearchObject.and.returnValue({});
                urlStateManager.getStateByContainerId('someId');

                expect(urlStateManager._getSearchObject).toHaveBeenCalledWith();
            });

            it('should return the value of _searchObject[containerId] ' +
                'if its defined', function () {
                mockGetSearchObject.and.returnValue({someKey: 'someValue'});
                mockAngularIsDefined.and.returnValue(true);
                expect(urlStateManager.getStateByContainerId('someKey')).toBe('someValue');
            });

            it('should return null if the value of _searchObject[containerId] ' +
                'is undefined', function () {
                mockGetSearchObject.and.returnValue({someKey: 'someValue'});
                mockAngularIsDefined.and.returnValue(false);
                expect(urlStateManager.getStateByContainerId('someKey')).toBe(null);
            });
        });

        describe('updateUrlStateParameter', function () {

            var stateId, paramId, value;

            beforeEach(function () {
                spyOn($location, 'search');
            });

            it('should should invoke $location.search with a deflated object', function () {
                stateId = 'stateId';
                paramId = 'paramId';
                value = {some: 'value'};
                urlStateManager.updateUrlStateParameter(stateId, paramId, value);
                expect($location.search)
                    .toHaveBeenCalledWith({
                        'stateId.paramId.some': 'value'
                    });
            });
        });

        describe('updateUrlStateParameters', function () {

            var stateId, hashMap;

            beforeEach(function () {
                spyOn($location, 'search');
                stateId = 'stateId';
                hashMap = {key1: 'value1', key2: 'value2', key3: {nested1: 'nested1'}};
            });

            it('should invoke $location.search with no arguments ' +
                'as part of the merge', function () {
                urlStateManager.updateUrlStateParameters(stateId, hashMap);
                expect($location.search).toHaveBeenCalledWith();
            });

            it('should invoke $location.search with futureState', function () {

                // Future state is generated by iterating all keys on hashMap, and for each key:
                // stateKey = stateId + '.' + hashKey
                // futureState[stateKey] = hashMap[hashKey]
                var futureState = {
                    'stateId.key1': 'value1',
                    'stateId.key2': 'value2',
                    'stateId.key3.nested1': 'nested1'
                };

                urlStateManager.updateUrlStateParameters(stateId, hashMap);
                expect($location.search).toHaveBeenCalledWith(futureState);
            });

        });
    });

});
