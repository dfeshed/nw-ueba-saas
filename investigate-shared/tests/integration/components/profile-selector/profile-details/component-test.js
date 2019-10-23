import { module, test } from 'qunit';
import hbs from 'htmlbars-inline-precompile';
import { setupRenderingTest } from 'ember-qunit';
import { find, render } from '@ember/test-helpers';

module('Integration | Component | Profile Details', function(hooks) {
  setupRenderingTest(hooks);

  const profileViewSelector = '.profile-view';
  const profileFormSelector = '.profile-form';

  const profile1 = {
    name: 'RSA Email Analysis',
    metaGroup: {
      name: 'RSA Email Analysis'
    },
    columnGroupView: 'CUSTOM',
    columnGroup: {
      name: 'RSA Email Analysis'
    },
    preQuery: 'service=24,25,109,110,995,143,220,993',
    contentType: 'OOTB'
  };

  test('profile details should render profile-view when an item is available', async function(assert) {
    const editProfile1 = () => {};
    this.set('profile', profile1);
    this.set('editProfile', editProfile1);
    this.set('metaGroups', []);
    this.set('columnGroups', []);
    this.set('profiles', [profile1]);
    await render(hbs`{{profile-selector/profile-details
      profile=profile
      editProfile=editProfile
      profiles=profiles
      columnGroups=columnGroups
      metaGroups=metaGroups}}`);
    assert.ok(find(profileViewSelector), 'Shall render profile-view');
    assert.notOk(find(profileFormSelector), 'Shall not render profile-form');
  });

  test('profile details should render profile-view when an item is available', async function(assert) {
    const editProfile1 = () => {};
    this.set('profile', {});
    this.set('editProfile', editProfile1);
    this.set('metaGroups', []);
    this.set('columnGroups', []);
    this.set('profiles', [profile1]);
    await render(hbs`{{profile-selector/profile-details
      profile=profile
      editProfile=editProfile
      profiles=profiles
      columnGroups=columnGroups
      metaGroups=metaGroups}}`);
    assert.notOk(find(profileViewSelector), 'Shall not render profile-view');
    assert.ok(find(profileFormSelector), 'Shall render profile-form');
  });
});
