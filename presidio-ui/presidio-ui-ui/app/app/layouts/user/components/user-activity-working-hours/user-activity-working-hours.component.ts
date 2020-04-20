module Fortscale.layouts.user {

    import IActivityUserWorkingHour = Fortscale.shared.services.entityActivityUtils.IActivityUserWorkingHour;
    interface IWorkingHour {
        hour:number;
        active:boolean
    }

    class ActivityWorkingHoursController {

        _workingHours:IActivityUserWorkingHour[];
        workingHours:IWorkingHour[];

        /**
         * "Activates" an hour (which will, via the template, add an 'active' class to the hour-bar)
         * @param hour
         * @private
         */
        _setHourActive (hour:IWorkingHour) {
            hour.active = !hour.active;
        }

        /**
         * Iterates through received working hours, and activates each corresponding hour on workingHours.
         * @private
         */
        _digestWorkingHours () {

            // Reset the index, and sort received working hours (to make sure hours are in order)
            let index = 0;
            let workingHours = _.sortBy<IActivityUserWorkingHour>(this._workingHours, 'hour');

            // Iterate through received working hours, and turn on each of this.workingHours
            _.each(workingHours, (workingHour:IActivityUserWorkingHour) => {

                // Make sure received hours do not break the method.
                if (workingHour.hour > 24 || workingHour.hour < 1) {
                    return;
                }

                // Move forward on this.workingHours, and stop and set when an hour matches.
                let cont = true;
                while (cont) {

                    if (this.workingHours[index].hour === workingHour.hour) {

                        // Activate hour with a timeout to create a cascade effect.
                        this.$timeout(this._setHourActive.bind(this, this.workingHours[index]), index * 100);
                        cont = false;
                    }
                    index += 1;
                }

            });

        }

        /**
         * Creates a new working hours list and returns it
         * @returns {Array}
         * @private
         */
        _generateWorkingHoursList ():IWorkingHour[] {
            let wh = [];
            for (let i = 1; i <= 24; i += 1) {
                wh.push({
                    hour: i,
                    active: false
                });
            }
            return wh;
        }

        /**
         * Sets to this.workingHours a list of deactivated IWorkingHour
         * @private
         */
        _initWorkingHours () {
            this.workingHours = this._generateWorkingHoursList();
        }

        /**
         * Sets watch to received workingHours. When workingHours is received a new list is set to this.workingHours,
         * and the received items are digested.
         * @private
         */
        _initWorkingHoursWatch () {
            this.$scope.$watch(
                () => this._workingHours,
                (workingHours:IActivityUserWorkingHour[]) => {
                    if (workingHours && workingHours.length) {
                        this._initWorkingHours();
                        this._digestWorkingHours();
                    }
                }
            );
        }

        /**
         * Generates the time as an am/pm string
         * @param workingHour
         * @returns {any}
         */
        getHourBarTitle (workingHour: IWorkingHour): string {
            let hour = workingHour.hour % 12;
            let isAm = workingHour.hour < 13;

            if (hour === 0) {
                hour = 12;
                isAm = !isAm;
            }

            return hour + (isAm ? ' AM' : ' PM');
        };

        $onInit () {
            // init working hours
            this._initWorkingHours();
            this._initWorkingHoursWatch();
        }

        static $inject = ['$scope', '$timeout'];

        constructor (public $scope:ng.IScope, public $timeout:ng.ITimeoutService) {
        }
    }

    let activityWorkingHoursComponent:ng.IComponentOptions = {
        controller: ActivityWorkingHoursController,
        templateUrl: 'app/layouts/user/components/user-activity-working-hours/user-activity-working-hours.component.html',
        bindings: {
            _workingHours: '<workingHours'
        }
    };
    angular.module('Fortscale.layouts.user')
        .component('userActivityWorkingHours', activityWorkingHoursComponent);
}
