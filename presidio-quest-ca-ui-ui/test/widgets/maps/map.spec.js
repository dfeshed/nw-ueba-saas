describe('map.directive', function () {
	'use strict';

	var helper = {
		getInternalDirectiveScope : function (element) {
			return angular.element(element.find('.highchart-chart-container')[0]).scope();
		},
		getMockSettings : function () {
			return {};
		},
		getMockData : function () {
			return [
				{"country":"Israel","code":"IL","event_count":2,"type":"VPN"},
				{"country":"United Kingdom","code":"UK","event_count":2,"type":"VPN"},
				{"country":"USA","code":"US","event_count":5,"type":"VPN"},
			];
		},
		getMockCountries : function () {
			return [
				{"path":["M",4039,-7252,"L",3946,-7311], "name":"Israel",
					"properties":{"hc-group":"admin0","hc-middle-x":0.59,"hc-middle-y":0.51,"hc-key":"il","hc-a2":"IL","name":"Algeria","labelrank":"3",
						"country-abbrev":"Alg.","subregion":"Northern Africa","woe-id":"23424740","region-wb":"Middle East & North Africa","iso-a3":"ILS","iso-a2":"IL",
						"region-un":"Africa","continent":"Africa"},"value":0,"flag":"il"
				},
				{"path":["M",4039,-7252,"L",3946,-7311], "name":"USA",
					"properties":{"hc-group":"admin0","hc-middle-x":0.59,"hc-middle-y":0.51,"hc-key":"us","hc-a2":"US","name":"Algeria","labelrank":"3",
						"country-abbrev":"Alg.","subregion":"Northern Africa","woe-id":"23424740","region-wb":"Middle East & North Africa","iso-a3":"USA","iso-a2":"US",
						"region-un":"America","continent":"America"},"value":0,"flag":"us"
				},
				{"path":["M",4039,-7252,"L",3946,-7311], "name":"United Kingdoms",
					"properties":{"hc-group":"admin0","hc-middle-x":0.59,"hc-middle-y":0.51,"hc-key":"uk","hc-a2":"UK","name":"uk","labelrank":"3",
						"country-abbrev":"Alg.","subregion":"Northern Africa","woe-id":"23424740","region-wb":"Middle East & North Africa","iso-a3":"uks","iso-a2":"UK",
						"region-un":"Europe","continent":"Europe"},"value":0,"flag":"uk"
				},
				{"path":["M",4039,-7252,"L",3946,-7311], "name":"Algeria",
					"properties":{"hc-group":"admin0","hc-middle-x":0.59,"hc-middle-y":0.51,"hc-key":"dz","hc-a2":"DZ","name":"Algeria","labelrank":"3",
						"country-abbrev":"Alg.","subregion":"Northern Africa","woe-id":"23424740","region-wb":"Middle East & North Africa","iso-a3":"DZA","iso-a2":"DZ",
						"region-un":"Africa","continent":"Africa"},"value":0,"flag":"dz"
				}
			];
		}
	};

	/**
	 * Setup
	 */

	beforeEach(function () {
	    // Load required Modules
	    module('FSHighChart');
	    module('MapWidget');
	});

	describe('Link function', function () {
		var $compile;
		var $rootScope;
		var $scope;
		var $browser;

		var element;

		// Store references to $rootScope and $compile, _$injector_, _$browser_
		// so they are available to all tests in this describe block
		beforeEach(inject(function (_$compile_, _$rootScope_, _$injector_, _$browser_){
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
			element = $compile('<map data-model="view.data" graph-settings="view.settings"></map>')($scope);
			// Need for highchart to find the chart on the DOM
			$("body").append(element);

			// fire all the watches, so the scope expression {{1 + 1}} will be evaluated
			$browser.defer.flush();
			$scope.$digest();
		}));

		afterEach(function () {
			element.remove();
		});

		it('Should render highchart-containr-div', function () {
			// Check that the compiled element contains the templated content
			expect(element.html()).toMatch('<div class="highchart-chart-container" id="');
		});

		it('Should add the chart object to the scope', function () {
			// Check that the internal directive scope has chart object
			//$controller = element.controller('scatterPlot');
			var internalDirectiveScope = helper.getInternalDirectiveScope(element);
			expect(internalDirectiveScope.chart).toBeDefined();
		});

	});

	describe('Controller', function () {

		describe('_getColorAxis', function () {
			var $compile;
			var $rootScope;
			var $scope;
			var $controller;
			var element;

			// Store references to $rootScope and $compile
			// so they are available to all tests in this describe block
			beforeEach(inject(function (_$compile_, _$rootScope_, _$injector_, _$browser_){
				// The injector unwraps the underscores (_) from around the parameter names when matching
				$compile = _$compile_;
				$rootScope = _$rootScope_;
				var $browser = _$browser_;

				var settings = helper.getMockSettings();
				$scope = $rootScope.$new();

				$scope.view = {
					data: [],
					settings: settings
				};
				// Compile a piece of HTML containing the directive
				element = angular.element('<map data-model="view.data" graph-settings="view.settings"></map>');
				$compile(element)($scope);

				$("body").append(element);
				$browser.defer.flush();
				$scope.$digest();
			}));


			it('Should return 6 categories: <1, 1-20,20-40,40-60,60-80,80-100', function () {

				// Check that the compiled element contains the templated content
				$controller = element.controller('map');
				var maxEventCount = 95;

				var result = $controller._getColorAxis(maxEventCount);
				expect(result.dataClassColor).toBe('category');
				expect(result.dataClasses.length).toBe(6);
				expect(result.dataClasses[0].to).toBe(1);
				expect(result.dataClasses[1].from).toBe(1);
				expect(result.dataClasses[1].to).toBe(20);
				expect(result.dataClasses[2].from).toBe(20);
				expect(result.dataClasses[2].to).toBe(40);
				expect(result.dataClasses[3].from).toBe(40);
				expect(result.dataClasses[3].to).toBe(60);
				expect(result.dataClasses[4].from).toBe(60);
				expect(result.dataClasses[4].to).toBe(80);
				expect(result.dataClasses[5].from).toBe(80);
				expect(result.dataClasses[5].to).toBe(100);

			});

			it('Should return 6 categories: <1, 1-180,180-360,360-540,540-720,720-900', function () {

				// Check that the compiled element contains the templated content
				$controller = element.controller('map');
				var maxEventCount = 840;

				var result = $controller._getColorAxis(maxEventCount);
				expect(result.dataClassColor).toBe('category');
				expect(result.dataClasses.length).toBe(6);
				expect(result.dataClasses[0].to).toBe(1);
				expect(result.dataClasses[1].from).toBe(1);
				expect(result.dataClasses[1].to).toBe(180);
				expect(result.dataClasses[2].from).toBe(180);
				expect(result.dataClasses[2].to).toBe(360);
				expect(result.dataClasses[3].from).toBe(360);
				expect(result.dataClasses[3].to).toBe(540);
				expect(result.dataClasses[4].from).toBe(540);
				expect(result.dataClasses[4].to).toBe(720);
				expect(result.dataClasses[5].from).toBe(720);
				expect(result.dataClasses[5].to).toBe(900);

			});

			it('Should return 5 categories: <1, 1-2,2-3,3-4,4-5', function () {

				// Check that the compiled element contains the templated content
				$controller = element.controller('map');
				var maxEventCount = 4;

				var result = $controller._getColorAxis(maxEventCount);
				expect(result.dataClassColor).toBe('category');
				expect(result.dataClasses.length).toBe(5);
				expect(result.dataClasses[0].to).toBe(1);
				expect(result.dataClasses[1].from).toBe(1);
				expect(result.dataClasses[1].to).toBe(2);
				expect(result.dataClasses[2].from).toBe(2);
				expect(result.dataClasses[2].to).toBe(3);
				expect(result.dataClasses[3].from).toBe(3);
				expect(result.dataClasses[3].to).toBe(4);
				expect(result.dataClasses[4].from).toBe(4);
				expect(result.dataClasses[4].to).toBe(5);

			});

			it('Should return 6 categories: <1, 1-2,2-4,4-6,6-8,8-10', function () {

				$controller = element.controller('map');
				var maxEventCount = 8;

				var result = $controller._getColorAxis(maxEventCount);
				expect(result.dataClassColor).toBe('category');
				expect(result.dataClasses.length).toBe(6);
				expect(result.dataClasses[0].to).toBe(1);
				expect(result.dataClasses[1].from).toBe(1);
				expect(result.dataClasses[1].to).toBe(2);
				expect(result.dataClasses[2].from).toBe(2);
				expect(result.dataClasses[2].to).toBe(4);
				expect(result.dataClasses[3].from).toBe(4);
				expect(result.dataClasses[3].to).toBe(6);
				expect(result.dataClasses[4].from).toBe(6);
				expect(result.dataClasses[4].to).toBe(8);
				expect(result.dataClasses[5].from).toBe(8);
				expect(result.dataClasses[5].to).toBe(10);

			});

			it('Should return 6 categories: <1, 1-5,5-10,10-15,15-20,20-25', function () {

				$controller = element.controller('map');
				var maxEventCount = 22;

				var result = $controller._getColorAxis(maxEventCount);
				expect(result.dataClassColor).toBe('category');
				expect(result.dataClasses.length).toBe(6);
				expect(result.dataClasses[0].to).toBe(1);
				expect(result.dataClasses[1].from).toBe(1);
				expect(result.dataClasses[1].to).toBe(5);
				expect(result.dataClasses[2].from).toBe(5);
				expect(result.dataClasses[2].to).toBe(10);
				expect(result.dataClasses[3].from).toBe(10);
				expect(result.dataClasses[3].to).toBe(15);
				expect(result.dataClasses[4].from).toBe(15);
				expect(result.dataClasses[4].to).toBe(20);
				expect(result.dataClasses[5].from).toBe(20);
				expect(result.dataClasses[5].to).toBe(25);

			});

			it('Should return 6 categories: <1, 1000-5000,5000-10000,10000-15000,15000-20000,20000-25000', function () {

				$controller = element.controller('map');
				var maxEventCount = 22000;

				var result = $controller._getColorAxis(maxEventCount);
				expect(result.dataClassColor).toBe('category');
				expect(result.dataClasses.length).toBe(6);
				expect(result.dataClasses[0].to).toBe(1);
				expect(result.dataClasses[1].from).toBe(1);
				expect(result.dataClasses[1].to).toBe(5000);
				expect(result.dataClasses[2].from).toBe(5000);
				expect(result.dataClasses[2].to).toBe(10000);
				expect(result.dataClasses[3].from).toBe(10000);
				expect(result.dataClasses[3].to).toBe(15000);
				expect(result.dataClasses[4].from).toBe(15000);
				expect(result.dataClasses[4].to).toBe(20000);
				expect(result.dataClasses[5].from).toBe(20000);
				expect(result.dataClasses[5].to).toBe(25000);

			});

			it('Should return 6 categories: <1, 1-100,100-200,200-300,300-400,400 - 500', function () {

				$controller = element.controller('map');
				var maxEventCount = 450;

				var result = $controller._getColorAxis(maxEventCount);
				expect(result.dataClassColor).toBe('category');
				expect(result.dataClasses.length).toBe(6);
				expect(result.dataClasses[0].to).toBe(1);
				expect(result.dataClasses[1].from).toBe(1);
				expect(result.dataClasses[1].to).toBe(100);
				expect(result.dataClasses[2].from).toBe(100);
				expect(result.dataClasses[2].to).toBe(200);
				expect(result.dataClasses[3].from).toBe(200);
				expect(result.dataClasses[3].to).toBe(300);
				expect(result.dataClasses[4].from).toBe(300);
				expect(result.dataClasses[4].to).toBe(400);
				expect(result.dataClasses[5].from).toBe(400);
				expect(result.dataClasses[5].to).toBe(500);

			});

			it('Should throw range error on negative count', function () {

				// Check that the compiled element contains the templated content
				$controller = element.controller('map');

				var val = -10;
				function testFunc () {
					$controller._getColorAxis(val);
				}

				expect(testFunc).toThrowError(RangeError);
				expect(testFunc).toThrowError('Score must be positive or zero');
			});

			afterEach(function () {
				element.remove();
			});

		});

		describe("refreshData", function () {
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
				element = $compile('<map data-model="view.data" graph-settings="view.settings"></map>')($scope);
				$("body").append(element); //Need for highchart to find the chart on the DOM

				// fire all the watches, so the scope expression {{1 + 1}} will be evaluated
				$browser.defer.flush();
				$scope.$digest();

			}));


			it('Should get called when data is changed', function () {

				$controller = element.controller('map');
				spyOn($controller, 'refreshData');
				$scope.view.data = null;
				$scope.$digest();
				expect($controller.refreshData).toHaveBeenCalled();
			});

			it('Should call _indexBy', function () {

				$controller = element.controller('map');
				spyOn($controller, '_indexBy');
				$scope.view.data = null;
				$scope.$digest();

				$controller.countriesList = [];
				$controller.refreshData(helper.getMockData());
				expect($controller._indexBy).toHaveBeenCalled();
			});

			it('Should call _getColorAxis with maxValueCount=5', function () {

				$controller = element.controller('map');

				//Set mock and spy values beofre refreshing data
				var mockData = helper.getMockData();
				var indexedChartData = {
					'IL': mockData[0],
					'UK' :mockData[1],
					'US' :mockData[2]
				};

				spyOn($controller, '_indexBy').and.returnValue(indexedChartData);
				spyOn($controller, '_getColorAxis');
				$controller.countriesList = helper.getMockCountries();

				//Actual make the call
				$controller.refreshData(mockData);
				expect($controller._getColorAxis).toHaveBeenCalledWith(5);
			});

			it('Should call _getSeries', function () {

				$controller = element.controller('map');

				//Set mock and spy values beofre refreshing data
				var mockData = helper.getMockData();

				//Mock indexedChartData
				var indexedChartData = {
					'IL': mockData[0],
					'UK' :mockData[1],
					'US' :mockData[2]
				};

				spyOn($controller, '_indexBy').and.returnValue(indexedChartData);
				spyOn($controller, '_getSeries');

				$controller.countriesList = helper.getMockCountries();

				//The actual call
				$controller.refreshData(mockData);

				//Prepeare the array with countries and values that we expect to arrive to $controller._getSeries
				var expectedCountriesListForUpdate = helper.getMockCountries();
				for (var i= 0; i<expectedCountriesListForUpdate.length;i++){
					switch (expectedCountriesListForUpdate[i].properties["iso-a2"]){
						case 'IL': expectedCountriesListForUpdate[i].value =  2;
							break;
						case 'US': expectedCountriesListForUpdate[i].value =  5;
							break;
						case 'UK': expectedCountriesListForUpdate[i].value =  2;
							break;
						default  : expectedCountriesListForUpdate[i].value =  0;
					}
				}

				expect($controller._getSeries).toHaveBeenCalledWith(expectedCountriesListForUpdate);
			});

			it('Should remove old values on refresh', function () {

				$controller = element.controller('map');

				//Set mock and spy values beofre refreshing data
				var mockData = helper.getMockData();

				//Mock indexedChartData
				var indexedChartData = {
					'IL': mockData[0],
					'UK' :mockData[1],
					'US' :mockData[2]
				};

				spyOn($controller, '_indexBy').and.returnValue(indexedChartData);
				spyOn($controller, '_getSeries');

				$controller.countriesList = helper.getMockCountries();

				//We manually set Algiria value to be 100, but Algiria is not in mockData (data from server)
				//Other countries has value of 0.
				$controller.countriesList[3].value = 100;

				//The actual call.
				//Should fill values in IL, UK, US, but clear Algiria's value and set it back to 0.
				$controller.refreshData(mockData);



				expect($controller.countriesList[3].value).toBe(0);
			});

			afterEach(function () {
				element.remove();
			});

		});
	});
});

