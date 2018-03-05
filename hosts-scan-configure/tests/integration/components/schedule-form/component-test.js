import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import { applyPatch, revertPatch } from '../../../helpers/patch-reducer';
import { patchSocket } from '../../../helpers/patch-socket';
import { patchFlash } from '../../../helpers/patch-flash';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import ReduxDataHelper from '../../../helpers/redux-data-helper';
import wait from 'ember-test-helpers/wait';

const hostsScan = {
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
};
let setState;
moduleForComponent('schedule-form', 'Integration | Component | schedule form', {
  integration: true,
  beforeEach() {
    initialize(this);
    this.registry.injection('component', 'i18n', 'service:i18n');
    setState = (state) => {
      applyPatch(state);
      this.inject.service('redux');
    };
    this.inject.service('dateFormat');
    this.set('dateFormat.selected', 'MM/dd/yyyy', 'MM/dd/yyyy');
  },
  afterEach() {
    revertPatch();
  }
});


test('should render the schedule scan form', function(assert) {
  new ReduxDataHelper(setState)
    .hostsScanConfig(hostsScan.config)
    .fetchScheduleStatus(hostsScan.fetchScheduleStatus)
    .build();
  this.render(hbs`{{schedule-form}}`);
  assert.equal(this.$('.schedule-form').length, 1, 'expecting container to be rendered');

});

test('should show loading indicator when fetching the schedule config', function(assert) {
  const status = 'wait';
  new ReduxDataHelper(setState)
    .hostsScanConfig(hostsScan.config)
    .fetchScheduleStatus(status)
    .build();
  this.render(hbs`{{schedule-form}}`);
  return wait().then(() => {
    assert.equal(this.$('.rsa-loader').length, 1, 'expecting show loader when loading the data');
  });

});

test('Display error message in case of error loading the page', function(assert) {
  const fetchScheduleStatus = 'error';
  new ReduxDataHelper(setState)
    .hostsScanConfig(hostsScan.config)
    .fetchScheduleStatus(fetchScheduleStatus)
    .build();
  this.render(hbs`{{schedule-form}}`);
  assert.equal(this.$('.schedule-form .rsa-panel-message').text().trim(),
    'An unexpected error has occurred attempting to retrieve this data.', 'Error message displayed');
});

test('should display schedule config once fetchScheduleStatus is completed', function(assert) {
  new ReduxDataHelper(setState)
    .hostsScanConfig(hostsScan.config)
    .fetchScheduleStatus(hostsScan.fetchScheduleStatus)
    .build();
  this.render(hbs`{{schedule-form}}`);

  assert.equal(this.$('.schedule-form__content').length, 1, 'expecting to display form content');
  assert.equal(this.$('.schedule-form__content-title').text().trim(), 'Scan Schedule', 'expected to match the title');
});


test('should display schedule config', function(assert) {
  new ReduxDataHelper(setState)
    .hostsScanConfig(hostsScan.config)
    .fetchScheduleStatus(hostsScan.fetchScheduleStatus)
    .build();
  this.render(hbs`{{schedule-form}}`);

  assert.equal(this.$('.schedule-form__content').length, 1, 'expecting to display form content');
  assert.equal(this.$('.recurrence-interval').length, 1, 'expected to render the recurrence interval field');
  assert.equal(this.$('.recurrence-interval input:eq(0)').val(), 'DAYS', 'expected to render DAYS as first field');
});

test('should display schedule time field', function(assert) {
  new ReduxDataHelper(setState)
    .hostsScanConfig(hostsScan.config)
    .fetchScheduleStatus(hostsScan.fetchScheduleStatus)
    .build();
  this.render(hbs`{{schedule-form}}`);

  assert.equal(this.$('.schedule-form__content').length, 1, 'expecting to display form content');
  assert.equal(this.$('.time-field').length, 1, 'expected to render the recurrence interval field');
});

test('should display cpu throttling field', function(assert) {
  new ReduxDataHelper(setState)
    .hostsScanConfig(hostsScan.config)
    .fetchScheduleStatus(hostsScan.fetchScheduleStatus)
    .build();
  this.render(hbs`{{schedule-form}}`);

  assert.equal(this.$('.schedule-form__content').length, 1, 'expecting to display form content');
  assert.equal(this.$('.cpu-throttling').length, 1, 'expected to render the cpu min and max fields');
});

test('should display start date field', function(assert) {
  new ReduxDataHelper(setState)
    .hostsScanConfig(hostsScan.config)
    .fetchScheduleStatus(hostsScan.fetchScheduleStatus)
    .build();
  this.render(hbs`{{schedule-form}}`);

  assert.equal(this.$('.schedule-form__content').length, 1, 'expecting to display form content');
  assert.equal(this.$('.schedule-time .flatpickr-wrapper').length, 1, 'expected to render the start date time');
});

test('should display the save button', function(assert) {
  new ReduxDataHelper(setState)
    .hostsScanConfig(hostsScan.config)
    .fetchScheduleStatus(hostsScan.fetchScheduleStatus)
    .build();
  this.render(hbs`{{schedule-form}}`);

  assert.equal(this.$('.schedule-form__content').length, 1, 'expecting to display form content');
  assert.equal(this.$('.rsa-form-button').length, 1, 'expecting the presence of a save button');
});

test('when save button is disabled', function(assert) {
  const config = {
    enabled: false
  };
  new ReduxDataHelper(setState)
    .hostsScanConfig(config)
    .build();
  this.render(hbs`{{schedule-form}}`);

  assert.equal(this.$('.schedule-form__content').length, 1, 'expecting to display form content');
  assert.equal(this.$('.rsa-form-button-wrapper').hasClass('is-disabled'), true, 'when enable button is off, save button is expected to be disabled');
});

test('save schedule scan function being called', function(assert) {
  const configData = {
    data: {
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
    }
  };
  new ReduxDataHelper(setState)
    .hostsScanConfig(hostsScan.config)
    .fetchScheduleStatus(hostsScan.fetchScheduleStatus)
    .build();
  this.render(hbs`{{schedule-form}}`);
  patchFlash((flash) => {
    assert.equal(flash.type, 'success');
  });
  patchSocket((method, modelName, query) => {
    assert.equal(method, 'update');
    assert.equal(modelName, 'schedule');
    assert.deepEqual(query, configData);
  });
  assert.equal(this.$('.schedule-form__content').length, 1, 'expecting to display form content');
  this.$('.rsa-form-button').trigger('click');
  return wait().then(() => {
    const state = this.get('redux').getState();
    assert.equal(state.hostsScan.config.success, true, 'scan schedule saved successfully');
  });
});

test('toggling the enable schedule button', function(assert) {
  new ReduxDataHelper(setState)
    .hostsScanConfig(hostsScan.config)
    .fetchScheduleStatus(hostsScan.fetchScheduleStatus)
    .build();
  this.render(hbs`{{schedule-form}}`);
  this.$('.x-toggle-container .x-toggle-btn').click();
  return wait().then(() => {
    const state = this.get('redux').getState();
    assert.equal(state.hostsScan.config.scheduleConfig.enabled, false, 'scan enable state changed to disable by toggling the button');
  });
});
