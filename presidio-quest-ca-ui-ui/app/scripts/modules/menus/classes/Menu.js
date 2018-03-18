(function () {
    'use strict';

    angular.module("Menus").factory("Menu", ["dynamicMenus", function (dynamicMenus) {

        function Menu (config) {
            this.validate(config);

            parseDynamicMenuItems(config);

            this.items = config.items;
            this.id = config.id;
            if (config.name) {
                this.text = config.name;
            }
        }

        Menu.prototype.validate = function (config) {
            if (!config) {
                return;
            }

            var ERROR_PREFIX = "Can't create Menu, ";

            if (!config.id) {
                throw new Error(ERROR_PREFIX + "missing ID.");
            }

            if (typeof(config.id) !== "string") {
                throw new TypeError(ERROR_PREFIX + "expected a string ID but got " + typeof(config.id));
            }

            if (!config.items) {
                throw new Error(ERROR_PREFIX + "missing the items array.");
            }

            if (config.items.constructor !== Array) {
                throw new TypeError(ERROR_PREFIX + " expected an array for items.");
            }

            for (var item of config.items) {
                if (item.dynamicMenu) {
                    continue;
                }

                if (Object(item) !== item) {
                    throw new TypeError(ERROR_PREFIX + "invalid item config, expected an object but got " + item);
                }

                if (!item.text && !item.name) {
                    throw new Error(ERROR_PREFIX + "missing text for item.");
                }

                if (!item.url && !item.onSelect) {
                    throw new Error(ERROR_PREFIX + "a menu item must have either a url or onSelect properties.");
                }
            }
        };

        function parseDynamicMenuItems (menuConfig) {
            dynamicMenus.setDynamicMenus(menuConfig.items);
        }

        return Menu;

    }]);
})();
