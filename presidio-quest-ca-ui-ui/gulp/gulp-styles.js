var gulp = require('gulp'),
    gulpConfig = require('../gulp/gulp-config'),
    sass = require('gulp-sass'),
    autoprefixer = require('gulp-autoprefixer'),
    cleanCSS = require('gulp-clean-css'),
    jshint = require('gulp-jshint'),
    rename = require('gulp-rename'),
    concat = require('gulp-concat'),
    del = require('del'),
    inject = require('gulp-inject'),
    series = require('stream-series');


function mainCss (srcStream, basename) {
    return gulp.src(srcStream)
        .pipe(sass().on('error', sass.logError))
        .pipe(rename({suffix: '.' + gulpConfig.ver , basename: basename}))
        .pipe(autoprefixer('last 2 version'))
        .pipe(gulp.dest(gulpConfig.distCssDir))
        .pipe(rename({suffix: '.min'}))
        .pipe(cleanCSS())
        .pipe(gulp.dest(gulpConfig.distCssDir));
}

function vendorsCss (srcStram, basename) {
    return gulp.src(srcStram)
        .pipe(concat(basename + '.css'))
        .pipe(rename({suffix: '.' + gulpConfig.ver}))
        .pipe(gulp.dest(gulpConfig.distCssDir))
        .pipe(rename({suffix: '.min'}))
        .pipe(cleanCSS())
        .pipe(gulp.dest(gulpConfig.distCssDir));
}

module.exports.indexMainCss = function () {
    return mainCss(gulpConfig.srcScssIndex, 'index-main');
};

module.exports.indexVendorsCss = function () {
    return vendorsCss(gulpConfig.srcCssIndexVendors, 'index-vendors');
};

module.exports.signinMainCss = function () {
    return mainCss(gulpConfig.srcScssSignin, 'signin-main');
};

module.exports.signinVendorsCss = function () {
    return vendorsCss(gulpConfig.srcCssSigninVendors, 'signin-vendors');
};

module.exports.changePasswordMainCss = function () {
    return mainCss(gulpConfig.srcScssChangePassword, 'change-password-main');
};

module.exports.changePasswordVendorsCss = function () {
    return vendorsCss(gulpConfig.srcCssChangePasswordVendors, 'change-password-vendors');
};

module.exports.adminMainCss = function () {
    return mainCss(gulpConfig.srcScssAdmin, 'admin-main');
};

module.exports.adminVendorsCss = function () {
    return vendorsCss(gulpConfig.srcCssAdminVendors, 'admin-vendors');
};

// module.exports.systemSetupMainCss = function () {
//     return mainCss(gulpConfig.srcCssSystemSetup, 'system-setup-main');
// };
//
// module.exports.systemSetupVendorsCss = function () {
//     return vendorsCss(gulpConfig.srcCssSystemSetupVendors, 'system-setup-vendors');
// };

module.exports.cleanupCSSDir = function () {
    return del([gulpConfig.distCssDir]);
};
