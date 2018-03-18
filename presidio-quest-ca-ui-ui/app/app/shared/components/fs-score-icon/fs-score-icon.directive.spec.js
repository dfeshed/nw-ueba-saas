describe('fs-score-icon.directive', function () {
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

        controller = element.controller('fsScoreIcon');
    }

    /**
     * Setup
     */

    beforeEach(function () {
        // Load View templates from html2js preprocessor
        module("fsTemplates");
        // Load required Modules
        module('Fortscale.shared.components.fsScoreIcon');
    });

    beforeEach(inject(function (_$injector_, _$rootScope_, _$compile_) {
        $rootScope = _$rootScope_;
        $compile = _$compile_;
        $injector = _$injector_;
        $scope = $rootScope.$new();

        // Cache a precompiled element of the tested directive
        preCompiledElement = angular.element('<fs-score-icon></fs-score-icon>');

        // Reset previously set values
        element = undefined;
        controller = undefined;
    }));

    /**
     * Test directive attributes
     */

    describe('Arguments', function () {

        // entityData
        it('should accept `score` as primitive', function () {
            preCompiledElement.attr({'score': 100});

            renderElement(preCompiledElement);

            expect(controller.score).toBe(100);
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

        describe('_getScoreRangeClass', function () {
            it('should return fs-score-icon-red if score is greater then ' +
                'or equal to 95', function () {
                expect(controller._getScoreRangeClass(100)).toBe('fs-score-icon-red');
                expect(controller._getScoreRangeClass(95)).toBe('fs-score-icon-red');
                expect(controller._getScoreRangeClass(94)).not.toBe('fs-score-icon-red');
            });
            it('should return fs-score-icon-red if score is greater then ' +
                'or equal to 80', function () {
                expect(controller._getScoreRangeClass(94)).toBe('fs-score-icon-orange');
                expect(controller._getScoreRangeClass(80)).toBe('fs-score-icon-orange');
                expect(controller._getScoreRangeClass(79)).not.toBe('fs-score-icon-orange');
            });
            it('should return fs-score-icon-red if score is greater then ' +
                'or equal to 50', function () {
                expect(controller._getScoreRangeClass(79)).toBe('fs-score-icon-yellow');
                expect(controller._getScoreRangeClass(50)).toBe('fs-score-icon-yellow');
                expect(controller._getScoreRangeClass(49)).not.toBe('fs-score-icon-yellow');
            });
            it('should return fs-score-icon-red if score is greater then ' +
                'or equal to 95', function () {
                expect(controller._getScoreRangeClass(49)).toBe('fs-score-icon-hide');
                expect(controller._getScoreRangeClass(30)).toBe('fs-score-icon-hide');
                expect(controller._getScoreRangeClass(0)).toBe('fs-score-icon-hide');
            });
        });

    });

    /**
     * Test integration with surrounding logic
     */

    describe('Integration', function () {
        it('element should have fs-score-icon-red class if score is greater then ' +
            'or equal to 95', function () {
            preCompiledElement.attr({'score': 100});
            renderElement(preCompiledElement);
            expect(element.hasClass('fs-score-icon-red')).toBe(true);
        });
        it('element should have fs-score-icon-red class if score is greater then ' +
            'or equal to 95', function () {
            preCompiledElement.attr({'score': 95});
            renderElement(preCompiledElement);
            expect(element.hasClass('fs-score-icon-red')).toBe(true);
        });
        it('element should have fs-score-icon-red class if score is greater then ' +
            'or equal to 95', function () {
            preCompiledElement.attr({'score': 94});
            renderElement(preCompiledElement);
            expect(element.hasClass('fs-score-icon-red')).toBe(false);
        });
        it('element should have fs-score-icon-orange class if score is greater then ' +
            'or equal to 80', function () {
            preCompiledElement.attr({'score': 94});
            renderElement(preCompiledElement);
            expect(element.hasClass('fs-score-icon-orange')).toBe(true);
        });
        it('element should have fs-score-icon-orange class if score is greater then ' +
            'or equal to 80', function () {
            preCompiledElement.attr({'score': 80});
            renderElement(preCompiledElement);
            expect(element.hasClass('fs-score-icon-orange')).toBe(true);
        });
        it('element should have fs-score-icon-orange class if score is greater then ' +
            'or equal to 80', function () {
            preCompiledElement.attr({'score': 79});
            renderElement(preCompiledElement);
            expect(element.hasClass('fs-score-icon-orange')).toBe(false);
        });
        it('element should have fs-score-icon-yellow class if score is greater then ' +
            'or equal to 50', function () {
            preCompiledElement.attr({'score': 79});
            renderElement(preCompiledElement);
            expect(element.hasClass('fs-score-icon-yellow')).toBe(true);
        });
        it('element should have fs-score-icon-yellow class if score is greater then ' +
            'or equal to 50', function () {
            preCompiledElement.attr({'score': 50});
            renderElement(preCompiledElement);
            expect(element.hasClass('fs-score-icon-yellow')).toBe(true);
        });
        it('element should have fs-score-icon-yellow class if score is greater then ' +
            'or equal to 50', function () {
            preCompiledElement.attr({'score': 49});
            renderElement(preCompiledElement);
            expect(element.hasClass('fs-score-icon-yellow')).toBe(false);
        });
        it('element should have fs-score-icon-hide if score is less then 50', function () {
            preCompiledElement.attr({'score': 45});
            renderElement(preCompiledElement);
            expect(element.hasClass('fs-score-icon-hide')).toBe(true);
        });
        it('element should have fs-score-icon-hide if score is less then 50', function () {

            preCompiledElement.attr({'score': 25});
            renderElement(preCompiledElement);
            expect(element.hasClass('fs-score-icon-hide')).toBe(true);
        });
        it('element should have fs-score-icon-hide if score is less then 50', function () {

            preCompiledElement.attr({'score': 0});
            renderElement(preCompiledElement);
            expect(element.hasClass('fs-score-icon-hide')).toBe(true);
        });
    });
});
