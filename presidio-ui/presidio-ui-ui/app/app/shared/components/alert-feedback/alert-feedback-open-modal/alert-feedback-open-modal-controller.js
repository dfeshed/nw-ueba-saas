(function () {
    'use strict';

    function OpenAlertModalInstanceCtrl ($scope, $modalInstance, inputParams) {
        this.$scope = $scope;
        this.$modalInstance = $modalInstance;
        this.inputParams = inputParams;

        this.init();
    }

    angular.extend(OpenAlertModalInstanceCtrl.prototype, {

        /**
         * When user click on OK button-
         * resolve the modal promise and send the model (the feedback of the alert)
         */
        clickOk: function () {
            this.$modalInstance.close(this.model);
        },

        /**
         * When user click on cancel button-
         * resolve the modal promise and return 'cancel'
         */
        clickCancel: function () {
            this.$modalInstance.dismiss('cancel');
        },

        /**
         * Inits the controller
         */
        init: function () {
            var ctrl = this;
            ctrl.model = {
                inputParams: ctrl.inputParams
            };
        }
    });

    OpenAlertModalInstanceCtrl.$inject = [
        '$scope',
        '$modalInstance',
        'inputParams'
    ];

    angular.module('Fortscale.shared.components.alertFeedback').controller('OpenAlertModalInstanceCtrl',
        OpenAlertModalInstanceCtrl);

}());
