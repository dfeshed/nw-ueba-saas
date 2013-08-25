if(typeof(yoxigen) === "undefined")
    yoxigen = angular.module("Yoxigen", []);

yoxigen.directive("pagination", ["$parse", "$timeout", function($parse, $timeout){
    return {
        templateUrl: "views/elements/pagination.html",
        restrict: 'E',
        scope: true,
        replace: true,
        require: "?ngModel",
        link: function postLink(scope, element, attrs, ngModel) {
            var pagesToShow,
                itemsPerPage,
                currentPagingData,
                ngChangeFunc = attrs.ngChange ? $parse(attrs.ngChange) : null,
                onChangeTimeout,
                wait,
                waitTimeout,
                graceTime = 300,
                defaults = {
                    pagesToShow: 10,
                    itemsPerPage: 10
                };

            scope.gotoPage = function(page){
                if (page === scope.currentPage)
                    return;

                scope.currentPage = page;
                setPages();
                currentPagingData.currentPage = page;

                if (!wait){
                    setWaitTime();

                    if (ngChangeFunc){
                        ngChangeFunc(scope, currentPagingData);
                    }
                }
                else
                    fireOnChange();
            };

            scope.$watch(attrs.ngModel, function(pagesData){
                if (!pagesData){
                    scope.pages = null;
                    return;
                }

                currentPagingData =pagesData;
                scope.currentPage = pagesData.currentPage || 1;
                pagesToShow = pagesData.pagesToShow || defaults.pagesToShow;
                itemsPerPage = pagesData.itemsPerPage || defaults.itemsPerPage;
                scope.totalPages = Math.ceil(pagesData.totalCount / itemsPerPage);

                setPages();
            });

            function setPages(){
                scope.pages = [];
                var firstPage = Math.max(1, Math.floor(scope.currentPage - pagesToShow / 2)),
                    lastPage = Math.min(firstPage + pagesToShow - 1, scope.totalPages);

                for (var i=firstPage; i <= lastPage; i++)
                    scope.pages.push(i);

                var displayingAllPages = scope.totalPages === scope.pages.length;
                scope.showPrev = !displayingAllPages && scope.currentPage !== 1;
                scope.showNext = !displayingAllPages && scope.currentPage !== scope.totalPages;

                scope.showFirst = firstPage > 2;
                scope.showLast = lastPage < scope.totalPages - 1;
            }

            function setWaitTime(){
                $timeout.cancel(waitTimeout);
                wait = true;
                waitTimeout = $timeout(function(){ wait = false; }, graceTime);
            }

            function fireOnChange(){
                $timeout.cancel(onChangeTimeout);
                setWaitTime();
                onChangeTimeout = $timeout(function(){
                    ngChangeFunc(scope, currentPagingData);
                }, graceTime);
            }
        }
    };
}]);
