import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

import { TYPES_BY_NAME } from 'recon/utils/reconstruction-types';

moduleForComponent('recon-event-titlebar', 'Integration | Component | recon event titlebar', {
  integration: true
});

test('no index or total shows just label for recon type', function(assert) {
  this.set('reconstructionType', TYPES_BY_NAME.PACKET);

  this.render(hbs`{{recon-event-titlebar reconstructionType=reconstructionType}}`);

  assert.equal(this.$('.prompt').text().trim(), TYPES_BY_NAME.PACKET.label);
});

test('title renders', function(assert) {
  this.set('reconstructionType', TYPES_BY_NAME.PACKET);
  this.set('total', 555);
  this.set('index', 25);

  this.render(
    hbs`{{recon-event-titlebar reconstructionType=reconstructionType total=total index=index}}`
  );

  assert.equal(this.$('.prompt').text().trim(), `${TYPES_BY_NAME.PACKET.label} (26 of 555)`);
});
