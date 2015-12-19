# Upgrade ember-cli and ember

* npm uninstall -g ember-cli -- Remove old global ember-cli
* npm cache clean -- Clear NPM cache
* bower cache clean -- Clear Bower cache
* npm install -g ember-cli@<i>version number</i> -- Install new global ember-cli

# SA, Component Library & Style Guide Changes
Follow these steps in each of these sub-directories of client: sa, component-lib & style-guide.
* rm -rf node_modules bower_components dist tmp -- Delete temporary development folders.
* npm install --save-dev ember-cli@<i>version number</i> -- Update project's package.json to use latest version.
* npm install -- Reinstall NPM dependencies.
* bower install -- Reinstall bower dependencies.
* ember init -- This runs the new project blueprint on your projects directory. Please follow the prompts,
and review all changes (tip: you can see a diff by pressing d). If you are checking out changes from github after
an upgrade, skip this step.
* Ensure the latest version of ember and ember-cli gets updated in package.json and bower.json.


Please make sure latest version of ember is used in the "sa" app, "component-lib" addon, and "style-guide" app.

Once the upgrade is successful,
* Try launching each of the "sa" app and "style-guide" app (separately, one at a time) and make sure they each work without issues
* Run unit tests in sa, component-lib and style-guide
* Test the pages that uses third-party addons and make sure the functionality is not broken
* If there are new deprecation warnings in our source code, plan to fix them


# List of addons used by SA
* [crossfilter](https://github.com/square/crossfilter)
* [ember-cli-blanket](https://github.com/sglanzer/ember-cli-blanket)
* [ember-cli-mirage](https://github.com/samselikoff/ember-cli-mirage)
* [ember-cli-sass](https://github.com/aexmachina/ember-cli-sass)
* [ember-i18n](https://github.com/jamesarosen/ember-i18n)
* [ember-simple-auth](https://github.com/simplabs/ember-simple-auth)
* [liquid-fire](https://github.com/ef4/liquid-fire)
* [mock-socket](https://github.com/thoov/mock-socket)
* [moment](https://github.com/moment/moment/)
* [pretender](https://github.com/pretenderjs/pretender)
* [sockjs](https://github.com/sockjs)
* [stomp-websocket](https://github.com/jmesnil/stomp-websocket)
* [tether-drop](http://github.hubspot.com/drop/)
* [phantomjs](http://phantomjs.org)

# List of addons used by dashboard
* [ember-cli-sass](https://github.com/aexmachina/ember-cli-sass)
* [clipboardjs](https://clipboardjs.com/)
* [highlightjs](https://highlightjs.org)
