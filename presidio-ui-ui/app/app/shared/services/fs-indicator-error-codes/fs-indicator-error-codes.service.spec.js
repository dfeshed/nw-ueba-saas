describe('fs-indicator-error-codes.service', function () {
    'use strict';

    // Provider instance
    var fsIndicatorErrorCodes;

    // Instantiate the module
    beforeEach(function () {
        module('Fortscale.shared.services.assert');
    });

    beforeEach(function () {
        module('Fortscale.shared.services.fsIndicatorErrorCodes');
    });

    // Get the appConfigProvider and the appConfigInstance
    beforeEach(function () {
        inject(function (_fsIndicatorErrorCodes_) {
            fsIndicatorErrorCodes = _fsIndicatorErrorCodes_;
        });
    });

    describe('fsIndicatorErrorCodes', function () {
        it('should be defined', function () {
            expect(fsIndicatorErrorCodes).toBeDefined();
        });

        describe('public methods', function () {

            describe('addErrorObject', function () {

                it('should throw if dataEntityId is not a string, not defined, or empty string', function () {
                    expect(fsIndicatorErrorCodes.addErrorObject.bind(fsIndicatorErrorCodes,123, {}))
                        .toThrowError(TypeError);
                    expect(fsIndicatorErrorCodes.addErrorObject.bind(fsIndicatorErrorCodes,undefined, {}))
                        .toThrowError(ReferenceError);
                    expect(fsIndicatorErrorCodes.addErrorObject.bind(fsIndicatorErrorCodes,'', {}))
                        .toThrowError(RangeError);
                });

                it('should throw if errorCodesObject is not an object, or not defined', function () {
                    expect(fsIndicatorErrorCodes.addErrorObject.bind(fsIndicatorErrorCodes,'ok', 123))
                        .toThrowError(TypeError);
                    expect(fsIndicatorErrorCodes.addErrorObject.bind(fsIndicatorErrorCodes,'ok', undefined))
                        .toThrowError(ReferenceError);
                });

                it('should add an object to the map with dataEntityId as key', function () {
                    var testObj = {im: 'test'};
                    fsIndicatorErrorCodes.addErrorObject('testId', testObj);
                    expect(fsIndicatorErrorCodes._errorCodesMap.get('testId')).toBe(testObj);
                });
            });

            describe('getDisplayMessage', function () {
                it('should throw if dataEntityId is not a string, not defined, or empty string', function () {
                    expect(fsIndicatorErrorCodes.getDisplayMessage.bind(fsIndicatorErrorCodes,123, 'ok'))
                        .toThrowError(TypeError);
                    expect(fsIndicatorErrorCodes.getDisplayMessage.bind(fsIndicatorErrorCodes,undefined, 'ok'))
                        .toThrowError(ReferenceError);
                    expect(fsIndicatorErrorCodes.getDisplayMessage.bind(fsIndicatorErrorCodes,'', 'ok'))
                        .toThrowError(RangeError);
                });
                it('should throw if errorCode is not a string, not defined, or empty string', function () {
                    expect(fsIndicatorErrorCodes.getDisplayMessage.bind(fsIndicatorErrorCodes, 'ok', 123))
                        .toThrowError(TypeError);
                    expect(fsIndicatorErrorCodes.getDisplayMessage.bind(fsIndicatorErrorCodes, 'ok', undefined))
                        .toThrowError(ReferenceError);
                    expect(fsIndicatorErrorCodes.getDisplayMessage.bind(fsIndicatorErrorCodes, 'ok', ''))
                        .toThrowError(RangeError);
                });

                it('should return the displayMessage of the errorCode', function () {
                    var testObj = {
                        someErrorCode: {
                            displayMessage: 'test'
                        }
                    };
                    fsIndicatorErrorCodes.addErrorObject('testId', testObj);
                    expect(fsIndicatorErrorCodes.getDisplayMessage('testId', 'someErrorCode')).toBe('test');
                });

                it('should return original errorCode if errorCode does not exist on the errorObject', function () {
                    var testObj = {
                        someErrorCode: {
                            displayMessage: 'test'
                        }
                    };
                    fsIndicatorErrorCodes.addErrorObject('testId', testObj);
                    expect(fsIndicatorErrorCodes.getDisplayMessage('testId', 'nonExistingCode'))
                        .toBe('nonExistingCode');
                });

                it('should return original errorCode if errorObject does not exist', function () {
                    var testObj = {
                        someErrorCode: {
                            displayMessage: 'test'
                        }
                    };
                    fsIndicatorErrorCodes.addErrorObject('testId', testObj);
                    expect(fsIndicatorErrorCodes.getDisplayMessage('nonId', 'someErrorCode'))
                        .toBe('someErrorCode');
                });
            });
        });

        describe('private methods', function () {
            describe('_getErrorsObject', function () {
                it('should return an error object when provided with proper id', function () {
                    var testObj = {im: 'test'};
                    fsIndicatorErrorCodes.addErrorObject('testId', testObj);
                    expect(fsIndicatorErrorCodes._getErrorsObject('testId')).toBe(testObj);
                });
                it('should return null when provided with a non existing id', function () {
                    var testObj = {im: 'test'};
                    fsIndicatorErrorCodes.addErrorObject('testId', testObj);
                    expect(fsIndicatorErrorCodes._getErrorsObject('nonExistingId')).toBe(null);
                });
            });
        });
    });

});
