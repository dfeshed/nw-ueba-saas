import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';

module('Integration | Component | endpoint/file-details-panel', function(hooks) {
  setupRenderingTest(hooks);

  test('it renders', async function(assert) {
    const fileStatusHistory = [
      { fileStatus: 'white list', category: 'some', lastModifiedBy: 'admin', lastModifiedOn: 1531848590, comment: 'abcd' }
    ];
    this.set('fileStatusHistory', fileStatusHistory);

    await render(hbs`{{endpoint/file-details-panel activeDataSourceTab='FILE_DETAILS' data=fileStatusHistory }}`);
    assert.equal(findAll('.status-history__item').length, 1, '1 history entry is present');
  });
});
