'use strict';

angular.module('Fortscale')
    .directive('searchbox', ["$timeout", "reports", "transforms", function ($timeout, reports, transforms) {
        return {
            template: "<span><input type='text' ng-model='searchValue' /><i class='icon-spinner icon-spin' ng-class='{ hidden: !loading }'></i></span>",
            restrict: 'A',
            replace: true,
            require: "?ngModel",
            link: function postLink(scope, element, attrs, ngModel) {
                var settings,
                    isInit,
                    MIN_SEARCH_SIZE = 3,// If the current term is smaller than this, search won't run.
                    onSelect,
                    onSelectTimeout;

                scope.$watch(attrs.searchSettings, function(value){
                    if (value && !isInit){
                        settings = value;
                        if (settings.onSelect){
                            onSelect = function(value){
                                $timeout.cancel(onSelectTimeout);
                                onSelectTimeout = $timeout(function(){
                                    scope.$apply(function(){
                                        scope.$emit("dashboardEvent", { event: settings.onSelect, data: { value: value } });
                                    });
                                }, 40);
                            }
                        }
                        init();
                        isInit = true;
                    }
                });

                scope.$watch(attrs.ngModel, function(value){
                    scope.searchValue = value;
                });

                function split( val ) {
                    return val.split( /,\s*/ );
                }
                function extractLast( term ) {
                    return split( term ).pop();
                }

                function init(){
                    var input = element.find("input").bind( "keydown", function( event ) {
                        if ( event.keyCode === $.ui.keyCode.TAB && $( this ).data( "ui-autocomplete" ).menu.active){
                            event.preventDefault();
                        }
                        else if (event.keyCode === $.ui.keyCode.ESCAPE){
                            $(this)
                                .val("")
                                .autocomplete("close");
                        }
                        else if (event.keyCode === $.ui.keyCode.ENTER && settings.allowAllStrings){
                            onSelect(this.value);
                        }
                    })
                        .attr("placeholder", settings.placeholder)
                        .autocomplete({
                            delay: 400,
                            source: function( request, response ) {
                                if (!request.term || request.term.length < MIN_SEARCH_SIZE)
                                    return;
                                scope.loading = true;

                                var searchTerm = extractLast(request.term);
                                if (settings.termTransform){
                                    searchTerm = transforms.string(searchTerm, settings.termTransform);
                                }
                                reports.runReports(settings.reports, { term: searchTerm }, true).then(function(results){
                                    var resultsArr = [];
                                    angular.forEach(results, function(reportResults, reportIndex){
                                        angular.forEach(reportResults.data, function(row){
                                            resultsArr.push({ label: row[settings.resultField || "Result"], value: row[settings.resultField || "Result"], report: settings.reports[reportIndex] });
                                        });
                                    });

                                    scope.loading = false;
                                    response(resultsArr);
                                }, function(error){
                                    scope.loading = false;
                                    response([]);
                                });
                            },
                            search: function() {
                                // custom minLength
                                var term = extractLast( this.value );
                                if ( term.length < 2 ) {
                                    return false;
                                }
                            },
                            focus: function() {
                                // prevent value inserted on focus
                                return false;
                            },
                            select: function( event, ui ) {
                                input.val(ui.item.value);
                                onSelect(ui.item.value);
                                return false;
                            }
                        });

                    if (settings.allowAllStrings){
                        input.bind("blur", function(e){
                            onSelect(this.value);
                        });
                    }
                }
            }
        };
    }]);