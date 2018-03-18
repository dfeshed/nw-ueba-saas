(function () {
    'use strict';

    function EntityUtils (assert, $http, BASE_URL, fsModals, URLUtils) {

        this.MINIMAL_OPEN_TIME = 1000;

        this._validateEntity = function (entity, errMsg) {
            errMsg = errMsg ? errMsg : '';

            assert.isObject(entity, 'entity', errMsg);
            assert.isString(entity.id, 'entityId', errMsg);
        };

        /**
         * Fetches entity id and type and navigates to user overview if the entity is "User"
         * *
         * @param {String} entityType - alert / coumputer etc...
         * @param {String} entityId (user ID)
         */
        this.navigateToEntityProfile = function (entityType, entityId) {

            // Go to user overview if entity type is User
            if (entityType === 'User') {
                URLUtils.setUrl('user/' + entityId + '/user_overview', false);
            }
        };

        /**
         * Changes a user's follow state
         *
         * @param {object} entity Must be a user
         * @param {boolean} followState If true user followed will be set to true,
         * @param {function=} finalCb A callback function that is invoked at the end of the chain
         * @param {function=} errorCb A callback function that is invoked on error
         * otherwise to false.
         */
        this.changeUserFollowState = function (entity, followState, finalCb, errorCb) {

            var ctrl = this;
            var processPopup, state, status;

            // Validations
            ctrl._validateEntity(entity);

            // Flow methods:
            // Changes the state of followed flag on the user
            function setUserFollowedState () {
                entity.followed = state;
            }

            // Closes the processing modal in minimal time
            function closeModalInMinialTime () {
                return fsModals.minimalClose(processPopup, openTime, ctrl.MINIMAL_OPEN_TIME);
            }

            // Opens a success modal
            function openSuccessModal () {
                return fsModals.openTimedPopup({
                    title: 'Success',
                    text: 'User is now ' + status + '.',
                    topColor: 'green'
                });
            }

            // Opens an error modal
            function openErrorModal () {
                return fsModals.openTimedPopup({
                    title: 'Server error',
                    text: 'There was a server error. Please try again later.',
                    topColor: 'red'
                });
            }

            // Returns the entity
            function returnEntity () {
                return entity;
            }

            // Sets defaults to call backs
            finalCb = finalCb || returnEntity;
            errorCb = errorCb || function () {
                    closeModalInMinialTime()
                        .then(openErrorModal);
                };

            // assignments
            state = !!followState;
            status = state ? 'followed' : 'unfollowed';

            // Opens a processing modal
            processPopup = fsModals
                .openProcessingPopup('Processing', 'Changing user status to ' + status + '.');

            // Open time is now
            var openTime = Date.now().valueOf();

            // Change the user's followed state on the server
            return $http.get(BASE_URL + '/analyst/followUser',
                {
                    params: {
                        follow: state,
                        userId: entity.id
                    }
                })
                .then(closeModalInMinialTime)
                .then(setUserFollowedState)
                .then(openSuccessModal)
                .then(returnEntity)
                .then(finalCb)
                .catch(errorCb);
        };
    }

    EntityUtils.$inject = ['assert', '$http', 'BASE_URL', 'fsModals', 'URLUtils'];

    angular.module('Fortscale.shared.services.modelUtils')
        .service('entityUtils', EntityUtils);

}());
