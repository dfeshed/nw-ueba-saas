(function () {
    'use strict';

    angular.module("Fortscale").controller("MonitoringController", ["$scope", "reports", function ($scope, reports) {
        $scope.getJob = function (job) {
            if (!job.steps) {
                reports.runReport(getJobDataReport(job.id), job, true).then(function (results) {
                    job.steps = results.steps;
                });
            }

            job.isOpen = !job.isOpen;
        };

        function getJobDataReport (jobId) {
            return {
                "mock_data": "monitor_" + jobId
            };
        }
    }]);
}());
