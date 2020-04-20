(function () {
    'use strict';

    angular.module("Fortscale")
        .directive("dropdownMenuDelegate", ["$timeout", "conditions", "state", function ($timeout, conditions, state) {
            return {
                restrict: 'A',
                scope: false,
                link: function postLink(scope, element, attrs) {
                    var dropdownMenu,
                        dropdownMenuList,
                        currentItems,
                        itemsRendered,
                        isOpen,
                        isTopMenu;


                    function init() {
                        element.on("click", ".dropdown-delegate-toggle", function (e) {
                            if (dropdownMenu && this.parentNode !== dropdownMenu) {
                                closeMenu();
                            }

                            dropdownMenu = this.parentNode;

                            if (!dropdownMenu.menu) {
                                return true;
                            }

                            currentItems = dropdownMenu.menu.items;
                            toggle(e);
                        });

                        element.on("click", ".dropdown-directive .dropdown-menu-link", function (e) {
                            selectMenuItem(e, this.menuItem);
                        });
                    }

                    function renderMenu() {
                        if (!dropdownMenuList) {
                            dropdownMenuList = document.createElement("ul");
                            dropdownMenuList.className = "dropdown-menu";
                            dropdownMenu.appendChild(dropdownMenuList);
                        }

                        if (!itemsRendered) {
                            currentItems.forEach(function (item, i) {
                                var listItem = document.createElement("li"),
                                    link = document.createElement("a");

                                link.className = "dropdown-menu-link";
                                link.menuItem = item;
                                if (item.href) {
                                    link.setAttribute("href", item.href);
                                } else {
                                    link.setAttribute("data-select", i);
                                }

                                link.innerText = item.text || item.name;

                                listItem.appendChild(link);
                                dropdownMenuList.appendChild(listItem);
                            });

                            itemsRendered = true;
                        }
                    }

                    function destroyMenu() {
                        dropdownMenu.removeChild(dropdownMenuList);
                        dropdownMenuList = null;
                        itemsRendered = false;
                        dropdownMenu = null;
                    }

                    function openMenu(event) {
                        renderMenu();
                        dropdownMenuList.style.opacity = "0";
                        var buttonBoundingRect = event.currentTarget.getBoundingClientRect();
                        dropdownMenuList.style.top = (buttonBoundingRect.bottom - 3) + "px";
                        dropdownMenuList.style.left = buttonBoundingRect.left + "px";
                        dropdownMenuList.classList.remove("rightDropdown");
                        dropdownMenuList.classList.remove("topDropdown");
                        isOpen = true;
                        dropdownMenu.classList.add("open");
                        dropdownMenu.classList.remove("dropdown-top");
                        document.body.addEventListener("mousedown", onBackgroundClick);
                        window.addEventListener("scroll", onBackgroundClick);

                        $timeout(function () {
                            setPosition(buttonBoundingRect);
                            dropdownMenuList.style.opacity = "1";
                        }, 1);
                    }

                    function setPosition(buttonBoundingRect) {
                        var width = document.documentElement.clientWidth,
                            height = document.documentElement.clientHeight,
                            dropdownMenuBoundingRect = dropdownMenuList.getBoundingClientRect(),
                            dropdownRight = dropdownMenuBoundingRect.right,
                            dropdownBottom = dropdownMenuBoundingRect.bottom,
                            marginRight = 20,
                            marginBottom = 60;

                        if (dropdownRight > width - marginRight) {
                            dropdownMenuList.style.removeProperty("left");
                            dropdownMenuList.style.left = (buttonBoundingRect.right - dropdownMenuBoundingRect.width) +
                                "px";
                            dropdownMenuList.classList.add("rightDropdown");
                        }
                        if (dropdownBottom > height - marginBottom) {
                            dropdownMenuList.style.removeProperty("top");
                            dropdownMenuList.style.top = (buttonBoundingRect.top - dropdownMenuBoundingRect.height) +
                                "px";
                            dropdownMenuList.classList.add("topDropdown");
                            dropdownMenu.classList.add("dropdown-top");
                            isTopMenu = true;
                        }
                        else {
                            dropdownMenu.classList.remove("dropdown-top");
                        }
                    }

                    function closeMenu(immediate) {
                        document.body.removeEventListener("mousedown", onBackgroundClick);
                        window.removeEventListener("scroll", onBackgroundClick);
                        isOpen = false;
                        if (dropdownMenu) {
                            dropdownMenu.classList.remove("open");
                            dropdownMenu.classList.remove("dropdown-top");
                            if (immediate) {
                                destroyMenu();
                            } else {
                                setTimeout(destroyMenu, 300);
                            }
                        }
                    }


                    function toggle(event) {
                        if (isOpen) {
                            closeMenu();

                        } else {
                            openMenu(event);
                        }
                    }

                    function selectMenuItem($event, item) {
                        if (!scope.menuSelect) {
                            return true;
                        }

                        closeMenu();
                        scope.menuSelect($event, item, dropdownMenu.data, dropdownMenu.menu, scope.mainDashboardParams);
                    }

                    function onBackgroundClick(e) {
                        if (!$(e.target).closest(".dropdown").length || $(e.target).closest(".dropdown")[0] !==
                            dropdownMenu) {
                            scope.$apply(closeMenu);
                        }

                        return true;
                    }

                    init();

                }
            };
        }]);
}());
