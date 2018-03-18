describe('objectUtils', function () {
    'use strict';

    var objectUtils;
    var objToDeflate;
    var nameSpace;

    beforeEach(module('Fortscale.shared.services.objectUtils'));

    beforeEach(inject(function (_objectUtils_) {
        objectUtils = _objectUtils_;
    }));

    beforeEach(function () {
        objToDeflate = {
            flprop1: 'flProp1',
            flProp2: {
                slProp1: 'slProp1',
                slProp2: {
                    tlProp1: 'tlProp1'
                }
            }
        };
        nameSpace = 'ns';
    });
    describe('Private methods', function () {
        var caller;

        beforeEach(function () {
            caller = 'caller';
        });

        describe('_validateObject', function () {
            it('should throw ReferenceError if obj is not defined', function () {
                function testFun() {
                    objectUtils._validateObject(caller);
                }

                expect(testFun)
                    .toThrowError(ReferenceError,
                    'Fortscale.shared.services.objectUtils: caller: ' +
                    'obj argument must be provided.');
            });

            it('should throw TypeError if obj is not an object', function () {
                function testFun() {
                    objectUtils._validateObject(caller, 'not object');
                }

                expect(testFun)
                    .toThrowError(TypeError,
                    'Fortscale.shared.services.objectUtils: caller: ' +
                    'obj argument must be an object.');
            });
        });

        describe('_validateObjectName', function () {
            it('should throw ReferenceError if objName is not defined', function () {
                function testFun() {
                    objectUtils._validateObjectName(caller);
                }

                expect(testFun)
                    .toThrowError(ReferenceError,
                    'Fortscale.shared.services.objectUtils: caller: ' +
                    'objName argument must be provided.');
            });

            it('should throw TypeError if objName is not a string', function () {
                function testFun() {
                    objectUtils._validateObjectName(caller, {});
                }

                expect(testFun)
                    .toThrowError(TypeError,
                    'Fortscale.shared.services.objectUtils: caller: ' +
                    'objName argument must be a string.');
            });

            it('should throw RangeError if objName is an empty string', function () {
                function testFun() {
                    objectUtils._validateObjectName(caller, '');
                }

                expect(testFun)
                    .toThrowError(RangeError,
                    'Fortscale.shared.services.objectUtils: caller: ' +
                    'objName argument must not be an empty string.');
            });

            describe('_flattenObject', function () {
                it('should return a flattened object as array ' +
                    'of key value pairs', function () {

                    var flattenedArray = objectUtils._flattenObject(objToDeflate, nameSpace);
                    var expected = ['ns.flprop1', 'flProp1', 'ns.flProp2.slProp1', 'slProp1',
                        'ns.flProp2.slProp2.tlProp1', 'tlProp1'];
                    expect(angular.equals(expected, flattenedArray)).toBe(true);
                });
            });

            describe('_createPairsObject', function () {
                it('should return the expected object ' +
                    'from a flattened array', function () {
                    var flattenedArray = ['ns.flprop1', 'flProp1', 'ns.flProp2.slProp1', 'slProp1',
                        'ns.flProp2.slProp2.tlProp1', 'tlProp1'];
                    var pairsObject = objectUtils._createPairsObject(flattenedArray);
                    var expected = {
                        'ns.flProp2.slProp1': 'slProp1',
                        'ns.flProp2.slProp2.tlProp1': 'tlProp1',
                        'ns.flprop1': 'flProp1'
                    };
                    expect(angular.equals(expected, pairsObject)).toBe(true);
                });

            });
        });
    });

    describe('Public methods', function () {
        describe('flattenToNamespace', function () {

            var obj, objName, result;

            beforeEach(function () {
                spyOn(objectUtils, '_validateObject');
                spyOn(objectUtils, '_validateObjectName');
                spyOn(objectUtils, '_flattenObject').and.returnValue('flattenedArray');
                spyOn(objectUtils, '_createPairsObject').and.returnValue('pairsObject');
                obj = {the: 'object'};
                objName = 'object name';

                result = objectUtils.flattenToNamespace(obj, objName);
            });

            it('should invoke _validateObject with obj', function () {

                expect(objectUtils._validateObject)
                    .toHaveBeenCalledWith('flattenToNamespace', obj);
            });

            it('should invoke _validateObjectName with objName', function () {

                expect(objectUtils._validateObjectName)
                    .toHaveBeenCalledWith('flattenToNamespace', objName);
            });

            it('should invoke _flattenObject with obj and objName', function () {

                expect(objectUtils._flattenObject)
                    .toHaveBeenCalledWith(obj, objName);
            });

            it('should invoke _createPairsObject with the result ' +
                'of _flattenObject', function () {

                expect(objectUtils._createPairsObject)
                    .toHaveBeenCalledWith('flattenedArray');
            });

        });

        describe('createFromFlattened', function () {

            var obj, result;
            var hashMap;

            beforeEach(function () {
                spyOn(objectUtils, '_validateObject');
                spyOn(objectUtils, '_validateHashObject');
                obj = {};
                hashMap = {
                    'ns.flProp2.slProp1': 'slProp1',
                    'ns.flProp2.slProp2.tlProp1': 'tlProp1',
                    'ns.flprop1': 'flProp1'
                };

                result = objectUtils.createFromFlattened(hashMap, obj);
            });

            it('should invoke _validateHashObject', function () {

                expect(objectUtils._validateHashObject)
                    .toHaveBeenCalledWith('createFromFlattened', hashMap);
            });

            it('should invoke _validateObject', function () {

                expect(objectUtils._validateObject)
                    .toHaveBeenCalledWith('createFromFlattened', obj);
            });

            it('should return an inflated object', function () {

                var expected = {
                    ns: {
                        flprop1: 'flProp1',
                        flProp2: {
                            slProp1: 'slProp1',
                            slProp2: {
                                tlProp1: 'tlProp1'
                            }
                        }
                    }
                };
                expect(angular.equals(result, expected)).toBe(true);
                expect(angular.equals(obj, expected)).toBe(true);
            });
        });
    });
});
