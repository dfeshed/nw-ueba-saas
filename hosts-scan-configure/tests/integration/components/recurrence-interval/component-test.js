import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import { clickTrigger } from '../../../helpers/ember-power-select';
import wait from 'ember-test-helpers/wait';
import Immutable from 'seamless-immutable';
import { applyPatch, revertPatch } from '../../../helpers/patch-reducer';
moduleForComponent('recurrence-interval', 'Integration | Component | recurrence interval', {
  integration: true,
  beforeEach() {
    this.registry.injection('component', 'i18n', 'service:i18n');
    const initState = Immutable.from({
      schedule: {
        config: {
          'name': 'default',
          'id': 1,
          'scheduleConfig': {
            'enabled': true,
            'group': 'default',
            'scanOptions': {
              'cpuMax': '80',
              'cpuMaxVm': '90'
            },
            'scheduleOptions': {
              'recurrenceIntervalUnit': 'DAYS',
              'recurrenceInterval': 1,
              'runOnDays': [1],
              'startTime': '2017-08-29T10:23:49.452Z',
              'timeZone': 'UTC'
            }
          }
        },
        fetchScheduleStatus: 'completed'
      }
    });
    applyPatch(initState);
    this.inject.service('redux');
  },
  afterEach() {
    revertPatch();
  }
});

test('raghs should render recurrence interval fields', function(assert) {
  this.render(hbs`{{recurrence-interval}}`);
  assert.equal(this.$('.recurrence-interval').length, 1, 'expected to have root element in DOM');
});

test('should display daily and weekly recurrence type', function(assert) {
  this.render(hbs`{{recurrence-interval}}`);
  assert.equal(this.$('.recurrence-type').length, 2, 'expected to have two radio button in dom');
});

test('should display Daily recurrence fields on clicking the Daily radio button', function(assert) {

  this.render(hbs`{{recurrence-interval}}`);
  assert.equal(this.$('.recurrence-interval').length, 1, 'expected to render the recurrence interval field');
  assert.equal(this.$('.recurrence-interval input:eq(0)').val(), 'DAYS', 'expected to render DAYS as first field');
  assert.equal(this.$('.recurrence-run-interval').length, 1, 'expected to render dropdown for run interval');
  assert.equal(this.$('input[type=radio]:checked').length, 1, 'Expected to select default radio button');
  return wait().then(() => {
    clickTrigger();
    assert.ok(this.$('.ember-power-select-option:contains("1")').attr('aria-disabled') !== 'true');
    assert.ok(this.$('.ember-power-select-option:contains("20")').attr('aria-disabled') !== 'true');
  });
});

test('should display weeks recurrence field options on clicking the Weekly radio button', function(assert) {

  this.render(hbs`{{recurrence-interval}}`);
  assert.equal(this.$('.recurrence-interval').length, 1, 'expected to render the recurrence interval field');
  this.$('.recurrence-interval input:eq(1)').click();
  assert.equal(this.$('input[type=radio]:eq(1):checked').length, 1, 'Expected to select Weekly radio button');
  assert.equal(this.$('.recurrence-run-interval__week-options').length, 1, 'Expected to display week options');

});


test('should select the week on clicking the available week options', function(assert) {

  this.render(hbs`{{recurrence-interval}}`);
  assert.equal(this.$('.recurrence-interval').length, 1, 'expected to render the recurrence interval field');
  this.$('.recurrence-interval input:eq(1)').click();
  assert.equal(this.$('.recurrence-run-interval__week-options').length, 1, 'Expected to display week options');
  this.$('.week-button:eq(0)').click();
  assert.equal(this.$('.week-button:eq(0).is-primary').length, 1);

});
