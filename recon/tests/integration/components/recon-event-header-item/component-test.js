import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

moduleForComponent('recon-event-header-item', 'Integration | Component | recon event header item', {
  integration: true,
  beforeEach() {
    initialize(this);
  }
});

test('header item name and value render', function(assert) {
  this.set('name', 'nwService');
  this.set('value', 'concentrator');

  this.render(hbs`{{recon-event-header-item name=name value=value}}`);
  assert.equal(this.$('.name').first().text().trim(), 'NW Service');
  assert.equal(this.$('.value').first().text().trim(), 'concentrator');
});

test('Context menu trigger renders for ordinary value', function(assert) {
  this.set('name', 'sessionId');
  this.set('value', '123');
  this.set('key', 'sessionid');

  this.render(hbs`{{recon-event-header-item name=name value=value key=key}}`);
  assert.equal(this.$('span.content-context-menu').length, 1, 'Context menu trigger should be rendered');
});

test('Two separate context menu triggers render for IP and port', function(assert) {
  this.set('name', 'source');
  this.set('value', '10.1.1.1 : 9090');
  this.set('key', 'ip.src : port.src');

  this.render(hbs`{{recon-event-header-item name=name value=value key=key}}`);
  assert.equal(this.$('span.content-context-menu').length, 2, 'Separate context menu triggers should be rendered for ip and port');
});

test('Context menu trigger does not render when key is null', function(assert) {
  this.set('name', 'sessionId');
  this.set('value', '123');

  this.render(hbs`{{recon-event-header-item name=name value=value}}`);

  assert.equal(this.$('span.content-context-menu').length, 0, 'Context menu trigger should not be rendered');
});