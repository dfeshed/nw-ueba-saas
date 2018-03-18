(function () {
    'use strict';

    function menusFactory ($q, utils, version, conditions, Menu) {
        var menus = {},
            onMenuLoad = {};

        function getMenu (menu, settings, data, params) {
            var menuItems = [],
                itemsData = angular.copy(data),
                menuInstance = angular.copy(menu);

            if (settings.params) {
                var param;
                for (var paramName in settings.params) {
                    if (settings.params.hasOwnProperty(paramName)) {
                        param = settings.params[paramName];
                        if (typeof(param) === "string") {
                            itemsData[paramName] = utils.strings.parseValue(settings.params[paramName], data, params);
                        } else if (angular.isObject(param) && param.dashboardParam) {
                            itemsData[paramName] = params[param.dashboardParam];
                        }
                    }
                }
            }

            menu.items.forEach(function (item, i) {
                var itemCopy = angular.copy(item);

                if (!item.conditions || conditions.validateConditions(item.conditions, itemsData, params)) {
                    itemCopy.text = utils.strings.parseValue(item.text || item.name, itemsData, params, i);
                    if (item.url) {
                        itemCopy.href = utils.strings.parseValue(item.url, itemsData, params);

                        if (itemCopy.href === window.location.href ||
                            utils.url.haveTheSameHash(itemCopy.href, window.location.href)) {
                            return true;
                        }
                    }
                    menuItems.push(itemCopy);
                }
            });

            if (!menuItems.length) {
                return null;
            }

            menuInstance.items = menuItems;
            menuInstance.params = settings.params;
            return menuInstance;
        }

        var methods = {
            getMenuById: function (menuId) {
                if (menus[menuId]) {
                    return $q.when(new Menu(utils.objects.copy(menus[menuId])));
                }

                if (onMenuLoad[menuId]) {
                    var deferred = $q.defer();
                    onMenuLoad[menuId].push({menuId: menuId, deferred: deferred});
                    return deferred.promise;
                }

                onMenuLoad[menuId] = [];
                return utils.http.wrappedHttpGet("data/menus/" + menuId.replace(/\./g, "/") + ".json?v=" +
                    version).then(function (menu) {
                    menus[menuId] = menu;

                    var menuObj = new Menu(utils.objects.copy(menu));

                    if (onMenuLoad[menuId].length) {
                        onMenuLoad[menuId].forEach(function (onLoad) {
                            onLoad.deferred.resolve(menuObj);
                        });
                    }

                    return menuObj;
                });
            },
            getMenu: function (menuSettings, data, params) {
                if (menuSettings.id) {
                    return methods.getMenuById(menuSettings.id).then(function (menu) {
                        return getMenu(menu, menuSettings, data, params);
                    });
                }

                return getMenu(menuSettings, menuSettings, data, params);
            },
            getMenuParser: function (menuSettings) {
                if (!menuSettings.id) {
                    return $q.when(menuSettings);
                }

                return this.getMenuById(menuSettings.id).then(function (menu) {
                    return function (data, params) {
                        return getMenu(menu, menuSettings, data, params);
                    };
                });
            },
            initMenu: function (menuId) {
                return methods.getMenuById(menuId);
            },
            initMenus: function (menuIds) {
                if (!menuIds || !menuIds.length) {
                    return $q.when(null);
                }

                var promises = [];
                menuIds.forEach(function (menuId) {
                    promises.push(methods.initMenu(menuId));
                });
                return $q.all(promises);
            }
        };

        return methods;
    }

    menusFactory.$inject = ["$q", "utils", "version", "conditions", "Menu"];

    angular.module("Menus").factory("menus", menusFactory);
})();
