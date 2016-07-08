# FAQ: Ember Addons

## What is an Ember Addon?

Addons are Ember projects that you can use in your Ember apps. The Ember CLI docs point to [this article](https://dockyard.com/blog/2014/06/24/introducing_ember_cli_addons) as an introduction, but [this EmberJS Seattle meetup video](https://www.youtube.com/watch?v=q7OHEh8buj8) might be easier to digest.  There's also a markdown document in the ember-cli GitHub repo about [Addon Hooks](https://github.com/ember-cli/ember-cli/blob/master/ADDON_HOOKS.md).

To add an addon to your ember app, use Ember CLI's `ember install <package>` command. This command uses npm to download the addon into your app's `node_modules/` subdirectory.

## How is an Ember Addon different form an Ember App?

An addon has a slightly different project structure than an app.  Here are some notable subdirectories you'll find in your addon project structure:

* Typically, most of the addon's code lives within an `addon/` subdirectory.  The code in `addon/` falls under the addon's namespace. (Note: as of May 2015, there's an issue with using "pod" folder structures within the addon, so you should avoid doing so for now.)
* Specifically, the addon's components typically live in `addon/components/`.  This means that those components will be under the addon's namespace.
* The addon's `app/` subdirectory is for code to be shared with the consuming app's namespace.
* The addon's `public/` subdir is for assets that the consuming app can use via the addon namespace (e.g.: `.foo { background: url("myaddon/images/foo.png"); }`)
* The addon's `tests/dummy/` subdirectory contains a dummy app that is used to host your addon for testing.
* The addon does have a build script, but it's not used by the consuming app. It's only used for building the app in `tests/dummy/`.

## How do I make an Ember Addon?

Use Ember CLI's `ember addon <addon-name>` command.  Analogous to the `ember new` command, the `ember addon` command will generate a new project directory for your addon, with a folder structure like the one discussed above.

Once your addon is built, you can implement things in it.  For example, you could fill up its `addon/components/` directory with components that other ("consuming") apps can then re-use.

A few issues to be aware of:

1. The command for creating a new addon is `ember addon <name>`, *not* `ember generate addon <name>`.

1. According to [this EmberJS Seattle meetup video](https://www.youtube.com/watch?v=q7OHEh8buj8)

2. Also according to the same video mentioned above, if your addon uses any templates (`*.hbs`), you may get an error (about missing a template compiler) when you try to build your addon.  If so, you can correct this error, but adding an entry in your addon's `package.json` for the template compiler you want to use. Note that the entry must go in the "dependencies" section, not the "devDependencies".  For example:

```json
  "dependencies": {
    "ember-cli-htmlbars": "0.7.9"
  }
```

## How do I make my Addon's Components available for use in the consuming App's templates?

By default, the addon's components will be located in the addon's `addon/components/` directory.  Therefore, they fall under the addon's namespace, not the consuming app's namespace.  So they are not "visible" to the consuming app's templates.

However, your addon can make them accessible to the consuming app's templates by "bridging" them from within your addon, like this:

1. In the addon's `app/components/` subdirectory, define a file corresponding to the component you wish to bridge (e.g., `app/components/my-comp.js`).
2. In that file, import the component from `<addon-name>/components/my-comp` and then export that same Component, like this:

```js
// file: <my-addon>/app/components/my-comp:
import MyComp from "<my-addon>/components/my-comp";
export default MyComp;
```

Now the addon's component is published under the consuming app's namespace and can be used in the app's templates (e.g., `{{my-comp}}`).

## How do I tell my App to include my local Addon?

Normally you install addons from the internet using the `ember install` command. But if you want to "install" your own addon, and its code is on your local machine, then you can add a reference to the add in the consuming app's `package.json`.

For example, suppose your app is in the directory `app1/` and your addon is in the sibling directory `addon1/`.  Then you would add the following in the `app1/package.json` file:

```json
  "ember-addon": {
    "paths": [
        "../addon1"
    ]
  }
```
