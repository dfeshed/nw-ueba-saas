(function () {
    'use strict';

    angular.module("Fortscale")
        .directive('tabs', function () {
            return {
                template: "<div class='tabs'></div>",
                restrict: 'E',
                replace: true,
                scope: {
                    onSelect: "&",
                    _tabs: "=tabsData"
                },
                require: '?ngModel',
                link: function (scope, element, attrs, ngModel) {
                    var dataWatcher,
                        selectedElement,
                        selectedTab;

                    ngModel.$render = function () {
                        selectedTab = ngModel.$viewValue;
                        selectTabElement(selectedTab);
                    };

                    function selectTabElement (tab) {
                        if (!scope._tabs) {
                            return;
                        }

                        if (selectedElement) {
                            selectedElement.classList.remove("selected");
                            selectedElement = null;
                        }

                        for (var i = 0; i < scope._tabs.length; i++) {
                            if (scope._tabs[i] === tab) {
                                selectedElement = element[0].childNodes[i];
                                break;
                            }
                        }

                        if (selectedElement) {
                            selectedElement.classList.add("selected");
                        }
                    }

                    element.on("click", ".tab", function (e) {
                        e.stopPropagation();
                        selectTab(scope._tabs[e.currentTarget.tabIndex]);
                    });

                    function selectTab (tab) {
                        selectTabElement(tab);
                        scope.selectedTab = tab;
                        if (scope.onSelect) {
                            scope.onSelect({tab: tab});
                        }
                    }

                    dataWatcher = scope.$watch("_tabs", function (tabsData) {
                        if (!tabsData) {
                            return;
                        }

                        tabsData.forEach(function (tabData, i) {
                            var tabElement = document.createElement("a");
                            tabElement.className = "tab";
                            if (selectedTab === tabData) {
                                tabElement.classList.add("selected");
                                selectedElement = tabElement;
                            }
                            tabElement.innerText = tabData.name;
                            element.append(tabElement);
                            tabElement.tabIndex = i;
                        });

                        dataWatcher();
                    });
                }
            };
        });
}());
