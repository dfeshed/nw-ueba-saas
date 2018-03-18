describe('fs-indicator-graphs-handler.provider', function () {
    'use strict';

    // Provider instance
    var fsIndicatorGraphsHandlerProvider;
    var fsIndicatorGraphsHandler;
    var assert;

    // Instantiate the module
    beforeEach(function () {
        module('Fortscale.shared.services.assert');
    });

    beforeEach(function () {
        module('Fortscale.shared.services.fsIndicatorGraphsHandler');
    });

    // Get the appConfigProvider and the appConfigInstance
    beforeEach(function () {
        module(function (_fsIndicatorGraphsHandlerProvider_, _assertConstant_) {
            fsIndicatorGraphsHandlerProvider = _fsIndicatorGraphsHandlerProvider_;
            assert = _assertConstant_;
        });

        inject(function (_fsIndicatorGraphsHandler_) {
            fsIndicatorGraphsHandler = _fsIndicatorGraphsHandler_;
        });
    });

    describe('fsIndicatorGraphsHandlerProvider', function () {
        it('should be defined', function () {
            expect(fsIndicatorGraphsHandlerProvider).toBeDefined();
        });

        describe('addIndicatorQuery', function () {

            var queryObj;
            var handlerFn;
            var errMsg;

            beforeEach(function () {
                queryObj = {};
                handlerFn = function () {
                };
                errMsg = fsIndicatorGraphsHandlerProvider._errMsg + "addIndicatorQuery: ";

                spyOn(assert, 'isObject');
                spyOn(assert, 'isFunction');
            });

            it('should call assert with queryObj', function () {
                fsIndicatorGraphsHandlerProvider.addIndicatorQuery(queryObj, handlerFn);
                expect(assert.isObject).toHaveBeenCalledWith(queryObj, 'queryObj', errMsg, false);
            });

            it('should call assert with handlerFn', function () {
                fsIndicatorGraphsHandlerProvider.addIndicatorQuery(queryObj, handlerFn);
                expect(assert.isFunction).toHaveBeenCalledWith(handlerFn, 'handlerFn', errMsg, false);
            });

            it('should throw if two queryObjects are equal', function () {
                function testFn () {
                    fsIndicatorGraphsHandlerProvider.addIndicatorQuery(queryObj, handlerFn);
                    fsIndicatorGraphsHandlerProvider.addIndicatorQuery(queryObj, handlerFn);
                }

                expect(testFn).toThrowError(RangeError, errMsg + "queryObj must be a unique query.");
            });

            it('should throw if queryObj is not parseble to string', function () {
                queryObj.propertyToFail = queryObj;
                function testFn () {
                    fsIndicatorGraphsHandlerProvider.addIndicatorQuery(queryObj, handlerFn);
                }

                expect(testFn).toThrowError(RangeError, errMsg + "queryObj must be a parseble to string.");
            });

            it('should add an item to _indicatorQueries', function () {
                queryObj = {test: 'test'};

                fsIndicatorGraphsHandlerProvider.addIndicatorQuery(queryObj, handlerFn);

                var result = fsIndicatorGraphsHandlerProvider._indicatorQueries.get(JSON.stringify(queryObj));
                expect(JSON.stringify(result.query)).toBe(JSON.stringify(queryObj));
                expect(result.handlerFn).toBe(handlerFn);
            });
        });

    });

    describe('fsIndicatorGraphsHandler', function () {

        describe('getHandlerFnByIndicator', function () {

            var queryObj1, queryObj2;
            var handlerFn1, handlerFn2;
            var indicator;

            beforeEach(function () {
                queryObj1 = {test: 1};
                queryObj2 = {test: 2};
                handlerFn1 = function () {
                };
                handlerFn2 = function () {
                };
            });

            it('should return a relevant handlerFn when matching an indicator or null if none ' +
                'match is made', function () {
                fsIndicatorGraphsHandlerProvider.addIndicatorQuery(queryObj1, handlerFn1);
                fsIndicatorGraphsHandlerProvider.addIndicatorQuery(queryObj2, handlerFn2);
                indicator = {a: 'blah', b: 'blah', test: 2};

                expect(fsIndicatorGraphsHandler.getHandlerFnByIndicator(indicator)).toBe(handlerFn2);

                indicator = {a: 'blah', b: 'blah', test: 1};
                expect(fsIndicatorGraphsHandler.getHandlerFnByIndicator(indicator)).toBe(handlerFn1);

                indicator = {a: 'blah', b: 'blah', test: 3};
                expect(fsIndicatorGraphsHandler.getHandlerFnByIndicator(indicator)).toBe(null);

            });


        });
    });

});
