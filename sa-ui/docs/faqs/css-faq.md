# FAQ: Ember & CSS

## What is CSS?

Short for "Cascading Style Sheets", CSS is the language used to format HTML content (i.e., to define the content's layout and cosmetic appearance).

## How do you use CSS to style HTML?

You write CSS code and add it to your HTML document.  To add your CSS code, you don't typically insert the CSS code directly into the HTML document.  Rather, you place the code in a `*.css` file, and then you add a link to that file in your HTML using `<link rel="stylesheet" href="path/to/file.css" />`.

## Are there guidelines for writing CSS code?

Yes, they can be found in the [`asoc/launch-libraries` GitHub repository](https://github.rsa.lab.emc.com/asoc/launch-libraries/blob/master/conventions/web/css-guidelines.md).

## How do I add CSS to my Ember app?

When your Ember app is first created, Ember generates a `app/styles/` subdirectory in your app, with a blank `app.css` file in there.  You can put your CSS in that file, and/or use `@import <file>` statements in there to include other CSS files from the `app/styles/` subdirectory.  

When you tell Ember to build your app (e.g., `ember build` or `ember serve`), Ember will automatically compile `app.css` and bundle all the resulting CSS into a single file named `assets/<myapp>.css`.  Your app's `index.html` comes with a `<link>` to `assets/<myapp>.css`.

## Can my app build have more than 1 output CSS file?

Yes, Ember's build script supports building multiple CSS files. By default, it builds a single CSS file, but the build script can be customized to build multiple files if needed.  For more details, read ember-cli's [Asset Compilation docs](http://www.ember-cli.com/user-guide/#asset-compilation) about building CSS, especially [Configuring Output Paths](http://www.ember-cli.com/user-guide/#configuring-output-paths) about building multiple CSS files.

## What is a CSS pre-processor?

A CSS pre-processor will convert code from some language (such as [SASS](http://sass-lang.com/) or [LESS](http://lesscss.org/)) into CSS.  

You may ask, why would I want to do that? Why not just write CSS? Simply put, SASS & LESS have more features than CSS.

CSS is missing certain features that would make it cleaner and easier to maintain.  To address this, the web development community has asked for certain enhancements to CSS.  However, when browsers didn't respond to those enhancement requests, the web community took matters into their own hands.  They implemented pre-processors.  The pre-processors will take your SASS/LESS code as input, and spit out CSS as output.  The output can be fed to browsers without requiring them to understand anything other than CSS.

## Does Ember require a CSS pre-processor?

No, Ember does not require a CSS pre-processor, nor does Ember dictate any particular CSS framework. We are free to choose a CSS pre-processor (LESS, SASS, other, or none) and a CSS framework (Bootstrap, Foundation, other, roll our own, or none).  

## Which CSS pre-processor should I use? SASS or LESS?

We have chosen SASS in our new Ember-Spring stack. This leaves open the possibility that, in the future, we might also use the Foundation CSS framework (if needed), which is built on top of SASS.

## How do I add a CSS pre-processor to my Ember app?

Use the Ember CLI command `ember install <addon>` to install an Ember addon for the pre-processor you choose. Once that is done, the developer can write SASS code, and Ember will automatically have it compiled to CSS whenever the Ember app is built.

To use the SASS pre-processor:
1. Run command: `ember install ember-cli-sass`
2. Rename your CSS file in `app/styles/` from `app.css` to `app.scss`.
