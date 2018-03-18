(function () {
    'use strict';

    var errMsg = 'anomalyTypeFormatter.filter: ';

    function anomalyTypeFormatter ($filter, assert, fsIndicatorErrorCodes) {

        function pipeNumberFormatterFilter (value) {
            return $filter('trancateDecimal')(value);
        }

        function pipeShortDateFilter(value){
            return $filter('date')(new Date(value),'yyyy-MM-dd HH:mm');
        }

        function pipeDataUsageAnomalyFilter (value) {
            return value + '/s';
        }

        function pipeFailureCodeAnomalyFilter (errorCode, indicator) {
            var dataEntityId = indicator.dataEntitiesIds[0];
            return fsIndicatorErrorCodes.getDisplayMessage(dataEntityId, errorCode);
        }

        function pipeToCamelCaseRemoveUnderscore (name, indicator) {
            // var dataEntityId = indicator.dataEntitiesIds[0];
            if (!_.isNil(name)){
                // return _.capitalize(_.replace(name,new RegExp("_","g"),' '));
                var newName = '';
                _.forEach(_.split(name,"_"),function(namePart){
                    newName=newName + " " + _.capitalize(namePart);
                });
                return _.trim(newName);
             } else {
                return '';
            }
        }

        function pipeFormatByIndicator (value, indicator) {

            /**
             * The filter config list.
             * @type {Array<{name: string, queries: Array<{}>, filter: function}>}
             */
            var filterConfigList = [
                {
                    name: "Bytes",
                    queries: [
                        {
                            anomalyType: "Downloaded Bytes"
                        },
                        {
                            anomalyType: "Data Usage Anomaly"
                        },
                        {
                            anomalyType: "High Volume of Printed Data"
                        },
                        {
                            anomalyType: "Email Attachment File Size Anomaly"
                        },
                        {
                            anomalyType: "Aggregated File Size Volume"
                        },
                        {
                            anomalyType: "Aggregated File Size Volume Copied from Network Directory"
                        },
                        {
                            anomalyType: "Aggregated File Size Volume Moved to Removable Device"
                        },
                        {
                            anomalyType: "Aggregated File Size Volume Moved from Network Directory"
                        },
                        {
                            anomalyType: "Aggregated File Size Volume Copied to Removable Device"
                        },
                        {
                            anomalyType: "Aggregated File Size Volume Moved to Removable Device"
                        },
                        {
                            anomalyType: "Aggregated File Size Volume Moved from Network Directory"
                        },
                        {
                            anomalyType: "Aggregated File Size Volume Copied from Network Directory"
                        },
                        {
                            anomalyType: "Aggregated File Size Volume Sent to an External Single Recipient"
                        }


                    ],
                    filter: $filter('prettyBytes')
                },
                {
                    name: "Data Usage Anomaly",
                    queries: [
                        {
                            anomalyType: "Data Usage Anomaly"
                        }
                    ],
                    filter: pipeDataUsageAnomalyFilter
                },
                {
                    name: "Short Time",
                    queries: [
                        {
                            anomalyType: "Time"
                        },
                        {
                            anomalyType: "Activity Time Anomaly"
                        },
                        {
                            anomalyTypeFieldName: "event_time"
                        }
                    ],
                    filter: pipeShortDateFilter
                },
                {
                    name: "Failure Code",
                    queries: [
                        {
                            anomalyTypeFieldName: "failure_code"
                        }
                    ],
                    filter: pipeFailureCodeAnomalyFilter
                },
                {
                    name: "To Upper Case",
                    queries: [
                        {
                            anomalyTypeFieldName: "abnormal_group_membership_sensitive_operation"
                        },
                        {
                            anomalyTypeFieldName: "abnormal_file_permision_change_operation_type"
                        },
                        {
                            anomalyTypeFieldName: "FILE_ACCESS_RIGHTS_CHANGED"
                        },
                        {
                            anomalyTypeFieldName: "abnormal_file_action_operation_type"
                        },
                        {
                            anomalyTypeFieldName: "abnormal_object_change_operation"
                        }
                    ],
                    filter: pipeToCamelCaseRemoveUnderscore
                }
            ];

            /**
             * Checks if indicator.key equals the provided value
             *
             * @param {*} value
             * @param {string} key
             * @returns {boolean}
             */
            function matchQueryField (value, key) {
                return indicator[key] === value;
            }

            /**
             * Iterates through query field and return true if all query fields match
             *
             * @param {{}} query
             * @returns {boolean}
             */
            function matchQuery (query) {
                return _.every(query, matchQueryField);
            }

            /**
             * Returns true if any of the queries is a match
             *
             * @param {Array<{}>} queries
             * @returns {boolean}
             */
            function isAnyFilterQueryMatch (queries) {
                return _.some(queries, matchQuery);
            }

            /**
             * If any of the queries in the filter config object match the indicator, pipe the value through filter
             *
             * @param {{name: string, queries: Array<{}>, filter: function}} filterConfigObj
             * @param {*} value
             * @returns {*}
             */
            function tentativelyPipeFilter (filterConfigObj, value) {
                if (isAnyFilterQueryMatch(filterConfigObj.queries)) {
                    return filterConfigObj.filter(value, indicator);
                }
                return value;
            }


            // Iterate through queryConfigList and pipe the value through any filter that has a query match
            _.each(filterConfigList, function (filterConfigObj) {
                value = tentativelyPipeFilter(filterConfigObj, value);
            });

            return value;
        }

        /**
         * This filter will find if any filter query matches the indicator. If it does, the value will be piped via the
         * filter.
         *
         * @param {*} value
         * @param {{}} indicator
         * @returns {*}
         */
        function anomalyTypeFormatterFilter (value, indicator) {

            // Make sure indicator is provided
            assert.isObject(indicator, 'indicator', errMsg);

            // Convert numbers to integer (default formatter)
            value = pipeNumberFormatterFilter(value);

            // Match filter queries and pipe the filters
            value = pipeFormatByIndicator(value, indicator);

            // Return the formatted value
            return value;

        }

        return anomalyTypeFormatterFilter;
    }

    anomalyTypeFormatter.$inject = ['$filter', 'assert', 'fsIndicatorErrorCodes'];

    angular.module('Fortscale.shared.filters')
        .filter('anomalyTypeFormatter', anomalyTypeFormatter);
}());
