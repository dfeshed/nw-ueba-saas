angular.module("Fortscale").controller("WidgetFilters", ["$scope", "$q", "reports", function($scope, $q, reports){
    var isNewFilter,
        currentFilterIndex;

    $scope.currentFilters = [];

    $scope.addFilter = function(){
        $scope.modalSettings = {
            show: true,
            width: 600,
            height: 350
        };

        $scope.openFilter = angular.copy($scope.widget.filters.fields[0]);
        isNewFilter = true;
    };

    function getFilterSearch(term){
        var field = {};
        field[$scope.openFilter.field] = { type: $scope.openFilter.type };

        return {
            "query": {
                "searchId": "search",
                "dataSource": "database",
                "endpoint": {
                    "entity": $scope.widget.filters.entity,
                    "method": "search"
                },
                "query": {
                    "entity": $scope.widget.filters.entity,
                    "conditions": [
                        {
                            "field": $scope.openFilter.field,
                            "operator": "contains",
                            "value": term
                        }
                    ],
                    "groupBy": $scope.openFilter.field
                },
                "options": {
                    "count": 10
                },
                "fields": field,
                "params": [
                    {
                        "field": $scope.openFilter.field,
                        "type": $scope.openFilter.type,
                        "dashboardParam": "term"
                    }
                ]
            }
        };
    }

    $scope.onFilterSelect = function(){
        var filter;
        for(var i= 0, currentFilter; i < $scope.widget.filters.length; i++){
            currentFilter = $scope.widget.filters[i];
            if (currentFilter.field === $scope.openFilter.field){
                $scope.openFilter.name = currentFilter.name;
                return;
            }
        }
    };

    $scope.filterSearchSettings = {
        "resultField": "name",
        "value": "#/d/user/{{id}}",
        "onSelect": function(value){
            if (!$scope.openFilter.values)
                $scope.openFilter.values = [];

            $scope.openFilter.values.push({
                value: value,
                operator: "equals"
            });
        },
        "search": function(term){
            var deferred = $q.defer(),
                report = getFilterSearch(term);

            reports.runReport(report, { term: term }).then(function(results){
                var formattedResults = [];

                angular.forEach(results.data, function(result){
                    formattedResults.push({
                        label: result[$scope.openFilter.field],
                        value: result[$scope.openFilter.field]
                    })
                });

                deferred.resolve(formattedResults);
            }, deferred.reject);

            return deferred.promise;
        },
        "showValueOnSelect": false
    };
    $scope.removeFilterValue = function(valueIndex){
        $scope.openFilter.values.splice(valueIndex, 1);
    };

    $scope.saveFilter = function(){
        if (isNewFilter)
            $scope.currentFilters.push($scope.openFilter);
        else
            $scope.currentFilters[currentFilterIndex] = $scope.openFilter;

        $scope.cancelFilter();
    };

    $scope.cancelFilter = function(){
        $scope.openFilter = null;
        $scope.modalSettings.show = false;
    }
}]);