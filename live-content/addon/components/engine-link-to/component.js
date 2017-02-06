import Ember from 'ember';

const { VERSION } = Ember;

function hasEmberVersion(major, minor) {
  const numbers = VERSION.split('-')[0].split('.');
  const actualMajor = parseInt(numbers[0], 10);
  const actualMinor = parseInt(numbers[1], 10);
  return actualMajor > major || (actualMajor === major && actualMinor >= minor);
}

const attributeMungingMethod = (function() {
  if (hasEmberVersion(2, 10)) {
    return 'didReceiveAttrs';
  } else {
    return 'willRender';
  }
})();

const {
    LinkComponent,
    getOwner,
    get,
    set
} = Ember;

// TODO: HACK - to resolve issue fixed in ember engine PR: https://github.com/ember-engines/ember-engines/pull/288
// TODO: Once upgrade to ember engines 0.5 is complete, remove this component and replace in live-content vertically
// TODO: stacked nav bar with original link-to

export default LinkComponent.extend({
  [attributeMungingMethod]() {
    this._super(...arguments);

    const { mountPoint } = getOwner(this);

    if (mountPoint) {
      // Prepend engine mount point to targetRouteName
      const fullRouteName = `${mountPoint}.${get(this, 'targetRouteName')}`;
      set(this, 'targetRouteName', fullRouteName);

      // Prepend engine mount point to current-when if set
      const currentWhen = get(this, 'current-when');
      if (currentWhen !== null) {
        const fullCurrentWhen = `${mountPoint}.${currentWhen}`;
        set(this, 'current-when', fullCurrentWhen);
      }
    }
  }
});
