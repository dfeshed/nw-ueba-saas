import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../helpers/engine-resolver';
import sinon from 'sinon';
import * as RequestApi from 'investigate-events/actions/query-validation-creators';

moduleForComponent('query-filter-fragment', 'Integration | Component | query-filter-fragment', {
  integration: true,
  resolver: engineResolverFor('investigate-events'),
  beforeEach() {
    this.inject.service('redux');
  }
});

const pressEnter = (input) => {
  input.trigger({
    type: 'keydown',
    which: 13,
    code: 'Enter'
  });
};

test('it renders', function(assert) {
  this.render(hbs`{{query-filter-fragment}}`);
  const $el = this.$('.rsa-query-fragment');
  assert.equal($el.length, 1, 'Expected root DOM element.');
});

test('it renders with proper class when editActive', function(assert) {
  this.render(hbs`{{query-filter-fragment editActive=true}}`);
  const $el = this.$('.rsa-query-fragment.edit-active');
  assert.equal($el.length, 1, 'Expected root DOM element.');
});

test('it renders with proper class when selected', function(assert) {
  this.render(hbs`{{query-filter-fragment selected=true}}`);
  const $el = this.$('.rsa-query-fragment.selected');
  assert.equal($el.length, 1, 'Expected root DOM element.');
});

test('it renders with proper class when empty', function(assert) {
  this.render(hbs`{{query-filter-fragment empty=true}}`);
  const $el = this.$('.rsa-query-fragment.empty');
  assert.equal($el.length, 1, 'Expected root DOM element.');
});

test('it renders with proper class when typing', function(assert) {
  this.render(hbs`{{query-filter-fragment typing=true}}`);
  const $el = this.$('.rsa-query-fragment.typing');
  assert.equal($el.length, 1, 'Expected root DOM element.');
});

test('it renders with proper class when isExpensive', function(assert) {
  this.render(hbs`{{query-filter-fragment isExpensive=true}}`);
  const $el = this.$('.rsa-query-fragment.is-expensive');
  assert.equal($el.length, 1, 'Expected root DOM element.');
});

test('it is selectable', function(assert) {
  this.render(hbs`{{query-filter-fragment}}`);
  const $fragment = this.$('.rsa-query-fragment');
  const $fragmentMeta = $fragment.find('.meta');
  $fragmentMeta.click();
  assert.ok($fragment.hasClass('selected'), 'Expected fragment to be selected.');
});

test('it manually quotes when metaFormat is Text and quotes are not included', function(assert) {
  this.set('list', []);
  this.set('metaOptions', [{
    format: 'Text',
    metaName: 'action'
  }]);

  this.render(hbs`{{query-filter-fragment validateWithServer=false filterList=list metaOptions=metaOptions editActive=true}}`);

  this.$('input').val('action=foo');
  pressEnter(this.$('input'));

  assert.equal(this.$('.meta').text().trim(), 'action = "foo"', 'Expected to be quoted.');
});

test('it validates when metaFormat is Text and value is correct', function(assert) {
  this.set('list', []);
  this.set('metaOptions', [{
    format: 'Text',
    metaName: 'action'
  }]);

  this.render(hbs`{{query-filter-fragment validateWithServer=false filterList=list metaOptions=metaOptions editActive=true}}`);

  this.$('input').val('action="foo"');
  pressEnter(this.$('input'));

  assert.notOk(this.$('.rsa-query-fragment').hasClass('query-fragment-invalid'), 'Expected valid.');
});

test('it fails validation when metaFormat is TimeT and value is not proper format', function(assert) {
  this.set('list', []);
  this.set('metaOptions', [{
    format: 'TimeT',
    metaName: 'time'
  }]);

  this.render(hbs`{{query-filter-fragment validateWithServer=false filterList=list metaOptions=metaOptions editActive=true}}`);

  this.$('input').val('time=notAtime');
  pressEnter(this.$('input'));


  assert.equal(this.$('.rsa-query-fragment .meta').prop('title'), 'You must enter a valid date.');
  assert.ok(this.$('.rsa-query-fragment').hasClass('query-fragment-invalid'), 'Expected invalid.');
});

test('it passes validation when metaFormat is TimeT and value is correct', function(assert) {
  this.set('list', []);
  this.set('metaOptions', [{
    format: 'TimeT',
    metaName: 'time'
  }]);

  this.render(hbs`{{query-filter-fragment validateWithServer=false filterList=list metaOptions=metaOptions editActive=true}}`);

  this.$('input').val(`action=${new Date()}`);
  pressEnter(this.$('input'));

  assert.notOk(this.$('.rsa-query-fragment').hasClass('query-fragment-invalid'), 'Expected valid.');
});

test('it fails validation when metaFormat is IPv4 and value is not proper format', function(assert) {
  this.set('list', []);
  this.set('metaOptions', [{
    format: 'IPv4',
    metaName: 'ip'
  }]);

  this.render(hbs`{{query-filter-fragment validateWithServer=false filterList=list metaOptions=metaOptions editActive=true}}`);

  this.$('input').val('ip=notAnIp');
  pressEnter(this.$('input'));

  assert.equal(this.$('.rsa-query-fragment .meta').prop('title'), 'You must enter an IPv4 address.');
  assert.ok(this.$('.rsa-query-fragment').hasClass('query-fragment-invalid'), 'Expected invalid.');
});

test('it passes validation when metaFormat is IPv4 and value is correct', function(assert) {
  this.set('list', []);
  this.set('metaOptions', [{
    format: 'IPv4',
    metaName: 'ip'
  }]);

  this.render(hbs`{{query-filter-fragment validateWithServer=false filterList=list metaOptions=metaOptions editActive=true}}`);

  this.$('input').val('ip=127.0.0.1');
  pressEnter(this.$('input'));

  assert.notOk(this.$('.rsa-query-fragment').hasClass('query-fragment-invalid'), 'Expected valid.');
});

test('it fails validation when metaFormat is IPv6 and value is not proper format', function(assert) {
  this.set('list', []);
  this.set('metaOptions', [{
    format: 'IPv6',
    metaName: 'ip'
  }]);

  this.render(hbs`{{query-filter-fragment validateWithServer=false filterList=list metaOptions=metaOptions editActive=true}}`);

  this.$('input').val('ip=notAnIp');
  pressEnter(this.$('input'));

  assert.equal(this.$('.rsa-query-fragment .meta').prop('title'), 'You must enter an IPv6 address.');
  assert.ok(this.$('.rsa-query-fragment').hasClass('query-fragment-invalid'), 'Expected invalid.');
});

test('it passes validation when metaFormat is IPv6 and value is correct', function(assert) {
  this.set('list', []);
  this.set('metaOptions', [{
    format: 'IPv6',
    metaName: 'ip'
  }]);

  this.render(hbs`{{query-filter-fragment validateWithServer=false filterList=list metaOptions=metaOptions editActive=true}}`);

  this.$('input').val('ip=2001:0db8:85a3:0000:0000:8a2e:0370:7334');
  pressEnter(this.$('input'));

  assert.notOk(this.$('.rsa-query-fragment').hasClass('query-fragment-invalid'), 'Expected valid.');
});

test('it fails validation when metaFormat is UInt8 and value is not proper format', function(assert) {
  this.set('list', []);
  this.set('metaOptions', [{
    format: 'UInt8',
    metaName: 'int'
  }]);

  this.render(hbs`{{query-filter-fragment validateWithServer=false filterList=list metaOptions=metaOptions editActive=true}}`);

  this.$('input').val('int=notAnInt');
  pressEnter(this.$('input'));

  assert.equal(this.$('.rsa-query-fragment .meta').prop('title'), 'You must enter an 8 bit Integer.');
  assert.ok(this.$('.rsa-query-fragment').hasClass('query-fragment-invalid'), 'Expected invalid.');
});

test('it passes validation when metaFormat is UInt8 and value is correct', function(assert) {
  this.set('list', []);
  this.set('metaOptions', [{
    format: 'UInt8',
    metaName: 'int'
  }]);

  this.render(hbs`{{query-filter-fragment validateWithServer=false filterList=list metaOptions=metaOptions editActive=true}}`);

  this.$('input').val('int=8');
  pressEnter(this.$('input'));

  assert.notOk(this.$('.rsa-query-fragment').hasClass('query-fragment-invalid'), 'Expected valid.');
});

test('it fails validation when metaFormat is UInt16 and value is not proper format', function(assert) {
  this.set('list', []);
  this.set('metaOptions', [{
    format: 'UInt16',
    metaName: 'int'
  }]);

  this.render(hbs`{{query-filter-fragment validateWithServer=false filterList=list metaOptions=metaOptions editActive=true}}`);

  this.$('input').val('int=notAnInt');
  pressEnter(this.$('input'));

  assert.equal(this.$('.rsa-query-fragment .meta').prop('title'), 'You must enter a 16 bit Integer.');
  assert.ok(this.$('.rsa-query-fragment').hasClass('query-fragment-invalid'), 'Expected invalid.');
});

test('it passes validation when metaFormat is UInt16 and value is correct', function(assert) {
  this.set('list', []);
  this.set('metaOptions', [{
    format: 'UInt16',
    metaName: 'int'
  }]);

  this.render(hbs`{{query-filter-fragment validateWithServer=false filterList=list metaOptions=metaOptions editActive=true}}`);

  this.$('input').val('int=8');
  pressEnter(this.$('input'));

  assert.notOk(this.$('.rsa-query-fragment').hasClass('query-fragment-invalid'), 'Expected valid.');
});

test('it fails validation when metaFormat is UInt32 and value is not proper format', function(assert) {
  this.set('list', []);
  this.set('metaOptions', [{
    format: 'UInt32',
    metaName: 'int'
  }]);

  this.render(hbs`{{query-filter-fragment validateWithServer=false filterList=list metaOptions=metaOptions editActive=true}}`);

  this.$('input').val('int=notAnInt');
  pressEnter(this.$('input'));

  assert.equal(this.$('.rsa-query-fragment .meta').prop('title'), 'You must enter a 32 bit Integer.');
  assert.ok(this.$('.rsa-query-fragment').hasClass('query-fragment-invalid'), 'Expected invalid.');
});

test('it passes validation when metaFormat is UInt32 and value is correct', function(assert) {
  this.set('list', []);
  this.set('metaOptions', [{
    format: 'UInt32',
    metaName: 'int'
  }]);

  this.render(hbs`{{query-filter-fragment validateWithServer=false filterList=list metaOptions=metaOptions editActive=true}}`);

  this.$('input').val('int=8');
  pressEnter(this.$('input'));

  assert.notOk(this.$('.rsa-query-fragment').hasClass('query-fragment-invalid'), 'Expected valid.');
});

test('it fails validation when metaFormat is Float32 and value is not proper format', function(assert) {
  this.set('list', []);
  this.set('metaOptions', [{
    format: 'Float32',
    metaName: 'float'
  }]);

  this.render(hbs`{{query-filter-fragment validateWithServer=false filterList=list metaOptions=metaOptions editActive=true}}`);

  this.$('input').val('float=notAnInt');
  pressEnter(this.$('input'));

  assert.equal(this.$('.rsa-query-fragment .meta').prop('title'), 'You must enter a 32 bit Float.');
  assert.ok(this.$('.rsa-query-fragment').hasClass('query-fragment-invalid'), 'Expected invalid.');
});

test('it passes validation when metaFormat is Float32 and value is correct', function(assert) {
  this.set('list', []);
  this.set('metaOptions', [{
    format: 'Float32',
    metaName: 'float'
  }]);

  this.render(hbs`{{query-filter-fragment validateWithServer=false filterList=list metaOptions=metaOptions editActive=true}}`);

  this.$('input').val('float=8.5');
  pressEnter(this.$('input'));

  assert.notOk(this.$('.rsa-query-fragment').hasClass('query-fragment-invalid'), 'Expected valid.');
});

test('it creates pill with exists', function(assert) {
  this.set('list', []);
  this.set('metaOptions', [{
    metaName: 'foo'
  }]);

  this.render(hbs`{{query-filter-fragment validateWithServer=false filterList=list metaOptions=metaOptions editActive=true}}`);

  this.$('input').val('foo exists');
  pressEnter(this.$('input'));

  assert.equal(this.$('.rsa-query-fragment .meta').text().trim(), 'foo exists', 'Expected exists.');
});

test('it creates pill with !exists', function(assert) {
  this.set('list', []);
  this.set('metaOptions', [{
    metaName: 'foo'
  }]);

  this.render(hbs`{{query-filter-fragment validateWithServer=false filterList=list metaOptions=metaOptions editActive=true}}`);

  this.$('input').val('foo !exists');
  pressEnter(this.$('input'));

  assert.equal(this.$('.rsa-query-fragment .meta').text().trim(), 'foo !exists', 'Expected !exists.');
});

test('it renders with proper class when a single query is invalid', function(assert) {
  this.render(hbs`{{query-filter-fragment queryFragmentInvalid=true}}`);
  const $el = this.$('.rsa-query-fragment.query-fragment-invalid');
  assert.equal($el.length, 1, 'Expected invalid class name binding.');
});

sinon.stub(RequestApi, 'validateIndividualQuery');

test('submitting a query fragment and checking if the api wrapper function is called', function(assert) {

  this.set('list', []);
  this.set('metaOptions', [{ displayName: 'Medium', flags: -2147483309, format: 'UInt8', metaName: 'medium' }]);

  this.render(hbs`{{query-filter-fragment validateWithServer=true filterList=list metaOptions=metaOptions editActive=true}}`);

  this.$('input').val('medium = 32');
  pressEnter(this.$('input'));

  assert.notOk(this.$('.rsa-query-fragment').hasClass('query-fragment-invalid'), 'Expected valid.');
  assert.ok(RequestApi.validateIndividualQuery.calledOnce, 'Validate query action creator called once');
  assert.equal(RequestApi.validateIndividualQuery.args[0][0], 'medium = 32', 'correct params are being passed');
  RequestApi.validateIndividualQuery.restore();
});
