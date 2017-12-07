import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../helpers/engine-resolver';
import { applyPatch, revertPatch } from '../../../helpers/patch-reducer';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import Immutable from 'seamless-immutable';

let setState;
moduleForComponent('time-range', 'Integration | Component | time range', {
  integration: true,
  resolver: engineResolverFor('investigate-events'),
  beforeEach() {
    initialize(this);
    setState = (state) => {
      const fullState = { investigate: { ...state } };
      applyPatch(Immutable.from(fullState));
      this.inject.service('redux');
    };
  },
  afterEach() {
    revertPatch();
  }
});

const nowInSeconds = parseInt(+(new Date()) / 1000, 10);
const oneWeekAgo = nowInSeconds - 7 * 24 * 60 * 60;

test('it renders', function(assert) {
  this.render(hbs`{{bread-crumb-container/time-range startTime=now}}`);
  assert.equal(this.$('.rsa-investigate-time-range').length, 1, 'Expected root DOM element.');
});

test('it renders as expected with only a startTime', function(assert) {
  setState({
    queryNode: {
      startTime: nowInSeconds
    }
  });

  this.render(hbs`{{bread-crumb-container/time-range}}`);

  assert.equal(this.$('.start').length, 1, 'Expected start time DOM element.');
  assert.equal(this.$('.end').length, 0, 'Expected to omit end time DOM element.');
  assert.equal(this.$('.rsa-investigate-time-range__sep').length, 0, 'Expected to omit separator DOM element.');
});

test('it renders as expected with only an endTime', function(assert) {
  setState({
    queryNode: {
      endTime: nowInSeconds
    }
  });

  this.render(hbs`{{bread-crumb-container/time-range}}`);

  assert.equal(this.$('.end').length, 1, 'Expected end time DOM element.');
  assert.equal(this.$('.start').length, 0, 'Expected to omit start time DOM element.');
  assert.equal(this.$('.rsa-investigate-time-range__sep').length, 0, 'Expected to omit separator DOM element.');
});

test('it renders as expected with startTime and endTime that are days apart match', function(assert) {
  setState({
    queryNode: {
      startTime: oneWeekAgo,
      endTime: nowInSeconds
    }
  });

  this.render(hbs`{{bread-crumb-container/time-range}}`);

  assert.equal(this.$('.start').length, 1, 'Expected start time DOM element.');
  assert.equal(this.$('.end').length, 1, 'Expected end time DOM element.');
  assert.equal(this.$('.rsa-investigate-time-range__sep').length, 1, 'Expected to find separator DOM element.');
});

test('it renders as expected with startTime and endTime that match', function(assert) {
  setState({
    queryNode: {
      startTime: nowInSeconds,
      endTime: nowInSeconds
    }
  });

  this.render(hbs`{{bread-crumb-container/time-range}}`);

  assert.equal(this.$('.start').length, 1, 'Expected start time DOM element.');
  assert.equal(this.$('.end').length, 1, 'Expected end time DOM element.');
  assert.equal(this.$('.rsa-investigate-time-range__sep').length, 1, 'Expected to find separator DOM element.');
});
