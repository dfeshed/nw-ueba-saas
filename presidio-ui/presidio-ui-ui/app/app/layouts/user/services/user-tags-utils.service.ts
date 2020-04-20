module Fortscale.layouts.user {

    import ITagDefinition = Fortscale.shared.services.tagsUtilsService.ITagDefinition;
    import ITagsUtilsService = Fortscale.shared.services.tagsUtilsService.ITagsUtilsService;
    import IToastrService = Fortscale.shared.services.toastrService.IToastrService;

    export interface IUserTagsUtilsService {
        removeTag (tag:ITagDefinition, user: any): ng.IPromise<any>;
        addTag (tagName:string, user, tagDisplayName?:string): ng.IPromise<any>;
        addNewTag (tagName:string, tags: ITagDefinition[], user: any): ng.IPromise<{user: any, tags: ITagDefinition[]}>
    }

    class UserTagsUtilsService implements IUserTagsUtilsService {

        /**
         * Removes a tag from a user,
         * @param {ITagDefinition} tag
         * @param {{}} user
         */
        removeTag (tag:ITagDefinition, user: any): ng.IPromise<any> {

            return this.tagsUtils.removeTag(user.id, tag.name)
                .then(() => {
                    user.tags = _.filter(user.tags, (userTagName:string) => {
                        return userTagName !== tag.name;
                    });
                    // this.toastrService.info(
                    //     `Tag <b>${tag.displayName}</b> was successfully removed from <b>${user.fallBackDisplayName}</b>.`);
                    return user;
                })
                .catch((err) => {
                    console.error(err);
                    this.toastrService.error(
                        `There was an error trying to remove tag <b>${tag.displayName}</b> to user <b>${user.fallBackDisplayName}</b>.<br>Please try again later.`);
                });


        }

        /**
         * Adds a tag to the user
         * @param {ITagDefinition} tag
         * @param {{}} user
         * @returns {IPromise<TResult>}
         */
        addTag (tagName:string, user:any, tagDisplayName?:string): ng.IPromise<any> {
            return this.tagsUtils.addTag(user.id, tagName)
                .then(() => {
                    user.tags.push(tagName);

                    return user;
                })
                .catch((err) => {
                    console.error(err);
                    this.toastrService.error(
                        `There was an error trying to add tag <b>${tagDisplayName?tagDisplayName:tagName}</b> to user <b>${user.fallBackDisplayName}</b>.<br>Please try again later.`);
                })
        }

        /**
         * Adds a new tag to system and to user
         * @param tagName
         * @param tags
         * @param user
         * @returns {ng.IPromise<any>}
         */
        addNewTag (tagName:string, tags: ITagDefinition[], user: any): ng.IPromise<any> {
            function findTagNameInTags (tags:ITagDefinition[], tagName:string):ITagDefinition {
                return <ITagDefinition>(_.find < (tags, {name: tagName}) || _.find(tags, {displayName: tagName}));
            }

            // find the new tag (if exists)
            let tag:ITagDefinition = findTagNameInTags(tags, tagName);

            // If tag is found, validate, if valid add it to user
            if (tag) {


                // Check if user has tag
                let userHasTag = _.some(user.tags, userTag => userTag === tag.name);
                if (userHasTag) {
                    this.toastrService.error(
                        `The user is already tagged with a tag named <b>${tag.displayName || tagName}</b>.`);
                    return null;
                }

                // Add tag to user if tag is not fixed and the user does not already have the tag
                return this.addTag(tag.name, user)
                    .then(user => {
                        return {user: user, tags: tags};
                    });
            }


            // Create the new tag and add it to the user
            return this.tagsUtils.createNewTag(tagName)
                .then((_tags:ITagDefinition[]) => {
                    //place new tags list in tags variable for future reference
                    tags = _tags;
                    // Add tag to user
                    return this.addTag(<any>findTagNameInTags(tags, tagName), user);
                })
                .then(user => {
                    return {user: user, tags: tags};
                })
                .catch((err) => {
                    this.toastrService.error(
                        `There was an error trying to add new tag <b>${tagName}</b> to user <b>${user.fallBackDisplayName}</b>.<br>Please try again later.`);
                });
        }

        static $inject = ['tagsUtils', 'toastrService'];
        constructor (public tagsUtils:ITagsUtilsService, public toastrService:IToastrService) {}
    }

    angular.module('Fortscale.layouts.user')
        .service('userTagsUtils', UserTagsUtilsService)
}
