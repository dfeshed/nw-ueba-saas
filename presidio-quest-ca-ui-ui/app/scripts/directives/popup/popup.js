(function () {
    'use strict';

    angular.module("Popup", []).directive("popup", [function () {
        return {
            templateUrl: 'scripts/directives/popup/popup.template.html',
            restrict: 'E',
            scope: true,
            replace: true,
            link: function postLink (scope, element, attrs) {
                var lastSrc;

                scope.$watch(attrs.popupConfig, function (popup) {
                    if (!popup) {
                        return;
                    }

                    scope.params = popup.params || {};
                    scope.width = popup.width || 500;
                    scope.height = popup.height || 400;
                    scope.popupTitle = popup.title;
                    scope.position = popup.position;
                    scope.show = popup.show;

                    if (popup.src && lastSrc !== popup.src) {
                        scope.popupSrc = popup.src;
                        lastSrc = popup.src;
                    }

                    if (popup.scope) {
                        for (var scopeParam in popup.scope) {
                            if (popup.scope.hasOwnProperty(scopeParam)) {
                                scope[scopeParam] = popup.scope[scopeParam];
                            }
                        }
                    }
                    setStyle();

                });

                scope.close = function () {
                    if (scope.popup) {
                        scope.show = scope.popup.show = false;
                    }
                };

                element.on("click", "a[href]", function () {
                    scope.close();
                    return true;
                });

                scope.$on("$destroy", function () {
                    element.empty();
                    element.off();
                });

                scope.$on("closePopups", scope.close);

                function setStyle () {
                    if (scope.position) {
                        var top = scope.position.top,
                            left = scope.position.left;

                        if (top + scope.height + 30 > document.body.clientHeight) {
                            top = scope.position.top - scope.height;

                            if (top < document.body.scrollTop) {
                                top = document.body.clientHeight - scope.height - 30;
                            }
                        }

                        if (left + scope.width + 30 > document.body.clientWidth) {
                            left = scope.position.left - scope.width;

                            if (left < document.body.scrollLeft) {
                                left = document.body.clientWidth - scope.width - 30;
                            }
                        }

                        scope.popupStyle = {
                            width: scope.width,
                            height: scope.height,
                            left: left,
                            top: top
                        };
                    }
                }
            }
        };
    }]);
}());
