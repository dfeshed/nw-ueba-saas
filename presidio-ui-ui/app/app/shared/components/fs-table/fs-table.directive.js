(function () {
    'use strict';

    /**
     * Holds custom sorting
     */
    var customSort = {
        generic: function (fieldName) {
            return function (a, b, descending) {
                return ((descending !== undefined) ? -1 : 1) * (a[fieldName] - b[fieldName]);
            };
        }
    };
    customSort.number = customSort.generic;

    function fsTableDirective () {

        function linkFn (scope, element, attrs, ctrl) {

            // Binding to the table data
            scope.$watch(
                function () {
                    return ctrl._watchTableModel();
                },
                function () {
                    return ctrl._watchTableModelAction();
                }
            );

            ctrl._initPagingSettings();

            if (ctrl.fetchStateDelegate) {
                scope.$watch(function () {
                    return ctrl.fetchStateDelegate(ctrl.tableId);
                }, ctrl._fetchStateDelegateWatchAction.bind(ctrl));
            }

        }

        /**
         * The directive's controller function
         *
         * @constructor
         */
        function FsTableController ($element, $scope, $compile) {
            this.init($element, $scope, $compile);
        }

        angular.extend(FsTableController.prototype, {

            /**
             * Validations
             */

            /**
             * Validate fetchStateDelegate.
             * Throw TypeError if fetchStateDelegate is received and is not a function
             * @private
             */
            _validateFetchStateDelegate: function _validateFetchStateDelegate () {
                if (this.fetchStateDelegate && !angular.isFunction(this.fetchStateDelegate)) {
                    throw new TypeError('fsTable.directive: FsTableController: ' +
                        'If fetchStateDelegate is provided, it must be a function.');
                }
            },
            /**
             * Validate updateStateDelegate.
             * Throw TypeError if updateStateDelegate is received and is not a function
             * @private
             */
            _validateUpdateStateDelegate: function _validateUpdateStateDelegate () {
                if (this.updateStateDelegate && !angular.isFunction(this.updateStateDelegate)) {
                    throw new TypeError('fsTable.directive: FsTableController: ' +
                        'If updateStateDelegate is provided, it must be a function.');
                }
            },

            /**
             * Validate values of sort direction
             * @private
             */
            _validateSortDirection: function _validateSortDirection () {
                if (this.sortDirection &&
                    this.sortDirection !== 'DESC' &&
                    this.sortDirection !== 'ASC') {
                    throw new Error('fsTable.directive: FsTableController: ' +
                        'If updateStateDelegate is provided, it must be "DESC" or "ASC".');
                }
            },

            /**
             * Validate page size
             * @private
             */
            _validatePageSize: function _validatePageSize () {
                if (this.pageSize) {
                    if (!angular.isNumber(this.pageSize)) {
                        throw new TypeError('fsTable.directive: FsTableController: ' +
                            'If pageSize is provided, it must be a number.');
                    }
                    if (this.pageSize < 1) {
                        throw new RangeError('fsTable.directive: FsTableController: ' +
                            'If pageSize is provided, it must greater then 0.');
                    }
                }
            },

            /**
             * Validate the page number
             * @private
             */
            _validatePage: function _validatePage () {
                if (this.page) {
                    if (!angular.isNumber(this.page)) {
                        throw new TypeError('fsTable.directive: FsTableController: ' +
                            'If page is provided, it must be a number.');
                    }
                    if (this.page < 1) {
                        throw new RangeError('fsTable.directive: FsTableController: ' +
                            'If page is provided, it must greater then 0.');
                    }
                }

            },

            /**
             * Initial validation of the controller's constructor.
             * Flow:
             * Invoke _validateFetchStateDelegate
             * Invoke _validateUpdateStateDelegate
             *
             * @private
             */
            _validate: function _validate () {
                this._validateFetchStateDelegate();
                this._validateUpdateStateDelegate();
                this._validateSortDirection();
                this._validatePageSize();
                this._validatePage();
            },

            /**
             * PRIVATE METHODS
             */

            /**
             * Places data on tableSettings model (this is a kendo-ui requirement).
             *
             * @param {array<object>} data
             * @private
             */
            _expandTableSettings: function _expandTableSettings (data) {
                var ctrl = this;

                var meta = data._meta;
                var gridData = {
                    data: data,
                    total: meta.page ? meta.page.totalElements : meta.total
                };

                //Display paging footer with it's abilities, only if
                // this.tableSettings.alwaysPageable set to true, or total amount of rows greater
                // then the page size.
                var pageable = false;
                var sortable = false;

                if ((this.tableSettings && this.tableSettings.alwaysPageable) ||
                    gridData.total && gridData.total > ctrl.pageSize) {
                    pageable = {
                        pageSizes: ctrl.pageSizes,
                        pageSize: ctrl.pageSize,
                        page: ctrl.page
                    };

                }

                if (this.tableSettings && this.tableSettings.sortable) {
                    sortable = this.tableSettings.sortable;
                }

                ctrl.localTableSettings = _.merge({}, {
                    dataSource: {
                        data: gridData,
                        schema: {
                            data: "data",
                            total: "total"
                        },
                        page: ctrl.page,
                        serverPaging: true,
                        serverSorting: true
                    },
                    pageable: pageable,
                    sortable: sortable,
                    detailExpand: function (e) {
                        e.detailRow.addClass('expanded');
                    },
                    detailCollapse: function (e) {
                        e.detailRow.removeClass('expanded');
                    }
                }, this.tableSettings, this._localSort);

                // If detailTableSettings exists, this means that hierarchy is required
                if (this.detailTableModelName) {
                    ctrl.localTableSettings.detailInit = ctrl._detailInit.bind(ctrl);
                }

                // Add custom sorting
                this._setCustomSort(ctrl.localTableSettings.columns);
            },

            /**
             * Create and render the sub table.
             *
             * @param e
             * @private
             */
            _detailInit: function (e) {
                var ctrl = this;

                e.detailRow.addClass('expanded');
                //e.detailRow.find('.k-hierarchy-cell')
                //    .append('<span class="l-shape-vertical"></span>')
                //    .append('<span class="l-shape-horizontal"></span>');
                e.detailRow.find('.k-hierarchy-cell').remove();
                e.detailRow.find('.k-detail-cell').attr('colspan', '9');

                ctrl.localDetailTableSettings = _.merge({}, this.detailTableSettings, {
                    dataSource: {
                        data: e.data[ctrl.detailTableModelName],
                        serverPaging: false,
                        serverSorting: false
                    },
                    pageable: false,
                    sortable: true
                });

                // Create an angular table element from kendo-grid directive
                var tableElement = this._createKendoGridElement();
                tableElement.attr('options', 'table.localDetailTableSettings');
                tableElement.addClass('details-table');

                // Compile and link table element
                tableElement = this._compileKendoGridElement(tableElement);

                // Add distinction to header
                tableElement.find('tr').first().addClass('details-table-header');

                //var element = $("<div/>").kendoGrid(this.localDetailTableSettings);
                //element = ctrl.$compile(element)(ctrl.$scope);
                tableElement.appendTo(e.detailCell);

                ctrl.detailTableWrapper = tableElement;
            },

            /**
             * Creates an angular element from a kendo-grid tag and returns it.
             *
             * @returns {angular.element}
             * @private
             */
            _createKendoGridElement: function _createKendoGridElement () {
                return angular.element('<kendo-grid class="fs-table" options=' +
                    '"table.localTableSettings"  ' +
                    'k-on-data-bound="table.onDataBound(kendoEvent)"' +
                    '>' +
                    '</kendo-grid>');
            },

            /**
             * Sets grouping dynamically. Returns true if new grouping has been set, otherwise it returns false.
             *
             * @param {} dataSource
             * @param {Array<{}>} sortList
             * @param {Array<{}>} columnsList
             * @returns {boolean}
             * @private
             */
            _setGrouping: function (dataSource, sortList, columnsList) {
                var groupingList = [];

                // Make sure all lists are populated as required
                if (sortList && sortList[0] && columnsList && columnsList.length) {

                    // Find the column that corresponds to the sort field
                    var column = _.find(columnsList, {field: sortList[0].field});

                    if (column && column.groupField) {
                        // populate a grouping object
                        groupingList = [{
                            field: column.groupField,
                            dir: sortList[0].dir
                        }];
                    }

                }

                // If no group is made, them remove groups from dataSource and return true
                if (groupingList.length === 0 && dataSource.group().length !== 0) {
                    dataSource.group(groupingList);
                    return true;
                }

                // If the newly created grouping object equals the current, then return false without setting
                if (groupingList.length === 0 ||
                    (dataSource.group()[0] &&
                    dataSource.group()[0].field === groupingList[0].field &&
                    dataSource.group()[0].dir === groupingList[0].dir)) {
                    return false;
                }

                // Set new grouping and return true
                dataSource.group(groupingList);
                return true;
            },

            /**
             * Iterates through columns, and if any have "customSort" it will mount a custom sort function on the
             * column.
             *
             * @param {Array<{}>} columns
             * @private
             */
            _setCustomSort: function (columns) {
                _.each(columns, function (column) {
                    if (column.customSort && column.field) {
                        column.sortable = {
                            compare: customSort[column.customSort](column.field)
                        };
                    }
                });
            },

            /**
             * Kendo's event - triggers when page size changed, page changed, column sorted or other things
             * changed.
             * If page or page size changed- we like to update the state immediately
             * @param arg
             */
            onDataBound: function (arg) {

                var ctrl = this;

                var ds = arg.sender.dataSource;

                // Add class to last and one to last tr's
                var tableRows = arg.sender.items();
                var tableRowsLength = tableRows.length;
                if (tableRowsLength > 5) {
                    $([tableRows[tableRowsLength- 1],tableRows[tableRowsLength- 2]])
                        .addClass('fs-table-last-rows');
                }


                if(this._setGrouping(arg.sender.dataSource, ds.sort(), arg.sender.columns)) {
                    return;
                }



                //User change the page size
                if (ds._pageSize && ds._pageSize !== ctrl.pageSize) {
                    //Page size changed
                    ctrl._updateStateAndExecuteUpdateStateDelegate(ds._pageSize, 1);
                    return; //End function
                }

                //User click on column sort
                if (ds.sort() && ds.sort()[0] &&
                        // The sorting is different for details-table
                    !arg.sender.element.hasClass('details-table')) {

                    // Make sure is not inner table
                    if (this.detailTableWrapper &&
                        arg.sender.wrapper[0] === this.detailTableWrapper[0]) {
                        return;
                    }

                    var sortData = ds.sort()[0];
                    var sortDir = sortData.dir.toUpperCase();
                    var sortField = sortData.field;

                    if (sortField !== ctrl.sortBy || sortDir !== ctrl.sortDirection) {

                        // Create a new local sort object
                        ctrl._localSort = {
                            dataSource: {
                                sort: {
                                    dir: sortDir.toLowerCase(),
                                    field: sortField
                                }
                            }
                        };

                        ctrl._updateStateAndExecuteUpdateStateDelegate(null, 1,
                            sortData.field, sortDir);

                        return; //End function
                    }
                }

                //User change page
                if (ds._page && ds._page !== +ctrl.page) {
                    //First page is 1.
                    ctrl._updateStateAndExecuteUpdateStateDelegate(null, ds._page);
                    return; //End function
                }
            },

            /**
             * @param pageSize -           The new size of the page
             * @param page -               The new page number
             * @param sortBy -             Field id - to sort by
             * @param sortDirection -      Sort direction - DESC / ASC
             * @private
             *
             * This method get the abouve parameters, set them on the controller + state
             * and execute the updateStateDelegate
             */
            _updateStateAndExecuteUpdateStateDelegate: function (pageSize, page, sortBy,
                sortDirection) {

                var ctrl = this;
                var state = {};
                //Update the controller and state
                ctrl._setToStateAndController(ctrl, state, 'pageSize', pageSize);
                ctrl._setToStateAndController(ctrl, state, 'page', page);
                ctrl._setToStateAndController(ctrl, state, 'sortBy', sortBy);
                ctrl._setToStateAndController(ctrl, state, 'sortDirection', sortDirection);

                // If _updatedInternalState flag is true, this means that state was updated from
                // the external state (by fetchStateDelegate) and it does not need to update the
                // external state.
                if (ctrl.updateStateDelegate) {
                    //Invoke ctrl.updateStateDelegate
                    ctrl.updateStateDelegate({
                        id: ctrl.tableId,
                        type: 'DATA',
                        value: state,
                        immediate: true
                    });
                }

            },

            /**
             *  That method get property and value, and if it's not null it set it on the controller.
             *  After that it take the value of the property ---from the controller---
             *  and set it on the state object.
             *
             * @param ctrl
             * @param stateObject
             * @param propertyName
             * @param value
             * @private
             */
            _setToStateAndController: function (ctrl, stateObject, propertyName, value) {
                if (angular.isDefined(value) && value !== null) {
                    ctrl[propertyName] = value;
                }
                stateObject[propertyName] = ctrl[propertyName];
            },
            /**
             * Takes a tableElement angular element, compiles it, and returns it.
             *
             * @param {angular.element} tableElement
             * @returns {angular.element}
             * @private
             */
            _compileKendoGridElement: function _compileKendoGridElement (tableElement) {
                return this.$compile(tableElement)(this.$scope);
            },

            /**
             * Takes a compiled and linked tableElement, and places it on the DOM, replacing current element.
             *
             * @param {angular.element} tableElement
             * @private
             */
            _replaceElementWithKendoElement: function _replaceElementWithKendoElement (tableElement) {
                var prevKendoElement = this.$element.find('[kendo-grid]');
                prevKendoElement.remove();

                this.$element.append(tableElement);
            },

            /**
             * PUBLIC METHODS
             */
            renderTable: function renderTable () {
                var ctrl = this;

                function _renderSequence () {

                    // Places data on tableSettings model
                    ctrl._expandTableSettings(ctrl.tableModel);

                    // Create an angular table element from kendo-grid directive
                    var tableElement = ctrl._createKendoGridElement();

                    // Compile and link table element
                    tableElement = ctrl._compileKendoGridElement(tableElement);

                    // Replace element with new table element
                    ctrl._replaceElementWithKendoElement(tableElement);

                    ctrl._tableElement = tableElement;
                }

                // Make sure data is valid by checking it exists, it has an array data property.
                if (ctrl.tableModel && angular.isArray(ctrl.tableModel)) {
                    if (ctrl.tableModel.length) { //Data returned for page
                        _renderSequence();
                    } else if (!ctrl.tableModel.length && ctrl.tableModel._meta &&
                        this.tableModel._meta.total > 0) {
                        //There is data for filter, but no data for page
                        //The table will navigate to last exists page.
                        var lastPageIndex = _.ceil(ctrl.tableModel._meta.total / ctrl.pageSize);
                        ctrl._updateStateAndExecuteUpdateStateDelegate(ctrl.pageSize, lastPageIndex);
                    } else {
                        // Clean out the directive's element if no alerts are found in model or its meta.
                        ctrl.$element[0].innerHTML = '';
                    }
                }
            },

            /**
             * WATCHERS
             */

            /**
             * A watch function. Returns instance's tableModel property
             *
             * @returns {object}
             * @private
             */
            _watchTableModel: function _watchTableModel () {
                return this.tableModel;
            },
            /**
             * A watch action function. Fires when this.tableModel changes.
             * Invokes renderTable, to render the table when data changes.
             *
             * @private
             */
            _watchTableModelAction: function _watchTableModelAction () {
                if (this.tableModel) {
                    this.renderTable();
                }
            },

            /**
             * This method triggered each time that fetchStateDelegate(ctrl.tableId)
             * return different answer.
             * The method update the controller
             *
             * @param newVal - state object
             * @param oldVal - state object
             * @private
             */
            _fetchStateDelegateWatchAction: function (newVal, oldVal) {

                var ctrl = this;

                if (newVal && (
                    ctrl.pageSize !== newVal.pageSize ||
                    ctrl.page !== newVal.page ||
                    ctrl.sortDirection !== newVal.sortDirection ||
                    ctrl.sortBy !== newVal.sortBy)) {

                    ctrl.page = newVal.page;
                    ctrl.pageSize = newVal.pageSize;
                    ctrl.sortBy = newVal.sortBy;
                    ctrl.sortDirection = newVal.sortDirection;

                    // Create a local search object
                    if (_.isString(ctrl.sortDirection) && ctrl.sortDirection !== '') {
                        ctrl._localSort = {
                            dataSource: {
                                sort: {
                                    dir: ctrl.sortDirection.toLowerCase(),
                                    field: ctrl.sortBy
                                }
                            }
                        };
                    }

                }
            },

            /**
             * Set default values for paging
             * @private
             */
            _initPagingSettings: function _initPagingSettings () {

                if (!this.pageSizes) {
                    this.pageSizes = [10, 20, 50, 100];
                }

                if (!this.pageSize) {
                    this.pageSize = 10;
                }

            },

            /**
             * Init
             */
            init: function init ($element, $scope, $compile) {
                // Put dependencies on the instance

                var ctrl = this;
                ctrl.$element = $element;
                ctrl.$scope = $scope;
                ctrl.$compile = $compile;

                ctrl._validate();

            }
        });

        FsTableController.$inject = ['$element', '$scope', '$compile'];

        return {
            restrict: 'E',
            scope: true,
            link: linkFn,
            controller: FsTableController,
            controllerAs: 'table',
            bindToController: {
                tableId: '@',
                fetchStateDelegate: '=',
                updateStateDelegate: '=',
                tableSettings: '=',
                detailTableSettings: '=',
                tableModel: '=',
                detailTableModelName: '@',
                pageSizes: '=?',
                pageSize: '=?',
                sortBy: '=?',
                sortDirection: '=?'
            },
            require: 'fsTable'
        };
    }

    fsTableDirective.$inject = [];

    angular.module('Fortscale.shared.components.fsTable')
        .directive('fsTable', fsTableDirective);
}());
