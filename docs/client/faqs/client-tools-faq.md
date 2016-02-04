# FAQ: Ember & Client-Side Tools

## What is Ember?

Ember is a JavaScript framework for building [Single Page Apps](https://en.wikipedia.org/wiki/Single-page_application) ("SPAs").  It began as a [Model-View-Controller ("MVC")](https://en.wikipedia.org/wiki/Model%E2%80%93view%E2%80%93controller) framework but in practice has gradually become more of a Route-Model-Component framework.  In a nutshell, the developer implements an app by implementing routes, models, components, etc; then the framework handles the loading and the inter-communication of these pieces.

## Why is Ember called a "framework"?

Ember consists of two things:

1. A client-side JavaScript library, ["EmberJS"](http://www.emberjs.com/).
2. A server-side command line tool, ["Ember CLI"](http://www.ember-cli.com/).

EmberJS is the framework that runs your app's front-end at run-time.  Ember CLI is the tool you use to build your app on your development server. Therefore, Ember is more than just a client-side library that you add to an existing app in order to enhance the front-end.  It is a whole system for building a Single Page App.

For more details, read on.

## Why is Ember called an "opinionated" framework?

Ember is "opinionated" in the sense that it expects you to organize your code in certain ways.

For example, Ember asks you to follow conventions such as:

1. You are expected to write your JavaScript source files as [ES6 modules](http://eviltrout.com/2014/05/03/getting-started-with-es6.html).

2. You should organize your app into certain types of modules: routes, models, components, services, initializers, helpers, utilities, adapters, etc.  Read more about these module types in later sections below.

3. There are conventions about where the module files should be located and what names they should use.   

If you comply with Ember's expectations, then the framework handles many things for you (such as compiling JS & CSS), which leaves you free to focus on building the business logic of your app.  But if you don't comply with Ember's expectations, then you will see diminishing returns for using the framework.

## What is Ember CLI?

[ember-cli](http://www.ember-cli.com/) is a command line tool for creating & compiling Ember apps. It runs on the developer's machine. It is the standard way of using Ember. In other words, you wouldn't typically use Ember without using Ember CLI.  In fact, you don't typically even install EmberJS; rather, you install Ember CLI and it installs EmberJS (among other things).

Note: The correct name for Ember CLI in the command line is `ember`, *not* `ember-cli` nor `emberjs`.  So whenever you see command line snippets in the form of `ember <command> <arg>`, those are Ember CLI calls.

## Why do I need to Ember CLI to create and compile my app?

### Ember CLI & App Creation

While creating your app, Ember CLI helps you follow Ember's conventions and guidelines.  For example, suppose you want to create a new component. You may ask yourself:

* What files do I need to create?
* Where should I put them?  

Ember CLI answers these questions for you.  Simply use the `ember generate <type> <name>` command like this:

````bash
ember generate component my-component
````  

Ember CLI will then create the folder and files for your component in the right place  (typically `app/components/my-component/`), even inserting some default boilerplate code into the files to get you started.  Similarly, Ember will also generate folders & files for automated tests for your component (typically in `/tests/components/components/`) to get you started with testing.

The example above used a component for illustration, but the same thing is true for other types of Ember modules (e.g., models, routes, controllers, services, initializers, utils, etc).  You should use `ember generate` when creating these modules, rather than creating new files from scratch.

Note: Before using the `ember generate` command, make sure that you have told Ember that you want to use a "pod structure" for your app.  Read [this article](http://ember-cli.com/user-guide/#using-pods) to learn what a pod structure is, and how to tell Ember to use it.  It's pretty simple to do; basically you just need to add the `"usePods": true` setting to the file `.ember-cli` in your UI's root directory, like so:

```json
/* file: .ember-cli */
{
  "disableAnalytics": true,
  "usePods": true
}
```

### Ember CLI & App Building

Unless your app is very small and simple, you probably need some sort of build process. At a minimum, you probably need to minify and bundle your JavaScript.  If you use a CSS pre-processor, you'll need to compile your SASS files into CSS.  If you use mock data, you may also want to include/exclude it from your app's distribution, depending on whether your are making a build for production or for testing.

Ember CLI has commands (`ember build` and `ember serve`) for kicking off your app's build process.  The build process is configurable, of course.  In your project's root directory, `ember-cli-build.js` (in your project's root directory) is a NodeJS file where you can write a custom build script.  For example, if you want to include a 3rd party JS library like [d3](http://www.d3js.org) in your build, you could do so in your build script like this:

```js
app.import(app.bowerDirectory + "/d3/d3.js");
```

Note that the snippet above assumes the d3 library has already been downloaded into your app's bower directory (typically `bower_components/`). To download it, you may use a bower command such as `bower install d3 --save`. To learn more about bower, read the "What is bower?" section below.

You may be surprised to find that `ember-cli-build.js` is mostly empty out-of-the-box, and yet Ember is still doing a lot when you build your app. That is because the Ember build process has many customization hooks aside from `ember-cli-build.js`.  Most notably, any Ember Addons you install can implement hooks that will be called by the build process.  Therefore, Ember Addons may be doing all kinds of things during the build that are not mentioned in your `ember-cli-build.js`.  To learn more about Ember Addons, read the [Ember Addon FAQ](ember-addon-faq.md).

## What tools do I need to know before I start using Ember?

Ember is an open source framework, and it leverages other open source resources too.  So you may find yourself leveraging various tools that you weren't familiar with before.  Here's a list of a few to help you start getting familiarized. Each of these is covered  briefly in later sections of this document:

1. node
2. npm
3. bower
4. Git and GitHub
5. CSS pre-processors, such as SASS

## What is node? How is it relevant to Ember?

Node (also called "NodeJS") is an open-source run-time environment for developing server-side web applications.  Node apps are written in JavaScript.  

Ember CLI requires Node, so you'll have to [install Node](http://ember-cli.com/user-guide/#node) if you don't have it already. (Personally, I was able to install it easily using [homebrew](http://brew.sh/) on Mac OS X, thanks to the fact that OS X comes with Ruby already installed.)

Note: if you are ready to install Node, make sure you do so without using the `sudo` command.  Otherwise you'll end up with pesky access issues.  For more info, read [this article](http://www.wenincode.com/installing-node-jsnpm-without-sudo/).

## What is npm? How is it relevant to Ember?

npm is a tool for downloading open-source packages.  Developers can use npm to install 3rd-party open-source packages in your Ember apps. The packages will typically be downloaded into your app's `node_modules/` subdirectory, and they'll be listed in your app's `package.json` file.

If you're unfamiliar with npm, read [this helpful article](https://docs.nodejitsu.com/articles/getting-started/npm/what-is-npm), summarized briefly below.

Node apps can be comprised of packages. Wouldn't it be nice if there were lots of open-source packages available online for Node developers to use in their apps?  Well, there are, and npm helps you get them.

npm, short for "Node Package Manager", is really two things:

1. An online repository of open-source Node.js projects.
2. A command-line utility for fetching-from/publishing-to that repository above.

If you want to search for node.js libraries and applications published on npm, look here: http://search.npmjs.org/

To install a 3rd party package into your Node app, call `npm install <package>`.  The package is then copied in your Node app's `node_modules/` subfolder. Now you can use it in your app's Node code by using `require(..)` statements.

If you add the `--save` flag to the `npm install` command, then the package's name will be added to your Node app's `package.json` file. This way, the `package.json` file essentially lists the dependencies for your app.

If you write a Node app that has dependencies on other packages, you can publish the app along with its `package.json` file, without publishing the `node_modules/` subdirectory that has the actual source for those dependencies.  Anyone who wants to install and run your app can still do so.  After they download your code, they just have to run the command `npm install` in the path of your app's `package.json` file.  The `npm install` command will find the `package.json` file and install all the dependencies listed there into your app's `node_modules/` subdirectory.  The bottom line is that you don't have to include your dependencies' code in your app's repo; you just need to include the `package.json` file.

Note that you can also install a Node package "globally", meaning that it will be available as a standalone command in Terminal. This is not usually done.  Usually node packages are installed "locally", meaning inside of a specific Node app.  One example of a global Node package is Ember CLI.  It is installed like this:

```text
npm install -g ember-cli
```

The `-g` flag causes  `npm install` to put a symlink to the package (`ember-cli`) in your `/usr/local/bin`, so you can run the package from any command line.

## What is bower? How is it relevant to Ember?

If you read the section "What is npm?" above, you know about installing open-source node packages into your Ember app.  But what if you want to install a simple client-only JavaScript library, like jQuery?  That's not a node package, so you wouldn't expect to find it in the npm online repository. You might find it on GitHub. Is there another tool for installing open-source projects from GitHub?  Yes, there is. It's called "bower".

Bower is essentially a shortcut for Git.  It can go and fetch GitHub files for you.  It's un-opinionated, meaning that it doesn't care what kind of files it fetches (JS, CSS, HTML, etc).  In fact, since Bower is lightweight and low-level, it is sometimes used by higher-level package managers, such as [Yeoman](http://yeoman.io/).

Use `bower search <keyword>` to search GitHub for projects with that `<keyword>` in their name.  

Use `bower install <projectname>` to download the source for the given `<projectname>`. In an Ember app, bower will download the source into your Ember app's `bower_components/` subdirectory.  This is similar to npm and it's `node_modules/` subdirectory.

If you add a `--save` flag, then `bower install --save <projectname>` will register that project in your Ember app's `bower.json` file.  This is similar to npm and it's `package.json` file.

Also similarly to npm, you can run the `bower install` command in the path of your `bower.json` file, and bower will install all the dependencies registered in the `bower.json` file.  This is similar to the `npm install` command and its use of the `package.json` file.

To uninstall a bower package, use `bower uninstall <package>`

For performance, bower uses caching.  When bower installs, it caches the downloaded code internally to `~/.bower/<package>` so that it can install from cache next time. To clear the cache, do `bower cache-clean`.

## Should I use bower or npm to install a dependency?

At times, you may find that the dependency you want is available from both bower and npm. So which should you use? Here's a general rule of thumb: if you need the dependency to execute any code on the server, use npm.  Otherwise use bower.

This means that for simple in-browser javascript libraries (such as jQuery, d3, dropJS, clipboardJS, highlightJS, etc.), you can use bower.  For [Ember Addons](ember-addon-faq.md), use npm.

## How explicit do I need to be with version numbers for my dependencies?

The files `bower.json` and `package.json` allow you to specify the version number for each dependency with wildcards.  How precise should the version numbers be? The answer may vary depending on the dependency, but here's a general rule of thumb.  Many dependencies adhere to [semantic versioning](http://semver.org/), meaning that minor releases shouldn't introduce incompatible API changes but can introduce new functionality. Therefore, generally speaking, our version numbers should specify a major version plus a minimum minor version.  This can be done using a tilde (`~`), as explained in the [npm docs](https://docs.npmjs.com/misc/semver#tilde-ranges-1-2-3-1-2-1).

## Does Ember require jQuery?

No, but you can use jQuery within your Ember code, if desired.  In fact, if you do install jQuery, Ember gives you a shortcut to it, using `Ember.$`.  So for example, if you are writing code for your custom component and you wish to use jQuery to access the component's DOM, you could use `Ember.$(this.element)`.

## What is a CSS pre-processor?

A CSS pre-processor converts source code written in some other language (e.g., SASS) into CSS code.  For more details, check out the [CSS FAQ](css-faq.md).
