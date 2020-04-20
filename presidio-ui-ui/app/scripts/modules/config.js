(function () {
    "use strict";

    var config = {
        dateFormat: "MMM DD, YYYY",
        hourFormat: "HH:mm:ss",
        timestampFormat: "MMM DD YYYY, HH:mm:ss",
        fallbackFormat: "MM/DD/YYYY HH:mm:ss",
        timezone: localStorage.timezone ? parseInt(localStorage.timezone, 10) : (new Date()).getTimezoneOffset() / -60,
        alwaysUtc: true,
        //used to determine threshold for notifying potentially large queries
        scoreThreshold: 50,
        daysThreshold: 30,
        popupLargeQueryMsg: "Please notice that this change might cause the query to take longer than usual. " +
        "To send the query, please select 'YES' otherwise select 'NO'"
    };
    config.popupScoreMessage = "You have chosen to filter the results by a Minimum Score smaller than <b>" +
        config.scoreThreshold + "</b>. " + config.popupLargeQueryMsg;
    config.popupDaysMessage = "You have chosen to filter the results by a Time Range greater than <b>" +
        config.daysThreshold + "</b> days. " + config.popupLargeQueryMsg;

    angular.module("Config", [])
        .constant("config", config)
        .constant("configFlags", {
            qa: !!localStorage.qa,
            verbose: !!localStorage.verbose,
            mockData: !!localStorage.allowMockData
        });

}());
