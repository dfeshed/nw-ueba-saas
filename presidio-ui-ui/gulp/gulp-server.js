'use strict';

var gulp = require('gulp');
var connect = require('gulp-connect');
var request = require('request');
var _ = require('lodash');
var runSequence = require('run-sequence');
var Q = require('q');
var fs = require('fs');

var mockMiddlewareFn = require('./gulp-mocks');

// Get configs
var proxyConfig = _.merge({}, require('./proxy-config'));
var configDevStat = null;
try {
    configDevStat = fs.statSync('./gulp/proxy-config-dev.js');
} catch (e) {
    console.log(e);
}

if (configDevStat) {
    proxyConfig = _.merge({}, proxyConfig, require('./proxy-config-dev'));
} else {
    proxyConfig = _.merge({}, proxyConfig);
}
var gulpConfig = require('./gulp-config');

// Create a regexp from proxyConfig.apiUrlRegex
var proxyRgx = new RegExp(proxyConfig.apiUrlRegex);


var webSocketsProxyRgsArr = [];
for (var i=0; i<proxyConfig.webSockets.length;i++) {
    webSocketsProxyRgsArr.push(new RegExp(proxyConfig.webSockets[i]));
}


/**
 * Returns the request's method
 * @param req
 * @returns {string|*}
 */
function getMethod (req) {
    switch (req.method) {
        case 'DELETE':
            return 'del';
        default:
            return req.method.toLowerCase();
    }
}

/**
 * Returns the proxy url
 *
 * @param req
 * @returns {string}
 */
function getProxyUrl (req) {
    return proxyConfig.proxyApiUrl + ':' + proxyConfig.proxyApiPort + "/tdui-webapp" + req.url;
}



var proxyServerMiddlewareFn = function (localRequest, localResponse, next) {

    var isWebsocket=false;
    for (var i=0; i<webSocketsProxyRgsArr.length;i++){
        if (webSocketsProxyRgsArr[i].test(localRequest.url)){
            isWebsocket=true;
        }
    }

    if ((proxyRgx.test(localRequest.url) || isWebsocket) && !localResponse.finished) {

        // Fetch data from the API server
        var reqObj = request[getMethod(localRequest)](
            getProxyUrl(localRequest),
            function (error, response, body) {
                if (error) {
                    console.log(error);
                }
            }
        );

        // Pipe the response back to the client
        localRequest.pipe(reqObj).pipe(localResponse);

    } else {
        next();
    }
};

/**
 * The proxy server request operation.
 *
 * @param connect
 * @param opt
 * @returns {*[]}
 */
var proxyServerMiddleware = function (connect, opt) {
    return [function (localRequest, localResponse, next) {
        localResponse.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
        next();
    }, mockMiddlewareFn, proxyServerMiddlewareFn];
};

function serve () {

    return Q.Promise(function (resolve, reject, notify) {
        connect.server({
            root: 'dist',
            port: gulpConfig.localServerPort,
            livereload: true,
            middleware: proxyServerMiddleware
        });


        function addWatch (watchConfigObject) {
            gulp.watch(watchConfigObject.files, function () {

                var runSequenceArray = watchConfigObject.sequence.slice();
                runSequenceArray.push(function () {
                    return gulp.src('app')
                        .pipe(connect.reload());
                });

                runSequence.apply(this, runSequenceArray);
            });
        }

        // Add watches
        var watches = [

            {
                files: gulpConfig.srcDir + '/**/messages-copy.properties',
                sequence: ['copy-messages']
            },
            {
                files: gulpConfig.srcIndexAppScripts,
                sequence: ['build-index-app-scripts']
            },
            {
                files: gulpConfig.srcSigninAppScripts,
                sequence: ['build-signin-app-scripts']
            },
            {
                files: gulpConfig.srcAdminAppScripts,
                sequence: ['build-admin-app-scripts']
            },
            {
                files: gulpConfig.srcSystemSetupAppScripts,
                sequence: ['build-system-setup-app-scripts']
            },
            {
                files: [gulpConfig.srcDir + '/**/*.scss', gulpConfig.srcDir + '/**/*.css'],
                sequence: ['build-styles']
            },
            {
                files: gulpConfig.srcDir + '/**/*.html',
                sequence: ['copy-htmls', 'build-ng-templates', 'injects']
            },
            {
                files: gulpConfig.srcDir + '/**/*.json',
                sequence: ['copy-jsons']
            },
            {
                files: gulpConfig.srcDir + '/**/*.jsonx',
                sequence: ['copy-jsons']
            },
            {
                files: gulpConfig.srcSVGsDir + '/**/*.svg',
                sequence: ['build-svg-sprite-sheet', 'copy-svgs-spritesheet-dev']
            }
        ];

        _.each(watches, addWatch);

        setTimeout(function () {
            gulp.src('app')
                .pipe(connect.reload());
            resolve();
        }, 500);
    });

}

function serveProduction () {

    return Q.Promise(function (resolve, reject, notify) {
        console.log('serving production');

        connect.server({
            root: 'dist',
            port: gulpConfig.localServerPort,
            livereload: true,
            middleware: proxyServerMiddleware
        });

        setTimeout(function () {
            gulp.src('app')
                .pipe(connect.reload());
            resolve();
        }, 500);
    });

}

module.exports.serve = serve;
module.exports.serveProduction = serveProduction;
