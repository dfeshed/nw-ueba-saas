    angular.module("Transforms", ["Utils"]).factory("transforms", ["utils", function(utils){
    var splunkDateFormatRegExp = /^(\d{2})\/(\d{2})\/(\d{4}):(\d{1,2}):(\d{1,2}):(\d{1,2})$/,
        splunkBucketRegExp = /^(\d{4})(\d{2})(\d{2})(\d{2})(\d{2})$/;

    var methods = {
        arrayJoin: function(array, options){
            if (!array)
                return null;

            options = options || {};
            return array.join(options.joiner || ", ");
        },
        count: function(array){
            if (!array)
                return 0;

            return array.length;
        },
        date: function(date, options){
            if (!date)
                return "";

            if (!options)
                options = { format: "MM/DD/YY HH:mm" };

            var newDate = methods.getDate(date);
            if (options.prettyDate)
                return utils.date.prettyDate(newDate.toDate(), options.shortPrettyDate);

            return newDate.format(options.format);
        },
        getDate: function(date){
            if (!date)
                return "";

            if (angular.isDate(date))
                return moment(date);

            if (typeof(date) === "number")
                return moment(date);

            if (/^\d+$/.test(date))
                return moment(parseInt(date));

            var newDate = moment(),
                subtractMatch = date.match(/^-(\d+)(\w+)$/);

            if (subtractMatch){
                var now = moment();
                newDate = now.subtract(subtractMatch[2], parseInt(subtractMatch[1]));
            }
            else{
                var specifiedDate = moment(date);
                if (specifiedDate.isValid())
                    newDate = specifiedDate;
                else if (/^\d+$/.test(date))
                    newDate = moment(parseInt(date, 10));
                else if (splunkDateFormatRegExp.test(date)){
                    newDate = moment(date.replace(/^(\d{2})\/(\d{2})\/(\d{4}):(\d{1,2}):(\d{1,2}):(\d{1,2})$/, "$3-$1-$2 $4:$5:$6"));
                }
                else if (splunkBucketRegExp.test(date)){
                    newDate = moment(date.replace(splunkBucketRegExp, "$1-$2-$3 $4:$5"));
                }
            }

            return newDate;
        },
        round: function(number){
            return parseInt(number, 10);
        },
        string: function(str, options){
            if (options && options.regExp){
                return str.replace(new RegExp(options.regExp), options.replaceWith);
            }

            return str;
        },
        stringDate: function(value, options){
            var transformed = methods.date(value, options);
            return methods.string(transformed, options);
        },
        transformParams: function(params, paramsTransform){
            var transformedParams = angular.copy(params);

            if (paramsTransform){
                var transform;
                transformedParams = {};

                for(var paramName in paramsTransform){
                    transform = paramsTransform[paramName];

                    if (params[paramName] !== undefined)
                        transformedParams[paramName] = methods[transform.type](params[paramName], transform);
                }
            }

            return transformedParams;
        }
    };

    return methods;
}]);