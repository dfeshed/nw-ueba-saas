# Engine Template

1. Copy the contents of this folder to the root of `sa_ui` into a folder named `changeme`
2. `cd changeme`
3. `ln -s ../node_modules node_modules`
4. `ember s`

You should now have a dummy app showing the engine running at `http://localhost:4200`

# Make your changes

1. To start, change all instances of `changeme` inside the folder with the name of your engine
  - Some instances of `changeme` are merely an indication that code is an example and should be updated.
2. Modify the `package.json` `ember-addon` property to reflect the addons you will be using.
3. Uncomment and use the blocks in `addon/engine.js` and `tests/dummy/app/app.js` that allow for transferring services between app and engine.

__IMPORTANT:__ This WILL NOT run from its location inside `docs`, it must be copied to the root of `sa_ui` before it can be used.