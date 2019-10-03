import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import { findAll, render } from '@ember/test-helpers';

module('Integration | Component | Profile Selector', function(hooks) {
  setupRenderingTest(hooks);

  hooks.beforeEach(function() {
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  const profileSelectorSelector = '.rsa-investigate-query-container__profile-selector';
  const listManagerSelector = '.list-manager';

  test('it renders with list manager', async function(assert) {
    this.set('profiles', []);
    this.set('listName', 'Query Profiles');
    this.set('stateLocation', 'listManagers.profiles');

    await render(hbs`{{profile-selector
      profiles=profiles
      listName=listName
      stateLocation=stateLocation
    }}`);

    assert.equal(findAll(profileSelectorSelector).length, 1, 'Expected root DOM element.');
    assert.equal(findAll(listManagerSelector).length, 1, 'Shall display list manager');
  });
});
