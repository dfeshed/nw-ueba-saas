describe('interpolate.service', function () {
    'use strict';

    var interpolation;
    var $interpolate;

    beforeEach(module(function ($provide) {
        $provide.provider('$interpolate', function () {
            this.$get = function () {
                $interpolate = jasmine.createSpy("$interpolate");
                return $interpolate;
            };
        });
    }));

    beforeEach(function () {
        module('Fortscale.shared.services');
    });

    beforeEach(inject(function (_interpolation_) {
        interpolation = _interpolation_;
    }));


    describe('Private methods', function () {
        describe('_convertTemplateToString', function () {

            var mockAngularIsString;
            var template;

            beforeEach(function () {
                mockAngularIsString = spyOn(angular, 'isString').and.callThrough();
            });

            it('should return template argument as is if it is a string', function () {
                template = 'string';
                mockAngularIsString.and.returnValue(true);
                expect(interpolation._convertTemplateToString(template)).toBe(template);
            });

            it('should return a stringified object if is a stingifiable object', function () {
                template = {
                    someControl: {
                        value: 'someValue'
                    }
                };
                mockAngularIsString.and.returnValue(false);
                expect(interpolation._convertTemplateToString(template))
                    .toBe('{"someControl":{"value":"someValue"}}');
            });

            it('should throw if trying to stringify an unstringified object', function () {
                var testFunc = function () {
                    template = window;
                    mockAngularIsString.and.returnValue(false);
                    interpolation._convertTemplateToString(template);
                };

                expect(testFunc)
                    .toThrowError(SyntaxError);
            });
        });

        describe('_convertStringToObject', function () {
            var templateString;

            beforeEach(function () {
                templateString = '{not a JSON parseble string}';
            });

            it('should throw a SyntaxError ' +
                'when trying to parse a non parseble string', function () {

                var testFunc = function () {
                    interpolation._convertStringToObject(templateString);
                };

                expect(testFunc).toThrowError(SyntaxError);
            });
        });

        describe('_interpolateTemplate', function () {

            var convertStringToObjectMock;
            var interpolateReturnMock;
            var templateString;
            var state;

            beforeEach(function () {
                convertStringToObjectMock = spyOn(interpolation, '_convertStringToObject');
                interpolateReturnMock = jasmine.createSpy('$interpolateReturn');
                $interpolate.and.returnValue(interpolateReturnMock);
                templateString = 'templateString';
                state = {some: 'state'};
            });

            it('should invoke $interpolate with provided templateString', function () {
                interpolation._interpolateTemplate(templateString, state);
                expect($interpolate).toHaveBeenCalledWith(templateString);
            });

            it('should invoke the returned function from $interpolate ' +
                'with provided state', function () {

                interpolation._interpolateTemplate(templateString, state);
                expect(interpolateReturnMock).toHaveBeenCalledWith(state);
            });

            it('should invoke _convertStringToObject ' +
                'with the result from $interpolate()()', function () {
                interpolateReturnMock.and.returnValue('interpolate()() return value');
                interpolation._interpolateTemplate(templateString, state);

                expect(interpolation._convertStringToObject)
                    .toHaveBeenCalledWith('interpolate()() return value');
            });
        });
    });

    describe('Public methods', function () {
        describe('interpolate', function () {
            var template, state;
            var templateString;
            var templateObject;

            beforeEach(function () {
                template = {control: {value: 'someValue'}};
                state = {some: 'state'};
                templateString = '{control: {value: \'someValue\'}}';
                templateObject = {control: {value: 'someValue'}};

                spyOn(interpolation, '_convertTemplateToString').and.returnValue(templateString);
                spyOn(interpolation, '_interpolateTemplate').and.returnValue(templateObject);

                interpolation.interpolate(template, state);

            });

            it('should invoke _convertTemplateToString with template', function () {
                expect(interpolation._convertTemplateToString)
                    .toHaveBeenCalledWith(template);
            });

            it('should invoke _interpolateTemplate with templateString and state', function () {
                expect(interpolation._interpolateTemplate)
                    .toHaveBeenCalledWith(templateString, state);
            });


            it('should return what _interpolateTemplate returns', function () {
                expect(interpolation.interpolate(template, state)).toEqual(templateObject);
            });
        });
    });

});

describe('interpolate.service', function () {
    'use strict';

    var interpolation;


    beforeEach(function () {
        module('Fortscale.shared.services');
    });

    beforeEach(inject(function (_$http_, _$httpBackend_, _interpolation_) {
        interpolation = _interpolation_;
    }));

    describe('Integration', function () {
        var template, state;
        var templateObject;

        it('should take a templateString and state ' +
            'and should return an interpolated and objectified template', function () {

            template = '{"conditions": {"control": "{{control.value}}"}}';
            state = {control: {value: 'someValue'}};
            templateObject = {conditions: {control: 'someValue'}};
            interpolation.interpolate(template, state);
            expect(angular.equals(interpolation.interpolate(template, state),templateObject))
                .toBe(true);

            template = '{"conditions": {"control": {{control.value}} }}';
            state = {control: {value: {from: 'from some value'}}};
            templateObject = {conditions: {control: {from: 'from some value'}}};
            interpolation.interpolate(template, state);
            expect(angular.equals(interpolation.interpolate(template, state),templateObject))
                .toBe(true);
        });

    });
});
