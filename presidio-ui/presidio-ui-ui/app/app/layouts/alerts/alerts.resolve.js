/**
 * Resolve object for alerts
 */
(function () {
    'use strict';


    var alertsPageResolve = {
        alertsResourceSettings: [
            'jsonLoader',
            function (jsonLoader) {
                return jsonLoader
                    .load('app/layouts/alerts/settings/alerts.resource.json');
            }
        ],
        alertsTableSettings: [
            'jsonLoader',
            function (jsonLoader) {
                return jsonLoader
                    .load('app/layouts/alerts/settings/alerts-table.settings.json');
            }
        ],
        alertsIndicatorsTableSettings: [
            'jsonLoader',
            function (jsonLoader) {
                return jsonLoader
                    .load('app/layouts/alerts/settings/alerts-indicators-table.settings.json');
            }
        ],
        alertsMainState: [
            'jsonLoader',
            function (jsonLoader) {
                return jsonLoader
                    .load('app/layouts/alerts/settings/alerts-main.state.json');
            }
        ],
        splitterSettings: [
            'jsonLoader',
            function (jsonLoader) {
                return jsonLoader
                    .load('app/layouts/alerts/settings/splitter.settings.json');
            }
        ],
        filtersPaneSettings: [
            'jsonLoader',
            function (jsonLoader) {
                return jsonLoader
                    .load('app/layouts/alerts/settings/filters-pane.settings.json');
            }
        ],
        tagsList: [
            'BASE_URL',
            '$http',
            function (BASE_URL, $http) {
                return $http
                    .get(BASE_URL + '/tags/user_tags')
                    .then(function (res) {
                    res.data.data = _.map(res.data.data, function (tag) {
                        return {
                            id: tag.name,
                            value: tag.displayName
                        };
                    });
                    return res.data;
                });
            }
        ],
        indicatorTypeList: [
            'fsIndicatorTypes',
            function (fsIndicatorTypes) {
                return fsIndicatorTypes.getIndicatorsList('/alerts/exist-anomaly-types');
            }
        ]
    };


    angular.module('Fortscale.layouts')
        .constant('alertsPageResolve', alertsPageResolve);
}());
