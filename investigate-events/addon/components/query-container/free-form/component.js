import Component from '@ember/component';
import { dirtyQueryToggle } from 'investigate-events/actions/query-validation-creators';
import { connect } from 'ember-redux';

const dispatchToActions = {
  dirtyQueryToggle
};

const freeForm = Component.extend({
  classNames: 'rsa-investigate-free-form-query-bar',

  actions: {
    keyDown(e) {
      this.send('dirtyQueryToggle');
      if (e.keyCode === 13) {
        this.executeQuery(this.get('freeFormText').trim());
      }
    }
  }

});

export default connect(undefined, dispatchToActions)(freeForm);