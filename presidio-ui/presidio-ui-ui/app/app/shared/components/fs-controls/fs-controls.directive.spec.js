describe('fs-controls.directive', function () {
    'use strict';

    var $rootScope;
    var $scope;
    var $compile;

    var preCompiledElement;
    var element;
    var controller;

    /**
     * Helper functions
     */

    /**
     * Renders a pre-compiled element and gets it's Controller
     */
    function renderElement() {
        element = $compile(preCompiledElement)($scope);
        $scope.$digest();
        controller = element.controller('fsControls');
    }

    /**
     * Setup
     */

    beforeEach(function () {
        // Load View templates from html2js preprocessor
        module("fsTemplates");
        // Load required Modules
        module('Fortscale.shared.services');
        module('Fortscale.shared.components.fsControls');
    });

    beforeEach(inject(function ($injector, _$rootScope_, _$compile_) {
        $rootScope = _$rootScope_;
        $compile = _$compile_;
        $scope = $rootScope.$new();

        // Cache a precompiled element of the tested directive
        preCompiledElement = angular.element('<fs-controls></fs-controls>');

        // Reset previously set values
        element = undefined;
        controller = undefined;
    }));


    describe('Arguments', function () {

        it('should accept title as string', function () {
            preCompiledElement.attr('title', 'some title');
            renderElement();
            expect(controller.title).toBe('some title');
        });

        it('should should accept className as string', function () {
            preCompiledElement.attr('class-name', 'firstClass secondClass');
            renderElement();
            expect(controller.className).toBe('firstClass secondClass');
        });

        it('should accept hideUpdate as string', function () {
            preCompiledElement.attr('class-name', 'firstClass secondClass');
            renderElement();
            expect(controller.className).toBe('firstClass secondClass');
        });

        it('should accept submitDelegate as object', function () {

            $scope.someDelegate = angular.noop;

            preCompiledElement.attr('submit-delegate', 'someDelegate');
            renderElement();
            expect(controller.submitDelegate).toBe(angular.noop);
        });
    });


    describe('Controller', function () {


        beforeEach(function () {
            renderElement();
        });

        it('should be defined', function () {
            expect(controller).toBeDefined();
        });

        describe('Public methods', function () {


            describe('submit', function () {

                var formCtrl;

                beforeEach(function () {
                    formCtrl = {
                        $setPristine: jasmine.createSpy('setPristine')
                    };

                    controller.submitDelegate = jasmine.createSpy('submitDelegate');

                });


                it('should invoke formCtrl.$setPristine', function () {
                    controller.submit(formCtrl);
                    expect(formCtrl.$setPristine).toHaveBeenCalledWith();
                });

                it('should invoke control\'s submitDelegate if its a function', function () {
                    spyOn(angular, 'isFunction').and.returnValue(true);
                    controller.submit(formCtrl);
                    expect(controller.submitDelegate).toHaveBeenCalledWith();
                });

                it('should not invoke control\'s submitDelegate if its not a function', function () {
                    spyOn(angular, 'isFunction').and.returnValue(false);
                    controller.submit(formCtrl);
                    expect(controller.submitDelegate).not.toHaveBeenCalled();
                });
            });
        });

    });


    describe('Integration', function () {

        var formCtrl;

        function getFormCtrl () {
            formCtrl = element.find('form').controller('form');
        }

        it('should transclude all children under `.controls-transclude`', function () {
            preCompiledElement.append('<div class="transcluded">some content</div>');
            renderElement();

            var parentFound = element.find('.transcluded')
                .parent().is('.controls-transclude');

            expect(parentFound).toBe(true);
        });

        it('should show title if title attribute is provided', function () {
            preCompiledElement.attr('title', 'some title');
            renderElement();
            var title = element.find('h3.control-params-title');
            expect(title.length).toBe(1);
            expect(title.html().trim()).toBe('some title');
        });

        it('should not show title if title attribute is not provided', function () {
            renderElement();
            var title = element.find('h3.control-params-title');
            expect(title.length).toBe(0);
        });

        it('should add classes by class-name attribute to div.form-container', function () {
            preCompiledElement.attr('class-name', 'firstClass secondClass');
            renderElement();
            var div = element.find('.form-container');
            expect(div[0].classList.contains('firstClass')).toBe(true);
            expect(div[0].classList.contains('secondClass')).toBe(true);
        });

        it('should not show button if hideUpdate is truthy', function () {
            preCompiledElement.attr('hide-update', 'true');
            renderElement();
            var button = element.find('button');
            expect(button.length).toBe(0);
        });

        it('should disable button if form is pristine', function () {
            renderElement();
            var button = element.find('button');
            getFormCtrl();
            formCtrl.$setPristine();
            $scope.$digest();
            expect(button[0].disabled).toBe(true);
        });

        it('should disable button if form is invalid', function () {
            renderElement();
            var button = element.find('button');
            getFormCtrl();
            formCtrl.$invalid = true;
            $scope.$digest();
            expect(button[0].disabled).toBe(true);
        });

        it('should not disable button if form is not pristine and not invalid', function () {
            renderElement();
            var button = element.find('button');
            getFormCtrl();
            formCtrl.$invalid = false;
            formCtrl.$pristine = false;
            $scope.$digest();
            expect(button[0].disabled).toBe(false);
        });

        it('should invoke submit with the form when clicking on update', function () {
            renderElement();
            var button = element.find('button');
            getFormCtrl();
            formCtrl.$invalid = false;
            formCtrl.$pristine = false;
            $scope.$digest();
            spyOn(controller, 'submit');

            button.trigger('click');
            expect(controller.submit).toHaveBeenCalledWith(formCtrl);
        });
    });
});
