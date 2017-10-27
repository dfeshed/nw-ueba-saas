import $ from 'jquery';
import { moduleFor, test } from 'ember-qunit';
import * as ACTION_TYPES from 'sa/actions/types';

moduleFor('controller:application', 'Unit | Controller | application', {
  needs: [
    'service:accessControl', 'service:assetLoader', 'service:fatalErrors',
    'service:headData', 'service:session', 'service:redux'
  ]
});

test('will alter the body class when theme is truly different', function(assert) {
  $('body').addClass('our-application');

  let updatesRun = 0;
  const redux = this.container.lookup('service:redux');
  const controller = this.subject();
  const original = controller._updateBodyClass;
  controller._updateBodyClass = function() {
    updatesRun++;
    return original.apply(this, arguments);
  };

  redux.dispatch({ type: ACTION_TYPES.UPDATE_PREFERENCES_THEME, theme: 'LIGHT' });
  assert.ok($('body').hasClass('light-theme'));
  assert.ok($('body').hasClass('our-application'));
  assert.notOk($('body').hasClass('dark-theme'));
  assert.equal(updatesRun, 1);

  redux.dispatch({ type: ACTION_TYPES.UPDATE_PREFERENCES_THEME, theme: 'DARK' });
  assert.ok($('body').hasClass('dark-theme'));
  assert.ok($('body').hasClass('our-application'));
  assert.notOk($('body').hasClass('light-theme'));
  assert.equal(updatesRun, 2);

  redux.dispatch({ type: ACTION_TYPES.UPDATE_PREFERENCES_THEME, theme: 'DARK' });
  assert.ok($('body').hasClass('dark-theme'));
  assert.ok($('body').hasClass('our-application'));
  assert.notOk($('body').hasClass('light-theme'));
  assert.equal(updatesRun, 2);

  controller._updateBodyClass = original;
});
