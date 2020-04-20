if(typeof(
    yoxigen) === "undefined")
    yoxigen = angular.module("Yoxigen", []);

yoxigen.directive("fsPagination", ["$parse", "$timeout", function($parse, $timeout){
    return {
        templateUrl: "views/elements/pagination.html",
        restrict: 'E',
        scope: {
        	page: "=",
        	pageSize: "=",
        	onSelect: "&",
        	hideIfEmpty: "=",
        	total: "="
        },
        replace: true,
        link: function postLink(scope, element, attrs) {
            var itemsPerPage,
                onChangeTimeout,
                wait,
                waitTimeout,
                graceTime = 300;

            scope.$on("$destroy", function(e, data){
                $timeout.cancel(waitTimeout);
                $timeout.cancel(onChangeTimeout);

                element.empty();
                element.off();
            });

            scope.gotoPage = function(page){
                if (page === scope.page || page > scope.total || page < 1)
                    return;

                scope.page = page;
                setPages();

                if (!wait){
                    setWaitTime();

                     if (scope.onSelect){
						scope.onSelect({ page: scope.page });
					}
                }
                else
                    fireOnChange();
            };

			scope.$watchGroup(["page", "pageSize", "total"], setPages);

            function setPages(){
                scope.pages = [];

                var firstPage = Math.max(1, Math.floor(scope.page - scope.pageSize / 2)),
                    lastPage = Math.min(firstPage + scope.pageSize - 1, scope.total),
                    pagesCount = lastPage - firstPage;

                if (scope.total > pagesCount){
                    for(var i= pagesCount, before = true; i < scope.pageSize - 1; i++){
                        if (before && firstPage > 1 || lastPage === scope.total && firstPage > 1){
                            firstPage --;
                        }
                        else if (lastPage < scope.total)
                            lastPage++;

                        before = !before;
                    }
                }

                for (i=firstPage; i <= lastPage; i++)
                    scope.pages.push(i);

                var displayingAllPages = scope.total === scope.pages.length;
                scope.showPrev = !displayingAllPages && scope.page !== 1;
                scope.showNext = !displayingAllPages && scope.page !== scope.total;

                scope.showFirst = firstPage > 2;
                scope.showLast = lastPage < scope.total - 1;
            }

            function setWaitTime(){
                $timeout.cancel(waitTimeout);
                wait = true;
                waitTimeout = $timeout(function(){ wait = false; }, graceTime);
            }

            function fireOnChange(){
                $timeout.cancel(onChangeTimeout);
                setWaitTime();
                if (scope.onSelect){
                	onChangeTimeout = $timeout(function(){
						scope.onSelect({ page: scope.page });
					}, graceTime);
				}
            }
        }
    };
}]);
