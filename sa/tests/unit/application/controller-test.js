import $ from 'jquery';
import { next } from '@ember/runloop';
import { moduleFor, test } from 'ember-qunit';
import * as ACTION_TYPES from 'sa/actions/types';

moduleFor('controller:application', 'Unit | Controller | application', {
  needs: [
    'service:accessControl', 'service:assetLoader', 'service:fatalErrors',
    'service:headData', 'service:session', 'service:redux'
  ]
});

test('generates prod theme url when link with fingerprint found', function(assert) {
  const fingerprint = 'assets/sa-cfd46dd672a31535e0e47662f6dcb59f.css';
  $('head').append(`<link rel="stylesheet" type="text/css" href="${fingerprint}">`);
  const controller = this.subject();
  const result = controller._generateFileName('dark');
  assert.equal(result, '/assets/dark-cfd46dd672a31535e0e47662f6dcb59f.css');

  const done = assert.async();
  $(`link[rel=stylesheet][href~="${fingerprint}"]`).remove();
  next(() => {
    done();
  });
});

test('generates non prod theme url when link with fingerprint not found', function(assert) {
  const controller = this.subject();
  const result = controller._generateFileName('light');
  assert.equal(result, '/assets/light.css');
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
