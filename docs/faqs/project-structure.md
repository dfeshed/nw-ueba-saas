# FAQ: Ember Project Structure

## Where do I declare globals for my Ember app?

Generally speaking, you don't. Ember encourages you to define modules, and then use `import` statements in your JavaScript to request whichever modules are needed by a given JavaScript file.

## Where do I set/read global configuration settings for my Ember app?

They are typically set in the file `config/environment.js`.  The file is mostly JSON, and you can generally put whatever custom settings in that JSON that you wish.  

If your JavaScript needs to read a configuration setting from that file, just import the file, like this:

```js
import config from "<myapp>/config/environment";

if (config.xyzIsEnabled) {
  // do stuff ...
}
```
## Do Ember apps have an HTML file? Where is it?

Yes, the HTML file can be found in your project's directory, at: `project1/app/index.html`. However, you don't typically need to edit it, and it contains some special Ember commands for bootstrapping the app, so handle with care.

## What is the root DOM element of an Ember app?

By default, the `document.body` (`<body>`) element is the app's root DOM element.  (One notable exception: if you run the Ember automated testing UI in your browser, then app's root DOM element will be set to some test container `<div>`.)  

The app's root DOM element will be assigned a CSS class "ember-application" at run-time.

## How are my app's subdirectories and folders structured?

Here are some notable subdirectories and file paths below. (Note: This list assumes your app is [using a "pod" structure](http://ember-cli.com/user-guide/#using-pods).)

If you are unfamiliar with some of the concepts/terminology used below, try the [Concepts FAQ](project-concepts.md).

|Directory|Description|
|:--------|:----------|
|`app/bower_components/`| For 3rd party dependencies from GitHub (typically: JS libraries). To learn more, read about [bower](client-tools-faq.md).|
|`app/components/`| For your app's custom GUI components, which are basically just an HTML template + a controller packaged as a standalone re-usable UI element.|
|`app/node_modules/`| For 3rd party dependencies from NPM (typically: Ember CLI addons). To learn more, read about [npm](client-tools-faq.md).|
|`app/helpers/`| For custom macros that can be used in your templates.|
|`app/initializers/`| For code that should be executed once when your app first starts up.|
|`app/mixins/`| For mixins. Any file that needs a mixin can manually load it by using an `import` statement.|
|`app/<model-name>/model.js` & `app/<route-name>/adapter.js`| Two files for each model type: the class file that defines the model's metadata, and the adapter file that implements CRUD operations for that model type. (See below for more details.)|
|`app/<route-name>/route.js` & `app/<route-name>/template.hbs`| Two files for each route: the javascript file with the route logic, and the template file with the route DOM. (See below for more details.)|
|`app/utils/`| For utility functions or packages of functions. Any file that needs a utility can manually load it by using an `import` statement.|
|`public/`| For static assets that are not code, like graphics, fonts, etc.|
|`tests/`| For automated tests.|
