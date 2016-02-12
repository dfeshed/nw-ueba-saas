# FAQ: Configuring IntelliJ for Developing an EmberJS App

## Introduction

This document provides some tips for configuring your IntellIJ IDEA to work on an Ember app. (Note: at time of writing, the current IntelliJ version is 14.x.)  Got more tips and/or corrections?  Submit a pull request!

This document assumes that you have an existing directory for your app; either you created a new app from scratch using the `ember init` or `ember new` commands, or you cloned an app from git.  Open the directory in IntelliJ and enjoy these tips below.

## How do I configure indentation?

At present, our convention is to indent with ~~4~~ **2 spaces** instead of tabs in JavaScript and CSS files. (This is a recent change; it was previously 4 not 2.) To configure this in IntelliJ, simply open the `.editorconfig` file in your project's root folder, and ensure that the `indent_style` is `space` and the `indent_size` is 2. Note that these settings may be declared separately for different file extensions. Here's a sample snippet of the file below:

```text
[*.js]
end_of_line = lf
charset = utf-8
trim_trailing_whitespace = true
insert_final_newline = true
indent_style = space
indent_size = 2
```

## How do I configure JSHint?

We use [JSHint](http://jshint.com/about/) to detect problems in JavaScript source code (such as syntax errors or code style violations). IntelliJ can be configured to use JSHint for analyzing your code live as you write it.  

IntelliJS relies on a JSHint configuration file, `.jshintrc`, for this setup.  The `.jshintrc` file in your project's root should automatically be used by your IDE. But if for some reason it isn't, you can explicitly tell the IDE to use it, as follows:

1. Go to the Preferences dialog (in Mac OS X, go to the app menu IntelliJ IDEA > Preferences).
2. In Preferences, search for `jshint`. The search should uncover a section called: `Languages & Frameworks` > `Javascript` > `Code Quality Tools` > `JSHint`.  Select the `JSHint` section.
3. In the `JSHint` section, click the `Enable` checkbox, and then check `Use config files`.
4. There should be a radio button for `Default`, which is supposed to read the `.jshintrc` file in your project root. If that's working correctly, great. But if not, you can try choosing the radio button `Custom configuration file` and then explicitly choose the path of your `.jshintrc` file.

## How do I configure ECMAScript6 (ES2015) support?

[Ember CLI](http://www.ember-cli.com/) encourages your code to be written in [ES6](http://es6-features.org/), also known as [ES2015](https://themeteorchef.com/blog/what-is-es2015/).  Ember CLI will then transpile it to ES5 when you make a build. Therefore you should make sure that your IDE understands ES6 syntax.  If you don't do this, your IDE will mark all your ES6-specific code (e.g., `import` and `export` statements) as errors, which will make your IDE's JSHint functionality useless.

1. In `IntelliJ IDEA` > `Preferences`, go to the section: `Languages & Frameworks` > `Javascript`.  
2. In that section, you'll see a pulldown for picking JavaScript language version.  Choose `ECMAScript 6`.

## Why does IntelliJ freeze up and say it is busy "Indexing"?

Indexing in IntelliJ is great; it allows you to search thru entire directories of source files quickly.  But indexing big directories takes time. There may be entire directories of your Ember project that you will almost never want to search through, and therefore don't need indexing.

When you open an Ember project in IntelliJ IDEA, you may find that the app gets very busy & slow (practically locks up) and the status bar says it is "indexing".  This may be a sign that your IDE is indexing a bunch of files you don't need to index. You can tell IntelliJ not to do that:

1. Go to the `File` menu > `Project Structure...` then click on `Modules`.
2. There is a GUI with a `Sources` tab that shows your project's folder hierarchy. You can click on folders that should not be indexed and then click `Excluded` to exclude them from indexing.  In an Ember project, some of the folders that you might want to exclude from indexing are:
  * `bower_components/`
  * `dist/`
  * `npm_modules/`
  * `public/`
  * `tmp/`
  * `vendor/`
