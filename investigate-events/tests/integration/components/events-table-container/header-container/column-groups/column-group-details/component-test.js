import { module, test } from 'qunit';
import hbs from 'htmlbars-inline-precompile';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { find, findAll, render } from '@ember/test-helpers';
import EventColumnGroups from '../../../../../../data/subscriptions/column-group/findAll/data';
import { mapColumnGroupsForEventTable } from 'investigate-events/util/mapping';

module('Integration | Component | Column Group Details', function(hooks) {
  let mappedColumnGroups;

  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-events')
  });

  hooks.before(function() {
    mappedColumnGroups = mapColumnGroupsForEventTable(EventColumnGroups);
  });

  hooks.beforeEach(function() {
    initialize(this.owner);
  });

  const COLUMNS = '.group-details ul.column-list li';

  const getTextFromDOMArray = (arr) => {
    return arr.reduce((a, c) => a + c.textContent.trim().replace(/\s+/g, ''), '');
  };


  test('columnGroup details should render column details', async function(assert) {

    this.set('item', mappedColumnGroups[3]);
    await render(hbs`{{events-table-container/header-container/column-groups/column-group-details columnGroup=item}}`);

    assert.ok(find('.column-group-details'), 'Column Group Details rendered correctly');
    assert.equal(find('.group-name .name').textContent.trim(), 'Group Name');
    assert.equal(find('.group-name .value').textContent.trim(), 'Summary List');
    assert.equal(find('.group-details .name').textContent.trim(), 'Displayed Meta Keys');
    assert.equal(findAll(COLUMNS).length, 5, '5 columns for Summary List rendered');

    const metaKeys = findAll(`${COLUMNS} span:first-of-type`);
    assert.equal(getTextFromDOMArray(metaKeys), 'custom.meta-detailscustom.thememediumsizetime', 'Displayed meta keys in alphabetical order');
  });

});
