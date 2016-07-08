# FAQ: Creating a New Ember Project

## How do I create an Ember app from scratch?

You may not need to create an Ember app from scratch in some cases. For example, if you are working on an existing app in GitHub, you can just clone the GitHub repository onto your local development machine.  But if you are building a POC or some new app from scratch, the following simple steps will help you start up a brand new Ember project on your development machine:

1. Install Ember CLI (see [Ember CLI User Guide](http://ember-cli.com/user-guide/) for instructions).

2. In a command line, navigate to the parent folder in which you want to create a new Ember project. Your project will be a new subdirectory within this parent folder.

3. Enter command: `ember new project1`, where `project1` is the name of your new Ember app.
  - This will generate a new subdirectory `project1/` for the app.
  - This will also generate subdirectories inside `project1/`, including most notably the `app/` subdirectory, which is where most of your code will go.
  - This will also fetch some 3rd party dependencies, using node & bower, so it may take a couple of minutes.
  - If you don't intend to use Git for your app, use the `--skip-git` flag. Otherwise, the `ember new` command will create a local git repo for your new project, and do an initial commit.

4. Apply a "pod" folder structure to your project, if desired.
  - Ask yourself: How do you want to organize your source files?  Do you want to group them (a) by file type, or (b) by the module/thing that they correspond to?  If you answered (b), then you want a "pod" folder structure.
  - By convention, we use a "pod" folder structure for our apps.
  - To learn more about what a "pod" folder structure means, read [this article](http://cball.me/organize-your-ember-app-with-pods/).
  - To tell Ember CLI that you want a "pod" structure, open the `.ember-cli` file in your project's root folder, and add this line: `"usePods": true`
  - Older versions of Ember CLI (say, < 0.10) didn't assume a "pod" folder structure, so this step was required.  But in recent versions that may have changed. Therefore this step might not be necessary anymore if you are using the latest Ember CLI version.  Still, it can't hurt.

5. Add a CSS pre-processor to the project, if desired.
  - If you are not familiar with CSS pre-processors, read our [CSS FAQ](cass-faq.md).
  - To add SASS compilation to your project, use this command: `ember install ember-cli-sass`. It install an ember "addon" which will compile your SASS code every time you make an ember build (for example, with the `ember serve` command).
  - Once you've added SASS compilation, just rename your out-of-the-box file `/app/styles/app.css` to `app.scss` and you are ready to use SASS.

# What's next, after setting up my new Ember project?

1. You'll probably want to make sure your IDE is configured to work with Ember files (especially, ECMAScript6 JavaScript files).  If your IDE is IntelliJ IDEA, open up your Ember project's root directory in your IDE and check out our tips in the [IntelliJ & Ember FAQ](intellij-faq.md).

2. Once you have done the above, you are ready to start writing your app.  Your next steps are likely to include: deciding what route URLs your app will support, then implementing the Route, Model and Component objects for those URLs.  You can read more about these concepts in the [Concepts FAQ](project-concepts.md) or the official [EmberJS Guides](http://guides.emberjs.com/v2.2.0/getting-started/core-concepts/).
