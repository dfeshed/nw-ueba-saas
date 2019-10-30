import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import hbs from 'htmlbars-inline-precompile';
import { find, findAll, render, click } from '@ember/test-helpers';
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
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  const profileSelectorSelector = '.rsa-investigate-query-container__profile-selector';
  const listManagerSelector = '.list-manager';
  const dropdownSelector = `${profileSelectorSelector} .rsa-split-dropdown button`;

  test('it does not render profile selector if profiles list does not exist', async function(assert) {
    // creating state with no profiles
    new ReduxDataHelper(setState).build();
    await render(hbs`{{query-container/profile-selector}}`);
    assert.equal(findAll(profileSelectorSelector).length, 1, 'Shall render profile-selector component');
    assert.equal(findAll(listManagerSelector).length, 0,
      'Shall not render list manager component if profiles does not exist');
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

  test('Save is disabled when profile form has not been edited at all', async function(assert) {

    new ReduxDataHelper(setState).profiles(DEFAULT_PROFILES).metaGroups().getColumns().build();
    await render(hbs`{{query-container/profile-selector}}`);
    assert.equal(findAll(dropdownSelector).length, 1, 'Shall render profile-selector component with proper class');

    await click(dropdownSelector);
    // create new profile
    await click(findAll('footer button')[0]);

    assert.ok(find('footer .close'), 'Close option available');
    assert.ok(find('footer .save.is-disabled'), 'disabled-save option available when new profile created is not valid');
  });

  test('Save disabled and Close button available when not all required fields valid', async function(assert) {
    new ReduxDataHelper(setState)
      .profiles(DEFAULT_PROFILES)
      .metaGroups()
      .columnGroups()
      .getColumns()
      .build();

    await render(hbs`{{query-container/profile-selector}}`);
    assert.equal(findAll(dropdownSelector).length, 1, 'Shall render profile-selector component with proper class');

    await click(dropdownSelector);
    // create new profile, but do not provide a name
    await click(findAll('footer button')[0]);

    assert.ok(find('footer .close'), 'Close option available');
    assert.ok(find('footer .save.is-disabled'), 'Save option shall render disabled');
  });
});
