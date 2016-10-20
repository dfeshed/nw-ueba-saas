import reduxActions from 'npm:redux-actions';

const testInitialState = {
  testing: true
};

const visuals = reduxActions.handleActions({
  foo: (state) => ({
    ...state,
    testing: false
  })
}, testInitialState);

export default visuals;
