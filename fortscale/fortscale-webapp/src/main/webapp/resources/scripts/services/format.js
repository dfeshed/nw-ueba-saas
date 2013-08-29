angular.module("Fortscale").factory("format", ["utils", function(utils){
    var methods = {
        date: function(value, options){
            var date = moment(value);
            if (!date.isValid()){
                var subtractMatch = value.match(/^-(\d+)(\w+)$/);
                if (subtractMatch){
                    var now = moment();
                    date = now.subtract(subtractMatch[2], parseInt(subtractMatch[1]));
                }
                else
                    return "";
            }

            if (options.prettyDate)
                return utils.date.prettyDate(date.toDate(), options.shortPrettyDate);

            return date.format(options.format);
        },
        float: function(value, options){
            var floatValue = parseFloat(value, 10);

            if (options && options.decimals)
                return floatValue.toFixed(options.decimals);
            else
                return floatValue;
        },
        int: function(value, options){
            return parseInt(value, 10);
        },
        formatItem: function(item, value){
            if (!item.format)
                return value;

            var method = methods[item.format];
            if (!method)
                return value;

            return method(value, item.formatOptions);
        }
    };

    methods.datetime = methods.date;

    return methods;
}]);