// NOTE
// Leaving this out of index.js to not engage babel
// as this is used via `ember serve`/`ember test` not via `node`

var determineSocketUrl = function(environment, productionPath) {
  // Set NOMOCK=anything (ex 'NOMOCK=1 ember s')
  // to not use mock in dev/test
  //
  // When running jenkins tests, the MOCK_PORT
  // is set to any of a number of possible ports
  // so need to get from 'process.env'

  let socketUrl;
  if ((environment === 'development' || environment === 'test') && !process.env.NOMOCK)  {
    let mockPort = process.env.MOCK_PORT || 9999;
    socketUrl = 'http://localhost:' + mockPort + '/socket/';
  } else {
    socketUrl = productionPath;
  }
  return socketUrl;
}

module.exports = {
  determineSocketUrl
}