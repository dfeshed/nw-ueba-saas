describe('fs-table-collapsible-list.directive', function () {
    'use strict';

    var $compile;
    var $rootScope;
    var $scope;
    var $injector;
    var $controller;

    var element;

    // Load FortscaleHighChart module
    beforeEach(module('Fortscale.shared.components.fsTable'));
    beforeEach(module('fsTemplates'));

    beforeEach(inject(function (_$compile_, _$rootScope_, _$injector_, _$controller_) {
        // The injector unwraps the underscores (_) from around the parameter names when matching
        $compile = _$compile_;
        $rootScope = _$rootScope_;
        $scope = $rootScope.$new();
        $controller = _$controller_;
        $injector = _$injector_;

    }));

    describe('Interface', function () {

        it('should place titleId on the controller when passed in the markup', function () {

            $scope.dataItemId = 'id';

            element = $compile('<fs-table-collapsible-list title-id="dataItemId"></fs-table-collapsible-list>')($scope);
            $scope.$digest();
            var controller = element.controller('fsTableCollapsibleList');
            expect(controller.titleId).toBe('id');

        });

        it('should place titleName on the controller when passed in the markup', function () {

            $scope.dataItemName = 'name';
            element =
                $compile('<fs-table-collapsible-list title-name="dataItemName"></fs-table-collapsible-list>')($scope);
            $scope.$digest();
            var controller = element.controller('fsTableCollapsibleList');
            expect(controller.titleName).toBe('name');

        });

        it('should place listCollection on the controller when passed in the markup', function () {

            $scope.collection = [{id: "1", name: "name1"}, {id: "2", name: "name2"}];
            element =
                $compile('<fs-table-collapsible-list list-collection="collection">' +
                    '</fs-table-collapsible-list>')($scope);
            $scope.$digest();
            var controller = element.controller('fsTableCollapsibleList');
            expect(controller.listCollection).toBe($scope.collection);

        });

        it('should place listCollectionDisplayField on the controller when passed in the markup', function () {

            $scope.collectionFieldName = 'name';
            element =
                $compile('<fs-table-collapsible-list list-collection-display-field="collectionFieldName">' +
                    '</fs-table-collapsible-list>')($scope);
            $scope.$digest();
            var controller = element.controller('fsTableCollapsibleList');
            expect(controller.listCollectionDisplayField).toBe('name');

        });

    });

    describe('Instance', function () {

        var element;
        var ctrl;
        var directiveInput;

        beforeEach(function () {
            $scope.dataItemId = 'id';
            $scope.dataItemName = 'name';
            directiveInput = {
                collection: [
                    {
                        id: "1",
                        name: "name1"
                    },
                    {
                        id: "2",
                        name: "name2"
                    },
                    {
                        id: "3",
                        name: "name3"
                    },
                    {
                        id: "4",
                        name: "name4"
                    }
                ]
            };
            $scope.collection = directiveInput.collection;
            $scope.collectionFieldName = 'name';

            element = $compile('<fs-table-collapsible-list title-id="dataItemId" ' +
                'title-name="dataItemName" list-collection="collection" ' +
                'list-collection-display-field="collectionFieldName">' +
                '</fs-table-collapsible-list>')($scope);
            $scope.$digest();
            ctrl = element.controller('fsTableCollapsibleList');

        });

        describe('public methods', function () {

            describe('showFullListFn', function () {
                it('should update showFullList to false and maxDisplayLengh to 3', function () {
                    ctrl.showFullList = true;
                    ctrl.maxDisplayLengh = 5;
                    ctrl.showFullListFn();
                    expect(ctrl.showFullList).toBe(false);
                    expect(ctrl.maxDisplayLength).toBe(3);
                });

                it('should update showFullList to true and maxDisplayLengh to 5', function () {
                    ctrl.showFullList = false;
                    ctrl.maxDisplayLength = 0;
                    ctrl.showFullListFn();
                    expect(ctrl.showFullList).toBe(true);
                    expect(ctrl.maxDisplayLength).toBe(directiveInput.collection.length);
                });
            });

        });

    });
});

