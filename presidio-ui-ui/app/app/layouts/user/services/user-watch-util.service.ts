module Fortscale.layouts.user {

    export interface IUserWatchUtilService {
        changeUserWatchState (user, state): ng.IPromise<any>;
    }

    class UserWatchUtil implements IUserWatchUtilService {
        CHANGE_USER_WATCH_STATE_PATH: string = this.BASE_URL + '/analyst/followUser';

        /**
         * Changes a user's watch state
         * @param {{}} user
         * @param {boolean} state
         * @returns {IPromise<TResult>}
         */
        changeUserWatchState (user, state): ng.IPromise<any> {
            return this.$http.get(this.CHANGE_USER_WATCH_STATE_PATH, {
                params: {
                    follow: state,
                    userId: user.id
                }
            })
                .then((res: ng.IHttpPromiseCallbackArg<any>) => {
                    user.followed = state;
                    return user;
                })
        }

        static $inject = ['BASE_URL', '$http'];
        constructor (public BASE_URL: string, public $http: ng.IHttpService) {}
    }

    angular.module('Fortscale.layouts.user')
        .service('userWatchUtil', UserWatchUtil)
}
