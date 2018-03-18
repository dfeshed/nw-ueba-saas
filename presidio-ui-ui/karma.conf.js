// Karma configuration
// Generated on Sun Sep 20 2015 18:13:37 GMT+0300 (IDT)

module.exports = function(config) {
    config.set({

        // base path that will be used to resolve all patterns (eg. files, exclude)
        basePath: '',


        // frameworks to use
        // available frameworks: https://npmjs.org/browse/keyword/karma-adapter
        frameworks: ['jasmine'],


        plugins: [
            //'karma-coverage',
            'karma-jasmine',
            'karma-chrome-launcher'
            //'karma-firefox-launcher'
        ],

        // list of files / patterns to load in the browser
        files: [

        /**
         * Libraries
         */

            'app/libs/jquery/jquery-2.2.1.js',
            'app/libs/bootstrap/js/bootstrap.js',
            'app/libs/jquery-ui/jquery-ui-1.11.0.custom.min.js',
            'app/libs/d3/d3.v3.min.js',
            'app/libs/d3/topojson.v1.min.js',
            'app/libs/d3/resources/colorbrewer.js',
            'app/libs/d3/resources/geometry.js',
            'app/libs/detail-wrap/scripts/raphael-min.js',
            'app/libs/lodash/lodash.js',

            // Angular
            'app/libs/angular/angular.js',
            'app/libs/angular-resource/angular-resource.js',
            'app/libs/angular-route/angular-route.js',
            'app/libs/angular-mocks/angular-mocks.js',
            'app/libs/angular-animate/angular-animate.js',
            'app/libs/angular-ui-router/angular-ui-router.js',
            'app/libs/angular-debounce/angular-debounce.min.js',
            'app/libs/restangular/restangular.js',
            'app/libs/angular-bootstrap/ui-bootstrap-tpls-0.13.3.min.js',

            'app/libs/moment/moment.js',
            'app/libs/jquery-ui-date-range-picker/js/daterangepicker.jQuery.js',
            'app/libs/angular-multi-select/angular-multi-select.js',
            'app/libs/paging/paging.js',
            'app/libs/ui-layout/ui-layout.js',
            'app/libs/yoxigen/pagination/pagination.js',

            // Highcharts
            'app/libs/highchart/highcharts.js',
            'app/libs/highchart/map.js',
            'app/libs/highchart/exporting.js',
            'app/libs/highchart/data.js',
            'app/libs/highchart/world.js',

            //angular-translate
            'app/libs/angular-translate/angular-translate.min.js',

            // Kendo UI
            'app/libs/kendo-ui/js/kendo.web.min.js',

            /**
             * Templates
             */
            'dist/assets/js/fs-templates.min.js',

            /**
             * App 2.0
             */

            'dist/assets/js/index-main.*.js',

            /**
             * Spec files
             */

            'test/**/*.spec.js',
            'app/**/*.spec.js'

        ],


        // list of files to exclude
        exclude: [
        ],


        // preprocess matching files before serving them to the browser
        // available preprocessors: https://npmjs.org/browse/keyword/karma-preprocessor
        preprocessors: {
        },


        // test results reporter to use
        // possible values: 'dots', 'progress'
        // available reporters: https://npmjs.org/browse/keyword/karma-reporter
        reporters: ['progress'],


        // web server port
        port: 9876,


        // enable / disable colors in the output (reporters and logs)
        colors: true,


        // level of logging
        // possible values: config.LOG_DISABLE || config.LOG_ERROR || config.LOG_WARN || config.LOG_INFO || config.LOG_DEBUG
        logLevel: config.LOG_INFO,


        // enable / disable watching file and executing tests whenever any file changes
        autoWatch: false,


        // start these browsers
        // available browser launchers: https://npmjs.org/browse/keyword/karma-launcher
        browsers: ['Chrome'],


        // Continuous Integration mode
        // if true, Karma captures browsers, runs the tests and exits
        singleRun: false
    })
};


