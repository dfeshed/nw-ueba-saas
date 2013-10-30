angular.module("Fortscale", [
    "Popup",
    "Cache",
    "Utils",
    "Format",
    "Conditions",
    "Styles",
    "Transforms",
    "Icons",
    "Yoxigen",
    "ui.sortable",
    "ui.slider",
    "ngResource",
    "FocusWhen",
    "FortscaleAuth",
    "TableWidget",
    "PieChartWidget",
    "BubblesChartWidget",
    "BarsChartWidget",
    "PercentChartWidget",
    "PropertiesWidget",
    "TabsWidget",
    "ForceChartWidget"
], function($routeProvider, $locationProvider){
    $routeProvider
        .when('/d/:dashboardId', {
            templateUrl: 'views/pages/main_dashboard.html',
            controller: "MainDashboardController"
        })
        .when('/d/:dashboardId/:entityId', {
            templateUrl: 'views/pages/main_dashboard.html',
            controller: 'MainDashboardController'
        })
        .when('/d/:dashboardId/e/:entityType/:entityId', {
            templateUrl: 'views/pages/entity.html',
            controller: 'EntityController'
        })
        .when('/d/:dashboardId/e/:entityType/:entityId/f/:featureId', {
            templateUrl: 'views/pages/entity.html',
            controller: 'EntityController'
        })
        .when('/users', {
            templateUrl: 'views/pages/user.html'
        })
        .when('/account', {
            templateUrl: 'views/pages/account.html',
            controller: "AccountSettingsController"
        })
        .when('/global_settings', {
            templateUrl: 'views/pages/global_settings.html',
            controller: "GlobalSettingsController"
        })
        .otherwise(
        {
            redirectTo: "/d/overview"
        });

    $locationProvider.html5Mode(false);
}).run(function($rootScope){
	if (!$rootScope.safeApply) {
		$rootScope.safeApply = function(fn) {
			var phase = this.$root.$$phase;
			if(phase == '$apply' || phase == '$digest') {
				if(fn && (typeof(fn) === 'function')) {
					fn();
				}
			} else {
				this.$apply(fn);
			}
		};
	}

        /*
    $( document ).tooltip({
        position: {
            my: "center bottom-20",
            at: "center top",
            using: function( position, feedback ) {
                $( this ).css( position );
                $( "<div>" )
                    .addClass( "arrow" )
                    .addClass( feedback.vertical )
                    .addClass( feedback.horizontal )
                    .appendTo( this );
            }
        }
    });
    */
});