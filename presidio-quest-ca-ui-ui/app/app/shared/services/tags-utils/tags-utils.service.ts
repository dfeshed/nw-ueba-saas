module Fortscale.shared.services.tagsUtilsService {
    'use strict';

    export interface ITagDefinition {
        id?:string
        name:string
        displayName:string
        active:boolean
        createsIndicator:boolean,
        rules: string[],
        isAssignable:boolean,
        predefined:boolean
    }

    export interface ITagsUtilsService {
        addTag (userId, tagName):ng.IHttpPromise<any>
        removeTag (userId, tagName):ng.IHttpPromise<any>
        getTags ():ng.IPromise<{data:ITagDefinition[]}>
        getTagsFromCacheOnly ():ng.IPromise<{data:ITagDefinition[]}>
        createNewTag (name:string, displayName?:string, createsIndicator?:boolean): ng.IPromise<ITagDefinition[]>
    }

    class TagsUtils implements ITagsUtilsService {
        private USER_URL = this.BASE_URL + '/user';
        private TAGS_URL = this.BASE_URL + '/tags';
        private USER_TAGS_URL = this.TAGS_URL + '/user_tags';
        private ERR_MSG = 'tagsUtils.service: ';

        private allTagsCache:ng.IPromise<{data:ITagDefinition[]}>;

        private _getPostUrl (userId) {
            return this.USER_URL + '/' + userId;
        }

        /**
         * Returns promise that resolves on the response of the http request. Adds tag.
         *
         * @param {string} userId
         * @param {string} tagName
         * @returns {HttpPromise|Promise}
         */
        addTag (userId, tagName):ng.IHttpPromise<any> {
            return this.$http.post(this._getPostUrl(userId), {add: tagName});
        }

        /**
         * Returns promise that resolves on the response of the http request. Removes tag.
         *
         * @param {string} userId
         * @param {string} tagName
         * @returns {HttpPromise|Promise}
         */
        removeTag (userId, tagName):ng.IHttpPromise<any> {
            return this.$http.post(this._getPostUrl(userId), {remove: tagName});
        }

        /**
         * Returns promise that resolves on a list of tags in the application.
         *
         * @returns {IPromise<{data: ITagDefinition[]}>}
         */
        getTags ():ng.IPromise<{data:ITagDefinition[]}> {
            let ctrl:any = this;
            ctrl.allTagsCache = this.$http.get(this.USER_TAGS_URL)
                .then(function (res) {
                    let data:any = res && res.data || null;
                    return data;
                });

            return ctrl.allTagsCache;
        }

        /**
         * Retrun tags from cache, if there is any data in the cache.
         * If no data in cache - return empty
         */
        getTagsFromCacheOnly ():ng.IPromise<{data:ITagDefinition[]}>{
            return this.allTagsCache ? this.allTagsCache : this.getTags();
        }

        /**
         * Creates a new tag in the system.
         * Flow: get tags, then validate new tag, then create new tag in system, then return new tags list
         * @param {string} name
         * @param {string=} displayName
         * @param {string=} createsIndicator
         * @returns {IPromise<IHttpPromiseCallbackArg<any>>}
         */
        createNewTag (name:string, displayName?:string, createsIndicator?:boolean): ng.IPromise<ITagDefinition[]> {

            let err_msg = this.ERR_MSG + 'createNewTag: ';
            let _tags: ITagDefinition[];
            let _tag: any;

            // Async validation
            return this.getTags()
                .then((res:{data:ITagDefinition[]}) => {
                    _tags = res.data;
                    // Validate tag name does not already exist
                    let valid = _.every(_tags, (tag:ITagDefinition) => {
                        return tag.name !== name;
                    });

                    return valid;

                })
                .then((valid:boolean) => {
                    // Validation
                    if (!valid) {
                        let err = new RangeError(err_msg + 'Trying to add a tag with a name that already exists.');
                        err.name = 'identical-tag-name';
                        throw err;
                    }
                    this.assert.isString(name, 'name', err_msg);
                    this.assert.isString(displayName, 'displayName', err_msg, true);
                })
                .then(() => {
                    // Set defaults
                    if (!displayName) {
                        displayName = name;
                    }
                    createsIndicator = !!createsIndicator;
                    _tag = {
                        name: name,
                        displayName: displayName,
                        active: true,
                        createsIndicator: createsIndicator,
                        rules: [],
                        isAssignable: true
                    };
                    return this.$http.post(`${this.USER_TAGS_URL}`, [_tag]);

                })
                .then((res:ng.IHttpPromiseCallbackArg<any>) => {
                    // if POST was successful return updated tags
                    if (res.status !== 202) {
                        throw new Error(`${err_msg}Server should have responded with 202, but instead got ${res.status}.`);
                    }
                    return this.getTags()
                        .then((res: {data: ITagDefinition[]}) => {
                            return res.data;
                        });

                })

        }

        static $inject = ['$http', 'BASE_URL', 'assert'];
        constructor (public $http:ng.IHttpService, public BASE_URL:string, public assert:any) {
        }
    }


    angular.module('Fortscale.shared.services.tagsUtils', [])
        .service('tagsUtils', TagsUtils);
}
