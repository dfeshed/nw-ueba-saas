(function () {
    'use strict';

    angular.module("Fortscale").controller("GeoHoppingPackageController",
        ["$scope", "$timeout", "geoHopping", "eventBus", "state", "utils",
            function ($scope, $timeout, geoHopping, eventBus, state, utils) {

                function destroy () {
                    state.onStateChange.unsubscribe(setEvents);
                }

                function setEvents (e, data) {
                    if (data && data.params && (data.params.subDashboardId || data.params.dashboardId)) {
                        destroy();
                        return;
                    }

                    $timeout.cancel(getDataTimeout);

                    getDataTimeout = $timeout(function () {
                        if ($scope.widget.parent) {
                            var params = utils.objects.extend({}, state.currentParams, $scope.widget.parent.getState());
                            $scope.$parent.packageLoading = true;
                            $scope.widget.isLoading = true;
                            geoHopping.getUserEvents(params.notifications_events_dates &&
                                params.notifications_events_dates.split(",")[0],
                                params.notifications_events_dates && params.notifications_events_dates.split(",")[1],
                                params.user).then(function (userEvents) {
                                    $scope.userEvents = userEvents;
                                    $scope.usersTitle = "user";
                                    $scope.isMultipleUsers = false;
                                    if ($scope.userEvents && $scope.userEvents.length) {
                                        var userCount = parseInt($scope.userEvents.length);
                                        if (!isNaN(userCount) && userCount > 1) {
                                            $scope.usersTitle = userCount + " users";
                                            $scope.isMultipleUsers = true;
                                        }
                                    }
                                    $scope.showAllUsers();
                                    $scope.widget.isLoading = false;
                                    $scope.widget.error = null;
                                }, function () {
                                    $scope.widget.loading = false;
                                    $scope.widget.error = "Error getting geo hopping data from server.";
                                });
                        }
                    }, 200);
                }

                function init () {
                    setEvents();
                    state.onStateChange.subscribe(setEvents);
                }


                var getDataTimeout;

                $scope.graphSettings = geoHopping.graphSettings;

                $scope.view = {
                    "type": "table",
                    settings: geoHopping.tableSettings
                };

                $scope.$on("$destroy", destroy);
                $scope.$on("refresh", function () {
                    setEvents();
                });


                $scope.showUser = function (user) {
                    if (user) {
                        geoHopping.getTableData($scope.view, user.events,
                            state.currentParams).then(function (tableData) {
                                $scope.view.data = tableData;
                            });
                        $scope.currentGeoHoppingUser = user;
                    }
                    else {
                        $scope.showAllUsers();
                    }
                };


                $scope.showAllUsers = function () {
                    var allEvents = [];
                    $scope.userEvents.forEach(function (user) {
                        allEvents = allEvents.concat(user.events);
                    });
                    $scope.currentGeoHoppingUser =
                    {name: $scope.userEvents.length === 1 ? $scope.userEvents[0].name : "All Users"};
                    geoHopping.getTableData($scope.view, allEvents, state.currentParams).then(function (tableData) {
                        $scope.view.data = tableData;
                    });
                };


                init();
            }]);
}());
