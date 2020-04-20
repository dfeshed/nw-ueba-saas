describe('restangular.config', function () {
    'use strict';

    describe('restangular.dataAdapters.provider', function () {
        var dataAdaptersProvider;
        var $injector;

        beforeEach(module('Fortscale'));
        beforeEach(function () {
            module([
                'restangular.dataAdaptersProvider',
                function (_dataAdaptersProvider) {
                    dataAdaptersProvider = _dataAdaptersProvider;
                }]);
        });
        beforeEach(inject(function (_$injector_) {
            $injector = _$injector_;
        }));

        it('should be defined', function () {
            expect(dataAdaptersProvider).toBeDefined();
        });

        describe('processDataQuery', function () {
            it('should throw ReferenceError if provided data argument ' +
                'has no "data" property', function () {

                var data = {};
                function testFunc () {
                    dataAdaptersProvider.processDataQuery(data);
                }

                expect(testFunc)
                    .toThrowError(ReferenceError, 'restangular.dataAdapters: ' +
                    'provided data argument must have a "data" property.');
            });

            it('should throw TypeError if "data" property value is not an object', function () {
                var data = {
                    data: 'not an object'
                };

                function testFunc () {
                    dataAdaptersProvider.processDataQuery(data);
                }
                expect(testFunc)
                    .toThrowError(TypeError, 'restangular.dataAdapters: ' +
                    'provided data argument\'s "data" property must be an object.');
            });


            it('should throw if it is consumed as anything but a provider', function () {

                function testFunc () {
                    $injector.get('restangular.dataAdapters');
                }

                expect(testFunc)
                    .toThrowError(Error, 'restangular.dataAdapters: ' +
                    'This provider is only supposed to be consumed in config phase.');
            });

            it('should take the "data" property from the provided data argument ' +
                'and return it as the main object. It should place all other properties ' +
                'under _meta property.', function () {
                var data = {
                    someProp1: 'someProp1',
                    someProp2: 'someProp2',
                    data: {some: 'data'}
                };

                expect(dataAdaptersProvider.processDataQuery(data).some).toBe('data');
                expect(angular.equals(dataAdaptersProvider.processDataQuery(data)._meta,
                    {
                        someProp1: 'someProp1',
                        someProp2: 'someProp2'
                    })).toBe(true);
            });
        });

    });

    describe('restangular config', function () {
        var Restangular;
        var BASE_URL;
        var dataAdaptersProvider;

        beforeEach(module('Fortscale'));

        beforeEach(function () {
            module([
                'restangular.dataAdaptersProvider',
                function (_dataAdaptersProvider) {
                    dataAdaptersProvider = _dataAdaptersProvider;
                }]);
        });
        beforeEach(inject(function (_Restangular_, _BASE_URL_) {
            Restangular = _Restangular_;
            BASE_URL = _BASE_URL_;

            spyOn(dataAdaptersProvider, 'processDataQuery');
            spyOn(dataAdaptersProvider, 'processRest');
        }));

        it('should be defined', function () {

            expect(Restangular).toBeDefined();
        });

        it('should have its base url assigned as BASE_URL', function () {

            expect(Restangular.configuration.baseUrl).toBe(BASE_URL);
        });

        it('should have a response interceptor that invokes dataAdaptersProvider.processDataQuery' +
            ' with the returned data if entity is dataQuery', function () {
            var ri = Restangular.configuration.responseInterceptors[0];
            var data = {some: 'data'};

            ri (data, '', 'dataQuery');

            expect(dataAdaptersProvider.processDataQuery)
                .toHaveBeenCalledWith(data);
        });

        it('should have a response interceptor that does not invoke ' +
            'dataAdaptersProvider.processDataQuery  if entity is not ' +
            'dataQuery', function () {
            var ri = Restangular.configuration.responseInterceptors[0];
            var data = {some: 'data'};

            ri (data, '', 'notDataQuery');

            expect(dataAdaptersProvider.processDataQuery).not.toHaveBeenCalled();
        });
    });
});
