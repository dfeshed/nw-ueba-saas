(function () {
    'use strict';
    /**
     * This modal show success message after alert was closed or reopened
     * @param $scope
     * @param $modalInstance
     * @param inputParams
     * @constructor
     */
    function FeedbackResultModalInstanceCtrl($scope, $modalInstance, inputParams){
        var ctrl = this;

        ctrl.$scope = $scope;
        ctrl.$modalInstance = $modalInstance;
        ctrl.inputParams = inputParams;

        /**
         * Used to fixate activation context and invoke _removeClickListener and _closeModal
         */
        ctrl.closeModal = function () {
            ctrl._removeClickListener();
            ctrl._closeModal();
        };

        ctrl.init();
    }

    angular.extend(FeedbackResultModalInstanceCtrl.prototype, {

        /**
         * Closes the modal
         *
         * @private
         */
        _closeModal: function () {
            this.$modalInstance.dismiss();
        },

        /**
         * Removes click event listener
         *
         * @private
         */
        _removeClickListener: function () {
            window.removeEventListener('click', this.closeModal, true);

        },

        /**
         * Used to cleanup all listeners
         *
         * @private
         */
        _cleanup: function () {
            this._removeClickListener();
        },

        /**
         * Initates watches
         *
         * @private
         */
        _initWatches: function () {
            window.addEventListener('click', this.closeModal, true);
            this.$scope.$on('$destroy', this._cleanup.bind(this));
        },

        /**
         * Init the controller
         */
        init: function(){
            var ctrl = this;
            ctrl.newAlertStatus = ctrl.inputParams.newAlertStatus;
            ctrl.specificBodyMessage = ctrl.inputParams.specificBodyMessage;

            ctrl._initWatches();
        }
    });

    FeedbackResultModalInstanceCtrl.$inject = [
        '$scope',
        '$modalInstance',
        'inputParams'
    ];

    angular.module('Fortscale.shared.components.alertFeedback')
        .controller('FeedbackResultModalInstanceCtrl', FeedbackResultModalInstanceCtrl);

}());
