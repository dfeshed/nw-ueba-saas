(function () {
    'use strict';

    /**
     * Post-processing for data returned by a report call. The process is define within the report itself.
     * @param $q
     * @param DAL
     * @param utils
     * @returns {{processData: processData}}
     */
    function reportsProcess ($q, DAL, utils) {
        var processes = {
            add: function (results, params) {
                if (!params || !params.field) {
                    throw new Error("Can't add results - no field specified.");
                }

                if (!results.data || !results.data.length) {
                    return null;
                }

                var addResult = results.data[0][params.field],
                    result = params.extend || {};

                if (results.data.length > 1) {
                    for (var i = 1; i < results.data.length; i++) {
                        addResult += results.data[i][params.field];
                    }
                }

                result[params.field] = addResult;
                return {data: [result], total: 1, time: results.time};
            },
            combine: function (results, params) {
                var index = {},
                    indexedResults = [];

                angular.forEach(results.data, function (result) {
                    var groupByValue = result[params.groupBy];
                    var indexObj = index[groupByValue];
                    if (!indexObj) {
                        indexObj = index[groupByValue] = angular.copy(params.defaultValue);
                        indexObj[params.groupBy] = groupByValue;
                    }

                    for (var fieldName in params.fields) {
                        if (params.fields.hasOwnProperty(fieldName)) {
                            indexObj[result[fieldName]] = result[params.fields[fieldName]];
                        }
                    }
                });

                for (var groupByValue in index) {
                    if (index.hasOwnProperty(groupByValue)) {
                        indexedResults.push(index[groupByValue]);
                    }
                }

                return $q.when({data: indexedResults, total: results.total, time: results.time});
            },
            extend: function (results, params) {
                if (!params || !Object.keys(params).length) {
                    return results;
                }

                results.data.forEach(function (item) {
                    angular.extend(item, params);
                });

                return results;
            },
            getUsersDetails: function (results, params) {
                var deferred = $q.defer(),
                    usernames = [];

                if (!params || !params.userField || !params.userType) {
                    deferred.reject("Missing parameters for getUserDetails.");
                    return deferred.promise;
                }

                angular.forEach(results.data, function (item) {
                    usernames.push(item[params.userField]);
                });

                if (!usernames.length) {
                    deferred.resolve({data: [], total: 0});
                }

                DAL.reports.runReport({
                    endpoint: {
                        entity: "app",
                        id: params.userType,
                        method: "usersDetails",
                        usernames: usernames.join(",")
                    },
                    "mock_data": "userFind"
                }).then(function (userDetailsResults) {
                    angular.forEach(userDetailsResults.data, function (userDetails) {
                        for (var i = 0, item; i < results.data.length; i++) {
                            item = results.data[i];

                            if ((userDetails.samacountName &&
                                userDetails.samacountName.toLowerCase() === item[params.userField].toLowerCase()) ||
                                (userDetails.adUserPrincipalName && userDetails.adUserPrincipalName.toLowerCase() ===
                                item[params.userField].toLowerCase() || (userDetails.name &&
                                userDetails.name.toLowerCase() === item[params.userField].toLowerCase()))) {
                                item.userDetails = userDetails;
                                break;
                            }
                        }
                    });

                    deferred.resolve(results);
                }, deferred.reject);

                return deferred.promise;
            },
            /*
             *
             *	Allow group by aggregation also in the frontend - ideally this functionality will move completely to
             *	the backend, cause of time implementation issues currently implemented in the frontend.
             *
             *
             */
            groupBy: function (results, params) {
                var index = {},
                    indexedResults = [];

                angular.forEach(results.data, function (result) {

                    var resultProperties = Object.keys(result);
                    var groupByValue = "";
                    // create id for all records that are group together
                    // support multiple group by fields, from which this id is created.
                    angular.forEach(params.groupBy, function (groupByColumn) {
                        groupByValue += "_" + result[groupByColumn];
                    });

                    // add/return the id to the distinct record map.
                    var indexObj = index[groupByValue];
                    if (!indexObj) {
                        indexObj = index[groupByValue] = {};
                    }

                    //remove group by fields values from original record.
                    angular.forEach(params.groupBy, function (groupByColumn) {
                        indexObj[groupByColumn] = result[groupByColumn];
                        resultProperties.splice(resultProperties.indexOf(groupByColumn), 1);
                    });

                    if (params.fields) {
                        // for all aggregation fields
                        angular.forEach(params.fields, function (field) {
                            // get field name and value and destination field name for the joined record.
                            var fieldValue = result[field.fieldName];
                            var fieldName = field.fieldName;
                            // remove value from original record if needed.
                            if (field.fieldNewName === undefined) {
                                var propertyIndex = resultProperties.indexOf(fieldName);
                                if (~propertyIndex) {
                                    resultProperties.splice(propertyIndex, 1);
                                }
                            }
                            else {
                                fieldName = field.fieldNewName;
                            }
                            //allow pivot operation uses the pivot field to get the prefix for the column name as
                            // addition to the aggregated field. the new field name pattern will be
                            // pivotFieldValue_aggregatedFieldName and the operation on the value can be any of the
                            // basic aggregation operations.
                            if (field.fieldFunc === "pivot") {
                                if (field.pivotFieldName !== undefined) {
                                    var pivotPropertyIndex = resultProperties.indexOf(field.pivotFieldName);
                                    if (~pivotPropertyIndex) {
                                        var pivotField = result[field.pivotFieldName];
                                        resultProperties.splice(pivotPropertyIndex, 1);
                                        fieldName = pivotField + "_" + fieldName;
                                        aggregateOperation(indexObj, fieldName, fieldValue, field.fieldPivotFunc);
                                    }
                                }
                            }
                            //any basic aggregation operation
                            else {
                                aggregateOperation(indexObj, fieldName, fieldValue, field.fieldFunc,
                                    result[field.referenceFieldName], field.referenceValue);
                            }
                        });
                    }

                    // add all other values haven't been handled until now
                    if (resultProperties.length) {
                        resultProperties.forEach(function (propertyName) {
                            indexObj[propertyName] = result[propertyName];
                        });
                    }
                });

                // add all joined records to new result set
                for (var groupByValue in index) {
                    if (index.hasOwnProperty(groupByValue)) {
                        indexedResults.push(index[groupByValue]);
                    }
                }

                return $q.when({data: indexedResults, total: results.total, time: results.time});
            },
            groupByField: function (results, params) {
                function groupRows (fieldValue) {
                    var rows = [];

                    for (var i = originalData.length - 1, row; !!(row = originalData[i]); i--) {
                        if (row[params.field] === fieldValue) {
                            rows.splice(0, 0, originalData.splice(i, 1)[0]);
                        }
                    }

                    newResults = newResults.concat(rows);
                }

                var originalData = angular.copy(results.data);

                if (!originalData || originalData.length <= 2) {
                    return results;
                }

                var newResults = originalData.splice(0, 1);

                while (originalData.length) {
                    groupRows(newResults[newResults.length - 1][params.field]);
                    if (originalData.length) {
                        newResults.push(originalData.splice(0, 1)[0]);
                    }
                }

                return {data: newResults, total: results.total, time: results.time};
            },
            map: function (results, params) {
                var mappedData = [];

                angular.forEach(results.data, function (item) {
                    mappedData.push(mapObj(params.map, item));
                });

                return {data: mappedData, total: results.total, time: results.time};
            },
            sort: function (results, params) {
                var fields = params.fields || [params.field];

                var resultsCopy = angular.copy(results),
                    sortFunc = params.direction && params.direction.toLocaleLowerCase() === "desc" ?
                        function (a, b) {
                            var values = getValuesToCompare(a, b);

                            if (values.a === values.b) {
                                return 0;
                            }

                            if (!values.a && values.a !== 0 && values.b) {
                                return -1;
                            }

                            if (!values.b && values.b !== 0 && values.a) {
                                return 1;
                            }

                            return values.a < values.b ? 1 : -1;
                        } :
                        function (a, b) {
                            var values = getValuesToCompare(a, b);

                            if (values.a === values.b) {
                                return 0;
                            }

                            if (!values.a && values.a !== 0 && values.b) {
                                return 1;
                            }

                            if (!values.b && values.b !== 0 && values.a) {
                                return -1;
                            }

                            return values.a > values.b ? 1 : -1;
                        };

                resultsCopy.data = resultsCopy.data.sort(sortFunc);

                function getValuesToCompare (a, b) {
                    var fieldIndex = -1,
                        field,
                        aVal, bVal;

                    do {
                        fieldIndex++;
                        field = fields[fieldIndex];
                        if (field) {
                            aVal = a[field];
                            bVal = b[field];
                        }
                    }
                    while (field && aVal === bVal);

                    return {a: aVal, b: bVal};
                }

                return resultsCopy;
            },
            limit: function (results, params) {
                var resultsCopy = angular.copy(results);
                resultsCopy.data = resultsCopy.data.splice(0, params.numOfRows);
                return resultsCopy;
            }

        };

        function aggregateOperation (indexObj, fieldName, fieldValue, fieldFunc, referenceField, referenceValue) {
            if (fieldFunc === "case") {
                if (referenceValue === referenceField) {
                    indexObj[fieldName] = fieldValue;
                }
            }
            else if (indexObj[fieldName] === undefined) {
                indexObj[fieldName] = fieldValue;
            } else {
                if (fieldFunc === "sum") {
                    indexObj[fieldName] += fieldValue;
                } else if (fieldFunc === "max") {
                    indexObj[fieldName] = Math.max(indexObj[fieldName], fieldValue);
                } else if (fieldFunc === "min") {
                    indexObj[fieldName] = Math.max(indexObj[fieldName], fieldValue);
                } else if (fieldFunc === "count") {
                    indexObj[fieldName] += 1;
                } else if (fieldValue) {
                    indexObj[fieldName] = fieldValue;
                }
            }
        }

        function mapObj (map, data) {
            var mappedObj = {},
                pValue;

            function populateMappedObj (propertyName) {
                mappedObj[p][propertyName] = data[propertyName];
            }

            for (var p in map) {
                if (map.hasOwnProperty(p)) {
                    pValue = map[p];
                    if (typeof(pValue) === "string") {
                        mappedObj[p] = utils.strings.parseValue(pValue, data);
                    } else if (angular.isArray(pValue)) {
                        mappedObj[p] = {};
                        angular.forEach(pValue, populateMappedObj);
                    }
                    else if (angular.isObject(pValue)) {
                        mappedObj[p] = mapObj(pValue, data);
                    }
                }
            }

            return mappedObj;
        }


        return {
            processData: function (processId, results, params) {
                var process = processes[processId];
                if (!process) {
                    throw new Error("Invalid process, '" + processId + "'.");
                }

                var data = results.data && angular.isArray(results.data) ? results : {data: [results]};

                return $q.when(process(data, params));
            }
        };
    }

    reportsProcess.$inject = ["$q", "DAL", "utils"];

    angular.module("Reports").factory("reportsProcess", reportsProcess);
})();
