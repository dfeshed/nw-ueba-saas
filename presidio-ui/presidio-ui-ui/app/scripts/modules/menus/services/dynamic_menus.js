(function () {
    'use strict';

    function dynamicMenus (dataEntities, utils, state) {

        /**
         * Iterates over an array of menu items and replaces dynamic menus with entities menu items
         * @param menuItems
         * @param params
         */
        function setDynamicMenus (menuItems, params) {
            var newIndices = [];

            params = params || state.currentParams;

            menuItems.forEach(function (menuItem, index) {
                if (menuItem.dynamicMenu) {
                    newIndices.push({
                        index: index,
                        newItems: getBaseEntityDynamicMenu(menuItem.dynamicMenu)
                    });
                }
                else if (menuItem.children) {
                    setDynamicMenus(menuItem.children);
                }
            });

            // It's done from the end to the start because the size of navData changes while looping:
            for (var itemsMap, i = newIndices.length - 1; i >= 0; i--) {
                itemsMap = newIndices[i];
                utils.arrays.replace(menuItems, itemsMap.index, itemsMap.newItems);
            }

            menuItems.forEach(function (menuItem) {
                if (menuItem.url) {
                    menuItem.href = utils.strings.parseValue(menuItem.url, {}, params);
                }
            });
        }

        /**
         * Given a dynamic menu configuration, with base entity, returns an array of menu items for all extending
         * (non-abstract) entities. Each menu item has 'name' and 'url' properties.
         * @param dynamicMenuConfig
         * @returns {*}
         */
        function getBaseEntityDynamicMenu (dynamicMenuConfig) {
            if (!dynamicMenuConfig) {
                throw new Error("Can't create base entity dynamic menu items - missing dynamicMenuConfig.");
            }

            if (!dynamicMenuConfig.baseEntity || dynamicMenuConfig.baseEntity.length === 0) {
                throw new Error("Can't get base entity dynamic menu items - no base entity specified.");
            }

            if (!dynamicMenuConfig.name || !dynamicMenuConfig.url) {
                throw new Error("Can't create dynamic menu items for base entity, 'name' and 'url' are required.");
            }

            // Go over all base entities to collect extending entities for menu

            var menuItems = [];
            // gets entity and returns item that represents menu item
            function extractMenuDataFromEntity (entity) {
                return {
                    name: utils.strings.parseValue(dynamicMenuConfig.name, {entity: entity}).replace(/\\\\/g, ""),
                    url: utils.strings.parseValue(dynamicMenuConfig.url, {entity: entity}).replace(/\\\\/g, "")
                };
            }

            var leafEntities;
            for (var index = 0; index < dynamicMenuConfig.baseEntity.length; index++) {
                // get extending entities of base entity
                leafEntities = dataEntities.getExtendingEntities(dynamicMenuConfig.baseEntity[index]);
                // create menu item for each extended entity
                menuItems = menuItems.concat(leafEntities.map(extractMenuDataFromEntity));

            }

            return menuItems;
        }

        return {
            setDynamicMenus: setDynamicMenus
        };

    }

    dynamicMenus.$inject = ["dataEntities", "utils", "state"];

    angular.module("Menus").factory("dynamicMenus", dynamicMenus);
})();
