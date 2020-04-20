(function () {
    'use strict';

    angular.module("Tags", []).factory("tags",
        ["$q", "utils",
            function ($q, utils) {

                var savedTags;
                var defer;

                function getTags(tagsName, data) {
                    if (!tagsName || !data) {
                        return null;
                    }

                    var tagsObj = angular.isObject(tagsName) && tagsName;

                    var tagsConfig = savedTags[tagsObj ? tagsObj.name : tagsName];
                    if (!tagsConfig) {
                        return null;
                    }

                    var usedTags = [],
                        valuesData = {};

                    if (tagsObj && tagsObj.values && data[tagsObj.values]) {
                        var tags = data[tagsObj.values];
                        if (typeof(tags) === "string") {
                            tags = tags.split(",");
                        }

                        if (tags.constructor !== Array) {
                            throw new TypeError("Invalid tags, expected an Array but got " +
                                tags.constructor.name + ".");
                        }

                        tags.forEach(function (value) {
                            valuesData[tagsObj.valuesMap && tagsObj.valuesMap[value] || value] = true;
                        });
                    }

                    tagsConfig.forEach(function (tag) {
                        var fieldName = tagsObj && tagsObj.map && tagsObj.map[tag.field] || tag.field;
                        if (valuesData[fieldName] || data[fieldName]) {
                            usedTags.push(tag);
                        }
                    });

                    return usedTags;
                }

                var methods = {
                    getTagsSync: getTags,
                    getTags: function (tagsName, data) {
                        if (savedTags) {
                            return $q.when(getTags(tagsName, data));
                        } else {
                            return methods.initTags().then(function (tags) {
                                return getTags(tagsName, data);
                            });
                        }
                    },
                    initTags: function () {
                        // Check if it's the first time we're fetching the tags
                        var firstTime = !defer;

                        // If called before, use the same deferred object
                        defer = defer || $q.defer();

                        // If first time, fetch the tags data
                        if (firstTime) {
                            utils.http.wrappedHttpGet("data/tags.json").then(function (tags) {
                                savedTags = tags;
                                // Resolve all of the promises that are waiting for the tags
                                defer.resolve(tags);
                            });
                        }

                        // Always return the same promise
                        // If it was already resolved, it's `.then` will execute immediately
                        // with `tags` data as first param
                        return defer.promise;
                    }
                };

                return methods;
            }]);
}());
