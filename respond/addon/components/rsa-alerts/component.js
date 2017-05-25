import Component from 'ember-component';
import connect from 'ember-redux/components/connect';
import columns from './columns';

const dispatchToActions = (/* dispatch */) => {
  return {
    bootstrap() {

    }
  };
};

const Alerts = Component.extend({
  columns
});

export default connect(undefined, dispatchToActions)(Alerts);