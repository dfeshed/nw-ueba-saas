(function () {
    'use strict';


    function FsResourceStoreProvider () {

        /**
         * Determines the expiration duration. Defaults to one hour in milliseconds.
         *
         * @type {number}
         * @private
         */
        var _expireDuration = 1000 * 60 * 60;

        /**
         * Sets the expiration duration.
         *
         * @param {number} duration
         */
        this.setExpireDuration = function (duration) {
            _expireDuration = duration;
        };

        /**
         * Gets the expiration duration.
         *
         * @returns {number}
         */
        this.getExpireDuration = function () {
            return _expireDuration;
        };

        /**
         * Flag that states if the resource should be purged on expiration. Defaults to false.
         *
         * @type {boolean}
         * @private
         */
        var _purgeOnExpire = false;

        /**
         * Sets purge on expire flag.
         *
         * @param {boolean} val
         */
        this.setPurgeOnExpire = function (val) {
            _purgeOnExpire = val;
        };

        /**
         * Gets purge on expire flag.
         *
         * @returns {boolean}
         */
        this.getPurgeOnExpire = function () {
            return _purgeOnExpire;
        };

        /**
         *
         * @constructor
         */
        function FsResourceStoreService (assert) {

            /**
             * Holds all the reources.
             *
             * @private
             */
            this._resources = {};

            /**
             * Checks if a resource is expired.
             *
             * @param {object} resourceWrapper
             * @returns {boolean}
             * @private
             */
            this._isExpired = function (resourceWrapper) {
                var now = new Date();
                return (resourceWrapper.updateTime.valueOf() + _expireDuration < now.valueOf());
            };

            /**
             *
             * @param {string} resourceName
             * @param {object} resource
             * @param {boolean=} purgeOnExpire
             */
            this.storeResource = function (resourceName, resource, purgeOnExpire) {

                if (purgeOnExpire === undefined) {
                    purgeOnExpire = _purgeOnExpire;
                }

                this._resources[resourceName] = {
                    updateTime: new Date(),
                    resource: resource,
                    purgeOnExpire: purgeOnExpire
                };
            };

            /**
             * Gets a resource by its name. If expired and purgeOnExpire is true,
             * the resource is purged, and null is returned. If expired an purgeOnExpire is false,
             * a '_isExpired' property will be added to the resource and set to true.
             * If no resource is found, null is returned.
             *
             * @param {string} resourceName
             * @returns {object|null}
             */
            this.fetchResource = function (resourceName) {

                // Get the resource
                var resourceWrapper = this._resources[resourceName];

                // if no resource is found return null
                if (!resourceWrapper) {
                    return null;
                }

                // If resource is expired
                if (this._isExpired(resourceWrapper)) {

                    // If purgeOnExpire is true, delete the resource and return null
                    if (resourceWrapper.purgeOnExpire) {
                        delete this._resources[resourceName];
                        return null;
                    }


                    // If pureOnExpire is false, add '_isExpired' property to resource
                    resourceWrapper.resource._isExpired = true;
                }

                // Return the resource
                return resourceWrapper.resource;
            };

            /**
             * Returns a specific resource item by its id. It fetches the resource by its name.
             * If resource is null, null is returned.
             * The resource is queried for a specific resource item, and if a resource is not found,
             * null is returned. If resource is expired, _isExpired will be added to the resource
             * item.
             *
             * @param {string} resourceName
             * @param {string} resourceId
             * @param {string=} idKey Defaults to 'id'
             * @returns {* | null}
             */
            this.fetchResourceItemById = function (resourceName, resourceId, idKey) {

                // set idKey default value if false
                idKey = idKey || 'id';

                // Get the resource
                var resource = this.fetchResource(resourceName);

                // If resource is null return null
                if (resource === null) {
                    return null;
                }

                // Build the query
                var query = {};
                query[idKey] = resourceId;

                // Get resource item
                var resourceItem = _.find(resource, query);

                // If resourceItem is undefined return null
                if (resourceItem === undefined) {
                    return null;
                }

                // If resource is expired, set _isExpired on resourceItem
                if (resource._isExpired) {
                    resourceItem._isExpired = true;
                }

                // Return the resourceItem
                return resourceItem;
            };

        }

        this.$get = ['assert', function (assert) {
            return new FsResourceStoreService(assert);
        }];
    }



    angular.module('Fortscale.shared.components.fsResourceStore')
    .provider('fsResourceStore', FsResourceStoreProvider);
}());
