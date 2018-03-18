/**
 *
 */
module Fortscale.shared.services.alertUpdatesService {
    'use strict';

    /**
     * Entities interfaces
     */
    export interface IUserActivity {
        type:String;
        analystUserName:String;
        id:String;
        modifiedAt:Date;
    }

    export interface IComment  extends  IUserActivity{
        commentText:String;

    }

    export interface IFeeadback extends  IUserActivity{
        alertFeedback: AlertFeedback;
        scoreDelta: number;
        userScoreAfter: number;
        userScoreSeverityAfter:string,

    }

    export interface FeedbackObject {
        status:AlertStatus;
        feedback: AlertFeedback;
    }

    /**
     * Enums
     */
    export enum AlertFeedback {
        Approved,
        Rejected,
        None
    }


    export enum AlertStatus {
        Open,
        Closed
    }


    /**
     * Service interfaces
     */

    export interface IAlertUpdatesService{
        addComment(alertId: String, comment: IComment): ng.IHttpPromise<Comment>;
        deleteComment(alertId: String, comment: IComment): ng.IHttpPromise<any>;
        updateComment(alertId: String, comment: IComment): ng.IHttpPromise<any>;
        updateFeedback(alertId:String, feedbackBody:FeedbackObject, analystName:string): ng.IHttpPromise<void>;
    }

    class AlertUpdatesService implements IAlertUpdatesService {


        private ALERT_URL = this.BASE_URL + '/alerts';

        private _getPostUrl (alertId: String) {
            return this.ALERT_URL + '/' + alertId + '/comments';
        }

        private _getDeleteAndUpdateUrl (alertId: String, commentId:String) {
            return this._getPostUrl(alertId) + '/' + commentId;
        }

        /**
         * Send HTTP post for adding comment
         * @param alertId
         * @param comment
         * @returns {IHttpPromise<T>} - return promise which contain the comment
         */
        addComment(alertId: String, comment: IComment): ng.IHttpPromise<Comment> {
            return this.$http.post(this._getPostUrl(alertId), comment)
        }


        /**
         * Send comment delete request and return promise without any bady
         * @param alertId
         * @param comment
         * @returns {IHttpPromise<T>}
         */
        deleteComment(alertId: String, comment: IComment): ng.IHttpPromise<any> {
            let url = this._getDeleteAndUpdateUrl(alertId, comment.id);
            return this.$http.delete(url);
        }

        /**
         * Send comment update request and return promise without any body
         * @param alertId
         * @param comment
         * @returns {IHttpPromise<T>}
         */
        updateComment(alertId: String, comment: IComment): ng.IHttpPromise<any> {
            return this.$http.patch(this._getDeleteAndUpdateUrl(alertId, comment.id),comment);
        }

        /**
         * Update the status and feedback of the alert and return promise without any body
         * @param alertId
         * @param feedbackBody
         * @returns {IHttpPromise<T>}
         */
        updateFeedback(alertId:String, feedbackBody:FeedbackObject, analystName:string): ng.IHttpPromise<any> {

            /**
             * Extract the values as string instead of number represents the enum
             * @type {{status: any, feedback: any}}
             */
            var body = {
                status: AlertStatus[feedbackBody.status],
                feedback: AlertFeedback[feedbackBody.feedback],
                analystUserName:analystName
            };

            // Create the patch request url
            var url = this.BASE_URL + '/alerts/' + alertId;

            return this.$http.patch(url, body);
        }

        static $inject = ['$http', 'BASE_URL'];

        constructor(public $http:ng.IHttpService, public BASE_URL:string) {

        }
    }


    angular.module('Fortscale.shared.services')
        .service('alertUpdatesService', AlertUpdatesService)
}
