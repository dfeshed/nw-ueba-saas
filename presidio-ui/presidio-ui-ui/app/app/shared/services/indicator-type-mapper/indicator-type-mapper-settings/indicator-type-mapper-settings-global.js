/**
 * This file will contain mapping settings which required  by few indicators mapping
 */

(function () {
    'use strict';
    angular.module('Fortscale.shared.services.indicatorTypeMapper')
        .value('indicatorTypeMapper.commonQueryParams',{
        /**
         * Used as a common param configuration
         */
            entityTypeAnomalyTypeCount : {
                feature: '{{anomalyTypeFieldName}}',
                'function': 'Count',
                num_columns: 4
            },
            /**
             * Used as a common param configuration for time aggregation indicators
             */
                aggregationIndicatorsByTime : {
                feature: '{{anomalyTypeFieldName}}',
                'function': 'distinctEventsByTime'
            },
            /**
             * Used as a common param configuration
             */
            anomalyTypeEntityTypeCount : {
                feature: '{{entityTypeFieldName}}',
                'function': 'Count',
                num_columns: 4
            },

            /**
             * param configuration for vpn-session data rate
             */
            entityTypeAnomalyTypeCount30days : {
                feature: '{{anomalyTypeFieldName}}',
                'function': 'VPNSession',
                num_columns: 5,
                time_range: 30
            },

            /**
             * Used as a common param configuration
             */
            entityTypeAnomalyTypeCount5Columns : {
                feature: '{{anomalyTypeFieldName}}',
                'function': 'Count',
                num_columns: 5
            },

        });
}());

