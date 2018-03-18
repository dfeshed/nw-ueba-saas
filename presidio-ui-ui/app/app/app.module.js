(function () {
	'use strict';

	angular.module("Fortscale", [
		"Version",
		'ngRoute',
        'ngMessages',
		'ui.router',
        'ngAnimate',
		"Config",
		"DAL",
		"Page",
		"Reports",
		"Widgets",
		"Controls",
		"Menus",
		"Search",
		"EventBus",
		"Events",
		"Popup",
		"Cache",
		"Chart",
		"Utils",
		"FSHighChart",
		"Format",
		"Conditions",
		"DataEntities",
		"State",
		"Styles",
		"Transforms",
		"Icons",
		"Tags",
		"Yoxigen",
		"DatePicker",
		"Dropdown",
		"ui.layout",
		"ngResource",
		"FocusWhen",
		"Tooltip",
		"Colors",
		"FortscaleAuth",
		"NumberRangeModule",
		"StringInModule",
        "multi-select",
		"rt.debounce",
		"paging",
		"simplePagination",
		"NumbersOnlyModule",
		"DurationOnlyModule",
        "PopupConditions",
        'restangular',

		// Widgets:
		"TableWidget",
		"SecurityFeed",
		"BubblesWidget",
		"HeatMapWidget",
		"TagsWidget",
		"BarsChartWidget",
		"PercentChartWidget",
		"PropertiesWidget",
		"TabsWidget",
		"ForceChartWidget",
		"ScatterPlotWidget",
        "pascalprecht.translate",

		// New design
        'fsTemplates',
		'Fortscale.shared',
        'Fortscale.layouts',
        'Fortscale.appConfig',
        'Fortscale.remoteAppConfig'

	])
        .config(['$rootScopeProvider', function ($rootScopeProvider) {
            // This was done to support Explore. It turns out that Explore needs more then 10 digest cycles
            // to work when switching data sources. I hope Explore will die soon, but most likely I'll die first...
            $rootScopeProvider.digestTtl(20);
        }])
		.run(['$rootScope', '$timeout', function ($rootScope, $timeout) {
			if (!$rootScope.safeApply) {
				// Prevent executing $apply if already in a digest cycle
				$rootScope.safeApply = function (fn) {
					var phase = $rootScope.$$phase;

					if (phase === '$apply' || phase === '$digest') {
						if (typeof fn === 'function') {
							fn();
						}
					} else {
						this.$apply(fn);
					}
				};
			}

			$rootScope.hideLoader = true;
		}]);
}());
