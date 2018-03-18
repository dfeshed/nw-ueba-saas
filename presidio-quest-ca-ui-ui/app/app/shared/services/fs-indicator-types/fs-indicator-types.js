(function () {
    'use strict';

    function FsIndicatorTypes (assert, $http, BASE_URL, appConfig, dataEntities) {
        // this._PATH = '/alerts/exist-anomaly-types';
        this._ERR_MSG = 'FsIndicatorTypes: ';
        this._CONFIG_LOCALE_KEY = 'system.locale.settings';
        this._CONFIG_INDICATOR_KEY = `messages.${appConfig.getConfigItem(this._CONFIG_LOCALE_KEY).value}.evidence.`;
        this._MAJOR_DELIMITER = '@@@';
        this._MINOR_DELIMITER = '@@';

        /**
         * Used to define the indicator type. Single or aggregated. Also used to define the sort order.
         * @type {Array<{id: string, queryFn: Function, sortOrder: number}>}
         * @private
         */
        this._indicator_types = [
            {
                id: 'Single',
                queryFn: function (val) {
                    return !(/daily$|hourly$/.test(val));
                },
                sortOrder: 0
            },
            {
                id: 'Aggregated',
                queryFn: function (val) {
                    return /daily$|hourly$/.test(val);
                },
                sortOrder: 1
            }
        ];


        /**
         * Return the indicator types resource
         *
         * @returns {Promise}
         * @private
         */
        this._getIndicatorsListResource = function (path) {
            return $http.get(BASE_URL + path);
        };

        /**
         *
         * @param {Array<string>} list
         * @private
         */

        /**
         *
         * @param {Array<string>} list
         * @returns {Array<{dataSourceId: string, anomalyTypeFieldName: string, id: string} | {}>}
         * @private
         */
        this._breakListByValue = function (list) {
            return _.map(list, listItem => {
                var listItemKeyValueList = listItem.split(this._MAJOR_DELIMITER);

                if (listItemKeyValueList.length !== 2) {
                    console.warn(this._ERR_MSG + "Trying to parse an incorrect value; " + listItem);
                    return {};
                }

                var dataSourceId = listItemKeyValueList[0];
                var anomalyTypeFieldName = listItemKeyValueList[1];

                return {
                    id: listItem,
                    dataSourceId: dataSourceId,
                    anomalyTypeFieldName: anomalyTypeFieldName
                };
            });
        };

        /**
         *
         * @param {Array<{dataSourceId: string, anomalyTypeFieldName: string, id: string}>} list
         * @returns {Array<{dataSourceId: string, anomalyTypeFieldName: string, anomalyTypeName: string, id: string}>}
         */
        this._populateAnomalyName = function (list) {
            return _.map(list, listItem => {
                    // Get anomaly name from config item

                var anomalyTypeConfigItem =
                    appConfig.getConfigItem(this._CONFIG_INDICATOR_KEY + listItem.dataSourceId + "." +
                        listItem.anomalyTypeFieldName);

                if (!anomalyTypeConfigItem) {
                    anomalyTypeConfigItem =
                        appConfig.getConfigItem(this._CONFIG_INDICATOR_KEY + listItem.anomalyTypeFieldName);
                }
                // If config item was found, then name would be its value, otherwise set it to anomalyTypeFieldName
                if (anomalyTypeConfigItem && anomalyTypeConfigItem.value) {
                    listItem.anomalyTypeName = anomalyTypeConfigItem.value;
                } else {
                    listItem.anomalyTypeName = listItem.anomalyTypeFieldName;
                }
                return listItem;
            });
        };

        this._populateAnomalyNameSingle = function (listItem) {

                    // Get anomaly name from config item

            var anomalyTypeName="";
            var anomalyTypeConfigItem =
                appConfig.getConfigItem(this._CONFIG_INDICATOR_KEY + listItem);

            if (!anomalyTypeConfigItem) {
                anomalyTypeConfigItem =
                    appConfig.getConfigItem(this._CONFIG_INDICATOR_KEY + listItem.anomalyTypeFieldName);
            }
            // If config item was found, then name would be its value, otherwise set it to anomalyTypeFieldName
            if (anomalyTypeConfigItem && anomalyTypeConfigItem.value) {
                anomalyTypeName = anomalyTypeConfigItem.value;
            } else {
                anomalyTypeName = listItem;
            }
            return anomalyTypeName;

        };

        //noinspection JSClosureCompilerSyntax
        /**
         *
         * @returns {Array<{dataSourceId: string, anomalyTypeFieldName: string, anomalyTypeName: string, id: string}>}
         * @returns {Array<{dataSourceId: string, anomalyTypeFieldName: string, anomalyTypeName: string,
         * dataSourceName: string, dataSourceName: string, id: string}>}
         * @private
         */
        this._populateDataSourceName = function (list) {
            return _.map(list, listItem => {

                // Get data entity
                var dataSource = dataEntities.getEntityById(listItem.dataSourceId);

                // If data entity exists set name to dataSourceName otherwise set it listItem.dataSourceId
                if (dataSource) {
                    listItem.dataSourceName = dataSource.name;
                } else {
                    listItem.dataSourceName = listItem.dataSourceId;
                }

                return listItem;
            });
        };

        /**
         * Populates anomalyTypeFullName: <dataSourceName - anomalyTypeName>
         *
         * @param list
         * @returns {Array}
         * @private
         */
        this._populateAnomalyFullName = function (list) {
            return _.map(list, listItem => {
                listItem.anomalyTypeFullName = listItem.dataSourceName + " - " + listItem.anomalyTypeName;
                return listItem;
            });
        };

        /**
         * Populates indicator types and indicator types order for sorting.
         *
         * @param list
         * @returns {Array}
         * @private
         */
        this._populateIndicatorType = function (list) {
            return _.map(list, listItem => {
                _.some(this._indicator_types, indicatorTypeQryObj => {
                    if (indicatorTypeQryObj.queryFn(listItem.id)) {
                        listItem.indicatorType = indicatorTypeQryObj.id;
                        listItem.indicatorTypeOrder = indicatorTypeQryObj.sortOrder;
                    }
                    return false;
                });

                return listItem;
            });
        };

        /**
         *
         * @param {Array<{dataSourceId: string, anomalyTypeFieldName: string, anomalyTypeName: string,
         * dataSourceName: string, dataSourceName: string, id: string}>} list
         * @param {Array<string>} sort the sorting strings
         * @param {Array<string>} order the sorting order
         * @returns {Array<{dataSourceId: string, anomalyTypeFieldName: string, anomalyTypeName: string,
         * dataSourceName: string, dataSourceName: string, id: string}>}
         * @private
         */
        this._orderPopulatedList = function (list, sort, order) {
            return _.orderBy(list, sort, order);
        };

        /**
         *
         * @param {Array<{dataSourceId: string, anomalyTypeFieldName: string, anomalyTypeName: string,
         * dataSourceName: string, dataSourceName: string, id: string}>} list
         * @returns {Array<{dataSourceId: string, anomalyTypeFieldName: string, anomalyTypeName: string,
         * dataSourceName: string, dataSourceName: string, id: string}>}
         * @private
         */
        this._populateFinalizedSinglesList = function (list) {

            var groupedListByAnomalyTypeFullName = _.groupBy(list, "anomalyTypeFullName");
            return _.map(groupedListByAnomalyTypeFullName, group => {
                var groupObj = _.merge({}, group[0]);

                // Set full id as a csv of ids
                groupObj.id = groupObj.dataSourceId + this._MAJOR_DELIMITER +
                    _.map(group, listItem => listItem.anomalyTypeFieldName).join(this._MINOR_DELIMITER);
                return groupObj;
            });
        };

        /**
         *
         * @param {Array<{dataSourceId: string, anomalyTypeFieldName: string, anomalyTypeName: string,
         * dataSourceName: string, dataSourceName: string, id: string}>} list
         * @returns {Array<{dataSourceId: string, anomalyTypeFieldName: string, anomalyTypeName: string,
         * dataSourceName: string, dataSourceName: string, id: string}>}
         * @private
         */
        this._populateFinalCommonList = function (list) {
            var groupedListByAnomalyTypeFullName = _.groupBy(list, "dataSourceName");
            return _.map(groupedListByAnomalyTypeFullName, group => {
                var groupObj = _.merge({}, group[0]);
                groupObj.id = groupObj.dataSourceId;
                groupObj.anomalyTypeFullName = "All " + groupObj.dataSourceName + " Indicators";
                return groupObj;

            });
        };

        /**
         * Takes a list of indicator type ids (anomalyTypeFieldName) and matches the display name taken from messages.
         *
         * @param {Array<string>} list
         * @returns {Array<{id: string, value: string}>}
         * @private
         */
        this._populateIdValueList = function (list) {

            // Break into a list of dataSourceId, anomalyTypeFieldName
            // var populatedList = this._breakListByValue(list) || [];
            //
            // // Filter out empty objects
            // populatedList = populatedList
            //     .filter(listItem => !!listItem.id);
            //
            // // add anomaly name for each item on list
            // populatedList = this._populateAnomalyName(populatedList);
            //
            // // Add data source name for each item on list
            // populatedList = this._populateDataSourceName(populatedList);
            //
            // // add to list concat value of data source name + anomaly name as anomalyFullName
            // populatedList = this._populateAnomalyFullName(populatedList);
            //
            // // Add indicator type and indicator type order
            // populatedList = this._populateIndicatorType(populatedList);
            //
            // // Create final single list
            // var finalSinglesList = this._populateFinalizedSinglesList(populatedList);
            //
            // // Sort list
            // finalSinglesList = this._orderPopulatedList(finalSinglesList,
            //     ['dataSourceName', 'indicatorTypeOrder', 'anomalyTypeFullName'],
            //     ['asc', 'asc', 'asc']);
            //
            // var finalCommonList = this._populateFinalCommonList(populatedList);
            // finalCommonList = this._orderPopulatedList(finalCommonList, ['dataSourceName'], ['asc']);
            //
            // var finalList = finalCommonList.concat(finalSinglesList);
            var currentThis = this;
            var finalList = list;

            // Create a list of id: anomalyTypeFieldName, value: anomalyFullName
            return _.map(finalList, (value,prop) => {
                var prettyName = currentThis._populateAnomalyNameSingle(prop);
                return {id: prop, value: prettyName, count:value};
            });

        };

        /**
         * Gets a list  a list of indicator type ids (anomalyTypeFieldName) from the server,
         * and creates a id-value list to be returned on resolve.
         * On error it will return an empty array.
         *
         * @returns {Promise}
         */
        this.getIndicatorsList = function (path) {
            var ctrl = this;
            var ERR_MSG = ctrl._ERR_MSG + 'getIndicatorsList: ';

            return ctrl._getIndicatorsListResource(path)
                .then(res => {

                    return ctrl._populateIdValueList(res.data);
                })
                .catch(err => {
                    console.error(ctrl._ERR_MSG + 'Indicators type list process failed.');
                    console.error(err);
                    return [];
                });
        };
    }

    FsIndicatorTypes.$inject = ['assert', '$http', 'BASE_URL', 'appConfig', 'dataEntities'];
    angular.module('Fortscale.shared.services.fsIndicatorTypes', [])
        .service('fsIndicatorTypes', FsIndicatorTypes);
}());
