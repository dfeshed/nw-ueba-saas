'use strict';

var gulp = require('gulp');
var gulpConfig = require('./gulp/gulp-config');
var runSequence = require('run-sequence');
var gulpCopies = require('./gulp/gulp-copies');
var gulpStyles = require('./gulp/gulp-styles');
var gulpScripts = require('./gulp/gulp-js-scripts');
var gulpInject = require('./gulp/gulp-inject');
var gulpServer = require('./gulp/gulp-server');
var gulpTests = require('./gulp/gulp-tests');
var gulpVersion = require('./gulp/gulp-version');
var jsonfile = require('jsonfile');
var jshint = require('gulp-jshint');

var scaffolds = require('./gulp/scaffolds');

/**
 * Cleanups
 */
gulp.task('cleanup-htmls', gulpCopies.cleanupHTMLs);
gulp.task('cleanup-css-dir', gulpStyles.cleanupCSSDir);
gulp.task('cleanup-script-dir', gulpScripts.cleanupScriptsDir);
gulp.task('cleanup-dist', gulpCopies.cleanupDist);

/**
 * Version
 */
gulp.task('create-version-file', gulpVersion.createVersionFile);
gulp.task('update-package-version', gulpVersion.updatePackageVersion);
gulp.task('update-version', ['create-version-file', 'update-package-version']);

/**
 * Copies
 */
gulp.task('copy-htmls', gulpCopies.copyHTMLs);
gulp.task('copy-vendor-scripts', gulpScripts.copyVendorScripts);
gulp.task('copy-vendor-scripts-production', gulpScripts.copyVendorScriptsProduction);
gulp.task('copy-jsons', gulpCopies.copyJSONs);
gulp.task('copy-jsons-production', gulpCopies.copyJSONsProduction);
gulp.task('copy-images', gulpCopies.copyImages);
gulp.task('copy-explicit-images', gulpCopies.copyExplicitImages);
gulp.task('copy-fonts', gulpCopies.copyFonts);
gulp.task('copy-svgs', gulpCopies.copySVGs);
gulp.task('copy-svgs-dev', gulpCopies.copySVGsDev);
gulp.task('copy-svgs-spritesheet-dev', gulpCopies.copySVGsSpritesheetDev);
gulp.task('copy-messages', gulpCopies.copyMessagesMock);
gulp.task('copy-amcharts-images', gulpCopies.copyAmChartsImages);
gulp.task('copies', ['copy-htmls', 'copy-vendor-scripts', 'copy-jsons', 'copy-images', 'copy-explicit-images',
    'copy-fonts', 'copy-messages', 'copy-svgs-dev', 'copy-amcharts-images']);
gulp.task('copies-production', ['copy-htmls', 'copy-vendor-scripts-production', 'copy-jsons-production', 'copy-images',
    'copy-explicit-images', 'copy-fonts', 'copy-svgs', 'copy-amcharts-images']);

/**
 * Styles
 */
gulp.task('build-svg-sprite-sheet', gulpCopies.buildSVGSpriteSheet);
gulp.task('build-index-main-styles', gulpStyles.indexMainCss);
gulp.task('build-index-vendor-styles', gulpStyles.indexVendorsCss);
gulp.task('build-signin-main-styles', gulpStyles.signinMainCss);
gulp.task('build-signin-vendor-styles', gulpStyles.signinVendorsCss);
gulp.task('build-change-password-main-styles', gulpStyles.changePasswordMainCss);
gulp.task('build-change-password-vendor-styles', gulpStyles.changePasswordVendorsCss);
gulp.task('build-admin-main-styles', gulpStyles.adminMainCss);
gulp.task('build-admin-vendor-styles', gulpStyles.adminVendorsCss);
gulp.task('build-styles', [
    'build-index-main-styles',
    'build-index-vendor-styles',
    'build-signin-main-styles',
    'build-signin-vendor-styles',
    'build-change-password-main-styles',
    'build-change-password-vendor-styles',
    'build-admin-main-styles',
    'build-admin-vendor-styles'
]);

/**
 * Scripts
 */
gulp.task('build-index-app-scripts', gulpScripts.appIndexScripts);
gulp.task('build-index-app-scripts-production', gulpScripts.appIndexScriptsProduction);
gulp.task('build-signin-app-scripts', gulpScripts.appSigninScripts);
gulp.task('build-signin-app-scripts-production', gulpScripts.appSigninScriptsProduction);
gulp.task('build-change-password-app-scripts', gulpScripts.appChangePasswordScripts);
gulp.task('build-change-password-app-scripts-production', gulpScripts.appChangePasswordScriptsProduction);
gulp.task('build-admin-app-scripts', gulpScripts.appAdminScripts);
gulp.task('build-admin-app-scripts-production', gulpScripts.appAdminScriptsProduction);
gulp.task('build-ng-templates', gulpScripts.ngTemplates);
gulp.task('build-json-templates', gulpScripts.jsonTemplates);
gulp.task('build-scripts', ['build-index-app-scripts', 'build-signin-app-scripts', 'build-admin-app-scripts',
    'build-ng-templates', 'build-change-password-app-scripts']);
gulp.task('build-scripts-no-templates', ['build-index-app-scripts', 'build-signin-app-scripts',
    'build-change-password-app-scripts', 'build-admin-app-scripts']);
gulp.task('build-scripts-production', ['build-index-app-scripts-production', 'build-signin-app-scripts-production',
    'build-change-password-app-scripts-production', 'build-admin-app-scripts-production', 'build-ng-templates']);
gulp.task('build-scripts-no-templates-production', ['build-index-app-scripts-production',
    'build-signin-app-scripts-production', 'build-change-password-app-scripts-production',
    'build-admin-app-scripts-production']);

/**
 * Injects
 */
gulp.task('inject-index', gulpInject.injectToIndex);
gulp.task('inject-index-production', gulpInject.injectToIndexProduction);
gulp.task('inject-signin', gulpInject.injectToSignin);
gulp.task('inject-signin-production', gulpInject.injectToSigninProduction);
gulp.task('inject-change-password', gulpInject.injectToChangePassword);
gulp.task('inject-change-password-production', gulpInject.injectToChangePasswordProduction);
gulp.task('inject-admin', gulpInject.injectToAdmin);
gulp.task('inject-admin-production', gulpInject.injectToAdminProduction);
gulp.task('injects', ['inject-index', 'inject-signin', 'inject-change-password', 'inject-admin'], function (done) {
    setTimeout(() => {
        done();
    }, 100);
});
gulp.task('injects-production', ['inject-index-production', 'inject-signin-production',
    'inject-change-password-production', 'inject-admin-production']);

/**
 * Test
 */
gulp.task('tests-production', ['build-ng-templates'], gulpTests.productionTests);
gulp.task('tests', ['build-ng-templates'], gulpTests.tests);

/**
 * Builds
 */
gulp.task('build', function (done) {
    return runSequence('update-version', 'cleanup-dist', 'copies',
        ['build-styles', 'build-scripts', 'build-svg-sprite-sheet'], 'injects',
        'copy-svgs-spritesheet-dev', function () {
            done();
        });

});
gulp.task('build-production', function (done) {
    return runSequence('update-version', 'lint', 'cleanup-dist', 'copies-production',
        ['build-styles', 'build-scripts-production'],
        'injects-production', 'tests-production', function () {
            done();
        });
});

gulp.task('build-production-team-city', function (done) {
    return runSequence('update-version', 'cleanup-dist', 'copies-production',
        ['build-styles', 'build-scripts-production'],
        'injects-production', function () {
            done();
        });
});

/**
 * Serves
 */
gulp.task('serve-no-build', gulpServer.serve);
gulp.task('serve-no-build-production', gulpServer.serveProduction);

gulp.task('serve', function () {
    return runSequence('build', gulpServer.serve);
});

gulp.task('serve-production', ['build-production'], gulpServer.serveProduction);

gulp.task('lint', function () {

    var success = true;

    return gulp.src([gulpConfig.srcDir + '/**/*.js', '!' + gulpConfig.srcDir + '/libs/**/*'])
        .pipe(jshint())
        .pipe(jshint.reporter('default', {verbose: true}))
        .on('data', function (file) {
            if (!file.jshint.success) {
                success = false;
            }
        })
        .on('end', function () {
            if (!success) {
                console.error('jshint failed. Please fix jshint errors.');
                process.exit(0);
            }
        });

});

gulp.task('lint-to-report', function (done) {
    var exec = require('child_process').exec;
    var cmd = 'gulp lint > lint-report.txt';
    exec(cmd, function (error, stdout, stderr) {
        done();
    });

});

/**
 * Scaffolds
 */

gulp.task('generate-directive', () => {
    return scaffolds.generateDirective()
        .then(() => process.exit(0));
});

gulp.task('test-source-scripts', gulpScripts.testScrScripts);

