(function () {
    'use strict';

    function CloseAlertModalInstanceCtrl($scope, $modalInstance, inputParams){
        this.$scope = $scope;
        this.$modalInstance = $modalInstance;
        this.inputParams = inputParams;

        this.init();
    }

    angular.extend(CloseAlertModalInstanceCtrl.prototype, {

        /**
         * When user click on OK button-
         * resolve the modal promise and send the model (the feedback of the alert)
         */
        clickOk : function () {
            this.$modalInstance.close(this.model);
        },

        /**
         * When user click on cancel button-
         * resolve the modal promise and return 'cancel'
         */
        clickCancel : function () {
            this.$modalInstance.dismiss('cancel');
        },

        /**
         * Convert the options of the feedback from enum object to array of "key" & "value"
         * @param options
         * @returns {Array} array of objects with "key" and "value" for the feedback radio buttons
         * @private
         */
        _getOptionsAsArray :  function(options){

            var optionsArray= [];
            _.forIn(options, function(key, value) {
                optionsArray.push({
                    value: value,
                    key: key
                });
            });
            return optionsArray;
        },

        /**
         * Init the controller
         */
        init: function(){
            var ctrl = this;

            ctrl.alert = ctrl.inputParams.alert;
            ctrl.model = {
                closeAlertStatus : '',
                inputParams : ctrl.inputParams,
            };

            //Init the feedback options list for the radio buttons
            ctrl.optionsList = ctrl._getOptionsAsArray(ctrl.inputParams.options);

        }
    });

    CloseAlertModalInstanceCtrl.$inject = [
        '$scope',
        '$modalInstance',
        'inputParams'
    ];

    angular.module('Fortscale.shared.components.alertFeedback')
        .controller('CloseAlertModalInstanceCtrl', CloseAlertModalInstanceCtrl);

}());
