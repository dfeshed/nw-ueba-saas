import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

moduleForComponent('recon-event-titlebar', 'Integration | Component | recon event titlebar', {
  integration: true
});

test('no title shows default', function(assert) {
  this.set('title', null);

  this.render(hbs`{{recon-event-titlebar title=title}}`);

  assert.equal(this.$('.heading h2').text().trim(), 'Event Reconstruction');
});

test('title renders', function(assert) {
  this.set('title', 'Event Reconstruction (3 of 2000)');

  this.render(hbs`{{recon-event-titlebar title=title}}`);

  assert.equal(this.$('.heading h2').text().trim(), 'Event Reconstruction (3 of 2000)');
});
