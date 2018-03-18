describe('CSVConverter.service', function () {
    'use strict';

    var CSVConverter;

    beforeEach(module('Fortscale.shared.services.CSVConverter'));

    beforeEach(inject(function (_CSVConverter_) {
        CSVConverter = _CSVConverter_;
    }));

    beforeEach(function () {

    });
    describe('Private methods', function () {


        describe('_validateCSVSchemaValue', function () {
            var CSVSchemaValue;
            var testFunc;

            beforeEach(function () {
                CSVSchemaValue = 'isString';
                testFunc = function () {
                    CSVConverter._validateCSVSchemaValue(CSVSchemaValue);
                };
            });
            it('should throw RangeError if CSVSchemaValue is an empty string', function () {
                CSVSchemaValue = '';
                expect(testFunc).toThrowError(RangeError,
                    'All CSVSchema members that are strings must not be empty strings.');
            });

            it('should throw ReferenceError if CSVSchemaValue is an object ' +
                'and does not have a name property', function () {
                CSVSchemaValue = {};
                expect(testFunc).toThrowError(ReferenceError,
                    'All CSVSchema members that are objects must have a "name" property.');
            });

            it('should throw TypeError if CSVSchemaValue is an object ' +
                'and its name property is not a string', function () {
                CSVSchemaValue = {name: 1};
                expect(testFunc).toThrowError(TypeError,
                    'All CSVSchema members that are objects ' +
                    'must have a "name" property that is a string.');
            });

            it('should throw RangeError if CSVSchemaValue is an object ' +
                'and its name property is an empty string', function () {
                CSVSchemaValue = {name: ''};
                expect(testFunc).toThrowError(RangeError,
                    'All CSVSchema members that are objects ' +
                    'must have a "name" property this is not an empty string.');
            });

            it('should throw ReferenceError if CSVSchemaValue is an object ' +
                'and does not have a type property', function () {
                CSVSchemaValue = {name: 'name'};
                expect(testFunc).toThrowError(ReferenceError,
                    'All CSVSchema members that are objects ' +
                    'must have a "type" property.');
            });

            it('should throw TypeError if CSVSchemaValue is an object ' +
                'and its type property is not a string', function () {
                CSVSchemaValue = {name: 'name', type: 1};
                expect(testFunc).toThrowError(TypeError,
                    'All CSVSchema members that are objects ' +
                    'must have a "type" property that is a string.');
            });

            it('should throw RangeError if CSVSchemaValue is an object ' +
                'and its type property is not a value that\'s allowed', function () {
                CSVSchemaValue = {name: 'name', type: 'notInList'};
                expect(testFunc).toThrowError(RangeError,
                    /must have a "type" property that is one of these values:/);
            });

            it('should throw TypeError if CSVSchema is not a string or an object', function () {
                CSVSchemaValue = 123;

                expect(testFunc).toThrowError(TypeError,
                    'All CSVSchema members must be either a string ' +
                    'or an object.');
            });
        });

        describe('_validateCSVSchema', function () {

            var CSVSchema;
            var testFunc;

            beforeEach(function () {
                CSVSchema = ['member1', 'member2', 'member3'];
                testFunc = function () {
                    CSVConverter._validateCSVSchema(CSVSchema);
                };
                spyOn(CSVConverter, '_validateCSVSchemaValue');
            });
            it('should throw ReferenceError if SCVSchema is not provided', function () {
                CSVSchema = undefined;
                expect(testFunc).toThrowError(ReferenceError,
                'CSVSchema argument must be provided.');
            });

            it('should throw if SCVSchema is not an array', function () {
                CSVSchema = 'notAnArray';
                expect(testFunc).toThrowError(TypeError,
                    'CSVSchema argument must be an array.');
            });

            it('should invoke _validateCSVSchemaValue for each member of the array', function () {
                CSVConverter._validateCSVSchema(CSVSchema);
                expect(CSVConverter._validateCSVSchemaValue).toHaveBeenCalledWith('member1', '');
                expect(CSVConverter._validateCSVSchemaValue).toHaveBeenCalledWith('member2', '');
                expect(CSVConverter._validateCSVSchemaValue).toHaveBeenCalledWith('member3', '');
                expect(CSVConverter._validateCSVSchemaValue.calls.count()).toBe(3);
            });
        });

        describe('_validateCSVString', function () {
            var CSVString;
            var testFunc;

            beforeEach(function () {
                CSVString = 'isString';
                testFunc = function () {
                    CSVConverter._validateCSVString(CSVString);
                };
            });

            it('should throw ReferenceError if CSVString is not provided', function () {
                CSVString = undefined;
                expect(testFunc).toThrowError(ReferenceError,
                    'CSVString argument must be provided.');
            });

            it('should throw TypeError if CSVString is not a string', function () {
                CSVString = 123;
                expect(testFunc).toThrowError(TypeError,
                    'CSVString argument must be a string.');
            });
        });

        describe('_validateModel', function () {
            var model;
            var testFunc;

            beforeEach(function () {
                model = {};
                testFunc = function () {
                    CSVConverter._validateModel(model);
                };
            });

            it('should throw ReferenceError if model is not provided', function () {
                model = undefined;
                expect(testFunc).toThrowError(ReferenceError,
                    'model argument must be provided.');
            });

            it('should throw TypeError if model is not an object', function () {
                model = 123;
                expect(testFunc).toThrowError(TypeError,
                    'model argument must be an object.');
            });
        });

        describe('_parsers', function () {
            describe('to', function () {

                var originalParseInt, originalParseFloat;
                var mockParseInt, mockParseFloat;

                beforeEach(function () {
                    originalParseInt = parseInt;
                    originalParseFloat = parseFloat;
                    mockParseInt = spyOn(window, 'parseInt').and.callThrough();
                    mockParseFloat = spyOn(window, 'parseFloat').and.callThrough();
                });

                afterEach(function () {
                    window.parseInt = originalParseInt;
                    window.parseFloat = originalParseFloat;

                });
                describe('boolean', function () {
                    it('should return false if value is undefined', function () {

                        expect(CSVConverter._parsers.to.boolean(undefined)).toBe(false);
                    });
                    it('should return false if value is null', function () {

                        expect(CSVConverter._parsers.to.boolean(undefined)).toBe(false);
                    });
                    it('should return false if value is "0"', function () {

                        expect(CSVConverter._parsers.to.boolean(undefined)).toBe(false);
                    });
                    it('should return false if value is 0', function () {

                        expect(CSVConverter._parsers.to.boolean(undefined)).toBe(false);
                    });
                    it('should return false if value is "false"', function () {

                        expect(CSVConverter._parsers.to.boolean(undefined)).toBe(false);
                    });
                    it('should return true if value is not one of the above', function () {

                        expect(CSVConverter._parsers.to.boolean(undefined)).toBe(false);
                    });
                });

                describe('integer', function () {


                    it('should invoke parseInt with the provided value', function () {
                        CSVConverter._parsers.to.integer('123');

                        expect(mockParseInt).toHaveBeenCalledWith('123');
                    });

                    it('should return the returned value from parseInt', function () {
                        mockParseInt.and.returnValue(123);

                        var result = CSVConverter._parsers.to.integer('123');

                        expect(result).toBe(123);
                    });
                });

                describe('number', function () {


                    it('should invoke parseInt with the provided value', function () {
                        CSVConverter._parsers.to.number('123.123');

                        expect(mockParseFloat).toHaveBeenCalledWith('123.123');
                    });

                    it('should return the returned value from parseInt', function () {
                        mockParseFloat.and.returnValue(123.123);

                        var result = CSVConverter._parsers.to.number('123.123');

                        expect(result).toBe(123.123);
                    });
                });
            });

            describe('from', function () {
                describe('boolean', function () {
                    it('should return "1" if true', function () {

                        expect(CSVConverter._parsers.from.boolean(true)).toBe('1');
                    });
                    it('should return "0" if false', function () {

                        expect(CSVConverter._parsers.from.boolean(false)).toBe('0');
                    });
                });
                describe('integer', function () {
                    it('should return the toString value of the provided value', function () {

                        var value = 123;
                        expect(CSVConverter._parsers.from.integer(value)).toBe(value.toString());
                    });
                });
                describe('number', function () {
                    it('should return the toString value of the provided value', function () {
                        var value = 123;
                        expect(CSVConverter._parsers.from.number(value)).toBe(value.toString());
                    });
                });
            });
        });
    });

    describe('Public methods', function () {

        describe('toModel', function () {
            var CSVString, CSVSchema;
            var mockValidateCSVSchema;
            var mockValidateCSVString;

            beforeEach(function () {
                CSVString = ['true', '123', 'hello', 'false', '456.456', 'world'].join(',');
                CSVSchema = [
                    {type: 'boolean', name: 'first'},
                    {type: 'number', name: 'second'},
                    'third'];

                mockValidateCSVSchema = spyOn(CSVConverter, '_validateCSVSchema');
                mockValidateCSVString = spyOn(CSVConverter, '_validateCSVString');
            });

            it('should invoke _validateCSVSchema with CSVSchema ' +
                'and an error message', function () {
                CSVConverter.toModel(CSVString, CSVSchema);
                expect(mockValidateCSVSchema).toHaveBeenCalledWith(CSVSchema,
                    CSVConverter._errMsg + 'toModel: ');
            });

            it('should invoke _validateCSVString with CSVString ' +
                'and an error message', function () {
                CSVConverter.toModel(CSVString, CSVSchema);
                expect(mockValidateCSVString).toHaveBeenCalledWith(CSVString,
                    CSVConverter._errMsg + 'toModel: ');
            });
            it('should return a model array', function () {
                var expected = [
                    {first: true, second: 123, third: 'hello'},
                    {first: false, second: 456.456, third: 'world'}
                ];
                expect(angular.equals(expected, CSVConverter.toModel(CSVString, CSVSchema)))
                    .toBe(true);
            });
        });

        describe('toCSVString', function () {
            var model, CSVSchema;
            var mockValidateCSVSchema;
            var mockValidateModel;

            beforeEach(function () {
                model = [
                    {first: true, second: 123, third: 'hello'},
                    {first: false, second: 456.456, third: 'world'}
                ];                CSVSchema = [
                    {type: 'boolean', name: 'first'},
                    {type: 'number', name: 'second'},
                    'third'];

                mockValidateCSVSchema = spyOn(CSVConverter, '_validateCSVSchema');
                mockValidateModel = spyOn(CSVConverter, '_validateModel');
            });

            it('should invoke _validateCSVSchema with CSVSchema ' +
                'and an error message', function () {
                CSVConverter.toCSVString(model, CSVSchema);
                expect(mockValidateCSVSchema).toHaveBeenCalledWith(CSVSchema,
                    CSVConverter._errMsg + 'toCSVString: ');
            });

            it('should invoke _validateModel with model ' +
                'and an error message', function () {
                CSVConverter.toCSVString(model, CSVSchema);
                expect(mockValidateModel).toHaveBeenCalledWith(model,
                    CSVConverter._errMsg + 'toCSVString: ');
            });

            it('should return a CSVstring from the model', function () {
                var expected = ['1', '123', 'hello', '0', '456.456', 'world'].join(',');
                expect(CSVConverter.toCSVString(model, CSVSchema)).toBe(expected);
            });
        });
    });

});
