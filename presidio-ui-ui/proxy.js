'use strict';

/**
 * Load modules
 */

var express = require('express');
var request = require('request');
var path = require('path');
var open = require('opn');
var fse = require('fs-extra');
var urlHelper = require('url');
var crc = require('crc');
var livereload = require('connect-livereload');
var debug = require('debug')('proxy');
var compress = require('compression');
var program = require('commander');

var app = express();

/**
 * CLI Settings
 */
program
    .option('--nobrowser', 'Do not open browser')
    .parse(process.argv);
/**
 * App settings
 */

// Default options for all proxy requests
request = request.defaults({
    // Allow us to use our self-signed cert for testing (for HTTPS)
    strictSSL: false,
    rejectUnauthorized: false
});

/**
 * Helper functions
 */

// Build a cache file path from a given URL
function urlToFilepath(url, cacheFolder) {
    var urlParts = urlHelper.parse(url);
    var pathname = urlParts.pathname.replace(/^\//, '');
    var query = urlParts.query;
    var filename;
    var filepath;

    // Hash the query with crc32 (shorter filename)
    query = query ? crc.crc32(query).toString(16) : '';

    filename = path.join(pathname, query);
    filepath = path.join(__dirname, cacheFolder, filename + '.json');

    return filepath;
}

// Cache an API response data in a file
function saveCacheFile(path, data) {
    fse.outputFile(path, data, function (err) {
        if (err) {
            throw err;
        }
    });
}

// Delete the whole API cache folder
function deleteCacheFolder(cacheFolder) {
    var cachePath;

    if (cacheFolder && cacheFolder !== '/') {
        cachePath = path.join(__dirname, cacheFolder);

        debug('Deleting cache folder at: ' + cachePath);
        fse.removeSync(cachePath);
    }
}

// Run a callback for each URL in an array if it matches the request URL
// Supports RegExp URLs
function urlsArrFilter(urls, req, callback) {
    if (!urls || !urls.length) {
        return false;
    }

    urls.some(function (val, inx) {
        var re_url = new RegExp(val.replace(/\\/g, '\\\\'));

        if (re_url.test(req.originalUrl)) {
            // Run callback, if returned value is true, stops the loop
            return callback(val, inx);
        }
    });
}

/**
 * Set Middleware and Routes
 */

function setRoutes(settings) {
    console.log('Proxy URL set to:', settings.apiUrl);
    console.log('LiveReload set on port:', settings.lrPort);
    console.log('API cache enabled:', !settings.disableCache);

    /**
     * Settings
     */

    var cacheFolder = settings.apiCache.cacheFolder;
    var clearCacheOnUrls = settings.apiCache.clearCacheOnUrls;

    var redirects = settings.proxy.redirects;
    var redirectsArr = redirects ? Object.keys(redirects) : null;

    var useWhiteList = settings.apiCache.useWhiteList;
    var whiteListUrls = settings.apiCache.whiteListUrls;

    // Enable GZip
    app.use(compress());

    /**
     * Define Routes
     */

        // Root redirect
    app.get('/', function (req, res) {
        res.redirect('/tdui-webapp/');
    });

    // Insert mocks
    require('./mocks')(app);


    // Proxy API requests
    app.use(settings.proxy.baseUrl, function (req, res) {
        var url = settings.apiUrl + req.originalUrl;
        var method = req.method.toLowerCase();
        var reqObj;
        var filepath;

        // Reset cache flags on each request
        // disableCache - Originally set from Gruntfile.js
        var disableCache = settings.disableCache;
        var useCache = false;

        // Handle proxy redirects (proxy.redirects)
        if (redirectsArr.length) {
            urlsArrFilter(redirectsArr, req, function (val, inx) {
                debug('Redirect: ' + req.originalUrl + ', To: ' + redirects[val]);

                res.redirect(redirects[val]);
                // Don't cache requests on redirects
                disableCache = true;
                // Stop the loop
                return true;
            });
        }

        // Handle cache clearing (apiCache.clearCacheOnUrls)
        if (clearCacheOnUrls) {
            urlsArrFilter(clearCacheOnUrls, req, function (val, inx) {
                deleteCacheFolder(cacheFolder);
                // Stop the loop
                return true;
            });
        }

        if (!disableCache) {
            // Handle API URLs cache flag (apiCache.whiteListUrls)
            if (useWhiteList && whiteListUrls) {
                urlsArrFilter(whiteListUrls, req, function (val, inx) {
                    useCache = true;
                    // Stop the loop
                    return true;
                });
            }

            // Get filepath for caching the response
            if (useCache) {
                filepath = urlToFilepath(req.originalUrl, cacheFolder);
            }
        }

        function reqCallback(error, response, body) {
            if (error) {
                throw error;
            }

            if (useCache && response.statusCode >= 200 && response.statusCode < 300) {
                debug('Caching req to file: ' + filepath);
                // Save response to file
                saveCacheFile(filepath, body);
            }
        }

        function proxyReq() {
            debug('Proxy req: ' + req.originalUrl);

            // Fetch data from the API server
            reqObj = request[method](url, reqCallback);
            // Pipe the response back to the client
            req.pipe(reqObj).pipe(res);
        }

        if (useCache) {
            // Check if already in cache
            fse.readJson(filepath, function (err, data) {
                // If file doesn't exist, proxy the request and save to cache
                if (err) {
                    return proxyReq();
                }

                // Return the cached file
                res.json(data);
            });
        }
        else {
            // Cache disabled, proxy the request
            proxyReq();
        }

    });

    if (settings.lrPort) {
        // Setup LiveReload to reload the browser on files changes
        app.use('/tdui-webapp', livereload({
            port: settings.lrPort
        }));
    }

    // Serve static files
    app.use('/tdui-webapp', express.static(path.join(__dirname, 'dist')));
}


/**
 * Return server initializer
 */

module.exports = function (settings) {
    var port = settings.port;

    // Set local server routes handling
    setRoutes(settings);

    /**
     * Start the server
     */

    app.listen(port, function () {
        console.log('WebApp listening on: http://localhost:' + port);
        // Open the browser
        if (!program.nobrowser) {
            open('http://localhost:' + port);
        }
    });
};
