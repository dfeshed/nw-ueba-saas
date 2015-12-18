import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

moduleForComponent('rsa-popover', 'Integration | Component | rsa popover', {
  integration: true
});

test('it renders without a target', function(assert) {
  assert.expect(1);

  this.render(hbs`{{rsa-popover class='rsa-popover-untargeted'}}`);

  // @workaround DOM from the popover content will likely get moved outside of this component's DOM at run time
  // (thanks to the drop JS library that we use), so look for it in the scope of the entire HTML document.
  assert.ok(document.getElementsByClassName('rsa-popover-untargeted').length, 'Could not find the untargeted popover DOM.');
});

test('it renders with a target', function(assert) {
  assert.expect(2);

  this.render(hbs`<button class='my-target'>Click Me</button>{{rsa-popover target='.my-target' class='rsa-popover-targeted'}}`);

  let target = this.$('.my-target');
  assert.ok(target.length, 'Unable to find target DOM.');
  target.trigger('click');

  // @workaround DOM from the popover content will likely get moved outside of this component's DOM at run time
  // (thanks to the drop JS library that we use), so look for it in the scope of the entire HTML document.
  assert.ok(document.getElementsByClassName('rsa-popover-targeted').length, 'Could not find the targeted popover DOM.');
});
