(function () {
    'use strict';

    angular.module("Fortscale").controller("MainController",
        ["$scope", "$routeParams", "dashboards", "auth", "users", "search", "reports", "configFlags", "utils", "$state",
            "userUtils","FORTSCALE_BRAND_UI",
            function ($scope, $routeParams, dashboards, auth, users, search, reports, configFlags, utils, $state,
                userUtils,FORTSCALE_BRAND_UI) {

                $scope.showBenchmarks = configFlags.qa;

                $scope.printPage = function () {
                    window.print();
                };



                $scope.loadingBranding = {
                    title : FORTSCALE_BRAND_UI?"Presidio UEBA": "Change Auditor"
                } ;

                auth.getCurrentUser()
                    .then(function (userData) {
                        if (userData) {
                            $scope.loggedInUser = userData;
                        }
                        else {

                            console.log("Reditect to Login");
                        }
                    });

                $scope.report = {
                    /**
                     * An error callback function. It prints out an error to the console, unless error object has a
                     * redirectToState property, in which case it redirects to the required state.
                     *
                     * @param {{message:string, redirectToState:string=}|string} error
                     */
                    error: function (error) {
                        var errorStr = typeof(error) === "string" ? error : typeof(error.message) === "string" ?
                            error.message : JSON.stringify(error);
                        if (error.redirectToState) {
                            console.warn(errorStr);
                            $state.go(error.redirectToState);
                        } else {
                            console.error(errorStr);
                        }
                    }
                };

                $scope.$on("$routeChangeSuccess", function () {
                    $scope.currentNav = document.location.hash.split("?")[0];
                    //setActiveNavItem();

                    // Visible only when printing a page in the browser (top right)
                    $scope.loadDate = utils.date.getMoment('now', false).format("MM/DD/YYYY HH:mm");
                });

                $scope.$on("$locationChangeSuccess", function (e, newUrl, oldUrl) {
                    //reports.abortCurrentReports();
                    $scope.$broadcast("locationChange", {
                        newUrl: newUrl,
                        oldUrl: oldUrl
                    });
                    //setActiveNavItem();

                });

                $scope.getCurrentNavRedirect = function () {
                    return encodeURIComponent(document.location.hash);
                };

                $scope.$on("authError", function (e, data) {
                    $scope.modal = {
                        show: true,
                        src: data.status === 403 ? "views/modals/password_expired.html" :
                            "views/modals/session_expired.html"
                    };
                });

                $scope.setCurrentMainDashboard = function (dashboard) {
                    $scope.currentMainDashboard = null;
                    var dashboardIndex = 0;
                    if ($scope.dashboards) {
                        for (var i = 0; i < $scope.dashboards.length && !$scope.currentMainDashboard; i++) {
                            if ($scope.dashboards[dashboardIndex].dashboardId === dashboard) {
                                $scope.currentMainDashboard = $scope.dashboards[dashboardIndex];
                            }
                        }
                    }
                };

                $scope.logout = function () {
                    auth.logout();
                };

                $scope.sortableOptions = {
                    update: function (e, ui) {

                    },
                    connectWith: ".sortable-contents",
                    tolerance: "pointer",
                    handle: ".widget-header",
                    forcePlaceholderSize: true,
                    placeholder: "sortable-placeholder",
                    cancel: ".widget-dashboard"
                };

                $scope.showPopup = function (popup) {
                    if ($scope.popup) {
                        $scope.popup = null;
                    }

                    popup.show = true;
                    $scope.popup = popup;
                };

                $scope.closePopup = function () {
                    $scope.popupShow = false;
                };

                // Add user control settings
                $scope.userControlSettings = {
                    "dataValueField": "id",
                    "dataTextField": "fallBackDisplayName",
                    /**
                     * Takes received users and creates fallBack display name for each, and prevents duplications.
                     * @param users
                     */
                    "dataTextFn": function (users) {
                        userUtils.setFallBackDisplayNames(users);
                        userUtils.preventFallBackDisplayNameDuplications(users);
                    },
                    "placeholder": "Search User"
                };

                // Add user control resource settings
                $scope.userControlResource = {
                    "entity": "user",
                    "params": {
                        "page": 1,
                        "size": 10,
                        "sort_field": "displayName",
                        "sort_direction": "ASC",
                        "search_field_contains": "{{search}}"
                    }
                };

                /**
                 * Used as state update delegate for the user control. When user control state changes, this method is
                 * invoked, and within, the input is cleaned, and state go is fired.
                 *
                 * @param {{id: string, immediate: boolean, type: string, value: *}}updateObj
                 */
                $scope.goToUserProfile = function (updateObj) {
                    // Clear user search control
                    $('#main_user_search').find('input').val('');
                    // Go to state
                    $state.go('userOverviewPage.userOverview', {userId: updateObj.value});
                };

            }]);
}());
