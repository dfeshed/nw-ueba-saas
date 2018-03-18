(function () {
    'use strict';

    function navigation (dynamicMenus, conditions, state) {
        return {
            template: "<div></div>",
            restrict: 'E',
            require: 'ngModel',
            scope: {
                onSelect: "&",
                navData: "="
            },
            link: function (scope, element, attrs, ngModel) {
                var selectedItem,
                    selectedElement,
                    navData;

                scope.$watch("navData", function (value) {
                    if (!!(navData = value)) {
                        setNav();
                    }
                });

                ngModel.$render = function () {
                    if (!navData || !ngModel.$viewValue) {
                        return;
                    }

                    if (selectedItem) {
                        selectedItem.selected = false;
                        selectedElement = element[0].querySelector(".nav-link.selected");
                        if (selectedElement) {
                            selectedElement.parentNode.classList.remove("selected");
                        }
                    }

                    selectedItem = getItem(navData, ngModel.$viewValue);
                    if (selectedItem) {
                        selectedItem.selected = true;
                        selectedElement = getElement(selectedItem);
                        selectedElement.parentNode.classList.add("selected");
                    }
                };

                function getItem (root, item) {
                    if ((root.id && root.id === item.id) || (root.url && root.url === item.url)) {
                        return root;
                    }

                    if (root.children) {
                        var found;
                        for (var i = 0, child; !!(child = root.children[i]); i++) {
                            found = getItem(child, item);
                            if (found) {
                                return found;
                            }
                        }
                    }

                    return null;
                }

                function getElement (item) {
                    var links = element[0].querySelectorAll(".nav-link");
                    for (var i = 0, link; !!(link = links[i]); i++) {
                        if (link.__data__ === item) {
                            return link;
                        }
                    }
                }

                function selectFirstItem () {
                    var firstElement = element.find(".nav-link").first();
                    if (firstElement.length) {
                        firstElement.parent().addClass("selected");
                        firstElement.parents(".nav").removeClass("closed").prev().removeClass("closed");
                        selectedElement = firstElement[0];
                        if (scope.onSelect) {
                            scope.onSelect({$item: selectedElement.__data__});
                        }
                    }
                }

                function addChildren (rootElement, navItem, isChild) {
                    var root = d3.select(rootElement);

                    if (navItem.name) {
                        var expand = root.append("a")
                            .attr("id", "menu-parent-item-" + navItem.name.toLowerCase().split(" ").join("_"))
                            .attr("class", "nav-expand" + (navItem.isOpen ? "" : " closed"));
                        expand.append("i").attr("class", "icon-");
                        expand.append("span").text(navItem.name);
                    }

                    var list = root.append("ul")
                        .attr("class", "unstyled nav " + (!isChild || navItem.isOpen ? "open" : "closed"));

                    if (navItem.children && navItem.children.length) {
                        rootElement.classList.add("with-children");
                    }

                    list.selectAll("li").data(navItem.children.filter(checkConditions))
                        .enter()
                        .append(function (d) {
                            var li = document.createElement("li");
                            if (d.children) {
                                addChildren(li, d, true);
                            } else {
                                d3.select(li).append("a")
                                    .attr("id", "nav-item-" + d.name.toLowerCase().split(" ").join("_"))
                                    .attr("class", "nav-link" + (d.selected ? " selected" : ""))
                                    .attr("href", d.href || d.url || null)
                                    .attr("data-regexp", d.selectedRegExp)
                                    .text(d.name)
                                    .data([d]);

                                // This will change the href of the nav's a tags to reflect the changes to the state.
                                if (d.href) {
                                    // Check if current hash is a match to d.href
                                    var hrefRgx = new RegExp(d.href);
                                    if (hrefRgx.test(window.location.href)) {

                                        // When a match, add a mouseover listener that updates href
                                        var aTag = li.querySelector('a');
                                        aTag.addEventListener('mouseover', function () {
                                            if (aTag.hash !== window.location.hash) {
                                                aTag.hash = window.location.hash;
                                            }
                                        });
                                    }
                                }
                            }
                            return li;
                        });

                    return root[0][0];
                }

                function checkConditions (navItem) {
                    return !(navItem.conditions &&
                    !conditions.validateConditions(navItem.conditions, null, state.currentParams));
                }

                function setNav () {
                    element.empty();
                    dynamicMenus.setDynamicMenus(navData.children);
                    var newElement = angular.element(addChildren(document.createElement("div"), navData)),
                        locationUrl = window.location.hash.split("?")[0];

                    element.append(newElement);

                    selectedElement = element.find(".nav-link.selected");
                    if (!selectedElement.length) {
                        selectedElement = element.find(".nav-link[href='" + locationUrl + "']");
                    }
                    if (!selectedElement.length) {
                        element.find(".nav-link[data-regexp]").each(function (i, link) {
                            var regexp = new RegExp(link.getAttribute("data-regexp"));
                            if (regexp && regexp.test(locationUrl)) {
                                selectedElement = jQuery(link);
                                return false;
                            }
                        });
                    }
                    if (selectedElement.length) {
                        selectedElement.parent().addClass("selected");
                        selectedElement.parents(".nav").removeClass("closed").prev().removeClass("closed");
                        selectedElement = selectedElement[0];
                    }
                    else {
                        selectFirstItem();
                    }
                }

                function init () {
                    if (scope.onSelect) {
                        element.on("click", ".nav-link", function (e) {
                            var navItem = e.target.__data__;

                            if (selectedElement) {
                                selectedElement.parentNode.classList.remove("selected");
                            }

                            selectedElement = e.target;
                            selectedElement.parentNode.classList.add("selected");

                            scope.$apply(function () {
                                scope.onSelect({$item: navItem});
                            });
                        });
                    }

                    element.on("click", ".nav-expand", function (e) {
                        $(e.currentTarget).toggleClass("closed").next().toggleClass("closed");
                    });

                    scope.$on("$destroy", function () {
                        element.empty();
                        element.off();
                    });
                }

                init();
            }
        };
    }

    navigation.$inject = ["dynamicMenus", "conditions", "state"];

    angular.module("Fortscale")
        .directive('nav', navigation);
}());
