import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import hbs from 'htmlbars-inline-precompile';
import { findAll, render } from '@ember/test-helpers';
import { patchReducer } from '../../../../helpers/vnext-patch';
import ReduxDataHelper, { DEFAULT_PROFILES } from '../../../../helpers/redux-data-helper';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

let setState;

module('Integration | Component | Profile Selector', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-events')
  });

  hooks.beforeEach(function() {
    setState = (state) => {
      patchReducer(this, state);
    };
    initialize(this.owner);
  });

  const profileSelectorSelector = '.rsa-investigate-query-container__profile-selector';
  const listManagerSelector = '.list-manager';

  test('it does not render profile selector if profiles list does not exist', async function(assert) {
    // creating state with no profiles
    new ReduxDataHelper(setState).build();
    await render(hbs`{{query-container/profile-selector}}`);
    assert.equal(findAll(profileSelectorSelector).length, 1, 'Shall render profile-selector component');
    assert.equal(findAll(listManagerSelector).length, 1,
      'Shall render list manager component if profiles does not exist');
  });

  test('it renders with proper class', async function(assert) {
    new ReduxDataHelper(setState).profiles(DEFAULT_PROFILES).build();
    await render(hbs`{{query-container/profile-selector}}`);
    assert.equal(findAll(profileSelectorSelector).length, 1, 'Shall render profile-selector component with proper class');
    assert.equal(findAll(listManagerSelector).length, 1,
      'Shall render list manager if profiles exists');
  });

  test('renders list manager in disabled state when profile read permissions are removed', async function(assert) {
    new ReduxDataHelper(setState).profiles([]).build();
    const accessControl = this.owner.lookup('service:accessControl');
    const origRoles = [...accessControl.roles];
    // removing profile read permission
    accessControl.set('roles', []);
    await render(hbs`{{query-container/profile-selector}}`);
    assert.equal(findAll(profileSelectorSelector).length, 1,
      'Shall render profile-selector component if profiles exists');
    assert.equal(findAll(listManagerSelector).length, 1, 'Should display list manager');
    // reset roles
    accessControl.set('roles', origRoles);
  });
});
