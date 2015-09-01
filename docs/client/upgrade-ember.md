# Upgrade ember-cli and ember

* npm uninstall -g ember-cli -- Remove old global ember-cli
* npm cache clean -- Clear NPM cache
* bower cache clean -- Clear Bower cache
* npm install -g ember-cli@<i>version number</i> -- Install new global ember-cli

# SA and dashboard changes
Follow these steps in sa and dashboard directory
* rm -rf node_modules bower_components dist tmp -- Delete temporary development folders.
* npm install --save-dev ember-cli@<i>version number</i> -- Update project's package.json to use latest version.
* npm install -- Reinstall NPM dependencies.
* bower install -- Reinstall bower dependencies.
* ember init -- This runs the new project blueprint on your projects directory. Please follow the prompts,
and review all changes (tip: you can see a diff by pressing d). If you are checking out changes from github after
an upgrade, skip this step.
* Ensure the latest version of ember and ember-cli gets updated in package.json and bower.json.


Please make sure latest version of ember is used in "SA" app and "dashboard" app.

Once the upgrade is successful,
* Launch both sa and dashboard app and make sure it works without issues
* Run unit tests in sa and dashboard
* Test the pages that uses thrid-party addons and make sure the functionality is not broken
* If there are new deprecation warnings in our source code, plan to fix them


# List of addons used by SA
* [ember-cli-blanket](https://github.com/sglanzer/ember-cli-blanket)
* [ember-cli-mirage](https://github.com/samselikoff/ember-cli-mirage)
* [ember-cli-simple-auth](https://github.com/simplabs/ember-cli-simple-auth)
* [ember-cli-simple-auth-testing](https://github.com/simplabs/ember-cli-simple-auth-testing)
* [ember-i18n](https://github.com/jamesarosen/ember-i18n)
* [liquid-fire](https://github.com/ef4/liquid-fire)
* [mock-socket](https://github.com/thoov/mock-socket)
* [pretender](https://github.com/pretenderjs/pretender)
* [sockjs](https://github.com/sockjs)
* [stomp-websocket](https://github.com/jmesnil/stomp-websocket)

# List of addons used by dashboard
* [foundation-apps](https://github.com/zurb/foundation-apps)
* [highlightjs](https://highlightjs.org)
