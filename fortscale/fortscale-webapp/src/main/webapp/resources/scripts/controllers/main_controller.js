angular.module("Fortscale").controller("MainController", ["$scope", "$routeParams", "dashboards", "auth", "users", function($scope, $routeParams, dashboards, auth, users){
    $scope.navigation = [
        {
            name: "Overview",
            url: "#/d/main",
            dashboardId: "main",
            icon: "home"
        },
        {
            name: "Login Analysis",
            url: "#/d/investigator?entities=wmievents4769",
            dashboardId: "auth-investigate",
            icon: "signin"
        },
        {
            name: "User Comparison",
            url: "#/d/user_comparison",
            dashboardId: "user_comparison",
            icon: "group"
        }
    ];

    $scope.buildVersion = "Unknown";

    dashboards.getDashboardsList().then(function(dashboardsList){
        if (dashboardsList.length){
            $scope.dashboards = dashboardsList;
        }
    });

    auth.getCurrentUser().then(function(userData){
        if (userData)
            $scope.loggedInUser = userData;
        else
            window.location.href = "/fortscale-webapp/signin.html";
    }, function(){
        if (!localStorage.debug)
            window.location.href = "/fortscale-webapp/signin.html";
    });

    $scope.report = {
        error: function(error){
            alert("ERROR: " + (typeof(error) === "string") ? error : error.message);
            console.error(error);
        }
    };

    $scope.toggleSidebar = function(){
        $scope.sidebarMinimized = !$scope.sidebarMinimized;
    };

    $scope.$on("$routeChangeSuccess", function(){
        if ($routeParams.dashboardId){
            if (!$scope.currentMainDashboard || $routeParams.dashboardId !== $scope.currentMainDashboard.dashboardId)
                $scope.setCurrentMainDashboard($routeParams.dashboardId);
        }

        $scope.currentNav = document.location.hash.split("?")[0];
    });

    $scope.getCurrentNavRedirect = function(){
        return encodeURIComponent($scope.currentNav);
    };

    $scope.$on("authError", function(e, data){
        $scope.modal = {
            show: true,
            src: data.status === 403 ? "views/modals/password_expired.html" : "views/modals/session_expired.html"
        };
    });

    $scope.setCurrentMainDashboard = function(dashboard){
        $scope.currentMainDashboard = null;
        var dashboardIndex = 0;
        if ($scope.dashboards){
            for(var i= 0; i < $scope.dashboards.length && !$scope.currentMainDashboard; i++){
                if ($scope.dashboards[dashboardIndex].dashboardId === dashboard)
                    $scope.currentMainDashboard = $scope.dashboards[dashboardIndex];
            }
        }
    };

    $scope.logout = function(){
        auth.logout();
    };

    $scope.searchSettings = angular.extend(users.getSearchSettings(), {
        "value": "#/d/user/{{id}}",
        "onSelect": {
            "url": "#/d/user/{{id}}"
        },
        "placeholder": "Users search"
    });

    $scope.sortableOptions = {
        update: function(e, ui) {

        },
        connectWith: ".sortable-contents",
        tolerance: "pointer",
        handle: ".widget-header",
        forcePlaceholderSize: true,
        placeholder: "sortable-placeholder",
        cancel: ".widget-dashboard"
    };

    $scope.popup = {src: ""};

    $scope.showPopup = function(popup){
        $scope.popup = popup;
        $scope.popup.show = true;
        $scope.$apply();
    };

    $scope.closePopup = function(){
        $scope.popup.show = false;
    }
}]);