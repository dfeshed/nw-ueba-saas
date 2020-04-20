(function () {
    'use strict';

    angular.module("Fortscale").directive("dropdownMenu", ["$timeout", function ($timeout) {
        return {
            restrict: 'E',
            template: '<menu class="dropdown dropdown-directive"></menu>',
            replace: true,
            scope: {
                items: "=",
                params: "=",
                onSelect: "&"
            },
            link: function postLink (scope, element) {

                function selectMenuItem ($event, item) {
                    if (!scope.onSelect) {
                        return true;
                    }

                    close();
                    scope.onSelect({$event: $event, $item: item});
                }

                function renderMenu () {
                    if (!dropdownMenu) {
                        dropdownMenu = document.createElement("ul");
                        dropdownMenu.className = "dropdown-menu";
                        element.append(dropdownMenu);
                    }

                    if (!itemsRendered) {
                        scope.items.forEach(function (item, i) {
                            var listItem = document.createElement("li"),
                                link = document.createElement("a");

                            if (item.href) {
                                link.setAttribute("href", item.href);
                            } else {
                                link.setAttribute("data-select", i);
                            }

                            link.innerText = item.text || item.name;

                            listItem.appendChild(link);
                            dropdownMenu.appendChild(listItem);
                        });

                        itemsRendered = true;
                    }
                }

                function destroyMenu () {
                    dropdownMenu.parentElement.removeChild(dropdownMenu);
                    dropdownMenu = null;
                    itemsRendered = false;
                }


                function open (event) {
                    renderMenu();

                    dropdownMenu.style.opacity = "0";
                    buttonBoundingRect = event.currentTarget.getBoundingClientRect();
                    dropdownMenu.style.top = (buttonBoundingRect.bottom - 3) + "px";
                    dropdownMenu.style.left = buttonBoundingRect.left + "px";
                    dropdownMenu.classList.remove("rightDropdown");
                    dropdownMenu.classList.remove("topDropdown");
                    isOpen = true;
                    element.addClass("open");
                    element.removeClass("dropdown-top");

                    $timeout(function () {
                        setPosition();
                        dropdownMenu.style.opacity = "1";

                        document.body.addEventListener("mousedown", onBackgroundClick);
                        window.addEventListener("scroll", onBackgroundClick);
                    }, 50);
                }

                function setPosition () {
                    var width = document.documentElement.clientWidth,
                        height = document.documentElement.clientHeight,
                        dropdownMenuBoundingRect = dropdownMenu.getBoundingClientRect(),
                        dropdownRight = dropdownMenuBoundingRect.right,
                        dropdownBottom = dropdownMenuBoundingRect.bottom,
                        marginRight = 20,
                        marginBottom = 60;

                    if (dropdownRight > width - marginRight) {
                        dropdownMenu.style.removeProperty("left");
                        dropdownMenu.style.left = (buttonBoundingRect.right - dropdownMenuBoundingRect.width) + "px";
                        dropdownMenu.classList.add("rightDropdown");
                    }
                    if (dropdownBottom > height - marginBottom) {
                        dropdownMenu.style.removeProperty("top");
                        dropdownMenu.style.top = (buttonBoundingRect.top - dropdownMenuBoundingRect.height) + "px";
                        dropdownMenu.classList.add("topDropdown");
                        element.addClass("dropdown-top");
                        scope.isTopMenu = true;
                    }
                    else {
                        element.removeClass("dropdown-top");
                    }
                }

                function close () {
                    document.body.removeEventListener("mousedown", onBackgroundClick);
                    window.removeEventListener("scroll", onBackgroundClick);
                    isOpen = false;
                    element.removeClass("open");
                    element.removeClass("dropdown-top");
                    setTimeout(destroyMenu, 300);
                }

                function onBackgroundClick (e) {
                    if (!$(e.target).closest(".dropdown").length ||
                        $(e.target).closest(".dropdown")[0] !== element[0]) {
                        scope.$apply(close);
                    }

                    return true;
                }

                var dropdownMenu,
                    buttonBoundingRect,
                    isOpen = false,
                    toggleButton = $('<a class="clickable dropdown-toggle hidden-phone"><b class="caret"></a>'),
                    itemsRendered;

                element.removeClass("open");

                scope.$on("$destroy", function () {
                    element.off();
                    element.empty();
                });

                function toggle (event) {
                    if (isOpen) {
                        close();
                    } else {
                        open(event);
                    }
                }

                element.append(toggleButton);
                toggleButton.on("click", toggle);


                scope.$watch("items", function (items) {
                    if (!items) {
                        return;
                    }

                    if (itemsRendered) {
                        destroyMenu();
                    }

                    renderMenu();
                });

                element.on("click", "[data-select]", function (e) {
                    var item = scope.items[parseInt(this.getAttribute("data-select"), 10)];
                    selectMenuItem(e, item);
                });

            }
        };
    }]);
}());
