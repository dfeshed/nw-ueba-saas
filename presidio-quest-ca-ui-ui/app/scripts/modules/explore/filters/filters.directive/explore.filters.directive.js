(function () {
    'use strict';

    /**
     * The filters selection for the explore widget
     * @param Filter
     * @param FilterCollection
     * @param dataEntities
     * @param DataEntity
     */
    function exploreFiltersDirective (Filter, FilterCollection, dataEntities, DataEntity) {

        function linkFn (scope, element, attrs, ngModel) {

            function init () {
                filtersCollection = new FilterCollection();
                scope.filtersCollection = filtersCollection;

                scope.applyFilters = applyFilters;
                scope.toggleAddFilter = function () {
                    scope.showAddFilter = !scope.showAddFilter;
                };
                scope.addFilter = addFilter;
                scope.removeFilter = removeFilter;

                scope.$watch("entity", function (entity) {
                    if (entity !== scope.entity) {
                        scope.entity = entity;
                        setAvailableFields();
                    }
                });

                scope.$watch("excludedFields", setAvailableFields);

                if (ngModel) {
                    ngModel.$render = function () {
                        if (ngModel.$viewValue) {
                            filtersCollection = FilterCollection.copy(ngModel.$viewValue);
                            ngModel.$viewValue.onFiltersChange.subscribe(onModelChange);
                        }
                        else {
                            filtersCollection = new FilterCollection();
                        }

                        scope.filtersCollection = filtersCollection;
                    };
                }
            }

            /**
             * Get available fields for filters
             */
            function setAvailableFields () {
                if (!scope.entity) {
                    return;
                }

                scope.filterGroups = [getEntityFilterGroup(scope.entity)];

                if (scope.entity.linkedEntities) {
                    scope.entity.linkedEntities.forEach(function (linkedEntity) {
                        scope.filterGroups.push(getEntityFilterGroup(linkedEntity));
                    });
                }
            }

            function getEntityFilterGroup (linkedEntity) {
                var entity = linkedEntity instanceof DataEntity ? linkedEntity :
                    dataEntities.getEntityById(linkedEntity.entity);

                var availableFields = entity.fieldsArray;

                // remove internal fields
                availableFields = availableFields.filter(function (field) {
                    return !(~field.attributes.indexOf("internal"));
                });

                // exclude fields according to ID
                if (scope.excludedFields) {
                    availableFields = availableFields.filter(function (field) {
                        return !~scope.excludedFields.indexOf(field.id);
                    });
                }

                //exclude all fields that specific for users entity
                if (scope.entity.name !== "Users") {
                    availableFields = availableFields.filter(function (field) {
                        return !( field.shownForSpecificEntity !== undefined &&
                        field.entity.id === field.shownForSpecificEntity);
                    });
                }

                return {name: entity.name, fields: availableFields, isOpen: true};
            }

            function onModelChange () {
                scope.filtersCollection = filtersCollection = FilterCollection.copy(ngModel.$viewValue);
            }

            function applyFilters () {
                if (!filtersCollection.isDirty) {
                    return false;
                }

                ngModel.$setViewValue(filtersCollection);

                if (scope.onFiltersChange) {
                    scope.onFiltersChange({filters: filtersCollection});
                }

                filtersCollection.unDirty();
            }

            function addFilter (dataEntityField) {
                var newFilter = new Filter(dataEntityField);
                filtersCollection.addFilter(newFilter);
                scope.showAddFilter = false;
            }

            function removeFilter (filter) {
                // Remove filter from local collection
                filtersCollection.removeFilter(filter);

                if (scope.onFiltersChange) {
                    // Add a flag indicating filters were removed from this collection
                    filtersCollection.filtersRemoved = true;

                    // Update URL and state parameters
                    scope.onFiltersChange({filters: filtersCollection});
                }
            }

            var filtersCollection;

            init();

        }

        return {
            templateUrl: "scripts/modules/explore/filters/filters.directive/explore.filters.directive.template.html",
            restrict: 'E',
            require: '?ngModel',
            replace: true,
            scope: {
                entity: "=",
                excludedFields: "=",
                onFiltersChange: "&"
            },
            link: linkFn
        };

    }

    angular.module("Explore.Filters").directive("exploreFilters",
        ["Filter", "FilterCollection", "dataEntities", "DataEntity", exploreFiltersDirective]);
})();
