import $ from 'jquery';
import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import wait from 'ember-test-helpers/wait';

moduleForComponent('/rsa-content-ip-connections', 'Integration | Component | rsa-content-ip-connections', {
  integration: true
});

test('it includes the proper classes', function(assert) {
  this.set('to', ['foo']);
  this.set('from', ['bar']);

  this.render(hbs `{{rsa-content-ip-connections toIPs=to fromIPs=from}}`);
  const component = this.$().find('.rsa-content-ip-connections.vertical').length;
  assert.equal(component, 1);
});

test('it includes the proper classes when flow is set to horizontal', function(assert) {
  this.set('to', ['foo']);
  this.set('from', ['bar']);

  this.render(hbs `{{rsa-content-ip-connections flow="horizontal" toIPs=to fromIPs=from}}`);
  const component = this.$().find('.rsa-content-ip-connections.horizontal').length;
  assert.equal(component, 1);
});

test('it renders to and from sections when IPs are present', function(assert) {
  this.set('to', ['foo']);
  this.set('from', ['bar']);

  this.render(hbs `{{rsa-content-ip-connections toIPs=to fromIPs=from}}`);
  const to = this.$().find('.to-ip').length;
  const from = this.$().find('.from-ip').length;
  assert.equal(to, 1);
  assert.equal(from, 1);
});

test('it does not render to and from sections when IPs are not present', function(assert) {

  this.render(hbs `{{rsa-content-ip-connections}}`);

  const to = this.$().find('.to-ip').length;
  const from = this.$().find('.from-ip').length;
  assert.equal(to, 0);
  assert.equal(from, 0);
});

test('it renders placeHolder display properly when no IPs are present', function(assert) {
  this.set('showPlaceHolder', true);
  this.render(hbs `{{rsa-content-ip-connections showPlaceHolder=showPlaceHolder}}`);

  const defaultIpText = '–';
  const to = this.$().find('.to-ip');
  const direction = this.$().find('.rsa-content-ip-connections .direction').length;
  const from = this.$().find('.from-ip');
  assert.equal(to.length, 1, 'to is present');
  assert.equal(to.text().trim(), defaultIpText, 'source Ip contains correct default value');
  assert.equal(direction, 1, 'direction indicator is present');
  assert.equal(from.length, 1, 'from is present');
  assert.equal(from.text().trim(), defaultIpText, 'destination Ip contains correct default value');
});

test('it renders the IP when one record is present', function(assert) {
  this.set('to', ['foo']);
  this.set('from', ['bar']);

  this.render(hbs `{{rsa-content-ip-connections toIPs=to fromIPs=from}}`);
  const to = this.$().find('.to-ip .ip').text();
  const from = this.$().find('.from-ip .ip').text();
  assert.equal(to, 'foo');
  assert.equal(from, 'bar');
});

test('it renders IP count when multiple records are present', function(assert) {
  this.set('to', ['foo', 'foo2']);
  this.set('from', ['bar', 'bar2']);

  this.render(hbs `{{rsa-content-ip-connections toIPs=to fromIPs=from}}`);
  const to = this.$().find('.to-ip .ip-count').text();
  const from = this.$().find('.from-ip .ip-count').text();
  assert.equal(to, '(2 IPs)');
  assert.equal(from, '(2 IPs)');
});

test('it renders a button and dropdown with to IP records when there are multiple', function(assert) {
  this.set('to', ['foo', 'foo2']);
  this.set('from', ['bar', 'bar2']);

  this.render(hbs `{{rsa-content-ip-connections toIPs=to fromIPs=from}}`);

  this.$().find('.to-ip .rsa-form-button').click();

  return wait().then(function() {
    const toListCount = $('.to-ip.rsa-dropdown-action-list li').length;
    assert.equal(toListCount, 2);
  });
});

test('it renders a button and dropdown with from IP records when there are multiple', function(assert) {
  this.set('to', ['foo', 'foo2']);
  this.set('from', ['bar', 'bar2']);

  this.render(hbs `{{rsa-content-ip-connections toIPs=to fromIPs=from}}`);

  this.$().find('.from-ip .rsa-form-button').click();

  return wait().then(function() {
    const fromListCount = $('.from-ip.rsa-dropdown-action-list li').length;
    assert.equal(fromListCount, 2);
  });
});

test('it does not include the direction indicator or from-ip when only toIPS is passed', function(assert) {
  this.set('to', ['foo']);
  this.render(hbs `{{rsa-content-ip-connections toIPs=to}}`);
  const direction = this.$().find('.rsa-content-ip-connections .direction').length;
  const fromIps = this.$().find('.rsa-content-ip-connections .from-ip').length;
  assert.equal(direction, 0);
  assert.equal(fromIps, 0);
});

test('it does not include the direction indicator or to-ip when only fromIPS is passed', function(assert) {
  this.set('from', ['foo']);

  this.render(hbs `{{rsa-content-ip-connections fromIPs=to}}`);
  const direction = this.$().find('.rsa-content-ip-connections .direction').length;
  const toIps = this.$().find('.rsa-content-ip-connections .to-ip').length;
  assert.equal(direction, 0);
  assert.equal(toIps, 0);
});
