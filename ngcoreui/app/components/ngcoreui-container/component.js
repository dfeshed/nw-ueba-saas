import Component from '@ember/component';
import { inject as service } from '@ember/service';
import { connect } from 'ngcoreui/actions/actions';

export default Component.extend({
  tagName: 'screen',
  redux: service(),

  init() {
    this._super(...arguments);
    this.get('redux').dispatch(connect());
  }
});
