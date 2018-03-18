var gulp = require('gulp'),
    gulpConfig = require('../gulp/gulp-config'),
    jshint = require('gulp-jshint'),
    rename = require('gulp-rename'),
    concat = require('gulp-concat-util'),
    uglify = require('gulp-uglify'),
    del = require('del'),
    inject = require('gulp-inject'),
    series = require('stream-series'),
    sourcemaps = require('gulp-sourcemaps'),
    notify = require('gulp-notify'),
    fs = require('fs'),
    path = require('path'),
    Q = require('q'),
    _ = require('lodash'),
    babel = require('gulp-babel'),
    ngTemplates = require('gulp-ng-templates'),
    htmlmin = require('gulp-htmlmin'),
    gulpSort = require('gulp-sort'),
    ts = require('gulp-typescript');

var tsConfig = require('../tsconfig.json');


function isFileExists(path) {
    return Q.ninvoke(fs, 'stat', path);
}

/**
 * Returns a path of the minified file if exists. Otherwise returns the path of the unminified file.
 * If no version of the file exists, it will throw a ReferenceError
 * @param {string} path
 */
function getSrcAsyncMin(path) {

    rgxMin = /\.min\.js$/;
    rgxJs = /\.js$/;

    var testMinPath = path;
    var testUnMinPath = path;

    var minPromise;

    if (!rgxMin.test(path)) {
        if (!rgxJs.test(path)) {
            throw new RangeError('path must end with .js');
        }
        testMinPath = testMinPath.replace(rgxJs, '.min.js');
    }


    return Q.allSettled([isFileExists(testMinPath), isFileExists(testUnMinPath)])
        .then(function (results) {
            if (results[0].state === 'fulfilled') {
                return testMinPath;
            }  else if (results[1].state === 'fulfilled') {
                return testUnMinPath;
            } else {
                throw ReferenceError('No version of this file exists: ' + path);
            }

        })

}


/**
 * Returns a path of the minified file if exists. Otherwise returns the path of the unminified file.
 * If no version of the file exists, it will throw a ReferenceError
 * @param {string} path
 */
function getSrcAsyncUnMin(path) {

    rgxMin = /\.min\.js$/;
    rgxJs = /\.js$/;

    var testMinPath = path;
    var testUnMinPath = path;

    if (rgxMin.test(path)) {
        testUnMinPath = testUnMinPath.replace(rgxMin, '.js');
    } else if (!rgxJs.test(path)) {
        throw new RangeError('path must end with .js');
    }


    return Q.allSettled([isFileExists(testUnMinPath), isFileExists(testMinPath)])
        .then(function (results) {
            if (results[0].state === 'fulfilled') {
                return testUnMinPath;
            } else if (results[1].state === 'fulfilled'){
                return testMinPath;
            } else {
                throw ReferenceError('No version of this file exists: ' + path);
            }
        })
        .catch(function (err) {
            throw ReferenceError('No version of this file exists: ' + path);
        });

}



function wrapFile (fileStr) {
    return '\n// File start:>\n' + fileStr + '\n// File End!\n'
}

function wrapFileStrip (fileStr) {
    fileStr = (fileStr.trim() + '\n').replace(/(^|\n)[ \t]*('use strict'|"use strict");?\s*/g, '$1');
    return wrapFile(fileStr);

}

/**
 * Returns a list of file paths. the file will be minified or not depending on env.
 *
 * @param {Array<string>} files
 * @param {string=} env
 * @returns {*}
 */
module.exports.getVendorFilesSrc = function getVendorFilesSrc(files, env) {

    var getSrcFn = getSrcAsyncUnMin;

    if (env === 'production') {
        getSrcFn = getSrcAsyncMin
    }

    // Inner map creates the correct path. Outer map creates a list of promises.
    return Q.all(_.map(_.map(files, function (filePath) {
        return gulpConfig.srcScriptsVendorDir + '/' + filePath;
    }), getSrcFn));
};

/**
 * Returns a list of file paths. the file will be minified or not depending on env.
 *
 * @param {Array<string>} files
 * @param {string=} env
 * @returns {*}
 */
module.exports.getVendorFilesProduction = function getVendorFilesProduction(files, env) {

    var getSrcFn = getSrcAsyncUnMin;

    if (env === 'production') {
        getSrcFn = getSrcAsyncMin
    }

    // Inner map creates the correct path. Outer map creates a list of promises.
    return Q.all(_.map(_.map(files, function (filePath) {
        var i = filePath.lastIndexOf('/');
        if (i !== -1) {
            filePath = filePath.substr(i+1);
        }
        return gulpConfig.distScriptsDir + '/' + filePath;
    }), getSrcFn));
};

module.exports.copyVendorScripts = function () {
    return module.exports.getVendorFilesSrc(gulpConfig.srcIndexVendorScripts)
        .then(function (files) {

            return gulp.src(files)
                .pipe(gulp.dest(gulpConfig.distScriptsDir));
        });
};


module.exports.copyVendorScriptsProduction = function () {
    return module.exports.getVendorFilesSrc(gulpConfig.srcIndexVendorScripts, 'production')
        .then(function (files) {

            return gulp.src(files)
                .pipe(gulp.dest(gulpConfig.distScriptsDir));
        });
};

function appScripts (srcStream, basename) {
    return gulp.src(tsConfig.files.concat(srcStream))
        .pipe(ts(tsConfig.compilerOptions))
        .pipe(sourcemaps.init())
        .pipe(concat(basename + '.' + gulpConfig.ver + '.js'))
        .pipe(sourcemaps.write('.'))
        .pipe(gulp.dest(gulpConfig.distScriptsDir));
}

function appScriptsProduction (srcStream, basesname) {
    return gulp.src(tsConfig.files.concat(srcStream))
        .pipe(ts(tsConfig.compilerOptions))
        .pipe(concat(basesname + '.' + gulpConfig.ver + '.min.js'))
        .pipe(uglify({
            mangle: false
        }))
        .pipe(gulp.dest(gulpConfig.distScriptsDir));
}

function testSrcScripts () {
    var src = gulpConfig.srcIndexAppScripts;

    return Q.all(_.map(src, (path) => {
        return isFileExists(path)
            .then(function (result) {
                    console.log(`EXISTS: ${path}`);
            })
            .catch(err => {
                console.error(`DOES NOT EXIST: ${path}`);
            })
    }));


}

module.exports.ngTemplates = function () {
    var srcStreamDef = [gulpConfig.srcDir + '/**/*.html',
        '!' + gulpConfig.srcDir + '/index.html',
        '!' + gulpConfig.srcDir + '/404.html',
        '!' + gulpConfig.srcDir + '/admin.html',
        '!' + gulpConfig.srcDir + '/change_password.html',
        '!' + gulpConfig.srcDir + '/signin.html',
        '!' + gulpConfig.srcDir + '/system-setup.html'
    ];
    var target =  gulp.src(srcStreamDef);

    return target
        .pipe(gulpSort())
        .pipe(htmlmin({collapseWhitespace: true}))
        .pipe(ngTemplates({
            module: 'fsTemplates',
            standalone: true,
            filename: 'fs-templates.min.js'
        }))
        .pipe(gulp.dest(gulpConfig.distScriptsDir));
};

module.exports.appIndexScripts = function () {
    return appScripts(gulpConfig.srcIndexAppScripts, 'index-main');
};

module.exports.appIndexScriptsProduction = function () {
    return appScriptsProduction(gulpConfig.srcIndexAppScripts, 'index-main');
};

module.exports.appSigninScripts = function () {
    return appScripts(gulpConfig.srcSigninAppScripts, 'signin-main');
};

module.exports.appSigninScriptsProduction = function () {
    return appScriptsProduction(gulpConfig.srcSigninAppScripts, 'signin-main');
};

module.exports.appChangePasswordScripts = function () {
    return appScripts(gulpConfig.srcChangePasswordAppScripts, 'change-password-main');
};

module.exports.appChangePasswordScriptsProduction = function () {
    return appScriptsProduction(gulpConfig.srcChangePasswordAppScripts, 'change-password-main');
};

module.exports.appAdminScripts = function () {
    return appScripts(gulpConfig.srcAdminAppScripts, 'admin-main');
};

module.exports.appAdminScriptsProduction = function () {
    return appScriptsProduction(gulpConfig.srcAdminAppScripts, 'admin-main');
};

// module.exports.appSystemSetupScripts = function () {
//     return appScripts(gulpConfig.srcSystemSetupAppScripts, 'system-setup-main');
// };

// module.exports.appSystemSetupScriptsProduction = function () {
//     return appScriptsProduction(gulpConfig.srcSystemSetupAppScripts, 'system-setup-main');
// };

module.exports.cleanupScriptsDir = function () {
    return del([gulpConfig.distScriptsDir]);
};

module.exports.testScrScripts = testSrcScripts;

