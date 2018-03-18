(function () {
    'use strict';

    /**
     * Handle Alerts page
     */

    function AlertsController ($scope, $element, alertsResourceSettings, alertsTableSettings,
        alertsIndicatorsTableSettings, splitterSettings, filtersPaneSettings,
        alertsMainState, tagsList, indicatorTypeList, dependencyMounter,TAGS_FEATURE_ENABLED) {

        var ctrl = this;

        dependencyMounter.mountOnConstructor(AlertsController, ['interpolation', 'utils',
            'fsResourceStore', 'BASE_URL', '$http', 'URLUtils', 'indicatorSeverities', 'entityUtils', 'page',
            'dateRanges', 'appConfig', '$state', 'fsDownloadFile', 'fsNanobarAutomationService']);

        // Set instance variables
        ctrl.$scope = $scope;
        ctrl.$element = $element;
        ctrl.alertsResourceSettings = _.merge({}, alertsResourceSettings);
        ctrl.alertsTableSettings = _.merge({}, alertsTableSettings);
        ctrl.alertsIndicatorsTableSettings = _.merge({}, alertsIndicatorsTableSettings);

        ctrl.fsSplitterSettings = _.merge({}, splitterSettings);
        ctrl.filtersPaneSettings = _.merge({}, filtersPaneSettings);
        ctrl.alertsMainState = _.merge({}, alertsMainState);
        ctrl.tagsList = _.merge({}, tagsList);
        ctrl.indicatorTypeList = _.merge([], indicatorTypeList);
        ctrl._alertsMainStateDefault = _.merge({}, alertsMainState);

        ctrl._defaultDaysRange = this.appConfig.getConfigValue('ui.' + this.$state.current.name, 'daysRange');

        ctrl.alertsResourceAdapter = function (data) {
            return ctrl._alertsResourceAdapter(data);
        };

        ctrl.filtersPaneSettings.tagsFilter = ctrl.tagsList;
        ctrl.TAGS_FEATURE_ENABLED = TAGS_FEATURE_ENABLED;
        ctrl.NANOBAR_ID = 'alerts';
        ctrl.dataFetchDelegate = (promise) => {
            ctrl.fsNanobarAutomationService.addPromise(this.NANOBAR_ID, promise);
        };

        ctrl._init();
    }

    angular.extend(AlertsController.prototype, {

        /**
         * Adds a startOfDay field for grouping purposes
         *
         * @param {Array} data
         * @returns {Array}
         * @private
         */
        _alertsResourceAdapter: function (data) {
            var ctrl = this;

            _.each(data, function (row) {
                row.startDateDay = ctrl.utils.date.getMoment(row.startDate, null)
                        .startOf('day').unix() + '000';
                row.indicatorsNum = row.evidences.length;

                // Add alert id and user id to indicator to populate ui-sref directive
                _.each(row.evidences, indicator => {
                    indicator.alert = {
                        id: row.id,
                        entityId: row.entityId
                    };
                });
            });

            return data;
        },

        _addDefaultDateRange: function () {
            this.alertsMainState.date_range_filter = {
                value: this.dateRanges.getByDaysRange(this._defaultDaysRange, 'short')
            };

            this._alertsMainStateDefault.date_range_filter = {
                value: this.dateRanges.getByDaysRange(this._defaultDaysRange, 'short')
            };
        },



        /**
         * Broadcasts an event that requests all controls (by control id) to reset.
         * It provides on the eventData the control id to reset, and the initial state.
         *
         * @param {object} stateContainer
         * @param {string} tableId
         * @param {Array<string>} controlIds
         */
        clearFilters: function (stateContainer, tableId, controlIds) {
            var ctrl = this;
         //   this.resetPreStateTablePageNumber(stateContainer, tableId);
            _.each(controlIds, function (id) {
                ctrl.$scope.$broadcast('control:reset', {
                    controlId: id,
                    initialState: (ctrl._alertsMainStateDefault[id] &&
                    ctrl._alertsMainStateDefault[id].value) || null
                });
            });
        },

        /**
         * Fetches user id and navigates to user overview
         * TODO: Replace dataQuery api with entity restful api
         *
         * @param {object} alert
         */
        navigateToEntityProfile: function (alert) {
            this.entityUtils.navigateToEntityProfile(alert.entityType, alert.entityId);

        },

        getAlertsCSV: function (stateContainer, alertsResourceSettings) {

            var interpolatedParams = this.interpolation.interpolate(alertsResourceSettings, stateContainer.stateModel);
            delete interpolatedParams.page;
            delete interpolatedParams.size;
            var excludedValues = ["","_ALL_"];

            //Copy all the attributes from interpolatedParams.params to filterParams, Except attributes with values from excludedValues array
            var filterParams = _.omitBy(interpolatedParams.params, function(value, key) {
                return _.indexOf(excludedValues, value) > -1;
            });
            var query = $.param(filterParams);
            var src = this.BASE_URL + "/alerts/export?" + query;
            this.fsDownloadFile.openIFrame(src);
        },

        /**
         * Returns a sorted array of severities
         *
         * @param {{}} severitiesObj
         * @returns {Array|undefined}
         */
        getSeveritiesCount: function (severitiesObj) {

            // return an array only if severitiesObj received
            if (severitiesObj) {

                // Create a new severities count list: Array<{name: string, count: number}>
                this._totalSeveritiesCount = _.map(_.sortBy(this.indicatorSeverities, 'order'),
                    function (indicatorSeverity) {
                        return _.merge({}, indicatorSeverity, {count: severitiesObj[indicatorSeverity.name]});
                    });

                // The newly created list can not be returned as is, because angular will identify the new object as
                // a change, and will start a new digest cycle to make sure the object is stable. It will never be
                // stable if that object is returned. Therefore we need to store the object in a property on the
                // instance, and compare it manually to the latest object delivered. If the objects are equal,
                // return the latest one, and not the new one. This will satisfy angular and no unneeded cycles will
                // occur.

                // If no _LatestTotalSeveritiesCount then set it and return _totalSeveritiesCount
                if (this._LatestTotalSeveritiesCount === undefined) {
                    this._LatestTotalSeveritiesCount = this._totalSeveritiesCount;
                    return this._totalSeveritiesCount;
                }

                // Equate the two lists (_totalSeveritiesCount and _LatestTotalSeveritiesCount). If the values are
                // not equal set _LatestTotalSeveritiesCount and return _totalSeveritiesCount.
                if (!_.every(this._totalSeveritiesCount, _.bind(function (sc, index) {
                        return (sc.count === this._LatestTotalSeveritiesCount[index].count);
                    }, this))) {
                    this._LatestTotalSeveritiesCount = this._totalSeveritiesCount;
                    return this._totalSeveritiesCount;
                }

                return this._LatestTotalSeveritiesCount;

            }
        },
        /**
         * Init function of the controller
         *
         * @private
         */
        _init: function _init () {
            this.page.setPageTitle("Alerts");
            this._addDefaultDateRange();

        }
    });

    AlertsController.$inject = ['$scope', '$element', 'alertsResourceSettings',
        'alertsTableSettings', 'alertsIndicatorsTableSettings', 'splitterSettings',
        'filtersPaneSettings', 'alertsMainState', 'tagsList', 'indicatorTypeList',
        'dependencyMounter','TAGS_FEATURE_ENABLED'];

    angular.module('Fortscale')
        .controller('AlertsController', AlertsController);

}());
