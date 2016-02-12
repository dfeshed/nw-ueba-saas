# FAQ: Ember Concepts

## Introduction

This document is an informal introduction to some of the most prominent concepts you'll need to know when you start building your app, including *routes*, *models*, and *components*.

Learn more by reading the official [EmberJS Guides](http://guides.emberjs.com/v2.2.0/getting-started/core-concepts/).

## What is a Router?

A [Router](http://guides.emberjs.com/v2.2.0/routing/) is a singleton that maps the current URL to a Route object, passing along any query params.  Each Ember app typically has one Router, which declares all of the support URL paths for the app.

Ember expects the Router file to be at `app/router.js`.

For example, the following code maps a URL pattern to a "dashboard" Route:
```js
// file: app/router.js:

Router.map(function() {
  this.route('dashboard', { path: '/dashboard/:dashboard_id' });
});
```

## What is a Route?

A [Route](http://guides.emberjs.com/v2.2.0/routing/defining-your-routes/) is a singleton, corresponding to some app URL path, that is responsible for choosing:

1. what data model to load, and

2. what component(s) should be rendered.

Conceptually, your Ember app is composed of one or more routes, which correspond to URL paths. By convention, for each of your routes, Ember expects the following route files to be at `app/<route-name>/`:
* `route.js`: This javascript file contains the logic for the route.
* `template.hbs`: The HTML template for the route. Typically does not contain tons of HTML, but rather just embeds other components and binds them to some data.

To continue the Router example above, Ember would expect to find the following files for the "dashboard" Route:
* `app/dashboard/route.js`
* `app/dashboard/template.hbs`

### How does my Route specify what model to load?

In Ember, a Route can have an optional `model()` method. If provided, Ember will automatically your Route's `model()` hook whenever the user navigates to your Route. The purpose of this method is to load the appropriate data for this Route.  The method should return either data or a Promise that will resolve with data.  

The `model()` hook can use whatever custom logic you wish to determine what data it should show.  In the simplest case, `model()` may always returns the same data. But most typically, `model()` will want to inspect the URL in order to decide what to load.

Using your Route's `model()` hook is convenient because  Ember will automatically pass in any dynamic segments that were used to navigate to your Route's URL.  For example, if you define a Route with a dynamic path like `/incidents/:incident_id` and a user navigates to the path `/incidents/INC-1`, then Ember will call `route.model({ incident_id: 'INC-1' })` on your Route.

For more about specifying a Route's model, read [Specifying A Route's Model](https://guides.emberjs.com/v2.3.0/routing/specifying-a-routes-model/).

Note that you may want to support query parameters in your Route's URL as well (i.e., params to the right of the `?` in the URL). These are typically used to store additional UI state in the URL, such as a "view mode" (List vs. Tile) or sort order.

Ember supports Query Parameters in your Routes as well.  Your `model()` hooks can decide whether or not to reload data in response to a query parameter change. You can read more about them in the [Query Parameters section](https://guides.emberjs.com/v2.3.0/routing/query-params/) of the Ember Guides.

As a general rule of thumb, when you design your UI, think about what UI state you would want to be specified in the URL.  State info that directly identifies the model should typically be implemented as a dynamic segment in the route; other info that is more cosmetic typically belongs in query parameters.

### How does my Route specify what components should be rendered?

This is done declaratively in your Route's template file (`template.hbs`).  Simply insert whatever HTML and/or components you want to display.

Check out the [Templating section](https://guides.emberjs.com/v2.3.0/templates/handlebars-basics/) of the Ember Guides to learn the syntax. For additional details about the template syntax, check out the [Handlebars docs](http://handlebarsjs.com/).  A few lesser-known but useful features you should know:

* You can use dot notation and square brackets for property paths. (See [here](http://handlebarsjs.com/expressions.html#basic-blocks).)
* You can use tilde (`~`) to remove white space. (See [here](http://handlebarsjs.com/expressions.html#whitespace-control).)
* You can use parentheses to nest expressions. (See [here](http://handlebarsjs.com/expressions.html#subexpressions).)
* You can use named parameters in your `{{each ..}}` loops to access an array's item and its index. (See [here](http://handlebarsjs.com/block_helpers.html?#block-params).)

## What is a Model?

In Ember, a [Model](http://guides.emberjs.com/v2.2.0/models/) is a class that represents a data record. Instances of a Model class are used to wrap data records once they are fetched from the server.

Models are a key concept in Ember Data. To learn more about Models and Ember Data in general, read the [Ember Data FAQ](ember-data-faq.md).

## What is Ember Data?

[Ember Data](https://github.com/emberjs/data) is an Ember addon which attempts to organize & simplify the reading & writing of server-side data.  It basically is an abstraction layer so that, rather than having your app code talk to AJAX & JSON, it instead talks to a data store and Model instances.  To learn more about it, read the [Ember Data FAQ](ember-data-faq.md).

## What is an Ember Component?

In Ember, a Component is a reusable standalone object with an HTML GUI. If you think in terms of the Model-View-Controller paradigm, an Ember Component is essentially a View + Controller packaged together.  However, one important difference is that Controllers are singletons while Components are not. An app can use many instances of a Component.

Once you have implemented a component, you can use it in any of your HTML templates like a custom HTML tag. For example, if you implement a component call "rsa-tree-diagram" then you could include it in any HTML template like this:

```hbs
<h1>Welcome</h1>
<p>Here is the diagram:</p>
{{rsa-tree-diagram prop1=val1 prop2=val2 .. }}
```

To learn more, try our [Components FAQ](components-faq.md).

## What is an Ember Addon?

Addons are Ember projects that you can use in your Ember apps. An Addon is the Ember equivalent of a "plug in" or "library".  To learn more about how to use Addons in your app, and how to make your own Addon, you can start with our [Ember Addons FAQ](ember-addon-faq.md);

## What are Fixtures?

Fixtures are mock data.  In Ember, we use the addon `ember-cli-mirage` to mock server data for our automated testing.  See [Mirage docs](http://www.ember-cli-mirage.com/) to learn more.
