import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../../helpers/engine-resolver';

moduleForComponent('host-scan/scan-command', 'Integration | Component | Host Scan Command', {
  integration: true,
  resolver: engineResolverFor('investigate-hosts'),
  beforeEach() {
    this.registry.injection('component', 'i18n', 'service:i18n');
  },
  afterEach() {
  }
});

test('it renders the scan start button', function(assert) {
  this.set('command', 'START_SCAN');
  this.render(hbs`{{host-scan/scan-command command=command}}`);
  assert.equal(this.$('.host-start-scan-button').length, 1, 'scan start button rendered');
});
