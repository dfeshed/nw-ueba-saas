var gulp = require('gulp');
var _ = require('lodash');
var Server = require('karma').Server;
var babel = require('gulp-babel');
var Q = require('q');
var flatten = require('gulp-flatten');
var del = require('del');

function delTempFolder () {
    return del('./temp');
}

function processSpecsToTemp () {
    return gulp.src('../**/*.spec.js')
        .pipe(flatten())
        .pipe(babel())
        .pipe(gulp.dest('./temp'));
}

function tests () {
    return Q.Promise(function (resolve, reject) {
        var server = new Server({
            configFile: __dirname + '/../gulp-karma.conf.js',
            singleRun: true
        }, function (exitCode) {
            if (exitCode === 0) {
                resolve(exitCode);
            } else {
                reject(new Error('Tests failed'));
            }
        });
        server.start();
    });
}

function productionTests(done) {
    Q.when(delTempFolder())
        .then(processSpecsToTemp)
        .then(tests)
        .then(delTempFolder)
        .then(function () {
            done();
        })
        .catch(function (error) {
            throw error;
        });
}



module.exports.productionTests = productionTests;
module.exports.tests = tests;
