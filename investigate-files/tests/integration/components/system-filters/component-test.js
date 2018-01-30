import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import { applyPatch, revertPatch } from '../../../helpers/patch-reducer';
import wait from 'ember-test-helpers/wait';
import engineResolver from '../../../helpers/engine-resolver';
import sinon from 'sinon';
import * as DataCreators from 'investigate-files/actions/data-creators';
import ReduxDataHelper from '../../../helpers/redux-data-helper';

let setState;

moduleForComponent('system-filters', 'Integration | Component | System Filters', {
  integration: true,
  resolver: engineResolver('investigate-files'),
  beforeEach() {
    setState = (state) => {
      applyPatch(state);
      this.inject.service('redux');
    };
  },
  afterEach() {
    revertPatch();
  }
});

test('System filter list is rendered', function(assert) {
  assert.expect();
  this.render(hbs`{{system-filters}}`);
  assert.equal(this.$('.filter-list').length, 1, 'Filter list rendered');
  assert.equal(this.$('.filter-list .filter-list__item').length, 3, '3 items of filter list');
  assert.equal(this.$('.filter-list .filter-list__item:eq(0)').text().trim(), 'WINDOWS', 'First item is Windows');
  assert.equal(this.$('.filter-list .filter-list__item:eq(1)').text().trim(), 'LINUX', 'Second item is Linux');
  assert.equal(this.$('.filter-list .filter-list__item:eq(2)').text().trim(), 'MAC', 'Third item is Mac');
});

sinon.stub(DataCreators, 'addSystemFilter');
sinon.stub(DataCreators, 'setSystemFilterFlag');

test('Values passed in apply filter action should be rendered', function(assert) {
  assert.expect();
  new ReduxDataHelper(setState).isSystemFilterForFiles(true).build();
  this.render(hbs`{{system-filters}}`);
  assert.equal(this.$('.filter-list').find('li').hasClass('is-active'), false, 'No item is active by default');
  this.$('.filter-list').find('li:eq(0)').click();
  return wait().then(() => {
    assert.equal(DataCreators.addSystemFilter.calledOnce, true, 'add system filter action called');
    assert.equal(DataCreators.addSystemFilter.args[0][0].length, 1, 'Length of parameters rendered');
    assert.equal(DataCreators.setSystemFilterFlag.calledOnce, true, 'system filter flag is set');
    assert.equal(DataCreators.setSystemFilterFlag.args[0][0], true, 'system filter flag value rendered');
    assert.equal(this.$('.filter-list').find('li:eq(0)').hasClass('is-active'), true, '1st filter item is active now');
    DataCreators.addSystemFilter.restore();
    DataCreators.setSystemFilterFlag.restore();
  });
});