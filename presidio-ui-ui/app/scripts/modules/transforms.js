(function () {
    'use strict';

    angular.module("Transforms", ["Utils"]).factory("transforms", ["utils", function (utils) {

        //function getGroupLinkDn(groupParts, partIndex) {
        //    var linkParts = [];
        //    for (var i = partIndex; i < groupParts.length; i++) {
        //        linkParts.push(groupParts[i]);
        //    }
        //    return linkParts.join(",");
        //}

        var methods = {
            adUser: function (value) {
                var cnMatch = value.match(/^CN=([^\,]+)/);
                if (cnMatch) {
                    return cnMatch[1];
                }

                return value;
            },
            adUsers: function (value) {
                if (!value) {
                    return value;
                }

                var users = value.match(/CN=([^\,]+)/g),
                    adUsers = [];

                if (!users) {
                    return value;
                }

                users.forEach(function (user) {
                    adUsers.push(user.split("=")[1]);
                });

                if (adUsers.length) {
                    return adUsers.join(", ");
                }

                return value;
            },
            arrayJoin: function (array, options) {
                if (!array) {
                    return null;
                }

                options = options || {};
                return array.join(options.joiner || ", ");
            },
            count: function (array) {
                if (!array) {
                    return 0;
                }

                return array.length;
            },
            date: function (date, options) {
                if (!date) {
                    return "";
                }

                if (!options) {
                    options = {format: "MM/DD/YY HH:mm"};
                }

                var newDate = utils.date.getMoment(date);
                if (options.prettyDate) {
                    return utils.date.prettyDate(newDate.toDate(), options.shortPrettyDate);
                }

                return newDate.format(options.format);
            },
            /* Deprecated, remove after March 1st, 2015 if no problem arises!
             group: function(groupStr, options){
             if (!groupStr)
             return "";

             options = options || {};

             var groupParts = groupStr.split(","),
             results = [];


             angular.forEach(groupParts, function(part, partIndex){
             var partMatch = part.match(/^(\w{2})=(.*)/);
             if (partMatch && partMatch[1] !== "DC"){
             results.splice(0, 0, "<a href='#/d/investigate/group_membership?user_groups.group_dn=" +
             encodeURIComponent("$" + getGroupLinkDn(groupParts, partIndex).replace(/,/g, "_;_")) + "'>" +
             partMatch[2] + "</a>");
             }
             });

             return results.join(options.divider || " &gt; ");
             },
             */
            ou: function (ouStr, options) {
                if (!ouStr) {
                    return "";
                }

                options = options || {};

                var ous = ouStr.split(","),
                    results = [],
                    ouMatch = /^OU=(.*)$/;

                angular.forEach(ous, function (ou) {
                    var match = ou.match(ouMatch);
                    if (match) {
                        results.push(match[1]);
                    }
                });

                return results.join(options.divider || " &gt; ");
            },
            round: function (number) {
                return parseInt(number, 10);
            },
            string: function (str, options) {
                if (options && options.regExp) {
                    return str.replace(new RegExp(options.regExp), options.replaceWith);
                }

                return str;
            },
            stringDate: function (value, options) {
                var transformed = methods.date(value, options);
                return methods.string(transformed, options);
            },
            transformParams: function (params, paramsTransform) {
                var transformedParams = angular.copy(params);

                if (paramsTransform) {
                    var transform;
                    transformedParams = {};

                    for (var paramName in paramsTransform) {
                        if (paramsTransform.hasOwnProperty(paramName)) {
                            transform = paramsTransform[paramName];

                            if (params[paramName] !== undefined) {
                                transformedParams[paramName] = methods[transform.type](params[paramName], transform);
                            }
                        }
                    }
                }

                return transformedParams;
            },
            transformValue: function (value, transformSettings) {
                var method = methods[transformSettings.method];
                if (!method) {
                    throw new Error("Invalid transform method: ", transformSettings.method);
                }

                return method(value, transformSettings.options);
            }
        };

        return methods;
    }]);
}());
