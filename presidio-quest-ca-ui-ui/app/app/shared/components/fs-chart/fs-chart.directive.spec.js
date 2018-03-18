describe('fs-chart.directive', function () {
    'use strict';

    var $rootScope;
    var $scope;
    var $compile;
    var $browser;

    var preCompiledElement;
    var element;
    var controller;
    var assertMock;

    /**
     * Helper functions
     */

    /**
     * Renders a pre-compiled element and gets it's Controller
     */
    function renderElement() {
        element = $compile(preCompiledElement)($scope);
        $scope.$digest();
        controller = element.controller('fsChart');
    }

    /**
     * Setup
     */

    beforeEach(function () {
        // Load View templates from html2js preprocessor
        module("fsTemplates");
        // Load required Modules
        module('Fortscale.shared.services.assert');
        module('Fortscale.shared.services');
        module('Fortscale.shared.components.fsChart');
    });

    beforeEach(function () {
        assertMock = jasmine.createSpy('assert');
        assertMock.isString = angular.noop;
        assertMock.isObject = angular.noop;
        module(function ($provide) {
            $provide.value('assert', assertMock);
        });
    });

    beforeEach(inject(function ($injector, _$rootScope_, _$compile_, _$browser_) {
        $rootScope = _$rootScope_;
        $compile = _$compile_;
        $scope = $rootScope.$new();
        $browser = _$browser_;

        // Cache a precompiled element of the tested directive
        preCompiledElement = angular.element('<fs-chart></fs-chart>');

        // Reset previously set values
        element = undefined;
        controller = undefined;
    }));


    var model, settings, chartType, mapSettings, styleSettings;

    beforeEach(function () {
        model = [];
        settings = {};
        chartType = 'pie';
        mapSettings = {
            name: 'machineName',
            y: 'count'
        };

        styleSettings = {
            'max-width': '100vw'
        };

        preCompiledElement.attr({
            'chart-type': 'pie',
            model: 'model',
            settings: 'settings',
            'map-settings': 'mapSettings',
            'style-settings': 'styleSettings'
        });

        $scope.model = model;
        $scope.settings = settings;
        $scope.mapSettings = mapSettings;
        $scope.styleSettings = styleSettings;
    });


    describe('Arguments', function () {


        it('should accept model from scope as _model', function () {
            renderElement();
            expect(controller._model).toBe(model);
        });

        it('should accept settings from scope as settings', function () {
            renderElement();
            expect(controller.settings).toBe(settings);
        });

        it('should accept style-settings from scope as settings', function () {
            renderElement();
            expect(controller._styleSettings).toBe(styleSettings);
        });


        it('should accept map-settings as object to _mapSettings', function () {
            renderElement();
            expect(controller._mapSettings).toBe(mapSettings);
        });

    });


    describe('Controller', function () {

        beforeEach(function () {
            renderElement();
        });

        describe('Private methods', function () {

            var lodashIsArrayMock, lodashIsArrayOrigin;
            var lodashIsObjectMock, lodashIsObjectOrigin;
            var lodashIsUndefinedMock, lodashIsUndefinedOrigin;
            var lodashIsStringMock, lodashIsStringOrigin;

            beforeEach(function () {
                lodashIsArrayOrigin = _.isArray;
                lodashIsObjectOrigin = _.isObject;
                lodashIsUndefinedOrigin = _.isUndefined;
                lodashIsStringOrigin = _.isString;
                lodashIsArrayMock = spyOn(_, 'isArray')
                .and.returnValue('isArray');
                lodashIsObjectMock = spyOn(_, 'isObject')
                    .and.returnValue('isObject');
                lodashIsUndefinedMock = spyOn(_, 'isUndefined')
                    .and.returnValue('isUndefined');
                lodashIsStringMock = spyOn(_, 'isString')
                    .and.returnValue('isString');
            });

            afterEach(function () {
                _.isArray = lodashIsArrayOrigin;
                _.isObject = lodashIsObjectOrigin;
                _.isUndefined = lodashIsUndefinedOrigin;
                _.isString = lodashIsStringOrigin;
            });

            describe('_validateModel', function () {
                it('should invoke _.isArray with _model', function () {
                    controller._model = 'model';
                    controller._validateModel();
                    expect(_.isArray).toHaveBeenCalledWith('model');
                });

                it('should invoke assert with result of _.isArray, errorMsg ' +
                    'and TypeError', function () {
                    controller._model = 'model';
                    controller._validateModel();
                    expect(assertMock)
                        .toHaveBeenCalledWith('isArray', 'provided model must be an array.',
                        TypeError);
                });
            });

            describe('_validateSettings', function () {
                it('should invoke _.isObject with settings', function () {
                    controller.settings = 'settings';
                    controller._validateSettings();
                    expect(_.isObject).toHaveBeenCalledWith('settings');
                });

                it('should invoke assert with result of _.isObject, errorMsg ' +
                    'and TypeError', function () {
                    controller.settings = 'settings';
                    controller._validateSettings();
                    expect(assertMock)
                        .toHaveBeenCalledWith('isObject', 'provided settings must be an object.',
                        TypeError);
                });
            });

            describe('_validateMapSettings', function () {
                it('should invoke _.isObject with settings', function () {
                    controller._mapSettings = 'mapSettings';
                    controller._validateMapSettings();
                    expect(_.isObject).toHaveBeenCalledWith('mapSettings');
                });

                it('should invoke assert with result of _.isObject, errorMsg ' +
                    'and TypeError', function () {
                    controller.settings = 'settings';
                    controller._validateMapSettings();
                    expect(assertMock)
                        .toHaveBeenCalledWith('isObject', 'provided map settings ' +
                        'must be an object.',
                        TypeError);
                });
            });

           describe('_validateStyleSettings', function () {
                it('should invoke _.isObject with style settings', function () {
                    controller._styleSettings = 'styleSettings';
                    controller._validateStyleSettings();
                    expect(_.isObject).toHaveBeenCalledWith('styleSettings');
                });

                it('should invoke assert with result of _.isObject, errorMsg ' +
                    'and TypeError', function () {
                    controller._styleSettings = 'styleSettings';
                    controller._validateStyleSettings();
                    expect(assertMock)
                        .toHaveBeenCalledWith('isObject', 'provided style settings ' +
                        'must be an object.',
                        TypeError);
                });
            });

            describe('_validateString', function () {
                it('should invoke _.isUndefined with controller[name]', function () {
                    controller._someString = 'someString';
                    controller._validateString('_someString');
                    expect(_.isUndefined).toHaveBeenCalledWith('someString');
                });

                it('should invoke assert with result of !_.isUndefined, errorMsg ' +
                    'and ReferenceError', function () {
                    controller._someString = 'someString';
                    controller._validateString('_someString');
                    expect(assertMock)
                        .toHaveBeenCalledWith(false,
                        '_someString must be provided in the directive html declaration.',
                        ReferenceError);
                });

                it('should invoke _.isString with controller[name]', function () {
                    controller._someString = 'someString';
                    controller._validateString('_someString');
                    expect(_.isString).toHaveBeenCalledWith('someString');
                });

                it('should invoke assert with result of _.isString, errorMsg ' +
                    'and ReferenceError', function () {
                    controller._someString = 'someString';
                    controller._validateString('_someString');
                    expect(assertMock)
                        .toHaveBeenCalledWith('isString',
                        'provided _someString must be a string.',
                        TypeError);
                });

                it('should invoke assert with result of controller[name] !== "", errorMsg ' +
                    'and RangeError', function () {
                    controller._someString = '';
                    controller._validateString('_someString');
                    expect(assertMock)
                        .toHaveBeenCalledWith(false,
                        'provided _someString must not be an empty string.',
                        RangeError);
                });
            });

            describe('_validations', function () {
                var errorMsg;
                beforeEach(function () {
                    spyOn(controller, '_validateSettings');
                    spyOn(controller, '_validateMapSettings');
                    spyOn(controller, '_validateStyleSettings');
                    spyOn(controller, '_validateString');

                    errorMsg = 'some error msg';
                    controller._validations(errorMsg);
                });


                it('should invoke _validateSettings with errorMsg', function () {
                    expect(controller._validateSettings).toHaveBeenCalledWith(errorMsg);
                });

                it('should invoke _validateMapSettings with errorMsg', function () {
                    expect(controller._validateMapSettings).toHaveBeenCalledWith(errorMsg);
                });

                it('should invoke _validateStyleSettings with errorMsg', function () {
                    expect(controller._validateStyleSettings).toHaveBeenCalledWith(errorMsg);
                });


                it('should invoke _validateString with "_chartType" and errorMsg', function () {
                    expect(controller._validateString)
                        .toHaveBeenCalledWith('_chartType', errorMsg);
                });

            });

            describe('_initSettings', function () {
                it('should define _settings', function () {
                    this._settings = undefined;
                    controller._initSettings();
                    expect(controller._settings).toBeDefined();
                });
            });

            describe('_watchModel', function () {
                it('should return _model', function () {
                    controller._model = 'some model';

                    expect(controller._watchModel()).toBe('some model');
                });
            });

            describe('_watchModelAction', function () {

                var mockValidateModel, mockRender;
                beforeEach(function () {
                    mockValidateModel = spyOn(controller, '_validateModel');
                    mockRender = spyOn(controller, '_render');
                });

                it('should invoke _validateModel if newVal is defined ' +
                    'and newVal !== oldVal', function () {
                    mockValidateModel.calls.reset();
                    controller._watchModelAction('newVal', 'oldVal');
                    expect(controller._validateModel)
                        .toHaveBeenCalledWith('fsChart.directive: _watchModelAction: ');
                });

                it('should invoke _render if newVal is defined ' +
                    'and newVal !== oldVal', function () {
                    mockRender.calls.reset();
                    controller._watchModelAction('newVal', 'oldVal');
                    expect(mockRender)
                        .toHaveBeenCalledWith();
                });
            });

            describe('_initWatches', function () {
                var mockInitModelWatch;
                beforeEach(function () {
                    mockInitModelWatch = spyOn(controller, '_initModelWatch');
                });

                it('should invoke _initModelWatch', function () {
                    mockInitModelWatch.calls.reset();
                    controller._initWatches();
                    expect(mockInitModelWatch).toHaveBeenCalledWith();
                });
            });

            describe('_mapData', function () {
                it('should return an array of mapped objects', function () {
                    controller._model = [
                        {someName: 'name1', someY: 'y1', otherProp: ''},
                        {someName: 'name2', someY: 'y2', otherProp: ''},
                        {someName: 'name3', someY: 'y3', otherProp: ''}
                    ];
                    controller._mapSettings = {
                        name: 'someName',
                        y: 'someY'
                    };

                    expect(controller._mapData(controller._model)).toEqual([
                        {name: 'name1', y: 'y1'},
                        {name: 'name2', y: 'y2'},
                        {name: 'name3', y: 'y3'}
                    ]);
                });

                it('should return an array of mapped and processed objects', function () {
                    controller._model = [
                        {someName: 'name1', someY: 'y1', otherProp: ''},
                        {someName: 'name2', someY: 'y2', otherProp: ''},
                        {someName: 'name3', someY: 'y3', otherProp: ''}
                    ];
                    controller._mapSettings = {
                        name: 'someName',
                        y: {
                            key: 'someY',
                            fn: function (value) {
                                return value + ' processed';
                            }
                        }
                    };
                    lodashIsStringMock.and.callThrough();
                    lodashIsObjectMock.and.callThrough();
                    expect(controller._mapData(controller._model)).toEqual([
                        {name: 'name1', y: 'y1 processed'},
                        {name: 'name2', y: 'y2 processed'},
                        {name: 'name3', y: 'y3 processed'}
                    ]);
                });
            });

            describe('_render', function () {

                var mockMapData, mockFind, mockHighchart;

                beforeEach(function () {
                    mockMapData = spyOn(controller, '_mapData').and.returnValue('mappedData');
                    mockHighchart = jasmine.createSpy('highcharts');
                    mockFind = spyOn(controller.$element, 'find').and.returnValue({
                        highcharts: mockHighchart
                    });
                });

                it('should invoke _mapData', function () {
                    mockMapData.calls.reset();
                    controller._render();
                    $browser.defer.flush();
                    expect(controller._mapData).toHaveBeenCalledWith(controller._model);
                });

                it('should set the result of _mapData to _settings.series[0]', function () {
                    controller._settings.series = [{}];
                    controller._render();
                    $browser.defer.flush();
                    expect(controller._settings.series[0].data).toBe('mappedData');
                });

                it('should invoke highcharts with _settings', function () {
                    controller._chartContainer.highcharts = mockHighchart;
                    mockHighchart.calls.reset();
                    controller._render();
                    $browser.defer.flush();
                    expect(mockHighchart).toHaveBeenCalledWith(controller._settings);
                });
            });

            describe('_mergeExternalSettings', function () {
                var lodashMergeMock, lodashMergeOrigin;

                beforeEach(function () {
                    lodashMergeOrigin = _.merge;
                    lodashMergeMock = spyOn(_, 'merge');
                });

                afterEach(function () {
                    _.merge = lodashMergeOrigin;
                });

                it('should invoke _merge with _settings and settings', function () {
                    controller._mergeExternalSettings();
                    expect(_.merge).toHaveBeenCalledWith(controller._settings, controller.settings);
                });
            });
        });

        describe('Public methods', function () {

            describe('init', function () {

                var mockValidations, mockInitSettings, mockMergeExternalSettings, mockInitWatches;
                beforeEach(function () {
                    mockValidations = spyOn(controller, '_validations');
                    mockInitSettings = spyOn(controller, '_initSettings');
                    mockMergeExternalSettings = spyOn(controller, '_mergeExternalSettings');
                    mockInitWatches = spyOn(controller, '_initWatches');
                });

                it('should invoke _validations with errorMsg', function () {
                    mockValidations.calls.reset();
                    controller.init();
                    expect(controller._validations)
                        .toHaveBeenCalledWith('fsChart.directive: init: ');
                });

                it('should invoke _initSettings', function () {
                    mockInitSettings.calls.reset();
                    controller.init();
                    expect(controller._initSettings)
                        .toHaveBeenCalledWith();
                });

                it('should invoke _mergeExternalSettings', function () {
                    mockMergeExternalSettings.calls.reset();
                    controller.init();
                    expect(controller._mergeExternalSettings)
                        .toHaveBeenCalledWith();
                });


                it('should invoke _initWatches', function () {
                    mockInitWatches.calls.reset();
                    controller.init();
                    expect(controller._initWatches)
                        .toHaveBeenCalledWith();
                });


            });
        });

    });


    describe('Integration', function () {

        beforeEach(function () {
            renderElement();
        });

        it('should change _model when $scope.model changes', function () {
            $scope.model = [{model: 'changed'}];
            $scope.$digest();
            expect(controller._model).toBe($scope.model);
        });

        it('should invoke render when model changes', function () {
            spyOn(controller, '_render').calls.reset();
            $scope.model = [{model: 'changed'}];
            $scope.$digest();
            expect(controller._render).toHaveBeenCalledWith();
        });
    });
});
