describe('scatterplot.directive', function () {
	'use strict';

	var helper = {
		getInternalDirectiveScope : function (element) {
			return angular.element(element.find('.highchart-chart-container')[0]).scope();
		},
		getMockSettings : function () {
			return {
				"timeField": "end_time",
				"yField": "duration_in_hours",
				"colorField": "session_score",
				"colorScale": "score",
				"legend": {
					"position": "right"
				},
				"shapes": {
					"field": "type"
				},
				"axes": {
					"x": {
						"type": "time",
						"timeFormat": "days",
						"label": "Session time"
					},
					"y": {
						"label": "Duration (hours)"
					}
				},
				"scales": {
					"x": {
						"type": "time"
					},
					"y": {
						"minValue": 0,
						"ticks": {
							"interval": 0.2
						}
					}
				},
				"onSelect": {
					"action": "showTooltip",
					"actionOptions": {
						"table": {
							"rows": [
								{
									"label": "Username",
									"value": "{{username}}"
								},
								{
									"label": "Start time",
									"value": "{{start_time:date:MM/DD/YYYY HH\\:mm\\:ss}}"
								},
								{
									"label": "End time",
									"value": "{{end_time:date:MM/DD/YYYY HH\\:mm\\:ss}}"
								},
								{
									"label": "Duration (hh:mm:ss)",
									"value": "{{duration:diffToPrettyTime:seconds}}"
								}
							]
						}
					}
				}
			};
		}
	};

	/**
	 * Spec
	 */

	// Load FortscaleHighChart module
	beforeEach(module('FSHighChart'));
	// Load the directive's module
	beforeEach(module('ScatterPlotWidget'));

	describe('Link function', function () {
		var $compile;
		var $rootScope;
		var $scope;
		var $controller;
		var $browser;

		var utils;
		var element;

		// Store references to $rootScope and $compile, _$injector_, _$browser_
		// so they are available to all tests in this describe block
		beforeEach(inject(function (_$compile_, _$rootScope_, _$injector_, _$browser_) {
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
			element = $compile('<scatter-plot data-model="view.data" graph-settings="view.settings" ></scatter-plot>')($scope);
			//Need for highchart to find the chart on the DOM
			$("body").append(element);

			// fire all the watches, so the scope expression {{1 + 1}} will be evaluated
			$scope.$digest();
			$browser.defer.flush();
		}));

		it('Should render highchart-containr-div', function () {
			// Check that the compiled element contains the templated content
			expect(element.html()).toContain('<div class="highchart-chart-container" id="');
		});

		it('Should add the chart object to the scope', function () {
			// Check that the internal directive scope has chart object
			//$controller = element.controller('scatterPlot');
			var internalDirectiveScope = helper.getInternalDirectiveScope(element);
			expect(internalDirectiveScope.chart).toBeDefined();
		});

		afterEach(function () {
			element.remove();
		});
	});

	describe('Controller', function () {

		describe('getRiskKeyByScore', function () {
			var $compile;
			var $rootScope;
			var $scope;
			var $controller;
			var utils;
			var element;

			// Load the ScatterPlotWidget module, which contains the directive
			beforeEach(module('ScatterPlotWidget'));

			// Store references to $rootScope and $compile
			// so they are available to all tests in this describe block
			beforeEach(inject(function (_$compile_, _$rootScope_, _$injector_) {
				// The injector unwraps the underscores (_) from around the parameter names when matching
				$compile = _$compile_;
				$rootScope = _$rootScope_;

				var settings = helper.getMockSettings();
				$scope = $rootScope.$new();

				$scope.view = {
					data: [],
					settings: settings
				};
				// Compile a piece of HTML containing the directive
				element = angular.element('<scatter-plot data-model="view.data" graph-settings="view.settings" ></scatter-plot>');
				var template  = $compile(element)($scope);

				// fire all the watches, so the scope expression {{1 + 1}} will be evaluated
				$scope.$digest();

			}));

			it('Should return low for 0-50, medium for score 50-90, high for 90-95, and critical for 95-100', function () {

				// Check that the compiled element contains the templated content
				$controller = element.controller('scatterPlot');
				var riskKey = $controller.getRiskKeyByScore(10);
				expect(riskKey).toBe('low');

				var riskKey = $controller.getRiskKeyByScore(51);
				expect(riskKey).toBe('medium');

				var riskKey = $controller.getRiskKeyByScore(93);
				expect(riskKey).toBe('high');

				var riskKey = $controller.getRiskKeyByScore(98);
				expect(riskKey).toBe('critical');
			});

			it('Should throw range error on negative score', function () {

				// Check that the compiled element contains the templated content
				$controller = element.controller('scatterPlot');

				var val = -10;
				function testFunc () {
					$controller.getRiskKeyByScore(val);
				}

				expect(testFunc).toThrowError(RangeError);
				expect(testFunc).toThrowError('Score must be 0-100');
			});

			it('Should throw range error on score above 100', function () {
				// Check that the compiled element contains the templated content
				$controller = element.controller('scatterPlot');

				var val = 101;
				function testFunc () {
					$controller.getRiskKeyByScore(val);
				}

				expect(testFunc).toThrowError(RangeError);
				expect(testFunc).toThrowError('Score must be 0-100');
			});

		});

		describe('addOrUpdateSeries', function () {
			var $compile;
			var $rootScope;
			var $scope;
			var $controller;
			var utils;
			var element;
			var $browser;

			// Load the myApp module, which contains the directive
			beforeEach(module('ScatterPlotWidget'));

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
				element = $compile('<scatter-plot data-model="view.data" graph-settings="view.settings" ></scatter-plot>')($scope);
				$("body").append(element); //Need for highchart to find the chart on the DOM

				// fire all the watches, so the scope expression {{1 + 1}} will be evaluated
				$scope.$digest();

				$browser.defer.flush();
			}));

			it('Should call update() on scope.chart.series when series with the specific index already exists', function () {
				$controller = element.controller('scatterPlot');
				var series = { update: function (data,color, name, markerSymbol, seriesIndex) {}};
				spyOn(series, 'update');

				//Generate data for series
				var data = [];
				var name = "testName";
				var markerSymbol = "testMarkerSymbol";

				var color = "#555555";

				var internalDirectiveScope = helper.getInternalDirectiveScope(element);
				//init pre-situation when 2 series exists, and we try to update series with index 1
				internalDirectiveScope.chart = { series : [series, series]};
				var seriesIndex = 1;

				$controller.addOrUpdateSeries(data,color, name, markerSymbol, seriesIndex)

				//Create the expected object
				var highchartSeriesObject={
					"data": data,
					"color": color,
					"name": name,
					"marker": {
						"symbol": markerSymbol
					}

				};

				expect(series.update).toHaveBeenCalledWith(highchartSeriesObject);
			});

			it('Should call add() on scope.chart when there is no other series with the specific index', function () {


				$controller = element.controller('scatterPlot');


				var data = [];
				var name = "testName";
				var markerSymbol = "testMarkerSymbol";
				var color = "#555555";

				$controller = element.controller('scatterPlot');
				var internalDirectiveScope = helper.getInternalDirectiveScope(element);

				//init pre-situation when 2 series exists, and we try to add series with index 3
				var series = { update: function (data,color, name, markerSymbol, seriesIndex) {}};
				internalDirectiveScope.chart.series = [series, series];
				var seriesIndex = 3;

				spyOn(internalDirectiveScope.chart, 'addSeries');
				$controller.addOrUpdateSeries(data,color, name, markerSymbol, seriesIndex);

				var highchartSeriesObject={
					"data": data,
					"color": color,
					"name": name,
					"marker": {
						"symbol": markerSymbol
					}

				};

				expect(internalDirectiveScope.chart.addSeries).toHaveBeenCalledWith(highchartSeriesObject);
			});


			afterEach(function () {
				element.remove();
			});
		});

		describe('xAxisIgnoreHours', function () {
			var $compile;
			var $rootScope;
			var $scope;
			var $controller;
			var utils;
			var element;
			var $browser;

			// Load the myApp module, which contains the directive
			beforeEach(module('ScatterPlotWidget'));

			// Store references to $rootScope and $compile
			// so they are available to all tests in this describe block
			beforeEach(inject(function (_$compile_, _$rootScope_, _$injector_, _$browser_) {
				// The injector unwraps the underscores (_) from around the parameter names when matching
				$compile = _$compile_;
				$rootScope = _$rootScope_;
				$browser = _$browser_;

			}));

			it('Should return true when yField is not defined ', function () {

				var settings = helper.getMockSettings();
				settings.yField = null;
				$scope = $rootScope.$new();
				$scope.view = {
					data: [],
					settings: settings
				};
				// Compile a piece of HTML containing the directive
				element = $compile('<scatter-plot data-model="view.data" graph-settings="view.settings" ></scatter-plot>')($scope);

				$controller = element.controller('scatterPlot');
				// fire all the watches, so the scope expression {{1 + 1}} will be evaluated
				$scope.$digest();

				$controller = element.controller('scatterPlot');
				var internalDirectiveScope = helper.getInternalDirectiveScope(element);
				var ignoreHours = $controller.xAxisIgnoreHours();
				expect(ignoreHours).toBe(true);
			});

			it('Should return false when yField on settings object has value', function () {

				var settings = helper.getMockSettings();
				settings.yField = "hour_field";
				$scope = $rootScope.$new();
				$scope.view = {
					data: [],
					settings: settings
				};
				// Compile a piece of HTML containing the directive
				element = $compile('<scatter-plot data-model="view.data" graph-settings="view.settings" ></scatter-plot>')($scope);

				$controller = element.controller('scatterPlot');
				// fire all the watches, so the scope expression {{1 + 1}} will be evaluated
				$scope.$digest();

				$controller = element.controller('scatterPlot');
				var internalDirectiveScope = helper.getInternalDirectiveScope(element);
				var ignoreHours = $controller.xAxisIgnoreHours();
				expect(ignoreHours).toBe(false);

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

			// Load the myApp module, which contains the directive
			beforeEach(module('ScatterPlotWidget'));

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
				element = $compile('<scatter-plot data-model="view.data" graph-settings="view.settings" ></scatter-plot>')($scope);
				$("body").append(element); //Need for highchart to find the chart on the DOM

				// fire all the watches, so the scope expression {{1 + 1}} will be evaluated
				$scope.$digest();
				$browser.defer.flush();
			}));


			it('Should get called when data is changed', function () {

				$controller = element.controller('scatterPlot');
				spyOn($controller, 'refreshData');
				$scope.view.data = null;
				$scope.$digest();
				expect($controller.refreshData).toHaveBeenCalled();
			});


			afterEach(function () {
				element.remove();
			});

		});
	});
});
