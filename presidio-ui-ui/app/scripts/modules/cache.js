(function(){
    'use strict';

    var prefix = "cache_";
    var CACHE_DISABLED = !!localStorage.__disableCache;

    function Cache($rootScope, options){
        if (!options || !options.id) {
            throw new Error("Can't instantiate Cache - no id specified.");
        }

        this.$rootScope = $rootScope;
        this.options = options || {};
        this.data = {};
        if (this.__defineGetter__) {
            this.__defineGetter__("id", function () {
                return options.id;
            });
        } else {
            this.id = options.id;
        }
    }

    Cache.clearAll = function(){
        var cachePrefixRegExp = new RegExp("^" + prefix);

        for(var key in localStorage){
            if (cachePrefixRegExp.test(key)) {
                localStorage.removeItem(key);
            }
        }
    };

    Cache.prototype = {
        getKey: function(keyName){
            return [prefix, this.id, keyName].join("_");
        },
        getItem: function(keyName, options){
            if (CACHE_DISABLED) {
                return null;
            }

            var dataStr = localStorage.getItem(this.getKey(keyName));

            if (!dataStr) {
                return null;
            }

            var dataObj = JSON.parse(dataStr);

            if (dataObj && dataObj.expires && dataObj.expires < new Date().valueOf()){
                this.removeItem(keyName);
                return null;
            }

            var data = dataObj && dataObj.data;

            if (options && options.hold) {
                this.data[keyName] = data;
            }

            return data;
        },
        removeItem: function(keyName){
            if (CACHE_DISABLED) {
                return null;
            }

            localStorage.removeItem(this.getKey(keyName));
            if (this.data[keyName]) {
                delete this.data[keyName];
            }
        },
        setItem: function(keyName, data, options){
            if (CACHE_DISABLED) {
                return null;
            }

            options = options || {};
            if (!options.expires && !options.expiresIn){
                options.expiresIn = this.options.itemsExpireIn;
            }

            var storageData = { data: data };
            if (options.expires) {
                storageData.expires = options.expires;

            } else if (options.expiresIn) {
                storageData.expires = new Date().valueOf() + options.expiresIn * 1000;
            }

            localStorage.setItem(this.getKey(keyName), JSON.stringify(storageData));
            if (options.hold) {
                this.data[keyName] = data;
            }
        }
    };

    angular.module("Cache", []).factory('Cache', function ($injector) {
        var constructor = function(options) { return $injector.instantiate(Cache, { options: options }); };
        constructor.clearAll = Cache.clearAll;
        return constructor;
    });
})();
