(function () {
    'use strict';

    angular.module("Format", ["Utils"])
        .factory("format", ["utils", function (utils) {
            var methods = {
                boolean: function (value) {
                    return !!value;
                },
                date: function (value, options) {
                    var date = utils.date.getMoment(value);

                    if (options.prettyDate) {
                        return utils.date.prettyDate(date.toDate(), options.shortPrettyDate);
                    }

                    return date.format(options.format);
                },
                float: function (value, options) {
                    var floatValue = parseFloat(value, 10);

                    if (options && options.decimals) {
                        return floatValue.toFixed(options.decimals);
                    } else {
                        return floatValue;
                    }
                },
                int: function (value, options) {
                    return parseInt(value, 10);
                },
                formatItem: function (item, value) {
                    if (!item.format) {
                        return value;
                    }

                    var method = methods[item.format];
                    if (!method) {
                        return value;
                    }

                    return method(value, item.formatOptions);
                }
            };

            methods.datetime = methods.date;

            return methods;
        }]);
}());
