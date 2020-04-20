(function () {

    'use strict';

    function FilterCollectionClass (Filter, queryOperators, filters, EventBus, filtersToDataQueriesAdapter,
        $rootScope) {

        /**
         * Constructor for a collection of filters. Parallel to DataQueryCondition
         * @constructor
         */
        function FilterCollection () {
            var filters = [];
            var operator = queryOperators.logicalOperators.AND;
            var FILTERS_CHANGE_EVENT = "filtersChange";
            var self = this;

            var eventBus = EventBus.setToObject(this, [FILTERS_CHANGE_EVENT]);

            this.__defineGetter__("filters", function () {
                return filters;
            });

            this.addFilter = function (filter, index) {
                if (!(filter instanceof Filter)) {
                    throw new TypeError("Invalid filter to add to FilterCollection, expected an instance of Filter.");
                }

                // Avoid adding duplicate filters:
                if (this.containsFilter(filter)) {
                    return this;
                }

                this._isDirty = true;
                if (typeof(index) === "number") {
                    filters.splice(index, 0, filter);
                } else {
                    filters.push(filter);
                }

                Object.observe(filter.validObj, observeFiltersState);
                eventBus.triggerEvent(FILTERS_CHANGE_EVENT, {method: "add", filter: filter});
                return this;
            }.bind(this);

            this.removeFilter = function (filter) {
                var filterPosition = filters.indexOf(filter);
                if (!~filterPosition) {
                    return false;
                }

                this._isDirty = true;
                filter.validObj.isValid = true;
                filter.validObj.isValidOnInit = true;
                Object.unobserve(filter.validObj, observeFiltersState);
                var removedFilter = filters.splice(filterPosition, 1);
                eventBus.triggerEvent(FILTERS_CHANGE_EVENT, {method: "remove", filter: removedFilter});
                return this;
            }.bind(this);

            /**
             * Returns true if the collection contains a filter - checked by value, not by pointer!
             * @type {function(this:FilterCollectionClass.FilterCollection)|*}
             */
            this.containsFilter = function (filter) {
                return filters.some(function (_filter) {
                    return filter.equals(_filter);
                });
            }.bind(this);

            function observeFiltersState () {
                $rootScope.$apply(function () {
                    self._isValid = isAllFiltersValid();
                });
            }

            this.clearFilters = function () {
                while (filters.length) {
                    filters.pop();
                }
                eventBus.triggerEvent(FILTERS_CHANGE_EVENT, {method: "clear"});

                return this;
            };

            this.__defineGetter__("operator", function () {
                return operator;
            });

            this.__defineGetter__("isValid", function () {
                if (this._isValid === undefined) {
                    return true;
                }

                return this._isValid;
            });

            this.__defineSetter__("operator", function (value) {
                var logicalOperator = queryOperators.logicalOperators[value];
                if (!logicalOperator) {
                    throw new Error("Invalid operator for FilterCollection, expected either 'AND' or 'OR'");
                }

                this._isDirty = true;
                operator = logicalOperator;
            }.bind(this));

            this.copy = function (filterCollection) {
                if (!(filterCollection instanceof FilterCollection)) {
                    throw new TypeError("Can't copy FilterCollection, expected an instance of FilterCollection.");
                }

                filters = filterCollection.filters.map(function (filter) {
                    return Filter.copy(filter);
                });

                operator = filterCollection.operator;
                this._isDirty = filterCollection._isDirty;
            };

            this.unDirty = function () {
                var changedFilters = [];
                this._isDirty = false;
                filters.forEach(function (filter) {
                    if (filter.isDirty) {
                        filter.isDirty = false;
                        changedFilters.push(filter);
                    }
                });

                if (changedFilters.length) {
                    eventBus.triggerEvent(FILTERS_CHANGE_EVENT, {method: "unDirty", filters: changedFilters});
                }

                return this;
            };

            this.hasFilterForField = function (dataEntityField) {
                return filters.some(function (filter) {
                    return filter.field === dataEntityField;
                });
            };

            function isAllFiltersValid () {
                for (var i = 0; i < filters.length; i++) {
                    var filter = filters[i];
                    if (!filter.validObj.isValid || !filter.validObj.isValidOnInit) {
                        return false;
                    }
                }
                return true;
            }
        }

        FilterCollection.copy = function (filterCollection) {
            var newFilterCollection = new FilterCollection();
            newFilterCollection.copy(filterCollection);
            return newFilterCollection;
        };

        FilterCollection.prototype.__defineGetter__("isDirty", function () {
            if (this._isDirty) {
                return true;
            }

            for (var filter of this.filters) {
                if (filter.isDirty) {
                    return true;
                }
            }

            return false;
        });

        /**
         * Returns an object with the collection's filters as an array of name/value objects.
         * In the future, should send also the operator of the FilterCollection (AND/OR), when it's used.
         * @returns {*}
         */
        FilterCollection.prototype.getParams = function () {
            return this.filters.map(function (filter) {
                var obj = {};
                obj[filter.field.entity.id + "." + filter.field.id] = filter.getParamValue();
                return obj;
            });

        };

        /**
         * Accepts params and sets the collection's filters accordingly.
         * @param params
         * @returns {FilterCollection}
         */
        FilterCollection.prototype.setParams = function (params) {
            this.clearFilters();

            if (!params) {
                return this;
            }

            var self = this;

            if (params.constructor !== Array) {
                params = [params];
            }

            var filter;
            params.forEach(function (paramObj) {
                for (var paramName in paramObj) {
                    if (paramObj.hasOwnProperty(paramName)) {
                        filter = filters.getFilterFromParam(paramName, paramObj[paramName]);

                        if (filter) {
                            self.addFilter(filter);
                        }
                    }
                }
            });

            return this;
        };

        /**
         *
         * @returns {*}
         */
        FilterCollection.prototype.getDataQuery = function () {
            if (!this.filters.length) {
                return {};
            }

            return {conditions: filtersToDataQueriesAdapter.filterCollectionToConditionTerm(this)};
        };

        return FilterCollection;
    }

    FilterCollectionClass.$inject =
        ["Filter", "queryOperators", "filters", "EventBus", "filtersToDataQueriesAdapter", "$rootScope"];

    angular.module("Explore.Filters").factory("FilterCollection", FilterCollectionClass);
})();
