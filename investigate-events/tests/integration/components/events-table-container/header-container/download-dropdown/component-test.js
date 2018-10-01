import { module, test } from 'qunit';
import hbs from 'htmlbars-inline-precompile';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import ReduxDataHelper from '../../../../../helpers/redux-data-helper';
import { patchReducer } from '../../../../../helpers/vnext-patch';
import { find, render } from '@ember/test-helpers';

const downloadSelector = '.rsa-investigate-events-table__header__downloadEvents';
const downloadTitle = '.rsa-investigate-events-table__header__downloadEvents span';

let setState;

module('Integration | Component | Download Dropdown', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-events')
  });

  hooks.beforeEach(function() {
    this.owner.inject('component', 'i18n', 'service:i18n');
    const accessControl = this.owner.lookup('service:accessControl');
    accessControl.set('hasInvestigateContentExportAccess', true);
    setState = (state) => {
      patchReducer(this, state);
    };
    initialize(this.owner);
  });

  test('download option should be visible if user has permissions', async function(assert) {
    new ReduxDataHelper(setState).allEventsSelected(false).withSelectedEventIds().build();
    await render(hbs`{{events-table-container/header-container/download-dropdown}}`);
    assert.ok(find(downloadSelector), 'Download option present');
  });

  test('download dropdown should be hidden if missing permissions', async function(assert) {
    const accessControl = this.owner.lookup('service:accessControl');
    accessControl.set('hasInvestigateContentExportAccess', false);
    new ReduxDataHelper(setState)
     .allEventsSelected(false)
     .withSelectedEventIds()
     .build();
    await render(hbs`{{events-table-container/header-container/download-dropdown}}`);
    assert.notOk(find(`${downloadSelector} .ember-power-select`), 'Download option not present');
  });

  test('download dropdown should read Download All if selectAll is checked', async function(assert) {
    new ReduxDataHelper(setState)
      .allEventsSelected(true)
      .build();
    await render(hbs`{{events-table-container/header-container/download-dropdown}}`);
    assert.equal(find(downloadTitle).textContent.trim(), 'Download All', 'Download dropdown should read `Download All` if selectAll is checked');
  });

  test('download dropdown should read Download if selectAll is not checked', async function(assert) {
    new ReduxDataHelper(setState)
      .allEventsSelected(false)
      .withSelectedEventIds()
      .build();
    await render(hbs`{{events-table-container/header-container/download-dropdown}}`);
    assert.equal(find(downloadTitle).textContent.trim(), 'Download', 'Download dropdown should read `Download` if selectAll is not checked');
  });

  test('download dropdown should be enabled if all or 1+ events are selected ', async function(assert) {
    new ReduxDataHelper(setState)
      .allEventsSelected(false)
      .withSelectedEventIds()
      .build();
    await render(hbs`{{events-table-container/header-container/download-dropdown}}`);
    assert.notOk(find(`${downloadSelector}.is-disabled`), 'Download is enabled');
  });

  test('download dropdown should be disabled if no events are selected ', async function(assert) {
    new ReduxDataHelper(setState)
      .allEventsSelected(false)
      .build();
    await render(hbs`{{events-table-container/header-container/download-dropdown}}`);
    assert.ok(find(`${downloadSelector}.is-disabled`), 'Download is disabled');
  });
});