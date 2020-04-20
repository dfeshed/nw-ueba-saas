(function () {
    'use strict';

    function ExploreController ($scope, $timeout, Explore, dataEntities, state, dataViewTypes, utils,
                                popupConditions) {
        var vm = this,
            lockStateOnChange,
            stateChangeTimeout;

        function onEntityChange (dataEntity) {
            // init();
            window.location.hash = "#/d/explore/" + dataEntity.id;
        }

        function setDataEntity (dataEntityId) {
            vm.selectedEntity = dataEntities.getEntityById(dataEntityId);
            vm.explore = new Explore(vm.selectedEntity, $scope.view.settings, $scope.widget.getState());
            vm.explore.unDirty();
            state.__explore__ = vm.explore;
        }

        function onFiltersChange (filters) {
            // Filters were deleted
            if (filters && filters.filtersRemoved) {
                // Reset flag
                filters.filtersRemoved = false;
            }
            // Rest of the cases
            else {
                var popupMessage = popupConditions.shouldNotifyPopup(vm.explore.defaultFilters);
                if (popupMessage !== "") {
                    /* jshint undef:false */
                    bootbox.confirm({
                        message: popupMessage,
                        buttons: {
                            'cancel': {
                                label: 'No'
                            },
                            'confirm': {
                                label: 'Yes'
                            }
                        },
                        callback: function (result) {
                            if (result) {
                                state.setParams(vm.explore.getParams());
                            }
                        }
                    });
                } else {
                    state.setParams(vm.explore.getParams());
                }
            }
        }

        function applyDefaultFilters () {
            onFiltersChange();
            vm.explore.defaultFilters.unDirty();
        }

        function onStateChange (e, data) {
            if (lockStateOnChange) {
                lockStateOnChange = false;
            }
            else {
                stateChangeTimeout = $timeout(function () {
                    if (data.params && data.params.entityId) {
                        setDataEntity(data.params.entityId);
                    }

                    if (vm.explore) {
                        // Reset page number if any kind of filters were changed
                        if (data.params.filters || data.params.default_filters) {
                            data.params.tableview_page = 1;
                        }
                        vm.explore.setParams(data.params);
                        vm.explore.unDirty();
                    }
                }, 40);
            }
        }

        function init () {
            var allDataEntities = dataEntities.getAllEntities();

            vm.dataEntities = [];

            for (var entity of allDataEntities) {
                if (entity.showInExplore && !entity.isAbstract) {
                    vm.dataEntities.push(entity);
                }
            }

            vm.onEntityChange = onEntityChange;
            vm.onFiltersChange = onFiltersChange;
            vm.applyDefaultFilters = applyDefaultFilters;
            vm.modes = dataViewTypes.typesArray;

            state.onStateChange.subscribe(onStateChange);

            var entityId = $scope.widget.getState().entityId;
            if (entityId) {
                setDataEntity(entityId);
            }
        }

        /**
         * Init
         */

        init();

        /**
         * Cleanup
         */

        $scope.$on("$destroy", function () {
            $timeout.cancel(stateChangeTimeout);
            state.onStateChange.unsubscribe(onStateChange);
        });

        /*
         * Update Explore data views upon URL change.
         */
        $scope.$on("locationChange", function (event, args) {
            var callStateChange = false;

            // Make sure Explore instance exists
            if (vm && vm.explore) {
                // Parse params of both URLs
                var newParams = utils.url.parseUrlParams(args.newUrl);
                var oldParams = utils.url.parseUrlParams(args.oldUrl);

                if (Object.keys(newParams).length === 0) {
                    // Trigger state change if URL has no params, so defaults will be set
                    callStateChange =
                        newParams.initParams = newParams.initDefaultFilters = newParams.initFilters = true;
                } else {
                    // If default filters were removed, initialize them
                    if (!newParams.default_filters && oldParams.default_filters) {
                        callStateChange = newParams.initDefaultFilters = true;
                    }
                    // If filters were removed, initialize as well
                    if (!newParams.filters && oldParams.filters) {
                        callStateChange = newParams.initFilters = true;
                    }
                }

                if (callStateChange) {
                    onStateChange(null, {"params": newParams});
                }
            }
        });
    }


    ExploreController.$inject =
        ["$scope", "$timeout", "Explore", "dataEntities", "state", "dataViewTypes", "utils",
            "popupConditions"];

    angular.module("ExploreWidget").controller("ExploreController", ExploreController);

})();
