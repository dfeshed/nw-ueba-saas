(function () {
    'use strict';

    function UserUtils (BASE_URL, $http, assert, appConfig, $cacheFactory, $state, $log) {
        this.BASE_URL = BASE_URL;
        this.$http = $http;
        this.assert = assert;
        this.appConfig = appConfig;
        this.$cacheFactory = $cacheFactory;
        this.$state = $state;
        this.$log = $log;
    }

    UserUtils.$inject = ['BASE_URL', '$http', 'assert', 'appConfig', '$cacheFactory', '$state', '$log'];

    /**
     * Takes the order from appConfig and tries to find a username match.
     *
     * @param user
     * @returns {*}
     */
    UserUtils.prototype.getFallBackDisplayNames = function (user) {

        // Set default value
        var fallbackDisplayName = user.id;

        // Get the default order value and convert to list of properties
        var propsCSV = this.appConfig.getConfigValue('default', 'userNameFallbackOrder');
        var props;
        if (propsCSV) {
            props = propsCSV.split(',');
        } else {
            props = ['id'];
        }

        // Iterate through list of properties and return the first value found on User
        _.some(props, prop => {
            var propTrim = prop.trim();

            if (user[propTrim] && user[propTrim].trim() !== '') {
                fallbackDisplayName = user[propTrim];
                return true;
            }
        });

        // Return the value
        return fallbackDisplayName;
    };

    /**
     * Sets fall back display name property. displayName or noDomainUsername or username
     *
     * @param {*} user
     * @private
     */
    UserUtils.prototype._setFallBackDisplayName = function (user) {
        user.fallBackDisplayName = this.getFallBackDisplayNames(user);
    };

    /**
     * Sets fall back display name property for all users. displayName or noDomainUsername or username
     *
     * @param {array<{displayName: string=, noDomainUsername: string=, username:string}>} users
     */
    UserUtils.prototype.setFallBackDisplayNames = function (users) {
        _.each(users, _.bind(this._setFallBackDisplayName, this));
    };

    /**
     * Prevent duplicated display names for a single user and its duplicates for any list of users.
     *
     * @param {{fallBackDisplayName: string, username: string}} user
     * @param {number} index
     * @param {array<{fallBackDisplayName: string, username: string}>} users
     * @private
     */
    UserUtils.prototype._preventUserDisplayNameDuplication = function (user, index, users) {

        // Create duplications list
        var duplications = _.filter(users.slice(index), function (itUser) {
            return itUser.fallBackDisplayName === user.fallBackDisplayName;
        });

        // If duplications list's length is greater than one, duplications exists and should be handled.
        if (duplications.length > 1) {

            // Handle duplications by adding username in braces.
            _.each(duplications, function (dupUser) {
                dupUser.fallBackDisplayName += ' (' + dupUser.username + ')';
            });
        }
    };

    /**
     * Prevent duplicated display names in any list of users.
     *
     * @param {array<{displayName: string=, noDomainUsername: string=, username:string}>} users
     */
    UserUtils.prototype.preventFallBackDisplayNameDuplications = function (users) {
        _.each(users, this._preventUserDisplayNameDuplication, this);
    };

    /**
     *
     * @param {string} userIds
     * @returns {Promise<T>|*|Promise.<T>|IPromise<TResult>}
     */
    UserUtils.prototype.getUsersDetails = function (userIds) {
        // validate user ids
        _.each(userIds, userId => {
            this.assert.isString(userId, 'userId', 'UserUtilsService: getUsersDetails: ');
        });

        // fetch user details
        return this.$http.get(`${this.BASE_URL}/user/${userIds.join(',')}/details`)
            .then(res => {
                if (!res.data || !res.data.data) {
                    console.error('UserUtilsService: getUsersDetails: Server response does not have data.', res);
                    return [];
                }

                return res.data.data;
            })
            .catch(err => {
                console.error('UserUtilsService: getUsersDetails: Server response error.', err);
                return [];
            });
    };

    /**
     * Returns a psomise that is resolved on system user tags groups.
     *
     * @returns {Promise<*>}
     */
    UserUtils.prototype.getUsersTagsCount = function () {
        return this.$http.get(`${this.BASE_URL}/user/usersTagsCount`)
            .then(res => {
                // Validate data
                if (!res.data || !res.data.data) {
                    console.error('UserUtilsService: getUsersTagsCount: Server response does not have data.', res);
                    return {};
                }

                return _.keyBy(res.data.data, 'key');
            })
            .catch(err => {
                console.error('UserUtilsService: getUsersTagsCount: Server response error.', err);
                return {};
            });
    };

    UserUtils.prototype.getUserAddress = function (user) {
        let adressItems = [];
        let addressKeys = ['streetAddress', 'adL', 'adC'];
        _.each(addressKeys, addressKey => {
            if (user[addressKey]) {
                adressItems.push(user[addressKey]);
            }
        });

        return adressItems.join(', ');

    };

    UserUtils.prototype.setFullAddress = function (user, addressKey) {
        addressKey = addressKey || 'fullAddress';

        user[addressKey] = this.getUserAddress(user);
    };

    UserUtils.prototype.setUsersFullAddress = function (users, addressKey) {
        _.each(users, (user) => {
            this.setFullAddress(user, addressKey);
        });
    };

    UserUtils.prototype.getUserByUsername = function (username) {
        return this.$http.get(`${this.BASE_URL}/user`, {
            params: {
                page: 1,
                size: 1,
                search_field_contains: username
            }
        })
            .then((res) => {
                return res.data.data[0];
            });
    };

    angular.module('Fortscale.shared.services.modelUtils')
        .service('userUtils', UserUtils);
}());
