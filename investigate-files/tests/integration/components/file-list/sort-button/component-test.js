import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, settled, findAll, click } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';

module('Integration | Component | file-list/sort button', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-files')
  });

  test('it renders', async function(assert) {
    assert.expect(3);
    this.set('column', {
      name: 'firstSeenTime',
      description: 'First seen time',
      dataType: 'DATE',
      searchable: false,
      wrapperType: 'STRING',
      sortField: 'firstSeenTime'
    });
    this.set('sortField', 'firstSeenTime');
    this.set('sortBy', function(field, sortDirection) {
      this.set('isSortDescending', sortDirection);
      assert.ok(true);
    });
    this.set('closeRiskPanel', function() {
      assert.ok(true);
    });
    await render(hbs` <style>
        box, section {
          min-height: 1000px
        }
      </style>{{file-list/sort-button
        sortField=sortField
        column=column
        isSortDescending=false
        closeRiskPanel=closeRiskPanel
        sortBy=sortBy}}`);

    assert.equal(findAll('.rsa-icon-arrow-up-7').length, 1, 'Icon indicates ascending order');
    await click('.column-sort');
    return settled().then(() => {
      assert.equal(findAll('.rsa-icon-arrow-down-7').length, 1, 'Icon indicates descending order');
    });

  });
});
