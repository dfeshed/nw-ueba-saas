import { module, test } from 'qunit';
import hbs from 'htmlbars-inline-precompile';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import ReduxDataHelper from '../../../../../../helpers/redux-data-helper';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { patchReducer } from '../../../../../../helpers/vnext-patch';
import { find, findAll, render } from '@ember/test-helpers';
import EventColumnGroups from '../../../../../../data/subscriptions/column-group/findAll/data';
import { mapColumnGroupsForEventTable } from 'investigate-events/util/mapping';

let setState;

module('Integration | Component | Column Group Details', function(hooks) {
  let mappedColumnGroups;

  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-events')
  });

  hooks.before(function() {
    mappedColumnGroups = mapColumnGroupsForEventTable(EventColumnGroups);
  });

  hooks.beforeEach(function() {
    setState = (state) => {
      patchReducer(this, state);
    };
    initialize(this.owner);
  });

  const DISPLAYED_COLUMNS = '.group-details > ul.column-list li';
  const AVAILABLE_META = '.add-details > ul.column-list li';

  const getTextFromDOMArray = (arr) => {
    return arr.reduce((a, c) => a + c.textContent.trim().replaceAll(' ', ''), '');
  };

  test('columnGroup details should render column details when an item is available', async function(assert) {

    this.set('columnGroup', mappedColumnGroups[3]);
    await render(hbs`{{events-table-container/header-container/column-groups/column-group-details columnGroup=columnGroup}}`);

    assert.ok(find('.column-group-view'), 'Column Group Details rendered correctly');
    assert.equal(find('.group-name .name').textContent.trim(), 'Group Name');
    assert.equal(find('.group-name .value').textContent.trim(), 'Summary List');
    assert.equal(find('.group-details .name').textContent.trim(), 'Displayed Meta Keys');
    assert.equal(findAll(DISPLAYED_COLUMNS).length, 5, '5 columns for Summary List rendered');

    const metaKeys = findAll(`${DISPLAYED_COLUMNS} span:first-of-type`);
    assert.equal(getTextFromDOMArray(metaKeys), 'custom.meta-detailscustom.thememediumsizetime', 'Displayed meta keys in alphabetical order');
  });

  test('columnGroup details should render an edit form to create a new column group', async function(assert) {

    this.set('columnGroup', null);
    new ReduxDataHelper(setState).metaKeyCache().build();
    await render(hbs`{{events-table-container/header-container/column-groups/column-group-details columnGroup=columnGroup}}`);

    assert.ok(find('.column-group-form'), 'Column Group Details rendered correctly');
    assert.equal(find('.group-name .name').textContent.trim(), 'Group Name');
    assert.ok(find('.group-name .value input'), 'input for group name');
    assert.equal(find('.group-details .name').textContent.trim(), 'Displayed Meta Keys');
    assert.equal(findAll(DISPLAYED_COLUMNS).length, 0, 'No columns present in displayed keys');
    assert.equal(find('.add-details .name').textContent.trim(), 'Available Meta Keys');
    assert.equal(findAll(AVAILABLE_META).length, 95, '95 meta keys available');

  });

});
