module Fortscale.layouts.user {
    'use strict';

    class ProfileController {

        user: any;

        $onInit () {
        }

        static $inject = [];
        constructor () {}
    }

    let userProfileComponent: ng.IComponentOptions = {
        controller: ProfileController,
        templateUrl: 'app/layouts/user/components/user-profile/user-profile.component.html',
        bindings: {
            user: '<userModel'
        }
    };

    angular.module('Fortscale.layouts.user')
        .component('userProfile', userProfileComponent);
}
