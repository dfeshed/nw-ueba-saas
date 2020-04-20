'use strict';

/**
 *  This file holds Route class, middleware function as this module's export, and the actual routes definitions at
 *  the bottom of the file.
 */

/**
 * Route.class Constructor for creating mock routes.
 *
 */
class Route {
    /**
     *
     * @param {{status: number=, headerObj: string=, urlRgx: RegExp, body: object|string=, disabled: boolean= }} config
     */
    constructor (config) {
        this.body = config.body;
        this.urlRgx = config.urlRgx;
        this.method = config.method;

        this.status = config.status || 200;
        this.headerObj = config.headerObj || this._getHeader();
        this.message = this._getMessage();
        this.disabled = !!config.disabled;
    }

    /**
     * Returns derived header. The header type is decided based on the body type.
     *
     * @returns {*}
     * @private
     */
    _getHeader () {
        if (typeof this.body === 'string') {
            return {"Content-Type": "application/text"};
        } else {
            return {"Content-Type": "application/json"};
        }
    }

    /**
     * Returns derived message. If string it returns as is. If object it returns strigified. If strigify fails, an
     * error message is returned. Headers and status may also change.
     *
     * @returns {string}
     * @private
     */
    _getMessage () {
        if (typeof this.body === 'string') {
            return this.body;
        }

        let message;
        try {
            message = JSON.stringify(this.body);
        } catch (e) {
            this.headerObj['Content-Type'] = "application/text";
            message = 'JSON: Could not convert object to string.';
            this.status = 500;
        }

        return message;
    }

}

// Mocks definitions
let routes = [];

/**
 * Iterates through routes and for the first route match, a response is returned.
 *
 * @param request
 * @param response
 * @param next
 */
module.exports = function (request, response, next) {
    let output = null;
    routes.some(route => {
        if (route.disabled) {
            return false;
        }
        var urlRgx1 = new RegExp(route.urlRgx.source + '$');
        var urlRgx2 = new RegExp(route.urlRgx.source + '\\?');
        if (request.method === route.method && (urlRgx1.test(request.url) || urlRgx2.test(request.url))) {
            output = route;
        }

        return !!output;
    });

    if (output !== null) {
        response.writeHead(output.status, output.headerObj);
        response.end(output.message);
    }

    next();
};

/**
 * Routes definitions
 */

    // route: /tdui-webapp/api/alerts
    // Disabled: true
routes.push(new Route({
    urlRgx: /\/tdui-webapp\/api\/alerts/,
    method: 'GET',
    body: require('./mocks/alerts.mock.json'),
    disabled: true
}));

// route: /tdui-webapp/api/application_configuration
// Disabled: true
routes.push(new Route({
    urlRgx: /\/tdui-webapp\/api\/application_configuration/,
    method: 'GET',
    body: require('./mocks/application-configuration.mock.json'),
    disabled: true
}));

// route: /tdui-webapp/api/pxgrid
// Disabled: true
routes.push(new Route({
    urlRgx: /\/tdui-webapp\/api\/pxgrid/,
    method: 'POST',
    body: {server: '666.666.666.666'},
    disabled: true
}));

// route: /tdui-webapp/api/pxgrid/generate_cer
// Disabled: true
routes.push(new Route({
    urlRgx: /\/tdui-webapp\/api\/pxgrid\/generate_cer/,
    method: 'GET',
    body: 'ok',
    disabled: true
}));

// route: /tdui-webapp/api/pxgrid/update_cer
// Disabled: false
routes.push(new Route({
    urlRgx: /\/tdui-webapp\/api\/pxgrid\/update_cer/,
    method: 'POST',
    body: '{}',
    status: 204,
    disabled: true
}));
// route: /tdui-webapp/api/user/user_tags
// Disabled: true
routes.push(new Route({
    urlRgx: /\/tdui-webapp\/api\/user\/user_tags/,
    method: 'POST',
    body: {},
    disabled: true
}));

// route: /tdui-webapp/api/analytics
// Disabled: true
routes.push(new Route({
    urlRgx: /\/tdui-webapp\/api\/analytics/,
    method: 'POST',
    body: {},
    status: 204,
    disabled: true
}));

// route: /tdui-webapp/api/user?size=5&sort_field=score
// For: High Risk Users
// Disabled: true
routes.push(new Route({
    urlRgx: /\/tdui-webapp\/api\/user\?size=5&sort_field=score/,
    method: 'GET',
    body: require('./mocks/mock-high-risk-users.json'),
    status: 200,
    disabled: true
}));
// route: /tdui-webapp/api/alerts?...
// For: Top Ten Alerts
// Disabled: true
routes.push(new Route({
    urlRgx: /\/tdui-webapp\/api\/alerts\?.*/,
    method: 'GET',
    body: require('./mocks/top-ten-alerts.json'),
    status: 200,
    disabled: true
}));
// route: /tdui-webapp/api/user/:userId/activity/locations
// For: Top Ten Alerts
// Disabled: true
routes.push(new Route({
    urlRgx: /\/tdui-webapp\/api\/user\/.*\/activity\/locations.*?/,
    method: 'GET',
    body: require('./mocks/activity-top-coutry-user.json'),
    status: 200,
    disabled: true
}));

// route: /tdui-webapp/api/user/:userId/activity/locations
// For: Top Ten Alerts
// Disabled: true
routes.push(new Route({
    urlRgx: /\/tdui-webapp\/api\/organization\/activity\/locations.*?/,
    method: 'GET',
    body: require('./mocks/activity-top-country-organization.json'),
    status: 200,
    disabled: true
}));

// route: /tdui-webapp/api/user/:userId/activity/authentications
// For: Top Ten Alerts
// Disabled: false
routes.push(new Route({
    urlRgx: /\/tdui-webapp\/api\/user\/.*\/activity\/authentications.*?/,
    method: 'GET',
    body: require('./mocks/activity-authentication-user.json'),
    status: 200,
    disabled: true
}));
// route: /tdui-webapp/api/user/:userId/activity/working-hours
// For: Top Ten Alerts
// Disabled: false
routes.push(new Route({
    urlRgx: /\/tdui-webapp\/api\/user\/.*\/activity\/working-hours.*?/,
    method: 'GET',
    body: require('./mocks/activity-working-hours.json'),
    status: 200,
    disabled: true
}));
// route: /tdui-webapp/api/user/:userId/activity/source-devices
// For: Top Ten Alerts
// Disabled: false
routes.push(new Route({
    urlRgx: /\/tdui-webapp\/api\/user\/.*\/activity\/source-devices.*?/,
    method: 'GET',
    body: require('./mocks/activity-source-devices-user.json'),
    status: 200,
    disabled: true
}));
// route: /tdui-webapp/api/user/:userId/activity/data-usage
// For: Top Ten Alerts
// Disabled: false
routes.push(new Route({
    urlRgx: /\/tdui-webapp\/api\/user\/.*\/activity\/data-usage.*?/,
    method: 'GET',
    body: require('./mocks/activity-data-usage-user.json'),
    status: 200,
    disabled: true
}));

routes.push(new Route({
    urlRgx: /\/tdui-webapp\/api\/log_repository/,
    method: 'GET',
    body: require('./mocks/get-log-repository.json'),
    status: 200,
    disabled: true
}));

