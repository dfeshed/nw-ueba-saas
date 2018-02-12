import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../../../../helpers/engine-resolver';

moduleForComponent('host-detail/base-property-panel/host-text/content', 'Integration | Component | host details base-property-panel host text content', {
  integration: true,
  resolver: engineResolverFor('investigate-hosts'),
  beforeEach() {
    this.registry.injection('component', 'i18n', 'service:i18n');
  }
});

test('it renders the content', function(assert) {
  this.render(hbs`{{host-detail/base-property-panel/host-text/content}}`);
  assert.equal(this.$('.tool-tip-value').length, 1, 'Expected to render the host text content');
});

test('it renders the content', function(assert) {
  this.set('value', 'XYZ');
  this.render(hbs`{{host-detail/base-property-panel/host-text/content text=value}}`);
  assert.equal(this.$('.tool-tip-value').text().trim(), 'XYZ');
});


test('it renders the SIZE content', function(assert) {
  this.set('format', 'SIZE');
  this.set('value', '1024');
  this.render(hbs`{{host-detail/base-property-panel/host-text/content format=format text=value}}`);
  assert.equal(this.$('.tool-tip-value .units').text().trim(), 'KB');
});

test('it renders the host-text SIGNATURE content', function(assert) {
  this.set('format', 'SIGNATURE');
  this.set('value', null);
  this.render(hbs`{{host-detail/base-property-panel/host-text/content format=format text=value}}`);
  assert.equal(this.$('.tool-tip-value').text().trim(), 'unsigned');
});

