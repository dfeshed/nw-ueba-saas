import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import { applyPatch, revertPatch } from '../../../helpers/patch-reducer';
import Immutable from 'seamless-immutable';
const initState = Immutable.from({
  hostsScan: {
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
          'startDate': '2018-01-10T10:23:49.452Z',
          'timeZone': 'UTC'
        }
      }
    },
    fetchScheduleStatus: 'completed'
  }
});
moduleForComponent('schedule-form', 'Integration | Component | schedule form', {
  integration: true,
  beforeEach() {
    this.registry.injection('component', 'i18n', 'service:i18n');
    this.inject.service('dateFormat');
    this.set('dateFormat.selected', 'MM/dd/yyyy', 'MM/dd/yyyy');
  },
  afterEach() {
    revertPatch();
  }
});


test('should render the schedule scan form', function(assert) {
  this.render(hbs`{{schedule-form}}`);
  assert.equal(this.$('.schedule-form').length, 1, 'expecting container to be rendered');

});

test('should show loading indicator when fetching the schedule config', function(assert) {
  const state = Immutable.from({
    hostsScan: {
      config: {

      },
      fetchScheduleStatus: 'wait'
    }
  });
  applyPatch(state);
  this.inject.service('redux');

  this.render(hbs`{{schedule-form}}`);

  assert.equal(this.$('.rsa-loader').length, 1, 'expecting show loader when loading the data');

});

test('should display schedule config once fetchScheduleStatus is completed', function(assert) {
  applyPatch(initState);
  this.inject.service('redux');
  this.render(hbs`{{schedule-form}}`);

  assert.equal(this.$('.schedule-form__content').length, 1, 'expecting to display form content');
  assert.equal(this.$('.schedule-form__content-title').text().trim(), 'Scan Schedule', 'expected to match the title');
});


test('should display schedule config', function(assert) {
  applyPatch(initState);
  this.inject.service('redux');
  this.render(hbs`{{schedule-form}}`);

  assert.equal(this.$('.schedule-form__content').length, 1, 'expecting to display form content');
  assert.equal(this.$('.recurrence-interval').length, 1, 'expected to render the recurrence interval field');
  assert.equal(this.$('.recurrence-interval input:eq(0)').val(), 'DAYS', 'expected to render DAYS as first field');
});

test('should display schedule time field', function(assert) {
  applyPatch(initState);
  this.inject.service('redux');
  this.render(hbs`{{schedule-form}}`);

  assert.equal(this.$('.schedule-form__content').length, 1, 'expecting to display form content');
  assert.equal(this.$('.time-field').length, 1, 'expected to render the recurrence interval field');
});

test('should display cpu throttling field', function(assert) {
  applyPatch(initState);
  this.inject.service('redux');
  this.render(hbs`{{schedule-form}}`);

  assert.equal(this.$('.schedule-form__content').length, 1, 'expecting to display form content');
  assert.equal(this.$('.cpu-throttling').length, 1, 'expected to render the cpu min and max fields');
});

test('should display start date field', function(assert) {
  applyPatch(initState);
  this.inject.service('redux');
  this.render(hbs`{{schedule-form}}`);

  assert.equal(this.$('.schedule-form__content').length, 1, 'expecting to display form content');
  assert.equal(this.$('.schedule-time .flatpickr-wrapper').length, 1, 'expected to render the start date time');
});
