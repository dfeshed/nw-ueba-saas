import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../../../../helpers/engine-resolver';
import { applyPatch, revertPatch } from '../../../../../../helpers/patch-reducer';
import Immutable from 'seamless-immutable';
import explore from '../../../../state/explore.fileSearchResults';
import wait from 'ember-test-helpers/wait';
import $ from 'jquery';


moduleForComponent('host-detail/header/titlebar/explore', 'Integration | Component | endpoint host titlebar explore', {
  integration: true,
  resolver: engineResolverFor('investigate-hosts'),
  beforeEach() {
    this.registry.injection('component', 'i18n', 'service:i18n');
    this.inject.service('timezone');
    this.inject.service('eventBus');
    const setState = Immutable.from({
      endpoint: {
        ...explore
      }
    });
    applyPatch(setState);
    this.inject.service('redux');
  },
  afterEach() {
    revertPatch();
  }
});

test('Should render the explore', function(assert) {
  this.get('timezone').set('_selected', { zoneId: 'Kwajalein' });
  this.render(hbs`{{host-detail/header/titlebar/explore}}`);
  const explore = $('.host-explore');
  assert.equal(explore.length, 1, 'Rendered the explore text box');
});

test('Application click should toggle the visibility of Explore content', function(assert) {
  assert.expect(2);
  this.get('timezone').set('_selected', { zoneId: 'Kwajalein' });
  this.render(hbs`{{host-detail/header/titlebar/explore}}`);
  this.get('eventBus').trigger('rsa-application-click');
  return wait().then(() => {
    assert.equal($('.host-explore__content__header').is(':visible'), false, 'host-explore content hidden on application click');
    $('.label-content').click();
    return wait().then(() => {
      assert.equal($('.host-explore__content__header').is(':visible'), true, 'host-explore content shown on Searchbox label click');
    });
  });
});
