import engineResolverFor from '../../../helpers/engine-resolver';

const pressEnter = (input) => {
  input.trigger({
    type: 'keydown',
    which: 13,
    code: 'Enter'
  });
};

const testSetupConfig = {
  integration: true,
  resolver: engineResolverFor('investigate-events'),
  beforeEach() {
    this.inject.service('redux');
  }
};

export {
  testSetupConfig,
  pressEnter
};
