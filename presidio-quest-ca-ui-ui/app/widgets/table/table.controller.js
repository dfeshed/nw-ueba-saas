(function () {
    'use strict';

    angular.module("TableWidget").controller("TableWidgetController",
        ["$scope", "utils", "state", "events", function ($scope, utils, state, events) {
            var currentSortField,
                sortDirection = 1,
                sortByField;

            $scope.sortTable = function (field) {
                if (!field.sortBy || field.sortDisabled) {
                    return;
                }

                if (field.sortBy === currentSortField) {
                    sortDirection *= -1;
                } else {
                    sortDirection = field.defaultSortDirection || 1;
                    currentSortField = field.sortBy;
                    if (sortByField) {
                        sortByField.sortDirection = 0;
                    }

                    sortByField = field;
                }

                field.sortDirection = sortDirection;

                $scope.$emit("tableSort", {direction: sortDirection, field: currentSortField});
                $scope.$emit("widgetDataSort",
                    {orderBy: currentSortField, orderByDirection: sortDirection === -1 ? "DESC" : "ASC"});

                if ($scope.view.settings.sortParam) {
                    var sortParams = {};
                    sortParams[$scope.view.settings.sortParam] = currentSortField;
                    sortParams[$scope.view.settings.sortParam + "_dir"] = sortDirection;

                    state.setParams(sortParams);
                }
            };

            $scope.tableFieldClick = function ($event, fieldData, fieldIndex, row, rowDataIndex) {
                var field = fieldData.field,
                    rowData = $scope.widget.rawData[rowDataIndex];

                $scope.$emit("tableClick", {
                    $event: $event,
                    field: field,
                    data: fieldData,
                    rawData: rowData[fieldData.field.id],
                    index: fieldIndex
                });
                if (field.events) {
                    if (field.events.click) {
                        $event.preventDefault();
                        if (field.events.click.action) {
                            events.triggerDashboardEvent(field.events.click, rowData, state.currentParams);
                        }
                        return false;
                    }
                }
            };

            $scope.initFilter = function (field) {
                if (!field.filter) {
                    return;
                }

                if (field.filter.defaultValue !== undefined) {
                    $scope.widget.params[field.filter.dashboardParam] = field.filter.value = field.filter.defaultValue;
                    field.filter.enabled = true;
                }
            };

            $scope.filterTable = function (field) {
                if (!field.filter) {
                    return;
                }

                $scope.widget.params[field.filter.dashboardParam] = field.filter.value;
                if (field.filter.value !== undefined) {
                    field.filter.enabled = true;
                }

                field.filter.lastAppliedValue = field.filter.value;
                if (!$scope.widget.query.options) {
                    $scope.widget.query.options = {};
                }

                $scope.widget.report.options.offset = 0;
                field.filter.enabled = field.filter.value !== field.filter.noFilterValue;
                $scope.runWidgetReport($scope.widget, true);
            };

            $scope.filterInputKeyDown = function (field, e) {
                if (e.keyCode === 13) {
                    $scope.filterTable(field);
                } else if (e.keyCode === 27) {
                    $scope.closeFilter(field);
                }
            };

            $scope.toggleFilter = function (field) {
                if (!!(field.filter.open = !field.filter.open)) {
                    $scope.currentFilter = field.filter;
                } else {
                    $scope.currentFilter = null;
                }
            };

            $scope.closeFilter = function (field) {
                field.filter.open = false;
                field.filter.value = field.filter.lastAppliedValue !== undefined ? field.filter.lastAppliedValue :
                    field.filter.defaultValue;
                $scope.currentFilter = null;
                field.filter.enabled = field.filter.value !== field.filter.noFilterValue;
            };

            $scope.resetFilterTable = function (field) {
                if (!field.filter) {
                    return;
                }

                field.filter.lastAppliedValue =
                    field.filter.value = $scope.widget.params[field.filter.dashboardParam] = field.filter.noFilterValue;
                field.filter.enabled = false;
                $scope.widget.report.options.offset = 0;
                $scope.runWidgetReport($scope.widget, true);
                field.filter.open = false;
                $scope.currentFilter = null;
            };

            $scope.pageTable = function () {
                $scope.$emit("pageData", {
                    page: $scope.tablePagingData.currentPage,
                    pageSize: $scope.tablePagingData.itemsPerPage,
                    offset: ($scope.tablePagingData.currentPage - 1) * $scope.tablePagingData.itemsPerPage
                });
            };

            function init () {
                angular.forEach($scope.view.settings.fields, function (field) {
                    if (field.sortBy && field.sortDirection) {
                        sortByField = field;
                        currentSortField = field.sortBy;
                        sortDirection = field.sortDirection;
                    }
                });

                if ($scope.view.settings.allowPaging) {
                    $scope.$watch("widget.totalResults", setPaginationData);
                    $scope.$watch("view.dataTotalResults", setPaginationData);
                }
            }

            init();

            function setPaginationData () {
                if (($scope.widget && $scope.widget.totalResults) || $scope.view.dataTotalResults) {
                    $scope.tablePagingData = {
                        itemsPerPage: $scope.view.settings.pageSize,
                        totalCount: $scope.widget.totalResults || $scope.view.dataTotalResults,
                        currentPage: $scope.view.settings.page || 1
                    };
                }

                if ($scope.view.settings.onDragStart) {
                    var onDragStart = $scope.view.settings.onDragStart;
                    $scope.view.settings.onDragStart = function (event, table) {
                        onDragStart(event, {data: $scope.view.data.rows[table.rowIndex]});
                    };
                }
            }
        }]);

}());
