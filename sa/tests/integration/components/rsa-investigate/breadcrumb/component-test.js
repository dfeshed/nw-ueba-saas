import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

moduleForComponent('rsa-investigate/breadcrumb', 'Integration | Component | rsa investigate/breadcrumb', {
  integration: true
});

test('it renders', function(assert) {
  const id = 'id1';
  const name = 'Service Name';
  const services = [{
    id,
    name
  }];
  const nowSeconds = parseInt(+(new Date()) / 1000, 10);
  const query = {
    serviceId: id,
    startTime: nowSeconds,
    endTime: nowSeconds
  };

  this.setProperties({
    services,
    query
  });

  this.render(hbs`{{rsa-investigate/breadcrumb services=services query=query}}`);

  let $el = this.$('.rsa-investigate-breadcrumb');
  assert.equal($el.length, 1, 'Expected root DOM element.');
  assert.equal($el.find('.service').text().trim(), name, 'Expected service name in DOM to match service data.');
});
