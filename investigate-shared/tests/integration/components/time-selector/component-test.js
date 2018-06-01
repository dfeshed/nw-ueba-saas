import { module, test, setupRenderingTest, render } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import { findAll } from '@ember/test-helpers';

const nowInSeconds = parseInt(+(new Date()) / 1000, 10);
const oneWeekAgo = nowInSeconds - 7 * 24 * 60 * 60;

module('Integration | Component | Time Selector', function(hooks) {
  setupRenderingTest(hooks);

  hooks.beforeEach(function() {
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  test('it renders', async function(assert) {
    await render(hbs`{{time-range startTime=now}}`);
    assert.equal(findAll('.rsa-investigate-time-range').length, 1, 'Expected root DOM element.');
  });

  test('it renders as expected with only a startTime', async function(assert) {
    this.set('startTime', nowInSeconds);

    await render(hbs`{{time-range startTime=startTime}}`);

    assert.equal(findAll('.start').length, 1, 'Expected start time DOM element.');
    assert.equal(findAll('.end').length, 0, 'Expected to omit end time DOM element.');
    assert.equal(findAll('.rsa-investigate-time-range__sep').length, 0, 'Expected to omit separator DOM element.');
  });

  test('it renders as expected with only an endTime', async function(assert) {

    this.set('endTime', nowInSeconds);

    await render(hbs`{{time-range endTime=endTime}}`);

    assert.equal(findAll('.end').length, 1, 'Expected end time DOM element.');
    assert.equal(findAll('.start').length, 0, 'Expected to omit start time DOM element.');
    assert.equal(findAll('.rsa-investigate-time-range__sep').length, 0, 'Expected to omit separator DOM element.');
  });

  test('it renders as expected with startTime and endTime that are days apart match', async function(assert) {
    this.set('endTime', nowInSeconds);
    this.set('startTime', oneWeekAgo);
    await render(hbs`{{time-range startTime=startTime endTime=endTime}}`);

    assert.equal(findAll('.start').length, 1, 'Expected start time DOM element.');
    assert.equal(findAll('.end').length, 1, 'Expected end time DOM element.');
    assert.equal(findAll('.rsa-investigate-time-range__sep').length, 1, 'Expected to find separator DOM element.');
  });

  test('it renders as expected with startTime and endTime that match', async function(assert) {

    this.set('endTime', nowInSeconds);
    this.set('startTime', nowInSeconds);
    await render(hbs`{{time-range startTime=startTime endTime=endTime}}`);

    assert.equal(findAll('.start').length, 1, 'Expected start time DOM element.');
    assert.equal(findAll('.end').length, 1, 'Expected end time DOM element.');
    assert.equal(findAll('.rsa-investigate-time-range__sep').length, 1, 'Expected to find separator DOM element.');
  });

});


