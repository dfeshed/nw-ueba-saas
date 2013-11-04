angular.module("TableWidget").controller("TableWidgetController", ["$scope", "$timeout", "widgets", function($scope, $timeout, widgets){
    var currentSortField,
        sortDirection = 1,
        sortByField;

    var sortTypeParsers = {
        date: function(value){
            return new Date(value);
        },
        float: function(value){
            return parseFloat(value, 10);
        },
        ip: function(value){
            var ipMatch = value.match(/^(\d{1,3})\.(\d{1,3})\.(\d{1,3})\.(\d{1,3})$/);
            if (ipMatch)
                return parseInt(ipMatch[1], 10) * 1000000000 + parseInt(ipMatch[2], 10) * 1000000 + parseInt(ipMatch[3], 10) * 1000 + parseInt(ipMatch[4], 10);

            return value;
        },
        number: function(value){
            return parseInt(value, 10);
        },
        string: function(value){
            return String(value);
        }
    };

    angular.forEach($scope.view.settings.fields, function(field){
        if (field.sortBy && field.sortDirection){
            sortByField = field;
            currentSortField = field.sortBy;
            sortDirection = field.sortDirection;
        }
    });
    $scope.sortTable = function(field, view, widgetParams){
        if (!field.sortBy)
            return;

        if (field.sortBy === currentSortField)
            sortDirection *= -1;
        else{
            sortDirection = 1;
            currentSortField = field.sortBy;
            if (sortByField)
                sortByField.sortDirection = 0;

            sortByField = field;
        }

        field.sortDirection = sortDirection;

        //if (view.settings.allowPaging && $scope.tablePagingData.totalCount > $scope.tablePagingData.itemsPerPage){
        /*
            if ($scope.widget){
                $scope.widget.params.sort = (sortDirection === 1 ? "" : "-") + currentSortField;
                $scope.runWidgetReport($scope.widget, true);
            }
            */
            $scope.$emit("tableSort", { direction: sortDirection, field: currentSortField })
        //}
        /*
        else{
            var fieldSettings = $scope.widget.report.query.fields[field.sortBy],
                parser = sortTypeParsers[fieldSettings.type] || function(value){ return value;},
                fieldIsArray = fieldSettings.isArray;

            view.rawData.sort(function(row1, row2){
                var sortVal = 0,
                    val1 = row1[field.sortBy], val2 = row2[field.sortBy];

                if (fieldIsArray){
                    val1 = val1.join("");
                    val2 = val2.join("");
                }

                val1 = parser(val1);
                val2 = parser(val2);

                if (val1 > val2)
                    sortVal = 1;
                else if (val1 < val2)
                    sortVal = -1;

                return sortVal * sortDirection;
            });

            view.data = widgets.setViewValues(view, view.rawData, widgetParams);
        }*/
    };

    $scope.tableFieldClick = function($event, fieldData, field, fieldIndex){
        $scope.$emit("tableClick", { $event: $event, field: field, data: fieldData, index: fieldIndex });
    };

    $scope.initFilter = function(field){
        if (!field.filter)
            return;

        if (field.filter.defaultValue !== undefined){
            $scope.widget.params[field.filter.dashboardParam] = field.filter.value = field.filter.defaultValue;
            field.filter.enabled = true;
        }
    };

    $scope.filterTable = function(field){
        if (!field.filter)
            return;

        $scope.widget.params[field.filter.dashboardParam] = field.filter.value;
        if (field.filter.value !== undefined)
            field.filter.enabled = true;

        field.filter.lastAppliedValue = field.filter.value;
        if (!$scope.widget.report.query.options)
            $scope.widget.report.query.options = {};

        $scope.widget.report.query.options.offset = 0;
        field.filter.enabled = field.filter.value !== field.filter.noFilterValue;
        $scope.runWidgetReport($scope.widget, true);
    };

    $scope.filterInputKeyDown = function(field, e){
        if (e.keyCode === 13)
            $scope.filterTable(field);
        else if (e.keyCode === 27)
            $scope.closeFilter(field);
    };

    $scope.toggleFilter = function(field){
        if (field.filter.open = !field.filter.open)
            $scope.currentFilter = field.filter;
        else
            $scope.currentFilter = null;
    };

    $scope.closeFilter = function(field){
        field.filter.open = false;
        field.filter.value = field.filter.lastAppliedValue !== undefined ? field.filter.lastAppliedValue : field.filter.defaultValue;
        $scope.currentFilter = null;
        field.filter.enabled = field.filter.value !== field.filter.noFilterValue;
    };

    $scope.resetFilterTable = function(field){
        if (!field.filter)
            return;

        field.filter.lastAppliedValue = field.filter.value = $scope.widget.params[field.filter.dashboardParam] = field.filter.noFilterValue;
        field.filter.enabled = false;
        $scope.widget.report.query.options.offset = 0;
        $scope.runWidgetReport($scope.widget, true);
        field.filter.open = false;
        $scope.currentFilter = null;
    };

    $scope.pageTable = function(){
        /*
        if ($scope.widget && $scope.widget.report){
            $scope.widget.report.query.options = $scope.widget.report.query.options || {};
            $scope.widget.report.query.options.offset = $scope.tablePagingData.itemsPerPage * ($scope.tablePagingData.currentPage - 1);
            $scope.runWidgetReport($scope.widget, true);
        }
          */
        $scope.$emit("tablePage", { page: $scope.tablePagingData.currentPage, pageSize: $scope.tablePagingData.itemsPerPage });
    };

    if ($scope.view.settings.allowPaging){
        $scope.$watch("widget.totalResults", setPaginationData);
        $scope.$watch("view.dataTotalResults", setPaginationData);
    }
    function setPaginationData(){
        if (($scope.widget && $scope.widget.totalResults) || $scope.view.dataTotalResults){
            $scope.tablePagingData = {
                itemsPerPage: $scope.view.settings.pageSize,
                totalCount: $scope.widget.totalResults || $scope.view.dataTotalResults,
                currentPage: $scope.view.settings.page || 1
            };
        }
    }
}]);
