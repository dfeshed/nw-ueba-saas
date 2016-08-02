import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

moduleForComponent('rsa-incident-detail-storyline', 'Integration | Component | rsa incident detail storyline', {
  integration: true
});


test('it renders', function(assert) {
  assert.expect(0);
  this.on('journalAction', function() {
    return true;
  });
  this.on('showEntity', function() {
    return true;
  });
  this.render(hbs`{{rsa-respond/incident-detail/detail-storyline journalAction=(action 'journalAction') entityAction=(action 'showEntity') }}`);
});
