/* global it */
'use strict';

var fs = require('fs');
var path = require('path');
var plugin = require('./');
var assert = require('assert');
var postcss = require('postcss');
var broccoli = require('broccoli');

it('will blow up when css syntax error present', function () {
  var outputTree = plugin(['fixtures'], 'syntax', 'app.css', true);
  var builder = new broccoli.Builder(outputTree);
  return new Promise(function(resolve, reject) {
    return builder.build().then(function() {
      reject(new Error('Error! Should have blown up with css syntax error'));
    }, function(e) {
      var syntaxErrorFound = e.message.indexOf('Unknown word') > -1;
      assert.strictEqual(syntaxErrorFound, true);
      resolve();
    });
  });
});

it('will not blow up when no app.css file found', function () {
  var outputTree = plugin(['fixtures'], 'empty', 'app.css', true);
  var builder = new broccoli.Builder(outputTree);
  return new Promise(function(resolve, reject) {
    return builder.build().then(resolve, reject);
  });
});

it('should produce a light and dark theme with static css', function () {
  var expectedLight = fs.readFileSync(path.join(__dirname, 'fixtures', 'expected', 'light.css'), 'utf8');
  var expectedDark = fs.readFileSync(path.join(__dirname, 'fixtures', 'expected', 'dark.css'), 'utf8');

  var outputTree = plugin(['fixtures'], 'assets', 'app.css', true);
  var builder = new broccoli.Builder(outputTree);
  return builder.build().then(function () {
    var light = fs.readFileSync(path.join(__dirname, 'fixtures', 'assets', 'light.css'), 'utf8');
    var dark = fs.readFileSync(path.join(__dirname, 'fixtures', 'assets', 'dark.css'), 'utf8');
    assert.strictEqual(light, expectedLight);
    assert.strictEqual(dark, expectedDark);
  });
});

it('should produce a light and dark theme for production builds', function () {
  var expectedLight = fs.readFileSync(path.join(__dirname, 'fixtures', 'expected', 'light.css'), 'utf8');
  var expectedDark = fs.readFileSync(path.join(__dirname, 'fixtures', 'expected', 'dark.css'), 'utf8');

  var outputTree = plugin(['fixtures'], 'prod', 'app.css', true);
  var builder = new broccoli.Builder(outputTree);
  return builder.build().then(function () {
    var light = fs.readFileSync(path.join(__dirname, 'fixtures', 'prod', 'light-cfd46dd672a31535e0e47662f6dcb59f.css'), 'utf8');
    var dark = fs.readFileSync(path.join(__dirname, 'fixtures', 'prod', 'dark-cfd46dd672a31535e0e47662f6dcb59f.css'), 'utf8');
    assert.strictEqual(light, expectedLight);
    assert.strictEqual(dark, expectedDark);
  });
});
