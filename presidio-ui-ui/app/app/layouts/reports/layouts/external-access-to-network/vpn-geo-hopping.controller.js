(function () {
    'use strict';

    var USERNAME_FIELD_NAME = 'username';
    var USER_ID_FIELD_NAME = 'userid';
    var NORMALIZED_USERNAME_FIELD_NAME = 'normalized_username';
    var COUNTRY_FIELD_NAME = 'country';
    var CITY_FIELD_NAME = 'city';
    var EVENT_TIME_FIELD_NAME = 'event_time_utc';
    var CHART_USERS_LIMIT = 10;

    function VPNGeoHoppingController (appConfig, $state, $scope, dateRanges, userUtils, userControlResource,
        userControlSettings, VPNGeoHoppingResource, VPNGeoHoppingTableSettings, VPNGeoHoppingChartSettings, fsNanobarAutomationService) {

        var ctrl = this;

        // Put injections on instance
        ctrl.state = _.merge({}, $state.current.data);
        ctrl.$scope = $scope;
        ctrl.dateRanges = dateRanges;
        ctrl.userUtils = userUtils;

        ctrl.userControlResource = userControlResource;
        ctrl.userControlSettings = userControlSettings;
        ctrl.VPNGeoHoppingResource = VPNGeoHoppingResource;
        ctrl.VPNGeoHoppingTableSettings = VPNGeoHoppingTableSettings;
        ctrl.VPNGeoHoppingChartSettings = VPNGeoHoppingChartSettings;

        ctrl._defaultDaysRange = appConfig.getConfigValue('ui.' + $state.current.name, 'daysRange');

        ctrl.resourceAdapter = function (dataList) {
            return ctrl._resourceAdapter(dataList);
        };

        this.NANOBAR_ID = 'reports';
        this.dataFetchDelegate = (promise) => {
            fsNanobarAutomationService.addPromise(this.NANOBAR_ID, promise);
        };


        // Init
        this.init();
    }

    _.merge(VPNGeoHoppingController.prototype, {

        /**
         * Initiates report main state
         *
         * @private
         */
        _initMainState: function () {

            var ctrl = this;
            ctrl.mainState = _.merge({}, {
                events_time: {
                    value: ctrl.dateRanges.getByDaysRange(ctrl._defaultDaysRange, 'short')
                },
                min_score: {
                    value: 50
                }
            });
        },

        /**
         * Initiates chart settings. Combines json "dry" settings with functional settings.
         *
         * @private
         */
        _initChartSettings: function () {
            var ctrl = this;

            this.chartSettings = _.merge({}, ctrl.VPNGeoHoppingChartSettings, {
                tooltip: {
                    formatter: function () {
                        var s = '';
                        s += '<b>Username: </b>' + this.point[USERNAME_FIELD_NAME] + '<br>';
                        s += '<b>Normalized Username: </b>' + this.point[NORMALIZED_USERNAME_FIELD_NAME] + '<br>';
                        s += '<b>Country: </b>' + this.point[COUNTRY_FIELD_NAME] + '<br>';
                        s += '<b>City: </b>' + this.point[CITY_FIELD_NAME] + '<br>';
                        s += '<b>Event Time: </b>' + moment(this.point[EVENT_TIME_FIELD_NAME]).utc()
                                .format("MMM Do YY HH:mm:ss") + '<br>';
                        return s;
                    }
                }
            });
        },

        /**
         * Initiates chart map settings.
         *
         * @private
         */
        _initChartMapSettings: function () {

            var ctrl = this;

            ctrl.chartMapSettings = {
                x: {
                    key: EVENT_TIME_FIELD_NAME,
                    fn: function (date) {
                        var dt = new Date(date);
                        return dt.valueOf();
                    }
                },
                y: {
                    key: USERNAME_FIELD_NAME,
                    fn: function (user) {
                        return ctrl._chartUsersList.indexOf(user);
                    }
                },
                [USERNAME_FIELD_NAME]: USERNAME_FIELD_NAME,
                [NORMALIZED_USERNAME_FIELD_NAME]: NORMALIZED_USERNAME_FIELD_NAME,
                [EVENT_TIME_FIELD_NAME]: EVENT_TIME_FIELD_NAME,
                [COUNTRY_FIELD_NAME]: COUNTRY_FIELD_NAME,
                [CITY_FIELD_NAME]: CITY_FIELD_NAME,
                [USER_ID_FIELD_NAME]: USER_ID_FIELD_NAME
            };
        },

        /**
         * Returns a list of user names from user series.
         *
         * @param {Array<{name: string, data: array}>} userSeries
         * @returns {Array}
         * @private
         */
        _getListOfUserNames: function (userSeries) {
            return _.map(userSeries, 'name');
        },

        /**
         * Takes dataList and returns an object that is grouped by username.
         *
         * @param {Array<{username: string}>} dataList
         * @returns {*}
         * @private
         */
        _groupByUser: function (dataList) {
            return _.groupBy(dataList, USERNAME_FIELD_NAME);
        },

        /**
         * Sorts each user group list (internally) by event time ascending.
         *
         * @param userGroups
         * @private
         */
        _sortUserGroups: function (userGroups) {
            _.each(userGroups, function (userGroup, key) {
                userGroups[key] = _.orderBy(userGroup, EVENT_TIME_FIELD_NAME, 'asc');
            });
        },

        /**
         * Adds mostRecentEvent property to each user group. This property will be later used to sort the groups.
         *
         * @param userGroups
         * @private
         */
        _addMostRecentProperty: function (userGroups) {
            _.each(userGroups, function (userGroup) {
                userGroup.mostRecentEvent = userGroup[userGroup.length - 1][EVENT_TIME_FIELD_NAME];
            });
        },

        /**
         * Takes user groups object and converts to list, sorted by mostRecentEvent.
         *
         * @param userGroups
         * @returns {Array<{name: string, data: Array<{}>}>}
         * @private
         */
        _convertUserGroupsToList: function (userGroups) {
            return _.map(_.sortBy(_.values(userGroups), 'mostRecentEvent').slice(0, CHART_USERS_LIMIT),
                function (userList) {
                    return {
                        name: userList[0][USERNAME_FIELD_NAME],
                        data: userList
                    };
                }).reverse();
        },

        /**
         * Takes the data list received from server, and convert it to sorted list of user groups
         *
         * @param dataList
         * @returns {*|Array.<{name: string, data: Array.<{}>}>}
         * @private
         */
        _createUsersSeries: function (dataList) {
            var userGroups = this._groupByUser(dataList);
            this._sortUserGroups(userGroups);
            this._addMostRecentProperty(userGroups);
            return this._convertUserGroupsToList(userGroups);
        },

        /**
         * Intercepts the received resource and creates chart model and table model.
         *
         * @param dataList
         * @returns {*}
         * @private
         */
        _resourceAdapter: function (dataList) {

            // Save reference to original list
            this._sourceDataList = dataList;

            // get list of 10 users by their most recent event
            this.chartModel = this._createUsersSeries(dataList);

            // Get list of user names for y axis categories
            this.chartSettings.yAxis.categories = this._chartUsersList = this._getListOfUserNames(this.chartModel);

            // Set the table model
            this.tableModel = this._sourceDataList.slice(0);
            this.tableModel._meta = this._sourceDataList._meta;

            return dataList;
        },

        /**
         * Handler for users list returned from accounts control. Sets fall back display name, and prevent display
         * name duplications.
         *
         * @param {array<{fallBackDisplayName: string, username: string}>} users
         * @private
         */
        _userControlDataTextFn: function (users) {
            this.userUtils.setFallBackDisplayNames(users);
            this.userUtils.preventFallBackDisplayNameDuplications(users);
        },

        /**
         * Attach handler for users list returned from accounts control. Sets fall back display name, and prevent
         * display name duplications. Handler is bound to the controller's instance.
         *
         * @private
         */
        _enrichUserControlSettings: function () {
            this.userControlSettings.dataTextFn = this._userControlDataTextFn.bind(this);
        },

        /**
         * Controller's init function
         */
        init: function () {
            this._initMainState();
            this._initChartSettings();
            this._initChartMapSettings();
            this._enrichUserControlSettings();

        }
    });

    VPNGeoHoppingController.$inject =
        ['appConfig', '$state', '$scope', 'dateRanges', 'userUtils', 'userControlResource', 'userControlSettings',
            'VPNGeoHoppingResource', 'VPNGeoHoppingTableSettings', 'VPNGeoHoppingChartSettings', 'fsNanobarAutomationService'];
    angular.module('Fortscale.layouts.reports')
        .controller('VPNGeoHoppingController', VPNGeoHoppingController);
}());
