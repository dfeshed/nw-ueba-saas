import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../../../helpers/engine-resolver';
import wait from 'ember-test-helpers/wait';
import $ from 'jquery';

moduleForComponent('host-detail/base-property-panel/host-test', 'Integration | Component | host details base-property-panel host text', {
  integration: true,
  resolver: engineResolverFor('investigate-hosts'),
  beforeEach() {
    this.registry.injection('component', 'i18n', 'service:i18n');
  }
});

test('it renders the host-text', function(assert) {
  this.render(hbs`{{host-detail/base-property-panel/host-text}}`);
  assert.equal(this.$('.host-text').length, 1, 'Expected to render the host text content');
});

test('it renders the host-text SIZE content', function(assert) {
  this.set('format', 'SIZE');
  this.set('value', '1024');
  this.render(hbs`{{host-detail/base-property-panel/host-text format=format value=value}}`);
  assert.equal(this.$('.host-text .units').text().trim(), 'KB');
});

test('it renders the host-text HEX content', function(assert) {
  this.set('format', 'HEX');
  this.set('value', '16');
  this.render(hbs`{{host-detail/base-property-panel/host-text format=format value=value}}`);
  assert.equal(this.$('.host-text').text().trim(), '0x10');
});

test('it renders the host-text SIGNATURE content', function(assert) {
  this.set('format', 'SIGNATURE');
  this.set('value', null);
  this.render(hbs`{{host-detail/base-property-panel/host-text format=format value=value}}`);
  assert.equal(this.$('.host-text').text().trim(), 'unsigned');
});

test('it renders the tooltip on mouse enter', function(assert) {
  this.set('value', 'test value 123123 123123 123123 123123 123123');
  this.set('tipPosition', 'top');
  this.render(hbs`{{host-detail/base-property-panel/host-text format=format value=value tipPosition=tipPosition}}`);
  this.$('.host-text').width(100);
  this.$('.host-text').mouseenter();
  return wait().then(() => {
    assert.equal($('.ember-tether').length, 1, 'Tool tip is rendered');
    assert.equal($('.ember-tether .tool-tip-value').text().trim(), 'test value 123123 123123 123123 123123 123123');
    this.$('.host-text').mouseleave();
    return wait().then(() => {
      assert.equal($('.ember-tether .tool-tip-value').length, 0, 'Tool tip is hidden');
    });
  });

});
