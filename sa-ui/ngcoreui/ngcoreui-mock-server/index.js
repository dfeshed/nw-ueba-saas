/* eslint-disable */
require('./node_modules/babel-register/lib/node.js')({
  presets: ['babel-preset-stage-0', 'babel-preset-es2015']
});

require('./server.js');
