describe('fs-table.directive', function () {
    'use strict';

    var $compile;
    var $rootScope;
    var $scope;
    var $injector;
    var $controller;

    var element;

    // Load FSTable module
    beforeEach(module('Fortscale.shared.components.fsTable'));

    beforeEach(inject(function (_$compile_, _$rootScope_, _$injector_, _$controller_) {
        // The injector unwraps the underscores (_) from around the parameter names when matching
        $compile = _$compile_;
        $rootScope = _$rootScope_;
        $scope = $rootScope.$new();
        $controller = _$controller_;
        $injector = _$injector_;

    }));

    describe('Interface', function () {
        it('should throw TypeError when fetchStateDelegate is not a function',
            function () {

                function test () {
                    element = $compile('<fs-table fetch-state-delegate="\'not a function\'">' +
                        '</fs-table>')($scope);
                    $scope.$digest();
                }

                expect(test).toThrowError(TypeError);
                expect(test).toThrowError('fsTable.directive: FsTableController: ' +
                    'If fetchStateDelegate is provided, it must be a function.');

            });

        it('should throw TypeError when updateStateDelegate is not a function',
            function () {

                function test () {
                    element = $compile('<fs-table update-state-delegate="\'not a function\'">' +
                        '</fs-table>')($scope);
                    $scope.$digest();

                }

                expect(test).toThrowError(TypeError);
                expect(test).toThrowError('fsTable.directive: FsTableController: ' +
                    'If updateStateDelegate is provided, it must be a function.');

            });

        it('should place fetchStateDelegate function on the controller ' +
            'when passed in the markup', function () {
            $scope.fetchStateDelegate = angular.noop;
            element = $compile('<fs-table fetch-state-delegate="fetchStateDelegate">' +
                '</fs-table>')($scope);
            $scope.$digest();
            var controller = element.controller('fsTable');
            expect(controller.fetchStateDelegate).toBe(angular.noop);
        });

        it('should place updateStateDelegate function on the controller ' +
            'when passed in the markup', function () {
            $scope.updateStateDelegate = angular.noop;
            element = $compile('<fs-table update-state-delegate="updateStateDelegate">' +
                '</fs-table>')($scope);
            $scope.$digest();
            var controller = element.controller('fsTable');
            expect(controller.updateStateDelegate).toBe(angular.noop);
        });

        it('should place tableSettings on the controller when passed in the markup', function () {
            $scope.tableSettings = {};
            element = $compile('<fs-table table-settings="tableSettings"></fs-table>')($scope);
            $scope.$digest();
            var controller = element.controller('fsTable');
            expect(controller.tableSettings).toBe($scope.tableSettings);
        });

        it('should place tableSettings on the controller when passed in the markup', function () {
            $scope.tableModel = {};
            element = $compile('<fs-table table-model="tableModel"></fs-table>')($scope);
            $scope.$digest();
            var controller = element.controller('fsTable');
            expect(controller.tableModel).toBe($scope.tableModel);
        });
    });

    describe('Instance', function () {
        var tableModel;
        var element;
        var ctrl;
        var watchTableModelSpy;

        beforeEach(function () {
            tableModel = {
                someData: 'someData'
            };
            $scope.tableModel = tableModel;
            element = $compile('<fs-table table-model="tableModel"></fs-table>')($scope);
            $scope.$digest();
            ctrl = element.controller('fsTable');
        });

        describe('_watchTableModel', function () {
            it('should return the tableModel', function () {
                expect(ctrl._watchTableModel()).toBe(tableModel);
            });
        });

        describe('_fetchStateDelegateWatchAction', function () {
            beforeEach(function () {
                ctrl.offset = 0;
                ctrl.page = 0;
                ctrl.pageSize = 0;
                ctrl.sortBy = 0;
                ctrl.sortDirection = 0;
            });
            it('should do nothing becuase new value is null', function () {
                ctrl._fetchStateDelegateWatchAction();
                expect(ctrl.page).toBe(0);
                expect(ctrl.pageSize).toBe(0);
                expect(ctrl.sortBy).toBe(0);
                expect(ctrl.sortDirection).toBe(0);
            });
            it('should update the state on the controller', function () {

                var state = {
                    page: 1,
                    pageSize: 1,
                    sortBy: 1,
                    sortDirection: 1
                };

                ctrl._fetchStateDelegateWatchAction(state);
                expect(ctrl.page).toBe(1);
                expect(ctrl.pageSize).toBe(1);
                expect(ctrl.sortBy).toBe(1);
                expect(ctrl.sortDirection).toBe(1);
            });
        });

        describe('dependencies', function () {
            it('should have $element on the controller instance', function () {
                expect(ctrl.$element[0]).toBe(element[0]);
            });
            it('should have $scope on the controller instance', function () {
                expect(ctrl.$scope).toBeDefined();
            });
            it('should have $compile on the controller instance', function () {
                expect(ctrl.$compile).toBe($compile);
            });
        });

        describe('private methods', function () {

            describe('_expandTableSettings', function () {

                it('should place data argument on ' +
                    'this.localTableSettings.dataSource.data', function () {
                    delete ctrl.localTableSettings;

                    var dataArray = [{iAm: 'tableModel'}];
                    dataArray._meta = {};

                    ctrl._expandTableSettings(dataArray);

                    expect(ctrl.localTableSettings.dataSource.data.data[0].iAm).toBe('tableModel');
                });
            });

            describe('_createKendoGridElement', function () {

                it('should return an angular element that\'s comprised of kendo-grid directive, ' +
                    'and has a fs-table class, and an options attribute ' +
                    'that equals table.localTableSettings',
                    function () {
                        expect(ctrl._createKendoGridElement()[0].outerHTML)
                            .toBe('<kendo-grid class="fs-table" options=' +
                            '"table.localTableSettings" ' +
                            'k-on-data-bound="table.onDataBound(kendoEvent)"' +
                            '>' +
                            '</kendo-grid>');
                    });
            });

            describe('_compileKendoGridElement', function () {

                var compiled;
                var someEl;

                beforeEach(function () {
                    someEl = angular.element('<div></div>');
                    compiled = jasmine.createSpy('compiled', function () {
                    });
                    spyOn(ctrl, '$compile').and.returnValue(compiled);

                });

                it('should invoke $compile with an element', function () {
                    ctrl._compileKendoGridElement(someEl);

                    expect(ctrl.$compile).toHaveBeenCalledWith(someEl);
                });

                it('should invoke the returned function with the $scope', function () {
                    ctrl._compileKendoGridElement(someEl);

                    expect(compiled).toHaveBeenCalledWith(ctrl.$scope);
                });
            });

            describe('_replaceElementWithKendoElement', function () {
                var someEl;

                beforeEach(function () {
                    spyOn(ctrl.$element, 'replaceWith');
                    someEl = angular.element('<div></div>');
                });
            });

            describe('_updateStateAndExecuteUpdateStateDelegate', function () {
                beforeEach(function () {
                    ctrl.pageSize = 1;
                    ctrl.page = 1;
                    ctrl.sortBy = 1;
                    ctrl.sortDirection = 1;

                    ctrl.updateStateDelegate = function () {
                    };
                    spyOn(ctrl, 'updateStateDelegate');
                });

                it('should invoke ctrl.updateStateDelegate ' +
                    'with pageSize = 2, and all the others to be 1', function () {

                    ctrl._updateStateAndExecuteUpdateStateDelegate(2, null, null, null);

                    var expectedState = {
                        id: ctrl.tableId,
                        type: 'DATA',
                        value: {
                            pageSize: 2,
                            page: 1,
                            sortBy: 1,
                            sortDirection: 1
                        },
                        immediate: true
                    };

                    expect(ctrl.updateStateDelegate).
                        toHaveBeenCalledWith(expectedState);

                    expect(ctrl.pageSize).toBe(2);
                    expect(ctrl.page).toBe(1);
                    expect(ctrl.sortBy).toBe(1);
                    expect(ctrl.sortDirection).toBe(1);

                });

                it('should invoke ctrl.updateStateDelegate ' +
                    'with page = 2, and all the others to be 1', function () {

                    ctrl._updateStateAndExecuteUpdateStateDelegate(null, 2, null, null);

                    var expectedState = {
                        id: ctrl.tableId,
                        type: 'DATA',
                        value: {
                            pageSize: 1,
                            page: 2,
                            sortBy: 1,
                            sortDirection: 1
                        },
                        immediate: true
                    };

                    expect(ctrl.updateStateDelegate).
                        toHaveBeenCalledWith(expectedState);

                    expect(ctrl.pageSize).toBe(1);
                    expect(ctrl.page).toBe(2);
                    expect(ctrl.sortBy).toBe(1);
                    expect(ctrl.sortDirection).toBe(1);
                });

                it('should invoke ctrl.updateStateDelegate ' +
                    'with sortBy = 2, and all the others to be 1', function () {

                    ctrl._updateStateAndExecuteUpdateStateDelegate(null, null, 2, null);

                    var expectedState = {
                        id: ctrl.tableId,
                        type: 'DATA',
                        value: {
                            pageSize: 1,
                            page: 1,
                            sortBy: 2,
                            sortDirection: 1
                        },
                        immediate: true
                    };

                    expect(ctrl.updateStateDelegate).
                        toHaveBeenCalledWith(expectedState);

                    expect(ctrl.pageSize).toBe(1);
                    expect(ctrl.page).toBe(1);
                    expect(ctrl.sortBy).toBe(2);
                    expect(ctrl.sortDirection).toBe(1);
                });

                it('should invoke ctrl.updateStateDelegate ' +
                    'with sortDirection = 2, and all the others to be 1', function () {

                    ctrl._updateStateAndExecuteUpdateStateDelegate(null, null, null, 2);

                    var expectedState = {
                        id: ctrl.tableId,
                        type: 'DATA',
                        value: {
                            pageSize: 1,
                            page: 1,
                            sortBy: 1,
                            sortDirection: 2
                        },
                        immediate: true
                    };

                    expect(ctrl.updateStateDelegate).
                        toHaveBeenCalledWith(expectedState);

                    expect(ctrl.pageSize).toBe(1);
                    expect(ctrl.page).toBe(1);
                    expect(ctrl.sortBy).toBe(1);
                    expect(ctrl.sortDirection).toBe(2);
                });

            });

        });

        describe('public methods', function () {

            var retFromCreateKendoGridElement = 'retFromCreateKendoGridElement';
            var retFromCompileKendoGridElement = 'retFromCompileKendoGridElement';
            var someDataArray = ['someData'];

            beforeEach(function () {
                spyOn(ctrl, '_expandTableSettings');
                spyOn(ctrl, '_createKendoGridElement').and
                    .returnValue(retFromCreateKendoGridElement);
                spyOn(ctrl, '_compileKendoGridElement').and
                    .returnValue(retFromCompileKendoGridElement);
                spyOn(ctrl, '_replaceElementWithKendoElement');
                ctrl.tableModel = someDataArray;

            });

            describe('renderTable', function () {

                it('should invoke _expandTableSettings ' +
                    'with ctrl.tableModel', function () {
                    ctrl.renderTable();
                    expect(ctrl._expandTableSettings).toHaveBeenCalledWith(someDataArray);
                });

                it('should invoke _createKendoGridElement', function () {
                    ctrl.renderTable();
                    expect(ctrl._createKendoGridElement).toHaveBeenCalled();
                });

                it('should invoke _compileKendoGridElement ' +
                    'with the returned value from _createKendoGridElement',
                    function () {
                        ctrl.renderTable();
                        expect(ctrl._compileKendoGridElement)
                            .toHaveBeenCalledWith(retFromCreateKendoGridElement);
                    }
                );

                it('should invoke _replaceElementWithKendoElement ' +
                    'with the returned value from _compileKendoGridElement', function () {
                    ctrl.renderTable();
                    expect(ctrl._replaceElementWithKendoElement)
                        .toHaveBeenCalledWith(retFromCompileKendoGridElement);

                });

            });

            describe('onDataBound', function () {
                beforeEach(function () {
                    spyOn(ctrl, '_updateStateAndExecuteUpdateStateDelegate');
                    spyOn(ctrl, '_setGrouping');
                });

                it('should invoke _updateStateAndExecuteUpdateStateDelegate ' +
                    'with pageSize = 3, offset = 0, page = 1', function () {

                    ctrl.pageSize = 2;
                    var arg = {
                        sender: {
                            dataSource: {
                                view: function () {
                                    return [];
                                },
                                _pageSize: 3,
                                sort: angular.noop
                            },
                            element: {
                                hasClass: function () {return false;}
                            },
                            items: function () {return [];}
                        }
                    };

                    ctrl.onDataBound(arg);
                    expect(ctrl._updateStateAndExecuteUpdateStateDelegate).
                        toHaveBeenCalledWith(3, 1);
                });

                it('should invoke _updateStateAndExecuteUpdateStateDelegate ' +
                    'with pageSize = null, offset = 0, page = 1' +
                    ',OTHER_FIELD, DESC', function () {

                    ctrl.pageSize = 3;
                    ctrl.sortBy = 'ANY_FIELD';
                    ctrl.sortDirection = "DESC";

                    var arg = {
                        sender: {
                            dataSource: {
                                view: function () {
                                    return [];
                                },
                                _pageSize: 3,
                                sort: function () {
                                    return [{
                                        field: 'OTHER_FIELD',
                                        dir: 'desc'
                                    }];
                                }
                            },
                            element: {
                                hasClass: function () {return false;}
                            },
                            items: function () {return [];}
                        }
                    };

                    ctrl.onDataBound(arg);
                    expect(ctrl._updateStateAndExecuteUpdateStateDelegate).
                        toHaveBeenCalledWith(null, 1, 'OTHER_FIELD', 'DESC');
                });

                it('should invoke _updateStateAndExecuteUpdateStateDelegate ' +
                    'with pageSize = null, offset = 0, page = 1' +
                    ',OTHER_FIELD,ASC', function () {

                    ctrl.pageSize = 3;
                    ctrl.sortBy = 'ANY_FIELD';
                    ctrl.sortDirection = "DESC";

                    var arg = {
                        sender: {
                            dataSource: {
                                view: function () {
                                    return [];
                                },
                                _pageSize: 3,
                                sort: function () {
                                    return [{
                                        field: 'ANY_FIELD',
                                        dir: 'asc'
                                    }];
                                }
                            },
                            element: {
                                hasClass: function () {return false;}
                            },
                            items: function () {return [];}
                        }
                    };

                    ctrl.onDataBound(arg);
                    expect(ctrl._updateStateAndExecuteUpdateStateDelegate).
                        toHaveBeenCalledWith(null, 1, 'ANY_FIELD', 'ASC');
                });

                it('should invoke _updateStateAndExecuteUpdateStateDelegate ' +
                    'with pageSize = null, offset = 0, page = 1' +
                    ',OTHER_FIELD,ASC', function () {

                    ctrl.pageSize = 5;
                    ctrl.sortBy = 'ANY_FIELD';
                    ctrl.sortDirection = "DESC";
                    ctrl.page = 3;

                    var arg = {
                        sender: {
                            dataSource: {
                                view: function () {
                                    return [];
                                },
                                _pageSize: 5,
                                _page: 4,
                                sort: function () {
                                    return [{
                                        field: 'ANY_FIELD',
                                        dir: 'desc'
                                    }];
                                }
                            },
                            element: {
                                hasClass: function () {return false;}
                            },
                            items: function () {return [];}
                        }
                    };

                    ctrl.onDataBound(arg);
                    expect(ctrl._updateStateAndExecuteUpdateStateDelegate).
                        toHaveBeenCalledWith(null, 4);
                });

                it('should not invoke _updateStateAndExecuteUpdateStateDelegate', function () {

                    ctrl.pageSize = 5;
                    ctrl.sortBy = 'ANY_FIELD';
                    ctrl.sortDirection = "DESC";
                    ctrl.page = 4;

                    var arg = {
                        sender: {
                            dataSource: {
                                view: function () {
                                    return [];
                                },
                                _pageSize: 5,
                                _page: 4,
                                sort: function () {
                                    return [{
                                        field: 'ANY_FIELD',
                                        dir: 'desc'
                                    }];
                                }
                            },
                            element: {
                                hasClass: function () {return false;}
                            },
                            items: function () {return [];}
                        }
                    };

                    ctrl.onDataBound(arg);
                    expect(ctrl._updateStateAndExecuteUpdateStateDelegate).not.
                        toHaveBeenCalled();
                });

            });

        });

        describe('watchers', function () {

            beforeEach(function () {
                watchTableModelSpy = spyOn(ctrl, '_watchTableModel').and.returnValue(undefined);
                spyOn(ctrl, '_watchTableModelAction');
            });

            describe('tableData', function () {

                it('should invoke _watchTableModel on digest', function () {
                    $scope.$digest();
                    expect(ctrl._watchTableModel).toHaveBeenCalled();
                });

                it('should invoke _watchTableModelAction when tableData changes', function () {
                    watchTableModelSpy.and.returnValue('changed');
                    $scope.$digest();
                    expect(ctrl._watchTableModelAction).toHaveBeenCalled();
                });

            });

        });

    });
});

