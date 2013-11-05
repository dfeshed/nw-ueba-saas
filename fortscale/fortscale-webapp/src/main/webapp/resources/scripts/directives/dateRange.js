'use strict';

angular.module('Fortscale')
    .directive('daterange', function ($parse, transforms) {
        return {
            template: "<input type='text' class='daterange-input' />",
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

                    var timeStart, timeEnd;
                    if (value){
                        if (angular.isObject(value)){
                            timeStart = value.timeStart;
                            timeEnd = value.timeEnd || value.timeStart;
                        }
                        else{
                            timeStart = transforms.getDate(value);
                            if (timeStart.isValid)
                                timeEnd = timeStart = timeStart.toDate();
                        }
                    }

                    params = {
                        timeStart: transforms.date(timeStart || new Date(), { format: "MM/DD/YYYY" }),
                        timeEnd: transforms.date(timeEnd || new Date(), { format: "MM/DD/YYYY" })
                    };

                    input.value = params.timeStart === params.timeEnd ? params.timeStart : [params.timeStart, params.timeEnd].join(" - ");
                    $(input).daterangepicker({
                        presets: {
                            specificDate: 'Specific Date',
                            dateRange: "Date Range"
                        },
                        presetRanges: [
                            {text: 'Today', dateStart: 'today', dateEnd: 'today', closeOnSelect: true },
                            {text: 'Last week', dateStart: function(){ return moment().subtract("days", 7).toDate() }, dateEnd: function(){ moment().toDate() }, closeOnSelect: true },
                            {text: 'Last month', dateStart: function(){ return moment().subtract("months", 1).toDate() }, dateEnd: function(){ moment().toDate() }, closeOnSelect: true }
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
