import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import { applyPatch, revertPatch } from '../../../helpers/patch-reducer';
import Immutable from 'seamless-immutable';

moduleForComponent('schedule-time', 'Integration | Component | schedule time', {
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

test('should render the schedule time field', function(assert) {
  this.render(hbs`{{schedule-time}}`);
  assert.equal(this.$('.schedule-time').length, 1, 'template rendered');
});
