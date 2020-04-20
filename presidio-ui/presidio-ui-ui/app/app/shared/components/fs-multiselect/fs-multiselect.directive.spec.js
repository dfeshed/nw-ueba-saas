describe('fs-sidebar.directive', function () {
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
    function renderElement() {
        element = $compile(preCompiledElement)($scope);
        $scope.$digest();

        controller = element.controller('fsMultiselect');
    }

    /**
     * Setup
     */

    beforeEach(function () {
        // Load View templates from html2js preprocessor
        module("fsTemplates");
        // Load required Modules
        module('Fortscale.shared.services.assert');
        module('Fortscale.shared.components.fsMultiselect');
    });

    beforeEach(inject(function (_$injector_, _$rootScope_, _$compile_, _assert_) {
        $rootScope = _$rootScope_;
        $compile = _$compile_;
        $injector = _$injector_;
        $scope = $rootScope.$new();
        assert = _assert_;

        // Cache a precompiled element of the tested directive
        preCompiledElement = angular.element('<fs-multiselect></fs-multiselect>');

        // Reset previously set values
        element = undefined;
        controller = undefined;
    }));


    var multiselectId, label, fetchStateDelegateMock, updateStateDelegateMock, listData;

    beforeEach(function () {
        multiselectId = 'multiselectId';
        label = 'label';
        fetchStateDelegateMock = jasmine.createSpy('fetchStateDelegate');
        updateStateDelegateMock = jasmine.createSpy('updateStateDelegate');
        listData = [
            {
                id: 'id1',
                value: 'value1'
            },
            {
                id: 'id2',
                value: 'value2'
            }
        ];

        $scope.multiselectId = multiselectId;
        $scope.label = label;
        $scope.fetchStateDelegate = fetchStateDelegateMock;
        $scope.updateStateDelegate = updateStateDelegateMock;
        $scope.listData = listData;

        preCompiledElement.attr({
            'list-data': 'listData',
            'label': 'label',
            'fetch-state-delegate': 'fetchStateDelegate',
            'update-state-delegate': 'updateStateDelegate',
            'multiselect-id': 'multiselectId'
        });

        renderElement();
    });

    /**
     * Test directive attributes
     */

    describe('Arguments', function () {


        it('should accept multiselectId', function () {
            expect(controller.multiselectId).toBe(multiselectId);
        });

        it('should accept listData', function () {
            expect(controller._listData).toBeDefined();
        });

        it('should accept label', function () {
            expect(controller.label).toBe(label);
        });

        it('should accept fetchStateDelegate', function () {
            expect(controller.fetchStateDelegate)
                .toBe(fetchStateDelegateMock);
        });

        it('should accept updateStateDelegate', function () {
            expect(controller.updateStateDelegate)
                .toBe(updateStateDelegateMock);
        });
    });

    /**
     * Test Directive Controller
     */

    describe('Controller', function () {
        describe('private', function () {

            describe('_ctrlInit', function () {

                var mock_ctrlValidations;

                beforeEach(function () {
                    mock_ctrlValidations = spyOn(controller, '_ctrlValidations');
                });
                it('should invoke _ctrlValidations', function () {
                    mock_ctrlValidations.calls.reset();
                    controller._ctrlInit();

                    expect(controller._ctrlValidations)
                        .toHaveBeenCalledWith(controller._errMsg + '_ctrlInit: ');
                });

                it('should clone listData to _listData', function () {
                    controller._ctrlInit();
                    expect(controller.listData).not.toBe(controller._listData);
                    expect(controller.listData).toEqual(controller._listData);
                });
            });

            describe('_validateListData', function () {

                var errMsg;
                var testFn = function () {
                    controller._validateListData(listData, errMsg);
                };

                beforeEach(function () {
                    errMsg = '';
                });

                it('should throw if any of the members do not have a value property', function () {
                    listData.push({id: 'someId'});

                    expect(testFn)
                        .toThrowError(ReferenceError, 'dataItem.value must be provided.');
                });

                it('should throw if any of the value properties are not strings', function () {
                    listData.push({id: 'someId', value: 100});

                    expect(testFn)
                        .toThrowError(TypeError, 'dataItem.value must be a string.');
                });

                it('should throw if any of the value properties are empty string', function () {
                    listData.push({id: 'someId', value: ''});

                    expect(testFn)
                        .toThrowError(RangeError, 'dataItem.value must not be an empty string.');
                });

                it('should throw if any of the value properties are not unique', function () {
                    listData.push({id: 'someId', value: 'value2'});

                    expect(testFn)
                        .toThrowError(RangeError, 'value property\'s values must be unique.');
                });

                it('should throw if any of the members do not have a id property', function () {
                    listData.push({value: 'someValue'});

                    expect(testFn)
                        .toThrowError(ReferenceError, 'dataItem.id must be provided.');
                });

                it('should throw if any of the id properties are not strings', function () {
                    listData.push({id: 100, value: 'someValue'});

                    expect(testFn)
                        .toThrowError(TypeError, 'dataItem.id must be a string.');
                });

                it('should throw if any of the id properties are empty string', function () {
                    listData.push({id: '', value: 'someValue'});

                    expect(testFn)
                        .toThrowError(RangeError, 'dataItem.id must not be an empty string.');
                });

                it('should throw if any of the value properties are not unique', function () {
                    listData.push({id: 'id2', value: 'someValue'});

                    expect(testFn)
                        .toThrowError(RangeError, 'id property\'s values must be unique.');
                });


            });

            describe('_ctrlValidations', function () {
                var errMsg;

                beforeEach(function () {
                    errMsg = 'err: ';

                    spyOn(assert, 'isString');
                    spyOn(assert, 'isArray');
                    spyOn(assert, 'isFunction');
                    spyOn(controller, '_validateListData');
                    controller._ctrlValidations(errMsg);

                });

                it('should invoke assert.isString with multiselectId, "multiselectId" ' +
                    'and errMsg', function () {

                    expect(assert.isString)
                        .toHaveBeenCalledWith(multiselectId, 'multiselectId', errMsg);
                });

                it('should invoke assert.isString with label, "label" ' +
                    'and errMsg', function () {
                    expect(assert.isString)
                        .toHaveBeenCalledWith(label, 'label', errMsg, true);
                });

                it('should invoke assert.isString with _listData, "listData" ' +
                    'and errMsg', function () {
                    expect(assert.isArray)
                        .toHaveBeenCalledWith(controller._listData, 'listData', errMsg);
                });

                it('should invoke _validateListData with _listData,' +
                    'errMsg + "listData argument: " ', function () {
                    expect(controller._validateListData)
                        .toHaveBeenCalledWith(controller._listData, 'err: listData argument: ');
                });

                it('should invoke assert.isFunction with fetchStateDelegate, ' +
                    '"fetchStateDelegate" and errMsg', function () {
                    expect(assert.isFunction)
                        .toHaveBeenCalledWith(fetchStateDelegateMock, 'fetchStateDelegate',
                        errMsg);
                });

                it('should invoke assert.isFunction with updateStateDelegate, ' +
                    '"updateStateDelegate" and errMsg', function () {
                    expect(assert.isFunction)
                        .toHaveBeenCalledWith(updateStateDelegateMock, 'updateStateDelegate',
                        errMsg);
                });
            });

            describe('_watchStateFn', function () {

                beforeEach(function () {
                    fetchStateDelegateMock.and.returnValue('fetchStateResult');
                });
                it('should invoke fetchStataDelegate with multiselectId', function () {
                    controller._watchStateFn();
                    expect(fetchStateDelegateMock)
                        .toHaveBeenCalledWith(controller.multiselectId);
                });

                it('should return the result of fetchStateDelegate', function () {

                    expect(controller._watchStateFn()).toBe('fetchStateResult');
                });
            });

            describe('_watchStateActionFn', function () {
                beforeEach(function () {
                    spyOn(controller, '_digestIncomingState');
                    spyOn(controller, '_setAllSelected');
                });

                it('should invoke _digestIncomingState with state', function () {
                    controller._watchStateActionFn('state');
                    expect(controller._digestIncomingState)
                        .toHaveBeenCalledWith('state');
                });

                it('should invoke _setAllSelected', function () {
                    controller._watchStateActionFn('state');
                    expect(controller._setAllSelected)
                        .toHaveBeenCalledWith();
                });

                it('should not invoke the above methods if no state is provided', function () {
                    controller._watchStateActionFn();

                    expect(controller._digestIncomingState).not.toHaveBeenCalled();
                    expect(controller._setAllSelected).not.toHaveBeenCalled();
                });
            });

            describe('_getMultiselectState', function () {

                it('should return null no listData items are checked', function () {
                    controller.listData = [{}, {}];
                    expect(controller._getMultiselectState()).toBe(null);
                });

                it('should return null if all listData items are checked', function () {
                    controller.listData = [{checked: true}, {checked: true}];
                    expect(controller._getMultiselectState()).toBe(null);
                });

                it('should return a csv of all id\'s of checked items', function () {
                    controller.listData = [
                        {checked: true, id: 'id1'},
                        {checked: true, id: 'id2'},
                        {checked: false, id: 'id3'}];
                    expect(controller._getMultiselectState()).toBe('id1,id2');
                });

            });

            describe('_getShortViewValue', function () {
                it('should return a ; delimited string of all item.value', function () {
                    var checkedItems = [{value: '1'}, {value: '2'}, {value: '3'}];
                    expect(controller._getShortViewValue(checkedItems))
                        .toBe('1; 2; 3');
                });
            });

            describe('_getLongViewValue', function () {
                it('should return "1 item" if number of items is one', function () {
                    var checkedItems = [{}];
                    expect(controller._getLongViewValue(checkedItems))
                        .toBe('1 item');
                });

                it('should should return "{n} items" if number of items is greater ' +
                    'then one', function () {
                    var checkedItems = [{}, {}, {}];
                    expect(controller._getLongViewValue(checkedItems))
                        .toBe('3 items');
                });
            });

            describe('_getMultiselectViewValue', function () {
                var mock_getShortViewValue, mock_getLongViewValue;

                beforeEach(function () {
                    mock_getShortViewValue = spyOn(controller, '_getShortViewValue')
                        .and.returnValue('short-view-value');

                    mock_getLongViewValue = spyOn(controller, '_getLongViewValue')
                        .and.returnValue('long-view-value');
                });

                it('should return "All" if all listData items are selected', function () {
                    controller._listData = [{checked: true}, {checked: true}];
                    expect(controller._getMultiselectViewValue())
                        .toBe('All');
                });

                it('should return "None" if no listData items are selected', function () {
                    controller._listData = [{checked: false}, {checked: false}];
                    expect(controller._getMultiselectViewValue())
                        .toBe('None');
                });

                it('should invoke _getShortViewValue if listData.length is under ' +
                    'the threshold', function () {
                    controller._listData = [
                        {checked: true},
                        {checked: true},
                        {checked: true},
                        {checked: false}
                    ];

                    controller.longListThreshold = 5;
                    expect(controller._getMultiselectViewValue())
                        .toBe('short-view-value');
                });

                it('should invoke _getLongViewValue if listData.length is equal ' +
                    'or greater then threshold', function () {
                    controller._listData = [
                        {checked: true},
                        {checked: true},
                        {checked: true},
                        {checked: false}
                    ];

                    controller.longListThreshold = 4;
                    expect(controller._getMultiselectViewValue())
                        .toBe('long-view-value');
                    controller.longListThreshold = 3;
                    expect(controller._getMultiselectViewValue())
                        .toBe('long-view-value');
                });
            });

            describe('_digestIncomingState', function () {

                beforeEach(function () {
                    spyOn(controller, '_getMultiselectViewValue').and.returnValue('someValue');

                    controller.listData = [
                        {
                            id: '1',
                            value: 'one'
                        },
                        {
                            id: '2',
                            value: 'two'
                        },
                        {
                            id: '3',
                            value: 'three'
                        }
                    ];

                    var state = '1,3';

                    controller._digestIncomingState(state);
                });

                it('should take an csv state and set checked to true for each item ' +
                    'that has an id that equals to one of the ids from the csv. ' +
                    'This should happen for both listData and _listData.', function () {


                    expect(controller.listData).toEqual([
                        {
                            id: '1',
                            value: 'one',
                            checked: true
                        },
                        {
                            id: '2',
                            value: 'two'
                        },
                        {
                            id: '3',
                            value: 'three',
                            checked: true
                        }
                    ]);
                });

                it('should invoke _getMultiselectViewValue', function () {

                    expect(controller._getMultiselectViewValue)
                        .toHaveBeenCalledWith();
                });

                it('should set the result of _getMultiselectViewValue to ' +
                    'controller.value', function () {

                    expect(controller.value).toBe('someValue');
                });
            });

            describe('_isAllSelected', function () {
                it('should return true if every item on a list is checked', function () {
                    var list = [
                        {
                            checked: true
                        },
                        {
                            checked: true
                        },
                        {
                            checked: true
                        }
                    ];
                    expect(controller._isAllSelected(list)).toBe(true);
                });

                it('should return false if not every item on a list is checked', function () {
                    var list = [
                        {
                            checked: true
                        },
                        {
                            checked: false
                        },
                        {
                            checked: true
                        }
                    ];
                    expect(controller._isAllSelected(list)).toBe(false);
                });
            });

            describe('_setAllSelected', function () {
                var mock_isAllSelected;

                beforeEach(function () {
                    mock_isAllSelected = spyOn(controller, '_isAllSelected');
                });

                it('should invoke _isAllSelected with listData', function () {
                    controller._setAllSelected();
                    expect(mock_isAllSelected).toHaveBeenCalledWith(controller.listData);
                });

                it('should set the value of _isAllSelected to allSelected', function () {
                    mock_isAllSelected.and.returnValue('mock_isAllSelected');
                    controller._setAllSelected();
                    expect(controller.allSelected).toBe('mock_isAllSelected');
                });
            });

            describe('_checkAllInView', function () {
                it('should set checked={checkedValu} to all items in listData', function () {
                    controller.listData = [
                        {
                            checked: false
                        },
                        {
                            checked: false
                        },
                        {
                            checked: false
                        }
                    ];

                    controller._checkAllInView(true);

                    expect(controller.listData).toEqual([
                        {
                            checked: true
                        },
                        {
                            checked: true
                        },
                        {
                            checked: true
                        }
                    ]);

                    controller._checkAllInView(false);
                    expect(controller.listData).toEqual([
                        {
                            checked: false
                        },
                        {
                            checked: false
                        },
                        {
                            checked: false
                        }
                    ]);
                });
            });

            describe('_getDropdownElement', function () {
                it('should invoke $element.find with ".fs-multiselect-dropdown-list"', function () {
                    spyOn(controller.$element, 'find').and.returnValue('element');

                    expect(controller._getDropdownElement()).toBe('element');
                    expect(controller.$element.find)
                        .toHaveBeenCalledWith('.fs-multiselect-dropdown-list');
                });
            });

            describe('_ctrlInit', function () {
                it('should invoke _ctrlValidations', function () {
                    spyOn(controller, '_ctrlValidations').calls.reset();
                    controller._ctrlInit();
                    expect(controller._ctrlValidations).toHaveBeenCalled();
                });
            });

            describe('_linkInit', function () {
                it('should invoke _getDropdownElement and set the return value ' +
                    'to _dropDownElement', function () {
                    controller._dropDownElement = null;
                    spyOn(controller, '_getDropdownElement').and.returnValue('_getDropdownElement');
                    controller._linkInit();
                    expect(controller._getDropdownElement).toHaveBeenCalledWith();
                    expect(controller._dropDownElement).toBe('_getDropdownElement');
                });

                it('should invoke _initWatches', function () {
                    spyOn(controller, '_initWatches');
                    controller._linkInit();
                    expect(controller._initWatches).toHaveBeenCalledWith();
                });
            });

        });

        describe('public methods', function () {

            describe('hideDropdown', function () {
                it('should invoke _dropDownElement.removeClass with "show"', function () {
                    spyOn(controller._dropDownElement, 'removeClass');
                    controller.hideDropdown();
                    expect(controller._dropDownElement.removeClass)
                        .toHaveBeenCalledWith('show');
                });

                it('should set the value of anyDropdownOpened to false', function () {
                    controller.__setAnyDropdownOpened(true);
                    controller.hideDropdown();
                    expect(controller.__getAnyDropdownOpened()).toBe(false);
                });

            });

            describe('showDropdown', function () {

                var mockHasClass;

                beforeEach(function () {
                    mockHasClass = spyOn(controller._dropDownElement, 'hasClass');
                    spyOn(controller._dropDownElement, 'addClass');
                });
                it('should invoke _dropDownElement.hasClass with "show"', function () {
                    controller.showDropdown();
                    expect(controller._dropDownElement.hasClass)
                        .toHaveBeenCalledWith('show');
                });

                it('should invoke _dropDownElement.addClass with "show" ' +
                    'if hasClass returns false and _anyDropdownOpened is false', function () {
                    mockHasClass.and.returnValue(false);
                    controller.__setAnyDropdownOpened(false);
                    controller.showDropdown();
                    expect(controller._dropDownElement.addClass)
                        .toHaveBeenCalledWith('show');
                });

                it('should set the value of anyDropdownOpened to true', function () {
                    mockHasClass.and.returnValue(false);
                    controller.__setAnyDropdownOpened(false);
                    controller.showDropdown();
                    expect(controller.__getAnyDropdownOpened()).toBe(true);
                });

                it('should not invoke _dropDownElement.addClass with "show" ' +
                    'if hasClass return true or _anyDropdownOpened is true', function () {
                    mockHasClass.and.returnValue(true);
                    controller.__setAnyDropdownOpened(false);
                    controller.showDropdown();
                    expect(controller._dropDownElement.addClass)
                        .not.toHaveBeenCalled();
                    mockHasClass.and.returnValue(false);
                    controller.__setAnyDropdownOpened(true);
                    controller.showDropdown();
                    expect(controller._dropDownElement.addClass)
                        .not.toHaveBeenCalled();
                });
            });

            describe('selectAllHandler', function () {

                var mock_isAllSelected;

                beforeEach(function () {
                    mock_isAllSelected = spyOn(controller, '_isAllSelected');
                    spyOn(controller, '_checkAllInView');
                    spyOn(controller.$scope.dropdownForm, '$setDirty');
                });

                it('should invoke _checkAllInView(false) if isAllSelected is true', function () {
                    mock_isAllSelected.and.returnValue(true);
                    controller.selectAllHandler();
                    expect(controller._checkAllInView)
                        .toHaveBeenCalledWith(false);
                });

                it('should set allSelected to false if isAllSelected is true', function () {
                    mock_isAllSelected.and.returnValue(true);
                    controller.selectAllHandler();
                    expect(controller.allSelected).toBe(false);
                });
                it('should invoke _checkAllInView(true) if isAllSelected is false', function () {
                    mock_isAllSelected.and.returnValue(false);
                    controller.selectAllHandler();
                    expect(controller._checkAllInView)
                        .toHaveBeenCalledWith(true);
                });

                it('should set allSelected to true if isAllSelected is false', function () {
                    mock_isAllSelected.and.returnValue(false);
                    controller.selectAllHandler();
                    expect(controller.allSelected).toBe(true);
                });

                it('should set the form to dirty', function () {
                    controller.selectAllHandler();
                    expect(controller.$scope.dropdownForm.$setDirty)
                        .toHaveBeenCalledWith();
                });
            });

            describe('selectSingleHandler', function () {
                it('should invoke _setAllSelected', function () {
                    spyOn(controller, '_setAllSelected');
                    controller.selectSingleHandler();
                    expect(controller._setAllSelected)
                        .toHaveBeenCalledWith();
                });
            });

            describe('okClickHandler', function () {

                var mock_getMultiselectViewValue, mock_getMultiselectState;

                beforeEach(function () {
                    mock_getMultiselectViewValue = spyOn(controller, '_getMultiselectViewValue');
                    mock_getMultiselectState = spyOn(controller, '_getMultiselectState');
                    spyOn(controller.$scope.dropdownForm, '$setPristine');
                    spyOn(controller, 'hideDropdown');
                });

                it('should invoke hideDropdown', function () {
                    controller.okClickHandler();
                    expect(controller.hideDropdown).toHaveBeenCalledWith();
                });

                it('should clone listData into _listData', function () {
                    controller._listData = null;
                    controller.listData = [{id: 1},{id: 2},{id: 3}];
                    controller.okClickHandler();
                    expect(controller._listData).not.toBe(controller.listData);
                    expect(controller._listData).toEqual(controller.listData);
                });

                it('should invoke _getMultiselectViewValue and ' +
                    'set its value to value', function () {
                    mock_getMultiselectViewValue.and.returnValue('_getMultiselectViewValue');
                    controller.okClickHandler();
                    expect(controller._getMultiselectViewValue).toHaveBeenCalledWith();
                    expect(controller.value).toBe('_getMultiselectViewValue');
                });

                it('should invoke $scope.dropdownForm.$setPristine', function () {
                    controller.okClickHandler();
                    expect(controller.$scope.dropdownForm.$setPristine)
                    .toHaveBeenCalledWith();
                });

                it('should should invoke _getMultiselectState', function () {
                    controller.okClickHandler();
                    expect(controller._getMultiselectState)
                        .toHaveBeenCalledWith();
                });

                it('should invoke updateStateDelegate with the proper object', function () {
                    mock_getMultiselectState.and.returnValue('state');
                    controller.okClickHandler();
                    expect(controller.updateStateDelegate)
                    .toHaveBeenCalledWith({
                            id: controller.multiselectId,
                            immediate: false,
                            type: 'data',
                            value: 'state'
                        });
                });
            });

            describe('cancelClickHandler', function () {

                beforeEach(function () {
                    spyOn(controller.$scope.dropdownForm, '$setPristine');
                    spyOn(controller, 'hideDropdown');
                    spyOn(controller, '_setAllSelected');
                });

                it('should invoke hideDropdown', function () {
                    controller.cancelClickHandler();
                    expect(controller.hideDropdown).toHaveBeenCalledWith();
                });

                it('should invoke $scope.dropdownForm.$setPristine', function () {
                    controller.cancelClickHandler();
                    expect(controller.$scope.dropdownForm.$setPristine)
                        .toHaveBeenCalledWith();
                });

                it('should clone _listData into listData', function () {
                    controller._listData = null;
                    controller._listData = [{id: 1},{id: 2},{id: 3}];
                    controller.cancelClickHandler();
                    expect(controller.listData).not.toBe(controller._listData);
                    expect(controller.listData).toEqual(controller._listData);
                });

                it('should invoke _setAllSelected', function () {
                    controller.cancelClickHandler();
                    expect(controller._setAllSelected).toHaveBeenCalledWith();
                });
            });
        });
    });

});
