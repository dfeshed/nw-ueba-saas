import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, settled, click } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { applyPatch, revertPatch } from '../../../../../../helpers/patch-reducer';
import Immutable from 'seamless-immutable';
import explore from '../../../../state/explore.fileSearchResults';

module('Integration | Component | endpoint host titlebar explore', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-hosts')
  });

  hooks.beforeEach(function() {
    this.owner.inject('component', 'i18n', 'service:i18n');
    this.timezone = this.owner.lookup('service:timezone');
    this.eventBus = this.owner.lookup('service:eventBus');
    const setState = Immutable.from({
      endpoint: {
        ...explore
      }
    });
    applyPatch(setState);
    this.redux = this.owner.lookup('service:redux');
  });

  hooks.afterEach(function() {
    revertPatch();
  });

  test('Should render the explore', async function(assert) {
    this.get('timezone').set('_selected', { zoneId: 'Kwajalein' });
    await render(hbs`{{host-detail/header/titlebar/explore}}`);
    const explore = document.querySelectorAll('.host-explore');
    assert.equal(explore.length, 1, 'Rendered the explore text box');
  });

  test('Application click should toggle the visibility of Explore content', async function(assert) {
    assert.expect(2);
    this.get('timezone').set('_selected', { zoneId: 'Kwajalein' });
    await render(hbs`{{host-detail/header/titlebar/explore}}`);
    this.get('eventBus').trigger('rsa-application-click');
    return settled().then(async() => {
      assert.equal(document.querySelectorAll('.host-explore__content__header').length, 0, 'host-explore content hidden on application click');
      await click('.label-content');
      return settled().then(() => {
        assert.equal(document.querySelector('.host-explore__content__header').getClientRects().length > 0, true, 'host-explore content shown on Searchbox label click');
      });
    });
  });
});
