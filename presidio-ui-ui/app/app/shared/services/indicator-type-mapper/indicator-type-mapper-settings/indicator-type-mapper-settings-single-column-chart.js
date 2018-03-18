(function () {
    'use strict';
    angular.module('Fortscale.shared.services.indicatorTypeMapper')
    /**
     * Filter which generate the relevant message key from the anomalyTypeFieldName and title or axisYtitle
     */
        .filter('buildSingleColumnKey', function () {
            var prefix= "evidence.singlecolumn.title.";
            return function (anomalyTypeFieldName, postfix) {
                return prefix+anomalyTypeFieldName;
            };
        })
        .factory('indicatorTypeMapper.single-column',['$filter',
            'indicatorTypeMapper.commonQueryParams',
            function ($filter, commonQueryParams) {
                return {
                    settings:  {
                        singleColumn: {
                            params: commonQueryParams.entityTypeAnomalyTypeCount5Columns,
                            styleSettings: {
                                height: '24rem',
                                boxSizing: 'border-box',
                                width: '100%',
                                padding: '0 1.25rem'
                            },
                            chartSettings: {

                                title: {
                                    text: '<span class="chart-title">' +
                                    '{{anomalyTypeFieldName  | buildSingleColumnKey | translate: this}}' +
                                    '</span>'
                                },
                                "series": [{}]
                            }
                        }

                    }
                };
            }]
    );
}());
