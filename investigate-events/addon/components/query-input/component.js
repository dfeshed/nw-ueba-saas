import Component from 'ember-component';
import { connect } from 'ember-redux';
import { notEmpty } from 'ember-computed-decorators';
import { setQueryString } from 'investigate-events/actions/interaction-creators';

const dispatchToActions = { setQueryString };

const QueryInputComponent = Component.extend({
  classNames: ['rsa-investigate-query-input'],
  classNameBindings: ['hasValue'],
  placeholder: 'Enter a Meta Key and Value (optional)',
  queryString: '',

  @notEmpty('queryString')
  hasValue: true,

  actions: {
    clear() {
      this.set('queryString', '');
      this.send('setQueryString', '');
    },
    valueChanged(e) {
      this.send('setQueryString', e.target.value);
    }
  }

});

export default connect(null, dispatchToActions)(QueryInputComponent);