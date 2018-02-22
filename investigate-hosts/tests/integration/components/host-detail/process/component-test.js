import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import wait from 'ember-test-helpers/wait';
import engineResolver from '../../../../helpers/engine-resolver';
import { applyPatch, revertPatch } from '../../../../helpers/patch-reducer';
import { processDetails, processList, processTree } from '../../../../integration/components/state/process-data';
import { patchSocket } from '../../../../helpers/patch-socket';
import ReduxDataHelper from '../../../../helpers/redux-data-helper';

let setState;

moduleForComponent('host-detail/process', 'Integration | Component | endpoint host detail/process', {
  integration: true,
  resolver: engineResolver('investigate-hosts'),
  beforeEach() {
    this.registry.injection('component', 'i18n', 'service:i18n');
    setState = (state) => {
      applyPatch(state);
      this.inject.service('redux');
    };
  },
  afterEach() {
    revertPatch();
  }
});

test('it renders data when isProcessDataEmpty is true', function(assert) {

  new ReduxDataHelper(setState)
    .processList([])
    .processTree([])
    .build();
  // set height to get all lazy rendered items on the page
  this.render(hbs`
    {{host-detail/process}}    
  `);

  return wait().then(() => {
    assert.deepEqual(this.$('.process-content-box').length, 0, 'process content box is not present');
    assert.deepEqual(this.$('.process-property-box').length, 0, 'process property box is not present');
  });
});

test('it renders data when isProcessDataEmpty is false', function(assert) {

  new ReduxDataHelper(setState)
    .processList(processList)
    .processTree(processTree)
    .processDetails(processDetails)
    .build();

  // set height to get all lazy rendered items on the page
  this.render(hbs`
    {{host-detail/process}}
  `);

  return wait().then(() => {
    assert.deepEqual(this.$('.process-content-box').length, 1, 'process-content-box');
    assert.deepEqual(this.$('.process-property-box').length, 1, 'process-property-box');
  });
});

test('it should not show toggle tree button when navigating from search result', function(assert) {
  new ReduxDataHelper(setState)
    .selectedTab({ tabName: 'PROCESS' })
    .build();
  // set height to get all lazy rendered items on the page
  this.render(hbs`
    {{host-detail/process}}
  `);

  return wait().then(() => {
    assert.deepEqual(this.$('.toggle-icon').length, 0, 'no toggle icon');
  });
});


test('it should toggle the tree view to list view', function(assert) {
  new ReduxDataHelper(setState)
    .processList(processList)
    .processTree(processTree)
    .processDetails(processDetails)
    .isTreeView(true)
    .build();
  // set height to get all lazy rendered items on the page
  this.render(hbs`
    {{host-detail/process}}
  `);

  return wait().then(() => {
    assert.equal(this.$('.toggle-icon').length, 1, 'toggle icon');
    this.$('.toggle-icon .rsa-icon').click();
    const { endpoint: { visuals: { isTreeView } } } = this.get('redux').getState();
    assert.equal(isTreeView, false, 'It should toggle to list view');
  });
});

test('it should toggle the list view to tree view', function(assert) {
  assert.expect(5);
  new ReduxDataHelper(setState)
    .agentId(1)
    .scanTime(123456789)
    .processList(processList)
    .processTree(processTree)
    .processDetails(processDetails)
    .isTreeView(false)
    .build();
  // set height to get all lazy rendered items on the page
  this.render(hbs`
    {{host-detail/process}}
  `);

  patchSocket((method, modelName, query) => {
    assert.equal(method, 'getProcess');
    assert.equal(modelName, 'endpoint');
    assert.deepEqual(query,
      {
        'data': {
          'agentId': 1,
          'pid': 1,
          'scanTime': 123456789
        }
      });
  });
  return wait().then(() => {
    assert.equal(this.$('.toggle-icon').length, 1, 'toggle icon');
    this.$('.toggle-icon .rsa-icon').click();
    const { endpoint: { visuals: { isTreeView } } } = this.get('redux').getState();
    assert.equal(isTreeView, true, 'It should toggle to tree view');
  });
});
