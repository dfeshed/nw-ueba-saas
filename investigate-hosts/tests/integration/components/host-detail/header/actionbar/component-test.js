import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import hbs from 'htmlbars-inline-precompile';
import ReduxDataHelper from '../../../../../helpers/redux-data-helper';
import { patchReducer } from '../../../../../helpers/vnext-patch';
import { clickTrigger, selectChoose } from 'ember-power-select/test-support/helpers';
import { click, find, findAll, render, settled } from '@ember/test-helpers';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import HosDetailsCreators from 'investigate-hosts/actions/data-creators/host-details';
import { snapShot } from '../../../../../data/data';
import sinon from 'sinon';

let changeSnapShotSpy, setState;
const spys = [];

module('Integration | Component | host-detail/header/actionbar', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-hosts')
  });

  hooks.beforeEach(function() {
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
    this.dateFormat = this.owner.lookup('service:dateFormat');
    this.timeFormat = this.owner.lookup('service:timeFormat');
    this.set('dateFormat.selected', 'MM/dd/yyyy', 'MM/dd/yyyy');
    this.set('timeFormat.selected', 'HR12', 'HR12');
    setState = (state) => {
      patchReducer(this, state);
    };
    spys.push(
      changeSnapShotSpy = sinon.stub(HosDetailsCreators, 'changeSnapshotTime'));
    this.owner.lookup('service:timezone').set('selected', { zoneId: 'UTC' });
  });

  hooks.afterEach(function() {
    spys.forEach((s) => {
      s.restore();
    });
  });

  hooks.after(function() {
    spys.forEach((s) => {
      s.restore();
    });
  });


  test('on selecting snapshot initializes the agent details input', async function(assert) {
    assert.expect(1);
    new ReduxDataHelper(setState)
      .snapShot(snapShot)
      .scanTime('2017-08-29T10:23:49.452Z')
      .agentId(1345)
      .build();
    await render(hbs`{{host-detail/header/actionbar}}`);
    await selectChoose('.actionbar', '.ember-power-select-option', 3);
    return settled().then(async() => {
      assert.equal(changeSnapShotSpy.callCount, 1, 'Change snapshot creator is called');
    });
  });


  test('snapshot power select renders appropriate items', async function(assert) {
    new ReduxDataHelper(setState)
      .snapShot(snapShot)
      .scanTime('2017-03-22T09:54:40.632Z')
      .build();
    await render(hbs`{{host-detail/header/actionbar}}`);
    await clickTrigger();
    assert.ok(find('.actionbar .ember-power-select-trigger'), 'should render the power-select trigger');
    assert.equal(findAll('.ember-power-select-option').length, 4, 'dropdown  rendered with available snapShots');
    assert.equal(find('.actionbar .rsa-button-group .ember-power-select-selected-item .datetime').textContent.trim().length, 22, 'Snapshot datetime is rendered properly,without miliseconds');
  });

  test('snapshot selection is disabled when process details is active', async function(assert) {
    new ReduxDataHelper(setState)
      .isProcessDetailsView(true)
      .snapShot(snapShot)
      .build();
    await render(hbs`{{host-detail/header/actionbar}}`);
    await clickTrigger();
    assert.ok(find('.actionbar .ember-power-select-trigger'), 'should render the power-select trigger');
    assert.equal(findAll('.actionbar .ember-power-select-trigger[aria-disabled=true]').length, 1);
  });

  test('Show right panel button is present when Details tab is selected', async function(assert) {
    new ReduxDataHelper(setState)
      .snapShot(snapShot)
      .hostName('XYZ')
      .isDetailRightPanelVisible(true)
      .build();
    await render(hbs`{{host-detail/header/actionbar}}`);
    assert.equal(findAll('.open-properties').length, 1, 'Show/Hide right panel button is present');
  });

  test('Right panel button is hidden when Process tab is selected', async function(assert) {
    new ReduxDataHelper(setState)
      .snapShot(snapShot)
      .hostName('XYZ')
      .isDetailRightPanelVisible(true)
      .build();
    await render(hbs`{{host-detail/header/actionbar}}`);
    assert.equal(findAll('.is-active').length, 1, 'Right panel is Active');
    await click(findAll('.open-properties button')[0]);
    assert.equal(findAll('.is-active').length, 0, 'Right panel is hidden');
  });

  test('snapshot power select does not render for Downloads', async function(assert) {
    new ReduxDataHelper(setState)
      .snapShot(snapShot)
      .selectedTabComponent('DOWNLOADS')
      .build();
    await render(hbs`{{host-detail/header/actionbar}}`);
    assert.equal(findAll('.actionbar .power-select').length, 0, 'should not render the power-select');
  });

  test('snapshot power select does not render for non download tabs', async function(assert) {
    new ReduxDataHelper(setState)
      .snapShot(snapShot)
      .selectedTabComponent('AUTORUNS')
      .build();
    await render(hbs`{{host-detail/header/actionbar}}`);
    assert.equal(findAll('.actionbar .power-select').length, 1, 'should render the power-select');
  });
});
