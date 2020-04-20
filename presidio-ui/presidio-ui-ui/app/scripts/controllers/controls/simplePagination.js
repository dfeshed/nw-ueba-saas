(function () {
    'use strict';

    angular.module('simplePagination', []).directive('simplePagination',
        ["$parse", "$timeout", "utils", function ($parse, $timeout, utils) {

            return {
                templateUrl: "views/controls/simplePagination.html",
                restrict: 'E',
                scope: {
                    page: "=",
                    pageSize: "=",
                    onSelect: "&",
                    hideIfEmpty: "=",
                    total: "="
                },
                replace: true,
                link: function postLink (scope, element) {

                    function setPages () {
                        if (!isNaN(scope.total) && !isNaN(scope.pageSize)) {
                            scope.pageCount = Math.ceil(scope.total / scope.pageSize);
                        }
                        if (isNaN(scope.page)) {
                            scope.page = 1;
                        }
                        scope.pageInput = scope.page;
                        if (scope.pageCount) {
                            scope.pageInputWidth = (scope.pageCount.toString().length + 1) + "em";
                        }
                    }

                    function setWaitTime () {
                        $timeout.cancel(waitTimeout);
                        wait = true;
                        waitTimeout = $timeout(function () {
                            wait = false;
                        }, graceTime);
                    }

                    function fireOnChange () {
                        $timeout.cancel(onChangeTimeout);
                        setWaitTime();
                        if (scope.onSelect) {
                            onChangeTimeout = $timeout(function () {
                                scope.onSelect({page: scope.page});
                            }, graceTime);
                        }
                    }

                    var onChangeTimeout,
                        wait,
                        waitTimeout,
                        graceTime = 300;

                    scope.$on("$destroy", function () {
                        $timeout.cancel(waitTimeout);
                        $timeout.cancel(onChangeTimeout);

                        element.empty();
                        element.off();
                    });
                    //listen to url changes and update the pagination control accordingly
                    scope.$on("locationChange", function (event, args) {
                        var params = utils.url.parseUrlParams(args.newUrl);
                        if (params.page) {
                            scope.gotoPage(Number(params.page));
                        }
                    });

                    scope.gotoPage = function (page) {
                        if (page === scope.page) {
                            return;
                        }

                        if (page > scope.pageCount) {
                            scope.page = scope.pageCount;
                        }

                        else if (page < 1) {
                            scope.page = 1;
                        }

                        else {
                            scope.page = page;
                        }

                        setPages();

                        if (!wait) {
                            setWaitTime();

                            if (scope.onSelect) {
                                scope.onSelect({page: scope.page});
                            }
                        }
                        else {
                            fireOnChange();
                        }
                    };

                    //enables the text box to insert the requested page number
                    scope.enablePageInput = function () {
                        scope.pageInputEnabled = true;
                    };

                    scope.prevPage = function () {
                        scope.gotoPage(scope.page - 1);
                    };

                    scope.nextPage = function () {
                        scope.gotoPage(scope.page + 1);
                    };

                    scope.onInputKeydown = function (e) {
                        if (e.keyCode === 27) {
                            scope.pageInput = scope.page;
                            scope.pageInputEnabled = false;
                        }

                    };

                    scope.$watchGroup(["page", "pageSize", "total"], setPages);

                }
            };
        }]);
}());
