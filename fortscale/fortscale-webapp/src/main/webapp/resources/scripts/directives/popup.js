angular.module("Popup", []).directive("popup", ["$parse", function($parse){
    return {
        template: '<div class="popup-overlay" ng-show="popupShow" ng-click="close($event)"><div class="popup" ng-style="popupStyle"><h2 class="popup-title"><a class="popup-close-btn" ng-click="close()"><i class="icon-remove icon-small"></i></a>{{popupTitle}}</h2><div class="popup-contents" ng-include="popupSrc"></div></div></div>',
        restrict: 'E',
        scope: true,
        link: function postLink(scope, element, attrs) {
            var width, height, position, show;
            var lastSrc;

            scope.$watch(attrs.position, function(position){
                scope.position = position;
                setStyle();
            });

            scope.$watch(attrs.width, function(width){
                scope.width = width;
                setStyle();
            });

            scope.$watch(attrs.height, function(height){
                scope.height = height;
                setStyle();
            });

            scope.$watch(attrs.popupShow, function(show){
                scope.popupShow = show;
            });

            scope.$watch(attrs.popupSrc, function(src){
                if (src && lastSrc !== src){
                    scope.popupSrc = src;
                    lastSrc = src;
                }
            });

            scope.$watch(attrs.popupTitle, function(title){
                scope.popupTitle = title;
            });

            scope.$watch(attrs.popupScope, function(popupScope){
                for(var scopeParam in popupScope){
                    scope[scopeParam] = popupScope[scopeParam];
                }
            });

            var closeFn = $parse(attrs.closePopup);
            scope.close = function(e){
                if (!e || e.target.className === "popup-overlay"){
                    scope.popupShow = false;
                    closeFn(scope);
                }
            };

            element.on("click", "a[href]", function(e){
                scope.close();
                return true;
            });

            function setStyle(){
                if (scope.position){
                    var top = scope.position.top;
                    if (top + scope.height+ 30 > document.body.clientHeight){
                        top = scope.position.top - scope.height;

                        if (top < document.body.scrollTop)
                            top = document.body.clientHeight - scope.height -30;
                    }

                    scope.popupStyle = {
                        width: scope.width,
                        height: scope.height,
                        left: scope.position.left,
                        top: top
                    }
                }
            }
        }
    }
}]);