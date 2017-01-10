# Engine Template

1. Copy the contents of this folder to the root of `sa_ui` and update the name to your desired name
2. `cd` into the new folder
3. Run `bower i`
4. Run `yarn`
5. Change all instances of `changeMe` inside the folder with the name of your engine
6. Modify the `package.json` `ember-addon` property to reflect the addons you will be using.
7. Run `ember s`

You should now have a dummy app showing the engine running at `http://localhost:4200`

__IMPORTANT:__ This WILL NOT run from its location inside `docs`, it must be copied to the root of `sa_ui` before it can be used.