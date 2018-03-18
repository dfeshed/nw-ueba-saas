var gulp = require('gulp');
var gulpConfig = require('../gulp/gulp-config');
var _ = require('lodash');
var del = require('del');
var gulpCopy = require('gulp-copy');
var flatten = require('gulp-flatten');
var Q = require('q');
var imageop = require('gulp-image-optimization');
var jsonminify = require('gulp-jsonminify');
var props2json = require('gulp-props2json');
var svgSprite;
try {
    svgSprite = require("gulp-svg-sprites");
} catch (err) {

}


var htmlFiles = [
    '404.html',
    'admin.html',
    'change_password.html',
    'index.html',
    'signin.html',
    'system-setup.html'
];

var srcHtmlFiles = _.map(htmlFiles, function (path) {
    return gulpConfig.srcDir + '/' + path;
});

var distHtmlFiles = _.map(htmlFiles, function (path) {
    return gulpConfig.distDir + '/' + path;
});

module.exports.copyHTMLs = function () {
    return gulp.src(srcHtmlFiles)
        .pipe(gulp.dest(gulpConfig.distDir));
};

module.exports.cleanupHTMLs = function () {
    return del(distHtmlFiles);
};

module.exports.cleanupDist = function () {
    return del(gulpConfig.distDir);
};

module.exports.copyJSONs = function () {
    return gulp.src([gulpConfig.srcDir + '/**/*.json', gulpConfig.srcDir + '/**/*.jsonx'])
        .pipe(gulpCopy(gulpConfig.distDir, {prefix: 1}));
};

module.exports.copyJSONsProduction = function () {
    return gulp.src([gulpConfig.srcDir + '/**/*.json', gulpConfig.srcDir + '/**/*.jsonx'])
        .pipe(jsonminify())
        //.pipe(gulpCopy(gulpConfig.distDir, {prefix: 1}));
        .pipe(gulp.dest(gulpConfig.distDir));
};

module.exports.copyImages = function () {
    return gulp.src(gulpConfig.srcImagesDir + '/**/*')
        .pipe(imageop({
            optimizationLevel: 5,
            progressive: true,
            interlaced: true
        }))
        .pipe(gulp.dest(gulpConfig.distDir + '/images'))
        .pipe(gulp.dest(gulpConfig.distAssetsDir + '/images'));
};

module.exports.copyExplicitImages = function () {
    "use strict";


    let copyToCssImgPromise = Q.Promise((resolve, reject) => {
        let copyToCssImg = gulp.src([
                gulpConfig.srcLibsDir + '/detail-wrap/img/lens.png',
                gulpConfig.srcLibsDir + '/bootstrap/img/glyphicons-halflings.png'
            ])
            .pipe(gulp.dest(gulpConfig.distAssetsDir + '/img'));
        copyToCssImg.on('end', resolve);
        copyToCssImg.on('error', reject);
    });


    let copyToCssImagesPromise = Q.Promise((resolve, reject) => {
        let copyToCssImages = gulp.src([
                gulpConfig.srcLibsDir + '/jquery-ui/fortscale/images/*'
            ])
            .pipe(gulp.dest(gulpConfig.distCssDir + '/images'));
        copyToCssImages.on('end', resolve);
        copyToCssImages.on('error', reject);
    });


    let copyToAssetsCssPromise = Q.Promise((resolve, reject) => {
        let copyToAssetsCss = gulp.src([
                gulpConfig.srcLibsDir + '/kendo-ui/style/**/*'
            ])
            .pipe(gulp.dest(gulpConfig.distCssDir));
        copyToAssetsCss.on('end', resolve);
        copyToAssetsCss.on('error', reject);
    });


    let copyToAssetsPromise = Q.Promise((resolve, reject) => {
        var copyToAssets = gulp.src([
                gulpConfig.srcLibsDir + '/flag-icon-css-master/**/*.svg'
            ])
            .pipe(gulp.dest(gulpConfig.distAssetsDir));
        copyToAssets.on('end', resolve);
        copyToAssets.on('error', reject);
    });


    return Q.all([
        copyToAssetsPromise,
        copyToCssImgPromise,
        copyToCssImagesPromise,
        copyToAssetsCssPromise
    ]);

};

module.exports.copyFonts = function () {
    'use strict';

    let generalFontsCopyPromise = Q.Promise((resolve, reject) => {
        "use strict";
        let generalFontsCopy = gulp.src(gulpConfig.srcStylesDir + '/fonts/**/*.ttf')
            .pipe(gulp.dest(gulpConfig.distCssDir + '/fonts'));
        generalFontsCopy.on('end', resolve);
        generalFontsCopy.on('error', reject);
    });


    let fontawsomeFontsCopyPromise = Q.Promise((resolve, reject) => {
        let fontawsomeFontsCopy = gulp.src([
                gulpConfig.srcDir + '/**/*.ttf',
                gulpConfig.srcDir + '/**/*.woff2',
                gulpConfig.srcDir + '/**/*.woff'])
            .pipe(flatten())
            .pipe(gulp.dest(gulpConfig.distAssetsDir + '/fonts'));
        fontawsomeFontsCopy.on('end', resolve);
        fontawsomeFontsCopy.on('error', reject);
    });


    return Q.all([
        generalFontsCopyPromise,
        fontawsomeFontsCopyPromise
    ]);
};

module.exports.copySVGs = function () {
    'use strict';

    let copyIndividualSVGsPromise = Q.Promise((resolve, reject) => {
        "use strict";
        let copyIndividualSVGs = gulp.src(gulpConfig.srcSVGsDir + '/**/*.svg')
            .pipe(gulp.dest(gulpConfig.distSVGsDir));

        copyIndividualSVGs.on('end', resolve);
        copyIndividualSVGs.on('error', reject);
    });


    let copySpritesheetPromise = Q.Promise(function (resolve, reject) {
        "use strict";
        let copySpritesheet = gulp.src(gulpConfig.srcSVGsSpritesheetDir + '/svg/symbols.svg')
            .pipe(gulp.dest(gulpConfig.distSVGsSpritesheetDir));

        copySpritesheet.on('end', resolve);
        copySpritesheet.on('error', reject);
    });


    return Q.all([
        copyIndividualSVGsPromise,
        copySpritesheetPromise
    ]);
};

module.exports.copySVGsDev = () => {
    "use strict";
    return gulp.src(gulpConfig.srcSVGsDir + '/**/*.svg')
        .pipe(gulp.dest(gulpConfig.distSVGsDir));
};

module.exports.copySVGsSpritesheetDev = () => {
    "use strict";
    return gulp.src(gulpConfig.srcSVGsSpritesheetDir + '/svg/symbols.svg')
        .pipe(gulp.dest(gulpConfig.distSVGsSpritesheetDir));
};

/**
 * This task used to translate the messages file into JSON,
 * in the same format as /api/messages do in the server.
 * Wrap the messages object with "data" and remove the "fortscale.message" prefix from keys
 */
module.exports.copyMessagesMock = function () {

    /**
     * Replacer function get all the properties with key and value,
     * and remove the prefix from each proeprty name under 'data' property.
     * For any other property - return itself
     * @param key
     * @param value
     */
    var replacerFunction = function (key, value) {
        if (key && key === 'data') {

            var propertyNamePrefix = "fortscale.message.";

            var oldMessages = value;
            var newMessages = {};


            for (var propertyName in value) {
                var newPropertyName = propertyName.replace(propertyNamePrefix, '');
                newMessages[newPropertyName] = oldMessages[propertyName];
            }

            value = newMessages;
        }
        return value;

    };

    var options = {
        namespace: 'data', //wrap the properties with 'data'
        replacer: replacerFunction
    };
    /**
     * Read messages-copy.properties and create the json file.
     */
    gulp.src(gulpConfig.messages + '/*.properties')
        .pipe(props2json(options))
        .pipe(gulp.dest(gulpConfig.distAssetsDir + '/messages/'))
};

module.exports.buildSVGSpriteSheet = function () {
    if (svgSprite) {
        return gulp.src(gulpConfig.srcSVGsDir + '/**/*.svg')
            .pipe(svgSprite({mode: "symbols"}))
            .pipe(gulp.dest(gulpConfig.srcSVGsSpritesheetDir));
    }
};


module.exports.copyAmChartsImages = function () {
    return gulp.src(gulpConfig.srcLibsDir + '/amcharts/images/**/*')
        .pipe(imageop({
            optimizationLevel: 5,
            progressive: true,
            interlaced: true
        }))
        .pipe(gulp.dest(gulpConfig.distScriptsDir + '/images'));
};

