'use strict';

angular.module('Fortscale')
    .directive('daterange', function ($parse, transforms) {
        return {
            template: "<input type='text' />",
            restrict: 'E',
            require: "?ngModel",
            link: function postLink(scope, element, attrs, ngModel) {
                var params,
                    init,
                    input = element.children("input")[0],
                    ngChangeFunc = attrs.ngChange ? $parse(attrs.ngChange) : null;

                var ngChangeTimeout,
                    lockUpdate;

                scope.$watch(attrs.ngModel, function(value){
                    if (init || lockUpdate)
                        return;

                    params = {
                        timeStart: transforms.date(value.timeStart, { format: "MM/DD/YYYY" }),
                        timeEnd: transforms.date(value.timeEnd, { format: "MM/DD/YYYY" })
                    };

                    input.value = params.timeStart === params.timeEnd ? params.timeStart : [params.timeStart, params.timeEnd].join(" - ");
                    $(input).daterangepicker({
                        presets: {
                            specificDate: 'Specific Date',
                            dateRange: "Date Range"
                        },
                        presetRanges: [
                            {text: 'Today', dateStart: 'today', dateEnd: 'today', closeOnSelect: true },
                            {text: 'Last week', dateStart: 'today-7days', dateEnd: 'today', closeOnSelect: true },
                            {text: 'Last month', dateStart: 'today-1months', dateEnd: 'today', closeOnSelect: true }
                        ],
                        closeOnSelect: false,
                        doneButtonText: "Apply",
                        dateFormat: "mm/dd/yy",
                        onDone: function(dates){
                            params.timeStart = dates.start.toString("yyyy-MM-dd");
                            params.timeEnd = dates.end.toString("yyyy-MM-dd");
                            lockUpdate = true;

                            scope.$apply(function(){

                                ngModel.$setViewValue(params);
                                if (ngChangeFunc){
                                    ngChangeFunc(scope, params);
                                }

                                clearTimeout(ngChangeTimeout);
                                ngChangeTimeout = setTimeout(function(){ lockUpdate = false; }, 200);
                            });
                        },
                        latestDate: "today"
                    });

                    init = true;
                });
            }
        };
    });
