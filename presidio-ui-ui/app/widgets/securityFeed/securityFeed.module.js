(function () {
    'use strict';

    angular.module("SecurityFeed", ["Utils", "Transforms", "Styles", "Icons", "Widgets", "State"]).run(["$q", "utils",
        "transforms", "styles", "icons", "widgetViews",
        function ($q, utils, transforms, styles, icons, widgetViews) {
            function securityFeedDataParser (view, data, params) {
                var deferred = $q.defer(),
                    viewData = [],
                    promises = [];

                angular.forEach(data, function (item) {

                    //an addition for adding the investigate url to the username accordign to the investigate generator
                    item.investigateLink = typeLinks[item.generator_name] ?
                        utils.strings.parseValue(typeLinks[item.generator_name].url, item) :
                        utils.strings.parseValue("#/user/{{fsId}}/user_overview".url, item);

                    if (view.settings.dataField) {
                        angular.forEach(item[view.settings.dataField], addItem);
                    } else {
                        addItem(item);
                    }
                });

                function addItem (item) {
                    var itemObj = {},
                        text = "";
                    //dynamicText deals with the case of nested {{ }}. example: {{expression with {{something}} inside
                    // }}
                    var dynamicText;

                    if (view.settings.link) {
                        var links = [];

                        if (item.aggregated) {
                            angular.forEach(item.aggregated, function (entity) {
                                if (!view.settings.linkIfExists || entity[view.settings.linkIfExists]) {
                                    if (view.settings.link.customUrl && view.settings.link.customUrl.map &&
                                        view.settings.link.customUrl.map[item.generator_name]) {
                                        links.push("<a href='" +
                                            utils.strings.parseValue(view.settings.link.customUrl
                                                .map[item.generator_name], entity, params) + "'>" +
                                            utils.strings.parseValue(view.settings.link.text, entity, params) + "</a>");
                                    } else {
                                        links.push("<a href='" +
                                            utils.strings.parseValue(view.settings.link.url, entity, params) + "'>" +
                                            utils.strings.parseValue(view.settings.link.text, entity, params) + "</a>");
                                    }
                                } else {
                                    links.push(utils.strings.parseValue(view.settings.link.text, entity, params));
                                }
                            });

                            if (item.aggregated.length > 3) {
                                itemObj.linksHtml = utils.arrays.toSentence(links);
                                var multipleName = typeLinks[item.generator_name] &&
                                    typeLinks[item.generator_name].aggregatedObjectName || view.settings.link.name;
                                itemObj.collapsedLinksText = item.aggregated.length + " " + multipleName;
                            }
                            else {
                                text += utils.arrays.toSentence(links);
                            }
                        }
                        else {
                            if (!view.settings.linkIfExists || item[view.settings.linkIfExists]) {
                                if (view.settings.link.customUrl && view.settings.link.customUrl.map &&
                                    view.settings.link.customUrl.map[item.generator_name]) {
                                    links.push("<a href='" +
                                        utils.strings.parseValue(view.settings.link.customUrl.map[item.generator_name],
                                            item, params) + "'>" +
                                        utils.strings.parseValue(view.settings.link.text, item, params) + "</a>");
                                } else {
                                    links.push("<a href='" +
                                        utils.strings.parseValue(view.settings.link.url, item, params) + "'>" +
                                        utils.strings.parseValue(view.settings.link.text, item, params) + "</a>");
                                }
                            } else {
                                links.push(utils.strings.parseValue(view.settings.link.text, item, params));
                            }

                            text += utils.arrays.toSentence(links);
                        }
                    }
                    //remove the outer set of curly brackets, so parseValue could deal with the inner curly brackets
                    dynamicText =
                        view.settings.textWithVariables ? item[view.settings.textWithVariables] : view.settings.text;
                    text += utils.strings.parseValue(dynamicText, item, params);

                    //attributeMatches parse the parts with single curly brackets '{something}', which usually comes
                    // from 'cause' in mongo
                    var attributeMatches = text.match(/\{([^\}]+)\}/g);
                    if (attributeMatches) {
                        angular.forEach(attributeMatches, function (attributeMatch) {
                            var property = attributeMatch.match(/^\{([^\}]+)/)[1],
                                propertyAttributes,
                                html = [],
                            //logical '&&' returns the first falsy var (or just the latest one is they all truthy)
                                itemAttributes = item.aggregated && item.aggAttributes || item.attributes;

                            if (itemAttributes) {
                                propertyAttributes = [];
                                angular.forEach(itemAttributes[property], function (attribute) {
                                    if (!~propertyAttributes.indexOf(attribute)) {
                                        propertyAttributes.push(attribute);
                                    }
                                });

                                var url;
                                if (view.settings.attributes && view.settings.attributes.url) {
                                    url = view.settings.attributes.url.map ?
                                        view.settings.attributes.url.map[item.generator_name] :
                                        view.settings.attributes.url;
                                }

                                if (url) {
                                    angular.forEach(propertyAttributes, function (attribute) {
                                        html.push("<a href='" +
                                            utils.strings.parseValue(url, {attribute: attribute}, params) + "'>" +
                                            attribute + "</a>");
                                    });
                                }
                                else {
                                    angular.forEach(propertyAttributes, function (attribute) {
                                        html.push("<span>" + attribute + "</span>");
                                    });
                                }
                            }

                            text = text.replace(attributeMatch, utils.arrays.toSentence(html));
                        });
                    }

                    itemObj.id = item.id;
                    itemObj.text = text.replace(/([^\.])$/, "$1.");
                    itemObj.dismissed = item.dismissed;
                    itemObj.flag = item.flag;

                    if (view.settings.note) {
                        itemObj.note = utils.strings.parseValue(view.settings.note, item, params);
                    }

                    if (view.settings.icon) {
                        promises.push(icons.getIcon(view.settings.icon, item).then(function (icon) {
                            itemObj.icon = icon;
                        }));
                    }

                    if (view.settings.allowComments) {
                        itemObj.commentsCount = item.commentsCount;
                        itemObj.comments = item.comments;
                        itemObj.loadedComments = false;
                    }

                    if (!item.aggregated || !typeLinks[item.generator_name] ||
                        typeLinks[item.generator_name].allowAggregated !== false) {
                        if (!!(itemObj.link = angular.copy(typeLinks[item.generator_name]))) {
                            var linkData = angular.copy(item);
                            if (item.aggregated) {
                                linkData.fsId = [];
                                linkData.displayName = [];
                                linkData.userCount = 0;

                                item.aggregated.forEach(function (user) {
                                    // Update array of fsIds
                                    if (linkData.fsId.indexOf(user.fsId) < 0) {
                                        linkData.fsId.push(user.fsId);
                                        // Increment number of users
                                        linkData.userCount++;
                                    }
                                    // Update array of displayNames
                                    if (linkData.displayName.indexOf(user.displayName) < 0) {
                                        linkData.displayName.push(user.displayName);
                                    }
                                });

                                // Concatenate fsIds
                                linkData.fsId = linkData.fsId.join(",");
                                // Concatenate displayNames
                                linkData.displayName = linkData.displayName.join(",");

                                // User count is not needed if there's only 1 user
                                if (linkData.userCount === 1) {
                                    delete linkData.userCount;
                                }
                            }
                            itemObj.link.href = utils.strings.parseValue(itemObj.link.url, linkData, params);
                            itemObj.link.text = utils.strings.parseValue(itemObj.link.text, linkData, params);
                        }
                    }

                    viewData.push(itemObj);
                }

                if (promises.length) {
                    $q.all(promises).then(function () {
                        deferred.resolve(viewData);
                    });

                    return deferred.promise;
                }

                return viewData;
            }

            var typeLinks = {
                VpnGeoHoppingNotificationGenerator: {
                    url: "#/packages/geo_hopping?user={{fsId}}&user_label={{displayName}}&start=" +
                    "{{:sinceNow:valueOf:-14d}}&end={{:sinceNow:valueOf}}&user_count={{userCount}}",
                    text: "Go to Geo-Hopping package"
                },
                VPNBruteForce: {
                    "text": "Investigate {{displayName}}'s VPN events"
                },
                UserCreatedEnabledDeletedGenerator: {
                    "url": "#/d/explore/users?filters=users.id%3D{{fsId}}",
                    "text": "Investigate user {{displayName}}",
                    "allowAggregated": false,
                    "notification_ts": "{{ts:date}}"
                },
                AboutToExpireGenerator: {
                    "url": "#/d/explore/users?filters=users.id%3D{{fsId}}",
                    "text": "Investigate user {{displayName}}",
                    "allowAggregated": false,
                    "notification_ts": "{{ts:date}}"
                },
                ScoreShotUpGenerator: {
                    "url": "#/d/explore/users?filters=users.id%3D{{fsId}}",
                    "text": "Investigate user {{displayName}}",
                    "allowAggregated": false,
                    "notification_ts": "{{ts:date}}"
                },
                UserHasNewAdminGroupGenerator: {
                    "url": "#/d/explore/users?filters=users.id%3D{{fsId}}",
                    "text": "Investigate user {{displayName}}",
                    "allowAggregated": false,
                    "notification_ts": "{{ts:date}}"
                },
                VpnOverlapNotification: {
                    "url": "#/d/explore/vpn_session?filters=vpn_session.username%3D{{attributes.username}}," +
                    "vpn_session.country%3DReserved%20Range&default_filters=vpn_session.session_time_utc%3D:" +
                    "{{attributes.start_date::date:unixtimestamp}}::{{attributes.end_date::date:unixtimestamp}}," +
                    "vpn_session.session_score%3D%3E%3D0&tableview_fields=username,source_ip,start_time,end_time," +
                    "country,city,read_bytes,write_bytes,duration,session_score",
                    "text": "Investigate user {{displayName}}",
                    "allowAggregated": false,
                    "notification_ts": "{{ts:date}}"
                }
            };

            widgetViews.registerView("securityFeed", {dataParser: securityFeedDataParser});

        }]);
}());
