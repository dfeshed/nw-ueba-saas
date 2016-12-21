import thunk from 'npm:redux-thunk';
import reduxPackMiddleware from 'npm:redux-pack';

const middleware = [thunk.default, reduxPackMiddleware.middleware];

export default middleware;