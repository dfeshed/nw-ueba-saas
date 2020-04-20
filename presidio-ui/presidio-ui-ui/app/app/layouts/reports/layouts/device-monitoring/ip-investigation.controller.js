(function () {
    'use strict';

    var SUSPICIOUS_EVENT_SCORE_FIELD_NAME = 'suspicious_event_score';
    var EVENT_COUNT_FIELD_NAME = 'event_count';
    var TYPE_FIELD_NAME = 'type';
    var EVENT_TIME_FIELD_NAME = 'event_time';
    var NORMALIZED_USERNAME_FIELD_NAME = 'normalized_username';
    var SOURCE_MACHINE_FIELD_NAME = 'source_machine';

    function IPInvestigationController (appConfig, $state, $scope, dateRanges, machinesForIpResource,
        machinesForIpTableSettings, usersForIpResource, usersForIpTableSettings, fsNanobarAutomationService) {

        var ctrl = this;

        // Put injections on instance
        ctrl.state = _.merge({}, $state.current.data);
        ctrl.$scope = $scope;

        ctrl._defaultDaysRange = appConfig.getConfigValue('ui.' + $state.current.name, 'daysRange');

        ctrl.mainState = _.merge({}, {
            events_time: {
                value: dateRanges.getByDaysRange(ctrl._defaultDaysRange, 'short')
            }
        });

        ctrl.machinesForIpResource = _.merge({}, machinesForIpResource);
        ctrl.machinesForIpTableSettingsMaster = _.merge({}, machinesForIpTableSettings);
        ctrl.usersForIpResource = _.merge({}, usersForIpResource);
        ctrl.usersForIpTableSettingsMaster = _.merge({}, usersForIpTableSettings);

        ctrl.ipInvestigationMachinesResourceAdapter = function (dataList) {
            return ctrl._ipInvestigationResourceAdapter(dataList, 'machines');
        };

        ctrl.ipInvestigationUsersResourceAdapter = function (dataList) {
            return ctrl._ipInvestigationResourceAdapter(dataList, 'users');
        };

        this.NANOBAR_ID = 'reports';
        this.dataFetchDelegate = (promise) => {
            fsNanobarAutomationService.addPromise(this.NANOBAR_ID, promise);
        };

        // Init
        this.init();
    }

    _.merge(IPInvestigationController.prototype, {

        /**
         * Extends table settings with GDS columns
         *
         * @param {Array<{type: string, suspicious_event_score: number}>} dataEntitiesSorted
         * @param {{columns: array}} settingsMaster
         * @returns {{}}
         * @private
         */
        _extendTableSettings: function (dataEntitiesSorted, settingsMaster) {
            var settings = _.merge({}, settingsMaster);
            _.each(dataEntitiesSorted, function (dataEntity, index) {
                settings.columns.push({
                    title: 'Suspicious<br>' + dataEntity[TYPE_FIELD_NAME] + '<br>Events',
                    field: 'dataEntities[' + index + '].' + SUSPICIOUS_EVENT_SCORE_FIELD_NAME
                });
            });
            return settings;
        },

        /**
         * Extends MachinesForIp Settings. Adds GDS columns.
         *
         * @param {Array<{type: string, suspicious_event_score: number}>} dataEntitiesSorted
         * @private
         */
        _extendMachinesForIpSettings: function (dataEntitiesSorted) {
            this.machinesForIpTableSettings = this._extendTableSettings(dataEntitiesSorted,
                this.machinesForIpTableSettingsMaster);
        },

        /**
         * Extends UsersForIp Settings. Adds GDS columns.
         *
         * @param {Array<{type: string, suspicious_event_score: number}>} dataEntitiesSorted
         * @private
         */
        _extendUsersForIpSettings: function (dataEntitiesSorted) {
            this.usersForIpTableSettings = this._extendTableSettings(dataEntitiesSorted,
                this.usersForIpTableSettingsMaster);
        },

        /**
         * Adds Kedo tooltip to account-control's question mark.
         *
         * @private
         */
        _initIPAddressControlTooltip: function () {
            this.$scope.$applyAsync(function () {
                $('.explain-ip-address').kendoTooltip({
                    position: "bottom",
                    showOn: "mouseenter click",
                    autoHide: true,
                    content: this.state.ipAddressControlTooltip,
                    width: "15em",
                    animation: {
                        close: {
                            effects: "fade:out",
                            duration: 500
                        },
                        open: {
                            effects: "fade:in",
                            duration: 500
                        }
                    }
                });
            }.bind(this));
        },

        /**
         * Takes an old object and a new object and transfers all properties directly or via an adapter function.
         * adapterFn will be invoked (if provided) with oldValue, newValue, oldObject, newObject
         *
         * @param {Array<{sourceProperty: string, targetProperty:string=, adapterFn:function=}>} propertiesConfList
         * @param {object} newObj
         * @param {object} oldObj
         * @param {boolean=} isOnce
         * @private
         */
        _setProperties: function (propertiesConfList, newObj, oldObj, isOnce) {

            isOnce = !!isOnce;

            _.each(propertiesConfList, function (propertyConf) {
                var sourceProperty = propertyConf.sourceProperty;
                var targetProperty = propertyConf.targetProperty || sourceProperty;
                if ((isOnce && newObj[targetProperty] === undefined) || !isOnce) {
                    newObj[targetProperty] = _.isFunction(propertyConf.adapterFn) ?
                        propertyConf.adapterFn(oldObj[sourceProperty], newObj[targetProperty], oldObj, newObj) :
                        oldObj[sourceProperty];
                }
            });

        },

        /**
         * Takes two values and returns the sum. If both arguments are not numbers, it returns the last argument if
         * its a number, then first argument if its a number, or null.
         *
         * @param {number|*} oldValue
         * @param {number|*} newValue
         * @returns {number|null}
         * @private
         */
        _incrementTentative: function (oldValue, newValue) {
            if (_.isNumber(newValue) && _.isNumber(oldValue)) {
                newValue += oldValue;
                return newValue;
            }

            if (_.isNumber(newValue)) {
                return newValue;
            }

            if (_.isNumber(oldValue)) {
                return oldValue;
            }

            return null;
        },

        /**
         * Takes a dataList, group by type, and return a sorted list by event_score of objects.
         *
         * @param {{type: string, suspicious_event_score: number}} dataList
         * @returns {Array<{type: string, suspicious_event_score: number}>} Sorted by suspicious_event_score
         * @private
         */
        _getDataEntitiesSorted: function (dataList) {

            // Group by type
            var groupByType = _.groupBy(dataList, TYPE_FIELD_NAME);

            // Extract data entities keys
            var dataEntitiesKeys = Object.keys(groupByType);

            // Create data entities sums object
            var dataEntitiesSums = {};

            // Iterate through dataEntitiesKeys and sum by suspicious_event_score and store on dataEntitiesSums
            _.each(dataEntitiesKeys, function (dataEntityName) {
                dataEntitiesSums[dataEntityName] =
                    _.sumBy(groupByType[dataEntityName], SUSPICIOUS_EVENT_SCORE_FIELD_NAME);
            });

            // Return sorted list of data entities: {type: string, suspicious_event_score: integer}
            return _.orderBy(_.map(dataEntitiesSums, function (value, key) {
                var obj = {};
                obj[SUSPICIOUS_EVENT_SCORE_FIELD_NAME] = value;
                obj[TYPE_FIELD_NAME] = key;
                return obj;
            }), SUSPICIOUS_EVENT_SCORE_FIELD_NAME, 'desc');
        },

        /**
         * Populates data entities list in a newDay object
         *
         * @param {array<{type: string, event_count: number, suspicious_event_score: number}>} dayGroup
         * @param {object} newDay
         * @param {{type: string}} dataEntity
         * @returns {{type: string, suspicious_event_score: number}|object}
         * @private
         */
        _populateDataEntities: function (dayGroup, newDay, dataEntity) {
            // Create dataEntities
            var type = dataEntity[TYPE_FIELD_NAME];
            var query = {};
            query[TYPE_FIELD_NAME] = type;
            var oldDay = _.filter(dayGroup, query)[0];

            if (oldDay) {
                // Increment devices_count, users_count, event_count
                this._setProperties([
                    {
                        sourceProperty: EVENT_COUNT_FIELD_NAME,
                        adapterFn: this._incrementTentative
                    }
                ], newDay, oldDay);
            } else {
                oldDay = {};
            }

            // Create a returned object
            var obj = {};
            obj[TYPE_FIELD_NAME] = type;
            obj[SUSPICIOUS_EVENT_SCORE_FIELD_NAME] = oldDay[SUSPICIOUS_EVENT_SCORE_FIELD_NAME] || 0;

            return obj;

        },

        /**
         * Populates a newDataObj with adapted items, where each key on the object is a day date.
         *
         * @param {object} newDataObj An object to be populated. From this object the new list will be returned
         * @param {{type: string, suspicious_event_score: number}} dataEntitiesSorted
         * @param {string} subGroupFieldName
         * @param {array<{type: string, event_time: string, source_machine: string, event_count: number,
         * suspicious_event_score: number}>} dayGroup
         * @private
         */
        _populateNewDataObj: function (newDataObj, dataEntitiesSorted, dayGroup) {

            _.each(dayGroup, _.bind(function (subGroup, subGroupName) {
                // Create new object for each day and subgroup if it doesn't exist
                var objKey = subGroup[0][EVENT_TIME_FIELD_NAME] + '_' + subGroupName;
                if (!newDataObj[objKey]) {
                    newDataObj[objKey] = {};
                }

                // Truncate newDay namespace for future reference
                var newDay = newDataObj[objKey];

                // Set source_machine, event_time on newDay object
                this._setProperties([
                    {
                        sourceProperty: SOURCE_MACHINE_FIELD_NAME
                    },
                    {
                        sourceProperty: NORMALIZED_USERNAME_FIELD_NAME
                    },
                    {
                        sourceProperty: EVENT_TIME_FIELD_NAME,
                        adapterFn: function (oldValue) {
                            return new Date(oldValue);
                        }
                    }
                ], newDay, subGroup[0], true);

                // Set event_count (=0), users_count (=0), devices_count (=0)
                newDay[EVENT_COUNT_FIELD_NAME] = newDay[EVENT_COUNT_FIELD_NAME] || 0;

                // populate dataEntities
                newDay.dataEntities =
                    _.map(dataEntitiesSorted, this._populateDataEntities.bind(this, subGroup, newDay));
            }, this));

        },

        /**
         * An adapter for the returned data for ip investigation.
         *
         * @param {{}} dataList
         * @param {string} target
         * @returns {Array}
         * @private
         */
        _ipInvestigationResourceAdapter: function (dataList, target) {

            // Get sorted list of data entities sums
            var dataEntitiesSorted = this._getDataEntitiesSorted(dataList);

            // Extend the tables settings to add columns settings
            if (target === 'machines') {
                this._extendMachinesForIpSettings(dataEntitiesSorted);
            }
            if (target === 'users') {
                this._extendUsersForIpSettings(dataEntitiesSorted);
            }

            // Group dataList by date (by day) to groupObj
            var groupedByDayObj = _.groupBy(dataList, EVENT_TIME_FIELD_NAME);

            // Create sub group by source machine or user name
            var subGroupFieldName = '';
            subGroupFieldName = target === 'machines' ? SOURCE_MACHINE_FIELD_NAME : subGroupFieldName;
            subGroupFieldName = target === 'users' ? NORMALIZED_USERNAME_FIELD_NAME : subGroupFieldName;
            _.each(groupedByDayObj, function (dayObj, key) {
                groupedByDayObj[key] = _.groupBy(dayObj, subGroupFieldName);
            });

            // create new object (keys are event_time)
            // new object scheme: {source_machine: string, event_time: Date, event_count: integer,
            // users_count: integer, devices_count: integer,
            // dataEntities: [{type: string, suspicious_event_score: integer (=0)}]}
            var newDataObj = {};

            // Iterate through days groups and populate newDataObj
            _.each(groupedByDayObj, this._populateNewDataObj.bind(this, newDataObj, dataEntitiesSorted));

            // Return a sorted list of days
            var newDataList = _.orderBy(_.map(newDataObj, function (day) {
                return day;
            }), EVENT_TIME_FIELD_NAME, 'desc');

            newDataList._meta = dataList._meta;
            return newDataList;

        },

        init: function () {
            this._initIPAddressControlTooltip();
        }
    });

    IPInvestigationController.$inject = ['appConfig', '$state', '$scope', 'dateRanges', 'machinesForIpResource',
        'machinesForIpTableSettings', 'usersForIpResource', 'usersForIpTableSettings', 'fsNanobarAutomationService'];
    angular.module('Fortscale.layouts.reports')
        .controller('IPInvestigationController', IPInvestigationController);
}());
