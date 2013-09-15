angular.module("Utils", []).factory("utils", [function(){
    var methods = {
        date: {
            prettyDate: function (date, isShort) {
                if (!date)
                    return "";

                if (angular.isNumber(date) || angular.isString(date))
                    date = new Date(date);

                try{
                    var diff = (((new Date()).getTime() - date.getTime()) / 1000),
                        day_diff = Math.floor(diff / 86400);
                }
                catch(e){
                    return "";
                }

                var units = {
                    long: {
                        now: "just now",
                        minute: "1 minute ago",
                        minutes: " minutes ago",
                        hour: "1 hour ago",
                        hours: " hours ago",
                        yesterday: "Yesterday",
                        days: " days ago",
                        week: " week ago",
                        weeks: " weeks ago",
                        month: "1 month ago",
                        months: " months ago",
                        years: " years ago"
                    },
                    short: {
                        now: "< 1m",
                        minute: "1m",
                        minutes: "m",
                        hour: "1h",
                        hours: "h",
                        yesterday: "1d",
                        days: "d",
                        weeks: "w",
                        month: "1M",
                        months: "M",
                        years: "y"
                    }
                };

                var unitsToUse = isShort ? units.short : units.long;

                if ( isNaN(day_diff) || day_diff < 0)
                    return;

                return (day_diff == 0 && (
                    diff < 60 && unitsToUse.now ||
                        diff < 120 && unitsToUse.minute ||
                        diff < 3600 && Math.floor( diff / 60 ) + unitsToUse.minutes ||
                        diff < 7200 && unitsToUse.hour ||
                        diff < 86400 && Math.floor( diff / 3600 ) + unitsToUse.hours) ||
                    day_diff == 1 && unitsToUse.yesterday ||
                    day_diff < 14 && day_diff + unitsToUse.days ||
                    day_diff < 31 && Math.ceil( day_diff / 7 ) + unitsToUse.weeks ||
                    day_diff < 62 && unitsToUse.month ||
                    day_diff < 365 && Math.floor(day_diff / 30.416) + unitsToUse.months ||
                    Math.floor(day_diff / 365) + unitsToUse.years);
            }
        },
        strings: {
            parseValue: function(value, data, params, index, format){
                var parsedValue = value.replace(/\{\{([^\}]+)\}\}/g, function(match, variable){
                    if (/^@/.test(variable)){
                        var param = variable.replace("@", "");
                        if (param === "index")
                            return index;

                        return params[param] || "";
                    }
                    else{
                        var dataValue = data[variable];
                        if (dataValue !== undefined && dataValue !== null){
                            if (format)
                                return format[format.method](dataValue, format.options);

                            return dataValue;
                        }

                        return "";
                    }
                });

                return parsedValue;
            }
        },
        url: {
            getQuery: function(params){
                var paramValue,
                    encodedParams = [];

                for(var paramName in params){
                    paramValue = params[paramName];

                    if (angular.isObject(paramValue))
                        paramValue = JSON.stringify(paramValue);

                    encodedParams.push([paramName, encodeURIComponent(paramValue)].join("="));
                }

                return encodedParams.join("&");
            },
            setHashQuery: function(params){
                var currentHash = document.location.hash,
                    hashMatch = currentHash.match(/^(#[^\?]+)/);

                if (hashMatch){
                    var encodedParams = methods.url.getQuery(params);
                    if (encodedParams)
                        document.location.hash = hashMatch[1] + "?" + encodedParams;
                }
            }
        }
    };

    return methods;
}]);
