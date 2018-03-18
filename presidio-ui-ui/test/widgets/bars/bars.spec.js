describe('bars.directive', function () {
	'use strict';

	var helper = {
		getInternalDirectiveScope : function (element) {
			return angular.element(element.find('.highchart-bar-chart-container')[0]).scope();
		},
		getMockSettings : function () {
			return {
					"chart":{
						"renderTo":"toyuhehtv68estt9",
						"type":"bar"
					},
					"legend":{
						"enabled":true,
						"layout":"vertical",
						"align":"right",
						"verticalAlign":"top",
						"x":10,
						"y":40,
						"borderWidth":0
					},
					"plotOptions":{
						"bar":{
							"shadow":true,
							"minPointLength":5,
							"dataLabels":{
								"enabled":true,
								"style":{
										"fontWeight":"bold"
								},
								"x":0,
								"y":0,
								"align":"right"}
						}
					},
					"scrollbar": {
						"enabled":true
					},
					"yAxis":{
						"min":0,
						"maxPadding":0,
						"title":{
							"text":"",
							"align":"high"
						},"labels":{
							"overflow":"justify"
						},
						"minTickInterval":1,
						"tickInterval":1,
						"endOnTick":true,
						"minRange":1
					},"xAxis":{
						"categories":[],
						"title":{
							"text":null
						}
					},"series":[
						{"name":"Critical","data":[],"color":"#D77576","visible":true},
						{"name":"High","data":[],"color":"#F78D1B","visible":true},
						{"name":"Medium","data":[],"color":"#F1CD37","visible":false},
						{"name":"Low","data":[],"color":"#80BFF0","visible":false}
					],
					"title":{
						"text":""
					},
					"credits":{
						"enabled":false
					},
					"tooltip":{
						"enabled":false
					},
					"loading":false,
					"size":{}

			};
		},
		getMockData : function() {
			var chartData =
				[
					{"label":"a@somebigcompany.com","severity":"Critical","event_count":3,"type":"VPN","_percent":12},
					{"label":"b@somebigcompany.com","severity":"Critical","event_count":4,"type":"VPN","_percent":16},
					{"label":"c@somebigcompany.com","severity":"Critical","event_count":4,"type":"VPN","_percent":16},
					{"label":"a@somebigcompany.com","severity":"High","event_count":3,"type":"VPN","_percent":12},
					{"label":"b@somebigcompany.com","severity":"High","event_count":3,"type":"VPN","_percent":12},
					{"label":"c@somebigcompany.com","severity":"High","event_count":3,"type":"VPN","_percent":12},
					{"label":"a@somebigcompany.com","severity":"Medium","event_count":5,"type":"VPN","_percent":20},
				];
			return chartData;
		}
	};

	/**
	 * Spec
	 */

	// Load FortscaleHighChart module
	beforeEach(module('FSHighChart'));
	// Load the directive's module
	beforeEach(module('BarsWidget'));

	describe('Link function', function() {
		var $compile;
		var $rootScope;
		var $scope;
		var $controller;
		var $browser;

		var utils;
		var element;

		// Store references to $rootScope and $compile, _$injector_, _$browser_
		// so they are available to all tests in this describe block
		beforeEach(inject(function(_$compile_, _$rootScope_, _$injector_, _$browser_){
			// The injector unwraps the underscores (_) from around the parameter names when matching
			$compile = _$compile_;
			$rootScope = _$rootScope_;
			$browser = _$browser_;

			var settings = helper.getMockSettings();
			$scope = $rootScope.$new();
			$scope.view = {
				data: [],
				settings: settings
			};
			// Compile a piece of HTML containing the directive
			element = $compile('<bars data-model="view.data" graph-settings="view.settings"></bars>')($scope);
			//Need for highchart to find the chart on the DOM
			$("body").append(element);

			// fire all the watches, so the scope expression {{1 + 1}} will be evaluated
			$scope.$digest();
			$browser.defer.flush();
		}));

		it('Should render highchart-containr-div', function() {
			// Check that the compiled element contains the templated content
			expect(element.html()).toContain('<div class="highchart-bar-chart-container-parent"><div class="highchart-bar-chart-container" id="');
		});

		it('Should add the chart object to the scope', function() {
			// Check that the internal directive scope has chart object
			//$controller = element.controller('scatterPlot');
			var internalDirectiveScope = helper.getInternalDirectiveScope(element);
			expect(internalDirectiveScope.chart).toBeDefined();
		});

		afterEach(inject(function(_$compile_, _$rootScope_, _$injector_, _$browser_){
			element.remove();
		}));
	});

	describe('Controller', function () {

		describe('refreshData', function () {
			var $compile;
			var $rootScope;
			var $scope;
			var $controller;
			var $browser;

			var utils;
			var element;

			// Store references to $rootScope and $compile
			// so they are available to all tests in this describe block
			beforeEach(inject(function (_$compile_, _$rootScope_, _$injector_, _$browser_) {
				// The injector unwraps the underscores (_) from around the parameter names when matching
				$compile = _$compile_;
				$rootScope = _$rootScope_;
				utils = _$injector_.get('utils');
				$browser = _$browser_;

				var settings = helper.getMockSettings();
				$scope = $rootScope.$new();
				$scope.view = {
					data: [],
					settings: settings
				};
				// Compile a piece of HTML containing the directive

				element = $compile('<bars data-model="view.data" graph-settings="view.settings"></bars>')($scope);
				$("body").append(element); //Need for highchart to find the chart on the DOM

				// fire all the watches, so the scope expression {{1 + 1}} will be evaluated
				$scope.$digest();
				$browser.defer.flush();
			}));


			it('Should get called when data is changed', function () {

				$controller = element.controller('bars');
				spyOn($controller, 'refreshData');
				$scope.view.data = null;
				$scope.$digest();
				expect($controller.refreshData).toHaveBeenCalled();
			});

			it('Should call _getPointByUserAndSeverity', function () {

				$controller = element.controller('bars');
				spyOn($controller, '_getPointByUserAndSeverity');
				$scope.view.data = null;
				$scope.$digest();


				$controller.refreshData(helper.getMockData());
				expect($controller._getPointByUserAndSeverity).toHaveBeenCalled();
			});

			it('Should call _prepareDataForChart', function () {

				var prepareDataForChartReturn = {
					categories : ['a','b','c'],
					series : {
						critical : {},
						high : {},
						medium : {},
						low : {}

					}
				};

				$controller = element.controller('bars');
				spyOn($controller, '_prepareDataForChart').and.returnValue(prepareDataForChartReturn);
				$scope.view.data = null;
				$scope.$digest();

				$controller.refreshData(helper.getMockData());
				expect($controller._prepareDataForChart).toHaveBeenCalled();
			});

			afterEach(function () {
				element.remove();
			});

		});

		describe("_getPointByUserAndSevirity", function () {
			var $compile;
			var $rootScope;
			var $scope;
			var $controller;
			var utils;
			var element;
			var $browser;

			// Store references to $rootScope and $compile
			// so they are available to all tests in this describe block
			beforeEach(inject(function (_$compile_, _$rootScope_, _$injector_, _$browser_) {
				// The injector unwraps the underscores (_) from around the parameter names when matching
				$compile = _$compile_;
				$rootScope = _$rootScope_;
				utils = _$injector_.get('utils');
				$browser = _$browser_;

				var settings = helper.getMockSettings();
				$scope = $rootScope.$new();
				$scope.view = {
					data: [],
					settings: settings
				};
				// Compile a piece of HTML containing the directive

				element = $compile('<bars data-model="view.data" graph-settings="view.settings"></bars>')($scope);
				$("body").append(element); //Need for highchart to find the chart on the DOM

				// fire all the watches, so the scope expression {{1 + 1}} will be evaluated
				$scope.$digest();
				$browser.defer.flush();
			}));


			it('Should return map with entry for each user a@somebigcompany.com,b@somebigcompany.com,c@somebigcompany.com ', function () {

				$controller = element.controller('bars');
				//spyOn($controller, 'refreshData');
				$scope.view.data = null;
				$scope.$digest();

				var ans = $controller._getPointByUserAndSeverity(helper.getMockData());

				expect(ans["a@somebigcompany.com"]).toBeDefined();
				expect(ans["b@somebigcompany.com"]).toBeDefined();
				expect(ans["c@somebigcompany.com"]).toBeDefined();
			});

			it('Should return object for sevirities for user', function () {

				$controller = element.controller('bars');
				//spyOn($controller, 'refreshData');
				$scope.view.data = null;
				$scope.$digest();

				var ans = $controller._getPointByUserAndSeverity(helper.getMockData());

				var singleUserData = ans["a@somebigcompany.com"];

				expect(singleUserData.critical).toBeDefined();
				expect(singleUserData.high).toBeDefined();
				expect(singleUserData.medium).toBeDefined();
				expect(singleUserData.low).toBeUndefined();

				expect(singleUserData.critical.y).toBe(3);
				expect(singleUserData.high.y).toBe(3);
				expect(singleUserData.medium.y).toBe(5);

				expect(singleUserData.critical.percentage).toBe("12.00");
				expect(singleUserData.high.percentage).toBe("12.00");
				expect(singleUserData.medium.percentage).toBe("20.00");

			});

			afterEach(function () {
				element.remove();
			});

		});
	});
});
