import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

moduleForComponent('rsa-investigate/time-range', 'Integration | Component | rsa investigate/time range', {
  integration: true
});

const nowInSeconds = parseInt(+(new Date()) / 1000, 10);
const oneWeekAgo = nowInSeconds - 7 * 24 * 60 * 60;

test('it renders', function(assert) {
  this.render(hbs`{{rsa-investigate/time-range startTime=now}}`);
  assert.equal(this.$('.rsa-investigate-time-range').length, 1, 'Expected root DOM element.');
});

test('it renders as expected with only a startTime', function(assert) {

  this.set('now', nowInSeconds);
  this.render(hbs`{{rsa-investigate/time-range startTime=now}}`);

  assert.equal(this.$('.start').length, 1, 'Expected start time DOM element.');
  assert.equal(this.$('.end').length, 0, 'Expected to omit end time DOM element.');
  assert.equal(this.$('.rsa-investigate-time-range__sep').length, 0, 'Expected to omit separator DOM element.');
});

test('it renders as expected with only an endTime', function(assert) {

  this.set('now', nowInSeconds);
  this.render(hbs`{{rsa-investigate/time-range endTime=now}}`);

  assert.equal(this.$('.end').length, 1, 'Expected end time DOM element.');
  assert.equal(this.$('.start').length, 0, 'Expected to omit start time DOM element.');
  assert.equal(this.$('.rsa-investigate-time-range__sep').length, 0, 'Expected to omit separator DOM element.');
});

test('it renders as expected with startTime and endTime that are days apart match', function(assert) {

  this.setProperties({ nowInSeconds, oneWeekAgo });
  this.render(hbs`{{rsa-investigate/time-range startTime=oneWeekAgo endTime=nowInSeconds}}`);

  assert.equal(this.$('.start').length, 1, 'Expected start time DOM element.');
  assert.equal(this.$('.end').length, 1, 'Expected end time DOM element.');
  assert.equal(this.$('.rsa-investigate-time-range__sep').length, 1, 'Expected to find separator DOM element.');
});

test('it renders as expected with startTime and endTime that match', function(assert) {

  this.set('now', nowInSeconds);
  this.render(hbs`{{rsa-investigate/time-range startTime=now endTime=now}}`);

  assert.equal(this.$('.start').length, 1, 'Expected start time DOM element.');
  assert.equal(this.$('.end').length, 1, 'Expected end time DOM element.');
  assert.equal(this.$('.rsa-investigate-time-range__sep').length, 1, 'Expected to find separator DOM element.');
});
