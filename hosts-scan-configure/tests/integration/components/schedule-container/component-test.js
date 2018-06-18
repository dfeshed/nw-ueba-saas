import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

moduleForComponent('hosts-scan-configure-container', 'Integration | Component | schedule container', {
  integration: true,
  beforeEach() {
    this.registry.injection('component', 'i18n', 'service:i18n');
    initialize(this);
  }
});

test('should render the schedule scan form', function(assert) {

  this.render(hbs`{{hosts-scan-configure-container}}`);
  assert.equal(this.$('.hosts-scan-configure-container').length, 1, 'expecting container to be rendered');
  assert.equal(this.$('.schedule-form').length, 1, 'expecting schedule form root element in DOM');

});
