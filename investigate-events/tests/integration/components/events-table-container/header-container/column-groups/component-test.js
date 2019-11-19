import { module, test } from 'qunit';
import hbs from 'htmlbars-inline-precompile';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { patchReducer } from '../../../../../helpers/vnext-patch';
import ReduxDataHelper from '../../../../../helpers/redux-data-helper';
import EventColumnGroups from '../../../../../data/subscriptions/column-group';
import { find, render, click } from '@ember/test-helpers';

let setState;

module('Integration | Component | Column Groups', function(hooks) {

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

  // selectors
  const columnGroupListManager = '.rsa-investigate-events-table__header__columnGroups';
  const listMenuTriggerButton = '.list-menu-trigger button.rsa-form-button';
  const dropdownSelector = `${columnGroupListManager} .rsa-split-dropdown button`;
  const dropdownSelectorDisabled = `${columnGroupListManager} .rsa-split-dropdown.is-disabled`;

  test('columnGroup manager should be visible', async function(assert) {
    new ReduxDataHelper(setState).selectedColumnGroup('SUMMARY').columnGroups(EventColumnGroups).build();
    await render(hbs`{{events-table-container/header-container/column-groups}}`);

    assert.ok(find(columnGroupListManager), 'Column Group Manager present');
    assert.equal(find(`${columnGroupListManager} .list-caption`).textContent.trim(), 'Column Group: Summary List',
      'Default column group is Summary List.');
    assert.notOk(find(`${columnGroupListManager} .list-caption .is-disabled`), 'column group is not disabled.');
    assert.notOk(find(dropdownSelectorDisabled), 'dropdown button is not disabled');
  });

  test('columnGroup manager should be visible when disabled', async function(assert) {
    new ReduxDataHelper(setState).selectedColumnGroup('SUMMARY').columnGroups(EventColumnGroups).build();
    const accessControl = this.owner.lookup('service:accessControl');
    const origRoles = [...accessControl.roles];
    // removing column group permissions
    accessControl.set('roles', []);
    await render(hbs`{{events-table-container/header-container/column-groups}}`);
    assert.ok(find(columnGroupListManager), 'Column Group Manager present');
    assert.equal(find(`${columnGroupListManager} .list-caption.is-disabled`).textContent.trim(), 'Column Group: Summary List',
      'Default column group is Summary List.');
    assert.ok(find(dropdownSelector), 'dropdown buttons present');
    await click(listMenuTriggerButton); // should not open list
    assert.notOk(find(`${columnGroupListManager} .rsa-button-menu.expanded`), 'Column Group Menu expanded');
    assert.ok(find(dropdownSelectorDisabled), 'dropdown button disabled');
    // reset roles
    accessControl.set('roles', origRoles);
  });
});
