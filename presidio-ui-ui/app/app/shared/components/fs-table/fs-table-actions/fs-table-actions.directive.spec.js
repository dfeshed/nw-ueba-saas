describe('fs-table-actions.directive', function () {
    'use strict';

    var $rootScope;
    var $scope;
    var $compile;

    var preCompiledElement;
    var element;
    var controller;

    var URLUtils;
    var fsResourceStore;
    var indicatorTypeMapper;

    /**
     * Helper functions
     */

    /**
     * Renders a pre-compiled element and gets it's Controller
     */
    function renderElement (preElement) {
        element = $compile(preElement)($scope);
        $scope.$digest();

        controller = element.controller('fsTableActions');
    }

    /**
     * Setup
     */

    beforeEach(function () {
        // Load View templates from html2js preprocessor
        module("fsTemplates");
        // Load required Modules
        module('Fortscale.shared.services.assert');
        module('Fortscale.shared.services.URLUtils');
        module('Fortscale.shared.components.fsTable');
        module('Fortscale.shared.components.fsResourceStore');
        module('Fortscale.shared.services.indicatorTypeMapper');
        module('Fortscale.shared.services.fsIndicatorGraphsHandler');
    });

    beforeEach(inject(function ($injector, _$rootScope_, _$compile_, _fsResourceStore_,
        _indicatorTypeMapper_) {
        URLUtils = $injector.get('URLUtils');
        fsResourceStore = _fsResourceStore_;
        indicatorTypeMapper = _indicatorTypeMapper_;

        $rootScope = _$rootScope_;
        $compile = _$compile_;
        $scope = $rootScope.$new();

        // Cache a precompiled element of the tested directive
        preCompiledElement = angular.element('<fs-table-actions></fs-table-actions>');

        // Set required attributes
        $scope.exploreId = '123';
        $scope.baseUrl = '/alerts';

        preCompiledElement.attr({
            'explore-id': 'exploreId',
            'base-url': 'baseUrl'
        });

        // Reset previously set values
        element = undefined;
        controller = undefined;
    }));

    /**
     * Test directive attributes
     */

    describe('Arguments', function () {

        var errStart = 'FsTableActionsController._validate: ';

        // `exploreId`
        it('should accept `exploreId` as a String', function () {
            renderElement(preCompiledElement);

            expect(controller.exploreId).toBe($scope.exploreId);
        });

        it('should throw if `exploreId` if provided and not a String', function () {
            $scope.exploreId = 999;
            preCompiledElement.attr({'explore-id': 'exploreId'});

            var test = function () {
                renderElement(preCompiledElement);
            };

            expect(test).toThrowError(TypeError,
                errStart + '`exploreId` must be a String');
        });

        it('should throw if `exploreId` if provided and an empty String', function () {
            $scope.exploreId = '';
            preCompiledElement.attr({'explore-id': 'exploreId'});

            var test = function () {
                renderElement(preCompiledElement);
            };

            expect(test).toThrowError(RangeError,
                errStart + '`exploreId` must not be empty');
        });

        // `baseUrl`
        it('should accept `baseUrl` as a String', function () {
            renderElement(preCompiledElement);

            expect(controller.baseUrl).toBe($scope.baseUrl);
        });

        it('should throw if `baseUrl` if provided and not a String', function () {
            $scope.baseUrl = 999;
            preCompiledElement.attr({'base-url': 'baseUrl'});

            var test = function () {
                renderElement(preCompiledElement);
            };

            expect(test).toThrowError(TypeError,
                errStart + '`baseUrl` must be a String');
        });

        it('should throw if `baseUrl` if provided and an empty String', function () {
            $scope.baseUrl = '';
            preCompiledElement.attr({'base-url': 'baseUrl'});

            var test = function () {
                renderElement(preCompiledElement);
            };

            expect(test).toThrowError(RangeError,
                errStart + '`baseUrl` must not be empty');
        });

    });

    /**
     * Test Directive Controller
     */

    describe('Controller', function () {

        beforeEach(function () {
            renderElement(preCompiledElement);
        });

        it('should be defined', function () {
            expect(controller).toBeDefined();
        });

        describe('Public methods', function () {

            describe('exploreAlert', function () {

                var alert;
                var indicator;
                var targetUrl;
                var mockFetchResourceItemById;
                var mockGetType;

                beforeEach(function () {
                    indicator = {
                        id: 'indicatorId'
                    };
                    alert = {
                        evidences: [
                            indicator
                        ]
                    };

                    mockFetchResourceItemById = spyOn(fsResourceStore, 'fetchResourceItemById')
                        .and.returnValue(alert);

                    mockGetType = spyOn(indicatorTypeMapper, 'getType')
                        .and.returnValue(null);

                    spyOn(URLUtils, 'setUrl');

                    renderElement(preCompiledElement);

                    targetUrl = controller.baseUrl + '/' + controller.exploreId;
                });

                it('should invoke fsResourceStore.fetchResourceItemById with "alerts", ' +
                    'controller.exploreId', function () {

                    controller.exploreAlert();
                    expect(fsResourceStore.fetchResourceItemById)
                        .toHaveBeenCalledWith('alerts', controller.exploreId);
                });

                it('should invoke indicatorTypeMapper.getType with all members of evidences on ' +
                    'alert, where alert returns from fsResourceStore.fetchResourceItemById',
                    function () {

                        controller.exploreAlert();
                        expect(indicatorTypeMapper.getType)
                            .toHaveBeenCalledWith(indicator);
                    });

                it('should invoke URLUtils.setUrl with ' +
                    '"{controller.baseUrl}/{controller.exploreId}", true; ' +
                    'when no indicatorType is found ', function () {

                    controller.exploreAlert();

                    expect(URLUtils.setUrl)
                        .toHaveBeenCalledWith(targetUrl, true);
                });

                it('should invoke URLUtils.setUrl with ' +
                    '"{controller.baseUrl}/{controller.exploreId}/gen/overview", true; ' +
                    'when indicatorType.indicatorClass is "gen" ', function () {

                    mockGetType.and.returnValue({
                        indicatorClass: 'gen'
                    });
                    targetUrl += '/' + indicator.id;
                    targetUrl += '/gen/overview';
                    controller.exploreAlert();

                    expect(URLUtils.setUrl)
                        .toHaveBeenCalledWith(targetUrl, true);
                });

                it('should invoke URLUtils.setUrl with ' +
                    '"{controller.baseUrl}/{controller.exploreId}/tag", true; ' +
                    'when indicatorType.indicatorClass is "tag" ', function () {

                    mockGetType.and.returnValue({
                        indicatorClass: 'tag'
                    });
                    targetUrl += '/' + indicator.id;
                    targetUrl += '/tag';
                    controller.exploreAlert();

                    expect(URLUtils.setUrl)
                        .toHaveBeenCalledWith(targetUrl, true);
                });

            });

        });

    });

    /**
     * Test integration with surrounding logic
     */

    describe('Integration', function () {
        var alert;
        var indicator;
        var mockFetchResourceItemById;
        var mockGetType;

        beforeEach(function () {
            indicator = {
                id: 'indicatorId'
            };
            alert = {
                evidences: [
                    indicator
                ]
            };

            mockFetchResourceItemById = spyOn(fsResourceStore, 'fetchResourceItemById')
                .and.returnValue(alert);

            mockGetType = spyOn(indicatorTypeMapper, 'getType')
                .and.returnValue(null);

            spyOn(URLUtils, 'setUrl');

            renderElement(preCompiledElement);

        });

        it('should call `URLUtils.setUrl` when Search button is clicked', function () {

            var searchBtnElm = element.find('[name="search-btn"]');
            searchBtnElm.trigger('click');

            expect(URLUtils.setUrl).toHaveBeenCalled();
        });

    });
});
