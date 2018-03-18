(function () {
    'use strict';

    angular.module('Fortscale')
        .directive('searchbox', ["$timeout", "reports", "transforms", "widgets", "$parse", "$rootScope",
            "search", "utils", function ($timeout, reports, transforms, widgets, $parse, $rootScope, search, utils) {
                return {
                    template: "<span class='searchbox' ng-class='{ loading: loading }'><input type='text'  " +
                    "placeholder='{{placeholder}}' class='search' ng-class='{ error: isRequired }' />" +
                    "<i class='icon-spinner icon-spin'></i></span>",
                    restrict: 'AE',
                    replace: true,
                    require: "?ngModel",
                    scope: {
                        onSelect: "&",
                        searchSettings: "=",
                        isRequired: "=",
                        display: "=",
                        charEntered: "="
                    },
                    link: function postLink(scope, element, attrs, ngModel) {
                        var isInit,
                            MIN_SEARCH_SIZE = 1,// If the current term is smaller than this, search won't run.
                            onSelectTimeout,
                            input = element.find("input"),
                            onSelect;

                        if (!element[0].classList.contains("search")) {
                            element[0].classList.add("search");
                        }

                        scope.$watch("onSelect", function () {
                            if (Object(scope.onSelect) === scope.onSelect && scope.onSelect.url) {
                                onSelect = function (selected) {
                                    if (selected.$item) {
                                        var path = utils.strings.parseValue(scope.onSelect.url, selected.$item);
                                        if (/^#/.test(scope.onSelect)) {
                                            window.location.hash = path;
                                        } else {
                                            window.location.href = path;
                                        }
                                    }
                                };
                            }
                            else {
                                onSelect = scope.onSelect;
                            }
                        });

                        scope.$watch("searchSettings", function (value) {
                            if (value && !isInit) {
                                init();
                            }
                        });

                        if (ngModel) {
                            ngModel.$render = function () {
                                if (ngModel.$viewValue) {
                                    input.val(scope.display || ngModel.$viewValue);
                                }
                            };
                        }


                        function split(val) {
                            return val.split(/,\s*/);
                        }

                        function extractLast(term) {
                            return split(term).pop();
                        }

                        function parseResults(results) {

                            var value, label;

                            if (scope.searchSettings.distinct) {
                                var checkDuplicates = [];
                                var toRemove = [];
                                results.forEach(function (item) {
                                    var key = item[Object.keys(item)[0]];
                                    if (checkDuplicates.indexOf(key) > -1) {
                                        toRemove.push(item);
                                    } else {
                                        checkDuplicates.push(key);
                                    }
                                });
                                toRemove.forEach(function (item) {
                                    results.splice(results.indexOf(item), 1);
                                });
                            }

                            if (results.data) {
                                results = results.data;
                            }

                            if (scope.searchSettings.valueField || scope.searchSettings.resultField) {
                                return results.map(function (result) {
                                    if (result.label || result.display_name) {
                                        value = result.value || result.id;
                                        label = result.label || result.display_name;
                                    }
                                    else {
                                        value = result[scope.searchSettings.valueField ||
                                        scope.searchSettings.resultField];
                                        label = scope.searchSettings.labelField ?
                                            result[scope.searchSettings.labelField] : value;
                                    }
                                    return {label: label, value: value};
                                });
                            }

                            return results;
                        }

                        function init() {
                            if (scope.searchSettings.onSelect && !attrs.onSelect) {
                                scope.onSelect = scope.searchSettings.onSelect;
                            }

                            isInit = true;

                            input.on("keyup", function (event) {
                                if (event.keyCode === $.ui.keyCode.TAB &&
                                    $(this).data("ui-autocomplete").menu.active) {
                                    event.preventDefault();
                                }
                                else if (event.keyCode === $.ui.keyCode.ESCAPE) {
                                    $(this)
                                        .val("")
                                        .autocomplete("close");
                                }
                                else if (event.keyCode === $.ui.keyCode.ENTER &&
                                    scope.searchSettings.allowAllStrings) {
                                    scope.onSelect(this.value);
                                } else if (!input.val() || input.val() === "") {
                                    onSelect({$value: "", $label: "", $item: $.ui.item});
                                    if (ngModel) {
                                        ngModel.$setViewValue("");
                                    }
                                } else {
                                    if (scope.charEntered && scope.display !== input.val()) {
                                        scope.charEntered();
                                    }
                                }

                            })
                                .attr("placeholder", scope.searchSettings.placeholder)
                                .data("autocomplete-enabled", true)
                                .autocomplete({
                                    delay: 400,
                                    autoFocus: true,
                                    minLength: MIN_SEARCH_SIZE,
                                    source: function (request, response) {
                                        if (!request.term || request.term.length < MIN_SEARCH_SIZE) {
                                            return;
                                        }

                                        //var searchMethod = scope.searchSettings.search.constructor === Function ?
                                        //    scope.searchSettings.search : search.searchDataEntityField.bind(search,
                                        //    scope.searchSettings.dataEntity, scope.searchSettings.dataEntityField,
                                        //    scope.searchSettings.labelField, scope.searchSettings.extraTerms);

                                        scope.loading = true;

                                        var searchTerm = extractLast(request.term);
                                        if (scope.searchSettings.termTransform) {
                                            searchTerm = transforms.string(searchTerm,
                                                scope.searchSettings.termTransform);
                                        }
                                        $rootScope.safeApply(function () {
                                            if (scope.searchSettings.search) {
                                                scope.searchSettings.search(searchTerm).then(function (results) {
                                                    results = results ? parseResults(results) : [];
                                                    scope.loading = false;
                                                    response(results);
                                                }, function (error) {
                                                    scope.loading = false;
                                                    response([]);
                                                });
                                            }
                                        });
                                    },
                                    search: function () {
                                        // custom minLength
                                        var term = extractLast(this.value);
                                        if (term.length < 1) {
                                            return false;
                                        }
                                    },
                                    focus: function () {
                                        // prevent value inserted on focus
                                        return false;
                                    },
                                    select: function (event, ui) {
                                        if (scope.searchSettings.showValueOnSelect) {
                                            input.val(ui.item.label || ui.item.value);
                                        } else {
                                            input.val("");
                                        }

                                        if (scope.onSelect) {
                                            $timeout.cancel(onSelectTimeout);
                                            onSelectTimeout = $timeout(function () {
                                                onSelect({
                                                    $value: ui.item.value,
                                                    $label: ui.item.label,
                                                    $item: ui.item
                                                });
                                                scope.display = ui.item.label;
                                                if (ngModel) {
                                                    ngModel.$setViewValue(ui.item.value);
                                                }
                                            }, 40);
                                        }

                                        return false;
                                    }
                                });

                            scope.$on("$destroy", function (e, data) {
                                var $input = element.find("input");

                                $timeout.cancel(onSelectTimeout);
                                if ($input.data("autocomplete-enabled")) {
                                    $input.autocomplete("destroy").off();
                                }

                                element.empty();
                                element.off();
                            });
                        }
                    }
                };
            }]);
}());
