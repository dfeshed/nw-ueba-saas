(function () {
    'use strict';

    angular.module("Utils", ["Config"])
        .factory("utils", ["$http", "config",
            function ($http, config) {

                var ipAddressRegExp = /^(\d{1,3}\.){3}\d{1,3}$/, // Not exactly accurate, but good enough for now
                    hashRegExp = /#.*$/,
                    parsedStringTest = /\{\{/;

                var stringParsers = {};

                var parseStringFormatters = {
                    /**
                     *
                     * @param {*} value
                     * @returns {*}
                     * @description
                     * Takes a value argument.
                     * If value is number, the method tries to convert it to absolute number.
                     * If value is anything but a number, the value is returned.
                     */
                    abs: function (value) {
                        return angular.isNumber(value) ? Math.abs(value) : value;
                    },
                    allowNull: function (value) {
                        return value === undefined || value === null || value === "" ? null : value;
                    },
                    bytesCount: function (value) {
                        return methods.numbers.bytesCount(value);
                    },
                    defaultIfEmpty: function (value, defaultValue) {
                        return typeof value !== "undefined" ? value : defaultValue;
                    },
                    bytesPerSecCount: function (value) {
                        value = methods.numbers.bytesCount(value);
                        if (!value) {
                            return value;
                        }
                        return value + "/sec";
                    },
                    capitalize: function (value) {
                        return methods.strings.capitalize(value);
                    },
                    cast: function (value, type) {
                        if (type === "number") {
                            return parseFloat(value);
                        }

                        if (type === "boolean") {
                            return !!value;
                        }

                        return value.toString();
                    },
                    closeDate: function (value) {
                        var momentValue = methods.date.getMoment(value);
                        if (momentValue.isValid()) {
                            return momentValue.fromNow();
                        }

                        return value;
                    },
                    count: function (value) {
                        return value.length;
                    },
                    /**
                     * Get date string from date
                     *
                     * @param  {number|Object} value           Date time value or Moment instance
                     * @param  {string}        format          Set format
                     *     "unixtimestamp" - Unix time value
                     *     "valueOf"       - Regular time value
                     *     "MM/DD/YYYY"    - Custom pattern (any valid Moment pattern)
                     *
                     * @param  {string}        add             Custom `add` (can be negative)
                     *     (e.g. "4hours", "-1month")
                     *     See: http://momentjs.com/docs/#/manipulating/add/
                     *
                     * @param  {string}        startOrEndOfDay Set time to start/end of day
                     *     "start" - Set time to start of day
                     *     "end"   - Set time to end of day
                     *
                     * @return {string}                        Resulting date string or ""
                     */
                    date: function (value, format, add, startOrEndOfDay) {
                        if (!value) {
                            return "";
                        }

                        // Build date object
                        // If already a moment object, returns as is
                        var momentValue = methods.date.getMoment(value);

                        if (momentValue.isValid()) {
                            if (startOrEndOfDay === "start") {
                                momentValue.startOf("day");
                            } else if (startOrEndOfDay === "end") {
                                momentValue.endOf("day");
                            }

                            if (add) {
                                // Custom `add` (e.g. "4hours" or "-1month")
                                momentValue = methods.date.shiftDate(momentValue, add);
                            }

                            if (format) {
                                // Predefined setups
                                if (format === "unixtimestamp") {
                                    return Math.floor(momentValue.valueOf() / 1000);
                                }
                                if (format === "valueOf") {
                                    return momentValue.valueOf();
                                }

                                // If part of config default patterns, use it, otherwise use as is
                                return momentValue.format(config[format] || format);
                            } else {
                                // Format using default patterns
                                return momentValue.format(config.timestampFormat || config.fallbackFormat);
                            }
                        }

                        return "";
                    },
                    dayDiff: function (value1, value2) {
                        var m1 = methods.date.getMoment(value1),
                            m2 = methods.date.getMoment(value2);

                        if (!m1.isValid() || !m2.isValid()) {
                            console.error("Can't get dayDiff, values: ", value1, value2);
                            return "?";

                        }
                        return methods.date.diff(m1.toDate(), m2.toDate(), "days");
                    },
                    dateDiffFromNow: function (value, valueUnit, returnFormat, startOrEndOfDay) {
                        valueUnit = valueUnit || "days";
                        returnFormat = returnFormat || "unixtimestamp";

                        switch (valueUnit) {
                            case "days":
                                value *= 24 * 60 * 60 * 1000;
                                break;
                            case "hours":
                                value *= 60 * 60 * 1000;
                                break;
                            case "minutes":
                                value *= 60 * 1000;
                                break;
                            case "seconds":
                                value *= 1000;
                                break;
                        }
                        // we reduce the time in miliseconds
                        var momentValue = methods.date.getMoment(new Date().getTime() - value);

                        if (startOrEndOfDay) {
                            if (startOrEndOfDay === "start") {
                                momentValue.startOf("day");
                            } else if (startOrEndOfDay === "end") {
                                momentValue.endOf("day");
                            }
                        }

                        if (returnFormat === "unixtimestamp") {
                            return Math.floor(momentValue.valueOf() / 1000);
                        }
                        if (returnFormat === "valueOf") {
                            return momentValue.valueOf();
                        }

                        return momentValue.format(returnFormat); // for example "MM/DD/YYYY HH:mm"
                    },
                    dateSql: function (value, column) {
                        return "to_date(" + parseStringFormatters.dateToUtc(column || 'date_time') + ")";
                    },
                    dateToUtc: function (value, columnName) {
                        return "hours_add(" + (value || columnName || 'date_time') + ", " + config.timezone + ")";
                    },
                    /**
                     *
                     * @param {*} value
                     * @returns {*}
                     * @description
                     * Takes a value. Validates that the value is a number.
                     * Returns the value if not a number.
                     * Returns (n*100).toFixes(2) if is number.
                     */
                    decimalToPercentage: function (value) {
                        if (angular.isNumber(value)) {
                            return (value * 100).toFixed(2);
                        }

                        return value;
                    },
                    /**
                     * Given a number, which represents a time span, returns the number formatted with prettyDate
                     */
                    diffToPrettyDate: function (value, units) {
                        var seconds = methods.date.toSeconds(value, units);
                        if (!seconds && seconds !== 0) {
                            return seconds;
                        }

                        return methods.date.prettyDate(seconds, true);
                    },
                    /**
                     * Given a number, which represents a time span, returns the number formatted with prettyTime
                     */
                    diffToPrettyTime: function (value, units) {
                        return methods.duration.prettyTime(value, units);
                    },
                    /**
                     * Given a prettyTime format, return the number according to the units
                     */
                    durationToNumber: function (value, units) {
                        return methods.duration.durationToNumber(value, units);
                    },
                    divide: function (value, divideBy, mathFunc) {
                        value = Number(value);
                        divideBy = Number(divideBy);

                        if (isNaN(value) || isNaN(divideBy)) {
                            return value;
                        }

                        if (!angular.isNumber(value) || !angular.isNumber(Number(divideBy))) {
                            return value;
                        }

                        var result = value / divideBy;
                        if (mathFunc) {
                            return Math[mathFunc](result);
                        }

                        return result;
                    },
                    encodeURIComponent: function (value) {
                        return encodeURIComponent(value);
                    },
                    /**
                     *
                     * @param {number} score
                     * @param {string} tableName
                     * @returns {string}
                     * @description
                     * Given a table name and a minimum event score, returns the optimized table's name,
                     * if it exists and the score fits.
                     **/
                    eventsTable: function (score, tableName) {
                        return methods.strings.getEventsTableName(tableName, score);
                    },
                    eventUserSql: function () {
                        return "users.displayname, users.id as userid, users.isuseradministrator, " +
                            "users.isuserexecutive, users.accountisdisabled, users.isuseraccountservice, " +
                            "users.followed";
                    },
                    groupName: function (value) {
                        var groupMatch = value.match(/^CN=([^\,]+)/);
                        if (groupMatch) {
                            return groupMatch[1];
                        }

                        return value;
                    },
                    //This function return a string that replace the separator with the splitter,
                    //e.g: (shavit,yossi):join:$:, will be ---> shavit$yossi
                    "join": function (value, separator, splitter) {
                        if (typeof(value) === "string") {
                            value = value.split(splitter || ",");
                        }

                        return (value) ? value.join(separator || ",") : null;
                    },
                    max: function (value, secondValue) {
                        var int1 = parseInt(value),
                            int2 = parseInt(secondValue),
                            max = Math.max(int1, int2);

                        return isNaN(max) ? "?" : max;
                    },
                    min: function (value, secondValue) {
                        var int1 = parseInt(value),
                            int2 = parseInt(secondValue),
                            min = Math.min(int1, int2);

                        return isNaN(min) ? "?" : min;
                    },
                    multiply: function (value, multiplyBy) {
                        if (!angular.isNumber(value) || !angular.isNumber(Number(multiplyBy))) {
                            return value;
                        }

                        return value * multiplyBy;
                    },
                    not: function (value) {
                        return !value;
                    },
                    number: function (value) {
                        var match = value.match(/\d+/);
                        if (match) {
                            return match[0];
                        }

                        return value;
                    },
                    "or": function (value1, value2) {
                        return !value1 && value1 !== 0 ? value2 : value1;
                    },
                    //squiz cahrs to the left side of a value to make it be with the proper chars
                    //e.g: "shavit":10:- will be "----shavit"
                    padLeft: function (value, length, character) {
                        return methods.strings.padLeft(value, length, character);
                    },
                    /*
                     * parse strings into URL params format (e.g replace "," with "%2C")
                     */
                    paramValue: function (value) {
                        return encodeURIComponent(value);
                    },
                    removeEmptyValue: function (value) {
                        if (value === undefined || value === null) {
                            return "";
                        }

                        return value;
                    },
                    removeAtDomain: function (value) {
                        if (!value) {
                            return "";
                        }
                        var match = value.match(/^(.+)@/);
                        return match ? (match[1] !== "" ? match[1] : value) : value;
                    },
                    removeDotDomain: function (value) {
                        if (methods.strings.isIpAddress(String(value))) {
                            return value;
                        }

                        if (!value) {
                            return "";
                        }

                        var match = value.match(/^([^\.]+)\./);
                        return match ? match[1] : value;
                    },
                    round: function (value) {
                        if (!angular.isNumber(value)) {
                            return value;
                        }

                        return Math.round(value);
                    },
                    secondsToHour: function (value, hourOnly) {
                        if (hourOnly) {
                            return Math.round(value / 3600);
                        }

                        return [
                            methods.strings.padLeft(Math.floor(value / 3600), 2, "0"),
                            methods.strings.padLeft(Math.floor(value % 3600 / 60), 2, "0"),
                            methods.strings.padLeft(value % 60, 2, "0")
                        ].join(":");
                    },
                    sinceNow: function (value, format, add, startOrEndOfDay) {
                        return parseStringFormatters.date.call(this, "now", format, add, startOrEndOfDay);
                    },
                    span: function (value1, value2Field, divider) {
                        var span = Math.abs(this.data[value2Field] - value1);
                        if (!isNaN(span) && divider) {
                            return Math.floor(span / divider);
                        }

                        return span;
                    },
                    "switch": function (value, ifTrue, ifFalse) {
                        return value ? ifTrue : ifFalse;
                    },
                    paging: function (page, pagingParamName, pageSize) {
                        page = page || 1;
                        if (typeof(page) === "string") {
                            page = parseInt(page);
                        }

                        var paging = this.params[pagingParamName],
                            offset = (page - 1) * pageSize;
                        pageSize = parseInt(pageSize, 10);

                        if (!paging || !pageSize || isNaN(offset) || isNaN(pageSize) || !paging.total ||
                            !paging.currentPageCount) {
                            return null;
                        }

                        return (offset + 1) + " - " + (offset + paging.currentPageCount) + " of " + paging.total;
                    },
                    pluralOrSingular: function (value, singular, plural) {
                        if (!value || !angular.isNumber(value)) {
                            return plural;
                        }

                        if (value === 1) {
                            return singular;
                        }

                        return plural;
                    },
                    properties: function (value) {
                        var str = [];
                        for (var p in value) {
                            if (value.hasOwnProperty(p)) {
                                str.push(p + ": " + value[p]);
                            }
                        }
                        return str.join(", ");
                    },
                    timeSpan: function (value1, value2Field) {
                        if (!value1 || value2Field && !this.data[value2Field]) {
                            return null;
                        }

                        var value2 = value2Field ? this.data[value2Field] : 'now';

                        var moment1 = methods.date.getMoment(value1);
                        var moment2 = methods.date.getMoment(value2);

                        if (!moment1.isValid() || !moment2.isValid()) {
                            return "?";
                        }

                        var diff = Math.abs(moment1.diff(moment2));
                        return diff < 1000 && (diff / 1000).toFixed(2) + " seconds" ||
                            (diff = diff / 1000) < 60 && diff.toFixed(2) + " seconds" ||
                            (diff = diff / 60) < 60 && Math.floor(diff) + ":" +
                            methods.strings.padLeft(String(Math.floor(60 * (diff % 1))), 2, "0") + " minutes" ||
                            (diff = diff / 60) < 24 && Math.floor(diff) + ":" +
                            methods.strings.padLeft(String(Math.floor(60 * (diff % 1))), 2, "0") + " hours" ||
                            Math.floor(diff / 24) + " days";
                    },
                    timeZone: function (value) {
                        return new Date().getTimezoneOffset() * -1;
                    },
                    toLowerCase: function (value) {
                        if (!value || !angular.isString(value)) {
                            return value;
                        }

                        return value.toLowerCase();
                    },
                    toUpperCase: function (value) {
                        if (!value || !angular.isString(value)) {
                            return value;
                        }

                        return value.toUpperCase();
                    },
                    unixTimeToDate: function (value) {
                        return methods.date.getMoment(Number(value) * 1000).toDate();
                    },
                    matches: function (value, pattern, matchValue, noMatchValue) {
                        var patternRegexp = methods.regexp.patterns[pattern] || new RegExp(pattern);

                        if (!patternRegexp || !patternRegexp.test(value)) {
                            return noMatchValue;
                        }

                        return matchValue;
                    },
                    toFixed: function (value, decimals) {
                        if (!value) {
                            return value;
                        }

                        return parseFloat(value.toFixed(decimals));
                    },
                    yesNo: function (value) {
                        return value ? "Yes" : "No";
                    }
                };

                function getParamValueForParser(value) {

                    if (!value || typeof(value) !== "string") {
                        return value;
                    }

                    var paramMatch = value.match(/^@(.+)/);

                    if (paramMatch) {
                        // This line is bad. There is no way to know what will be the 'this'
                        // However, fixing it is a problem because the original intention is not known.
                        // So adding validthis is the best ot the worst options :(
                        /*jshint validthis: true */
                        return this.params[paramMatch[1]];
                    }

                    return value;
                }

                function getValueVariable(val) {
                    var objName, path;

                    if (/^@/.test(val)) {
                        objName = "params";
                        path = val.match(/^@(.*)/)[1];
                    } else {
                        objName = "data";
                        path = val;
                    }

                    var pathProperties = path.split("."),
                        propertyName = pathProperties[0],
                        finalVariable = [objName + (~propertyName.indexOf("**dot**") ? "['" + propertyName + "']" :
                        "." + propertyName)];

                    if (pathProperties.length > 1) {
                        for (var i = 1; i < pathProperties.length; i++) {
                            propertyName = pathProperties[i];
                            finalVariable.push(finalVariable[finalVariable.length - 1] +
                                (~propertyName.indexOf("**dot**") ? "['" + propertyName + "']" : "." + propertyName));
                        }
                    }

                    return finalVariable.join(" && ").replace(/\*\*dot\*\*/g, ".");
                }

                function getParamsAndFormatting(param) {
                    var paramWithFormatting = param.replace(/\\:/g, "*__*"),
                        params;

                    paramWithFormatting = paramWithFormatting.split(":");

                    var value = paramWithFormatting[0];
                    value = value.replace(/\\\./g, "**dot**");
                    params = value.split(/\s?\|\|\s?/);
                    for (var i = params.length - 1; i >= 0; i--) {
                        param = params[i];
                        if (!param) {
                            params.splice(i, 1);
                        } else {
                            params[i] = getValueVariable(param);
                        }
                    }

                    if (paramWithFormatting.length === 1) {
                        return {params: params};
                    }

                    var methodParams = paramWithFormatting.slice(2);
                    angular.forEach(methodParams, function (paramValue, i) {
                        methodParams[i] = '"' + paramValue.replace(/\*__\*/g, ":") + '"';
                    });

                    return {
                        params: params,
                        method: paramWithFormatting[1],
                        parameters: methodParams
                    };
                }

                var methods = {
                    // Exposing parsing methods for unit testing
                    // This is private, do not call it from other files!
                    _parseStringFormatters: parseStringFormatters,
                    arrays: {
                        areEqual: function (arr1, arr2) {
                            if (!arr1 || !arr2) {
                                throw new Error("Missing values to compare arrays.");
                            }

                            if (!angular.isArray(arr1) || !angular.isArray(arr2)) {
                                throw new Error("areEqual received non-array parameter(s).");
                            }

                            if (arr1.length !== arr2.length) {
                                return false;
                            }

                            for (var i = 0; i < arr1.length; i++) {
                                if (!methods.objects.areEqual(arr1[i], arr2[i])) {
                                    return false;
                                }
                            }

                            return true;
                        },
                        /**
                         *
                         * @param {Array} arr1
                         * @param {Array} arr2
                         * @returns {boolean}
                         * @description
                         * Verifies that all members in second array exist in first array
                         */
                        doesNotContain: function (arr1, arr2) {
                            var _errMsg = 'utils.arrays.doesNotContain: ';

                            // Validations
                            if (!angular.isArray(arr1)) {
                                throw new TypeError(_errMsg + 'first argument must be an array.');
                            }
                            if (!angular.isArray(arr2)) {
                                throw new TypeError(_errMsg + 'second argument must be an array.');
                            }

                            // Iterate through second array and find the indexOf for each member.
                            // If any of the members return indexof of 0 and up the method will return false,
                            // otherwise the method will return true.
                            for (var i = 0; i < arr2.length; i++) {
                                if (!!~arr1.indexOf(arr2[i])) {
                                    return false;
                                }
                            }

                            return true;
                        },
                        /**
                         *
                         * @param {Array} arr
                         * @param findFunction
                         * @returns {*}
                         * @description
                         * Takes an array and a function.
                         * Iterates through the array.
                         * The find function is invoked for each member of the array with the member.
                         * If the fundFunction returns true, the method returns the member.
                         * If findFunction does not return true for any of the iterations, the method returns null.
                         */
                        find: function (arr, findFunction) {

                            // Validations
                            var _errMsg = 'utils.arrays.find: ';
                            if (!angular.isArray(arr)) {
                                throw new TypeError(_errMsg + 'arr argument must be an array.');
                            }
                            if (!angular.isFunction(findFunction)) {
                                throw new TypeError(_errMsg + 'findFunction argument must be a function.');
                            }

                            var member;
                            for (var i = 0; i < arr.length; i++) {
                                member = arr[i];
                                if (findFunction(member)) {
                                    return member;
                                }
                            }

                            return null;
                        },
                        /**
                         * Given an array of objects, returns an array of unique values for the specified property.
                         * For example, for ([{ a: 2, b: 1}, { a: 3, b: 1}, { a: 2, b: 6 }], "a"), returns [2, 3]
                         * If the array is not of objects, the property is disregarded and an array of unique values
                         * is returned, for example:
                         * [1,2,4,2,4,6,1,2,1] returns [1,2,4,6]
                         * @param array An array of objects
                         * @param property The property to get unique values for
                         */
                        getUniqueValues: function (array, property) {
                            var index = {};
                            array.forEach(function (obj) {
                                var value;
                                if (obj) {
                                    if (angular.isObject(obj)) {
                                        value = obj[property];
                                    } else {
                                        value = obj;
                                    }

                                    if (value !== undefined) {
                                        index[value] = true;
                                    }
                                }
                            });

                            return Object.keys(index);
                        },
                        /**
                         * Replaces the array's member in the 'index' position with the specified newMembers.
                         * If multiple members are specified, the size of the array changes.
                         * @param arr The array in which to do the replacement
                         * @param index the position in the array that should be replaced
                         * @param newMembers The new members. Can be either a value, object or array. If array,
                         * the replacement puts all array members into the original array.
                         */
                        replace: function (arr, index, newMembers) {
                            if (newMembers.constructor !== Array) {
                                newMembers = [newMembers];
                            }

                            Array.prototype.splice.apply(arr, [index, 1].concat(newMembers));
                        },
                        shuffle: function (array) {
                            var currentIndex = array.length,
                                temporaryValue,
                                randomIndex;

                            // While there remain elements to shuffle...
                            while (0 !== currentIndex) {

                                // Pick a remaining element...
                                randomIndex = Math.floor(Math.random() * currentIndex);
                                currentIndex -= 1;

                                // And swap it with the current element.
                                temporaryValue = array[currentIndex];
                                array[currentIndex] = array[randomIndex];
                                array[randomIndex] = temporaryValue;
                            }

                            return array;
                        },
                        toSentence: function (array, connector, wrapper) {
                            var arrayCopy = angular.copy(array);

                            connector = connector || "and";

                            if (arrayCopy.length < 2) {
                                return arrayCopy.toString();
                            }

                            if (wrapper) {
                                arrayCopy.forEach(function (member, i) {
                                    arrayCopy[i] = wrapper + member + wrapper;
                                });
                            }

                            return arrayCopy.slice(0, -1).join(", ") + " " + connector + " " +
                                arrayCopy[arrayCopy.length - 1];
                        }
                    },
                    date: {
                        /**
                         * Used to match "4hours" or "-1month"
                         */
                        re_shiftDate: /^(-?\d+)(\w+)$/,
                        /**
                         * Shift a date object forward or backwards
                         *
                         * @param  {Object} dateObj Moment date object
                         * @param  {string} shiftBy Shift string (can be negative)
                         *     (e.g. "4hours", "-1month")
                         *     See: http://momentjs.com/docs/#/manipulating/add/
                         *
                         * @return {Object}         Shifted date object
                         */
                        shiftDate: function (dateObj, shiftBy) {
                            var addMatch = shiftBy.match(this.re_shiftDate);

                            // Validations
                            if (!addMatch) {
                                throw new Error("Invalid shiftBy string: " + shiftBy);
                            }
                            if (!moment.isMoment(dateObj)) {
                                throw new Error("dateObj must be a moment instance: " + dateObj);
                            }

                            return dateObj.add(Number(addMatch[1]), addMatch[2]);
                        },
                        /**
                         * Is the value a timestamp string
                         *
                         * @param  {*}       value Timestamp value
                         * @return {Boolean}       Is it a timestamp in seconds or milliseconds
                         */
                        isTimeStamp: function (value) {
                            if (typeof value !== 'string' && typeof value !== 'number') {
                                throw new TypeError("Timestap value must be a String or a Number; Value: " + value);
                            }

                            return /^(\d{10}|\d{13})$/.test(value);
                        },
                        compareDates: function (date1, date2) {
                            return date1.getYear() === date2.getYear() && date1.getMonth() === date2.getMonth() &&
                                date1.getDate() === date2.getDate();
                        },
                        diff: function (date1, date2, units) {
                            if (date1 === "now") {
                                date1 = this.getMoment('now').toDate();
                            }

                            if (date2 === "now") {
                                date2 = this.getMoment('now').toDate();
                            }

                            var milliseconds = Math.abs(date2 - date1);
                            if (!units || units === "milliseconds") {
                                return milliseconds;
                            }

                            var seconds = milliseconds / 1000;

                            if (units === "minutes") {
                                return Math.round(seconds / 60);
                            }

                            if (units === "hours") {
                                return Math.round(seconds / 3600);
                            }

                            if (units === "days") {
                                return Math.round(seconds / (3600 * 24));
                            }

                            if (units === "weeks") {
                                return Math.round(seconds / (3600 * 24 * 7));
                            }

                            if (units === "months") {
                                return Math.round(seconds / (3600 * 24 * 30));
                            }

                            if (units === "years") {
                                return Math.round(seconds / (3600 * 24 * 365));
                            }
                        },
                        /**
                         * Get a Moment object
                         *
                         * @param  {number|string|Object} date Input date
                         * @param  {boolean=} utc         Flag to define usage of moment with UTC
                         *     Use `null` to force the default setting
                         * @param  {string=}  parseFormat Specific parsing format for the input
                         *     See: http://momentjs.com/docs/#/parsing/string-format/
                         *
                         * @return {Object}       Moment object
                         */
                        getMoment: function (date, utc, parseFormat) {
                            // Handle default UTC value from `config`
                            if (typeof utc === 'undefined' || utc === null) {
                                utc = config.alwaysUtc || false;
                            }

                            // Cache a reference to Moment depending on UTC flag
                            var _moment = utc ? moment.utc : moment;

                            if (!date) {
                                date = 'now';
                            }

                            if (typeof date === "number") {
                                // Sometimes the time value is returned in seconds and not milliseconds
                                if (String(date).length === 10) {
                                    date = date * 1000;
                                }

                                return _moment(date);
                            }

                            if (typeof date === "string") {
                                // If date in format "+0d" or "-0h"
                                if (date === "now" || /^[\-\+]?0[smhd]$/.test(date)) {
                                    return _moment();
                                }

                                // If a string of a number date value, reasses as a number
                                if (/^\d+$/.test(date)) {
                                    return this.getMoment(Number(date));
                                }

                                // If date in format "YYYY-MM-DD"
                                if (/^\d{4}-\d{2}-\d{2}$/.test(date)) {
                                    var newDate = _moment(date, "YYYY-MM-DD");

                                    if (newDate.isValid()) {
                                        return newDate;
                                    }
                                }

                                // If date in format "4hours" or "-1month",
                                // return current time after shift
                                if (this.re_shiftDate.test(date)) {
                                    return this.shiftDate(_moment(), date);
                                }
                            }

                            // If already a moment object, return it
                            if (moment.isMoment(date)) {
                                return date;
                            }

                            // If a specific parsing format has been given
                            if (parseFormat && _moment(date, parseFormat).isValid()) {
                                return _moment(date, parseFormat);
                            }

                            // If a JS date object, wrap it with moment
                            // Or if it's a valid date after creation, use that
                            if (angular.isDate(date) || _moment(date).isValid()) {
                                return _moment(date);
                            }

                            throw new Error("Invalid date: " + date);
                        },
                        getDatesSpan: function (start, end) {
                            var firstDate = this.getMoment(start),
                                lastDate = this.getMoment(end);

                            if (!firstDate.isValid() || !lastDate.isValid()) {
                                return null;
                            }

                            var daysCount = Math.abs(firstDate.diff(lastDate, "days")),
                                dates = [];

                            for (var i = 0; i < daysCount; i++) {
                                dates.push(firstDate.add(1, "days").clone().toDate());
                            }

                            return dates;
                        },
                        localToUtc: function (date) {
                            var moment = this.getMoment(date);
                            if (!moment.isValid()) {
                                throw new Error("Invalid date: " + date);
                            }

                            moment.subtract(config.timezone, "hours");
                            return moment.toDate();
                        },
                        // ES6 Getter
                        get timezone() {
                            return config.timezone;
                        },
                        toUnixTimestamp: function (date) {
                            var moment = this.getMoment(date);
                            return Math.floor(moment.valueOf() / 1000);
                        },
                        utcToLocal: function (date) {
                            var moment = this.getMoment(date);
                            if (!moment.isValid()) {
                                throw new Error("Invalid date: " + date);
                            }

                            moment.add(config.timezone, "hours");
                            return moment.toDate();
                        },
                        prettyDate: function (diffSeconds, isShort) {
                            var day_diff = Math.floor(diffSeconds / 86400);
                            var units = {
                                long: {
                                    now: "< 1 minute",
                                    second: "1 second",
                                    seconds: " seconds",
                                    minute: "1 minute",
                                    minutes: " minutes",
                                    hour: "1 hour",
                                    hours: " hours",
                                    yesterday: "1 day",
                                    days: " days",
                                    week: " week",
                                    weeks: " weeks",
                                    month: "1 month",
                                    months: " months",
                                    years: " years"
                                },
                                short: {
                                    now: "< 1m",
                                    second: "1s",
                                    seconds: "s",
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

                            if (isNaN(day_diff) || day_diff < 0) {
                                return;
                            }

                            return (day_diff === 0 && (
                            diffSeconds < 1 && unitsToUse.now ||
                            diffSeconds === 1 && unitsToUse.second ||
                            diffSeconds < 60 && diffSeconds + unitsToUse.seconds ||
                            diffSeconds < 120 && unitsToUse.minute ||
                            diffSeconds < 3600 && Math.floor(diffSeconds / 60) + unitsToUse.minutes ||
                            diffSeconds < 7200 && unitsToUse.hour ||
                            diffSeconds < 86400 && Math.floor(diffSeconds / 3600) + unitsToUse.hours) ||
                            day_diff === 1 && unitsToUse.yesterday ||
                            day_diff < 14 && day_diff + unitsToUse.days ||
                            day_diff < 31 && Math.ceil(day_diff / 7) + unitsToUse.weeks ||
                            day_diff < 62 && unitsToUse.month ||
                            day_diff < 365 && Math.floor(day_diff / 30.416) + unitsToUse.months ||
                            Math.floor(day_diff / 365) + unitsToUse.years);
                        },
                        toSeconds: function (value, units) {
                            switch (units) {
                                case "milliseconds":
                                    return value / 1000;
                                case "seconds":
                                    return value;
                                case "minutes":
                                    return value * 60;
                                case "hours":
                                    return value * 3600;
                                case "days":
                                    return value * 3600 * 24;
                                default:
                                    throw new Error("Unsupported unit, '" + units +
                                        "', expecting milliseconds, seconds, minutes, hours or days.");
                            }
                        }
                    },
                    http: {
                        wrappedHttpGet: function (url, config) {
                            return $http.get(url, config).then(function (response) {
                                return response.data;
                            });
                        },
                        wrappedHttpPost: function (url, config) {
                            return $http.post(url, config).then(function (response) {
                                return response.data;
                            });
                        }
                    },
                    numbers: {

                        parse: function (value) {
                            if (typeof(value) === "number") {
                                return value;
                            }

                            if (typeof(value) === "string") {
                                if (/^(\-|\+)?([0-9\.]+|Infinity)$/.test(value)) {
                                    return value;
                                }
                            }

                            return NaN;
                        },
                        /**
                         *
                         * @param {*} value
                         * @returns {string}
                         * @description
                         * Takes a value argument.
                         * It tries to parse it to integer.
                         * If the parse fails, RangeError is thrown.
                         * If the parse is successful a string is returned by the following value:
                         * (n < 10^3) => n + ' B'
                         * else (n < 10^6) => (n/10^3).toFixed(2) + ' KB'
                         * else (n < 10^9) => (n/10^6).toFixed(2) + ' MB'
                         * else (n < 10^12) => (n/10^9).toFixed(2) + ' TB'
                         * Examples: bytesCount('999990') //returns '999.99 KB'
                         * Examples: bytesCount(1010000000000) //returns '999.99 GB'
                         */
                        bytesCount: function (value) {
                            if (!value) {
                                return value;
                            }

                            var count = parseInt(value, 10);
                            if (isNaN(count)) {
                                throw new RangeError('utils.numbers.bytesCount: parseInt(value, 10) returned NaN.');
                            }

                            return count < 1000 && count + " B" ||
                                (count = count / 1000) < 1000 && count.toFixed(2) + " KB" ||
                                (count = count / 1000) < 1000 && count.toFixed(2) + " MB" ||
                                (count = count / 1000) < 1000 && count.toFixed(2) + " GB" ||
                                (count = count / 1000) && count.toFixed(2) + " TB";
                        }
                    },
                    strings: {
                        /**
                         * Return the string with a capital letter
                         * @param str
                         * @returns {*}
                         */
                        capitalize: function (str) {
                            if (!str) {
                                return str;
                            }

                            if (!angular.isString(str)) {
                                throw new TypeError('utils.strings.capitalize: str is not a String');
                            }
                            return str.toLowerCase().replace(/\b\w/g,
                                //For each first character of any word - do "char".upparcase
                                function (c) {
                                    return c.toUpperCase();
                                });
                        },
                        /**
                         *
                         * @param {string|*} tableName
                         * @param {number=} minScore Optional
                         * @returns {string}
                         * @description
                         * Given a table name and a minimum event score, returns the optimized table's name,
                         * if it exists and the score fits.
                         */
                        getEventsTableName: function (tableName, minScore) {

                            // Validations
                            var _errMsg = 'utils.strings.getEventsTableName: ';
                            if (!tableName) {
                                throw new ReferenceError(_errMsg +
                                    'tableName argument must not be falsy.');
                            }

                            // If tableName is not in this whiteList array, the tableName is returned as-is
                            if (["authenticationscores", "sshscores", "vpndatares", "vpnsessiondatares"]
                                    .indexOf(tableName) === -1) {
                                return tableName;
                            }

                            if (minScore && angular.isNumber(minScore) && minScore >= 50) {
                                return tableName + "_top";
                            }

                            return tableName;
                        },
                        /**
                         * Test IPAddress
                         * @param str
                         * @returns {boolean}
                         */
                        isIpAddress: function (str) {
                            return ipAddressRegExp.test(str);
                        },


                        /**
                         * Return value of a shorter string acording to length
                         * @param str
                         * @param numOfChars
                         * @returns {string}
                         */
                        shortStr: function (str, numOfChars) {
                            if (str.length > 0) {
                                if (str.length > numOfChars) {
                                    return str.substring(0, numOfChars - 3) + "...";
                                } else {
                                    return str;
                                }
                            }
                        },
                        padLeft: function (str, length, padCharacter) {
                            str = String(str);
                            var padLength = length - str.length;
                            if (padLength <= 0) {
                                return str;
                            }

                            var pad = [];
                            while (pad.length < padLength) {
                                pad.push(padCharacter);
                            }

                            return pad.join("") + str;
                        },
                        /**
                         * @param value The string to parse (placeholders using {{}} )
                         * @param data  Parameters to use to replace placeholders
                         * @param params    Parameters to use to replace placeholders
                         * @param index
                         * @param dataOverrideParams    In case the same parameters exists both in the data and params,
                         * use the data's (default is false)
                         * @returns {*} The parsed string
                         */
                        parseValue: function (value, data, params, index, dataOverrideParams) {
                            if (!value) {
                                return "";
                            }

                            if (!parsedStringTest.test(value)) {
                                return value;
                            }

                            var cachedParser = stringParsers[value];
                            if (!cachedParser) {
                                var foundVars = false,
                                    escapedValue = value.replace(/\"/g, "\\\"");

                                var parserStr = escapedValue.replace(/\{\{([^\}]+)\}\}/g, function (match, variable) {
                                    foundVars = true;

                                    var paramsAndFormatting = getParamsAndFormatting(variable);
                                    if (paramsAndFormatting.method) {
                                        return '" + parsers.removeEmptyValue.call({ originalContext: this }, parsers.' +
                                            paramsAndFormatting.method + '.apply({ data: data, params: params, ' +
                                            'index: index, originalContext: this }, [' +
                                            paramsAndFormatting.params.join(" || ") + ', ' +
                                            paramsAndFormatting.parameters.join(",") +
                                            '].map(getParamValueForParser.bind({ params: params })))) + "';
                                    } else {
                                        return '" + parsers.removeEmptyValue.call({ originalContext: this }, ' +
                                            paramsAndFormatting.params.join(" || ") + ') + "';
                                    }

                                });

                                if (!foundVars) {
                                    return value;
                                }

                                parserStr = 'try{ return "' + parserStr +
                                    '"; } catch(e){ console.error("Error parsing string \'' + value +
                                    '\': ", e.message); throw e; }';
                                /*jslint evil: true */
                                //The following code causes: The Function constructor is a form of eval.
                                cachedParser = stringParsers[value] = new Function("data", "params", "index",
                                    "parsers", "getParamValueForParser", parserStr);
                            }

                            /*
                             IMPORTANT: This is a hack. Originally we used only the "data" to fill the placeholders
                             in the value string, but after adding also the "params", we didn't handle the scenario of
                             properties with the same key that appear both in "data" and "params". In order to avoid
                             major changes we left the default to be "params" overrides "data", but left an option to
                             change it. We should probably try to let the "data" override the "param"
                             */
                            if (dataOverrideParams) {
                                // In case the same parameters exists both in the data and params, use the data's
                                return cachedParser(angular.extend({}, params, data), params || {}, index,
                                        parseStringFormatters, getParamValueForParser) || "";
                            } else {
                                // In case the same parameters exists both in the data and params, use the param's
                                return cachedParser(angular.extend({}, data, params), params || {}, index,
                                        parseStringFormatters, getParamValueForParser) || "";
                            }


                        }
                    },
                    duration: {

                        /**
                         gets string of format hh:mm:ss and return number that represent duration in decimal
                         notation (hours scope).
                         */
                        durationToNumber: function (value, units) {
                            if (value === undefined || value === 'undefined' || value === null) {
                                return 0;
                            }
                            var duration = moment.duration(value, units);
                            switch (units) {
                                case "milliseconds":
                                    return duration.asMilliseconds();
                                case "seconds":
                                    return duration.asSeconds();
                                case "minutes":
                                    return duration.asMinutes();
                                case "hours":
                                    return duration.asHours().toFixed(2);
                                case "days":
                                    return duration.asDays().toFixed(2);
                                default:
                                    throw new Error("Unsupported unit, '" + units + "', expecting milliseconds, " +
                                        "seconds, minutes, hours or days.");
                            }
                        },
                        /**
                         * Formats an int representing number of seconds into a time string, hh:mm:ss
                         * @param diffSeconds
                         */
                        prettyTime: function (diffSeconds, units) {
                            // if no duration in source data - like to show the value as "" -
                            // which will be translated to N/A
                            if (diffSeconds === null || diffSeconds === undefined) {
                                return "";
                            } else {
                                var duration = moment.duration(Number(diffSeconds), units);
                                var days = duration.days();
                                //should never happened in our product - all our sessions are defined as less
                                // than 24 hours
                                if (days && days > 0) {
                                    return days + "d";
                                } else {
                                    var hours = duration.hours(),
                                        minutes = duration.minutes(),
                                        seconds = duration.seconds();
                                    if (hours || minutes || seconds) {
                                        return methods.strings.padLeft(hours, 2, "0") + ":" +
                                            methods.strings.padLeft(minutes, 2, "0") + ":" +
                                            methods.strings.padLeft(seconds, 2, "0");
                                    } else {
                                        //if session duration is less than 1 sec - will shown as 0 second
                                        return "00:00:00";
                                    }
                                }
                            }
                        }
                    },
                    objects: {
                        arrayToObject: function (arr, property) {
                            var obj = {};

                            angular.forEach(arr, function (member) {
                                var memberPropertyValue = member[property];
                                if (memberPropertyValue) {
                                    obj[memberPropertyValue] = member;
                                }
                            });

                            return obj;
                        },
                        areEqual: function (a, b) {
                            if (typeof(a) !== typeof(b)) {
                                return false;
                            }

                            if (!a && b || a && !b) {
                                return false;
                            }

                            if (angular.isArray(a) !== angular.isArray(b)) {
                                return false;
                            }

                            if (angular.isArray(a) && angular.isArray(b)) {
                                if (a.length !== b.length) {
                                    return false;
                                }

                                for (var i = 0; i < a.length; i++) {
                                    if (!methods.objects.areEqual(a[i], b[i])) {
                                        return false;
                                    }
                                }

                                return true;
                            } else if (angular.isObject(a)) {
                                if (a.constructor !== b.constructor) {
                                    return false;
                                }

                                if (angular.isDate(a) && angular.isDate(b)) {
                                    return a.valueOf() === b.valueOf();
                                }

                                if (Object.keys(a).length !== Object.keys(b).length) {
                                    return false;
                                }

                                for (var p in a) {
                                    if (a.hasOwnProperty(p)) {
                                        if (!methods.objects.areEqual(a[p], b[p])) {
                                            return false;
                                        }
                                    }
                                }

                                return true;
                            } else {
                                return a === b;
                            }
                        },
                        /**
                         * Return true if obj contains any of the specified param names
                         * @param obj
                         * @param paramNames
                         */
                        containsAnyParam: function (obj, paramNames) {
                            for (var i = 0; i < paramNames.length; i++) {
                                if (obj[paramNames[i]] !== undefined) {
                                    return true;
                                }
                            }

                            return false;
                        },
                        containsAllParams: function (obj, paramNames) {
                            for (var i = 0, paramName; (!!(paramName = paramNames[i])); i++) {
                                if (!obj[paramName]) {
                                    return false;
                                }
                            }

                            return true;
                        },
                        /**
                         * DEEP copy! Muaahaaahaahhh!
                         * @param obj
                         * @returns {*|void}
                         */
                        copy: function (obj) {
                            return methods.objects.extend({}, obj);
                        },
                        /**
                         * Extends objects, uses jQuery.extend, since angular.extend doesn't have deep-extend support.
                         * @param args
                         * @returns {*|void}
                         */
                        extend: function (args) {
                            return jQuery.extend.apply(this, [true].concat(Array.prototype.slice.call(arguments, 0)));
                        },
                        getObjectByPath: function (rootObj, path) {
                            path = path || '';
                            var parts = path.split("."),
                                obj = rootObj;

                            for (var i = 0, part; (part = parts[i]) !== undefined; i++) {
                                obj = obj[part];
                                if (obj === null || obj === undefined) {
                                    return obj;
                                }
                            }

                            return obj;
                        },
                        toArray: function (obj, formatter) {
                            var arr = [];
                            for (var p in obj) {
                                if (obj.hasOwnProperty(p)) {
                                    arr.push(formatter ? formatter(p, obj[p]) : obj[p]);
                                }
                            }
                            return arr;
                        }
                    },
                    regexp: {
                        patterns: {
                            IP: /^(\d{1,3}\.){3}(\d{1,3})$/
                        }
                    },
                    url: {
                        escapeUrl: function (url) {
                            var parts = url.split("?");

                            if (parts.length > 1) {
                                var params = parts[1].split("&"),
                                    escapedParams = [];

                                params.forEach(function (param) {
                                    var paramSplit = param.match(/^([\w\.]+=)(.*)$/);
                                    if (paramSplit) {
                                        escapedParams.push(paramSplit[1] + encodeURIComponent(paramSplit[2]));

                                    } else {
                                        escapedParams.push(param);
                                    }
                                });

                                return parts[0] + "?" + escapedParams.join("&");
                            }

                            return url;
                        },
                        getQuery: function (params) {
                            var paramValue,
                                encodedParams = [];

                            for (var paramName in params) {
                                if (params.hasOwnProperty(paramName)) {
                                    paramValue = params[paramName];

                                    if (angular.isObject(paramValue)) {
                                        paramValue = JSON.stringify(paramValue);
                                    }

                                    if (paramValue !== null && paramValue !== undefined && paramValue !== "") {
                                        encodedParams.push([paramName, encodeURIComponent(paramValue)].join("="));
                                    }
                                }
                            }

                            return encodedParams.join("&");
                        },
                        getQueryParams: function () {
                            var queryParams = window.location.search;
                            return this.parseUrlParams(queryParams);
                        },
                        //given a url string parse it to an object where each url parameter is mapped to a property
                        // in the return object
                        parseUrlParams: function (url) {
                            var params = {};

                            if (url) {
                                // Discard the URL prefix (take only the parameters)
                                var index = url.indexOf("?");
                                var paramKeyValues = index >= 0 ? url.substring(index + 1).split("&") : [];
                                angular.forEach(paramKeyValues, function (keyValue) {
                                    if (keyValue) {
                                        var parts = keyValue.split("=");
                                        var decoded = decodeURIComponent(parts[1]);
                                        // Do not add key-value pair if decoding failed
                                        if (decoded) {
                                            params[parts[0]] = decoded;
                                        }
                                    }
                                });
                            }

                            return params;
                        },
                        haveTheSameHash: function (url1, url2) {
                            var hash1 = url1.match(hashRegExp),
                                hash2 = url2.match(hashRegExp);

                            if (hash1 && hash2) {
                                return hash1[0] === hash2[0];
                            }

                            return false;
                        }
                    }
                };

                return methods;
            }]);
}());
