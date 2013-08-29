angular.module("Fortscale").controller("MainController", ["$scope", "$routeParams", "dashboards", "auth", function($scope, $routeParams, dashboards, auth){
    $scope.navigation = [
        {
            name: "Overview",
            url: "#/d/main",
            dashboardId: "main",
            icon: "home"
        },
        {
            name: "Login Analysis",
            url: "#/d/auth-investigate",
            dashboardId: "auth-investigate",
            icon: "signin"
        },
        {
            name: "Malware Discovery",
            url: "#/d/malware",
            dashboardId: "malware",
            icon: "sitemap"
        },
        {
            name: "Reports",
            icon: "list-alt",
            children: [
                { name: "Hostname to IPs", url: "#/d/hostname-to-ips", icon: "table" },
                { name: "IP to Hostnames", url: "#/d/ip-to-hostnames", icon: "table" }
            ]
        }
    ];

    dashboards.getDashboardsList().then(function(dashboardsList){
        if (dashboardsList.length){
            $scope.dashboards = dashboardsList;
        }
    });

    $scope.report = {
        error: function(error){
            alert("ERROR: " + (typeof(error) === "string") ? error : error.message);
            console.error(error);
        }
    };

    $scope.$on("$routeChangeSuccess", function(){
        if ($routeParams.dashboardId){
            if (!$scope.currentMainDashboard || $routeParams.dashboardId !== $scope.currentMainDashboard.dashboardId)
                $scope.setCurrentMainDashboard($routeParams.dashboardId);
        }
        else{
            window.location.hash = "#/d/" + $scope.navigation[0].dashboardId;
        }

        $scope.currentNav = document.location.hash.split("?")[0];
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
        window.location.href = "/fortscale-ui-atlantis/app/signin.html";
    };

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