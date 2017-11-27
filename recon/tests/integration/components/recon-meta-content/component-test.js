import wait from 'ember-test-helpers/wait';
import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

import { applyPatch, revertPatch } from '../../../helpers/patch-reducer';
import ReduxDataHelper from '../../../helpers/redux-data-helper';

let setState;
moduleForComponent('recon-meta-content', 'Integration | Component | recon meta content', {
  integration: true,
  beforeEach() {
    setState = (state) => {
      applyPatch(state);
      this.inject.service('redux');
    };
  },
  afterEach() {
    revertPatch();
  }
});

test('several meta items render correctly', function(assert) {
  new ReduxDataHelper(setState).meta([
    [ 'size', 62750 ],
    [ 'payload', 56460 ],
    [ 'medium', 1 ],
    [ 'eth.src', '70:56:81:9A:94:DD' ],
    [ 'eth.dst', '10:0D:7F:75:C4:C8' ]
  ]).build();

  this.render(hbs`{{recon-meta-content}}`);
  return wait().then(() => {
    assert.equal(this.$('.recon-meta-content-item').length, 5);
  });
});

test('zero meta items render correctly', function(assert) {
  new ReduxDataHelper(setState).meta([]).build();
  this.render(hbs`{{recon-meta-content}}`);
  return wait().then(() => {
    assert.equal(this.$('.recon-meta-content-item').length, 0);
  });
});
