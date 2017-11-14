import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../../helpers/engine-resolver';
import wait from 'ember-test-helpers/wait';
import { SINCE_WHEN_TYPES } from 'respond/utils/since-when-types';
import $ from 'jquery';

moduleForComponent('rsa-alerts-search-results', 'Integration | Component | Alerts Search Results', {
  integration: true,
  resolver: engineResolverFor('respond')
});

const entity = { id: '10.20.30.40', type: 'IP' };
const timeFrameName = SINCE_WHEN_TYPES[0].name;
const itemsStatus = 'complete';
const items = [
  { id: 1, alert: { name: 'Alert 1' } },
  { id: 2, alert: { name: 'Alert 2' } }
];


test('it renders the header DOM and results DOM as expected', function(assert) {
  this.setProperties({
    entity,
    timeFrameName,
    itemsStatus,
    items
  });

  this.render(hbs`{{#rsa-alerts-search/results 
    entity=entity
    timeFrameName=timeFrameName
    itemsStatus=itemsStatus
    items=items
    as |item index column|
    }}
      <span class="alert">{{item.alert.name}}</span>
    {{/rsa-alerts-search/results}}`);

  return wait()
    .then(() => {
      assert.ok(this.$('.rsa-alerts-search-results').length, 'Expected to find root DOM node');

      const $header = this.$('.rsa-alerts-search-results__header');
      assert.ok($header.length, 'Expected to find header DOM');

      assert.ok(this.$('.rsa-alerts-search-results__time-frame').length, 'Expected to find time frame in header DOM');
      assert.notOk(this.$('.rsa-loader').length, 'Expected to not find wait DOM when status is complete');

      assert.ok(this.$('.rsa-data-table').length, 'Expected to find data table DOM');
      const $alerts = this.$('.rsa-data-table .alert');
      assert.ok($alerts.length, 'Expected to find DOM for an alert in results, but possibly not all alerts, because of lazy rendering');
      $alerts.each(function(index) {
        const $el = $(this);
        assert.equal($el.text().trim(), items[index].alert.name, 'Expected to find custom alert content in DOM');
      });
    });
});

test('it renders a loading icon when the results status is streaming', function(assert) {
  this.setProperties({
    entity,
    timeFrameName,
    items
  });

  this.render(hbs`{{rsa-alerts-search/results 
    entity=entity
    timeFrameName=timeFrameName
    itemsStatus="streaming"
    items=items
    }}`);

  return wait()
    .then(() => {
      assert.ok(this.$('.rsa-loader').length, 'Expected to find wait DOM');
    });
});

