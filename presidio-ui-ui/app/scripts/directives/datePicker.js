(function () {
    'use strict';

    angular.module('DatePicker', ["Utils"])
        .directive('ofsDatepicker', ["utils", function (utils) {

            return {
                template: '<span><input type="text" ng-model="dateValue" ng-change="onDateValueChange(dateValue)" ' +
                'class="datepicker" placeholder="{{placeholder}}" ng-class="{ error: isRequired && !currentDate}" /> ' +
                '<select ng-show="showHours" ng-options="hour.value as hour.label for hour in hours" ng-model="hour"' +
                ' ng-change="onSelectHour(hour)"></select></span>',
                restrict: 'E',
                require: '?ngModel',
                replace: true,
                scope: {
                    onSelect: "&",
                    isRequired: "=?",
                    endOfDay: "=?",
                    startOfDay: "=?",
                    min: "=?minValue",
                    max: "=?maxValue",
                    showHours: "=?",
                    placeholder: "@"
                },
                link: function postLink(scope, element, attrs, ngModel) {
                    var $input = element.find("input"),
                        currentHour = 0;

                    var MOMENT_DATE_FORMAT = "MM/DD/YYYY",
                        DATEPICKER_DATE_FORMAT = "mm/dd/yy";


                    function setHours(min, max) {
                        scope.min = scope.min || 0;
                        scope.max = scope.max || 24;

                        scope.hours = [];
                        for (var i = scope.min; i <= scope.max; i++) {
                            scope.hours.push({value: i, label: utils.strings.padLeft(String(i), 2, "0") + ":00"});
                        }
                    }

                    function selectDate(date) {
                        if (scope.onSelect) {
                            scope.onSelect({date: date});
                        }
                        scope.currentDate = date;
                        ngModel.$setViewValue(date, scope);
                    }

                    function minMaxWatch(datepickerOption) {
                        return function (value) {
                            if (!value) {
                                return;
                            }

                            var currentDate = scope.currentDate;
                            if (!angular.isDate(currentDate) && typeof(scope.currentDate === "string")) {
                                currentDate = utils.date.getMoment(scope.currentDate, null, MOMENT_DATE_FORMAT);
                            }

                            $input.datepicker("option", datepickerOption + "Date", value);

                            if (typeof value === "string") {
                                value = parseInt(value, 10);
                            }

                            if (typeof value === "number") {
                                value = utils.date.getMoment('now').add(value, "days").toDate();
                            }

                            var shouldChange = datepickerOption === "min" ?
                            currentDate.valueOf() >= value.valueOf() :
                            currentDate.valueOf() <= value.valueOf();

                            if (currentDate && value && shouldChange) {
                                $input.val(utils.date.getMoment(currentDate).format(MOMENT_DATE_FORMAT));
                            }
                        };
                    }

                    scope.$on("$destroy", function (e, data) {
                        element.empty();
                        element.off();
                    });

                    ngModel.$render = function () {
                        try {
                            var valueMoment;
                            if (ngModel.$viewValue && ngModel.$viewValue.timeStart && ngModel.$viewValue.timeEnd) {
                                if (utils.date.getDatesSpan(ngModel.$viewValue.timeStart,
                                        ngModel.$viewValue.timeEnd).length === 0) {
                                    valueMoment = utils.date.getMoment(ngModel.$viewValue.timeStart);
                                }
                            }
                            if (!valueMoment) {
                                valueMoment = ngModel.$viewValue && utils.date.getMoment(ngModel.$viewValue);
                            }


                            if (valueMoment && valueMoment.isValid()) {
                                scope.dateValue = valueMoment.format(MOMENT_DATE_FORMAT);
                                scope.currentDate = valueMoment.toDate();
                                scope.hour = scope.currentDate.getHours();
                            }
                            else {
                                $input.val("");
                                scope.currentDate = null;
                            }
                        }
                        catch (e) {
                            $input.val("");
                            scope.currentDate = null;
                        }

                        scope.selectedTab = ngModel.$viewValue;
                    };

                    scope.$watch("min", minMaxWatch("min"));
                    scope.$watch("max", minMaxWatch("max"));

                    scope.onSelectHour = function (hour) {
                        currentHour = hour;
                        scope.hour = hour;

                        if (scope.onSelect) {
                            scope.onSelect({
                                date: utils.date.getMoment(
                                    scope.currentDate.setHours(hour)
                                ).toDate()
                            });
                        }
                    };

                    scope.onDateValueChange = function (value) {
                        if (!value && scope.currentDate) {
                            selectDate(null);
                        }
                    };

                    scope.hour = 0;

                    $input.datepicker({
                        dateFormat: DATEPICKER_DATE_FORMAT,
                        onSelect: function (dateText) {
                            scope.$apply(function () {
                                var date = utils.date.getMoment(dateText, null, MOMENT_DATE_FORMAT);

                                if (scope.endOfDay) {
                                    date.endOf("day");
                                }
                                else if (scope.startOfDay) {
                                    date.startOf("day");
                                }
                                else if (scope.hour) {
                                    date.add(scope.hour, "hours");
                                }

                                selectDate(date.toDate());
                            });
                        }
                    });

                    setHours();

                }
            };

        }]);
}());
