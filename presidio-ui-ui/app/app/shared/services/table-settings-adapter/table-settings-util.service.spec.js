describe('tableSettingsUtil.service', function () {
    'use strict';

    var tableSettingsUtil;

    beforeEach(module('Fortscale.shared.services.tableSettingsUtil'));

    beforeEach(inject(function (_tableSettingsUtil_) {
        tableSettingsUtil = _tableSettingsUtil_;
    }));


    describe('Private methods', function () {

        var mergeOriginal, mergeMock;

        beforeEach(function () {
            mergeOriginal = _.merge;
            mergeMock = spyOn(_, 'merge');
        });

        afterEach(function () {
            _.merge = mergeOriginal;
        });

        describe('_getAdapterObject', function () {
            it('should not invoke _.merge when adapter is not provided', function () {
                tableSettingsUtil._getAdapterObject();
                expect(_.merge).not.toHaveBeenCalled();
            });
            it('should return this._adapter if adapter is not provided', function () {

                expect(tableSettingsUtil._getAdapterObject()).toBe(tableSettingsUtil._adapter);
            });
            it('should invoke _.merge with {types: {}, ids: {}}, adapter, ' +
                'tableSettingsUtil._adapter when adapter is provided', function () {
                var adapter = {};
                tableSettingsUtil._getAdapterObject(adapter);
                expect(_.merge).toHaveBeenCalledWith({types: {}, ids: {}}, adapter,
                    tableSettingsUtil._adapter);
            });
            it('should return the result of _.merge when adapter is provided', function () {
                var adapter = {};
                var result = 'result';
                mergeMock.and.returnValue(result);
                expect(tableSettingsUtil._getAdapterObject(adapter)).toBe(result);
            });
        });

        describe('_getAdapter', function () {
            it('should return null when adapterType is not on adapter', function () {
                var adapter = {noTypes: {}};
                var adapterType = 'types';
                var prop = 'prop';
                expect(tableSettingsUtil._getAdapter(adapterType, prop, adapter))
                    .toBe(null);
            });

            it('should return null when prop is not adapterType', function () {
                var adapter = {types: {}};
                var adapterType = 'types';
                var prop = 'prop';
                expect(tableSettingsUtil._getAdapter(adapterType, prop, adapter))
                    .toBe(null);
            });

            it('should return adapter[adapterType][prop] when it exists', function () {
                var adapter = {types: {prop: 'someProp'}};
                var adapterType = 'types';
                var prop = 'prop';
                expect(tableSettingsUtil._getAdapter(adapterType, prop, adapter))
                    .toBe('someProp');
            });
        });

        describe('_getColumnByEntityField', function () {

            var mockGetAdapterByType, mockGetAdapterById;
            var entityField, adapter;

            beforeEach(function () {
                mockGetAdapterByType = spyOn(tableSettingsUtil, 'getAdapterByType')
                    .and.returnValue('adapterByType');
                mockGetAdapterById = spyOn(tableSettingsUtil, 'getAdapterById')
                    .and.returnValue('adapterById');
                entityField = {type: {id: 'type-id'}, id: 'id', name: 'name'};
                adapter = 'adapter';
            });
            it('should invoke getAdapterByType with entityField.type.id, adapter', function () {
                tableSettingsUtil._getColumnByEntityField(entityField, adapter);

                expect(mockGetAdapterByType).toHaveBeenCalledWith(entityField.type.id, adapter);
            });

            it('should invoke getAdapterById with entityField.id, adapter', function () {
                tableSettingsUtil._getColumnByEntityField(entityField, adapter);

                expect(mockGetAdapterById).toHaveBeenCalledWith(entityField.id, adapter);
            });

            it('should not invoke _.merge with columnDef and the result of getAdapterByType ' +
                'if its null', function () {
                tableSettingsUtil._getColumnByEntityField(entityField, adapter);

                expect(_.merge)
                    .toHaveBeenCalledWith({title: 'name', field: 'id'}, 'adapterByType');
            });


            it('should not invoke _.merge with columnDef and the result of getAdapterById ' +
                'if its null', function () {
                mockGetAdapterByType.and.returnValue(null);
                mockGetAdapterById.and.returnValue(null);
                tableSettingsUtil._getColumnByEntityField(entityField);

                expect(_.merge).not.toHaveBeenCalled();
            });

            it('should invoke _.merge with columnDef and the result of getAdapterById ' +
                'if its not null', function () {
                tableSettingsUtil._getColumnByEntityField(entityField, adapter);

                expect(_.merge)
                    .toHaveBeenCalledWith({title: 'name', field: 'id'}, 'adapterById');
            });

            it('should return a {title: \'name\', field: \'id\'} ' +
                'if result of getAdapterByType or getAdapterById is null', function () {
                mockGetAdapterByType.and.returnValue(null);
                mockGetAdapterById.and.returnValue(null);
                tableSettingsUtil._getColumnByEntityField(entityField);
                expect(tableSettingsUtil._getColumnByEntityField(entityField, adapter))
                    .toEqual({title: 'name', field: 'id'});
            });
        });

    });

    describe('Public methods', function () {
        describe('getAdapterByType', function () {

            beforeEach(function () {
                spyOn(tableSettingsUtil, '_getAdapter').and.returnValue('result');
            });

            it('should invoke _getAdapter with "types", type (upperCased), adapter', function () {
                var type = 'type';
                var adapter = 'adapter';
                tableSettingsUtil.getAdapterByType(type, adapter);
                expect(tableSettingsUtil._getAdapter)
                    .toHaveBeenCalledWith('types', 'TYPE', 'adapter');
            });

            it('should return the result of _getAdapter', function () {
                var type = 'type';
                var adapter = 'adapter';
                expect(tableSettingsUtil.getAdapterByType(type, adapter)).toBe('result');
            });
        });
        describe('getAdapterById', function () {

            beforeEach(function () {
                spyOn(tableSettingsUtil, '_getAdapter').and.returnValue('result');
            });

            it('should invoke _getAdapter with "ids", id , adapter', function () {
                var id = 'id';
                var adapter = 'adapter';
                tableSettingsUtil.getAdapterById(id, adapter);
                expect(tableSettingsUtil._getAdapter)
                    .toHaveBeenCalledWith('ids', 'id', 'adapter');
            });

            it('should return the result of _getAdapter', function () {
                var id = 'id';
                var adapter = 'adapter';
                expect(tableSettingsUtil.getAdapterByType(id, adapter)).toBe('result');
            });
        });
        describe('getColumnsByEntityFields', function () {

            var entityFields;
            var adapter;

            beforeEach(function () {

                spyOn(tableSettingsUtil, '_getAdapterObject').and.returnValue('adapter');
                spyOn(tableSettingsUtil, '_getColumnByEntityField');

                entityFields = ['entityField', 'entityField', 'entityField'];
                adapter = 'adapter';

                tableSettingsUtil.getColumnsByEntityFields(entityFields, adapter);

            });
            it('should invoke _getAdapterObject with adapter', function () {
                expect(tableSettingsUtil._getAdapterObject)
                    .toHaveBeenCalledWith(adapter);
            });

            it('should invoke _getColumnByEntityField for each entityField ' +
                'on entityFields', function () {
                expect(tableSettingsUtil._getColumnByEntityField.calls.count())
                .toBe(3);
            });

            it('should invoke _getColumnByEntityField with entityField, adapter', function () {
                expect(tableSettingsUtil._getColumnByEntityField)
                    .toHaveBeenCalledWith('entityField', adapter);
            });
        });
    });

});
