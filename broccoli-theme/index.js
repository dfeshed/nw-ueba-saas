'use strict'

var fs = require('fs');
var path = require('path');
var mkdirp = require('mkdirp');
var postcss = require('postcss');
var CachingWriter = require('broccoli-caching-writer');
var customProperties = require('postcss-custom-properties');

function PostcssCompiler (inputNodes, directory, inputFile, testing) {
  if (!(this instanceof PostcssCompiler)) {
    return new PostcssCompiler(inputNodes, directory, inputFile, testing);
  }

  CachingWriter.call(this, inputNodes);

  this.testing = testing;
  this.directory = directory;
  this.inputFile = inputFile;
}

PostcssCompiler.prototype = Object.create(CachingWriter.prototype);
PostcssCompiler.prototype.constructor = PostcssCompiler;

PostcssCompiler.prototype.compile = function (appCss, plugin, fileName) {
  var processor = postcss();
  processor.use(plugin);

  var directory = this.directory;
  var prefix = this.testing ? 'fixtures' : this.outputPath;
  var themePath = path.join(prefix, directory, fileName);
  return processor.process(appCss).then(function(result) {
    mkdirp.sync(path.dirname(themePath));
    fs.writeFileSync(themePath, result.css, {
      encoding: 'utf8'
    });
  });
};

PostcssCompiler.prototype.pluginify = function(themeName) {
  return postcss.plugin('postcss-theme-helper', function() {
    return function plugin(css) {
      var themeCss = css;
      themeCss.walkRules(function (rule) {
        if(rule.selector.indexOf(themeName) > -1) {
          rule.selector = ':root';
        }
      });
      return new Promise(function (resolve, reject) {
        postcss().use(customProperties()).process(themeCss).then(function(result) {
          resolve(result);
        });
      });
    }

  });
};

PostcssCompiler.prototype.productionRegex = function () {
  var directory = this.directory;
  var inputFileTokens = this.inputFile.match(/(.*)\.(.*)/);
  var fileName = inputFileTokens[1];
  var extension = inputFileTokens[2];
  return new RegExp(directory + '/' + fileName + '-(.*)\.' + extension);
};

PostcssCompiler.prototype.searchInputPaths = function (filePath) {
  var appCss = [];
  this.inputPaths.forEach(function(inputPath) {
    try {
      var fromFilePath = path.join(inputPath, filePath);
      var cssFile = fs.readFileSync(fromFilePath, 'utf8');
      appCss.push(cssFile);
    } catch (e) {
      //iterate all paths looking for app.css
    }
  });

  if (appCss.length > 1) {
    throw new Error('PostcssCompiler: app.css was found > 1x');
  }

  return appCss[0];
};

PostcssCompiler.prototype.findProdFile = function () {
  var env = process.env.EMBER_ENV || 'development';

  if (env === 'production') {
    var productionRegex = this.productionRegex();
    var entries = this.listEntries().filter(function(entry) {
      var relativePath = entry && entry.relativePath;
      return relativePath && productionRegex.exec(relativePath);
    });
    return entries && entries[0] && entries[0].relativePath;
  }
};

PostcssCompiler.prototype.findCss = function () {
  var productionFile = this.findProdFile();
  var filePath = productionFile || path.join(this.directory, this.inputFile);
  return this.searchInputPaths(filePath);
};

PostcssCompiler.prototype.productionHash = function () {
  var productionFile = this.findProdFile();
  if (productionFile) {
    var productionRegex = this.productionRegex();
    return productionRegex.exec(productionFile);
  }
};

PostcssCompiler.prototype.generateFileName = function (themeName) {
  var fingerprint = this.productionHash();
  if (fingerprint) {
    return themeName + '-' + fingerprint[1] + '.css';
  }
  return themeName + '.css';
};

PostcssCompiler.prototype.build = function () {
  var appCss = this.findCss();

  if (appCss === undefined) {
    return;
  }

  var lightPlugin = this.pluginify('.light-theme');
  var darkPlugin = this.pluginify('.dark-theme');

  var lightFileName = this.generateFileName('light');
  var darkFileName = this.generateFileName('dark');

  var light = this.compile(appCss, lightPlugin, lightFileName);
  var dark = this.compile(appCss, darkPlugin, darkFileName);

  return new Promise(function(resolve, reject) {
    return Promise.all([light, dark]).then(resolve, reject);
  });
};

module.exports = PostcssCompiler;
