(function () {
    'use strict';
    /**
     * This modal present error message after chagne alert status failed.
     * @param $scope
     * @param $modalInstance
     * @param inputParams
     * @constructor
     */
    function FeedbackFailureModalInstanceCtrl($scope, $modalInstance, inputParams){
        this.$scope = $scope;
        this.$modalInstance = $modalInstance;
        this.inputParams = inputParams;

        this.init();
    }

    angular.extend(FeedbackFailureModalInstanceCtrl.prototype, {

        /**
         * When user click on OK button-
         * resolve the modal promise close the modal
         */
        clickOk : function () {
            this.$modalInstance.close();
        },

        /**
         * Init the controller
         */
        init: function(){
            var ctrl = this;
            ctrl.newAlertStatus = ctrl.inputParams.newAlertStatus;
            ctrl.specificBodyMessage = ctrl.inputParams.specificBodyMessage;
        }
    });

    FeedbackFailureModalInstanceCtrl.$inject = [
        '$scope',
        '$modalInstance',
        'inputParams'
    ];

    angular.module('Fortscale.shared.components.alertFeedback')
        .controller('FeedbackFailureModalInstanceCtrl', FeedbackFailureModalInstanceCtrl);

}());
