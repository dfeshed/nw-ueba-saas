(function () {
    'use strict';

    angular.module("FSHighChart", ["Colors", "Utils"]).service('fsHighChartService', function () {

        var basicConfiguration;
        /**
         * Init static highchart settings
         * Define the init and execute it
         */
        var init = function () {

            /**
             * Workaround - when have multi highcharts and you print one of them-
             * all the other are shrink.
             * This workaround taken from: https://github.com/highslide-software/highcharts.com/issues/1093
             */
            Highcharts.setOptions({
                chart: {
                    events: {
                        //When any chart trigger "afterPrint" event, we manually iterate all the charts and operate the
                        // "reflow" method.
                        afterPrint: function () {
                            Highcharts.charts.forEach(function (chart) {
                                if (chart !== undefined) {
                                    chart.reflow();
                                }
                            });
                        }
                    }
                },
                global: {
                    useUTC: true,
                    timezoneOffset: 0
                }
            });

            basicConfiguration = {
                "chart": {},
                "series": [],
                "title": {
                    //Must be empty, if we will remove it, highchart will generate default title
                    "text": ""
                },
                "credits": {
                    //Payment license, no credits needed.
                    "enabled": false
                },
                tooltip: {
                    enabled: false
                },
                // Fortscale as default loading mechanism, no need to use loading of highchart.
                "loading": false,
                "size": {}
            };
        };

        this.getBasicChartConfiguration = function () {
            return angular.copy(basicConfiguration);
        };

        init();

    });
}());
