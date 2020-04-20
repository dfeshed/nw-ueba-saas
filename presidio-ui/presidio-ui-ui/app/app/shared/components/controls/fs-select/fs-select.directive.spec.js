describe('fsSelect.directive', function () {
    'use strict';

    var $rootScope;
    var $scope;
    var $compile;
    var $injector;

    var preCompiledElement;
    var element;
    var controller;

    var assert;

    /**
     * Helper functions
     */

    /**
     * Renders a pre-compiled element and gets it's Controller
     */
    function renderElement () {
        element = $compile(preCompiledElement)($scope);
        $scope.$digest();

        controller = element.controller('fsSelect');
    }

    /**
     * Setup
     */

    beforeEach(function () {
        // Load View templates from html2js preprocessor
        module("fsTemplates");
        // Load required Modules
        module('Fortscale.shared.services.assert');
        module('Fortscale.shared.components.fsSelect');
    });

    beforeEach(inject(function (_$injector_, _$rootScope_, _$compile_, _assert_) {
        $rootScope = _$rootScope_;
        $compile = _$compile_;
        $injector = _$injector_;
        $scope = $rootScope.$new();
        $scope.fetchStateDelegate = jasmine.createSpy('fetchStateDelegate');
        $scope.setStateDelegate = jasmine.createSpy('setStateDelegate');

        // Cache a pre compiled element of the tested directive
        preCompiledElement = angular.element('<fs-select></fs-select>');

        // Reset previously set values
        element = undefined;
        controller = undefined;

        assert = _assert_;
    }));

    beforeEach(function () {
        $scope.selectMap = {};
        preCompiledElement.attr('select-map', 'selectMap');
        renderElement();
    });

    /**
     * Test directive attributes
     */

    describe('Arguments', function () {

        beforeEach(function () {
            spyOn(controller.constructor.prototype, '_validateSelectedId');
            spyOn(controller.constructor.prototype, '_validations');
            spyOn(controller.constructor.prototype, '_initWatches');

        });

        it('should accept selectId as string', function () {
            preCompiledElement.attr('select-id', 'someId');
            renderElement();
            expect(controller.selectId).toBe('someId');
        });

        it('should accept selectMap as an object', function () {
            var selectMap = {some: 'map'};
            $scope.selectMap = selectMap;

            preCompiledElement.attr('select-map', 'selectMap');
            renderElement();
            expect(controller.selectMap).toBe(selectMap);
        });

        it('should accept selectedId as string', function () {
            preCompiledElement.attr('selected-id', 'someId');
            renderElement();
            expect(controller.selectedId).toBe('someId');
        });

        it('should accept isNotImmediate as string', function () {
            preCompiledElement.attr('is-not-immediate', 'true');
            renderElement();
            expect(controller.isNotImmediate).toBe('true');
        });

        it('should accept fetchStateDelegate as an object', function () {
            var fetchStateDelegate = {some: 'object'};
            $scope.fetchStateDelegate = fetchStateDelegate;

            preCompiledElement.attr('fetch-state-delegate', 'fetchStateDelegate');
            renderElement();
            expect(controller.fetchStateDelegate).toBe(fetchStateDelegate);
        });

        it('should accept updateStateDelegate as an object', function () {
            var updateStateDelegate = {some: 'object'};
            $scope.updateStateDelegate = updateStateDelegate;

            preCompiledElement.attr('update-state-delegate', 'updateStateDelegate');
            renderElement();
            expect(controller.updateStateDelegate).toBe(updateStateDelegate);
        });
    });

    /**
     * Test Directive Controller
     */

    describe('Controller', function () {

        it('should be defined', function () {
            expect(controller).toBeDefined();
        });

        describe('private', function () {
            describe('Validations', function () {

                beforeEach(function () {
                    spyOn(assert, 'isObject');
                    spyOn(assert, 'isString');
                    spyOn(assert, 'isFunction');
                    controller.selectMap = {
                        firstId: 'FirstValue',
                        secondId: 'SecondValue'
                    };
                    controller.fetchStateDelegate = jasmine.createSpy('fetchStateDelegate');
                    controller.updateStateDelegate = jasmine.createSpy('updateStateDelegate');
                });

                describe('_validateSelectMap', function () {
                    it('should invoke assert.isObject with: ' +
                        'selectMap, "selectMap", errMsg+ "arguments: "', function () {
                        controller._validateSelectMap();
                        expect(assert.isObject)
                            .toHaveBeenCalledWith(controller.selectMap, 'selectMap', 'fsSelect.directive: arguments: ');
                    });

                    it('should invoke assert.isString with value, "selectMap[key]", errMsg, false, true', function () {
                        controller._validateSelectMap();
                        expect(assert.isString)
                            .toHaveBeenCalledWith(controller.selectMap.firstId, 'selectMap[firstId]',
                            'fsSelect.directive: arguments: ', false, true);
                    });

                    it('should invoke assert.isString for each key on selectMap', function () {
                        controller._validateSelectMap();
                        expect(assert.isString.calls.count()).toBe(2);
                    });
                });

                describe('_validateGetStateFn', function () {
                    it('should invoke assert.isFunction', function () {
                        controller._validateGetStateFn();
                        expect(assert.isFunction)
                            .toHaveBeenCalledWith(controller.fetchStateDelegate, 'fetchStateDelegate',
                            'fsSelect.directive: arguments: ', true);
                    });
                });

                describe('_validateSetStateFn', function () {
                    it('should invoke assert.isFunction', function () {
                        controller._validateSetStateFn();
                        expect(assert.isFunction)
                            .toHaveBeenCalledWith(controller.updateStateDelegate, 'updateStateDelegate',
                            'fsSelect.directive: arguments: ', true);
                    });
                });

                describe('_validateSelectedId', function () {

                    it('should throw RangeError if selectedId is not one of the keys on selectMap', function () {
                        controller.selectedId = 'thirdId';
                        expect(controller._validateSelectedId.bind(controller, 'someErrMsg'))
                            .toThrowError(RangeError, 'someErrMsg');
                    });

                    it('should not throw if selectedId is one of the keys on selectMap', function () {
                        controller.selectedId = 'secondId';
                        expect(controller._validateSelectedId.bind(controller)).not.toThrow();
                    });

                    it('should not throw if selectedId is not provided', function () {
                        controller.selectedId = undefined;
                        expect(controller._validateSelectedId.bind(controller)).not.toThrow();
                    });

                });


            });

            describe('_addSelectOption', function () {

                it('should add an option', function () {
                    controller.selectElement = angular.element(document.createElement('select'));
                    controller._addSelectOption('first', 'First');
                    expect(controller.selectElement[0].innerHTML)
                        .toBe('<option value="first">First</option>');
                });
            });

            describe('_renderOptions', function () {
                it('should create all options on selectMap', function () {
                    controller.selectElement = angular.element(document.createElement('select'));
                    controller.selectMap = {
                        firstId: 'FirstValue',
                        secondId: 'SecondValue'
                    };
                    controller._renderOptions();
                    expect(controller.selectElement[0].innerHTML)
                        .toBe('<option value="firstId">FirstValue</option>' +
                        '<option value="secondId">SecondValue</option>');
                });
            });

            describe('_setInitialValue', function () {

                beforeEach(function () {
                    controller.selectElement = angular.element(document.createElement('select'));
                    controller.selectMap = {
                        firstId: 'FirstValue',
                        secondId: 'SecondValue'
                    };
                    controller.selectedId = 'secondId';
                    spyOn(controller, 'setSelectValue');
                });

                it('should invoke setSelectValue with selectedId if it exists', function () {
                    controller._renderOptions();
                    controller._setInitialValue();
                    expect(controller.setSelectValue).toHaveBeenCalledWith(controller.selectedId);
                });

                it('should invoke setSelectValue with first option if selectedId is not provided', function () {
                    controller.selectedId = undefined;
                    controller._renderOptions();
                    controller._setInitialValue();
                    expect(controller.setSelectValue).toHaveBeenCalledWith('firstId');
                });

            });

            describe('_renderSelectElement', function () {
                beforeEach(function () {
                    spyOn(controller, '_renderOptions');
                    spyOn(controller, '_setInitialValue');
                });
            });

            describe('_initWatches', function () {
                beforeEach(function () {
                    spyOn(controller, '_initStateWatch');
                    spyOn(controller, '_initSelectChangeWatch');
                });

                it('should invoke _initStateWatch', function () {
                    controller._initWatches();
                    expect(controller._initStateWatch).toHaveBeenCalled();
                });

                it('should invoke _initStateWatch', function () {
                    controller._initWatches();
                    expect(controller._initSelectChangeWatch).toHaveBeenCalled();
                });
            });

            describe('_stateWatchActionFn', function () {

                beforeEach(function () {
                    spyOn(controller, 'setSelectValue');
                });
                it('should invoke setSelectValue if value is not null or undefined  ', function () {
                    controller.setSelectValue.calls.reset();
                    controller._stateWatchActionFn('someValue');
                    expect(controller.setSelectValue).toHaveBeenCalledWith('someValue');
                });

                it('should not invoke setSelectValue if value is null or undefined  ', function () {
                    controller.setSelectValue.calls.reset();
                    controller._stateWatchActionFn(null);
                    expect(controller.setSelectValue).not.toHaveBeenCalled();
                    controller._stateWatchActionFn(undefined);
                    expect(controller.setSelectValue).not.toHaveBeenCalled();
                });
            });

            describe('_SelectChangeHandler', function () {

                var updateStateDelegateMock;

                beforeEach(function () {

                    $scope.fetchStateDelegate = jasmine.createSpy('fetchStateDelegate');
                    updateStateDelegateMock = $scope.updateStateDelegate = jasmine.createSpy('updateStateDelegate');


                    $scope.selectId = 'selectId';

                    $scope.selectMap = {
                        firstId: 'First',
                        secondId: 'Second'
                    };

                    preCompiledElement.attr({
                        'select-id': 'selectId',
                        'select-map': 'selectMap',
                        'fetch-state-delegate': 'fetchStateDelegate',
                        'update-state-delegate': 'updateStateDelegate'
                    });

                    renderElement();

                    spyOn(controller.$scope, '$apply');
                });

                it('should invoke updateStateDelegate with id, type:"DATA", value: selectValue, ' +
                    'immediate: !this.isNotImmediate', function () {
                    controller._SelectChangeHandler();

                    expect(updateStateDelegateMock).toHaveBeenCalledWith({
                        id: 'selectId',
                        type: 'DATA',
                        value: 'firstId',
                        immediate: true
                    });

                    preCompiledElement.attr('is-not-immediate', 'true');
                    renderElement();

                    controller._SelectChangeHandler();

                    expect(updateStateDelegateMock).toHaveBeenCalledWith({
                        id: 'selectId',
                        type: 'DATA',
                        value: 'firstId',
                        immediate: false
                    });

                });

                it('should invoke $scope.$apply if there is a formCtrl and isNotImmediate is not falsey', function () {
                    controller.formCtrl = {
                        $setDirty: angular.noop
                    };
                    controller.isNotImmediate = 'true';

                    controller._SelectChangeHandler();
                    expect(controller.$scope.$apply).toHaveBeenCalled();
                });

                it('should not invoke $scope.$apply if there isn\'t a formCtrl or isNotImmediate is ' +
                    'falsey', function () {
                    controller.formCtrl = {
                        $setDirty: angular.noop
                    };
                    controller.isNotImmediate = undefined;

                    controller._SelectChangeHandler();
                    expect(controller.$scope.$apply).not.toHaveBeenCalled();

                    controller.formCtrl = undefined;
                    controller.isNotImmediate = 'true';

                    controller._SelectChangeHandler();
                    expect(controller.$scope.$apply).not.toHaveBeenCalled();
                });


            });
        });

        describe('public methods', function () {
            describe('init', function () {
                beforeEach(function () {
                    spyOn(controller, '_validations');
                    spyOn(controller, '_initWatches');
                    controller.init();
                });

                it('should put the select element on selectElement', function () {
                    expect(controller.selectElement[0].type).toBe('select-one');
                });

                it('should invoke validations', function () {
                    expect(controller._validations).toHaveBeenCalled();
                });

                it('should invoke _initWatches', function () {
                    expect(controller._initWatches).toHaveBeenCalled();
                });
            });

            describe('setSelectValue', function () {

                beforeEach(function () {

                    $scope.selectMap = {
                        firstId: 'FirstValue',
                        secondId: 'SecondValue'
                    };
                    preCompiledElement.attr('select-map', 'selectMap');
                    renderElement();
                    spyOn(controller, '_validateSelectedId');


                });

                it('should invoke _validateSelectedId with errMsg, value', function () {
                    controller.setSelectValue('someId');
                    expect(controller._validateSelectedId)
                        .toHaveBeenCalledWith('fsSelect.directive: setSelectValue: ', 'someId');
                });

                it('should set the value ', function () {
                    controller.setSelectValue('secondId');
                    expect(controller.selectElement[0].value).toBe('secondId');
                });
            });

            describe('getAutocompleteValue', function () {
                beforeEach(function () {

                    $scope.selectMap = {
                        firstId: 'FirstValue',
                        secondId: 'SecondValue'
                    };
                    preCompiledElement.attr('select-map', 'selectMap');
                    renderElement();
                });

                it('should return the value of the element', function () {
                    expect(controller.getSelectValue()).toBe('firstId');
                });
            });




        });

    });

    /**
     * Test directive integration
     */

    describe('Integration', function () {
        describe('element', function () {

        });

    });

});
