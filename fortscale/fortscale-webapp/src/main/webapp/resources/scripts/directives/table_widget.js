'use strict';

angular.module('Fortscale')
    .directive('widgetTable', ["widgets", function (widgets) {
        return {
            restrict: 'C',
            require: "?ngModel",
            link: function postLink(scope, element, attrs, ngModel) {
                angular.forEach(scope.view.settings.fields, function(field){
                    if (field.events){
                        for(var eventType in field.events){
                            element.on(eventType, ".event-field-" + field.name.replace(/\s/g, "_"), function(e){
                                var dataRowIndex = $(e.target).closest("[data-index]").attr("data-index");

                                if (dataRowIndex){
                                    var fieldEvent = field.events[eventType],
                                        dataRow = scope.view.rawData[dataRowIndex];

                                    if (fieldEvent.confirm){
                                        if (!window.confirm(widgets.parseFieldValue({}, fieldEvent.confirm, dataRow, dataRowIndex, scope.dashboardParams)))
                                            return false;
                                    }

                                    scope.fireEvent(fieldEvent, e, dataRow, field);
                                }
                            });
                        }
                    }
                });
            }
        };
    }]);
