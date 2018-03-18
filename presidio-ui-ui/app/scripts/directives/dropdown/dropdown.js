(function () {
    'use strict';

    angular.module("Dropdown", []).directive("dropdown", ["$timeout", function ($timeout) {
        function dropdownLink (scope, element) {

            function open () {
                dropdownEl.classList.add("dropdown-open");
                setTimeout(setMenuPositionAndShow);
                document.body.addEventListener("mousedown", onMouseDown);
            }

            function close (e) {
                if (e && (e.target.classList.contains("dropdown-list-toggle-label") ||
                    e.target.classList.contains("dropdown-directive-menu"))) {
                    return true;
                }

                dropdownEl.classList.remove("dropdown-visible");
                $timeout(function () {
                    dropdownEl.classList.remove("dropdown-open");
                    menuEl.style.removeProperty("height");
                    menuEl.style.removeProperty("width");
                });
                document.body.removeEventListener("mousedown", onMouseDown);
            }

            function toggle () {
                if (scope.isOpen) {
                    close();
                } else {
                    open();
                }
            }

            function onMouseDown (e) {
                var el = e.target;
                if (el.classList.contains("dropdown-list-toggle-label")) {
                    return true;
                }

                do {
                    if (el === document.documentElement) {
                        return close();
                    }

                    if (el === menuEl) {
                        return true;
                    }
                }
                while (!!(el = el.parentNode));
            }

            function setMenuPositionAndShow () {
                var buttonRect = buttonEl.getBoundingClientRect();

                menuEl.style.top = buttonRect.top + "px";
                menuEl.style.left = buttonRect.left + "px";

                var documentWidth = document.documentElement.clientWidth,
                    documentHeight = document.documentElement.clientHeight,
                    menuClientRect = menuEl.getBoundingClientRect(),
                    maxWidth = documentWidth - MARGIN * 2,
                    maxHeight = documentHeight - MARGIN * 2;

                var recalculateRect,
                    dontSetLeft, dontSetTop;

                if (menuClientRect.width > maxWidth) {
                    menuEl.style.width = maxWidth + "px";
                    menuEl.style.left = MARGIN + "px";
                    recalculateRect = true;
                    dontSetLeft = true;
                }

                if (menuClientRect.height > maxHeight) {
                    menuEl.style.height = maxHeight + "px";
                    menuEl.style.top = MARGIN + "px";
                    recalculateRect = true;
                    dontSetTop = true;
                }

                if (!dontSetLeft || !dontSetTop) {
                    if (recalculateRect) {
                        menuClientRect = menuEl.getBoundingClientRect();
                    }

                    if (!dontSetLeft) {
                        var farthestPosition = documentWidth - MARGIN;
                        if (menuClientRect.right > farthestPosition && menuClientRect.width < maxWidth) {
                            var leftDelta = menuClientRect.right - farthestPosition;
                            menuEl.style.left = (buttonRect.left - leftDelta) + "px";
                        }
                    }

                    if (!dontSetTop) {
                        var lowestPosition = documentHeight - MARGIN;
                        if (menuClientRect.bottom > lowestPosition && menuClientRect.height < maxHeight) {
                            var bottomDelta = menuClientRect.bottom - lowestPosition;
                            menuEl.style.top = (buttonRect.top - bottomDelta) + "px";
                        }
                    }
                }

                dropdownEl.classList.add("dropdown-visible");
            }

            var dropdownEl = element[0],
                menuEl = dropdownEl.querySelector(".dropdown-directive-menu"),
                buttonEl = dropdownEl.querySelector("button");

            var MARGIN = 20;

            scope.toggle = toggle;
            menuEl.addEventListener("mouseup", close);

        }

        return {
            replace: true,
            restrict: "E",
            transclude: true,
            scope: {
                buttonText: "@",
                buttonClass: "@",
                closeOnClick: "=",
                disabled: "="
            },
            template: '<div class="dropdown-directive"><button ng-click="toggle()" class="{{buttonClass}}" ' +
            'ng-disabled="disabled">{{buttonText}}</button>' +
            '<div class="dropdown-directive-menu" ng-transclude></div></div>',
            link: dropdownLink
        };

    }]);
})();
