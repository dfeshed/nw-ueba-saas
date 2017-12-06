import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import Immutable from 'seamless-immutable';
import explore from '../../../../state/explore.fileSearchResults';

import engineResolverFor from '../../../../../../helpers/engine-resolver';
import { applyPatch, revertPatch } from '../../../../../../helpers/patch-reducer';

moduleForComponent('host-detail/header/titlebar/explore/content', 'Integration | Component | endpoint host titlebar explore content', {
  integration: true,
  resolver: engineResolverFor('investigate-hosts'),
  beforeEach() {
    this.registry.injection('component', 'i18n', 'service:i18n');
    this.inject.service('timezone');
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

test('Date time formate based on timezone', function(assert) {
  this.get('timezone').set('_selected', { zoneId: 'Kwajalein' });
  this.render(hbs`{{host-detail/header/titlebar/explore/content }}`);

  assert.equal(this.$('.host-explore__content__snapshot:first h3').text().trim(), '2017-11-17 03:31:39.000 pm (81)');
});
