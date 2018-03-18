/**
 * indicator-type.map is an angular factory.
 * It is consumed by indicatorTypeMapper service.
 * Its purpose is to provide a mapper object for indicatorTypeMapper service, so the service will be
 * able to decide which indicator correlates to which indicator type.
 * When an indicator type is ascertained, the type object is then returned to the caller of the
 * indicatorTypeMapper service to be used as the caller sees fit.
 *
 * The type objects are used as required.
 * Each base property on indicatorTypeMap is an indicator type.
 * It must have 'queries' property which is an array of objects. Each of those should be a key-value
 * JSON, and an indicator must qualify all key-values in a query object
 * (effectively an AND expression).
 * Example:
 'indicator105': {
                queries: [
                    {
                        dataEntitiesIds: ['vpn'],
                        anomalyTypeFieldName: 'vpn_geo_hopping',
                        evidenceType: 'Notification'
                    }
                ],
                templateUrl: 'app/layouts/alert/layouts/indicator-templates/singlecolumn.html',
                settings: {
                    singleColumn: {
                        params: entityTypeAnomalyTypeCount,
                        chartSettings: {

                            title: {
                                text: 'Countries For User {{entityName}}<br> Last 90 Days'
                            },
                            "series": [{}]
                        }
                    }

                },
                indicatorClass: 'gen'
            }
 * In this example an indicator must have a dataEntitiesIds that equals ['vpn'] and
 * anomalyTypeFieldName that equals 'vpn_geo_hopping' and indicatorType that equals 'Notification' in
 * order to qualify to be 'indicator105' type.
 * 'queries' is an array, so an indicator must qualify to any of the objects in 'queries' to qualify
 * (effectively an OR expression).
 *
 * Once a type has been ascertained, the whole object is returned.
 *
 * 'indicatorClass' is used by alert-tab.controller to determine the entire indicator view. (used by
 * goToindicatorTab method).
 *
 * 'template' or 'templateUrl' is used by indicator-tab-overview.controller . It uses the string
 * (either a direct string in template or a derived string from a template url) to build the
 * chart part of the view (if 'gen' class).
 *
 * 'settings' is an object that holds settingIds, where each id holds 'params' 'styleSettings',
 * and 'chartSettings'.
 *
 * 'params' is used as an interpolation base to build the query params. In the above example params
 * is an object called entityTypeAnomalyTypeCount, which is:
 {
      context_type: '{{entityTypeFieldName}}',
      context_value: '{{entityName}}',
      feature: '{{anomalyTypeFieldName}}',
      'function': 'Count',
      num_columns: 5
  }
 * This will be interpolated against the indicator and set in the query params.
 *
 * 'styleSettings' is used to define the style of the chart container (use in ng-style format).
 *
 * 'chartSettings' is handed over to a fs-chart directive which is merged into the typed settings
 * of a chart type (see shared/components/fs-chart)
 *
 * The settingIds are mapped in the html template. for example:
 * (The singlecoulmn template)
 <fs-dashboard-columns>
 <fs-dashboard-column column-span="15">
 <div class="loading" ng-if="sourceCountriesColumn.loading">Loading...</div>
 <fs-indicator-column settings-id="singleColumn"></fs-indicator-column>

 </fs-dashboard-column>

 </fs-dashboard-columns>

 * singleColumn in the 'settings' property on 'indicator105' is mapped to settings-id="singleColumn"
 *
 * This file uses variables to group common occurrences. For example entityTypeAnomalyTypeCount
 * is used as a common param configuration. Many indicator types use this exact configuration so
 * it makes sense to put it in a variable rather then repeat the code.
 */

(function () {
    'use strict';
    function indicatorTypeMapFactory (aggregatedSerialSettings,
        dualColumnSettings, singlePieChartSettings, scatterColumnChartSettings, sharedCredentialsSettings,
        aggregatedSerialDataRateSettings, activityTimeAnomalySettings, geoLocationSettings, geoSequenceSettings, lateralMovementSettings) {

        /**
         * This object is returned as angular.value. It holds all indicator types. Type can be a single
         * indicator or a family of indicator, etc.
         */
        var indicatorTypeMap = {
            // 'tag': {
            //     queries: [
            //         {
            //             anomalyTypeFieldName: 'tag'
            //         }
            //     ],
            //     indicatorClass: 'tag'
            // },
            // 'sharedCredentials': {
            //     queries: [
            //         {
            //             anomalyTypeFieldName: 'VPN_user_creds_share'
            //         }
            //     ],
            //     templateUrl: 'app/layouts/user/components/user-indicator/components/indicator-templates/shared-credentials.html',
            //     settings: sharedCredentialsSettings.settings,
            //     indicatorClass: 'gen'
            // },

            'singleColumnDataRate': {
                queries: [
                    {
                        dataEntitiesIds: ['vpn_session'],
                        anomalyTypeFieldName: 'data_bucket',
                        evidenceType: 'AnomalySingleEvent'
                    }

                ],
                settings: aggregatedSerialDataRateSettings.settings,
                templateUrl: 'app/layouts/user/components/user-indicator/components/indicator-templates/aggregated-serial.html',
                indicatorClass: 'gen'

            },

            'singleColumnHistogram': {
                queries: [
                    {

                        anomalyTypeFieldName: 'email_sender',
                        evidenceType: 'AnomalySingleEvent'
                    },
                    {

                        anomalyTypeFieldName: 'account_management_change_anomaly',
                        evidenceType: 'AnomalySingleEvent'
                    }

                ],
                templateUrl: 'app/layouts/user/components/user-indicator/components/indicator-templates/dualcolumn.html',
                settings: _.merge({},dualColumnSettings.settings,{secondColumn:{hidden:true}}),
                indicatorClass: 'gen'

            },

            'activityTimeAnomaly': {
                queries: [
                    {
                        anomalyTypeFieldName: 'event_time',
                        evidenceType: 'AnomalySingleEvent'
                    },
                    {
                        anomalyTypeFieldName: 'abnormal_event_day_time',
                        evidenceType: 'AnomalySingleEvent'
                    },
                    {
                        anomalyTypeFieldName: 'abnormal_logon_day_time',
                        evidenceType: 'AnomalySingleEvent'
                    },
                    {
                        anomalyTypeFieldName: 'abnormal_active_directory_day_time_operation',
                        evidenceType: 'AnomalySingleEvent'
                    }

                ],
                templateUrl: 'app/layouts/user/components/user-indicator/components/indicator-templates/activity-time-anomaly.html',
                settings: activityTimeAnomalySettings.settings,
                indicatorClass: 'gen'
            },
            'geoLocation': {
                queries: [
                    {
                        evidenceType: 'AnomalySingleEvent',
                        anomalyTypeFieldName: 'country'
                    }

                ],
                templateUrl: 'app/layouts/user/components/user-indicator/components/fs-indicator-am-geo-location/fs-indicator-am-geo-location-template.html',
                settings: geoLocationSettings.settings,
                indicatorClass: 'gen'
            },
            'geoLocationSequence': {
                queries: [
                    {
                        anomalyTypeFieldName: 'vpn_geo_hopping'
                    }
                ],
                templateUrl: 'app/layouts/user/components/user-indicator/components/fs-indicator-am-geo-location/fs-indicator-am-geo-location-template.html',
                settings: geoSequenceSettings.settings,
                indicatorClass: 'gen'
            },
            'basicTwoHistogramsUser': {
                queries: [
                    {
                        evidenceType: 'AnomalySingleEvent',
                        anomalyTypeFieldName: 'normalized_src_machine'
                    },
                    {
                        evidenceType: 'AnomalySingleEvent',
                        anomalyTypeFieldName: 'normalized_dst_machine'
                    },
                    {
                        evidenceType: 'AnomalySingleEvent',
                        anomalyTypeFieldName: 'action_code'
                    },
                    {
                        evidenceType: 'AnomalySingleEvent',
                        anomalyTypeFieldName: 'db_object'
                    },
                    {
                        evidenceType: 'AnomalySingleEvent',
                        anomalyTypeFieldName: 'db_username'
                    },
                    {
                        evidenceType: 'AnomalySingleEvent',
                        anomalyTypeFieldName: 'email_recipient_domain'
                    }

                ],
                templateUrl: 'app/layouts/user/components/user-indicator/components/indicator-templates/dualcolumn.html',
                settings: dualColumnSettings.settings,
                indicatorClass: 'gen'
            },
            'singlePieHistogram': {
                queries: [
                    {
                        anomalyTypeFieldName: 'auth_method',
                        evidenceType: 'AnomalySingleEvent'
                    },
                    {
                        anomalyTypeFieldName: 'failure_code',
                        evidenceType: 'AnomalySingleEvent'
                    },
                    {
                        anomalyTypeFieldName: 'action_type',
                        evidenceType: 'AnomalySingleEvent'
                    },
                    {
                        anomalyTypeFieldName: 'status',
                        evidenceType: 'AnomalySingleEvent'
                    },
                    {
                        evidenceType: 'AnomalySingleEvent',
                        anomalyTypeFieldName: 'return_code'
                    },

                    {
                        evidenceType: 'AnomalySingleEvent',
                        anomalyTypeFieldName: 'attachment_file_extension'
                    },
                    {
                        evidenceType: 'AnomalySingleEvent',
                        anomalyTypeFieldName: 'executing_application'
                    },
                    {
                        anomalyTypeFieldName: 'abnormal_computer_accessed_remotely'
                    },
                    {
                        anomalyTypeFieldName: 'abnormal_file_permision_change_operation_type'
                    },
                    {
                        anomalyTypeFieldName: 'abnormal_file_action_operation_type'
                    },
                    {
                        anomalyTypeFieldName: 'admin_changed_his_own_password'
                    },
                    {
                        anomalyTypeFieldName: 'user_account_enabled'
                    },
                    {
                        anomalyTypeFieldName: 'user_account_disabled'
                    },
                    {
                        anomalyTypeFieldName: 'user_account_unlocked'
                    },
                    {
                        anomalyTypeFieldName: 'user_account_type_changed'
                    },
                    {
                        anomalyTypeFieldName: 'user_account_locked'
                    },
                    {
                        anomalyTypeFieldName: 'user_password_never_expires_option_changed'
                    },
                    {
                        anomalyTypeFieldName: 'user_password_changed_by_non-owner'
                    },
                    {
                        anomalyTypeFieldName: 'user_password_changed'
                    },
                    {
                        anomalyTypeFieldName: 'nested_member_added_to_critical_enterprise_group'
                    },
                    {
                        anomalyTypeFieldName: 'member_added_to_critical_enterprise_group'
                    },
                    {
                        anomalyTypeFieldName: 'abnormal_destination_machine'
                    },
                    {
                        anomalyTypeFieldName: 'abnormal_source_machine'
                    },{
                        anomalyTypeFieldName: 'abnormal_object_change_operation'
                    },{
                        anomalyTypeFieldName: 'abnormal_group_membership_sensitive_operation'
                    },{
                        anomalyTypeFieldName: 'abnormal_remote_destination_machine'
                    },{
                        anomalyTypeFieldName: 'abnormal_site'
                    }
                ],
                templateUrl: 'app/layouts/user/components/user-indicator/components/indicator-templates/pie.html',
                settings: singlePieChartSettings.settings,
                indicatorClass: 'gen'
            },
            'aggregatedIndicatorWithTime': {
                queries: [
                    {
                        evidenceType: 'AnomalyAggregatedEvent'
                    }
                ],
                templateUrl: 'app/layouts/user/components/user-indicator/components/indicator-templates/aggregated-serial.html',
                settings: aggregatedSerialSettings.settings,
                indicatorClass: 'gen'

            },
            'lateralMovementIndicator': {
                queries: [
                    {
                        anomalyTypeFieldName: 'VPN_user_lateral_movement'
                    }
                ],
                templateUrl: 'app/layouts/user/components/user-indicator/components/indicator-templates/lateral-movement.html',
                indicatorClass: 'gen',
                settings: lateralMovementSettings.settings

            }
        };
        return indicatorTypeMap;
    }

    indicatorTypeMapFactory.$inject = [
        'indicatorTypeMapper.aggregated-serial',
        'indicatorTypeMapper.dual-column',
        'indicatorTypeMapper.pie',
        'indicatorTypeMapper.scatter-column',
        'indicatorTypeMapper.sharedCredentials',
        'indicatorTypeMapper.aggregated-serial-data-rate',
        'indicatorTypeMapper.activityTimeAnomaly',
        'indicatorTypeMapper.geo-location',
        'indicatorTypeMapper.geo-sequence',
        'indicatorTypeMapper.lateralMovement'
    ];

    angular.module('Fortscale.shared.services.indicatorTypeMapper')
        .factory('indicatorTypeMap', indicatorTypeMapFactory);
}());
