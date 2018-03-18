module Fortscale.layouts.overview.components.usersTagsCount {
    import IUsersUtils = Fortscale.layouts.users.IUsersUtils;
    import UserFilter = Fortscale.layouts.users.UserFilter;

    interface IUsersTagsCount {
        [key:string]:{
            key:string,
            value:number
        };
    }

    interface IItemSetting {
        displayName:string,
        displayNameSingle:string,
        icon:string,
        iconWidth:number,
        iconHeight:number,
        tagKey:string,
        defaultValue:any,
        href:string,
        query:UserFilter
    }

    interface IItemsSettings {
        [key:string]:IItemSetting
    }


    class UsersTagsCountController {
        usersTagsCount:IUsersTagsCount;

        itemsSettings:IItemsSettings = {
            risky: {
                displayName: 'Risky',
                displayNameSingle: 'Risky',
                icon: 'bell-icon',
                iconWidth: 36,
                iconHeight: 36,
                tagKey: 'risky',
                defaultValue: 0,
                href: '#/users?users-page.minScore=0',
                query: {
                    minScore : 0,
                }
            },
            admins: {
                displayName: 'Admin',
                displayNameSingle: 'Admin',
                icon: 'user-admins-icon',
                iconWidth: 36,
                iconHeight: 36,
                tagKey: 'admin',
                defaultValue: 0,
                href: '#/users?users-page.minScore=&users-page.userTags=admin',
                query: {
                    userTags : "admin"
                }
            },
            watched: {
                displayName: 'Watched',
                displayNameSingle: 'Watched',
                icon: 'watch-icon',
                iconWidth: 36,
                iconHeight: 36,
                tagKey: 'watched',
                defaultValue: 0,
                href: '#/users?users-page.minScore=&users-page.isWatched=true',
                query: {
                    isWatched : true
                }
            },
            executives: {
                displayName: 'Executive',
                displayNameSingle: 'Executive',
                icon: 'user-executive-icon',
                iconWidth: 36,
                iconHeight: 36,
                tagKey: 'executive',
                defaultValue: 0,
                href: '#/users?users-page.minScore=&users-page.userTags=executive',
                query: {
                    userTags : "executive"
                }
            },
            service: {
                displayName: 'Service',
                displayNameSingle: 'Service',
                icon: 'user-service-icon',
                iconWidth: 36,
                iconHeight: 36,
                tagKey: 'service',
                defaultValue: 0,
                href: '#/users?users-page.minScore=&users-page.userTags=service',
                query: {
                    userTags : "service"
                }
            },
            tagged: {
                displayName: 'Tagged',
                displayNameSingle: 'Tagged',
                icon: 'tag_icon',
                iconWidth: 36,
                iconHeight: 36,
                tagKey: 'Tagged',
                defaultValue: 0,
                href: '#/users?users-page.minScore=&users-page.userTags=any',
                query: {
                       userTags : "any"
                    }
            }
        };

        $onInit ():void {
        }
    }

    class UsersTagsCountItemController {
        itemSettings: IItemSetting;
        count:number;

        static $inject = ['usersUtils'];

        constructor (public usersUtils:IUsersUtils) {
        }

        getCount(){
            let query = this.itemSettings.query;
            this.usersUtils.countUsersByFilter(query).then((res:any)=>{
                this.count = res;
            });
        }

        $onInit (): void {
            this.getCount();
        }
    }

    let UsersTagsCountComponent:ng.IComponentOptions = {
        controller: UsersTagsCountController,
        bindings: {
            usersTagsCount: '<'
        },
        templateUrl: 'app/layouts/overview/components/overview-users-tags-count/overview-users-tags-count.component.html'
    };



    let UsersTagsCountItemComponent: ng.IComponentOptions = {
        controller: UsersTagsCountItemController,
        bindings: {
            itemSettings: '<'
        },
        templateUrl: 'app/layouts/overview/components/overview-users-tags-count/users-tags-count-item.component.html'
    };

    angular.module('Fortscale.layouts.overview')
        .component('overviewUsersTagsCount', UsersTagsCountComponent)
        .component('overviewUsersTagsCountItem', UsersTagsCountItemComponent);

}
