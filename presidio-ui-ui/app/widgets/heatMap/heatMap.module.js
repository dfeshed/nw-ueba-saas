(function () {
    'use strict';

    angular.module("HeatMapWidget", ["Utils", "Colors", "Widgets"]).run(["utils", "widgetViews",
        function (utils, widgetViews) {

            /**
             * Return the items of the legend from the widget settings
             * @param view
             * @returns {*|Array}
             */
            function getLegendItems (view) {
                return view.settings.legend || [];

            }

            function heatMapDataParser (view, data, params) {
                var allColumnsArr = getColumnsObj(view, data, params);
                verifyThatAllHoursExists(view, allColumnsArr, data);
                var rowsArr = getRowsArr(view, data, params, allColumnsArr);

                return {
                    columns: allColumnsArr,
                    rows: rowsArr,
                    columnsOnePercentValue: getColumnsPercentValue(allColumnsArr,
                        view.settings.behavior.columns.percentCalculationMethod),
                    legend: {
                        items: getLegendItems(view)
                    }
                };
            }

            /**
             * This function creates an array of columns by the data that being retrieved from the DB
             * This array is being used for building the columns, and a copy of the array is being
             * created in each of the row object
             * @param view
             * @param data
             * @param params
             * @returns {Array}
             */
            function getColumnsObj (view, data, params) {
                var columnsObj = {},
                    arrToReturn = [];

                for (var i = 0; i < data.length; i++) {
                    var item = data[i];
                    var columnName = utils.strings.parseValue(view.settings.column, item, params);
                    if (!columnsObj[columnName]) {
                        // we do this only to check that we are not repeating ourselves
                        columnsObj[columnName] = {name: columnName || "Unknown", value: 0, valueSum: 0, count: 0};
                        arrToReturn.push(columnsObj[columnName]);
                    }
                }
                return arrToReturn;
            }

            /**
             * This function creates the array of rows by using the data that being retrieved from the DB
             * @param view
             * @param data
             * @param params
             * @param allColumnsArr
             * @returns {Array}
             */
            function getRowsArr (view, data, params, allColumnsArr) {
                var rowsArr = [],
                    rowsObj = {};
                angular.forEach(data, function (item) {

                    if (typeof view.settings.row === "object") {
                        // we don't specifically declare the row labels, every object in the json object which is not
                        // label is a row
                        angular.forEach(item, function (value, key) {
                            if (view.settings.row[key]) {
                                var rowItem = {
                                    key: key,
                                    name: view.settings.row[key],
                                    count: 0,
                                    columns: angular.copy(allColumnsArr)
                                };
                                var columnName = utils.strings.parseValue(view.settings.column, item, params);

                                if (!rowsObj[rowItem.name]) {
                                    rowsObj[rowItem.name] = true;
                                    setColumnsValue(view, rowItem, columnName, item, allColumnsArr);
                                    rowsArr.push(rowItem);
                                } else {
                                    var rowToChange = rowsArr.filter(function (row) {
                                        return row.name === rowItem.name;
                                    })[0];
                                    setColumnsValue(view, rowToChange, columnName, item, allColumnsArr);
                                }
                            }

                        });

                    } else {
                        //This mean we specifically declare the value of the rows
                        // like this feature was designed in the first time

                        // we building the row item and adding a copy of the "all columns object" to it
                        var rowItem = {
                            name: utils.strings.parseValue(view.settings.row, item, params),
                            count: 0,
                            columns: angular.copy(allColumnsArr)
                        };
                        // we use the current column name  and send it to the setColumnsValue()
                        var columnName = utils.strings.parseValue(view.settings.column, item, params);

                        if (!rowsObj[rowItem.name]) {
                            rowsObj[rowItem.name] = true;
                            setColumnsValue(view, rowItem, columnName, item, allColumnsArr);
                            rowsArr.push(rowItem);
                        } else {
                            var rowToChange = rowsArr.filter(function (row) {
                                return row.name === rowItem.name;
                            })[0];
                            setColumnsValue(view, rowToChange, columnName, item, allColumnsArr);
                        }
                    }
                });

                return rowsArr;
            }

            /**
             * This function used the row and mark the values of the heat in each row.columns[n]
             * when there is no value it will mark 1, the idea is that the colors spectrum will be
             * from 0-1.
             * @param view
             * @param rowItem
             * @param columnName
             * @param dataItem
             * @param allColumnsArr
             */
            function setColumnsValue (view, rowItem, columnName, dataItem, allColumnsArr) {

                rowItem.columns.map(function (column) {
                    if (column.name === columnName) {
                        var value;
                        if (typeof view.settings.row === "object") {
                            value = dataItem[rowItem.key];
                        } else {
                            value = utils.strings.parseValue(view.settings.value, dataItem);
                        }

                        rowItem.count++;

                        if (value !== "") {
                            column.value = Number(value);
                        } else {
                            column.value = 1;
                        }

                        if (column.value > 0) {
                            // we updating the value in the main column object
                            allColumnsArr.map(function (c) {
                                if (c.name === columnName) {
                                    c.count++;
                                    c.valueSum += column.value;
                                }
                            });
                        }
                    }
                    column.rowName = rowItem.name;
                });
            }

            /**
             * When defined at the settings, we calculate the percent of each column and
             * return it's value
             * @param allColumnsArr
             * @param percentCalculationMethod
             * @returns {number}
             */
            function getColumnsPercentValue (allColumnsArr, percentCalculationMethod) {
                var sum = 0;
                angular.forEach(allColumnsArr, function (c) {
                    sum += c[percentCalculationMethod];
                });
                return Math.round(100 / sum);
            }


            /**
             * This function related to vpn hours display only,
             * It basically add columns of hours where there are non to make the board (heat map) look like
             * a 24 hours board instead of dependency over the data.
             * @param view
             * @param columns
             */
            function verifyThatAllHoursExists (view, columns) {
                if (view.settings.showHours === true) {
                    var hours = {
                        "00:00": 0, "01:00": 0, "02:00": 0, "03:00": 0, "04:00": 0, "05:00": 0,
                        "06:00": 0, "07:00": 0, "08:00": 0, "09:00": 0, "10:00": 0, "11:00": 0,
                        "12:00": 0, "13:00": 0, "14:00": 0, "15:00": 0, "16:00": 0, "17:00": 0,
                        "18:00": 0, "19:00": 0, "20:00": 0, "21:00": 0, "22:00": 0, "23:00": 0
                    };

                    angular.forEach(columns, function (c) {
                        hours[c.name] = 1;
                    });

                    var count = 0;
                    angular.forEach(hours, function (value, key) {

                        if (value === 0) {
                            var column = {
                                count: 0,
                                name: key,
                                value: 0,
                                valueSum: 0
                            };
                            columns.splice(count, 0, column);

                        }
                        count++;
                    });
                }
            }

            widgetViews.registerView("heatMap", {dataParser: heatMapDataParser});

        }]);
}());
