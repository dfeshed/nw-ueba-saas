(function () {
    'use strict';

    angular.module("Fortscale").factory("comments", ["$q", function ($q) {
        var methods = {
            listToTree: function (comments) {
                var tree = [],
                    index = {};

                comments.forEach(function (comment) {
                    index[comment.id] = comment;
                });

                comments = comments.sort(function (a, b) {
                    return a.when < b.when ? 1 : -1;
                });

                comments.forEach(function (comment) {
                    if (comment.basedOn) {
                        var replyToComment = index[comment.basedOn];
                        if (!replyToComment) {
                            console.error("missing comment: ", replyToComment);
                        }

                        if (replyToComment.children) {
                            replyToComment.children.push(comment);
                        } else {
                            replyToComment.children = [comment];
                        }
                    } else {
                        tree.push(comment);
                    }

                    index[comment.id] = comment;
                });

                return tree;
            }
        };

        return methods;
    }]);
}());
