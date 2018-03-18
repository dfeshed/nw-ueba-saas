(function () {
	'use strict';
	/**
	 * Uses example:
	 * 	<fs-date-range date-range-id="dr1" fetch-state-delegate="::overview.getRangeState"
     * 	update-state-delegate="::overview.setRangeState"></fs-date-range>
	 */
	function fsDateRangeDirective () {


		/**
		 * The directive's controller function
		 *
		 * @constructor
		 */
		function FsDateRangeController ($scope, $timeout, utils, $filter) {
			this.init($scope, $timeout, utils, $filter);
		}

		angular.extend(FsDateRangeController.prototype, {

			//Set the "from" and "to" to cover the last 7 dats
			setLast7Days: function (){

				var currentMoment = this.utils.date.getMoment('now');
				this.endTime   = currentMoment.toDate();

				currentMoment = this.utils.date.getMoment('now');
				this.startTime = currentMoment.subtract(7, 'days').toDate();
			},

			//Set the "from" and "to" to cover the last month
			setLastMonth: function (){
				var currentMoment = this.utils.date.getMoment('now');
				this.endTime =	currentMoment.toDate();

				currentMoment = this.utils.date.getMoment('now');
				this.startTime = currentMoment.subtract(1, 'months').toDate();
			},

			/**
			 * Validate fetchStateDelegate.
			 * Throw TypeError if fetchStateDelegate is received and is not a function
			 * @private
			 */
			_validateGetStateFn: function () {
				if (this.fetchStateDelegate && !angular.isFunction(this.fetchStateDelegate)) {
					throw new TypeError('fsDateRange.directive: FsDateRangeController: ' +
						'If fetchStateDelegate is provided, it must be a function.');
				}
			},
			/**
			 * Validate fetchStateDelegate.
			 * Throw TypeError if fetchStateDelegate is received and is not a function
			 * @private
			 */
			_validateSetStateFn: function () {
				if (this.updateStateDelegate && !angular.isFunction(this.updateStateDelegate)) {
					throw new TypeError('fsDateRange.directive: FsDateRangeController: ' +
						'If updateStateDelegate is provided, it must be a function.');
				}
			},

            /**
             * update start and end time when fetchStateDelegate changed
             * @param newVal
             * @private
             */
            _fetchStateDelegateWatchAction: function (newVal) {

                var ctrl = this;


                if (angular.isString(newVal)) {

                    var values = newVal.split(',');
                    var startTimeUnix = values[0];
                    var endTimeUnix = values[1];

                    if (ctrl.startTimeUnix !== startTimeUnix || ctrl.endTimeUnix !== endTimeUnix) {
                        ctrl.startTimeUnix = startTimeUnix;
                        ctrl.endTimeUnix = endTimeUnix;

                        var startTimeDateText = ctrl.$filter('date')(startTimeUnix*1000,'MM/dd/yyyy','UTC');
                        ctrl.startTime=ctrl.utils.date.getMoment(startTimeDateText, false, 'MM/DD/YYYY').toDate();

                        var endTimeDateText = ctrl.$filter('date')(endTimeUnix*1000,'MM/dd/yyyy','UTC');
                        ctrl.endTime=ctrl.utils.date.getMoment(endTimeDateText, false, 'MM/DD/YYYY').toDate();


                    }
                }
            },

            /**
             * when startTime or endTime changed- this method invoke updateStateDelegate
             * with the new values, and convert to long value of the date.
             * If the system use UTC - we also convert the time to UTC using
             * ctrl.utils.date.getMoment
             *
             * @param newValues - array of [startTime, endTime]
             * @param oldValues - array of [startTime, endTime]
             * @private
             */
            _startTimeEndTimeWatchAction: function (newValues, oldValues){

                var ctrl = this;

                if (newValues && newValues[0] && newValues[1] &&
                    (oldValues[0] !== newValues[0] || oldValues[1] !== newValues[1])) {

                    //invoke updateStateDelegate only if it defined
                    if (this.updateStateDelegate) {

                        var startTimeMoment = ctrl._prepareSelectedDate(newValues[0]);
                        var endTimeMoment   = ctrl._prepareSelectedDate(newValues[1]);
                        if (endTimeMoment) {
                            endTimeMoment.endOf('day');
                        }
                        var value = '' +
                            ctrl.utils.date.toUnixTimestamp(startTimeMoment) + ',' +
                            ctrl.utils.date.toUnixTimestamp(endTimeMoment);

                        this.updateStateDelegate({
                        	id: ctrl.dateRangeId,
                        	type: 'DATA',
                        	value: value,
                            immediate: this._immediate
                        });
                    }

                    if (this.formCtrl) {
                        this.formCtrl.$setDirty();
                    }
                }
            },
            /**
             * This method get a date object, truncate the hours/minutes/seconds etc...,
             * and convert the date to the time zone according to configuration.
             * The method ignores the original time zone
             *
             * @param {Date} time date object, in any timeozne
             * @return  The method returns a moment object,
             * representing the selected date with the application
             * timezone according to application's configuration.
             *
             * Throw TypeError if time is not a date or not defined
             * @private
             *
             */
            _prepareSelectedDate: function (time){
                if (!(time instanceof Date)){
                    throw new TypeError('fsDateRange.directive: FsDateRangeController: ' +
                        'time must be defined and be an Object of type Date');
                }
                var ctrl = this;
                var dateFormat =    'MM/dd/yyyy';
                var dateFilterFn =  ctrl.$filter('date');
                var timeText = dateFilterFn(time, dateFormat);
                return ctrl.utils.date.getMoment(timeText, null, 'MM/DD/YYYY');
            },

            /**
             * This function compare start time and end time
             * (and ignores hours, minutes, seconds ...),
             * and return true if they are not the same
             *
             * @returns {boolean} true when start time and end time on different days,
             * false if they are on the same day
             */
            isStartTimeAndEndTimeOnDifferentDays: function (){
                var ctrl = this;
                var dateFilterFn =  ctrl.$filter('date');

                if (!angular.isDefined(ctrl.startTime) || !angular.isDefined(ctrl.endTime)) {
                    return false;
                }

                return dateFilterFn(ctrl.startTime).valueOf() !==
                    dateFilterFn(ctrl.endTime).valueOf();

            },

			/**
			 * Init
			 */
			init: function init ($scope, $timeout, utils, $filter) {

                var ctrl = this;
                ctrl._immediate = this._immediate?ctrl._immediate:false; //this._immediate is false by default
                // Put dependencies on the instance
                ctrl.$scope = $scope;
                ctrl.utils = utils;
                ctrl.$filter = $filter;


                ctrl._validateGetStateFn();
                ctrl._validateSetStateFn();

                //Init default values
                ctrl.setLast7Days();

                //Listen when state delegate return different value
                if (ctrl.fetchStateDelegate) {
                    $scope.$watch(function () {
                        return ctrl.fetchStateDelegate(ctrl.dateRangeId);
                    }, ctrl._fetchStateDelegateWatchAction.bind(ctrl));
                }

				//When startTime or endTime changed - 	 update the state
				$scope.$watchGroup([
						function(){
							return ctrl.startTime;
						},
						function(){
							return ctrl.endTime;
						}
						],  ctrl._startTimeEndTimeWatchAction.bind(ctrl));

                $scope.$on('control:reset', function (event, eventData) {
                    if (eventData.controlId === ctrl.dateRangeId) {
                        if (!_.isNil(eventData.initialState) && eventData.initialState.split(',').length===2){
                            var startEndArr = eventData.initialState.split(',');
                            ctrl.endTime   = new Date(startEndArr[1]*1000);
                            ctrl.startTime = new Date(startEndArr[0]*1000);
                        } else { //Fallback, backward competability
                            ctrl.setLast7Days();
                        }

                    }
                });
			}
		});

		FsDateRangeController.$inject = ['$scope','$timeout','utils','$filter'];

        function linkFn(scope, element, attr, formCtrl){
            scope.dateRange.formCtrl = formCtrl;
        }


		return {
			restrict: 'E',
			scope: {},
			controller: FsDateRangeController,
			controllerAs: 'dateRange',
			templateUrl: 'app/shared/components/controls/fs-daterange/fs-daterange.view.html',
			bindToController: {
				dateRangeId: '@',
				fetchStateDelegate: '=',
				updateStateDelegate: '=',
                label: '@',
                _immediate:'@?immediate',
			},
            link: linkFn,
            require: '?^^form'
		};
	}

	fsDateRangeDirective.$inject = [];

	angular.module('Fortscale.shared.components.fsDateRange', [ "kendo.directives" ,'Utils'])
		.directive('fsDateRange', fsDateRangeDirective);
}());
