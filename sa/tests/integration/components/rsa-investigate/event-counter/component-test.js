import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

moduleForComponent('rsa-investigate/event-counter', 'Integration | Component | rsa investigate/event counter', {
  integration: true
});

test('it renders a given count', function(assert) {
  this.render(hbs`{{rsa-investigate/event-counter count=55}}`);
  assert.equal(this.$('.rsa-investigate-event-counter').length, 1, 'Expected root DOM element.');
  assert.equal(this.$('.rsa-investigate-event-counter__count').text().trim(), '55', 'Expected count value to be displayed in DOM.');
});

test('it renders the appropriate DOM for a given threshold', function(assert) {
  const count = 100;
  this.setProperties({
    count,
    threshold: undefined
  });
  this.render(hbs`{{rsa-investigate/event-counter count=count threshold=threshold}}`);
  assert.equal(this.$('.rsa-investigate-event-counter__plus').length, 0, 'Expected to not find plus DOM element.');

  this.set('threshold', count - 1);
  assert.equal(this.$('.rsa-investigate-event-counter__plus').length, 0, 'Expected to not find plus DOM element.');

  this.set('threshold', count);
  assert.equal(this.$('.rsa-investigate-event-counter__plus').length, 1, 'Expected to find plus DOM element.');
});

test('it fires callbacks as expected', function(assert) {
  assert.expect(6);

  let goAction = (() => {
    assert.ok(true, 'goAction was invoked.');
  });
  let stopAction = (() => {
    assert.ok(true, 'stopAction was invoked.');
  });
  let retryAction = (() => {
    assert.ok(true, 'retryAction was invoked.');
  });
  let $el;

  this.setProperties({
    value: 0,
    goAction,
    stopAction,
    retryAction
  });

  this.render(hbs`{{rsa-investigate/event-counter count=count status=status goAction=goAction stopAction=stopAction retryAction=retryAction}}`);

  // Set status to 'streaming' and test the stopAction callback.
  this.set('status', 'streaming');
  $el = this.$('.rsa-investigate-event-counter__stop');
  assert.equal($el.length, 1, 'Expected stop DOM element.');
  $el.trigger('click');

  // Set status to 'idle' and test the goAction callback.
  this.set('status', 'idle');
  $el = this.$('.rsa-investigate-event-counter__go');
  assert.equal($el.length, 1, 'Expected go DOM element.');
  $el.trigger('click');

  // Set status to 'error' and test the retryAction callback.
  this.set('status', 'error');
  $el = this.$('.rsa-investigate-event-counter__retry');
  assert.equal($el.length, 1, 'Expected retry DOM element.');
  $el.trigger('click');

});

