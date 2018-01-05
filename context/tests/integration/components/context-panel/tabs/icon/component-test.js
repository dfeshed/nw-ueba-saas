import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

const ENABLED_CLASSNAME = 'rsa-context-panel__tabs__enabled';
const DISABLED_CLASSNAME = 'rsa-context-panel__tabs__disabled';

moduleForComponent('context-panel/tabs/icon', 'Integration | Component | context panel/tabs/icon', {
  integration: true
});

test('derivedClassName enabled & not disabled if loadingIcon + toolTipText are both undefined', function(assert) {
  this.set('tab', {});
  this.render(hbs`{{context-panel/tabs/icon tab=tab}}`);
  assert.equal(this.$('div').hasClass(ENABLED_CLASSNAME), true, 'icon did not have enabled className');
  assert.equal(this.$('div').hasClass(DISABLED_CLASSNAME), false, 'icon had disabled className when it should not have');
});

test('derivedClassName disabled & not enabled if loadingIcon false + toolTipText truthy', function(assert) {
  this.set('tab', {
    toolTipText: 'x',
    loadingIcon: false
  });
  this.render(hbs`{{context-panel/tabs/icon tab=tab}}`);
  assert.equal(this.$('div').hasClass(ENABLED_CLASSNAME), false, 'icon had enabled className when it should not have');
  assert.equal(this.$('div').hasClass(DISABLED_CLASSNAME), true, 'icon did not have disabled className');
});

test('derivedClassName disabled & not enabled if loadingIcon null + toolTipText truthy', function(assert) {
  this.set('tab', {
    toolTipText: 'x',
    loadingIcon: null
  });
  this.render(hbs`{{context-panel/tabs/icon tab=tab}}`);
  assert.equal(this.$('div').hasClass(ENABLED_CLASSNAME), false, 'icon had enabled className when it should not have');
  assert.equal(this.$('div').hasClass(DISABLED_CLASSNAME), true, 'icon did not have disabled className');
});

test('derivedClassName disabled & not enabled if loadingIcon undefined + toolTipText truthy', function(assert) {
  this.set('tab', {
    toolTipText: 'x'
  });
  this.render(hbs`{{context-panel/tabs/icon tab=tab}}`);
  assert.equal(this.$('div').hasClass(ENABLED_CLASSNAME), false, 'icon had enabled className when it should not have');
  assert.equal(this.$('div').hasClass(DISABLED_CLASSNAME), true, 'icon did not have disabled className');
});

test('derivedClassName not disabled & not enabled if loadingIcon true + toolTipText truthy', function(assert) {
  this.set('tab', {
    toolTipText: 'x',
    loadingIcon: true
  });
  this.render(hbs`{{context-panel/tabs/icon tab=tab}}`);
  assert.equal(this.$('div').hasClass(ENABLED_CLASSNAME), false, 'icon had enabled className when it should not have');
  assert.equal(this.$('div').hasClass(DISABLED_CLASSNAME), false, 'icon had disabled className when it should not have');
});

test('derivedClassName not disabled & not enabled if loadingIcon true + toolTipText undefined', function(assert) {
  this.set('tab', {
    loadingIcon: true
  });
  this.render(hbs`{{context-panel/tabs/icon tab=tab}}`);
  assert.equal(this.$('div').hasClass(ENABLED_CLASSNAME), false, 'icon had enabled className when it should not have');
  assert.equal(this.$('div').hasClass(DISABLED_CLASSNAME), false, 'icon had disabled className when it should not have');
});

test('derivedClassName recomputed when loadingIcon & toolTipText change', function(assert) {
  this.set('tab', {
  });
  this.render(hbs`{{context-panel/tabs/icon tab=tab}}`);
  assert.equal(this.$('div').hasClass(ENABLED_CLASSNAME), true, 'icon did not have enabled className');
  assert.equal(this.$('div').hasClass(DISABLED_CLASSNAME), false, 'icon had disabled className when it should not have');

  this.set('tab.toolTipText', 'x');
  assert.equal(this.$('div').hasClass(ENABLED_CLASSNAME), false, 'icon had enabled className when it should not have');
  assert.equal(this.$('div').hasClass(DISABLED_CLASSNAME), true, 'icon did not have disabled className');

  this.set('tab.loadingIcon', true);
  assert.equal(this.$('div').hasClass(ENABLED_CLASSNAME), false, 'icon had enabled className when it should not have');
  assert.equal(this.$('div').hasClass(DISABLED_CLASSNAME), false, 'icon had disabled className when it should not have');

  this.set('tab.loadingIcon', false);
  assert.equal(this.$('div').hasClass(ENABLED_CLASSNAME), false, 'icon had enabled className when it should not have');
  assert.equal(this.$('div').hasClass(DISABLED_CLASSNAME), true, 'icon did not have disabled className');
});
