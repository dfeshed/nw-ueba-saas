import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll, click } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import ReduxDataHelper from '../../../../../helpers/redux-data-helper';
import { patchReducer } from '../../../../../helpers/vnext-patch';
import Immutable from 'seamless-immutable';

let setState;

module('Integration | Component | process-details/events-table/table', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-process-analysis')
  });

  hooks.beforeEach(function() {
    setState = (state) => {
      patchReducer(this, Immutable.from(state));
    };
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  test('process-events-table renders', async function(assert) {
    const eventsData = [
      {
        sessionId: 45328,
        time: 1525950159000,
        id: 'event_3'
      },
      {
        sessionId: 45337,
        time: 1525950159000,
        id: 'event_4'
      }];

    new ReduxDataHelper(setState)
      .eventsData(eventsData)
      .eventsFilteredCount(2)
      .build();
    const timezone = this.owner.lookup('service:timezone');
    const timeFormat = this.owner.lookup('service:timeFormat');
    const dateFormat = this.owner.lookup('service:dateFormat');

    timezone.set('_selected', { zoneId: 'UTC' });
    timeFormat.set('_selected', { format: 'hh:mm:ss' });
    dateFormat.set('_selected', { format: 'YYYY-MM-DD' });

    assert.expect(3);

    await render(hbs`{{process-details/events-table/table}}`);
    assert.equal(findAll('.rsa-data-table-body-row').length, 2, 'Expected to render 2 rows');
    assert.equal(findAll('.rsa-icon-arrow-down-7-filled').length, 1, 'Expected down arrow icon');
    await click('.sort');
    assert.equal(findAll('.rsa-icon-arrow-up-7-filled').length, 1, 'Expected up arrow icon');
  });

  test('it renders the header', async function(assert) {

    const eventsData = [
      {
        sessionId: 45328,
        time: 1525950159000,
        id: 'event_3'
      },
      {
        sessionId: 45337,
        time: 1525950159000,
        id: 'event_4'
      }];

    new ReduxDataHelper(setState)
      .eventsData(eventsData)
      .eventsFilteredCount(2)
      .build();
    const timezone = this.owner.lookup('service:timezone');
    const timeFormat = this.owner.lookup('service:timeFormat');
    const dateFormat = this.owner.lookup('service:dateFormat');

    timezone.set('_selected', { zoneId: 'UTC' });
    timeFormat.set('_selected', { format: 'hh:mm:ss' });
    dateFormat.set('_selected', { format: 'YYYY-MM-DD' });

    await render(hbs`{{process-details/events-table/table}}`);
    assert.equal(findAll('.rsa-data-table-header .js-move-handle').length, 12, 'Move handler exist for all the columns');
  });

  test('clicking the header button will call the external action', async function(assert) {
    assert.expect(2);
    this.set('toggleFilterPanel', function() {
      assert.ok(true);
    });
    const eventsData = [
      {
        sessionId: 45328,
        time: 1525950159000,
        id: 'event_3'
      },
      {
        sessionId: 45337,
        time: 1525950159000,
        id: 'event_4'
      }];

    new ReduxDataHelper(setState)
      .eventsData(eventsData)
      .eventsFilteredCount(2)
      .build();
    const timezone = this.owner.lookup('service:timezone');
    const timeFormat = this.owner.lookup('service:timeFormat');
    const dateFormat = this.owner.lookup('service:dateFormat');

    timezone.set('_selected', { zoneId: 'UTC' });
    timeFormat.set('_selected', { format: 'hh:mm:ss' });
    dateFormat.set('_selected', { format: 'YYYY-MM-DD' });

    await render(hbs`{{process-details/events-table/table toggleFilterPanel=(action toggleFilterPanel)}}`);
    assert.equal(findAll('.title-header').length, 1, 'Header section exists');
    await click('.filter-button button');
  });

});
