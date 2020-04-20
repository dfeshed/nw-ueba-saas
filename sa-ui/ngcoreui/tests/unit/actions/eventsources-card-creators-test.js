import { module, test } from 'qunit';
import { setupTest } from 'ember-qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import * as esCardCreators from 'ngcoreui/actions/creators/logcollector/eventsources-card-creators';
import ACTION_TYPES from 'ngcoreui/actions/types';

module('Unit | Actions | eventsources-card creators', function(hooks) {
  setupTest(hooks);
  hooks.beforeEach(function() {
    initialize(this.owner);
  });

  test('fetchEventSourcesStatsData method returns proper type(s), payload(s), and/or promise(s)', function(assert) {
    const result = esCardCreators.fetchEventSourcesStatsData();
    assert.equal(result.type, ACTION_TYPES.LOG_COLLECTOR_EVENT_SOURCES_STATS_DATA, 'action has the correct type');
    assert.ok(result.promise, 'action has a fetchEventSourcesStatsData promise');
  });
});
