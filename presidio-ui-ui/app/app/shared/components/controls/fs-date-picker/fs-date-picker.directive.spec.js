describe('fsDatePicker.directive', function () {
    'use strict';

    var $rootScope;
    var $scope;
    var $compile;
    var $injector;

    var preCompiledElement;
    var element;
    var controller;

    /**
     * Helper functions
     */

    /**
     * Renders a pre-compiled element and gets it's Controller
     */
    function renderElement () {
        element = $compile(preCompiledElement)($scope);
        $scope.$digest();

        controller = element.controller('fsDatePicker');
    }

    /**
     * Setup
     */

    beforeEach(function () {
        // Load View templates from html2js preprocessor
        module("fsTemplates");
        // Load required Modules
        module('Fortscale.shared.services.dateRanges');
        module('Fortscale.shared.components.fsDatePicker');
    });

    beforeEach(inject(function (_$injector_, _$rootScope_, _$compile_) {
        $rootScope = _$rootScope_;
        $compile = _$compile_;
        $injector = _$injector_;
        $scope = $rootScope.$new();
        $scope.fetchStateDelegate = jasmine.createSpy('fetchStateDelegate');
        $scope.setStateDelegate = jasmine.createSpy('setStateDelegate');

        // Cache a precompiled element of the tested directive
        preCompiledElement = angular.element('<fs-date-picker date-picker-id="date_picker" ' +
            'fetch-state-delegate="fetchStateDelegate" ' +
            'update-state-delegate="setStateDelegate"></fs-date-picker>');

        // Reset previously set values
        element = undefined;
        controller = undefined;
    }));

    beforeEach(function () {
        renderElement();
    });

    /**
     * Test directive attributes
     */

    describe('Arguments', function () {

    });

    /**
     * Test Directive Controller
     */

    describe('Controller', function () {

        it('should be defined', function () {
            expect(controller).toBeDefined();
        });

        describe('private', function () {

        });

        describe('public methods', function () {
            describe('init', function () {
                var validationsMock, initWatchesMock;

                beforeEach(function () {
                    validationsMock = spyOn(controller, '_validations');
                    initWatchesMock = spyOn(controller, '_initWatches');
                });

                it('should invoke _validations', function () {
                    controller.init();
                    expect(validationsMock).toHaveBeenCalledWith();
                });

                it('should invoke _initWatches', function () {
                    controller.init();
                    expect(initWatchesMock).toHaveBeenCalledWith();
                });
            });

            describe('getPickerValue', function () {
                var mockDatePickerControllerValueFn;

                beforeEach(function () {
                    mockDatePickerControllerValueFn = jasmine.createSpy('mockDatePickerControllerValueFn');
                    controller.datePickerKendoController = {
                        value: mockDatePickerControllerValueFn
                    };
                });

                it('should invoke datePickerKendoController.value', function () {
                    controller.getPickerValue();
                    expect(mockDatePickerControllerValueFn).toHaveBeenCalledWith();
                });
            });

            describe('setPickerValue', function () {
                var mockDatePickerControllerValueFn, mockGetDateValueFromGenericValue, value;

                beforeEach(function () {
                    mockDatePickerControllerValueFn = jasmine.createSpy('mockDatePickerControllerValueFn');
                    mockGetDateValueFromGenericValue = spyOn(controller, '_getDateValueFromGenericValue');
                    controller.datePickerKendoController = {
                        value: mockDatePickerControllerValueFn
                    };
                });

                it('should invoke _getDateValueFromGenericValue with value', function () {
                    value = 100;
                    controller.setPickerValue(value);

                    expect(mockGetDateValueFromGenericValue).toHaveBeenCalledWith(value);
                });

                it('should invoke datePickerKendoController.value ' +
                    'with _getDateValueFromGenericValue return value turned to Date', function () {
                    value = 100;
                    mockGetDateValueFromGenericValue.and.returnValue(200);
                    controller.setPickerValue(value);

                    var newDate = new Date(200);
                    expect(mockDatePickerControllerValueFn).toHaveBeenCalledWith(newDate);
                });
            });
        });

    });

    /**
     * Test directive integration
     */

    describe('Integration', function () {
        describe('element', function () {

            it('should render kendo date picker', function () {
                expect(element.find('.k-datepicker').length === 1).toBe(true);
            });


            it('it should allow initial value', function () {
                preCompiledElement.attr('initial-value', '1/1/10');
                renderElement();
                expect(controller.datePickerKendoController.value().valueOf()).toBe(1262296800000);
            });

            it('should fire fetchStateDelegate on render', function () {
                $scope.fetchStateDelegate.calls.reset();
                renderElement();
                expect($scope.fetchStateDelegate).toHaveBeenCalled();
            });

            it('should fire setStateDelegate when value of date range changes', function () {
                $scope.setStateDelegate.calls.reset();
                controller.datePickerKendoController.trigger('change');
                expect($scope.setStateDelegate).toHaveBeenCalled();
            });
        });

    });

});
