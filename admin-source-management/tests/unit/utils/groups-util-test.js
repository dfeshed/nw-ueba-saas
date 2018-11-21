import { module, test } from 'qunit';
import { setupTest } from 'ember-qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { sourceCountTooltip, getSourceCount } from 'admin-source-management/utils/groups-util';

module('Unit | Utils | utils/groups-util', function(hooks) {
  setupTest(hooks);
  hooks.beforeEach(function() {
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  test('sourceCountTooltip() returns the correct tool tip text', function(assert) {
    const i18n = this.owner.lookup('service:i18n');
    // unpublished edit
    let toolTip = sourceCountTooltip(i18n, true, 30, 1523655368173);
    let expectedTooltip = i18n.t('adminUsm.groups.list.sourceCountUnpublishedEditedGroupTooltip');
    assert.equal(toolTip, expectedTooltip.string, 'expected unpublished edit tooltip');

    // new published group with endpoints
    toolTip = sourceCountTooltip(i18n, false, -1, 1523655368173);
    expectedTooltip = i18n.t('adminUsm.groups.list.sourceCountPublishedNewGroupTooltip');
    assert.equal(toolTip, expectedTooltip.string, 'expected new published with endpoint tooltip');

    // new published group no endpoints
    toolTip = sourceCountTooltip(i18n, false, -2, 1523655368173);
    expectedTooltip = i18n.t('adminUsm.groups.list.sourceCountPublishedNoEndpointTooltip');
    assert.equal(toolTip, expectedTooltip.string, 'expected new published no endpoints tooltip');

    // un published group
    toolTip = sourceCountTooltip(i18n, true, -3, 0);
    expectedTooltip = i18n.t('adminUsm.groups.list.sourceCountUnpublishedNewGroupTooltip');
    assert.equal(toolTip, expectedTooltip.string, 'expected un published group tooltip');

    // published group with synced up count
    toolTip = sourceCountTooltip(i18n, false, 100, 0);
    assert.equal(toolTip, '', 'expected published group tooltip');
  });

  test('getSourceCount() returns the correct count', function(assert) {
    // unpublished edit, published with endpoints
    let expectedCount = 30;
    let actualCount = getSourceCount(30);
    assert.equal(actualCount, expectedCount, 'expected unpublished edit and published count');

    // published with endpoints
    expectedCount = '--';
    actualCount = getSourceCount(-1);
    assert.equal(actualCount, expectedCount, 'expected published with endpoints count');

    // published with no endpoints
    actualCount = getSourceCount(-2);
    assert.equal(actualCount, expectedCount, 'expected published with no endpoints count');

    // unpublished with endpoints
    actualCount = getSourceCount(-3);
    assert.equal(actualCount, expectedCount, 'expected unpublished with endpoints count');
  });
});