import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

moduleForComponent('/rsa-content-ip-connections', 'Integration | Component | rsa-content-ip-connections', {
  integration: true
});

test('it includes the proper classes', function(assert) {
  this.set('to', ['foo']);
  this.set('from', ['bar']);

  this.render(hbs `{{rsa-content-ip-connections toIPs=to fromIPs=from}}`);
  let component = this.$().find('.rsa-content-ip-connections').length;
  assert.equal(component, 1);
});

test('it renders to and frim sections when IPs are present', function(assert) {
  this.set('to', ['foo']);
  this.set('from', ['bar']);

  this.render(hbs `{{rsa-content-ip-connections toIPs=to fromIPs=from}}`);
  let to = this.$().find('.to-ip').length;
  let from = this.$().find('.from-ip').length;
  assert.equal(to, 1);
  assert.equal(from, 1);
});

test('it does not render to and frim sections when IPs are not present', function(assert) {
  this.render(hbs `{{rsa-content-ip-connections}}`);
  let to = this.$().find('.to-ip').length;
  let from = this.$().find('.from-ip').length;
  assert.equal(to, 0);
  assert.equal(from, 0);
});

test('it renders the IP when one record is present', function(assert) {
  this.set('to', ['foo']);
  this.set('from', ['bar']);

  this.render(hbs `{{rsa-content-ip-connections toIPs=to fromIPs=from}}`);
  let to = this.$().find('.to-ip .ip').text();
  let from = this.$().find('.from-ip .ip').text();
  assert.equal(to, 'foo');
  assert.equal(from, 'bar');
});

test('it renders IP count when multiple records are present', function(assert) {
  this.set('to', ['foo', 'foo2']);
  this.set('from', ['bar', 'bar2']);

  this.render(hbs `{{rsa-content-ip-connections toIPs=to fromIPs=from}}`);
  let to = this.$().find('.to-ip .ip-count').text();
  let from = this.$().find('.from-ip .ip-count').text();
  assert.equal(to, '(2 IPs)');
  assert.equal(from, '(2 IPs)');
});

test('it renders a button and dropdown with IP records when there are multiple', function(assert) {
  this.set('to', ['foo', 'foo2']);
  this.set('from', ['bar', 'bar2']);

  this.render(hbs `{{rsa-content-ip-connections toIPs=to fromIPs=from}}`);
  let toListCount = this.$().find('.to-ip .rsa-form-button-wrapper.with-dropdown li').length;
  let fromListCount = this.$().find('.from-ip .rsa-form-button-wrapper.with-dropdown li').length;
  assert.equal(toListCount, 2);
  assert.equal(fromListCount, 2);
});
