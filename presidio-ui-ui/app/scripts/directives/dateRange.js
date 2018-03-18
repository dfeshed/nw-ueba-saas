angular.module('Fortscale')
    .directive('daterange',
        ["$parse", "transforms", "utils",
        function ($parse, transforms, utils) {
        'use strict';

        return {
			template: "<input type='text'  readonly='readonly' class='daterange-input' />",
            restrict: 'E',
            require: "?ngModel",
            link: function postLink(scope, element, attrs, ngModel) {
                var params,
                    init,
                    input = element.children("input")[0],
                    ngChangeFunc = attrs.ngChange ? $parse(attrs.ngChange) : null;

                var MOMENT_DATE_FORMAT = "MM/DD/YYYY";

                scope.$on("$destroy", function(e, data){
                    $(input).empty().off();
                    element.empty();
                    element.off();
                });

                function onChange(dates) {
                    var timeStart = utils.date.getMoment(dates.start, null, MOMENT_DATE_FORMAT);
                    var timeEnd = utils.date.getMoment(dates.end, null, MOMENT_DATE_FORMAT);

                    params.timeStart = timeStart.startOf('day');
                    params.timeEnd = timeEnd.endOf('day');

                    ngModel.$setViewValue(params);

                    if (ngChangeFunc) {
                        ngChangeFunc(scope, params);
                    }
                }

                scope.$watch(attrs.ngModel, function (value) {
                    var timeStart, timeEnd;

                    if (value) {
                        if (angular.isObject(value)) {
                        	if (value.timeStart && value.timeEnd) {
                                timeStart = utils.date.getMoment(value.timeStart).startOf('day');
                                timeEnd = utils.date.getMoment(value.timeEnd).endOf('day');
							}
						}

						if (!timeStart || !timeEnd) {
                            timeStart = utils.date.getMoment(value).startOf('day');

                            if (timeStart.isValid()) {
                                timeEnd = timeStart = timeStart.toDate();
                            }
                        }
                    }

                    params = {
                        timeStart: transforms.date(timeStart || utils.date.getMoment('now'), {
                            format: MOMENT_DATE_FORMAT
                        }),
                        timeEnd: transforms.date(timeEnd || utils.date.getMoment('now'), {
                            format: MOMENT_DATE_FORMAT
                        })
                    };

                    if (params.timeStart === params.timeEnd) {
                        input.value = params.timeStart;
                    }
                    else {
                        input.value = [params.timeStart, params.timeEnd].join(" - ");
                    }

                    // Init DatePicker component
                    $(input).daterangepicker({
                        presets: {
                            dateRange: "Date Range"
                        },
                        presetRanges: [
                            {text: 'Last week', dateStart: 'today-7days', dateEnd: 'today', closeOnSelect: true },
                            {text: 'Last month', dateStart: 'today-1months', dateEnd: 'today', closeOnSelect: true }
                        ],
                        closeOnSelect: false,
                        doneButtonText: "Apply",
                        dateFormat: "mm/dd/yy",
                        onDone: onChange,
                        onChange: onChange,
                        latestDate: "today"
                    });

                    init = true;
                });
            }
        };
    }]);
