(function(){
    'use strict';

	var LOW = "low";
	var MEDIUM = "medium";
	var HIGH = "high";
	var CRITICAL = "critical";
	var LOW_SCORE = 50;
	var MEDIUM_SCORE = 80;
	var HIGH_SCORE = 95;
	var CRITICAL_SCORE = 101;

	var LOW_COLOR = "#80BFF0";
	var MEDIUM_COLOR = "#F1CD37";
	var HIGH_COLOR = "#F78D1B";
	var CRITICAL_COLOR = "#D77576";

	//Pay attention- changing the order of the score colors in the array will change the order of the legend
	var SCORE_COLOR_META_DATA = {

		critical: {
			name: CRITICAL,
			color: CRITICAL_COLOR,
			minScore:  HIGH_SCORE,
			maxScore:  CRITICAL_SCORE
		},
		high: {
			name: HIGH,
			color: HIGH_COLOR,
			minScore:  MEDIUM_SCORE,
			maxScore:  HIGH_SCORE
		},
		medium: {
			name: MEDIUM,
			color: MEDIUM_COLOR,
			minScore:  LOW_SCORE,
			maxScore:  MEDIUM_SCORE
		},
		low: {
			name: LOW,
			color: LOW_COLOR,
			minScore:  0,
			maxScore:  LOW_SCORE
		}
	};

	var COLORS_RANGE_BLUE= ['#A4C0FC','#6E9AF5','#5786EB','#2661E0','#0441C4','#032B80'];
	angular.module("FSHighChart").constant('SCORE_COLOR_META_DATA', SCORE_COLOR_META_DATA);
	angular.module("FSHighChart").constant('COLORS_RANGE_BLUE', COLORS_RANGE_BLUE);
})();
