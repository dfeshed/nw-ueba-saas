var gulp = require('gulp'),
    gulpConfig = require('../gulp/gulp-config'),
    inject = require('gulp-inject'),
    series = require('stream-series'),
    Q = require('q'),
    getVendorFilesProduction = require('./gulp-js-scripts').getVendorFilesProduction;

/**
 * Returns a gulp.src of the target file
 *
 * @param fileName
 * @returns {*}
 */
function getTargetReference(fileName) {
    return gulp.src(gulpConfig.distDir + '/' + fileName + '.html');
}

/**
 *
 * @param {string} filePath
 * @returns {NodeJS.ReadWriteStream}
 */
function getStream(filePath) {
    return gulp.src(filePath, {read: false});
}


/**
 * Creates a target stream, css streams, scripts streams, and returns an inject operation
 *
 * @param targetStream
 * @param cssVendorStream
 * @param cssMainStream
 * @param scriptVendorStream
 * @param scriptMainStream
 * @param resolve
 * @returns {*}
 */
function _injectToFile (targetStream, cssVendorStream, cssMainStream, scriptVendorStream, scriptMainStream, resolve) {
    targetStream.on('end', function () {
        resolve();
    });

    return targetStream
        .pipe(inject(series(cssVendorStream, cssMainStream, scriptVendorStream, scriptMainStream), {
            ignorePath: 'dist',
            addRootSlash: false
        }))
        .pipe(gulp.dest(gulpConfig.distDir));


}

function initInjectToFile (srcStream, targetName, env, targetFillIn) {
    targetFillIn = targetFillIn || targetName;
    env = env || '';
    var minStr = (env === 'production') ? '.min' : '';

    return Q.Promise(function (resolve, reject) {
        getVendorFilesProduction(srcStream, env)
            .then(function (scriptVendorStreamSrc) {
                var target = getTargetReference(targetName);
                var cssVendorStream = getStream(gulpConfig.distCssDir + '/' + targetFillIn + '-vendors.' +
                    gulpConfig.ver + minStr + '.css');
                var cssMainStream = getStream(gulpConfig.distCssDir + '/' + targetFillIn + '-main.' +
                    gulpConfig.ver + minStr + '.css');
                var scriptVendorStream = getStream(scriptVendorStreamSrc);
                var scriptMainStream = getStream(gulpConfig.distScriptsDir + '/' + targetFillIn + '-main.' +
                    gulpConfig.ver + minStr + '.js');

                return _injectToFile(target, cssVendorStream, cssMainStream, scriptVendorStream,
                    scriptMainStream, resolve);
            });
    });
}

/**
 * Injects into index.html file
 *
 * @returns {*}
 */

module.exports.injectToIndex = function () {
    return initInjectToFile(gulpConfig.srcIndexVendorScripts, 'index');
};

module.exports.injectToIndexProduction = function () {
    return initInjectToFile(gulpConfig.srcIndexVendorScripts, 'index', 'production');
};


/**
 * Injects into signin.html file
 *
 * @returns {*}
 */

module.exports.injectToSignin = function () {
    return initInjectToFile(gulpConfig.srcSigninVendorScripts, 'signin');
};

/**
 * Injects into signin.html file - production files
 *
 * @returns {*}
 */

module.exports.injectToSigninProduction = function () {
    return initInjectToFile(gulpConfig.srcSigninVendorScripts, 'signin', 'production');
};


/**
 * Injects into change_password.html file
 *
 * @returns {*}
 */

module.exports.injectToChangePassword = function () {
    return initInjectToFile(gulpConfig.srcChangePasswordVendorScripts, 'change_password', '', 'change-password');
};

/**
 * Injects into signin.change_password file - production files
 *
 * @returns {*}
 */
module.exports.injectToChangePasswordProduction = function () {
    return initInjectToFile(gulpConfig.srcChangePasswordVendorScripts, 'change_password', 'production',
        'change-password');
};

/**
 * Injects into admin.html file
 *
 * @returns {*}
 */
module.exports.injectToAdmin = function () {
    return initInjectToFile(gulpConfig.srcAdminVendorScripts, 'admin');
};

/**
 * Injects into admin.html file - production files
 *
 * @returns {*}
 */
module.exports.injectToAdminProduction = function () {
    return initInjectToFile(gulpConfig.srcAdminVendorScripts, 'admin', 'production');
};

/**
 * Injects into system-setup.html file
 *
 * @returns {*}
 */
module.exports.injectToSystemSetup = function () {
    return initInjectToFile(gulpConfig.srcSystemSetupVendorScripts, 'system-setup');
};

/**
 * Injects into system-setup.html file - production files
 *
 * @returns {*}
 */
module.exports.injectToSystemSetupProduction = function () {
    return initInjectToFile(gulpConfig.srcSystemSetupVendorScripts, 'system-setup', 'production');
};
