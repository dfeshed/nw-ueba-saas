xdescribe('fs-state-container.directive', function () {
    'use strict';

    var $compile;
    var $rootScope;
    var $scope;
    var $injector;
    var $location;
    var element;
    var dependencyMounter;
    var $http;

    beforeEach(module('Fortscale.shared.directives.fsStateContainer'));


    beforeEach(inject(function (_$compile_, _$rootScope_, _$injector_, _$location_, _$http_) {
        // The injector unwraps the underscores (_)
        // from around the parameter names when matching
        $compile = _$compile_;
        $rootScope = _$rootScope_;
        $scope = $rootScope.$new();
        $injector = _$injector_;
        $location = _$location_;
        $http = _$http_;
        dependencyMounter = $injector.get('dependencyMounter');

        element = angular.element('<fs-state-container></fs-state-container>');

    }));


    describe('Interface', function () {
        it('should accept state-model attribute ' +
            'and put it on the controller as _stateModel', function () {
            $scope.stateModel = {someState: ''};
            element.attr('state-model', 'stateModel');
            element.attr('container-id', 'someId');

            var el = $compile(element)($scope);

            expect(angular.equals(el.controller('fsStateContainer')
                .stateModel, $scope.stateModel)).toBe(true);
        });
    });

    describe('controller instance', function () {


        var controller;
        var StateContainerController;

        beforeEach(function () {

            $scope.stateModel = {someState: ''};
            element.attr('state-model', 'stateModel');
            element.attr('container-id', 'someId');

            var el = $compile(element)($scope);

            controller = el.controller('fsStateContainer');
            StateContainerController = controller.constructor;
        });

        describe('Private methods', function () {


            describe('_initState', function () {

                beforeEach(function () {
                    spyOn(angular, 'merge').and.returnValue($scope.stateModel);
                    controller.stateModel = null;
                    controller._initState();
                });


                it('should store null on instance\'s stateModel', function () {
                    expect(controller.stateModel).toBe(null);
                });
            });


            describe('_initChildStateRefList', function () {
                beforeEach(function () {
                    controller._childStateContainerCtrls = null;
                    controller._initChildStateRefList();
                });

                it('should create a _childStateContainerCtrls property ' +
                    'that is an empty array', function () {
                    expect(angular.equals(controller._childStateContainerCtrls, [])).toBe(true);
                });

            });

            describe('_initParenStateRef', function () {
                beforeEach(function () {
                    controller._parentStateContainerCtrl = undefined;
                    controller._initParenStateRef();
                });

                it('should create a _parentStateContainerCtrl property ' +
                    'that equals null', function () {
                    expect(controller._parentStateContainerCtrl).toBe(null);
                });

            });

            describe('_initResource', function () {

                beforeEach(function () {
                    spyOn(controller, '_validateResourceSettings');

                    spyOn(controller.interpolation, 'interpolate')
                        .and.returnValue({
                        entity: 'entity'
                    });
                    spyOn(controller.resourceFactory, 'create')
                        .and.returnValue('returned resource');

                    controller._resourceSettings = {resource: 'setting'};
                    controller.stateModel = {state: 'model'};

                });

                it('should not proceed if controller does not have a ' +
                    '_resourceSettings property', function () {
                    controller._resourceSettings = null;
                    controller._initResource();
                    expect(controller.interpolation.interpolate).not.toHaveBeenCalled();
                });

                it('should invoke _validateResourceSettings with "_initResource" ' +
                    'and resourceSettings', function () {
                    controller._initResource();
                    expect(controller._validateResourceSettings)
                        .toHaveBeenCalledWith('_initResource', controller._resourceSettings);
                });

                it('should invoke interpolation.interpolate with resourceSettings ' +
                    'and stateModel', function () {
                    controller._initResource();
                    expect(controller.interpolation.interpolate)
                        .toHaveBeenCalledWith(controller._resourceSettings, controller.stateModel);
                });

                it('should invoke resourceFactory.create ' +
                    'with the interpolated setting\'s entity', function () {
                    controller._initResource();
                    expect(controller.resourceFactory.create)
                        .toHaveBeenCalledWith('entity');
                });

                it('should place the resource on controller as _resource', function () {
                    controller._initResource();
                    expect(controller._resource).toBe('returned resource');
                });
            });

            describe('_registerParentController', function () {

                var parentController;

                beforeEach(function () {
                    controller._parentStateContainerCtrl = null;
                    parentController = new controller.constructor({}, {}, dependencyMounter);
                });

                it('should throw TypeError if stateContainerCtrl argument ' +
                    'is not StateContainerController instance',
                    function () {
                        function testFunc () {
                            controller._registerParentController({});
                        }

                        expect(testFunc).toThrowError(TypeError);
                        expect(testFunc)
                            .toThrowError('StateContainerController: ' +
                            '_registerParentController: stateContainerCtrl argument must be ' +
                            'an instance of StateContainerController.');
                    });

                it('should place controller instance on instance ' +
                    'as _parentStateContainerCtrl', function () {
                    controller._registerParentController(parentController);
                    expect(controller._parentStateContainerCtrl).toBe(parentController);
                });
            });

            describe('_updateChildStates', function () {

                var childUpdateStateSpy;
                var doNotOverride, doNotFetchData, doGetFromUrl, alterState;
                beforeEach(function () {
                    childUpdateStateSpy = jasmine.createSpy('childUpdateStateSpy',
                        function () {
                        });
                    doNotOverride = 'doNotOverride';
                    doNotFetchData = 'doNotFetchData';
                    doGetFromUrl = 'doGetFromUrl';
                    alterState = {alter: 'state'};

                    function ChildCtrl () {
                        this.updateState = childUpdateStateSpy;
                    }

                    controller._childStateContainerCtrls = [
                        new ChildCtrl(), new ChildCtrl(), new ChildCtrl()
                    ];
                });

                it('should invoke each child controller\'s updateState with its alterState, ' +
                    'doNotOverride, doNotFetchData ,doGetFromUrl and true' +
                    'if alterState was provided', function () {


                    controller
                        ._updateChildStates(doNotOverride, doNotFetchData, doGetFromUrl,
                        alterState);

                    expect(childUpdateStateSpy.calls.count()).toBe(3);
                    expect(childUpdateStateSpy)
                        .toHaveBeenCalledWith(alterState,
                        doNotOverride, doNotFetchData, doGetFromUrl, true);
                });

                it('should invoke each child controller\'s updateState with its state, ' +
                    'doNotOverride, doNotFetchData ,doGetFromUrl and false' +
                    'if alterState was not provided', function () {

                    controller
                        ._updateChildStates(doNotOverride, doNotFetchData, doGetFromUrl);

                    expect(childUpdateStateSpy.calls.count()).toBe(3);
                    expect(childUpdateStateSpy)
                        .toHaveBeenCalledWith(controller.stateModel,
                        doNotOverride, doNotFetchData, doGetFromUrl, false);
                });

            });

            describe('_ctrlUpdateChildStates', function () {

                var mockUpdateCtrlState;
                var MockChildController;
                var ctrlState;

                beforeEach(function () {

                    ctrlState = {some: 'state'};

                    mockUpdateCtrlState = jasmine.createSpy('mockUpdateCtrlState');

                    MockChildController = function () {
                        this._updateCtrlState = mockUpdateCtrlState;
                    };

                    controller._childStateContainerCtrls
                        .push(new MockChildController(), new MockChildController(),
                        new MockChildController());
                });

                it('should invoke _updateCtrlState for each child controller ' +
                    'with ctrlState and true', function () {
                    controller._ctrlUpdateChildStates(ctrlState);
                    expect(mockUpdateCtrlState.calls.count()).toBe(3);
                    expect(mockUpdateCtrlState).toHaveBeenCalledWith(ctrlState, true);

                });

            });

            describe('_isRootState', function () {

                beforeEach(function () {
                });

                it('should return false when there is a parent state', function () {
                    controller._parentStateContainerCtrl = {};

                    expect(controller._isRootState()).toBe(false);

                });

                it('should return true when there is no parent state', function () {
                    controller._parentStateContainerCtrl = null;

                    expect(controller._isRootState()).toBe(true);
                });

            });

            describe('_initDataFetchingService', function () {

                var interpolated;

                beforeEach(function () {
                    interpolated = {
                        params: 'params'
                    };

                    spyOn(controller.interpolation, 'interpolate').and.returnValue(interpolated);
                    spyOn(controller.objectUtils, 'removeNulls');
                    controller._resource = jasmine.createSpyObj('resource', [
                        'get',
                        'getList'
                    ]);
                    controller._resourceSettings = {};
                });

                it('should throw ReferenceError if _resourceSettings is not defined', function () {
                    controller._resourceSettings = undefined;
                    expect(controller._initDataFetchingService.bind(controller))
                        .toThrowError(ReferenceError, 'fs-state-container: ' +
                        ' _initDataFetchingService: _resourceSettings must be defined.');
                });

                it('should invoke interpolation.interpolate with resourceSettings ' +
                    'and stateModel', function () {

                    var resourceSettings = {resource: 'setting'};
                    var stateModel = {state: 'model'};

                    controller._resourceSettings = resourceSettings;
                    controller.stateModel = stateModel;

                    controller._initDataFetchingService();
                    expect(controller.interpolation.interpolate)
                        .toHaveBeenCalledWith(controller._resourceSettings, controller.stateModel,
                        controller._stateAdapter);
                });


                it('should invoke objectUtils.removeNulls with interpolated.params and an ' +
                    'empty string', function () {
                    var resourceSettings = {resource: 'setting'};
                    var stateModel = {state: 'model'};

                    controller._resourceSettings = resourceSettings;
                    controller.stateModel = stateModel;

                    controller._initDataFetchingService();
                    expect(controller.objectUtils.removeNulls)
                        .toHaveBeenCalledWith('params', '');
                });


                it('should invoke _resource.getList with interpolated.params ' +
                    'if interpolated.id does notexists', function () {

                    interpolated.id = 'someId';
                    interpolated.params = {some: 'params'};
                    controller._initDataFetchingService();

                    expect(controller._resource.get)
                        .toHaveBeenCalledWith(interpolated.id, interpolated.params);
                });

                it('should invoke _resource.get with interpolated.id and interpolated.params ' +
                    'if interpolated.id exists', function () {

                    delete interpolated.id;
                    interpolated.params = {some: 'params'};
                    controller._initDataFetchingService();

                    expect(controller._resource.getList)
                        .toHaveBeenCalledWith(interpolated.params);
                });


            });

            describe('_isDataRequired', function () {
                it('should return true if controller._resource is truthy', function () {
                    controller._resource = 'some resource';
                    expect(controller._isDataRequired()).toBe(true);
                });
            });

            describe('_fetchData', function () {
                var successFunc;
                var data = 'some data';

                beforeEach(function () {
                    successFunc = function success (cb) {
                        cb(data);
                        return {
                            then: successFunc,
                            catch: angular.noop
                        };
                    };

                    spyOn(controller, '_initDataFetchingService').and.returnValue({
                        then: successFunc
                    });
                });

                it('should invoke _initDataFetchingService', function () {
                    controller._fetchData();
                    expect(controller._initDataFetchingService)
                        .toHaveBeenCalledWith();
                });

                it('should put the data on the instance as dataModel', function () {
                    controller._fetchData();
                    expect(controller.dataModel).toBe(data);
                });
            });

            describe('_startInitialStateUpdate', function () {

                var mockIsDataRequired;
                beforeEach(function () {
                    spyOn(controller, '_updateChildStates');
                    mockIsDataRequired = spyOn(controller, '_isDataRequired');
                    spyOn(controller, '_fetchData');

                });
                it('should invoke _updateChildStates with true, false, true', function () {
                    controller._startInitialStateUpdate();
                    expect(controller._updateChildStates)
                        .toHaveBeenCalledWith(true, false, true);
                });

                it('should invoke _isDataRequired', function () {
                    controller._startInitialStateUpdate();
                    expect(controller._isDataRequired).toHaveBeenCalledWith();
                });


                it('should not invoke _fetchData if _isDataRequired returns false ' +
                    'or _isStateInitialized is true', function () {
                    mockIsDataRequired.and.returnValue(true);
                    controller._isStateInitialized = true;
                    controller._startInitialStateUpdate();
                    expect(controller._fetchData).not.toHaveBeenCalled();

                    mockIsDataRequired.and.returnValue(false);
                    controller._isStateInitialized = false;
                    controller._startInitialStateUpdate();
                    expect(controller._fetchData).not.toHaveBeenCalled();
                });

                it('should invoke _fetchData if _isDataRequired returns true ' +
                    'and _isStateInitialized is false', function () {
                    mockIsDataRequired.and.returnValue(true);
                    controller._isStateInitialized = false;
                    controller._startInitialStateUpdate();
                    expect(controller._fetchData).toHaveBeenCalledWith();
                });


                it('should invoke urlStateManager.getStateByContainerId ' +
                    'with containerId if _isStateInitialized is false', function () {

                    spyOn(controller.urlStateManager, 'getStateByContainerId');
                    controller._isStateInitialized = false;

                    controller.containerId = 'someId';
                    controller._startInitialStateUpdate();

                    expect(controller.urlStateManager.getStateByContainerId)
                        .toHaveBeenCalledWith(controller.containerId);
                });

                it('should not invoke urlStateManager.getStateByContainerId ' +
                    'with containerId if _isStateInitialized is true', function () {

                    spyOn(controller.urlStateManager, 'getStateByContainerId');
                    controller._isStateInitialized = true;

                    controller.containerId = 'someId';
                    controller._startInitialStateUpdate();

                    expect(controller.urlStateManager.getStateByContainerId)
                        .not.toHaveBeenCalled();
                });

                it('should invoke angular.merge with stateModel and ' +
                    'converted urlStateManager.getStateByContainerId state if ' +
                    '_isStateInitialized is false', function () {
                    var newState = {some: 'state'};
                    spyOn(controller.urlStateManager, 'getStateByContainerId')
                        .and.returnValue(newState);
                    spyOn(angular, 'merge');

                    controller.containerId = 'someId';
                    controller.stateModel = {existing: 'state'};
                    controller._isStateInitialized = false;

                    controller._startInitialStateUpdate();

                    expect(angular.merge)
                        .toHaveBeenCalledWith({existing: 'state'}, {some: {value: 'state'}});
                });

                it('should not invoke angular.merge if _isStateInitialized is true', function () {
                    controller._isStateInitialized = true;

                    spyOn(angular, 'merge');


                    controller._startInitialStateUpdate();

                    expect(angular.merge)
                        .not.toHaveBeenCalled();
                });

            });

            describe('_validateCtrlState', function () {
                var errorMsgStart;
                var angularIsDefined, angularIsString, angularIsObject;
                var ctrlState;
                var caller;

                beforeEach(function () {
                    angularIsDefined = {is: 'defined'};
                    angularIsString = {is: 'string'};

                    ctrlState = {type: 'someType', id: 'someId'};
                    caller = 'someMethod';
                    errorMsgStart = controller._validateStartMessage + caller + ': ';


                    spyOn(controller, 'assert');
                    spyOn(angular, 'isDefined').and.returnValue(angularIsDefined);
                    spyOn(angular, 'isObject').and.returnValue(angularIsObject);
                    spyOn(angular, 'isString').and.returnValue(angularIsString);
                });

                it('should invoke angular.isDefined with ctrlState', function () {
                    controller._validateCtrlState(caller, ctrlState);
                    expect(angular.isDefined).toHaveBeenCalledWith(ctrlState);
                });

                it('should should invoke assert ' +
                    'with angular is defined, a proper message and error type',
                    function () {
                        controller._validateCtrlState(caller, ctrlState);
                        expect(controller.assert).toHaveBeenCalledWith(
                            angularIsDefined,
                            errorMsgStart + 'ctrlState argument must be provided.',
                            ReferenceError
                        );
                    });

                it('should invoke angular.isObject with ctrlState', function () {
                    controller._validateCtrlState(caller, ctrlState);
                    expect(angular.isObject).toHaveBeenCalledWith(ctrlState);
                });

                it('should should invoke assert ' +
                    'with angular is defined, a proper message and error type',
                    function () {
                        controller._validateCtrlState(caller, ctrlState);
                        expect(controller.assert).toHaveBeenCalledWith(
                            angularIsObject,
                            errorMsgStart + 'ctrlState argument must be an object.',
                            TypeError
                        );
                    });

                it('should invoke angular.isDefined with ctrlState.id', function () {
                    controller._validateCtrlState(caller, ctrlState);
                    expect(angular.isDefined).toHaveBeenCalledWith(ctrlState.id);
                });

                it('should should invoke assert ' +
                    'with angular is defined, a proper message and error type',
                    function () {
                        controller._validateCtrlState(caller, ctrlState);
                        expect(controller.assert).toHaveBeenCalledWith(
                            angularIsDefined,
                            errorMsgStart + 'ctrlState argument must have an "id" property.',
                            ReferenceError
                        );
                    });

                it('should invoke angular.isString with ctrlState.id', function () {
                    controller._validateCtrlState(caller, ctrlState);
                    expect(angular.isString).toHaveBeenCalledWith(ctrlState.id);
                });

                it('should should invoke assert ' +
                    'with angular is defined, a proper message and error type',
                    function () {
                        controller._validateCtrlState(caller, ctrlState);
                        expect(controller.assert).toHaveBeenCalledWith(
                            angularIsString,
                            errorMsgStart + 'ctrlState.id must be a string.',
                            TypeError
                        );
                    });

                it('should should invoke ' +
                    'assert with condition, a proper message and error type',
                    function () {
                        controller._validateCtrlState(caller, ctrlState);
                        expect(controller.assert).toHaveBeenCalledWith(
                            true, // trlState.id !== ''
                            errorMsgStart + 'ctrlState.id must not be an empty string.',
                            RangeError
                        );
                    });

                it('should invoke angular.isDefined with ctrlState.type', function () {
                    controller._validateCtrlState(caller, ctrlState);
                    expect(angular.isDefined).toHaveBeenCalledWith(ctrlState.type);
                });

                it('should should invoke assert ' +
                    'with angular is defined, a proper message and error type',
                    function () {
                        controller._validateCtrlState(caller, ctrlState);
                        expect(controller.assert).toHaveBeenCalledWith(
                            angularIsDefined,
                            errorMsgStart + 'ctrlState argument must have an "type" property.',
                            ReferenceError
                        );
                    });

                it('should invoke angular.isString with ctrlState.type', function () {
                    controller._validateCtrlState(caller, ctrlState);
                    expect(angular.isString).toHaveBeenCalledWith(ctrlState.type);
                });

                it('should should invoke assert ' +
                    'with angular is defined, a proper message and error type',
                    function () {
                        controller._validateCtrlState(caller, ctrlState);
                        expect(controller.assert).toHaveBeenCalledWith(
                            angularIsString,
                            errorMsgStart + 'ctrlState.type must be a string.',
                            TypeError
                        );
                    });

                it('should should invoke assert ' +
                    'with condition, a proper message and error type',
                    function () {

                        // Make sure ctrlType is valid
                        ctrlState.type = Object.keys(controller.controlTypes)[0];

                        controller._validateCtrlState(caller, ctrlState);
                        expect(controller.assert).toHaveBeenCalledWith(
                            true, // controlTypes.indexOf(ctrlState.type.toUpperCase()) > -1
                            errorMsgStart + 'ctrlState.type must be a valid type: ' +
                            Object.keys(controller.controlTypes).join(', ') +
                            ', and it is "' + ctrlState.type.toUpperCase() + '"', RangeError
                        );
                    });

            });

            describe('_populateActionStateObject', function () {
                var targetState, state, stateProperty;

                beforeEach(function () {
                    targetState = {};
                    state = {control1: {preValue: 'someValue'}};
                    stateProperty = 'control1';
                });

                it('should set the value of state\'s property\'s preValue ' +
                    'to targetState\'s property value', function () {
                    controller._populateActionStateObject(targetState, state, stateProperty);

                    expect(targetState.control1.value).toBe(state.control1.preValue);
                });
            });

            describe('_deletePreValue', function () {
                it('should delete the preValue from a property on the stateModel', function () {
                    controller.stateModel = {control1: {preValue: 'valueTBD'}};

                    controller._deletePreValue('control1');

                    expect(controller.stateModel.control1).toBeDefined();
                    expect(controller.stateModel.control1.preValue).not.toBeDefined();
                });
            });

            describe('_applyTargetStateToUrl', function () {

                var targetState;

                beforeEach(function () {
                    targetState = {
                        control1: {value: 'control1 value'},
                        control2: {value: 'control2 value'}
                    };

                    spyOn(controller.urlStateManager, 'updateUrlStateParameters');
                });

                it('should invoke urlStateManager.updateUrlStateParameters ' +
                    'if urlTargetState has any properties', function () {
                    controller._applyTargetStateToUrl(targetState);

                    expect(controller.urlStateManager.updateUrlStateParameters).toHaveBeenCalled();
                });

                it('should not invoke urlStateManager.updateUrlStateParameters ' +
                    'if urlTargetState has no properties', function () {
                    controller._applyTargetStateToUrl({});

                    expect(controller.urlStateManager.updateUrlStateParameters)
                        .not.toHaveBeenCalled();
                });

                it('should convert targetState scheme to urlStateScheme', function () {
                    controller.containerId = 'someId';
                    controller._applyTargetStateToUrl(targetState);

                    expect(controller.urlStateManager.updateUrlStateParameters)
                        .toHaveBeenCalledWith(controller.containerId,
                        {
                            control1: 'control1 value',
                            control2: 'control2 value'
                        });
                });


            });

            describe('_validateResourceSettings', function () {

                var resourceSettings;
                var testFunc;

                beforeEach(function () {
                    resourceSettings = {};
                    testFunc = function () {
                        controller._validateResourceSettings('caller', resourceSettings);
                    };
                });

                it('should throw ReferenceError if resourceSettings.entity ' +
                    'is not defined', function () {

                    expect(testFunc).toThrowError(ReferenceError, 'fs-state-container: caller: ' +
                        'resourceSettings must have an "entity" property.');
                });

                it('should throw TypeError if resourceSettings.entity ' +
                    'is not a string', function () {

                    resourceSettings.entity = 123;

                    expect(testFunc).toThrowError(TypeError, 'fs-state-container: caller: ' +
                        'resourceSettings.entity must be a string.');
                });

                it('should throw RangeError if resourceSettings.entity ' +
                    'is an empty string', function () {

                    resourceSettings.entity = '';

                    expect(testFunc).toThrowError(RangeError, 'fs-state-container: caller: ' +
                        'resourceSettings.entity must not be an empty string.');
                });

                it('should throw TypeError if resourceSettings.id is defined and  ' +
                    'is not a string', function () {

                    resourceSettings.entity = 'entity';
                    resourceSettings.id = 123;

                    expect(testFunc).toThrowError(TypeError, 'fs-state-container: caller: ' +
                        'resourceSettings.id must be a string.');
                });

                it('should throw RangeError if resourceSettings.entity ' +
                    'is an empty string', function () {

                    resourceSettings.entity = 'entity';
                    resourceSettings.id = '';

                    expect(testFunc).toThrowError(RangeError, 'fs-state-container: caller: ' +
                        'resourceSettings.id must not be an empty string.');
                });
            });

            describe('_init', function () {
                var mockScope, mockElement;
                beforeEach(function () {
                    spyOn(controller, '_initState');
                    spyOn(controller, '_initChildStateRefList');
                    spyOn(controller, '_initParenStateRef');
                    spyOn(dependencyMounter, 'mountOnConstructor');
                    mockScope = 'someScope';
                    mockElement = 'someElemenet';
                    controller.$scope = null;
                    controller.$element = null;
                    controller._init(mockScope, mockElement, dependencyMounter);
                });

                it('should invoke _initState', function () {
                    expect(controller._initState).toHaveBeenCalledWith();
                });

                it('should invoke _initChildStateRefList', function () {
                    expect(controller._initChildStateRefList).toHaveBeenCalledWith();
                });

                it('should invoke _initParenStateRef', function () {
                    expect(controller._initParenStateRef).toHaveBeenCalledWith();
                });

                it('should place $scope argument on instance as $scope', function () {
                    expect(controller.$scope).toBe(mockScope);
                });

                it('should place $element argument on instance as $element', function () {
                    expect(controller.$element).toBe(mockElement);
                });

                it('should invoke dependencyMounter.mountOnConstructor', function () {
                    expect(dependencyMounter.mountOnConstructor).toHaveBeenCalledWith(
                        controller.constructor, ['$parse', 'assert',
                            'controlTypes', 'urlStateManager', '$log', 'resourceFactory',
                            'interpolation', 'objectUtils']);
                });
            });
        });

        describe('Public methods', function () {
            var controller;

            beforeEach(function () {
                spyOn(StateContainerController.prototype, '_init');
                controller = new StateContainerController();
            });

            describe('updateState', function () {

                var mockIsDataRequired;

                beforeEach(function () {
                    spyOn(controller, '_updateChildStates');
                    mockIsDataRequired = spyOn(controller, '_isDataRequired');
                    spyOn(controller, '_fetchData');
                });


                it('should should merge and override all properties ' +
                    'if doNotOverride is falsy', function () {
                    controller.stateModel = {propA: 'a', propB: 'b'};
                    var newState = {propB: 'notB', propC: 'c'};

                    controller.updateState(newState);

                    expect(angular.equals(controller.stateModel,
                        {propA: 'a', propB: 'notB', propC: 'c'}))
                        .toBe(true);
                });

                it('should merge without override if do not override is truthy', function () {
                    controller.stateModel = {propA: 'a', propB: 'b'};
                    var newState = {propB: 'notB', propC: 'c'};

                    controller.updateState(newState, true);

                    expect(angular.equals(controller.stateModel,
                        {propA: 'a', propB: 'b', propC: 'c'}))
                        .toBe(true);
                });

                it('should invoke _updateChildStates with doNotOverride, doNotFetchData, and ' +
                    'doGetFromUrl', function () {
                    var doNotOverride = 'doNotOverride';
                    var doNotFetchData = 'doNotFetchData';
                    var doGetFromUrl = 'doGetFromUrl';
                    controller.updateState({}, doNotOverride, doNotFetchData, doGetFromUrl);

                    expect(controller._updateChildStates)
                        .toHaveBeenCalledWith(doNotOverride, doNotFetchData, doGetFromUrl);
                });

                it('should invoke _isDataRequired', function () {
                    controller.updateState({});
                    expect(controller._isDataRequired).toHaveBeenCalledWith();
                });

                it('should invoke _fetchData if _isDataRequired returns true', function () {
                    mockIsDataRequired.and.returnValue(true);
                    controller.updateState({});
                    expect(controller._fetchData).toHaveBeenCalledWith();
                });

                it('should not invoke _fetchData ' +
                    'if _isDataRequired returns false', function () {
                    mockIsDataRequired.and.returnValue(false);
                    controller.updateState({});
                    expect(controller._fetchData).not.toHaveBeenCalled();
                });
            });

            describe('fetchStateById', function () {

                it('should return null if stateModel[controlId] is undefined', function () {
                    controller.stateModel = {};
                    expect(controller.fetchStateById('someId')).toBe(null);
                });

                it('should return the value of property.value from state object', function () {
                    controller.stateModel = {someControl: {value: 'someValue'}};
                    expect(controller.fetchStateById('someControl')).toBe('someValue');
                });

            });

            describe('fetchStateByPath', function () {

                it('should return the value of the path in the stateModel', function () {
                    controller.stateModel = {
                        root: {
                            firstNesting: {
                                secondNesting: {
                                    value: 'value'
                                }
                            }
                        }
                    };

                    expect(controller.fetchStateByPath('root.firstNesting.secondNesting.value'))
                        .toBe('value');
                });
            });


            describe('updateCtrlState', function () {

                var ctrlState;

                var mockUpdateStateByPath;
                var mockIsDataRequired;
                var invokedByParent;

                beforeEach(function () {
                    spyOn(controller, '_validateCtrlState');
                    mockUpdateStateByPath = spyOn(controller, 'updateStateByPath');
                    spyOn(controller, '_ctrlUpdateChildStates');

                    mockIsDataRequired = spyOn(controller, '_isDataRequired');
                    spyOn(controller, '_fetchData');

                    ctrlState = {};
                });


                it('should invoke _updateCtrlState with ctrlState', function () {
                    var ctrlState = {some: 'state'};
                    spyOn(controller, '_updateCtrlState');
                    controller.updateCtrlState(ctrlState);
                    expect(controller._updateCtrlState).toHaveBeenCalledWith(ctrlState);
                });

                it('should invoke _validateCtrlState ' +
                    'if invokedByParent argument is false', function () {
                    invokedByParent = false;

                    controller._updateCtrlState(ctrlState, invokedByParent);

                    expect(controller._validateCtrlState)
                        .toHaveBeenCalledWith('updateCtrlState', ctrlState);
                });

                it('should not invoke _validateCtrlState ' +
                    'if invokedByParent argument is true', function () {
                    invokedByParent = true;

                    controller._updateCtrlState(ctrlState, invokedByParent);

                    expect(controller._validateCtrlState).not.toHaveBeenCalled();
                });

                it('should invoke updateStateByPath ' +
                    'with the ctrlState.id and .value or .preValue ' +
                    'and ctrlState.value', function () {
                    var path;
                    controller.containerId = 'someContainerId';


                    ctrlState.id = 'someId';
                    ctrlState.immediate = true;
                    ctrlState.value = 'someValue';

                    path = 'someId.value';
                    controller.updateCtrlState(ctrlState);
                    expect(controller.updateStateByPath)
                        .toHaveBeenCalledWith(path, ctrlState.value);

                    ctrlState.immediate = false;
                    path = 'someId.preValue';
                    controller.updateCtrlState(ctrlState);
                    expect(controller.updateStateByPath)
                        .toHaveBeenCalledWith(path, ctrlState.value);
                });

                it('should not invoke _ctrlUpdateChildStates ' +
                    'if ctrlState.immediate is falsy', function () {
                    invokedByParent = false;
                    ctrlState.immediate = false;
                    controller._updateCtrlState(ctrlState, invokedByParent);
                    expect(controller._ctrlUpdateChildStates).not.toHaveBeenCalled();
                });

                it('should update the state so "prevalue" will be "postvalue" ' +
                    '[integration test]', function () {
                    controller.stateModel = {control: {value: 'prevalue'}};

                    invokedByParent = true;
                    ctrlState.id = 'control';
                    ctrlState.value = 'postvalue';
                    ctrlState.immediate = true;
                    ctrlState.type = 'DATA';

                    mockUpdateStateByPath.and.callThrough();

                    controller.$scope = $rootScope.$new();
                    controller._updateCtrlState(ctrlState, invokedByParent);

                    expect(controller.stateModel.control.value).toBe('postvalue');
                });

                it('should invoke _fetchData if isDataRequired is true ' +
                    'and ctrlState.type is data', function () {
                    controller.stateModel = {control: {value: 'prevalue'}};
                    controller.containerId = 'someContainerId';


                    ctrlState.id = 'control';
                    ctrlState.value = 'postvalue';
                    ctrlState.immediate = true;
                    ctrlState.type = 'DATA';

                    mockIsDataRequired.and.returnValue(true);

                    controller.updateCtrlState(ctrlState);

                    expect(controller._fetchData).toHaveBeenCalledWith();
                });
            });

            describe('updateStateByPath', function () {

                it('should return the value of the path in the stateModel', function () {
                    controller.stateModel = {
                        root: {
                            firstNesting: {
                                secondNesting: {
                                    value: 'value'
                                }
                            }
                        }
                    };

                    controller.$scope = $rootScope.$new();
                    controller.updateStateByPath(
                        'root.firstNesting.secondNesting.value',
                        {newValue: 'newValue'}
                    );
                    expect(controller.stateModel.root.firstNesting.secondNesting.value.newValue)
                        .toBe('newValue');
                });
            });

            describe('registerChildController', function () {

                var childController;

                beforeEach(function () {
                    controller._childStateContainerCtrls = [];
                    childController = new controller.constructor({}, {}, dependencyMounter);
                });

                it('should throw TypeError if stateContainerCtrl argument ' +
                    'is not StateContainerController instance',
                    function () {
                        function testFunc () {
                            controller.registerChildController({});
                        }

                        expect(testFunc).toThrowError(TypeError);
                        expect(testFunc)
                            .toThrowError('StateContainerController: ' +
                            '_registerChildController: stateContainerCtrl argument ' +
                            'must be an instance of StateContainerController.');
                    });

                it('should add the instance to _childStateContainerCtrls', function () {
                    controller.registerChildController(childController);
                    expect(controller._childStateContainerCtrls[0]).toBe(childController);
                });

                it('should not add an instance if it is already in the list', function () {
                    controller._childStateContainerCtrls.push(childController);
                    controller.registerChildController(childController);
                    expect(controller._childStateContainerCtrls.length).toBe(1);
                });
            });

            describe('unregisterChildController', function () {
                var objA, objB, objC, objD;

                beforeEach(function () {
                    objA = {obj: 'a'};
                    objB = {obj: 'b'};
                    objC = {obj: 'c'};
                    objD = {obj: 'd'};

                    controller._childStateContainerCtrls = [objA, objB, objC];
                });

                it('should return false if childStateContainerCtrl ' +
                    'is not in the list', function () {
                    expect(controller.unregisterChildController(objD)).toBe(false);
                });

                it('should not change _childStateContainerCtrls ' +
                    'if childStateContainerCtrl is not in the list',
                    function () {
                        controller.unregisterChildController(objD);
                        expect(angular.equals(controller._childStateContainerCtrls,
                            [objA, objB, objC]))
                            .toBe(true);
                    });

                it('should splice out a controller if its in the list', function () {
                    controller.unregisterChildController(objB);
                    expect(angular.equals(controller._childStateContainerCtrls, [objA, objC]))
                        .toBe(true);
                });
            });

            describe('applyPreState', function () {

                var mockApplyPreState;
                var mockIsDefined;
                var mockPopulateActionStateObject;
                var actionState;

                beforeEach(function () {
                    mockApplyPreState = spyOn(controller, '_applyPreState').and.callThrough();
                    mockIsDefined = spyOn(angular, 'isDefined').and.callThrough();
                    mockPopulateActionStateObject = spyOn(controller, '_populateActionStateObject');
                    spyOn(controller, '_deletePreValue');
                    spyOn(controller, '_applyTargetStateToUrl');
                    spyOn(controller, 'updateState');

                    controller.stateModel = {
                        control1: {
                            preValue: 'some prevalue for control 1'
                        },
                        control2: {
                            value: 'no prevalue'
                        },
                        control3: {
                            preValue: 'some prevalue for control 3'
                        }
                    };

                    actionState = {};

                });

                it('should invoke _applyPreState', function () {
                    mockApplyPreState.and.returnValue();

                    controller.applyPreState();
                    expect(controller._applyPreState).toHaveBeenCalledWith();
                });

                it('should invoke angular.isDefined for each property on stateModel', function () {

                    controller.applyPreState();

                    expect(angular.isDefined.calls.count()).toBe(3);
                });

                it('should invoke _populateActionStateObject for each property ' +
                    'that has a preValue defined', function () {

                    controller.applyPreState();

                    expect(controller._populateActionStateObject)
                        .toHaveBeenCalledWith(actionState, controller.stateModel, 'control1');
                    expect(controller._populateActionStateObject)
                        .toHaveBeenCalledWith(actionState, controller.stateModel, 'control3');

                });

                it('should invoke _deletePreValue for each property ' +
                    'that has a preValue defined', function () {

                    controller.applyPreState();

                    expect(controller._deletePreValue)
                        .toHaveBeenCalledWith('control1');
                    expect(controller._deletePreValue)
                        .toHaveBeenCalledWith('control3');

                });

                it('should not invoke _applyTargetStateToUrl ' +
                    'if no properties have preValue', function () {
                    mockIsDefined.and.returnValue(false);

                    controller.applyPreState();

                    expect(controller._applyTargetStateToUrl).not.toHaveBeenCalled();
                });

                it('should invoke _applyTargetStateToUrl when any of the properties ' +
                    'have preValue defined', function () {
                    mockPopulateActionStateObject.and.callFake(
                        function (actionState, state, stateProperty) {
                            actionState[stateProperty] = {
                                value: state[stateProperty].preValue
                            };
                        }
                    );

                    controller.applyPreState();

                    expect(controller._applyTargetStateToUrl)
                        .toHaveBeenCalledWith({
                            control1: {
                                value: 'some prevalue for control 1'
                            },
                            control3: {
                                value: 'some prevalue for control 3'
                            }
                        });

                });

                it('should not invoke updateState if no properties have preValue', function () {
                    mockIsDefined.and.returnValue(false);

                    controller.applyPreState();

                    expect(controller.updateState).not.toHaveBeenCalled();
                });

                it('should invoke updateState when any of the properties ' +
                    'have preValue defined', function () {
                    mockIsDefined.and.returnValue(true);

                    controller.applyPreState();

                    expect(controller.updateState)
                        .toHaveBeenCalledWith({}, false, false, false, true);
                });


            });

            describe('refreshData', function () {

                var mockInitDataFetchingService;
                var mockSuccessFn, mockErrorFn;
                var invokeSuccess, invokeError;
                var mockData;
                var mockError;

                beforeEach(function () {
                    invokeSuccess = false;
                    invokeError = false;
                    mockData = 'someData';
                    mockError = 'someError';

                    mockErrorFn = jasmine.createSpy('mockErrorFn').and.callFake(
                        function (cb) {
                            if (invokeError) {
                                cb(mockError);
                            }
                        }
                    );
                    mockSuccessFn = jasmine.createSpy('mockSuccessFn').and.callFake(
                        function (cb) {
                            if (invokeSuccess) {
                                cb(mockData);
                            }

                            return {
                                then: mockSuccessFn,
                                catch: mockErrorFn
                            };
                        }
                    );

                    mockInitDataFetchingService = spyOn(controller, '_initDataFetchingService')
                        .and.returnValue({
                            then: mockSuccessFn
                        });
                });


                it('should invoke _fetchData', function () {
                    spyOn(controller, '_fetchData');
                    controller.refreshData();
                    expect(controller._fetchData).toHaveBeenCalledWith();
                });

                it('should set errorModel to null and isLoading to true', function () {
                    controller.errorModel = 'someValue';
                    controller.isLoading = 'someValue';

                    controller._fetchData();

                    expect(controller.errorModel).toBe(null);
                    expect(controller.isLoading).toBe(true);
                });

                it('should should set isLoading to false if request is successful', function () {
                    controller.isLoading = true;
                    invokeSuccess = true;

                    controller._fetchData();

                    expect(controller.isLoading).toBe(false);
                });

                it('should should set data to dataModel if request is successful', function () {
                    controller.dataModel = null;
                    invokeSuccess = true;

                    controller._fetchData();

                    expect(controller.dataModel).toBe(mockData);
                });

                it('should set isLoading to false if request failed', function () {
                    controller.isLoading = true;
                    invokeError = true;

                    controller._fetchData();

                    expect(controller.isLoading).toBe(false);
                });

                it('should set dataModel to null if request failed', function () {
                    controller.dataModel = mockData;
                    invokeError = true;

                    controller._fetchData();

                    expect(controller.dataModel).toBe(null);
                });

                it('should set the returned error to errorModel if request failed', function () {
                    controller.errorModel = null;
                    invokeError = true;

                    controller._fetchData();

                    expect(controller.errorModel).toBe(mockError);
                });

                it('should set a custom error if returned error is null ' +
                    'and request failed', function () {
                    controller.errorModel = null;
                    mockError = null;

                    invokeError = true;

                    controller._fetchData();

                    expect(angular.equals(controller.errorModel, {
                        message: 'Connection error'
                    })).toBe(true);

                });


            });
        });
    });

    describe('Integration', function () {

        var controller;
        var element;
        var childElement;
        var childElement2;
        var grandchildElement;
        var childController;
        var childController2;
        var grandchildController;


        describe('controller instance', function () {
            beforeEach(function () {
                element = angular.element('<fs-state-container id="state-container">' +
                    '</fs-state-container>');
                $scope.stateModel = {someState: ''};
                element.attr('state-model', 'stateModel');
                element.attr('container_id', 'someId');

                element = $compile(element)($scope);

                controller = element.controller('fsStateContainer');

            });

            it('should have stateModel property that equals $scope.stateModel', function () {
                expect(angular.equals(controller.stateModel, $scope.stateModel)).toBe(true);
            });


            it('should have a $element property that equals the element', function () {

                // There is no way to compare original element with what's on the controller
                // controller.$element and element are not the same, but for this test's purpose
                // they are close enough. To verify, the outer html is compared.
                expect(controller.$element[0].outerHTML).toBe(element[0].outerHTML);
            });

            it('should have a _childStateContainerCtrls property ' +
                'that is an empty array', function () {
                expect(angular.equals(controller._childStateContainerCtrls, [])).toBe(true);
            });

            it('should have a _parentStateContainerCtrl property ' +
                'that equals null', function () {
                expect(controller._parentStateContainerCtrl).toBe(null);
            });

            it('should have $parse on the prototype', function () {
                expect(controller.constructor.prototype.$parse).toBeDefined();
            });
        });

        describe('directive', function () {
            beforeEach(function () {

                // Setup elements
                element = angular.element('<fs-state-container ' +
                    'state-model="parentState" container-id="parent"></fs-state-container>');
                childElement = angular.element('<fs-state-container ' +
                    'child-container state-model="childState" container-id="child1">' +
                    '</fs-state-container>');
                childElement2 = angular.element('<fs-state-container ' +
                    'child-container2 state-model="childState2" container-id="child2">' +
                    '</fs-state-container>');
                grandchildElement =
                    angular.element('<fs-state-container ' +
                        'grandchild-container state-model="grandchildState" ' +
                        'container-id="grandchild"></fs-state-container>');

                // Setup elements hierarchy
                element.append(childElement);
                element.append(childElement2);
                childElement2.append(grandchildElement);

                // Setup states
                $scope.parentState = {
                    parent: 'parent', common: 'parent',
                    control: {value: 'prevalue'}
                };
                $scope.childState = {child: 'child', common: 'child'};
                $scope.childState2 = {child2: 'child2', common: 'child2'};
                $scope.grandchildState = {grandchild: 'grandchild', common: 'grandchild'};

                // Compile element
                element = $compile(element)($scope);
                childElement = element.find('[child-container]');

                // Extract controllers
                controller = element.controller('fsStateContainer');
                childController = childElement.controller('fsStateContainer');
                childController2 = childElement2.controller('fsStateContainer');
                grandchildController = grandchildElement.controller('fsStateContainer');

            });

            describe('parent state', function () {
                it('should have the proper state', function () {
                    expect(angular.equals(controller.stateModel, $scope.parentState))
                        .toBe(true);
                });

            });

            describe('child controller', function () {
                it('should register the parent controller', function () {
                    expect(childController._parentStateContainerCtrl).toBe(controller);
                });
            });

            describe('parent controller', function () {
                it('should not have any parent registered', function () {
                    expect(controller._parentStateContainerCtrl).toBe(null);
                });

                it('should have the child controller registered', function () {
                    expect(controller._childStateContainerCtrls[0]).toBe(childController);
                });
            });

            describe('destroy', function () {
                beforeEach(function () {
                    spyOn(controller, 'unregisterChildController');
                });

                describe('scope destroy', function () {


                    it('should invoke unregisterChildController ' +
                        'on the parent with the child controller',
                        function () {
                            childController.$scope.$destroy();
                            expect(controller.unregisterChildController)
                                .toHaveBeenCalledWith(childController);
                        });
                });

                describe('element destroy (remove)', function () {

                    it('should invoke unregisterChildController ' +
                        'on the parent with the child controller',
                        function () {
                            childController.$element.remove();

                            expect(controller.unregisterChildController)
                                .toHaveBeenCalledWith(childController);
                        });

                });
            });

            describe('initStateUpdate', function () {
                it('should cause state permeation to child state ' +
                    'without override', function () {
                    expect(angular.equals(childController.stateModel,
                        {
                            parent: 'parent',
                            child: 'child',
                            common: 'child',
                            control: {value: 'prevalue'}
                        })).toBe(true);

                    expect(angular.equals(childController2.stateModel,
                        {
                            parent: 'parent',
                            child2: 'child2',
                            common: 'child2',
                            control: {value: 'prevalue'}
                        })).toBe(true);

                    expect(angular.equals(grandchildController.stateModel,
                        {
                            parent: 'parent',
                            child2: 'child2',
                            grandchild: 'grandchild',
                            common: 'grandchild',
                            control: {value: 'prevalue'}
                        })).toBe(true);
                });
            });

            describe('update with state from url', function () {
                beforeEach(function () {


                    // Setup elements
                    element = angular.element('<fs-state-container ' +
                        'state-model="parentState" container-id="parent">' +
                        '</fs-state-container>');
                    childElement = angular.element('<fs-state-container ' +
                        'child-container state-model="childState" container-id="child1">' +
                        '</fs-state-container>');
                    childElement2 = angular.element('<fs-state-container ' +
                        'child-container2 state-model="childState2" container-id="child2">' +
                        '</fs-state-container>');
                    grandchildElement =
                        angular.element('<fs-state-container ' +
                            'grandchild-container state-model="grandchildState" ' +
                            'container-id="grandchild"></fs-state-container>');

                    // Setup elements hierarchy
                    element.append(childElement);
                    element.append(childElement2);
                    childElement2.append(grandchildElement);

                    // Setup states
                    $scope.parentState = {
                        parent: 'parent', common: 'parent',
                        control: {value: 'prevalue'}
                    };
                    $scope.childState = {child: 'child', common: 'child'};
                    $scope.childState2 = {child2: 'child2', common: 'child2'};
                    $scope.grandchildState = {grandchild: 'grandchild', common: 'grandchild'};

                    // We're using this spy to fake urlStateManager.getStateByContainerId.
                    // There is no other way to fake the $location.search object
                    // But we want to return {control: {value: 'urlValue'}} only if
                    // containerId is container2
                    spyOn(controller.constructor.prototype.urlStateManager,
                        'getStateByContainerId').and.callFake(function (containerId) {
                            if (containerId === 'child2') {
                                return {control: 'urlValue'};
                            } else {
                                return null;
                            }
                        });
                    // Compile element
                    element = $compile(element)($scope);
                    childElement = element.find('[child-container]');

                    // Extract controllers
                    controller = element.controller('fsStateContainer');
                    childController = childElement.controller('fsStateContainer');
                    childController2 = childElement2.controller('fsStateContainer');
                    grandchildController = grandchildElement.controller('fsStateContainer');

                });

                it('should cause state permeation to child state without override ' +
                    'with state from ur', function () {
                    expect(angular.equals(childController.stateModel,
                        {
                            parent: 'parent',
                            child: 'child',
                            common: 'child',
                            control: {value: 'prevalue'}
                        })).toBe(true);

                    expect(angular.equals(childController2.stateModel,
                        {
                            parent: 'parent',
                            child2: 'child2',
                            common: 'child2',
                            control: {value: 'urlValue'}
                        })).toBe(true);

                    expect(angular.equals(grandchildController.stateModel,
                        {
                            parent: 'parent',
                            child2: 'child2',
                            grandchild: 'grandchild',
                            common: 'grandchild',
                            control: {value: 'urlValue'}
                        })).toBe(true);
                });
            });

            describe('query', function () {

                var parentElement;

                beforeEach(function () {

                    parentElement = angular.element('<fs-state-container ' +
                        'container-id="parent"></fs-state-container>');

                    element = angular.element('<fs-state-container ' +
                        'state-model="stateModel" container-id="child"' +
                        'query-template="overview.queryTemplate"></fs-state-container>');

                    parentElement.append(element);

                    $scope = $rootScope.$new();

                    angular.extend($scope, {
                        overview: {
                            queryTemplate: '{"some": "{{control.value}}"}'
                        },
                        stateModel: {control: {value: 'query'}}
                    });


                    parentElement = $compile(parentElement)($scope);
                    controller = element.controller('fsStateContainer');

                });


                it('should place on controller the property _queryValue ' +
                    'that equals "{some: "query"}"', function () {
                    expect(controller._queryTemplate).toBe('{"some": "{{control.value}}"}');
                });

            });

            describe('updateCtrlState', function () {
                var ctrlState;

                beforeEach(function () {
                    ctrlState = {
                        id: 'control',
                        type: 'data',
                        value: 'postvalue',
                        immediate: true
                    };
                });

                it('should update the grandchild state from parent state', function () {
                    controller.updateCtrlState(ctrlState);
                    expect(grandchildController.stateModel.control.value).toBe('postvalue');
                });

                it('should invoke _fetchData on grandchildController when immediate is true',
                    function () {
                        spyOn(grandchildController, '_fetchData');
                        grandchildController._isDataRequired = function () {
                            return true;
                        };

                        controller.updateCtrlState(ctrlState);
                        expect(grandchildController._fetchData).toHaveBeenCalledWith();
                    });
            });

        });
    });

});
